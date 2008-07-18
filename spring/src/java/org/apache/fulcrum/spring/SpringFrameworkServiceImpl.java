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

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.Configuration;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * Starts an instance of the Spring Service Framework as Avalon service.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public class SpringFrameworkServiceImpl
    extends AbstractLogEnabled
    implements SpringFrameworkService, Configurable, Initializable, Disposable, ServiceManager
{
    /** the list of configuration files passed to the Spring container */
    private String[] configLocations;

    /** the Spring service container */
    private AbstractApplicationContext ctx;

    /** ServiceManager facade to lookup Spring services */
    private BeanFactoryServiceManager beanFactoryServiceManager;

    /////////////////////////////////////////////////////////////////////////
    // Avalon Service Lifecycle Implementation
    /////////////////////////////////////////////////////////////////////////

    /**
     * Constructor
     */
    public SpringFrameworkServiceImpl()
    {
        this.configLocations = new String[0];
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration configuration) throws ConfigurationException
    {

        // parse the 'configLocations' to passed to Spring

        Configuration[] configLocationConfigurationList = configuration.getChild("configurations").getChildren("configuration");
        this.configLocations = new String[configLocationConfigurationList.length];

        for(int i=0; i<configLocations.length; i++)
        {
            this.configLocations[i] = configLocationConfigurationList[i].getValue();
        }

        if(this.configLocations.length == 0)
        {
            String msg = "No configuration files for the Spring container are defined";
            throw new ConfigurationException(msg);
        }
    }

    /**
     * @see org.apache.avalon.framework.activity.Initializable#initialize()
     */
    public void initialize() throws Exception
    {
        this.ctx = new FileSystemXmlApplicationContext(this.configLocations);
        this.beanFactoryServiceManager = new BeanFactoryServiceManager(ctx);
    }

    /**
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     */
    public void dispose()
    {
        if(this.ctx != null)
        {
            try
            {
                this.ctx.close();
            }
            catch(Exception e)
            {
                String msg = "Failed to dispose the Spring service";
                this.getLogger().error(msg, e);
            }
            finally
            {
                this.ctx = null;
            }
        }

        this.beanFactoryServiceManager = null;        
    }

    /////////////////////////////////////////////////////////////////////////
    // Service interface implementation
    /////////////////////////////////////////////////////////////////////////

    /** @see SpringFrameworkService#getAbstractApplicationContext() */ 
    public AbstractApplicationContext getAbstractApplicationContext()
    {
        return this.ctx;
    }

    /** @see org.apache.avalon.framework.service.ServiceManager#lookup(String) */
    public Object lookup(String key) throws ServiceException
    {
        if(this.beanFactoryServiceManager == null)
        {
            throw new RuntimeException("The SpringFrameworkService is not yet initialized");
        }

        return this.beanFactoryServiceManager.lookup(key);
    }

    /** @see org.apache.avalon.framework.service.ServiceManager#hasService(String)  */
    public boolean hasService(String key)
    {
        if(this.beanFactoryServiceManager == null)
        {
            throw new RuntimeException("The SpringFrameworkService is not yet initialized");
        }

        return this.beanFactoryServiceManager.hasService(key);
    }

    /** @see org.apache.avalon.framework.service.ServiceManager#release(Object)  */
    public void release(Object o)
    {
        if(this.beanFactoryServiceManager == null)
        {
            throw new RuntimeException("The SpringFrameworkService is not yet initialized");
        }

        this.beanFactoryServiceManager.release(o);
    }    
}