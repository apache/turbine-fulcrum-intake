package org.apache.fulcrum.template;

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
    public void service(ServiceManager manager)
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
