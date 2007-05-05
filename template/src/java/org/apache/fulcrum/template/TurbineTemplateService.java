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


import java.io.File;
import java.io.OutputStream;
import java.io.Writer;
import java.util.HashMap;

import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.thread.ThreadSafe;

/**
 * This service provides a method for mapping templates to their
 * appropriate Screens or Navigations.  It also allows templates to
 * define a layout/navigations/screen modularization within the
 * template structure.  It also performs caching if turned on in the
 * properties file.
 *
 * Since everything is keyed off the template variable,
 * if data.getParameters().getString("template") returns
 * /about_us/directions/driving.vm, the search for the
 * Screen class is as follows (in order):
 *
 * 1. about_us.directions.Driving
 * 2. about_us.directions.Default
 * 3. about_us.Default
 * 4. Default
 * 5. VelocityScreen
 *
 * If the template variable does not exist, then VelocityScreen will be
 * executed and templates/screens/index.vm will be executed.
 * If index.vm is not found or if the template is invalid or Velocity
 * execution throws an exception of any reason, then
 * templates/screens/error.vm will be executed.
 *
 * For the Layouts and Navigations, the following paths will be
 * searched in the layouts and navigations template
 * subdirectories (in order):
 *
 * 1./about_us/directions/driving.vm
 * 2./about_us/directions/default.vm
 * 3./about_us/default.vm
 * 4./default.vm
 *
 * @author <a href="mailto:jmcnally@collab.net">John D. McNally</a>
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @author <a href="mailto:ilkka.priha@simsoft.fi">Ilkka Priha</a>
 * @version $Id$
 */
public class TurbineTemplateService
    extends AbstractLogEnabled
    implements TemplateService, Contextualizable, ThreadSafe
{
    /**
     * The default file extension used as a registry key when a
     * template's file extension cannot be determined.
     */
    protected static final String NO_FILE_EXT = "";

    /**
     * Default extension for templates.
     */
    private String defaultExtension;

    /**
     * The application root
     */
    private String applicationRoot;

    /**
     * The mappings of template file extensions to {@link
     * org.apache.fulcrum.template.TemplateEngineService}
     * implementations.  Implementing template engines can locate
     * templates within the capability of any resource loaders they
     * may posses, and other template engines are stuck with file
     * based template hierarchy only.
     */
    private HashMap templateEngineRegistry;

    public TurbineTemplateService()
    {
    }

    /**
     * Translates the supplied template paths into their Turbine-canonical
     * equivalent (probably absolute paths).
     *
     * @param templatePaths An array of template paths.
     * @return An array of translated template paths.
     */
    public String[] translateTemplatePaths(String[] templatePaths)
    {
        for (int i = 0; i < templatePaths.length; i++)
        {
            templatePaths[i] = getRealPath(templatePaths[i]);
        }
        return templatePaths;
    }

    /**
     * Looks for the specified template file in each of the specified paths.
     *
     * @param template The template file to check for the existance of.
     * @param templatePaths The paths to check for the template.
     *
     * @return true if a match is found in one of the supplied paths, or false.
     */
    public boolean templateExists(String template,
                                  String[] templatePaths)
    {
        for (int i = 0; i < templatePaths.length; i++)
        {
            if (new File(templatePaths[i],template).exists())
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Determine if a template exists. Delegates to the appropriate {@link
     * org.apache.fulcrum.template.TemplateEngineService} to check the
     * existance of the specified template. If no template engine service is
     * found for the template, false is returned.
     *
     * @param template The template file to check for the existence of.
     *
     * @return true if there is a template engine service registered for the
     *         given template, and it reports that the template exists,
     *         otherwise false.
     */
    public boolean templateExists(String template)
    {
        TemplateEngineService tes = getTemplateEngineService(template);

        if (tes != null)
        {
            return tes.templateExists(template);
        }
        else
        {
            return false;
        }
    }

    /**
     * Registers the provided template engine for use by the
     * <code>TemplateService</code>.
     *
     * @param service The <code>TemplateEngineService</code> to register.
     */
    public synchronized void registerTemplateEngineService(TemplateEngineService service)
    {
        // Clone the registry to write to non-sync'd
        // Map implementations.
        HashMap registry = templateEngineRegistry != null ?
            (HashMap) templateEngineRegistry.clone() : new HashMap();

        String[] exts = service.getAssociatedFileExtensions();

        for (int i = 0; i < exts.length; i++)
        {
            registry.put(exts[i], service);
        }
        templateEngineRegistry = registry;
    }

    /**
     * The {@link org.apache.fulcrum.template.TemplateEngineService}
     * associated with the specified template's file extension.
     *
     * @param template The template name.
     * @return The template engine service.
     */
    protected TemplateEngineService getTemplateEngineService(String template)
    {
        HashMap registry = templateEngineRegistry;
        if (registry != null && template != null)
        {
            int dotIndex = template.lastIndexOf('.');
            String ext = dotIndex == -1 ?
                defaultExtension : template.substring(dotIndex + 1);
            return (TemplateEngineService) registry.get(ext);
        }
        else
        {
            return null;
        }
    }

    public String handleRequest(TemplateContext context, String template)
        throws TemplateException
    {
        TemplateEngineService tes = getTemplateEngineService(template);
        return tes.handleRequest(context, template);
    }

    public void handleRequest(TemplateContext context, String template,
                              OutputStream outputStream)
        throws TemplateException
    {
        TemplateEngineService tes = getTemplateEngineService(template);
        tes.handleRequest(context, template, outputStream);
    }

    public void handleRequest(TemplateContext context, String template,
                              Writer writer)
        throws TemplateException
    {
        TemplateEngineService tes = getTemplateEngineService(template);
        tes.handleRequest(context, template, writer);
    }

    public TemplateContext getTemplateContext()
    {
        return new DefaultTemplateContext();
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

    public void contextualize(Context context) throws ContextException {
        this.applicationRoot = context.get( "urn:avalon:home" ).toString();
    }


}
