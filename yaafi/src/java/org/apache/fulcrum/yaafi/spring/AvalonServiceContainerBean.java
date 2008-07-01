package org.apache.fulcrum.yaafi.spring;

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
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.logger.CommonsLogger;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.fulcrum.yaafi.framework.factory.ServiceContainerConfiguration;
import org.apache.fulcrum.yaafi.framework.factory.ServiceContainerFactory;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.BeansException;

/**
 * A POJO starting/stopping the YAAFI Avalon container and exposing a ServiceManager.
 * This allows to run an Avalon container within Spring and to lookup Avalon
 * services.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public class AvalonServiceContainerBean implements BeanNameAware, BeanFactoryAware, ServiceManager, Initializable, Disposable
{
    /** The service manager */
    private Object serviceContainer;

    /** The location of the container configuration */
    private String containerConfigValue;

    /** The logger being used */
    private Logger logger;

    /** the working directory */
    private String applicationHome;

    /** the temp directory */
    private String tempHome;

    /** the Spring bean factory creating this instance */
    private BeanFactory beanFactory;

    /** the name of the bean */
    private String beanName;

    /** the name of the CommonsLogger instance */
    private String loggerName;
    
    /**
     * Constructor
     */
    public AvalonServiceContainerBean()
    {
        this.containerConfigValue   = "./conf/containerConfiguration.xml";
        this.applicationHome        = ".";
        this.tempHome               = System.getProperty("java.io.tmpdir",".");
    }

    /////////////////////////////////////////////////////////////////////////
    // Interface Implementation
    /////////////////////////////////////////////////////////////////////////

    /**
     * Initialize the instance. This method must be configured using
     * the 'init-method' attribute. 
     *
     * @see org.apache.avalon.framework.activity.Initializable#initialize()
     * @throws Exception the initialization failed
     */
    public void initialize() throws Exception
    {
        ServiceContainerConfiguration config = new ServiceContainerConfiguration();

        // wrap Spring's BeanFactory to allow service lookups
        BeanFactoryServiceManager beanFactoryServiceManager = new BeanFactoryServiceManager(this.beanFactory);

        // create a logger
        this.createLogger();

        // intialize the Avalon serviceContainer
        config.setLogger( this.getLogger() );
        config.setApplicationRootDir( this.getApplicationHome() );
        config.setTempRootDir( this.getTempHome() );
        config.loadContainerConfiguration( this.getContainerConfigValue(), "auto" );
        config.setParentServiceManager(beanFactoryServiceManager);

        this.serviceContainer = ServiceContainerFactory.create( config );
    }

    /**
     * Dispose the YAAFI container. This method must be configured using
     * the 'destroy-method' attribute.
     *
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     */
    public void dispose()
    {
        if( this.serviceContainer == null)
        {
            return;
        }

        this.getLogger().debug( "Terminating " + this.getClass().getName() );

        try
        {
            // dispose the service serviceContainer

            if( this.serviceContainer != null )
            {
                ((Disposable) this.serviceContainer).dispose();
            }
        }
        catch (Exception e)
        {
            String msg = "Failed to terminate " + this.getClass().getName();
            this.getLogger().error(msg,e);
        }
        finally {
            this.serviceContainer = null;
        }
    }

    /** @see org.apache.avalon.framework.service.ServiceManager#lookup(String) */
    public Object lookup(String s) throws ServiceException
    {
        return ((ServiceManager) this.serviceContainer).lookup(s);
    }

    /** @see org.apache.avalon.framework.service.ServiceManager#hasService(String) */
    public boolean hasService(String s)
    {
        return ((ServiceManager) this.serviceContainer).hasService(s);
    }

    /** @see org.apache.avalon.framework.service.ServiceManager#release(Object) */
    public void release(Object o)
    {
        ((ServiceManager) this.serviceContainer).release(o);
    }

    /** @see org.springframework.beans.factory.BeanFactoryAware#setBeanFactory(org.springframework.beans.factory.BeanFactory)  */
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException
    {
        this.beanFactory = beanFactory;
    }

    /** @see org.springframework.beans.factory.BeanNameAware#setBeanName(String) */
    public void setBeanName(String name)
    {
        this.beanName = name;
    }

    /////////////////////////////////////////////////////////////////////////
    // Generated getters & setters
    /////////////////////////////////////////////////////////////////////////

    /**
     * @return Returns the applicationHome.
     */
    public String getApplicationHome()
    {
        return this.applicationHome;
    }

    /**
     * @param applicationHome The applicationHome to set.
     */
    public void setApplicationHome(String applicationHome)
    {
        this.applicationHome = applicationHome;
    }

    /**
     * @return Returns the containerConfigValue.
     */
    public String getContainerConfigValue()
    {
        return containerConfigValue;
    }

    /**
     * @param containerConfigValue The containerConfigValue to set.
     */
    public void setContainerConfigValue(String containerConfigValue)
    {
        this.containerConfigValue = containerConfigValue;
    }

    /**
     * @return Returns the tempHome.
     */
    public String getTempHome()
    {
        return this.tempHome;
    }

    /**
     * @param tempHome The tempHome to set.
     */
    public void setTempHome(String tempHome)
    {
        this.tempHome = tempHome;
    }

    /**
     * @return Returns the loggerName.
     */
    public String getLoggerName()
    {
        if(this.loggerName == null)
        {
            return this.beanName;
        }
        else
        {
            return loggerName;
        }
    }

    /**
     * @param loggerName The loggerName to set.
     */
    public void setLoggerName(String loggerName)
    {
        this.loggerName = loggerName;
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

    /////////////////////////////////////////////////////////////////////////
    // Implementation
    /////////////////////////////////////////////////////////////////////////

    /**
     * Create the Avalon Logger to be used for the Avalon container. This
     * method can be overridden if you need a different logger.
     *
     * @return avalon loggger
     */
    public Logger createLogger()
    {
        if(this.logger == null)
        {
            Log log = LogFactory.getLog(this.getLoggerName());
            this.logger = new CommonsLogger(log, this.getLoggerName());
        }

        return this.logger;
    }

    /**
     * @see Object#toString()
     */
    public String toString()
    {
        StringBuffer result = new StringBuffer();
        result.append(getClass().getName()).append("@").append(Integer.toHexString(hashCode()));
        result.append('[');
        result.append("workingDir=").append(new File("").getAbsolutePath());
        result.append(',');
        result.append("applicationHome=").append(this.getApplicationHome());
        result.append(',');
        result.append("tempHome=").append(this.getTempHome());
        result.append(',');
        result.append("logger=").append(this.getLogger().getClass().getName());
        result.append(',');
        result.append("containerConfigValue=").append(this.getContainerConfigValue());
        result.append(']');
        return result.toString();
    }
}