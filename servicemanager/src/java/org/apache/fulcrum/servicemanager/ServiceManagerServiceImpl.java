package org.apache.fulcrum.servicemanager;

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

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.Reconfigurable;

/**
 * This is a sort of "edelhack" to solve the problem of accessing
 * the Avalon infrastructure without having an instance of the
 * container. The implementation stores the very first instance
 * of itself in a static variable which can be accessed using
 * getInstance().
 *
 * This allows access to the various Avalon artifacts.
 *
 *  @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public class ServiceManagerServiceImpl
    implements ServiceManagerService, LogEnabled, Contextualizable, Reconfigurable, Parameterizable, Serviceable, Disposable
{
    /** The one and only instance */
    private static ServiceManagerServiceImpl instance;

    /** Store the Avalon logger */
    private Logger logger;
    
    /** Store the ServiceContainer on a per instance base */
    private ServiceManager serviceManager;

    /** Store the passed parameters on a per instance base */
    private Parameters parameters;

    /** Store the passed context on a per instance base */
    private Context context;

    /** Store the passed configuration on a per instance base */
    private Configuration configuration;

    /**
     * Constructor
     */
    public ServiceManagerServiceImpl()
    {
        setInstance(this);
    }

    /////////////////////////////////////////////////////////////////////////
    // Avalon Lifecycle Implementation
    /////////////////////////////////////////////////////////////////////////

    /**
     * @see org.apache.avalon.framework.logger.LogEnabled#enableLogging(org.apache.avalon.framework.logger.Logger)
     */
    public void enableLogging(Logger logger)
    {
        this.logger = logger;
    }

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager serviceManager) throws ServiceException
    {
        this.serviceManager = serviceManager;
    }

    /**
     * @see org.apache.avalon.framework.context.Contextualizable#contextualize(org.apache.avalon.framework.context.Context)
     */
    public void contextualize(Context context) throws ContextException
    {
        this.context = context;
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration configuration) throws ConfigurationException
    {
        this.configuration = configuration;
    }

    /**
     * @see org.apache.avalon.framework.configuration.Reconfigurable#reconfigure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void reconfigure(Configuration configuration) throws ConfigurationException
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
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     */
    public void dispose()
    {
        this.serviceManager = null;
        this.parameters = null;
        this.context = null;
        this.configuration = null;
        this.logger = null;
        ServiceManagerServiceImpl.instance = null;
    }

    /////////////////////////////////////////////////////////////////////////
    // Service Interface Implementation
    /////////////////////////////////////////////////////////////////////////

    /**
     * @see org.apache.avalon.framework.service.ServiceManager#hasService(java.lang.String)
     */
    public boolean hasService(String name)
    {
        return this.serviceManager.hasService(name);
    }

    /**
     * @see org.apache.avalon.framework.service.ServiceManager#lookup(java.lang.String)
     */
    public Object lookup(String name) throws ServiceException
    {
        return this.serviceManager.lookup(name);
    }

    /**
     * @see org.apache.avalon.framework.service.ServiceManager#release(java.lang.Object)
     */
    public void release(Object object)
    {
        this.serviceManager.release(object);
    }

    /**
     * @return the ServiceManager for the container
     */
    public ServiceManager getServiceManager()
    {
        return this.serviceManager;
    }

    /**
     * @return the Parameters for the container
     */
    public Parameters getParameters()
    {
        return this.parameters;
    }

    /**
     * @return the Context for the container
     */
    public Context getContext()
    {
        return this.context;
    }

    /**
     * @see org.apache.fulcrum.servicemanager.ServiceManagerService#getLogger()
     */
    public Logger getLogger()
    {
        return this.logger;
    }

    /**
     * @see org.apache.fulcrum.servicemanager.ServiceManagerService#getConfiguration()
     */    
    public Configuration getConfiguration()
    {
        return this.configuration;
    }

    /////////////////////////////////////////////////////////////////////////
    // Service Implementation
    /////////////////////////////////////////////////////////////////////////

    /**
     * @return the one and only instance of this class
     */
    public static synchronized ServiceManagerService getInstance()
    {
        return instance;
    }

    /**
     * Create the one and only instance
     * @param instance the instance
     */
    protected static synchronized void setInstance( ServiceManagerServiceImpl instance )
    {
        ServiceManagerServiceImpl.instance = instance;
    }
}
