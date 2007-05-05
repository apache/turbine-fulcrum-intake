package org.apache.fulcrum.template.velocity;


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


import java.io.OutputStream;
import java.io.Writer;

import org.apache.fulcrum.template.TemplateException;
import org.apache.velocity.context.Context;

/**
 * This is a simple static accessor to common Velocity tasks such as
 * getting an instance of a context as well as handling a request for
 * processing a template.
 * <pre>
 * Context context = VelocityServiceFacade.getContext(data);
 * context.put("message", "Hello from Turbine!");
 * String results = VelocityServiceFacade.handleRequest(context, "helloWorld.vm");
 * data.getPage().getBody().addElement(results);
 * </pre>
 *
 * @author <a href="mailto:jmcnally@collab.net">John D. McNally</a>
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @author <a href="mailto:jvanzyl@zenplex.com">Jason van Zyl</a>
 * @author <a href="mailto:stack@collab.net">Michael Stack</a>
 * @version $Id$
 */
public class VelocityServiceFacade
{
    private static VelocityService velocityService;
    /**
     * Utility method for accessing the service
     * implementation
     *
     * @return a VelocityService implementation instance
     */
    protected static VelocityService getService()
    {
        return velocityService;
    }

    protected static void setService(VelocityService velocityService){
        VelocityServiceFacade.velocityService = velocityService;
    }

    /**
     * This allows you to pass in a context and a path to a template
     * file and then grabs an instance of the velocity service and
     * processes the template and returns the results as a String
     * object.
     *
     * @param context A Context.
     * @param template The path to the template file.
     * @return The processed template.
     * @exception Exception Error processing template.
     */
    public static String handleRequest(Context context, String template)
        throws TemplateException
    {
        return getService().handleRequest(context, template);
    }

    /**
     * @see org.apache.fulcrum.velocity.VelocityService#handleRequest(Context,
     * String, String, String)
     */
    public String handleRequest(Context context, String template,
                                String charset, String encoding)
        throws TemplateException
    {
        return getService().handleRequest(context, template, charset,
                                          encoding);
    }

    /**
     * Process the request and fill in the template with the values
     * you set in the Context.
     *
     * @param context A Context.
     * @param filename A String with the filename of the template.
     * @param out A OutputStream where we will write the process template as
     * a String.
     *
     * @exception Exception Error processing template.
     *
     * @see org.apache.fulcrum.velocity.VelocityService#handleRequest(Context,
     * String, OutputStream)
     */
    public static void handleRequest(Context context, String template,
                                     OutputStream out)
        throws TemplateException
    {
        getService().handleRequest(context, template, out);
    }

    /**
     * Process the request and fill in the template with the values
     * you set in the Context.
     *
     * @param context A Context.
     * @param template The path to the template file.
     * @param out A OutputStream where we will write the process template as
     * a String.
     * @param charset The character set to use when writing the result.
     * @param encoding The encoding to use when merging context and template.
     *
     * @exception Exception Error processing template.
     *
     * @see org.apache.fulcrum.velocity.VelocityService#handleRequest(Context,
     * String, OutputStream)
     */
    public static void handleRequest(Context context, String template,
                                     OutputStream out, String charset,
                                     String encoding)
        throws TemplateException
    {
        getService().handleRequest(context, template, out, charset, encoding);
    }

    /**
     * @see org.apache.fulcrum.velocity.VelocityService#handleRequest(Context,
     * String, Writer)
     */
    public static void handleRequest(Context context, String filename,
                                     Writer writer)
        throws TemplateException
    {
        getService().handleRequest(context, filename, writer, null);
    }

    /**
     * @see org.apache.fulcrum.velocity.VelocityService#handleRequest(Context,
     * String, Writer, String)
     */
    public static void handleRequest(Context context, String filename,
                                     Writer writer, String encoding)
        throws TemplateException
    {
        getService().handleRequest(context, filename, writer, encoding);
    }
}
