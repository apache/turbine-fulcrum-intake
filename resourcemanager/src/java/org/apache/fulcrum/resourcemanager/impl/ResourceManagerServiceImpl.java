package org.apache.fulcrum.resourcemanager.impl;

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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.fulcrum.resourcemanager.ResourceManager;
import org.apache.fulcrum.resourcemanager.ResourceManagerService;

/**
 * Concrete implementation of the Avalon ResourceManager Service.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */
public class ResourceManagerServiceImpl
	extends AbstractLogEnabled
	implements ResourceManagerService, Contextualizable, Serviceable, Configurable, Initializable, Disposable
{
    /** The context supplied by the avalon framework */
    private Context context;

    /** The service manager supplied by the avalon framework */
    private ServiceManager serviceManager;

    /** the list of domain configurations */
    private Configuration[] domainConfigurationList;

    /** The list of registered domains */
    private Hashtable domainList;

    /////////////////////////////////////////////////////////////////////////
    // Avalon Service Lifecycle Implementation
    /////////////////////////////////////////////////////////////////////////

    /**
     * Constructor
     */
    public ResourceManagerServiceImpl()
    {
        this.domainList = new Hashtable();
    }

    /**
     * @see org.apache.avalon.framework.context.Contextualizable#contextualize(org.apache.avalon.framework.context.Context)
     */
    public void contextualize(Context context) throws ContextException
    {
        this.context = context;
    }

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager serviceManager) throws ServiceException
    {
        this.serviceManager = serviceManager;
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration cfg) throws ConfigurationException
    {
        this.domainConfigurationList = cfg.getChildren();
    }

    /**
     * @see org.apache.avalon.framework.activity.Initializable#initialize()
     */
    public void initialize() throws Exception
    {
        ResourceManager resourceManager = null;

        for( int i=0; i<this.domainConfigurationList.length; i++ )
        {
            resourceManager = this.incarnate( domainConfigurationList[i] );
            this.domainList.put( resourceManager.getDomain(), resourceManager );
        }
    }

    /**
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     */
    public void dispose()
    {
        String[] domainList = this.listDomains();
        ResourceManager resourceManager = null;

        for( int i=0; i<domainList.length; i++ )
        {
            resourceManager = (ResourceManager) this.getResourceManager(domainList[i]);

            if( resourceManager instanceof Disposable )
            {
                ((Disposable) resourceManager).dispose();
            }
        }

        this.domainList.clear();

        this.context = null;
        this.domainList = null;
        this.domainConfigurationList = null;
        this.serviceManager = null;
    }

    /////////////////////////////////////////////////////////////////////////
    // Service interface implementation
    /////////////////////////////////////////////////////////////////////////

    /**
     * @see org.apache.fulcrum.resourcemanager.ResourceManagerService#create(java.lang.String, java.lang.String, java.lang.Object)
     */
    public void create(String domain, String resourcePath, Object resourceContent) throws IOException
    {
        ResourceManager resourceManager = this.getResourceManager(domain);
        resourceManager.create( resourcePath, resourceContent );
    }

    /**
     * @see org.apache.fulcrum.resourcemanager.ResourceManagerService#delete(java.lang.String, java.lang.String)
     */
    public boolean delete(String domain, String resourcePath)
        throws IOException
    {
        ResourceManager resourceManager = this.getResourceManager(domain);
        return resourceManager.delete( resourcePath );
    }

    /**
     * @see org.apache.fulcrum.resourcemanager.ResourceManagerService#exists(java.lang.String)
     */
    public boolean exists(String domain)
    {
        return this.getDomainList().containsKey( domain );
    }

    /**
     * @see org.apache.fulcrum.resourcemanager.ResourceManagerService#exists(java.lang.String, java.lang.String)
     */
    public boolean exists(String domain, String resourceName)
    {
        ResourceManager resourceManager = this.getResourceManager(domain);
        return resourceManager.exists( resourceName );
    }

    /**
     * @see org.apache.fulcrum.resourcemanager.ResourceManagerService#exists(java.lang.String, java.lang.String[], java.lang.String)
     */
    public boolean exists(String domain, String[]context, String resourceName)
    {
        ResourceManager resourceManager = this.getResourceManager(domain);
        return resourceManager.exists( context, resourceName );
    }

    /**
     * @see org.apache.fulcrum.resourcemanager.ResourceManagerService#listDomains()
     */
    public String[] listDomains()
    {
        String key = null;
        Enumeration keys = this.getDomainList().keys();
        ArrayList result = new ArrayList();

        while( keys.hasMoreElements() )
        {
            key = (String) keys.nextElement();
            result.add( key );
        }

        return (String[]) result.toArray( new String[result.size()] );
    }

    /**
     * @see org.apache.fulcrum.resourcemanager.ResourceManagerService#listResources(java.lang.String)
     */
    public String[] listResources(String domain)
    {
        ResourceManager resourceManager = this.getResourceManager(domain);
        return resourceManager.listResources();
    }

    /**
     * @see org.apache.fulcrum.resourcemanager.ResourceManagerService#read(java.lang.String, java.lang.String)
     */
    public byte[] read(String domain, String resourcePath)
    	throws IOException
    {
        ResourceManager resourceManager = this.getResourceManager(domain);
        return resourceManager.read( resourcePath );
    }

    /**
     * @see org.apache.fulcrum.resourcemanager.ResourceManagerService#update(java.lang.String, java.lang.String, java.lang.Object)
     */
    public void update(String domain, String resourcePath, Object resourceContent)
    	throws IOException
    {
        ResourceManager resourceManager = this.getResourceManager(domain);
        resourceManager.update( resourcePath, resourceContent );
    }

    /**
     * @see org.apache.fulcrum.resourcemanager.ResourceManagerService#getResourceURL(java.lang.String, java.lang.String[], java.lang.String)
     */
    public URL getResourceURL(String domain, String[] context, String resourceName)
    {
        ResourceManager resourceManager = this.getResourceManager(domain);
        return resourceManager.getResourceURL( context, resourceName );
    }

    /**
     * @see org.apache.fulcrum.resourcemanager.ResourceManagerService#read(java.lang.String, java.lang.String[], java.lang.String)
     */
    public byte [] read(String domain, String[] context, String resourceName)
        throws IOException
    {
        ResourceManager resourceManager = this.getResourceManager(domain);
        return resourceManager.read( context, resourceName );
    }

    /**
     * @see org.apache.fulcrum.resourcemanager.ResourceManagerService#locate(java.lang.String, java.lang.String[], java.lang.String)
     */
    public String locate(String domain, String[] context, String resourceName)
    {
        ResourceManager resourceManager = this.getResourceManager(domain);
        return resourceManager.locate( context, resourceName );
    }

    /////////////////////////////////////////////////////////////////////////
    // Service implementation
    /////////////////////////////////////////////////////////////////////////

    /**
     * @return Returns the domainList.
     */
    protected Hashtable getDomainList()
    {
        return domainList;
    }

    /**
     * Incarnates the concrete resource manager instances
     */
    private ResourceManager incarnate( Configuration domainConfiguration )
    	throws Exception
    {
        ResourceManager result = null;

        String domainName = domainConfiguration.getAttribute("name");
        String domainType = domainConfiguration.getAttribute("type",FileResourceManager.class.getName());

        // create an instance dynamically

        this.getLogger().debug( "Creating a resource manager for " + domainName);

        result = this.createResourceManager( domainType, domainName );

        // invoke the lifecycle methods

        Logger currLogger = this.getLogger().getChildLogger(domainName);

        if( result instanceof AbstractLogEnabled )
        {
            ((AbstractLogEnabled) result).enableLogging( currLogger );
        }

        if( result instanceof Contextualizable )
        {
            ((Contextualizable) result).contextualize( this.context );
        }

        if( result instanceof Serviceable )
        {
            ((Serviceable) result).service( this.serviceManager );
        }

        if( result instanceof Configurable )
        {
            ((Configurable) result).configure( domainConfiguration );
        }

        if( result instanceof Initializable )
        {
            ((Initializable) result).initialize();
        }

        return result;
    }

    /**
     * Load the given class dynamically and return an instance of it.
     * @param clazz the name of the class
     * @return an instance of the class
     * @throws Exception creating an instance failed
     */
    private ResourceManager createResourceManager( String clazz, String domain )
    	throws Exception
	{
        ResourceManager result = null;

        try
        {
            result = (ResourceManager) Class.forName(clazz).newInstance();
            return result;
        }
        catch ( Exception e )
        {
            String msg = "Failed to create an instance for domain " + domain;
            this.getLogger().error( msg, e );
            throw new RuntimeException( e.getMessage() );
        }
	}

    /**
     * Get the ResourceManager for the given domain.
     * @param domain the name of the domain
     * @return the ResourceManager
     */
    private ResourceManager getResourceManager( String domain )
    {
        ResourceManager result = (ResourceManager) this.getDomainList().get(
            domain
            );

        if( result == null )
        {
            String msg = "The following domain does not exist : " + domain;
            throw new IllegalArgumentException( msg );
        }

        return result;
    }
}
