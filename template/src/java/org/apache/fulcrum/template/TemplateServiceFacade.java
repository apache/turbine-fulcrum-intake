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


import java.io.OutputStream;
import java.io.Writer;
import org.apache.fulcrum.template.TemplateContext;

/**
 * This is a simple static accessor to common TemplateService tasks such as
 * getting a Screen that is associated with a screen template.
 *
 * @author <a href="mailto:jmcnally@collab.net">John D. McNally</a>
 * @version $Id$
 */
public abstract class TemplateServiceFacade
{
    private static TemplateService templateService;
    
    /**
     * @param localizationService
     */
    public static void setTemplateService(TemplateService templateService) {
        TemplateServiceFacade.templateService = templateService;
    }    
    /**
     * Utility method for accessing the service
     * implementation
     *
     * @return a TemplateService implementation instance
     */
    protected static TemplateService getService()
    {
        return templateService;
    }

    public static final void registerTemplateEngineService(TemplateEngineService service)
    {
        getService().registerTemplateEngineService(service);
    }

    public static final String[] translateTemplatePaths(String[] templatePaths)
    {
        return getService().translateTemplatePaths(templatePaths);
    }

    public static final boolean templateExists(String template, String[] templatePaths)
    {
        return getService().templateExists(template, templatePaths);
    }

    public static final String handleRequest(TemplateContext context, String template)
        throws TemplateException
    {
        return getService().handleRequest(context, template);
    }

    public static final void handleRequest(TemplateContext context,
                                             String template,
                                             OutputStream outputStream)
        throws TemplateException
    {
        getService().handleRequest(context, template, outputStream);
    }

    public static final void handleRequest(TemplateContext context,
                                           String template,
                                           Writer writer)
        throws TemplateException
    {
        getService().handleRequest(context, template, writer);
    }

    public static final TemplateContext getTemplateContext()
    {
        return getService().getTemplateContext();
    }

    public static final boolean templateExists(String template)
    {
        return getService().templateExists(template);
    }
}
