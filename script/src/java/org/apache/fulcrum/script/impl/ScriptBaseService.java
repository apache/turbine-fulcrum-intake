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

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.Reconfigurable;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;

/**
 * Base class for a service implementation capturing the Avalon
 * configuration artifats. This is basically a "copy-and-waste" from
 * the YAAFI framework.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public abstract class ScriptBaseService
    extends AbstractLogEnabled
    implements Contextualizable, Serviceable, Configurable, Parameterizable, Reconfigurable, Disposable
{
    /** context key for persistent directory */
    private final static String URN_AVALON_HOME = "urn:avalon:home";

    /** context key for temporary directory */
    private final static String URN_AVALON_TEMP = "urn:avalon:temp";

    /** The context supplied by the avalon framework */
    private Context context;

    /** The service manager supplied by the avalon framework */
    private ServiceManager serviceManager;

    /** The configuraton supplied by the avalon framework */
    private Configuration configuration;

    /** The parameters supplied by the avalon framework */
    private Parameters parameters;

    /** the Avalon application directory */
    private File applicationDir;

    /** the Avalon temp directory */
    private File tempDir;

    /**
     * Constructor
     */
    public ScriptBaseService()
    {
        // nothing to do
    }

    /**
     * @see org.apache.avalon.framework.context.Contextualizable#contextualize(org.apache.avalon.framework.context.Context)
     */
    public void contextualize(Context context) throws ContextException
    {
        this.context        = context;
        this.applicationDir = (File) context.get(URN_AVALON_HOME);
        this.tempDir        = (File) context.get(URN_AVALON_TEMP);
    }

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager serviceManager) throws ServiceException
    {
        this.serviceManager = serviceManager;
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration configuration) throws ConfigurationException
    {
        this.configuration = configuration;
    }

    /**
     * @see org.apache.avalon.framework.parameters.Parameterizable#parameterize(org.apache.avalon.framework.parameters.Parameters)
     */
    public void parameterize(Parameters parameters) throws ParameterException
    {
        this.parameters = parameters;
    }

    /**
     * @see org.apache.avalon.framework.configuration.Reconfigurable#reconfigure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void reconfigure(Configuration configuration)
        throws ConfigurationException
    {
        this.configuration = configuration;
    }

    /**
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     */
    public void dispose()
    {
        this.applicationDir = null;
        this.tempDir = null;
        this.context = null;
        this.serviceManager = null;
        this.parameters = null;
        this.configuration = null;
    }

    /**
     * @return Returns the configuration.
     */
    protected Configuration getConfiguration()
    {
        return this.configuration;
    }

    /**
     * @return Returns the context.
     */
    protected Context getContext()
    {
        return this.context;
    }

    /**
     * @return Returns the parameters.
     */
    protected Parameters getParameters()
    {
        return this.parameters;
    }

    /**
     * @return Returns the serviceManager.
     */
    protected ServiceManager getServiceManager()
    {
        return this.serviceManager;
    }

    /**
     * @return Returns the applicationDir.
     */
    public File getApplicationDir()
    {
        return applicationDir;
    }

    /**
     * @return Returns the tempDir.
     */
    public File getTempDir()
    {
        return tempDir;
    }
}
