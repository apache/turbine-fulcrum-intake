package org.apache.fulcrum.groovy.impl;

/*
 * Copyright 2004 Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.fulcrum.groovy.GroovyAvalonContext;

/**
 * Holder class for Avalon related stuff
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */
public class GroovyAvalonContextImpl implements GroovyAvalonContext
{
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
    public GroovyAvalonContextImpl(
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
        return (File) this.context.get("urn:avalon:home");
    }

    /**
     * @return Returns the Avalon application directory
     */
    public File getTempDir()
        throws ContextException
    {
        return (File) this.context.get("urn:avalon:temp");
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
