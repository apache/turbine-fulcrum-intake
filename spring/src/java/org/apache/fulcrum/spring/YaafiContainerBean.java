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

import java.io.File;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.fulcrum.yaafi.framework.factory.ServiceContainerConfiguration;
import org.apache.fulcrum.yaafi.framework.factory.ServiceContainerFactory;

/**
 * A POJO starting/stopping the YAAFI container and exposing a ServiceManager.
 * This allows to run an Avalon container within Spring and to lookup Avalon
 * services using the exposed ServiceManager.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */
public class YaafiContainerBean extends AvalonContainerBean
{
    /** The location of the container configuration */
    private String containerConfigValue;

    /** the working directory */
    private String applicationHome;

    /** the temp directory */
    private String tempHome;
    
    /**
     * Constructor
     */
    public YaafiContainerBean()
    {
        super();
        
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
        BeanFactoryServiceManager beanFactoryServiceManager = new BeanFactoryServiceManager(this.getBeanFactory());

        // intialize the Avalon serviceContainer
        config.setLogger( this.getLogger() );
        config.setApplicationRootDir( this.getApplicationHome() );
        config.setTempRootDir( this.getTempHome() );
        config.loadContainerConfiguration( this.getContainerConfigValue(), "auto" );
        config.setParentServiceManager(beanFactoryServiceManager);
        config.setContext(new DefaultContext(this.getDefaultContext()));

        this.setServiceManager(ServiceContainerFactory.create(config));
    }

    /**
     * Dispose the YAAFI container. This method must be configured using
     * the 'destroy-method' attribute.
     *
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     */
    public void dispose()
    {
        if( this.getServiceManager() == null)
        {
            return;
        }

        try
        {
            // dispose the service serviceContainer
            ((Disposable) this.getServiceManager()).dispose();
        }
        catch (Exception e)
        {
            String msg = "Failed to terminate " + this.getClass().getName();
            this.getLogger().error(msg,e);
        }
        finally {
            this.setServiceManager(null);
        }
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

    /////////////////////////////////////////////////////////////////////////
    // Implementation
    /////////////////////////////////////////////////////////////////////////

    /**
     * @see Object#toString()
     */
    public String toString()
    {
        StringBuffer result = new StringBuffer();
        result.append(getClass().getName()).append("@").append(Integer.toHexString(hashCode()));
        result.append('[');
        result.append("beanName=").append(this.getBeanName());
        result.append(',');
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