package org.apache.fulcrum.xslt;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Turbine" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Turbine", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Hashtable;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.apache.fulcrum.BaseService;
import org.apache.fulcrum.InitializationException;

/**
 * Implementation of the Turbine XSLT Service. It transforms xml with a given
 * xsl file.  XSL stylesheets are compiled and cached (if the service property
 * is set) to improve speeds.
 *
 * @author <a href="mailto:leon@opticode.co.za">Leon Messerschmidt</a>
 * @author <a href="mailto:rubys@us.ibm.com">Sam Ruby</a>
 */
public class TurbineXSLTService
    extends BaseService
    implements XSLTService

{
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
     * Initialize the TurbineXSLT Service.  Load the path to search for
     * xsl files and initiates the cache.
     */
    public void init()
        throws InitializationException
    {
        //!! is this even needed? jvz.
        if (isInitialized())
        {
            return;
        }

        path = getRealPath(getConfiguration().getString(STYLESHEET_PATH));

        if (!path.endsWith("/") && !path.endsWith ("\\"))
        {
            path=path+File.separator;
        }

        caching = getConfiguration().getBoolean(STYLESHEET_CACHING);

        tfactory = TransformerFactory.newInstance();

        setInit(true);
    }

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

}
