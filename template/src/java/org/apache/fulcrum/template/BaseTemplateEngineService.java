package org.apache.fulcrum.template;


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
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.thread.ThreadSafe;

/**
 * The base implementation of Turbine {@link
 * org.apache.fulcrum.template.TemplateEngineService}.
 *
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @version $Id$
 */
public abstract class BaseTemplateEngineService
    extends AbstractLogEnabled
    implements
        TemplateEngineService,
        Configurable,
        Contextualizable,
        ThreadSafe,
        Disposable,
        Serviceable
{

    private ServiceManager manager = null;

    /**
     * The application root
     */
    protected String applicationRoot;

    /**
     * A Map containing the configuration for the template
     * engine service. The configuration contains:
     *
     * 1) template extensions
     * 2) default page
     * 3) default screen
     * 4) default layout
     * 5) default navigation
     * 6) default error screen
     */
    private Hashtable configuration = new Hashtable();

    /**
     * @see org.apache.fulcrum.template.TemplateEngineService#getTemplateEngineServiceConfiguration
     */
    public Hashtable getTemplateEngineServiceConfiguration()
    {
        return configuration;
    }

    /**
     * @see org.apache.fulcrum.template.TemplateEngineService#getAssociatedFileExtensions
     */
    public String[] getAssociatedFileExtensions()
    {
        return (String[]) configuration.get(TEMPLATE_EXTENSIONS);
    }

    /**
     * @see org.apache.fulcrum.template.TemplateEngineService#templateExists
     */
    public abstract boolean templateExists(String template);

    public abstract String handleRequest(
        TemplateContext context,
        String template)
        throws TemplateException;

    public abstract void handleRequest(
        TemplateContext context,
        String template,
        OutputStream os)
        throws TemplateException;

    /**
     * @see org.apache.fulcrum.template.TemplateEngineService
     */
    public abstract void handleRequest(
        TemplateContext context,
        String template,
        Writer writer)
        throws TemplateException;

    // ---------------- Avalon Lifecycle Methods ---------------------

    /**
     * Used by subclasses in their configure method
     */
    protected void registerConfiguration(Configuration conf, String defaultExt)
        throws ConfigurationException
    {
        initConfiguration(conf, defaultExt);

        TemplateService component = null;
        try
        {
            component = (TemplateService) manager.lookup(TemplateService.ROLE);
            component.registerTemplateEngineService(this);
        }
        catch (ServiceException ce)
        {
            throw new RuntimeException(ce.getMessage());
        }
        finally
        {
            if (component != null)
            {
                manager.release(component);
            }
        }
    }

    /**
     * Note engine file extension associations.  First attempts to
     * pull a list of custom extensions from the property file value
     * keyed by <code>template.extension</code>.  If none are defined,
     * uses the value keyed by
     * <code>template.default.extension</code>, defaulting to the
     * emergency value supplied by <code>defaultExt</code>.
     *
     * @param defaultExt The default used when the default defined in the
     *                   properties file is missing or misconfigured.
     */
    private void initConfiguration(Configuration conf, String defaultExt)
        throws ConfigurationException
    {

        List extensionList = new ArrayList();
        final Configuration[] extensions =
            conf.getChildren("template-extension");
        if (extensions != null)
        {
            for (int i = 0; i < extensions.length; i++)
            {
                extensionList.add(extensions[i].getValue());
            }
        }

        String[] fileExtensionAssociations =
            (String[]) extensionList.toArray(new String[extensionList.size()]);

        if (fileExtensionAssociations == null
            || fileExtensionAssociations.length == 0)
        {
            Configuration defaultConf =
                conf.getChild("default-template-extension", true);
            fileExtensionAssociations = new String[1];
            fileExtensionAssociations[0] = defaultConf.getValue(defaultExt);
        }

        configuration.put(TEMPLATE_EXTENSIONS, fileExtensionAssociations);

        configuration.put(
            DEFAULT_PAGE_TEMPLATE,
            conf.getChild("default-page-template", true).getValue());

        configuration.put(
            DEFAULT_LAYOUT_TEMPLATE,
            conf.getChild("default-layout-template", true).getValue());
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

    public void contextualize(Context context) throws ContextException
    {
        this.applicationRoot = context.get("urn:avalon:home").toString();
    }
    /**
     * Avalon component lifecycle method
     */
    public void service(ServiceManager manager) throws ServiceException
    {
        this.manager = manager;

    }

    /**
     * Avalon component lifecycle method
     */
    public void dispose()
    {
        manager = null;
    }
}
