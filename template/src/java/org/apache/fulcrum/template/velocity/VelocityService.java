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
import org.apache.velocity.app.event.EventCartridge;
import org.apache.velocity.context.Context;

/**
 * The Turbine service interface to
 * <a href="http://velocity.apache.org/engine/">Velocity</a>.
 *
 * @author <a href="mailto:jmcnally@collab.net">John D. McNally</a>
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @version $Id$
 */
public interface VelocityService
{
    String ROLE = VelocityService.class.getName();

    /**
     * Process the request and fill in the template using the values
     * set in <code>context</code>.
     *
     * @param context A context to use when evaluating the specified
     * template.
     * @param filename The file name of the template.
     * @return The processed template.
     * @exception Exception, a generic exception.
     */
    public String handleRequest(Context context, String filename)
        throws TemplateException;

    /**
     * Process the request and fill in the template using the values
     * set in <code>context</code>.
     *
     * @param context A context to use when evaluating the specified
     * template.
     * @param filename The file name of the template.
     * @param charset The character set to use when writing the result.
     * @param encoding The encoding to use when merging context and
     * template.
     * @return The processed template.
     * @exception Exception, a generic exception.
     */
    public String handleRequest(Context context, String template,
                                String charset, String encoding)
        throws TemplateException;

    /**
     * Process the request and fill in the template using the values
     * set in <code>context</code>.
     *
     * @param context A context to use when evaluating the specified
     * template.
     * @param filename The file name of the template.
     * @param out The stream to which we will write the processed
     * template as a String.
     * @throws ServiceException Any exception trown while processing will be
     *         wrapped into a ServiceException and rethrown.
     */
    public void handleRequest(Context context, String filename,
                              OutputStream out)
        throws TemplateException;

    /**
     * Process the request and fill in the template using the values
     * set in <code>context</code>.
     *
     * @param context A context to use when evaluating the specified
     * template.
     * @param filename The file name of the template.
     * @param out The stream to which we will write the processed
     * template as a String.
     * @param charset The character set to use when writing the result.
     * @param encoding The encoding to use when merging context and
     * template.
     * @throws ServiceException Any exception trown while processing will be
     *         wrapped into a ServiceException and rethrown.
     */
    public void handleRequest(Context context, String filename,
                              OutputStream out, String charset,
                              String encoding)
        throws TemplateException;

    /**
     * Process the request and fill in the template using the values
     * set in <code>context</code>.
     *
     * @param context A context to use when evaluating the specified
     * template.
     * @param filename The file name of the template.
     * @param writer The writer to which we will write the processed template.
     * @throws ServiceException Any exception trown while processing will be
     *         wrapped into a ServiceException and rethrown.
     */
    public void handleRequest(Context context, String filename,
                              Writer writer)
        throws TemplateException;

    /**
     * Process the request and fill in the template using the values
     * set in <code>context</code>.
     *
     * @param context A context to use when evaluating the specified
     * template.
     * @param filename The file name of the template.
     * @param writer The writer to which we will write the processed template.
     * @param encoding The encoding to use when merging context and
     * template.
     * @throws ServiceException Any exception trown while processing will be
     *         wrapped into a ServiceException and rethrown.
     */
    public void handleRequest(Context context, String filename,
                              Writer writer, String encoding)
        throws TemplateException;

    /**
     * Returns the populated event cartridge or null if it has not been populated
     */
    public EventCartridge getEventCartridge();

    /**
     * By default, this is true if there is configured event cartridges.
     * You can disable EC processing if you first disable it and then call
     * handleRequest.
     */
    public void setEventCartridgeEnabled(boolean value);
}
