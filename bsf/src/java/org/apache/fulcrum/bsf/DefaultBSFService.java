package org.apache.fulcrum.bsf;

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
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.StringWriter;


import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;

/**
 *
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 */
public class DefaultBSFService extends AbstractLogEnabled
    implements BSFService, Configurable, Initializable, ThreadSafe, Contextualizable
{

      /**
       * The application root
       */
    private String applicationRoot;
    /**
     * Tag for scripts directory in the service
     * configuration.
     */
    protected static final String SCRIPTS_DIRECTORY = "scriptsDirectory";

    /**
     * Tag for default extension in the service
     * configuration.
     */
    protected static final String DEFAULT_EXTENSION = "defaultExtension";

    /**
     * BSF manager that is responsible for executing scripts.
     * This may eventually be a pool of managers and
     * utilize the pool service.
     */
    protected BSFManager manager;

    /**
     * Directory where scripts are stored
     */
    protected String scriptsDirectory;

    /**
     * Default extension for scripts if an extension is not
     * provided.
     */
    protected String defaultExtension;


    public DefaultBSFService()
    {
    }

    /**
     * Execute a script. The script can be in any of
     * the scripting languages supported by the BSF.
     *
     * @param String name of the script
     */
    public void execute(String script)
    {
        if (script.lastIndexOf('.') == -1)
        {
            script += '.' + defaultExtension;
        }

        script = getRealPath(scriptsDirectory + "/" + script);

        getLogger().debug("[BSFService] Script to execute: " + script);

        try
        {
            manager.exec(BSFManager.getLangFromFilename(script),
                script, 0, 0, fileContentsToString(script));
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
        catch (BSFException bsfe)
        {
            /*
             * Find out what went wrong with the processing
             * of the script.
             */
            int reason = bsfe.getReason();
            System.out.println("Reason:" + reason);
            bsfe.printStackTrace();
        }
    }

    /**
     * Create a string from the contents of a file.
     *
     * @param String file from which to read the contents
     * @return String file contents
     *
     */
    protected String fileContentsToString(String file)
        throws IOException
    {
        StringWriter sw = new StringWriter();

        BufferedReader reader = new BufferedReader(
            new InputStreamReader(new FileInputStream(file)));

        char buf[] = new char[1024];
        int len = 0;

        while ( ( len = reader.read( buf, 0, 1024 )) != -1)
        {
            sw.write( buf, 0, len );
        }

        return sw.toString();
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

    /**
     * Avalon component lifecycle method
     */
    public void configure(Configuration conf)
        throws ConfigurationException
    {

            scriptsDirectory = conf.getAttribute(SCRIPTS_DIRECTORY, null);
            if (scriptsDirectory == null)
            {
                throw new ConfigurationException(
                    "You must provide a scripts directory in " +
                    "order to executes scripts!");
            }

            defaultExtension = conf.getAttribute(DEFAULT_EXTENSION, "bsf");

    }

    public void contextualize(Context context) throws ContextException {
        this.applicationRoot = context.get( "urn:avalon:home" ).toString();
    }

    /**
     * Avalon component lifecycle method
     */
    public void initialize()
    {
        manager = new BSFManager();
    }

}
