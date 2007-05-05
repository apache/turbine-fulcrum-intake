package org.apache.fulcrum.yaafi.interceptor.baseservice;

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
import java.util.HashSet;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.Reconfigurable;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.fulcrum.yaafi.framework.interceptor.AvalonInterceptorContext;
import org.apache.fulcrum.yaafi.framework.interceptor.AvalonInterceptorService;
import org.apache.fulcrum.yaafi.framework.util.StringUtils;

/**
 * A base service providing common functionality for interceptors
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public class BaseInterceptorServiceImpl
    extends AbstractLogEnabled
    implements AvalonInterceptorService, Contextualizable, Reconfigurable
{
    /** this matches all services */
    private static final String WILDCARD = "*";

    /** contains the services being monitored by the interceptor */
    private HashSet serviceSet;

    /** is the interceptor service enabled */
    private boolean isEnabled;

    /** The name of the service as defined in the role configuration file */
    private String serviceName;

    /** The service manager supplied by the Avalon framework */
    private ServiceManager serviceManager;

    /** the Avalon application directory */
    private File serviceApplicationDir;

    /** the Avalon temp directory */
    private File serviceTempDir;

    /** the supplied class loader */
    private ClassLoader classLoader;


    /////////////////////////////////////////////////////////////////////////
    // Avalon Service Lifecycle Implementation
    /////////////////////////////////////////////////////////////////////////

    /**
     * Constructor
     */
    public BaseInterceptorServiceImpl()
    {
        this.serviceSet = new HashSet();
    }

    /**
     * @see org.apache.avalon.framework.context.Contextualizable#contextualize(org.apache.avalon.framework.context.Context)
     */
    public void contextualize(Context context) throws ContextException
    {
        this.serviceName = (String) context.get("urn:avalon:name");
        this.serviceApplicationDir = (File) context.get("urn:avalon:home");
        this.serviceTempDir = (File) context.get("urn:avalon:temp");
        this.classLoader = (ClassLoader) context.get("urn:avalon:classloader");
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration configuration) throws ConfigurationException
    {
        // take care - the default is disabled which is helpful
        // for the way we use the interceptors

        this.isEnabled = configuration.getChild("isEnabled").getValueAsBoolean(false);

        // parse the service to be monitored

        Configuration[] serviceConfigList = configuration.getChild("services").getChildren("service");

        if( serviceConfigList.length == 0 )
        {
            this.getServiceSet().add(WILDCARD);
        }
        else
        {
            for( int i=0; i<serviceConfigList.length; i++ )
            {
                String name = serviceConfigList[i].getAttribute("name", null);
                String shorthand = serviceConfigList[i].getAttribute("shorthand", null);

                if( !StringUtils.isEmpty(name) )
                {
                    this.getServiceSet().add(name);
                }

                if( !StringUtils.isEmpty(shorthand) )
                {
                    this.getServiceSet().add(shorthand);
                }
            }
        }
    }

    /**
     * @see org.apache.avalon.framework.configuration.Reconfigurable#reconfigure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void reconfigure(Configuration configuration) throws ConfigurationException
    {
        this.getServiceSet().clear();
    }

    /////////////////////////////////////////////////////////////////////////
    // Service interface implementation
    /////////////////////////////////////////////////////////////////////////

    /**
     * @see org.apache.fulcrum.yaafi.framework.interceptor.AvalonInterceptorService#onEntry(org.apache.fulcrum.yaafi.framework.interceptor.AvalonInterceptorContext)
     */
    public void onEntry(AvalonInterceptorContext avalonInterceptorContext)
    {
        // nothing to do
    }

    /**
     * @see org.apache.fulcrum.yaafi.framework.interceptor.AvalonInterceptorService#onError(org.apache.fulcrum.yaafi.framework.interceptor.AvalonInterceptorContext, java.lang.Throwable)
     */
    public void onError(AvalonInterceptorContext avalonInterceptorContext,Throwable t)
    {
        // nothing to do
    }

    /**
     * @see org.apache.fulcrum.yaafi.framework.interceptor.AvalonInterceptorService#onExit(org.apache.fulcrum.yaafi.framework.interceptor.AvalonInterceptorContext, java.lang.Object)
     */
    public void onExit(AvalonInterceptorContext avalonInterceptorContext, Object result)
    {
        // nothing to do
    }

    /////////////////////////////////////////////////////////////////////////
    // Service Implementation
    /////////////////////////////////////////////////////////////////////////

    /**
     * @return Returns the isEnabled.
     */
    protected boolean isEnabled()
    {
        return isEnabled;
    }

    /**
     * Determine if the given service is monitored.
     *
     * @param avalonInterceptorContext interceptor context
     * @return true if the service is monitored or false otherwise
     */
    protected boolean isServiceMonitored( AvalonInterceptorContext avalonInterceptorContext )
    {
        if( !this.isEnabled() )
        {
            return false;
        }
        else if( this.getServiceSet().contains(WILDCARD) )
        {
            return true;
        }
        else if( this.getServiceSet().contains(avalonInterceptorContext.getServiceName()) )
        {
            return true;
        }
        else if( this.getServiceSet().contains(avalonInterceptorContext.getServiceShorthand()) )
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * @return Returns the serviceApplicationDir.
     */
    protected File getServiceApplicationDir()
    {
        return serviceApplicationDir;
    }

    /**
     * @return Returns the serviceManager.
     */
    protected ServiceManager getServiceManager()
    {
        return serviceManager;
    }

    /**
     * @return Returns the serviceName.
     */
    protected String getServiceName()
    {
        return serviceName;
    }

    /**
     * @return Returns the serviceTempDir.
     */
    protected File getServiceTempDir()
    {
        return serviceTempDir;
    }

    /**
		 * @return Returns the classLoader.
		 */
		protected ClassLoader getClassLoader() {
			return this.classLoader;
		}

		/**
     * Determines the file location of the given name. If the name denotes
     * a relative file location it will be resolved using the application
     * home directory.
     *
     * @param name the filename
     * @return the file
     */
    protected File makeAbsoluteFile( String name )
    {
        File result = new File(name);

        if( result.isAbsolute() == false )
        {
            result = new File( this.getServiceApplicationDir(), name );
        }

        return result;
    }

    /**
     * @return Returns the serviceMap.
     */
    private HashSet getServiceSet()
    {
        return serviceSet;
    }
}
