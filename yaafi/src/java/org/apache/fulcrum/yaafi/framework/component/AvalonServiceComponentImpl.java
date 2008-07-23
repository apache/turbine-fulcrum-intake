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
import org.apache.fulcrum.yaafi.framework.interceptor.AvalonInterceptorFactory;
import org.apache.fulcrum.yaafi.framework.interceptor.AvalonInterceptorService;
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
     * @param parentLogger the logger of the service container
     * @param logger The logger for the service instance
     */
    public AvalonServiceComponentImpl(
        RoleEntry roleEntry, Logger parentLogger, Logger logger)
    {
        super( roleEntry, parentLogger, logger );
    }

    /////////////////////////////////////////////////////////////////////////
    // Service Component Lifecycle Implementation
    /////////////////////////////////////////////////////////////////////////

    /**
     * @see org.apache.fulcrum.yaafi.framework.component.ServiceComponent#incarnate()
     */
    protected void incarnateInstance() throws Exception
    {
        this.getParentLogger().debug( "Incarnating the service " + this.getShorthand() );

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

        // create a dynamic proxy only if
        //
        // +) interceptors are enabled
        // +) the instance is not an AvalonServiceInterceptor

        boolean isInterceptor = AvalonInterceptorService.class.isAssignableFrom(
            this.getImplementationClazz()
            );

        if( (this.getRoleEntry().hasDynamicProxy()) && (isInterceptor == false ) )
        {
            if( this.getParentLogger().isDebugEnabled() )
            {
                this.getParentLogger().debug( "Creating a dynamic proxy for " + this.getShorthand() );
            }

            Object proxyInstance = AvalonInterceptorFactory.create(
                this.getName(),
                this.getShorthand(),
                this.getServiceManager(),
                this.getRoleEntry().getInterceptorList(),
                this.getRawInstance(false)
                );

            this.setProxyInstance(proxyInstance);
        }
        else
        {
            this.getRoleEntry().setHasDynamicProxy(false);
        }
    }

    /**
     * @see org.apache.fulcrum.yaafi.framework.component.ServiceComponent#reconfigure()
     */
    public void reconfigure() throws Exception
    {
        Throwable lastThrowable = null;

        this.getParentLogger().debug( "Reconfiguring " + this.getShorthand() );

        try
        {
            this.suspend();
        }
        catch (Throwable t)
        {
            String msg = "Suspending the following service failed : " + this.getShorthand();
            this.getParentLogger().error( msg, t );
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
            this.getParentLogger().error( msg, t );
            lastThrowable = t;
        }

        try
        {
            this.resume();
        }
        catch (Throwable t)
        {
            String msg = "Resumimg the following service failed : " + this.getShorthand();
            this.getParentLogger().error( msg, t );
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
     * Stop and dispose the service implementation.
     *
     * @see org.apache.fulcrum.yaafi.framework.component.ServiceComponent#decommision()
     */
    public void decommision() throws Exception
    {
        this.getParentLogger().debug( "Decommisioning the service " + this.getShorthand() );

        try
        {
            this.stop();
        }
        catch (Throwable e)
        {
            String msg = "Stopping the following service failed : " + this.getShorthand();
            this.getParentLogger().error( msg, e );
        }

        try
        {
            Object rawInstance = this.getRawInstance(false);

            // dispose the service implementation class

            if( rawInstance instanceof Disposable )
            {
                try
                {
                    this.getParentLogger().debug( "Disposable.dispose() for " + this.getShorthand() );
                    ((Disposable) rawInstance).dispose();
                }
                catch (Exception e)
                {
                    String msg = "Disposing the following service failed : " + this.getShorthand();
                    this.getParentLogger().error(msg,e);
                    throw new RuntimeException(msg);
                }
            }
        }
        catch (Throwable e)
        {
            String msg = "Disposing the following service failed : " + this.getShorthand();
            this.getParentLogger().error( msg, e );
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
        Object rawInstance = this.getRawInstance(false);

        if( rawInstance instanceof LogEnabled )
        {
            try
            {
                this.getParentLogger().debug( "LogEnabled.enableLogging() for " + this.getShorthand() );
                ((LogEnabled) rawInstance).enableLogging(logger);
            }
            catch (Throwable t)
            {
                String msg = "LogEnable the following service failed : " + this.getShorthand();
                this.getParentLogger().error(msg,t);
                throw new RuntimeException(msg);
            }
        }
    }

    /**
     * @see org.apache.avalon.framework.context.Contextualizable#contextualize(org.apache.avalon.framework.context.Context)
     */
    public void contextualize(Context context) throws ContextException
    {
        Object rawInstance = this.getRawInstance(false);

        if( rawInstance instanceof Contextualizable )
        {
            try
            {
                this.getParentLogger().debug( "Contextualizable.contextualize() for " + this.getShorthand() );
                ((Contextualizable) rawInstance).contextualize(context);
            }
            catch (ContextException e)
            {
                String msg = "Contextualizing the following service failed : " + this.getShorthand();
                this.getParentLogger().error(msg,e);
                throw e;
            }
            catch (Throwable t)
            {
                String msg = "Contextualizing the following service failed : " + this.getShorthand();
                this.getParentLogger().error(msg,t);
                throw new ContextException(msg,t);
            }
        }
    }

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager serviceManager) throws ServiceException
    {
        Object rawInstance = this.getRawInstance(false);

        if( rawInstance instanceof Serviceable )
        {
            try
            {
                this.getParentLogger().debug( "Serviceable.service() for " + this.getShorthand() );
                ((Serviceable) rawInstance).service(serviceManager);
            }
            catch (ServiceException e)
            {
                String msg = "Servicing the following service failed : " + this.getShorthand();
                this.getParentLogger().error(msg,e);
                throw e;
            }
            catch (Throwable t)
            {
                String msg = "Servicing the following service failed : " + this.getShorthand();
                this.getParentLogger().error(msg,t);
                throw new ServiceException(this.getShorthand(),msg,t);
            }
        }
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration configuration) throws ConfigurationException
    {
        Object rawInstance = this.getRawInstance(false);

        if( rawInstance instanceof Configurable )
        {
            try
            {
                this.getParentLogger().debug( "Configurable.configure() for " + this.getShorthand() );
                ((Configurable) rawInstance).configure(configuration);
            }
            catch (ConfigurationException e)
            {
                String msg = "Configuring the following service failed : " + this.getShorthand();
                this.getParentLogger().error(msg,e);
                throw e;
            }
            catch (Throwable t)
            {
                String msg = "Configuring the following service failed : " + this.getShorthand();
                this.getParentLogger().error(msg,t);
                throw new ConfigurationException(msg,t);
            }
        }
    }

    /**
     * @see org.apache.avalon.framework.parameters.Parameterizable#parameterize(org.apache.avalon.framework.parameters.Parameters)
     */
    public void parameterize(Parameters parameters) throws ParameterException
    {
        Object rawInstance = this.getRawInstance(false);

        if( rawInstance instanceof Parameterizable )
        {
            try
            {
                this.getParentLogger().debug( "Parameterizable.parametrize() for " + this.getShorthand() );
                ((Parameterizable) rawInstance).parameterize(parameters);
            }
            catch (ParameterException e)
            {
                String msg = "Parameterizing the following service failed : " + this.getShorthand();
                this.getParentLogger().error(msg,e);
                throw e;
            }
            catch (Throwable t)
            {
                String msg = "Parameterizing the following service failed : " + this.getShorthand();
                this.getParentLogger().error(msg,t);
                throw new ParameterException(msg,t);
            }
        }
    }

    /**
     * @see org.apache.avalon.framework.activity.Initializable#initialize()
     */
    public void initialize() throws Exception
    {
        Object rawInstance = this.getRawInstance(false);

        if( rawInstance instanceof Initializable )
        {
            try
            {
                this.getParentLogger().debug( "Initializable.initialize() for " + this.getShorthand() );
                ((Initializable) rawInstance).initialize();
            }
            catch (Exception e)
            {
                String msg = "Initializing the following service failed : " + this.getShorthand();
                this.getParentLogger().error(msg,e);
                throw e;
            }
            catch (Throwable t)
            {
                String msg = "Initializing the following service failed : " + this.getShorthand();
                this.getParentLogger().error(msg,t);
                throw new RuntimeException(msg);
            }
        }
    }

    /**
     * @see org.apache.avalon.framework.activity.Executable#execute()
     */
    public void execute() throws Exception
    {
        Object rawInstance = this.getRawInstance(false);

        if( rawInstance instanceof Executable )
        {
            try
            {
                this.getParentLogger().debug( "Executable.execute() for " + this.getShorthand() );
                ((Executable) rawInstance).execute();
            }
            catch (Exception e)
            {
                String msg = "Executing the following service failed : " + this.getShorthand();
                this.getParentLogger().error(msg,e);
                throw e;
            }
            catch (Throwable t)
            {
                String msg = "Executing the following service failed : " + this.getShorthand();
                this.getParentLogger().error(msg,t);
                throw new RuntimeException(msg);
            }
        }
    }

    /**
     * @see org.apache.avalon.framework.activity.Startable#start()
     */
    public void start() throws Exception
    {
        Object rawInstance = this.getRawInstance(false);

        if( rawInstance instanceof Startable )
        {
            try
            {
                this.getParentLogger().debug( "Startable.start() for " + this.getShorthand() );
                ((Startable) rawInstance).start();
            }
            catch (Exception e)
            {
                String msg = "Starting the following service failed : " + this.getShorthand();
                this.getParentLogger().error(msg,e);
                throw e;
            }
            catch (Throwable t)
            {
                String msg = "Starting the following service failed : " + this.getShorthand();
                this.getParentLogger().error(msg,t);
                throw new RuntimeException(msg);
            }
        }
    }

    /**
     * @see org.apache.avalon.framework.activity.Startable#stop()
     */
    public void stop() throws Exception
    {
        Object rawInstance = this.getRawInstance(false);

        if( rawInstance instanceof Startable )
        {
            try
            {
                this.getParentLogger().debug( "Startable.stop() for " + this.getShorthand() );
                ((Startable) rawInstance).stop();
            }
            catch (Exception e)
            {
                String msg = "Stopping the following service failed : " + this.getShorthand();
                this.getParentLogger().error(msg,e);
                throw e;
            }
            catch (Throwable t)
            {
                String msg = "Stopping the following service failed : " + this.getShorthand();
                this.getParentLogger().error(msg,t);
                throw new RuntimeException(msg);
            }
        }
    }

    /**
     * @see org.apache.avalon.framework.activity.Suspendable#resume()
     */
    public void resume()
    {
        Object rawInstance = this.getRawInstance(false);

        if( rawInstance instanceof Suspendable )
        {
            try
            {
                this.getParentLogger().debug( "Suspendable.resume() for " + this.getShorthand() );
                ((Suspendable) rawInstance).resume();
            }
            catch (Throwable t)
            {
                String msg = "Resuming the following service failed : " + this.getShorthand();
                this.getParentLogger().error(msg,t);
                throw new RuntimeException(msg);
            }
        }
    }

    /**
     * @see org.apache.avalon.framework.activity.Suspendable#suspend()
     */
    public void suspend()
    {
        Object rawInstance = this.getRawInstance(false);

        if( rawInstance instanceof Suspendable )
        {
            try
            {
                this.getParentLogger().debug( "Suspendable.suspend() for " + this.getShorthand() );
                ((Suspendable) rawInstance).suspend();
            }
            catch (Throwable t)
            {
                String msg = "Suspending the following service failed : " + this.getShorthand();
                this.getParentLogger().error(msg,t);
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

        Object rawInstance = this.getRawInstance(false);

        if( rawInstance instanceof Reconfigurable )
        {
            try
            {
                this.getParentLogger().debug( "Reconfigurable.reconfigure() for " + this.getShorthand() );
                ((Reconfigurable) rawInstance).reconfigure(configuration);
            }
            catch (Throwable t)
            {
                String msg = "Reconfiguring the following service failed : " + this.getShorthand();
                this.getParentLogger().error(msg,t);
                throw new RuntimeException(msg);
            }
        }
    }
}
