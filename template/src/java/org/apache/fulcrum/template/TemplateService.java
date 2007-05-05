package org.apache.fulcrum.template;


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

/**
 * This service provides a method for mapping templates to their
 * appropriate Screens or Navigations.  It also allows templates to
 * define a layout/navigations/screen modularization within the
 * template structure.  It also performs caching if turned on in the
 * properties file.
 *
 * @author <a href="mailto:jmcnally@collab.net">John D. McNally</a>
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @author <a href="mailto:ilkka.priha@simsoft.fi">Ilkka Priha</a>
 * @version $Id$
 */
public interface TemplateService
{

    String ROLE = TemplateService.class.getName();

    /**
     * The key to the template context.
     */
    public static final String CONTEXT = "TEMPLATE_CONTEXT";

    /**
     * Translates the supplied template paths into their Turbine-canonical
     * equivalent (probably absolute paths).
     *
     * @param templatePaths An array of template paths.
     * @return An array of translated template paths.
     */
    public String[] translateTemplatePaths(String[] templatePaths);

    /**
     * Delegates to the appropriate {@link
     * org.apache.fulcrum.template.TemplateEngineService} to
     * check the existance of the specified template.
     *
     * @param template      The template to check for the existance of.
     * @param templatePaths The paths to check for the template.
     */
    public boolean templateExists(String template,
                                  String[] templatePaths);

    /**
     * Registers the provided template engine for use by the
     * <code>TemplateService</code>.
     *
     * @param service The <code>TemplateEngineService</code> to register.
     */
    public void registerTemplateEngineService(TemplateEngineService service);

    public String handleRequest(TemplateContext context, String template)
        throws TemplateException;

    public void handleRequest(TemplateContext context, String template,
                              OutputStream outputStream)
        throws TemplateException;

    public void handleRequest(TemplateContext context, String template,
                              Writer writer)
        throws TemplateException;

    public TemplateContext getTemplateContext();

    public boolean templateExists(String template);
}
