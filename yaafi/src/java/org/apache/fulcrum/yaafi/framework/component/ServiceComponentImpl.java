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
     * @param logger The logger of the service container
     */
    public ServiceComponentImpl( RoleEntry roleEntry, Logger logger )
    {
        Validate.notNull( roleEntry, "roleEntry" );
        Validate.notNull( logger, "logger" );
        
        this.roleEntry = roleEntry;
        this.logger = logger;
    }
	
    /////////////////////////////////////////////////////////////////////////
    // Service Component Lifecycle Implementation
    /////////////////////////////////////////////////////////////////////////

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
        
        return this.instance;
    }

    /**
     * @see org.apache.fulcrum.yaafi.framework.component.ServiceComponentLifecycle#incarnate()
     */
    public void incarnate() throws Exception
    {
        try
        {
            this.implementationClazz = this.getClass().getClassLoader().loadClass(
                this.getRoleEntry().getImplementationClazzName()
              	);
            
            if( this.getRoleEntry().isEarlyInit() )
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
     * @return Returns <b>true</b> if the service instance was already instantiated.
     */
    protected boolean isInstantiated()
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
        this.getLogger().debug( "Instantiating the implementation class for " + this.getShorthand() );
        this.instance =  this.implementationClazz.newInstance();
        return this.instance;
    }

    /**
     * @see org.apache.fulcrum.yaafi.framework.component.ServiceComponent#incarnate()
     */
    protected abstract void incarnateInstance() throws Exception;

    /**
     * @return Returns the raw instance, i.e. does not incarnate
     * the instance.
     */
    protected Object getRawInstance()
    {
        return this.instance;
    }
}
