package org.apache.fulcrum.bsf;

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
