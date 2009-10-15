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
import java.io.Writer;
import java.util.Map;

import org.w3c.dom.Node;

/**
 * This is a static accesor class for {@link XSLTService}.
 *
 * @author <a href="mailto:leon@opticode.co.za">Leon Messerschmidt</a>
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 * @version $Id$
 */
public class XSLTServiceFacade
{
    private static XSLTService xsltService;

    /**
     * Utility method for accessing the service implementation
     *
     * @return a XSLTService implementation instance
     */
    protected static XSLTService getService()
    {
        return xsltService;
    }

    /**
     * Static utility method to set the service instance to be used in the
     * facade
     *
     * @param xsltService
     *            the service instance
     */
    protected static void setService(XSLTService xsltService)
    {
        XSLTServiceFacade.xsltService = xsltService;
    }

    /**
     * Uses an xsl file to transform xml input from a reader and writes the
     * output to a writer.
     *
     * @param xslName The name of the file that contains the xsl stylesheet.
     * @param in The reader that passes the xml to be transformed
     * @param out The writer for the transformed output
     * @throws Exception the transformation failed
     */
    public static void transform(String xslName, Reader in, Writer out)
            throws Exception
    {
        getService().transform(xslName, in, out);
    }

    /**
     * Uses an xsl file to transform xml input from a reader and returns a
     * string containing the transformed output.
     *
     * @param xslName The name of the file that contains the xsl stylesheet.
     * @param in The reader that passes the xml to be transformed
     * @return the transformed output
     * @throws Exception the transformation failed
     */
    public static String transform(String xslName, Reader in) throws Exception
    {
        return getService().transform(xslName, in);
    }

    /**
     * Uses an xsl file to transform xml input from a DOM note and writes the
     * output to a writer.
     *
     * @param xslName The name of the file that contains the xsl stylesheet.
     * @param in The DOM Node to be transformed
     * @param out The writer for the transformed output
     * @throws Exception the transformation failed
     */
    public void transform(String xslName, Node in, Writer out) throws Exception
    {
        getService().transform(xslName, in, out);
    }

    /**
     * Uses an xsl file to transform xml input from a DOM note and returns a
     * string containing the transformed output.
     *
     * @param xslName The name of the file that contains the xsl stylesheet.
     * @param in The DOM Node to be transformed
     * @return the transformed output
     * @throws Exception the transformation failed
     */
    public String transform(String xslName, Node in) throws Exception
    {
        return getService().transform(xslName, in);
    }

    /**
     * Uses an xsl file to transform xml input from a reader and writes the
     * output to a writer.
     *
     * @param xslName The name of the file that contains the xsl stylesheet.
     * @param in The reader that passes the xml to be transformed
     * @param out The writer for the transformed output
     * @param params A set of parameters that will be forwarded to the XSLT
     * @throws Exception the transformation failed
     */
    void transform(String xslName, Reader in, Writer out, Map params) throws Exception
    {
        getService().transform(xslName, in, out, params);
    }

    /**
     * Uses an xsl file to transform xml input from a reader and returns a
     * string containing the transformed output.
     *
     * @param xslName The name of the file that contains the xsl stylesheet.
     * @param in The reader that passes the xml to be transformed
     * @param params A set of parameters that will be forwarded to the XSLT
     * @return the transformed output
     * @throws Exception the transformation failed
     */
    String transform(String xslName, Reader in, Map params) throws Exception
    {
        return getService().transform(xslName, in, params);
    }

    /**
     * Uses an xsl file to transform xml input from a DOM note and writes the
     * output to a writer.
     *
     * @param xslName The name of the file that contains the xsl stylesheet.
     * @param in The DOM Node to be transformed
     * @param out The writer for the transformed output
     * @param params A set of parameters that will be forwarded to the XSLT
     * @throws Exception the transformation failed
     */
    void transform(String xslName, Node in, Writer out, Map params) throws Exception
    {
        getService().transform(xslName, in, out, params);
    }

    /**
     * Uses an xsl file to transform xml input from a DOM note and returns a
     * string containing the transformed output.
     *
     * @param xslName The name of the file that contains the xsl stylesheet.
     * @param in The DOM Node to be transformed
     * @param params A set of parameters that will be forwarded to the XSLT
     * @return the transformed output
     * @throws Exception the transformation failed
     */
    String transform(String xslName, Node in, Map params) throws Exception
    {
        return getService().transform(xslName, in, params);
    }
}
