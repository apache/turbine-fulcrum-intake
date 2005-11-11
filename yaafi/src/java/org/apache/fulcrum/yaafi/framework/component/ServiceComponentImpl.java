package org.apache.fulcrum.yaafi.framework.component;

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

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.fulcrum.yaafi.framework.role.RoleEntry;
import org.apache.fulcrum.yaafi.framework.util.ReadWriteLock;
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
    private Class implementationClazz;

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

    /** read/write lock to snychronize access to services */
    private ReadWriteLock readWriteLock;
    
    /**
     * Constructor to parse the configuration.
     *
     * @param roleEntry The information extracted from the role configuration file
     * @param parentLogger the logger of the service container
     * @param logger The logger for the service instance
     * @param readWriteLock the read/write lock to synchronize access to services
     */
    public ServiceComponentImpl( 
        RoleEntry roleEntry, Logger parentLogger, Logger logger, ReadWriteLock readWriteLock )
    {
        Validate.notNull( roleEntry, "roleEntry" );
        Validate.notNull( parentLogger, "parentLogger" );
        Validate.notNull( logger, "logger" );
        Validate.notNull( readWriteLock, "readWriteLock" );

        this.roleEntry = roleEntry;
        this.parentLogger = parentLogger;
        this.logger = logger;
        this.readWriteLock = readWriteLock;
    }

    /////////////////////////////////////////////////////////////////////////
    // Service Component Lifecycle Implementation
    /////////////////////////////////////////////////////////////////////////

    /**
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
    
    /**
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

    /**
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

    /**
     * @see org.apache.fulcrum.yaafi.framework.component.ServiceComponentLifecycle#reconfigure()
     */
    public abstract void reconfigure() throws Exception;

    /**
     * @see org.apache.fulcrum.yaafi.framework.component.ServiceComponentLifecycle#decommision()
     */
    public void decommision() throws Exception
    {
        this.instance = null;
        this.proxy = null;
    }

    /**
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
        this.readWriteLock = null;
    }
    
    /**
     * @param logger The logger to set.
     */
    public void setLogger(Logger logger)
    {
        this.logger = logger;
    }

    /**
     * @param context The context to set.
     */
    public void setContext(Context context)
    {
        this.context = context;
    }

    /**
     * @param serviceManager The serviceManager to set.
     */
    public void setServiceManager(ServiceManager serviceManager)
    {
        this.serviceManager = serviceManager;
    }

    /**
     * @param configuration The configuration to set.
     */
    public void setConfiguration(Configuration configuration)
    {
        this.configuration = configuration;
    }

    /**
     * @param parameters The parameters to set.
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

    /**
     * @see org.apache.fulcrum.yaafi.framework.component.ServiceComponent#getName()
     */
    public String getName()
    {
        return this.getRoleEntry().getName();
    }

    /**
     * @return Returns the roleEntry.
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
    public Class getImplementationClazz()
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
     * @return Returns the paramaters.
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

    /**
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
     * @throws InstantiationException th
     * @throws IllegalAccessException
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
     */
    protected abstract void incarnateInstance() throws Exception;

    /**
     * Get either the original service object or the dynamic proxy
     *
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
    
    /**
     * @return Returns the readWriteLock.
     */
    protected final ReadWriteLock getReadWriteLock()
    {
        return readWriteLock;
    }
}
