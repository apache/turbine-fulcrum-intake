package org.apache.fulcrum.xslt;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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
import org.w3c.dom.Node;
import org.w3c.dom.Document;

/**
 * Implementation of the Turbine XSLT Service. It transforms xml with a given
 * xsl file. XSL stylesheets are compiled and cached (if the service property is
 * set) to improve speeds.
 *
 * @author <a href="mailto:leon@opticode.co.za">Leon Messerschmidt</a>
 * @author <a href="mailto:rubys@us.ibm.com">Sam Ruby</a>
 * @author <a href="mailto:epugh@opensourceconnections.com">Eric Pugh</a>
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 */
public class DefaultXSLTService extends AbstractLogEnabled implements
        XSLTService, Initializable, Configurable, Contextualizable, Serviceable
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
     * Path to style sheets used for tranforming well-formed XML documents. The
     * path is relative to the webapp context.
     */
    protected String path;

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
     * Try to create a valid url object from the style parameter.
     *
     * @param style
     *            the xsl-Style
     * @return a <code>URL</code> object or null if the style sheet could not
     *         be found
     */
    private URL getStyleURL(String style)
    {
        StringBuffer sb = new StringBuffer();

        sb.append(path);

        // we chop off the existing extension
        int colon = style.lastIndexOf(".");

        if (colon > 0)
        {
            sb.append(style.substring(0, colon));
        }
        else
        {
            sb.append(style);
        }

        sb.append(".xslt");

        URL url = null;

        try
        {
            url = new URL(sb.toString());
        }
        catch (MalformedURLException e)
        {
            getLogger().error("Malformed URL: " + sb, e);
        }

        return url;
    }

    /**
     * Compile Templates from an input file.
     *
     * @param source the source URL
     * @return the compiled template
     * @throws Exception the compilation failed
     */
    protected Templates compileTemplates(URL source) throws Exception
    {
        StreamSource xslin = new StreamSource(source.openStream());
        return tfactory.newTemplates(xslin);
    }

    /**
     * Retrieves Templates. If caching is switched on the first attempt is to
     * load Templates from the cache. If caching is switched of or if the
     * Stylesheet is not found in the cache new Templates are compiled from an
     * input file.
     * <p>
     * This method is synchronized on the xsl cache so that a thread does not
     * attempt to load Templates from the cache while it is still being
     * compiled.
     *
     * @param xslName the name of the XSL file
     * @return the correspondint template or null if the XSL was not found
     * @throws Exception getting the template failed
     */
    protected Templates getTemplates(String xslName) throws Exception
    {
        synchronized (cache)
        {
            URL fn = getStyleURL(xslName);
            if (fn == null)
                return null;

            if (caching && cache.containsKey(fn))
            {
                return (Templates) cache.get(fn);
            }

            Templates sr = compileTemplates(fn);

            if (caching)
            {
                cache.put(fn, sr);
            }

            return sr;
        }
    }

    protected void transform(String xslName, Source xmlin, Result xmlout, Map params)
            throws Exception
    {
        try
        {
            long startTime = System.currentTimeMillis();
            Transformer transformer = getTransformer(xslName);

            if (params != null)
            {
                for (Iterator it = params.entrySet().iterator(); it.hasNext(); )
                {
                    Map.Entry entry = (Map.Entry) it.next();
                    transformer.setParameter(String.valueOf(entry.getKey()), entry.getValue());
                }
            }

            transformer.transform(xmlin, xmlout);

            if(getLogger().isDebugEnabled())
            {
                long duration = System.currentTimeMillis() - startTime;
                getLogger().debug("The transforamtion '" + xslName + "' took " + duration + " ms");
            }
        }
        catch(Exception e)
        {
            getLogger().debug("The transformation '" + xslName + "' failed due to : " + e.getMessage());
            throw e;
        }
    }

    /**
     * Uses an xsl file to transform xml input from a reader and writes the
     * output to a writer.
     *
     * @param xslName
     *            The name of the file that contains the xsl stylesheet.
     * @param in
     *            The reader that passes the xml to be transformed
     * @param out
     *            The writer for the transformed output
     */
    public void transform(String xslName, Reader in, Writer out)
            throws Exception
    {
        Source xmlin = new StreamSource(in);
        Result xmlout = new StreamResult(out);
        transform(xslName, xmlin, xmlout, null);
    }

    /**
     * Uses an xsl file to transform xml input from a reader and returns a
     * string containing the transformed output.
     *
     * @param xslName
     *            The name of the file that contains the xsl stylesheet.
     * @param in
     *            The reader that passes the xml to be transformed
     */
    public String transform(String xslName, Reader in) throws Exception
    {
        StringWriter sw = new StringWriter();
        transform(xslName, in, sw, null);
        return sw.toString();
    }

    /**
     * Uses an xsl file to transform xml input from a DOM note and writes the
     * output to a writer.
     *
     * @param xslName
     *            The name of the file that contains the xsl stylesheet.
     * @param in
     *            The DOM Node to be transformed
     * @param out
     *            The writer for the transformed output
     */
    public void transform(String xslName, org.w3c.dom.Node in, Writer out)
            throws Exception
    {
        Source xmlin = new DOMSource(in);
        Result xmlout = new StreamResult(out);
        transform(xslName, xmlin, xmlout, null);
    }

    /**
     * Uses an xsl file to transform xml input from a DOM note and returns a
     * string containing the transformed output.
     *
     * @param xslName
     *            The name of the file that contains the xsl stylesheet.
     * @param in
     *            The DOM Node to be transformed
     */
    public String transform(String xslName, org.w3c.dom.Node in)
            throws Exception
    {
        StringWriter sw = new StringWriter();
        transform(xslName, in, sw);
        return sw.toString();
    }

    /**
     * Uses an xsl file to transform xml input from a reader and writes the
     * output to a writer.
     *
     * @param xslName
     *            The name of the file that contains the xsl stylesheet.
     * @param in
     *            The reader that passes the xml to be transformed
     * @param out
     *            The writer for the transformed output
     * @param params
     *            A set of parameters that will be forwarded to the XSLT
     */
    public void transform(String xslName, Reader in, Writer out, Map params)
            throws Exception
    {
        Source xmlin = new StreamSource(in);
        Result xmlout = new StreamResult(out);
        transform(xslName, xmlin, xmlout, params);
    }

    /**
     * Uses an xsl file to transform xml input from a reader and returns a
     * string containing the transformed output.
     *
     * @param xslName
     *            The name of the file that contains the xsl stylesheet.
     * @param in
     *            The reader that passes the xml to be transformed
     * @param params
     *            A set of parameters that will be forwarded to the XSLT
     */
    public String transform(String xslName, Reader in, Map params)
            throws Exception
    {
        StringWriter sw = new StringWriter();
        transform(xslName, in, sw, params);
        return sw.toString();
    }

    /**
     * Uses an xsl file to transform xml input from a DOM note and writes the
     * output to a writer.
     *
     * @param xslName
     *            The name of the file that contains the xsl stylesheet.
     * @param in
     *            The DOM Node to be transformed
     * @param out
     *            The writer for the transformed output
     * @param params
     *            A set of parameters that will be forwarded to the XSLT
     */
    public void transform(String xslName, Node in, Writer out, Map params)
            throws Exception
    {
        Source xmlin = new DOMSource(in);
        Result xmlout = new StreamResult(out);
        transform(xslName, xmlin, xmlout, params);
    }

    /**
     * Uses an xsl file to transform xml input from a DOM note and returns a
     * string containing the transformed output.
     *
     * @param xslName
     *            The name of the file that contains the xsl stylesheet.
     * @param in
     *            The DOM Node to be transformed
     * @param params
     *            A set of parameters that will be forwarded to the XSLT
     */
    public String transform(String xslName, Node in, Map params)
            throws Exception
    {
        StringWriter sw = new StringWriter();
        transform(xslName, in, sw, params);
        return sw.toString();
    }

    /**
     * Uses an xsl file without any input.
     *
     * @param xslName The name of the file that contains the xsl stylesheet.
     * @param params A set of parameters that will be forwarded to the XSLT
     * @return the transformed output
     * @throws Exception the transformation failed
     */
    public String transform(String xslName, Map params) throws Exception {

        StringWriter sw = new StringWriter();
        transform(xslName, sw, params);
        return sw.toString();
    }

    /**
     * Uses an xsl file without any xml input (simplified stylesheet)
     *
     * @param xslName The name of the file that contains the xsl stylesheet
     * @param out The writer for the transformed output.
     * @param params A set of parameters that will be forwarded to the XSLT
     * @throws Exception the transformation failed
     */
    public void transform(String xslName, Writer out, Map params) throws Exception {

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();

        transform(xslName, document.getDocumentElement(), out, params);
    }

    /**
     * Retrieve a transformer for the given stylesheet name. If no stylesheet is
     * available for the provided name, an identity transformer will be
     * returned. This allows clients of this service to perform more complex
     * transformations (for example, where parameters must be set). When
     * possible prefer using one of the forms of {@link #transform}.
     *
     * @param xslName
     *            Identifies stylesheet to get transformer for
     * @return A transformer for that stylesheet
     * @throws Exception retrieving the transformer failed
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

    // ---------------- Avalon Lifecycle Methods ---------------------
    /**
     * Avalon component lifecycle method
     *
     * This method processes the repository path, to make it relative to the web
     * application root, if neccessary. It supports URL-style repositories.
     */
    public void configure(Configuration conf) throws ConfigurationException
    {
        StringBuffer sb = new StringBuffer(conf.getAttribute(STYLESHEET_PATH,
                "/"));

        // is URL?
        if (!sb.toString().matches("[a-zA-Z]{3,}://.*"))
        {
            // No
            if (sb.charAt(0) != '/')
            {
                sb.insert(0, '/');
            }
            sb.insert(0, applicationRoot);
            sb.insert(0, "file:");
        }

        if (sb.charAt(sb.length() - 1) != '/')
        {
            sb.append('/');
        }

        path = sb.toString();
        caching = conf.getAttributeAsBoolean(STYLESHEET_CACHING, false);
    }

    /**
     * Initializes the service.
     */
    public void initialize() throws Exception
    {
        tfactory = TransformerFactory.newInstance();
    }

    public void contextualize(Context context) throws ContextException
    {
        this.applicationRoot = context.get("urn:avalon:home").toString();
    }

    /**
     * Avalon component lifecycle method
     */
    public void service(ServiceManager manager)
    {
        XSLTServiceFacade.setService(this);
    }
}
