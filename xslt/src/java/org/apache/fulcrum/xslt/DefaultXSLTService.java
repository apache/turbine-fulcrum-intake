package org.apache.fulcrum.xslt;


/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import java.io.File;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Hashtable;


import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;

/**
 * Implementation of the Turbine XSLT Service. It transforms xml with a given
 * xsl file.  XSL stylesheets are compiled and cached (if the service property
 * is set) to improve speeds.
 *
 * @author <a href="mailto:leon@opticode.co.za">Leon Messerschmidt</a>
 * @author <a href="mailto:rubys@us.ibm.com">Sam Ruby</a>
 * @author <a href="mailto:epugh@opensourceconnections.com">Eric Pugh</a>
 */
public class DefaultXSLTService
    extends AbstractLogEnabled
    implements XSLTService,Initializable,Configurable,Contextualizable,Serviceable

{
    /**
     * The application root
     */
    private String applicationRoot;
    
    /**
     * Property to control the caching of Templates.
     */
    protected boolean caching = false;

    /**
     * Path to style sheets used for tranforming well-formed
     * XML documents. The path is relative to the webapp context.
     */
    protected  String path;

    /**
     * What the configured value was
     */
    private  String styleSheetPath;    
    
    /**
     * Cache of compiled Templates.
     */
    protected Hashtable cache = new Hashtable();

    protected final static String STYLESHEET_PATH = "path";

    protected final static String STYLESHEET_CACHING = "cache";

    /**
     * Factory for producing templates and null transformers
     */
    private static TransformerFactory tfactory;



    /**
     * Get a valid and existing filename from a template name.
     * The extension is removed and replaced with .xsl.  If this
     * file does not exist the method attempts to find default.xsl.
     * If it fails to find default.xsl it returns null.
     */
    protected String getFileName (String templateName)
    {
        // First we chop of the existing extension
        int colon = templateName.lastIndexOf (".");
        if (colon > 0)
        {
            templateName = templateName.substring (0,colon);
        }

        // Now we try to find the file ...
        File f = new File (path+templateName+".xsl");
        if (f.exists())
        {
            return path+templateName+".xsl";
        }
        else
        {
            // ... or the default file
            f = new File (path+"default.xsl");
            if (f.exists())
            {
                return path+"default.xsl";
            }
            else
            {
                return null;
            }
        }
    }

    /**
     * Compile Templates from an input file.
     */
    protected Templates compileTemplates (String source) throws Exception
    {
        StreamSource xslin = new StreamSource(new File(source));
        Templates root = tfactory.newTemplates(xslin);
        return root;
    }

    /**
     * Retrieves Templates.  If caching is switched on the
     * first attempt is to load Templates from the cache.
     * If caching is switched of or if the Stylesheet is not found
     * in the cache new Templates are compiled from an input
     * file.
     * <p>
     * This method is synchronized on the xsl cache so that a thread
     * does not attempt to load Templates from the cache while
     * it is still being compiled.
     */
    protected Templates getTemplates(String xslName) throws Exception
    {
        synchronized (cache)
        {
            String fn = getFileName (xslName);
            if (fn == null) return null;

            if (caching && cache.containsKey (fn))
            {
                return (Templates)cache.get(fn);
            }

            Templates sr = compileTemplates (fn);

            if (caching)
            {
                cache.put (fn,sr);
            }

            return sr;
        }

    }

    protected void transform (String xslName, Source xmlin, Result xmlout)
        throws Exception
    {
        Transformer transformer = getTransformer( xslName );

        transformer.transform(xmlin, xmlout);
    }

    /**
     * Execute an xslt
     */
    public void transform (String xslName, Reader in, Writer out)
        throws Exception
    {
        Source xmlin = new StreamSource(in);
        Result xmlout = new StreamResult(out);
        transform (xslName,xmlin,xmlout);
    }

    /**
     * Execute an xslt
     */
    public String transform (String xslName, Reader in)
        throws Exception
    {
        StringWriter sw = new StringWriter();
        transform (xslName,in,sw);
        return sw.toString();
    }


    /**
     * Execute an xslt
     */
    public void transform (String xslName, org.w3c.dom.Node in, Writer out)
        throws Exception
    {
        Source xmlin = new DOMSource(in);
        Result xmlout = new StreamResult(out);
        transform (xslName,xmlin,xmlout);
    }

    /**
     * Execute an xslt
     */
    public String transform (String xslName, org.w3c.dom.Node in)
        throws Exception
    {
        StringWriter sw = new StringWriter();
        transform (xslName,in,sw);
        return sw.toString();
    }

    /**
     * Retrieve a transformer for the given stylesheet name. If no stylesheet
     * is available for the provided name, an identity transformer will be
     * returned. This allows clients of this service to perform more complex
     * transformations (for example, where parameters must be set). When
     * possible prefer using one of the forms of {@link #transform}.
     *
     * @param xslName Identifies stylesheet to get transformer for
     * @return A transformer for that stylesheet
     */
    public Transformer getTransformer(String xslName) throws Exception
    {
        Templates sr = getTemplates(xslName);

        if (sr == null)
        {
            return tfactory.newTransformer();
        }
        else
        {
            return sr.newTransformer();
        }
    }
    
    /**
     * @see org.apache.fulcrum.ServiceBroker#getRealPath(String)
     */
    public String getRealPath(String path)
    {
        String absolutePath = null;
        if (applicationRoot == null) 
        {
            absolutePath = new File(path).getAbsolutePath();
        }
        else 
        {
            absolutePath = new File(applicationRoot, path).getAbsolutePath();
        }
        
        return absolutePath;
    }        
    // ---------------- Avalon Lifecycle Methods ---------------------
    /**
     * Avalon component lifecycle method
     */
    public void configure(Configuration conf)  throws ConfigurationException
    {
        styleSheetPath =conf.getAttribute(STYLESHEET_PATH);       
        caching = conf.getAttributeAsBoolean(STYLESHEET_CACHING);

    }
    
    /**
     * Initializes the service.
     *
     * This method processes the repository path, to make it relative to the
     * web application root, if neccessary
     */
    public void initialize() throws Exception
    {
        path = getRealPath(styleSheetPath);
        if (!path.endsWith("/") && !path.endsWith ("\\"))
        {
            path=path+File.separator;
        }
        
        tfactory = TransformerFactory.newInstance();
    }    
    
    public void contextualize(Context context) throws ContextException {
        this.applicationRoot = context.get( "urn:avalon:home" ).toString();
    }  
    
    /**
     * Avalon component lifecycle method
     */
    public void service( ServiceManager manager) {        
        
        XSLTServiceFacade.setService(this);
        
    }    
    /**
     * Avalon component lifecycle method
     */
    public void dispose()
    {
        
    }       

}
