package org.apache.fulcrum.spring;

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

import java.util.Map;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.logger.CommonsLogger;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.BeansException;

/**
 * Base class to create an Avalon container as Spring bean.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */
public abstract class AvalonContainerBean implements BeanNameAware, BeanFactoryAware, ServiceManager, Initializable, Disposable
{
    /** The service manager used for service lookups */
    private ServiceManager serviceManager;

    /** The logger being used */
    private Logger logger;

    /** the name of the bean */
    private String beanName;

    /** the Spring bean factory creating this instance */
    private BeanFactory beanFactory;

    /** the Avalon default context passed to the container */
    private Map defaultContext;

    /**
     * Constructor
     */
    public AvalonContainerBean()
    {
    }

    /////////////////////////////////////////////////////////////////////////
    // Service Interface Implementation
    /////////////////////////////////////////////////////////////////////////

    /**
     * Initialize the instance. This method must be configured using
     * the 'init-method' attribute.
     *
     * @see org.apache.avalon.framework.activity.Initializable#initialize()
     * @throws Exception the initialization failed
     */
    public abstract void initialize() throws Exception;

    /**
     * Dispose the YAAFI container. This method must be configured using
     * the 'destroy-method' attribute.
     *
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     */
    public abstract void dispose();

    /**
     * @see org.apache.avalon.framework.service.ServiceManager#lookup(String)
     */
    public Object lookup(String s) throws ServiceException
    {
        return this.getServiceManager().lookup(s);
    }

    /**
     * @see org.apache.avalon.framework.service.ServiceManager#hasService(String)
     */
    public boolean hasService(String s)
    {
        return this.getServiceManager().hasService(s);
    }

    /**
     * @see org.apache.avalon.framework.service.ServiceManager#release(Object)
     */
    public void release(Object o)
    {
        this.getServiceManager().release(o);
    }

    /**
     * @see org.springframework.beans.factory.BeanFactoryAware#setBeanFactory(org.springframework.beans.factory.BeanFactory)
     */
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException
    {
        this.beanFactory = beanFactory;
    }

    /**
     * @see org.springframework.beans.factory.BeanNameAware#setBeanName(String)
     */
    public void setBeanName(String name)
    {
        this.beanName = name;
    }

    /////////////////////////////////////////////////////////////////////////
    // Generated getters & setters
    /////////////////////////////////////////////////////////////////////////

    /**
     * @return Returns the logger.
     */
    public Logger getLogger()
    {
        if(this.logger == null)
        {
            this.logger = this.createLogger();
        }

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
     * Get the values for the custom Avalon context
     *
     * @return the Avalon default context
     */
    public Map getDefaultContext() {
        return defaultContext;
    }

    /**
     * Allows setting a custom Avalon context.
     * 
     * @param defaultContext The Avalon default context to set
     */
    public void setDefaultContext(Map defaultContext) {
        this.defaultContext = defaultContext;
    }

    /**
     * @return the Spring bean name
     */
    public String getBeanName() {
        return beanName;
    }

    /**
     * @return the Spring bean factory
     */
    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    /////////////////////////////////////////////////////////////////////////
    // Implementation
    /////////////////////////////////////////////////////////////////////////

    /**
     * Create the Avalon Logger to be used for the Avalon container. This
     * method can be overridden if you don't want a CommonsLogger.
     *
     * @return avalon loggger
     */
    protected Logger createLogger()
    {
        Log log = LogFactory.getLog(this.getBeanName());
        return new CommonsLogger(log, this.getBeanName());
    }

    protected ServiceManager getServiceManager()
    {
        return this.serviceManager;
    }

    protected void setServiceManager(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }
}