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
import org.apache.fulcrum.yaafi.framework.role.RoleEntry;
import org.apache.fulcrum.yaafi.framework.util.Validate;

/**
 * This class implements a service component singleton with
 * an arbitray lifecycle.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public class AvalonServiceComponentImpl
    extends ServiceComponentImpl
{       
    /**
     * Constructor to parse the configuration.
     * 
     * @param roleEntry The information extracted from the role configuration file
     * @param logger The logger of the service container
     */
    public AvalonServiceComponentImpl( RoleEntry roleEntry, Logger logger )
    {
        super( roleEntry, logger );
    }
	
    /////////////////////////////////////////////////////////////////////////
    // Service Component Lifecycle Implementation
    /////////////////////////////////////////////////////////////////////////
    
    /**
     * @see org.apache.fulcrum.yaafi.framework.component.ServiceComponent#incarnate()
     */
    protected void incarnateInstance() throws Exception
    {
        this.getLogger().debug( "Incarnating the service " + this.getShorthand() );
        
        if( this.getLogger() != null )
        {
            this.enableLogging( this.getLogger() );
        }
        
        if( this.getContext() != null )
        {
            this.contextualize( this.getContext() );
        }
        
        if( this.getServiceManager() != null )
        {
            this.service( this.getServiceManager() );    
        }
        
        if( this.getConfiguration() != null )
        {
            this.configure( this.getConfiguration() );
        }
        
        if( this.getParamaters() != null )
        {
            this.parameterize( this.getParamaters() );
        }
        
        this.initialize();
        this.execute();
        this.start();
    }

    /**
     * @see org.apache.fulcrum.yaafi.framework.component.ServiceComponent#reconfigure()
     */
    public void reconfigure() throws Exception
    {
        Throwable lastThrowable = null;
        
        this.getLogger().debug( "Reconfiguring " + this.getShorthand() );

        try
        {
            this.suspend();
        }
        catch (Throwable t)
        {
            String msg = "Suspending the following service failed : " + this.getShorthand();
            this.getLogger().error( msg, t );
            lastThrowable = t;
        }

        try
        {
            if( this.getConfiguration() != null )
            {
                this.reconfigure( this.getConfiguration() );
            }
        }
        catch (Throwable t)
        {
            String msg = "Reconfiguring the following service failed : " + this.getShorthand();
            this.getLogger().error( msg, t );
            lastThrowable = t;
        }
        
        try
        {
            this.resume();
        }
        catch (Throwable t)
        {
            String msg = "Resumimg the following service failed : " + this.getShorthand();
            this.getLogger().error( msg, t );
            lastThrowable = t;
        }

        if( lastThrowable != null )
        {
            if( lastThrowable instanceof Exception )
            {
                throw (Exception) lastThrowable;
            }
            else
            {
                throw new RuntimeException( lastThrowable.getMessage() );
            }
        }
    }

    /** 
     * @see org.apache.fulcrum.yaafi.framework.component.ServiceComponent#decommision()
     */
    public void decommision() throws Exception
    {
        this.getLogger().debug( "Decommisioning the service " + this.getShorthand() );
     
        try
        {
            this.stop();
        }
        catch (Throwable e)
        {
            String msg = "Stopping the following service failed : " + this.getShorthand();
            this.getLogger().error( msg, e );
        }

        try
        {
            this.dispose();
        }
        catch (Throwable e)
        {
            String msg = "Disposing the following service failed : " + this.getShorthand();
            this.getLogger().error( msg, e );
        }
        
        super.decommision();
    }

    /////////////////////////////////////////////////////////////////////////
    // Avalon Lifecycle Implementation
    /////////////////////////////////////////////////////////////////////////

    /**
     * @see org.apache.avalon.framework.logger.LogEnabled#enableLogging(org.apache.avalon.framework.logger.Logger)
     */
    public void enableLogging(Logger logger)
    {
		if( this.getRawInstance() instanceof LogEnabled )
		{
			try
			{
				this.getLogger().debug( "LogEnabled.enableLogging() for " + this.getShorthand() );
				((LogEnabled )this.getInstance()).enableLogging(logger);
			}
			catch (Throwable t)
			{
				String msg = "LogEnable the following service failed : " + this.getShorthand();
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
		if( this.getRawInstance() instanceof Contextualizable )
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
	 * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
	 */
	public void service(ServiceManager serviceManager) throws ServiceException
	{
		if( this.getRawInstance() instanceof Serviceable )
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
        if( this.getRawInstance() instanceof Configurable )
        {
            try
            {
                this.getLogger().debug( "Configurable.configure() for " + this.getShorthand() );
            	((Configurable )this.getInstance()).configure(configuration);
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
     * @see org.apache.avalon.framework.parameters.Parameterizable#parameterize(org.apache.avalon.framework.parameters.Parameters)
     */
    public void parameterize(Parameters parameters) throws ParameterException
    {
		if( this.getRawInstance() instanceof Parameterizable )
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
        if( this.getRawInstance() instanceof Initializable )
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
                throw new RuntimeException(msg);
            }
        }
    }

    /**
     * @see org.apache.avalon.framework.activity.Executable#execute()
     */
    public void execute() throws Exception
    {
        if( this.getRawInstance() instanceof Executable )
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
                throw new RuntimeException(msg);
            }            
        }        
    }

    /**
     * @see org.apache.avalon.framework.activity.Startable#start()
     */
    public void start() throws Exception
    {
        if( this.getRawInstance() instanceof Startable )
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
        if( this.getRawInstance() instanceof Startable )
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
            catch (Throwable t)
            {
                String msg = "Stopping the following service failed : " + this.getShorthand();
                this.getLogger().error(msg,t);
                throw new RuntimeException(msg);
            }            
        }
    }

    /**
     * @see org.apache.avalon.framework.activity.Suspendable#resume()
     */
    public void resume()
    {
        if( this.getRawInstance() instanceof Suspendable )
        {
            try
            {
                this.getLogger().debug( "Suspendable.resume() for " + this.getShorthand() );
                ((Suspendable )this.getInstance()).resume();
            }
            catch (Throwable t)
            {
                String msg = "Resuming the following service failed : " + this.getShorthand();
                this.getLogger().error(msg,t);
                throw new RuntimeException(msg);
            }            
        }
    }

    /**
     * @see org.apache.avalon.framework.activity.Suspendable#suspend()
     */
    public void suspend()
    {
        if( this.getRawInstance() instanceof Suspendable )
        {
            try
            {
                this.getLogger().debug( "Suspendable.suspend() for " + this.getShorthand() );
                ((Suspendable )this.getInstance()).suspend();
            }
            catch (Throwable t)
            {
                String msg = "Suspending the following service failed : " + this.getShorthand();
                this.getLogger().error(msg,t);
                throw new RuntimeException(msg);
            }
        }
    }

    /**
     * @see org.apache.avalon.framework.configuration.Reconfigurable#reconfigure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void reconfigure(Configuration configuration) throws ConfigurationException
    {
        Validate.notNull( configuration, "configuration" );
        
        if( this.getRawInstance() instanceof Reconfigurable )
        {
            try
            {
                this.getLogger().debug( "Reconfigurable.reconfigure() for " + this.getShorthand() );
                ((Reconfigurable )this.getInstance()).reconfigure(configuration);
            }
            catch (Throwable t)
            {
                String msg = "Reconfiguring the following service failed : " + this.getShorthand();
                this.getLogger().error(msg,t);
                throw new RuntimeException(msg);
            }            
        }
    }

    /**
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     */
    public void dispose()
    {
        if( this.getRawInstance() instanceof Disposable )
        {
            try
            {
                this.getLogger().debug( "Disposable.dispose() for " + this.getShorthand() );
                ((Disposable )this.getInstance()).dispose();
            }
            catch (Exception e)
            {
                String msg = "Disposing the following service failed : " + this.getShorthand();
                this.getLogger().error(msg,e);
                throw new RuntimeException(msg);
            }
        }
    }
}
