package org.apache.fulcrum.yaafi.framework.container;

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

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Executable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.activity.Suspendable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.Reconfigurable;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;

/**
 * Holder of the metadata of a service component.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public class ServiceComponentImpl
    implements ServiceComponent
{
    /** The name of the service */
    private String name;

    /** The name of the implementation class of the service */
    private String clazzName;
    
    /** The actual implementation class of the service */
    private Class clazz;

    /** The instance of the implementation class of the service */
    private Object instance;

    /** The short name of this service */
    private String shorthand;
    
    /** The logger to be used  */
    private Logger logger;
     
    /** Do we incarnate this instance during start-up */
    private boolean isEarlyInit;
    
    /** A description for the service if any */
    private String description;
    
    /** The type of component´, e.g. "merlin", "phoenix" or "fortress*/
    private String componentType;
    
    /**
     * Constructor
     * @param configuration The configuration to obtain the meta informations
     * @param logger The logger of the service container
     */
    public ServiceComponentImpl( Configuration configuration, Logger logger )
    	throws ConfigurationException
    {
        this.notNull( configuration, "configuration" );
        this.notNull( logger, "logger" );
        
        if( configuration.getName().equals("role") )
        {
			this.clazzName		= configuration.getAttribute("default-class");
	        this.name			= configuration.getAttribute("name",this.clazzName);
	        this.shorthand		= configuration.getAttribute("shorthand",this.name);
	        this.logger			= logger;
	        this.isEarlyInit 	= configuration.getAttributeAsBoolean("early-init",true);
	        this.description	= configuration.getAttribute("description",null);
	        this.componentType	= configuration.getAttribute("component-type","merlin");
        }
        else
        {
			this.clazzName		= configuration.getAttribute("class");
	        this.name			= configuration.getAttribute("name",this.clazzName);
	        this.shorthand		= configuration.getAttribute("shorthand",this.name);
	        this.logger			= logger;
	        this.isEarlyInit 	= configuration.getAttributeAsBoolean("early-init",true);
	        this.description	= configuration.getAttribute("description",null);
	        this.componentType	= configuration.getAttribute("component-type","merlin");
        }
    }
	
    /////////////////////////////////////////////////////////////////////////
    // Service Lifecycle Implementation
    /////////////////////////////////////////////////////////////////////////

    /**
     * Create an instance of the service class
     *
     * @throws ClassNotFoundException
     */
    public Class loadClass()
    	throws ClassNotFoundException
    {
        this.getLogger().debug( "Loading the implementation class for " + this.getShorthand() );
        this.clazz = this.getClass().getClassLoader().loadClass(this.clazzName);
        return this.clazz;
    }
    
    /**
     * Create an instance of the service class
     *
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public Object create()
        throws InstantiationException, IllegalAccessException
    {
        this.getLogger().debug( "Instantiating the implementation class for " + this.getShorthand() );
        this.instance =  this.clazz.newInstance();
        return this.instance;
    }

    /**
     * @see org.apache.avalon.framework.logger.LogEnabled#enableLogging(org.apache.avalon.framework.logger.Logger)
     */
    public void enableLogging(Logger logger)
    {
		if( this.instance instanceof LogEnabled )
		{
			try
			{
				this.getLogger().debug( "LogEnabled.enableLogging() for " + this.getShorthand() );
				Logger avalonLogger = logger.getChildLogger( this.getShorthand() ); 
				((LogEnabled )this.getInstance()).enableLogging(avalonLogger);
			}
			catch (Throwable t)
			{
				String msg = "LogEnable the following service failed : " + this.getName();
				this.getLogger().error(msg,t);
				throw new RuntimeException(msg);
			}		    
		}
    }

    /**
     * @see org.apache.avalon.framework.context.Contextualizable#contextualize(org.apache.avalon.framework.context.Context)
     */
    public void contextualize(Context context) throws ContextException
    {
        this.notNull( context, "context" );
        
		if( this.instance instanceof Contextualizable )
		{
			try
			{
				this.getLogger().debug( "Contextualizable.contextualize() for " + this.getShorthand() );
				((Contextualizable )this.getInstance()).contextualize(context);
			}
			catch (ContextException e)
			{
				String msg = "Contextualizing the following service failed : " + this.getShorthand();
				this.getLogger().error(msg,e);
				throw e;
			}
			catch (Throwable t)
			{
				String msg = "Contextualizing the following service failed : " + this.getShorthand();
				this.getLogger().error(msg,t);
				throw new ContextException(msg,t);
			}
		 }
    }

	/**
	 * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceContainer)
	 */
	public void service(ServiceManager serviceManager) throws ServiceException
	{
	    this.notNull( serviceManager, "serviceManager" );
	    
		if( this.instance instanceof Serviceable )
		{
			try
			{
				this.getLogger().debug( "Serviceable.service() for " + this.getShorthand() );
				((Serviceable )this.getInstance()).service(serviceManager);
			}
			catch (ServiceException e)
			{
				String msg = "Servicing the following service failed : " + this.getShorthand();
				this.getLogger().error(msg,e);
				throw e;
			}
			catch (Throwable t)
			{
				String msg = "Servicing the following service failed : " + this.getShorthand();
				this.getLogger().error(msg,t);
				throw new ServiceException(this.getShorthand(),msg,t);
			}
		}
	}

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration configuration) throws ConfigurationException
    {
        this.notNull( configuration, "configuration" );
        
        if( this.instance instanceof Configurable )
        {
            try
            {
                this.getLogger().debug( "Configurable.configure() for " + this.getShorthand() );
            	Configuration componentConfiguraton = configuration.getChild(this.getShorthand());
            	((Configurable )this.getInstance()).configure(componentConfiguraton);
            }
            catch (ConfigurationException e)
            {
                String msg = "Configuring the following service failed : " + this.getShorthand();
                this.getLogger().error(msg,e);
                throw e;
            }
            catch (Throwable t)
            {
                String msg = "Configuring the following service failed : " + this.getShorthand();
                this.getLogger().error(msg,t);
                throw new ConfigurationException(msg,t);
            }
        }
    }

    /**
     * @see org.apache.avalon.framework.parameters.Parameterizable#parameterize(org.apache.avalon.framework.parameters.CryptoParameters)
     */
    public void parameterize(Parameters parameters) throws ParameterException
    {
        this.notNull( parameters, "parameters" );
        
		if( this.instance instanceof Parameterizable )
		{
			try
			{
				this.getLogger().debug( "Parameterizable.parametrize() for " + this.getShorthand() );
				((Parameterizable )this.getInstance()).parameterize(parameters);
			}
			catch (ParameterException e)
			{
				String msg = "Parameterizing the following service failed : " + this.getShorthand();
				this.getLogger().error(msg,e);
				throw e;
			}
			catch (Throwable t)
			{
				String msg = "Parameterizing the following service failed : " + this.getShorthand();
				this.getLogger().error(msg,t);
				throw new ParameterException(msg,t);
			}
		}
    }

    /**
     * @see org.apache.avalon.framework.activity.Initializable#initialize()
     */
    public void initialize() throws Exception
    {
        if( this.instance instanceof Initializable )
        {
            try
            {
                this.getLogger().debug( "Initializable.initialize() for " + this.getShorthand() );
                ((Initializable )this.getInstance()).initialize();
            }
            catch (Exception e)
            {
                String msg = "Initializing the following service failed : " + this.getShorthand();
                this.getLogger().error(msg,e);
                throw e;
            }
            catch (Throwable t)
            {
                String msg = "Initializing the following service failed : " + this.getShorthand();
                this.getLogger().error(msg,t);
                throw new ConfigurationException(msg,t);
            }
        }
    }

    /**
     * @see org.apache.avalon.framework.activity.Executable#execute()
     */
    public void execute() throws Exception
    {
        if( this.instance instanceof Executable )
        {
            try
            {
                this.getLogger().debug( "Executable.execute() for " + this.getShorthand() );
                ((Executable )this.getInstance()).execute();
            }
            catch (Exception e)
            {
                String msg = "Executing the following service failed : " + this.getShorthand();
                this.getLogger().error(msg,e);
                throw e;
            }
            catch (Throwable t)
            {
                String msg = "Executing the following service failed : " + this.getShorthand();
                this.getLogger().error(msg,t);
                throw new ConfigurationException(msg,t);
            }            
        }        
    }

    /**
     * @see org.apache.avalon.framework.activity.Startable#start()
     */
    public void start() throws Exception
    {
        if( this.instance instanceof Startable )
        {
            try
            {
                this.getLogger().debug( "Startable.start() for " + this.getShorthand() );
                ((Startable )this.getInstance()).start();
            }
            catch (Exception e)
            {
                String msg = "Starting the following service failed : " + this.getShorthand();
                this.getLogger().error(msg,e);
                throw e;
            }
            catch (Throwable t)
            {
                String msg = "Starting the following service failed : " + this.getShorthand();
                this.getLogger().error(msg,t);
                throw new RuntimeException(msg);
            }
        }        
    }

    /**
     * @see org.apache.avalon.framework.activity.Startable#stop()
     */
    public void stop() throws Exception
    {
        if( this.instance instanceof Startable )
        {
            try
            {
                this.getLogger().debug( "Startable.stop() for " + this.getShorthand() );
                ((Startable )this.getInstance()).stop();
            }
            catch (Exception e)
            {
                String msg = "Stopping the following service failed : " + this.getShorthand();
                this.getLogger().error(msg,e);
                throw e;
            }
        }
    }

    /**
     * @see org.apache.avalon.framework.activity.Suspendable#resume()
     */
    public void resume()
    {
        if( this.instance instanceof Suspendable )
        {
            try
            {
                this.getLogger().debug( "Suspendable.resume() for " + this.getShorthand() );
                ((Suspendable )this.getInstance()).resume();
            }
            catch (Exception e)
            {
                String msg = "Resuming the following service failed : " + this.getShorthand();
                this.getLogger().error(msg,e);
            }            
        }
    }

    /**
     * @see org.apache.avalon.framework.activity.Suspendable#suspend()
     */
    public void suspend()
    {
        if( this.instance instanceof Suspendable )
        {
            try
            {
                this.getLogger().debug( "Suspendable.suspend() for " + this.getShorthand() );
                ((Suspendable )this.getInstance()).suspend();
            }
            catch (Exception e)
            {
                String msg = "Suspending the following service failed : " + this.getShorthand();
                this.getLogger().error(msg,e);
            }
        }
    }

    /**
     * @see org.apache.avalon.framework.configuration.Reconfigurable#reconfigure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void reconfigure(Configuration configuration) throws ConfigurationException
    {
        this.notNull( configuration, "configuration" );
        
        if( this.instance instanceof Reconfigurable )
        {
            try
            {
                this.getLogger().debug( "Reconfigurable.reconfigure() for " + this.getShorthand() );
    			String shorthand = this.getShorthand();
    			Configuration componentConfiguraton = configuration.getChild(shorthand);
                ((Reconfigurable )this.getInstance()).reconfigure(componentConfiguraton);
            }
            catch (Exception e)
            {
                String msg = "Reconfiguring the following service failed : " + this.getShorthand();
                this.getLogger().error(msg,e);
            }            
        }
    }

    /**
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     */
    public void dispose()
    {
        if( this.instance instanceof Disposable )
        {
            try
            {
                this.getLogger().debug( "Disposable.dispose() for " + this.getShorthand() );
                ((Disposable )this.getInstance()).dispose();
                this.instance = null;
            }
            catch (Exception e)
            {
                String msg = "Disposing the following service failed : " + this.getShorthand();
                this.getLogger().error(msg,e);
            }
        }
    }
    
    
    /**
     * @return Returns the if the service instance was already instantiated.
     */
    public boolean isInstantiated()
    {
        return ( this.instance != null ? true : false );
    }

    /**
     * @return Return true if the service is created on startup
     */
    public boolean isEarlyInit()
    {
        return this.isEarlyInit;
    }
    
    /**
     * @return Returns the instance. If it is not instantiated yet then
     * do it
     */
    public Object getInstance()
    	throws InstantiationException, IllegalAccessException
    {
        if( this.isInstantiated() )
        {
            return this.instance;
        }
        else
        {
            return this.create();
        }
    }
        
    /////////////////////////////////////////////////////////////////////////
    // Generated getters and setters
    /////////////////////////////////////////////////////////////////////////
    
    /**
     * @return Returns the clazz.
     */
    public Class getClazz()
    {
        return this.clazz;
    }
    /**
     * @param clazz The clazz to set.
     */
    public void setClazz(Class clazz)
    {
        this.clazz = clazz;
    }
    /**
     * @return Returns the clazzName.
     */
    public String getClazzName()
    {
        return this.clazzName;
    }
    /**
     * @param clazzName The clazzName to set.
     */
    public void setClazzName(String clazzName)
    {
        this.clazzName = clazzName;
    }
    /**
     * @param instance The instance to set.
     */
    public void setInstance(Object instance)
    {
        this.instance = instance;
    }
    /**
     * @return Returns the logger.
     */
    public Logger getLogger()
    {
        return this.logger;
    }
    /**
     * @param logger The logger to set.
     */
    public void setLogger(Logger logger)
    {
        this.logger = logger;
    }
    /**
     * @return Returns the name.
     */
    public String getName()
    {
        return this.name;
    }
    /**
     * @param name The name to set.
     */
    public void setName(String name)
    {
        this.name = name;
    }
    /**
     * @return Returns the shorthand.
     */
    public String getShorthand()
    {
        return this.shorthand;
    }
    /**
     * @param shorthand The shorthand to set.
     */
    public void setShorthand(String shorthand)
    {
        this.shorthand = shorthand;
    }
    
    /**
     * @return Returns the description if any.
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * @param description The description to set.
     */
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    
    /**
     * @return Returns the componentType.
     */
    public String getComponentType()
    {
        return componentType;
    }
    
    /**
     * @param componentType The componentType to set.
     */
    public void setComponentType(String componentType)
    {
        this.componentType = componentType;
    }
    
    /////////////////////////////////////////////////////////////////////////
    // MISC
    /////////////////////////////////////////////////////////////////////////
    
    private void notNull( Object object, String name )
    {
        if( object == null )
        {
            throw new NullPointerException( name );
        }
    }
}
