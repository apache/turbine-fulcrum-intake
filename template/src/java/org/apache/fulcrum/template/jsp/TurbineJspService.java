package org.apache.fulcrum.template.jsp;


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
import java.util.ArrayList;
import java.util.List;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.fulcrum.template.BaseTemplateEngineService;
import org.apache.fulcrum.template.TemplateContext;
import org.apache.fulcrum.template.TemplateException;
import org.apache.fulcrum.template.TemplateServiceFacade;

/**
 * This is a Service that can process JSP templates from within a Turbine
 * screen.
 *
 * @author <a href="mailto:jmcnally@collab.net">John D. McNally</a>
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 */
public class TurbineJspService
    extends BaseTemplateEngineService
    implements JspService
{
    /** paths as given in the configuration file */
    private String[] rawPaths;

    /** The base path[s] prepended to filenames given in arguments */
    private String[] templatePaths;

    /** The relative path[s] prepended to filenames */
    private String[] relativeTemplatePaths;

    /** The buffer size for the output stream. */
    private int bufferSize;

    /**
     * Adds some convenience objects to the request.  For example an instance
     * of JspLink which can be used to generate links to other templates.
     *
     * @param RunData the turbine rundata object
     */

    /*
    public void addDefaultObjects(RunData data)
    {
        HttpServletRequest req = data.getRequest();
        //req.setAttribute(LINK, new JspLink(data));
        req.setAttribute(RUNDATA, data);
    }
    */

    /**
     * The buffer size
     *
     */
    public int getDefaultBufferSize()
    {
        return bufferSize;
    }

    /**
     * Process the request
     *
     * @param RunData
     * @param String the filename of the template.
     * @throws ServiceException Any exception trown while processing will be
     *         wrapped into a ServiceException and rethrown.
     */
    /*
    public void handleRequest(RunData data, String filename)
       throws ServiceException
    {
       handleRequest(data, filename, false);
    }
    */

    /**
     * Process the request
     *
     * @param RunData
     * @param String the filename of the template.
     * @param boolean whether to perform a forward or include.
     * @throws ServiceException Any exception trown while processing will be
     *         wrapped into a ServiceException and rethrown.
     */
    /*
    public void handleRequest(RunData data, String filename, boolean isForward)
       throws ServiceException
    {
       // template name with relative path
       String relativeTemplateName = getRelativeTemplateName(filename);
    
       if (relativeTemplateName == null)
       {
           throw new ServiceException(
           "Template " + filename + " not found in template paths");
       }
    
       // get the RequestDispatcher for the JSP
       RequestDispatcher dispatcher = data.getServletContext()
           .getRequestDispatcher(relativeTemplateName);
    
       try
       {
           if (isForward)
           {
               // forward the request to the JSP
               dispatcher.forward( data.getRequest(), data.getResponse() );
           }
           else
           {
               data.getOut().flush();
               // include the JSP
               dispatcher.include( data.getRequest(), data.getResponse() );
           }
       }
       catch(Exception e)
       {
           // as JSP service is in Alpha stage, let's try hard to send the error
           // message to the browser, to speed up debugging
           try
           {
               data.getOut().print("Error encountered processing a template: "+filename);
               e.printStackTrace(data.getOut());
           }
           catch(IOException ignored)
           {
           }
    
           // pass the exception to the caller according to the general
           // contract for tamplating services in Turbine
           throw new ServiceException(
               "Error encountered processing a template:" + filename, e);
       }
    }
    */

    /**
     * Currently a no-op.  This method must be implemented so that the
     * the JSP service comformith to the TemplateEngineService
     * interface.
     */
    public String handleRequest(TemplateContext context, String template)
        throws TemplateException
    {
        // TODO: Implement me!
        return "";
    }

    /**
     * Currently a no-op.  This method must be implemented so that the
     * the JSP service comformith to the TemplateEngineService
     * interface.
     */
    public void handleRequest(
        TemplateContext context,
        String template,
        OutputStream outputStream)
        throws TemplateException
    {
        // TODO: Implement me!
    }

    /**
     * @see org.apache.fulcrum.template.TemplateEngineService#handleRequest(
     * Context, String, Writer)
     */
    public void handleRequest(
        TemplateContext context,
        String template,
        Writer writer)
        throws TemplateException
    {
        // TODO: Implement me!
    }

    /**
     * Determine whether a given template exists. This service
     * currently only supports file base template hierarchies
     * so we will use the utility methods provided by
     * the template service to do the searching.
     *
     * @param String template
     * @return boolean
     */
    public boolean templateExists(String template)
    {
        return TemplateServiceFacade.templateExists(template, templatePaths);
    }
    /**
     * Searchs for a template in the default.template path[s] and
     * returns the template name with a relative path which is
     * required by <a href="http://java.sun.com/products/servlet/2.3/javadoc/javax/servlet/ServletContext.html#getRequestDispatcher(java.lang.String)">
     * javax.servlet.RequestDispatcher</a>
     *
     * @param String template
     * @return String
     */

    public String getRelativeTemplateName(String template)
    {
        /*
         * A dummy String[] object used to pass a String to
         * TurbineTemplate.templateExists
         */
        String[] testTemplatePath = new String[1];

        /**
         * Find which template path the template is in
         */
        for (int i = 0; i < relativeTemplatePaths.length; i++)
        {
            testTemplatePath[0] = getRealPath(relativeTemplatePaths[i]);
            if (TemplateServiceFacade
                .templateExists(template, testTemplatePath))
            {
                return relativeTemplatePaths[i] + template;
            }
        }

        return null;
    }

    /*
    public void doBuildBeforeAction(RunData data)
    {
        addDefaultObjects(data);
    
        try
        {
            //We try to set the buffer size from defaults
            data.getResponse().setBufferSize(getDefaultBufferSize());
        }
        catch (IllegalStateException ise )
        {
            //If the response was already commited, we die silently
            //No logger here?
        }
    }
    
    public void doBuildAfterAction(RunData data)
    {
        // do nothing
    }
    
    public void doPostBuild(RunData data)
    {
        // do nothing
    }
    */

    // ---------------- Avalon Lifecycle Methods ---------------------

    /**
     * Avalon component lifecycle method
     */
    public void configure(Configuration conf) throws ConfigurationException
    {

        List pathList = new ArrayList();
        final Configuration[] paths = conf.getChildren("template-path");
        if (paths != null)
        {
            for (int i = 0; i < paths.length; i++)
            {
                pathList.add(paths[i].getValue());
            }
        }
        rawPaths = (String[]) pathList.toArray(new String[pathList.size()]);

        bufferSize = conf.getAttributeAsInteger("buffer-size", 8192);

        // Register with the template service.
        registerConfiguration(conf, "jsp");

        // Use the turbine template service to translate the template paths.
        templatePaths = TemplateServiceFacade.translateTemplatePaths(rawPaths);

        // Set relative paths from config.
        // Needed for javax.servlet.RequestDispatcher
        relativeTemplatePaths = rawPaths;
        // Make sure that the relative paths begin with /
        for (int i = 0; i < relativeTemplatePaths.length; i++)
        {
            if (!relativeTemplatePaths[i].startsWith("/"))
            {
                relativeTemplatePaths[i] = "/" + relativeTemplatePaths[i];
            }
        }
    }

}
