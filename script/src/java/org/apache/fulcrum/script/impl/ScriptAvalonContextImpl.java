package org.apache.fulcrum.script.impl;

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

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.fulcrum.script.ScriptAvalonContext;

/**
 * Holder class for Avalon related stuff.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */
public class ScriptAvalonContextImpl implements ScriptAvalonContext
{
    /** context key for persistent directory*/
    private final static String URN_AVALON_HOME = "context-root";

    /** context key for temporary directory */
    private final static String URN_AVALON_TEMP = "impl.workDir";

    /** the Avalon service manager instance */
    private ServiceManager serviceManager;

    /** the Avalon context */
    private Context context;

    /** the Avalon logger to use */
    private Logger logger;

    /** the Avalon configuration to be used */
    private Configuration configuration;

    /** the Avalon parameters to be used */
    private Parameters parameters;

    /**
     * Constructor
     * @param logger the Avalon logger
     * @param serviceManager the Avalon ServiceManager
     * @param context the Avalon context
     * @param configuration the Avalon configuration
     * @param parameters the Avalon parameters
     */
    public ScriptAvalonContextImpl(
        Logger logger,
        ServiceManager serviceManager,
        Context context,
        Configuration configuration,
        Parameters parameters )
    {
        this.logger = logger;
        this.serviceManager = serviceManager;
        this.context = context;
        this.configuration = configuration;
        this.parameters = parameters;
    }

    /**
     * @return Returns the context.
     */
    public Context getContext()
    {
        return context;
    }

    /**
     * @return Returns the logger.
     */
    public Logger getLogger()
    {
        return logger;
    }

    /**
     * @return Returns the serviceManager.
     */
    public ServiceManager getServiceManager()
    {
        return serviceManager;
    }

    /**
     * @return Returns the Avalon application directory
     */
    public File getApplicationDir()
        throws ContextException
    {
        return (File) this.context.get(URN_AVALON_HOME);
    }

    /**
     * @return Returns the Avalon application directory
     */
    public File getTempDir()
        throws ContextException
    {
        return (File) this.context.get(URN_AVALON_TEMP);
    }

    /**
     * @return Returns the configuration.
     */
    public Configuration getConfiguration()
    {
        return configuration;
    }

    /**
     * @return Returns the parameters.
     */
    public Parameters getParameters()
    {
        return parameters;
    }
}
