package org.apache.fulcrum.yaafi.framework.component;

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

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.fulcrum.yaafi.framework.role.RoleEntry;
import org.apache.fulcrum.yaafi.framework.util.ToStringBuilder;
import org.apache.fulcrum.yaafi.framework.util.Validate;

/**
 * This class implements am abstract base service component singleton with
 * an arbitrary lifecycle.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public abstract class ServiceComponentImpl
    implements ServiceComponent
{
    /** the information from the role configuration file */
    private RoleEntry roleEntry;

    /** the actual implementation class of the service component */
    private Class<?> implementationClazz;

    /** the instance of the implementation class of the service component */
    private Object instance;

    /** the proxy of the instance if any */
    private Object proxy;

    /** the Avalon logger of the container */
    private Logger parentLogger;

    /** the Avalon logger to be passed to the service component instance */
    private Logger logger;

    /** The Avalon ServiceManager passed to the service component instance */
    private ServiceManager serviceManager;

    /** The Avalon Context passed to the service component instance */
    private Context context;

    /** The Avalon Configuration passed to the service component instance */
    private Configuration configuration;

    /** The Avalon Parameters passed to the service component instance */
    private Parameters parameters;

    /**
     * Constructor to parse the configuration.
     *
     * @param roleEntry The information extracted from the role configuration file
     * @param parentLogger the logger of the service container
     * @param logger The logger for the service instance
     */
    public ServiceComponentImpl(
        RoleEntry roleEntry, Logger parentLogger, Logger logger)
    {
        Validate.notNull( roleEntry, "roleEntry" );
        Validate.notNull( parentLogger, "parentLogger" );
        Validate.notNull( logger, "logger" );

        this.roleEntry = roleEntry;
        this.parentLogger = parentLogger;
        this.logger = logger;
    }

    /////////////////////////////////////////////////////////////////////////
    // Service Component Lifecycle Implementation
    /////////////////////////////////////////////////////////////////////////

    /* (non-Javadoc)
     * @see org.apache.fulcrum.yaafi.framework.component.ServiceComponentLifecycle#loadImplemtationClass(java.lang.ClassLoader)
     */
    public void loadImplemtationClass(ClassLoader classLoader)
    	throws ClassNotFoundException
    {
        ClassLoader currClassLoader = null;

        if( classLoader != null )
        {
            currClassLoader = classLoader;
        }
        else
        {
            currClassLoader = this.getClass().getClassLoader();
        }

        try
        {
            this.implementationClazz = currClassLoader.loadClass(
                this.getRoleEntry().getImplementationClazzName()
                );
        }

        catch(ClassNotFoundException e)
        {
            String msg = "Failed to load the implementation class "
                + this.getRoleEntry().getImplementationClazzName();

            this.getParentLogger().error(msg,e);

            throw e;
        }
    }

    /* (non-Javadoc)
     * @see org.apache.fulcrum.yaafi.framework.component.ServiceComponentLifecycle#getInstance()
     */
    public Object getInstance()
        throws Exception
    {
        if( this.isInstantiated() == false )
        {
            this.createInstance();
            this.incarnateInstance();
        }

        return this.getRawInstance(true);
    }

    /* (non-Javadoc)
     * @see org.apache.fulcrum.yaafi.framework.component.ServiceComponentLifecycle#incarnate()
     */
    public void incarnate() throws Exception
    {
        try
        {
            if( this.isEarlyInit() )
            {
                this.getInstance();
            }
        }
        catch(Throwable t)
        {
            String msg = "Failed initialize "
                + this.getRoleEntry().getImplementationClazzName();

            throw new ConfigurationException(msg,t);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.fulcrum.yaafi.framework.component.ServiceComponentLifecycle#reconfigure()
     */
    public abstract void reconfigure() throws Exception;

    /* (non-Javadoc)
     * @see org.apache.fulcrum.yaafi.framework.component.ServiceComponentLifecycle#decommision()
     */
    public void decommision() throws Exception
    {
        this.instance = null;
        this.proxy = null;
    }

    /* (non-Javadoc)
     * @see org.apache.fulcrum.yaafi.framework.component.ServiceComponentLifecycle#dispose()
     */
    public void dispose()
    {
        this.roleEntry = null;
        this.implementationClazz = null;
        this.instance = null;
        this.proxy = null;
        this.parentLogger = null;
        this.logger = null;
        this.serviceManager = null;
        this.context = null;
        this.configuration = null;
        this.parameters = null;
    }

    /* (non-Javadoc)
     * @see org.apache.fulcrum.yaafi.framework.component.ServiceComponentLifecycle#setLogger(org.apache.avalon.framework.logger.Logger)
     */
    public void setLogger(Logger logger)
    {
        this.logger = logger;
    }

    /* (non-Javadoc)
     * @see org.apache.fulcrum.yaafi.framework.component.ServiceComponentLifecycle#setContext(org.apache.avalon.framework.context.Context)
     */
    public void setContext(Context context)
    {
        this.context = context;
    }

    /* (non-Javadoc)
     * @see org.apache.fulcrum.yaafi.framework.component.ServiceComponentLifecycle#setServiceManager(org.apache.avalon.framework.service.ServiceManager)
     */
    public void setServiceManager(ServiceManager serviceManager)
    {
        this.serviceManager = serviceManager;
    }

    /* (non-Javadoc)
     * @see org.apache.fulcrum.yaafi.framework.component.ServiceComponentLifecycle#setConfiguration(org.apache.avalon.framework.configuration.Configuration)
     */
    public void setConfiguration(Configuration configuration)
    {
        this.configuration = configuration;
    }

    /* (non-Javadoc)
     * @see org.apache.fulcrum.yaafi.framework.component.ServiceComponentLifecycle#setParameters(org.apache.avalon.framework.parameters.Parameters)
     */
    public void setParameters(Parameters parameters)
    {
        this.parameters = parameters;
    }

    /////////////////////////////////////////////////////////////////////////
    // Generated getters and setters
    /////////////////////////////////////////////////////////////////////////

    /**
     * @return Return true if the service is created on startup
     */
    public boolean isEarlyInit()
    {
        return this.getRoleEntry().isEarlyInit();
    }

    /* (non-Javadoc)
     * @see org.apache.fulcrum.yaafi.framework.component.ServiceComponent#getName()
     */
    public String getName()
    {
        return this.getRoleEntry().getName();
    }

    /* (non-Javadoc)
     * @see org.apache.fulcrum.yaafi.framework.component.ServiceComponent#getRoleEntry()
     */
    public RoleEntry getRoleEntry()
    {
        return roleEntry;
    }

    /**
     * @return Returns the logger.
     */
    public Logger getLogger()
    {
        return this.logger;
    }

    /**
     * @return Returns the parentLogger.
     */
    public Logger getParentLogger()
    {
        return parentLogger;
    }

    /**
     * @return Returns the implementationClazz.
     */
    public Class<?> getImplementationClazz()
    {
        return this.implementationClazz;
    }

    /**
     * @return Returns the configuration.
     */
    public Configuration getConfiguration()
    {
        return configuration;
    }

    /**
     * @return Returns the context.
     */
    public Context getContext()
    {
        return context;
    }

    /**
     * @return Returns the parameters.
     */
    public Parameters getParamaters()
    {
        return parameters;
    }

    /**
     * @return Returns the serviceManager.
     */
    public ServiceManager getServiceManager()
    {
        return serviceManager;
    }

    /**
     * @return the shorthand of the service
     */
    public String getShorthand()
    {
        return roleEntry.getShorthand();
    }

    /////////////////////////////////////////////////////////////////////////
    // Class implementation
    /////////////////////////////////////////////////////////////////////////

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        ToStringBuilder toStringBuilder = new ToStringBuilder(this);
        toStringBuilder.append("roleEntry",this.roleEntry);
        toStringBuilder.append("instance",this.instance);
        toStringBuilder.append("proxy",this.proxy);
        return toStringBuilder.toString();
    }

    /**
     * @return Returns <b>true</b> if the service instance was already instantiated.
     */
    protected final boolean isInstantiated()
    {
        return ( this.instance != null ? true : false );
    }

    /**
     * Create an instance of the service component implementation class
     * 
     * @return instance of the service component class
     * @throws InstantiationException if unable to instantiate
     * @throws IllegalAccessException if unable to access
     */
    protected Object createInstance()
        throws InstantiationException, IllegalAccessException
    {
        if( this.getParentLogger().isDebugEnabled() )
        {
            this.getParentLogger().debug( "Instantiating the implementation class for " + this.getShorthand() );
        }

        this.instance = this.implementationClazz.newInstance();
        this.proxy = null;
        return this.instance;
    }

    /**
     * @see org.apache.fulcrum.yaafi.framework.component.ServiceComponent#incarnate()
     * @throws Exception generic exception
     */
    protected abstract void incarnateInstance() throws Exception;

    /**
     * Get either the original service object or the dynamic proxy
     *
     * @param useProxy set to true if using a proxy
     * @return Returns the raw instance, i.e. does not incarnate
     * the instance.
     */
    protected Object getRawInstance(boolean useProxy)
    {
        if( useProxy && (this.proxy != null) )
        {
            return this.proxy;
        }
        else
        {
            return this.instance;
        }
    }

    /**
     * @param proxy the service proxy instance
     */
    protected void setProxyInstance(Object proxy)
    {
        this.proxy = proxy;
    }
}
