package org.apache.fulcrum.jsp;

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
import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
//import org.apache.turbine.RunData;
import org.apache.fulcrum.ServiceException;
//import org.apache.fulcrum.jsp.util.JspLink;
import org.apache.fulcrum.InitializationException;
import org.apache.fulcrum.template.BaseTemplateEngineService;
import org.apache.fulcrum.template.TurbineTemplate;
import org.apache.fulcrum.template.TemplateContext;

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
    /** The base path[s] prepended to filenames given in arguments */
    private String[] templatePaths;

    /** The relative path[s] prepended to filenames */
    private String[] relativeTemplatePaths;

    /** The buffer size for the output stream. */
    private int bufferSize;

    /**
     * Performs early initialization of this Turbine service.
     */
    public void init()
        throws InitializationException
    {
        try
        {
            initJsp();
            registerConfiguration("jsp");
            setInit(true);
        }
        catch (Exception e)
        {
            throw new InitializationException(
                "TurbineJspService failed to initialize", e);
        }
    }

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
        throws ServiceException
    {
        // TODO: Implement me!
        return "";
    }

    /**
     * Currently a no-op.  This method must be implemented so that the
     * the JSP service comformith to the TemplateEngineService
     * interface.
     */
    public void handleRequest(TemplateContext context, String template,
                              OutputStream outputStream)
        throws ServiceException
    {
        // TODO: Implement me!
    }

    /**
     * This method sets up the template cache.
     */
    private void initJsp() throws Exception
    {
        /*
         * Use the turbine template service to translate
         * the template paths.
         */
        templatePaths = TurbineTemplate.translateTemplatePaths(
        getConfiguration().getStringArray("templates"));

        /*
         * Set relative paths from config.
         * Needed for javax.servlet.RequestDispatcher
         */
        relativeTemplatePaths = getConfiguration().getStringArray("templates");

        /*
         * Make sure that the relative paths begin with /
         */
        for (int i = 0; i < relativeTemplatePaths.length; i++)
        {
            if (!relativeTemplatePaths[i].startsWith("/"))
            {
                relativeTemplatePaths[i] = "/" + relativeTemplatePaths[i];
            }
        }

        bufferSize = getConfiguration().getInt("buffer.size", 8192);

        /*
         * Register with the template service.
         */
        registerConfiguration("jsp");
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
        return TurbineTemplate.templateExists(template, templatePaths);
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
            if (TurbineTemplate.templateExists(template, testTemplatePath))
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
}
