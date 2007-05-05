package org.apache.fulcrum.yaafi.service.reconfiguration;

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
import java.io.InputStream;
import java.security.MessageDigest;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.activity.Suspendable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.configuration.Reconfigurable;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.fulcrum.yaafi.framework.container.ServiceLifecycleManager;


/**
 * Monitors the componentConfiguration.xml and triggers a reconfiguration
 * if the content of the component configuration file  has changed.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public class ReconfigurationServiceImpl
    extends AbstractLogEnabled
    implements ReconfigurationService, Serviceable, Contextualizable,
        Reconfigurable, Initializable, Runnable, Startable, Disposable
{
    /** the interval between two checks in ms */
    private int interval;

    /** shall the worker thread terminate immediately */
    private boolean terminateNow;

    /** the worker thread polling the componentConfiguraton */
    private Thread workerThread;

    /** the ServiceManager to use */
    private ServiceManager serviceManager;

    /** the application directory */
    private File applicationDir;

    /** our list of resources to monitor */
    private ReconfigurationEntry[] reconfigurationEntryList;

    /** the interface to reconfigure individual services */
    private ServiceLifecycleManager serviceLifecycleManager;

    /////////////////////////////////////////////////////////////////////////
    // Avalon Service Lifecycle Implementation
    /////////////////////////////////////////////////////////////////////////

    /**
     * Constructor
     */
    public ReconfigurationServiceImpl()
    {
        this.terminateNow = false;
    }

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager manager) throws ServiceException
    {
        this.serviceManager = manager;
        this.serviceLifecycleManager = (ServiceLifecycleManager) manager;
    }

    /**
     * @see org.apache.avalon.framework.context.Contextualizable#contextualize(org.apache.avalon.framework.context.Context)
     */
    public void contextualize(Context context) throws ContextException
    {
        this.applicationDir  = (File) context.get("urn:avalon:home");
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration configuration) throws ConfigurationException
    {
        // limit to minimum interval of 1 second

        this.interval = Math.max( configuration.getAttributeAsInteger("interval",5000), 1000 );

        this.getLogger().debug( "Monitoring the resources every " + this.interval + " ms" );

        // parse the resources to monitor

        Configuration entry = null;
        Configuration services = null;
        Configuration[] serviceEntries = null;
        Configuration[] entryList = configuration.getChildren("entry");

        String location = null;
        String serviceName = null;
        String[] serviceNameList = null;
        ReconfigurationEntry reconfigurationEntry = null;
        ReconfigurationEntry[] list = new ReconfigurationEntry[entryList.length];

        for( int i=0; i<entryList.length; i++ )
        {
            entry = entryList[i];
            location = entry.getChild("location").getValue();
            services = entry.getChild("services",false);

            this.getLogger().debug( "Adding the following resource to monitor : " + location );

            if( services != null )
            {
                serviceEntries = services.getChildren("service");
                serviceNameList = new String[serviceEntries.length];

                for( int j=0; j<serviceEntries.length; j++ )
                {
                    serviceName = serviceEntries[j].getAttribute("name");
                    serviceNameList[j] = serviceName;
                }
            }

            reconfigurationEntry = new ReconfigurationEntry(
                this.getLogger(),
                this.applicationDir,
                location,
                serviceNameList
                );

            list[i] = reconfigurationEntry;
        }

        this.getLogger().debug( "Monitoring " + list.length + " resources" );

        this.setReconfigurationEntryList(list);
    }

    /**
     * @see org.apache.avalon.framework.activity.Initializable#initialize()
     */
    public void initialize() throws Exception
    {
        // request a SHA-1 to make sure that it is supported

        MessageDigest.getInstance( "SHA1" );

        // check that the ServiceManager inplements Reconfigurable

        if( (this.serviceManager instanceof ServiceLifecycleManager) == false )
        {
            String msg = "The ServiceManager instance does not implement ServiceLifecycleManager?!";
            throw new IllegalArgumentException( msg );
        }

        // create the worker thread polling the target

        this.workerThread = new Thread( this, "ReconfigurationService" );
    }

    /**
     * @see org.apache.avalon.framework.activity.Startable#start()
     */
    public void start() throws Exception
    {
        this.getLogger().debug( "Starting worker thread ..." );
        this.workerThread.start();
    }

    /**
     * @see org.apache.avalon.framework.activity.Startable#stop()
     */
    public void stop() throws Exception
    {
        this.getLogger().debug( "Stopping worker thread ..." );
        this.terminateNow = true;
        this.workerThread.interrupt();
        this.workerThread.join( 10000 );
    }

    /**
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     */
    public void dispose()
    {
        this.terminateNow = false;
        this.applicationDir = null;
        this.workerThread = null;
        this.serviceManager = null;
        this.reconfigurationEntryList = null;
    }

    /**
     * @see org.apache.avalon.framework.configuration.Reconfigurable#reconfigure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void reconfigure(Configuration configuration)
        throws ConfigurationException
    {
        this.configure(configuration);
    }

    /////////////////////////////////////////////////////////////////////////
    // Service interface implementation
    /////////////////////////////////////////////////////////////////////////

    /**
     * Polls for changes in the confguration to reconfigure either the
     * whole container or just a list of services.
     *
     * @see java.lang.Runnable#run()
     */
    public void run()
    {
        ReconfigurationEntry reconfigurationEntry = null;
        ReconfigurationEntry[] list = null;

        while( this.terminateNow == false )
        {
            list = this.getReconfigurationEntryList();

            try
            {
                for( int i=0; i<list.length; i++ )
                {
                    reconfigurationEntry = list[i];

                    if( reconfigurationEntry.hasChanged() )
                    {
                        this.onReconfigure( reconfigurationEntry );
                    }
                }

                Thread.sleep( this.interval );
            }
            catch( InterruptedException e )
            {
                continue;
            }
            catch(Exception e)
            {
                String msg = "The ReconfigurationService had a problem";
                this.getLogger().error(msg,e);
                continue;
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////
    // Service implementation
    /////////////////////////////////////////////////////////////////////////

    /**
     * Reconfigure either the whole container or a list of services. This
     * method is called within a seperate worker thred.
     *
     * @param reconfigurationEntry the configuration what to reconfigure
     * @throws Exception the reconfiguration failed
     */
    protected void onReconfigure( ReconfigurationEntry reconfigurationEntry )
        throws Exception
    {
        if( reconfigurationEntry.getServiceList() == null )
        {
            // reconfigure the whole container using Avalon Lifecycle Spec

            InputStream is = reconfigurationEntry.locate();
            DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
            Configuration configuration = builder.build(is);
            is.close();
            is = null;

            this.getLogger().warn( "Starting to reconfigure the container" );

            if( this.serviceManager instanceof Suspendable)
            {
                this.getLogger().info( "Calling suspend() of the container" );
                ((Suspendable) this.serviceManager).suspend();
            }

            if( this.serviceManager instanceof Reconfigurable)
            {
                this.getLogger().info( "Calling reconfigure() of the container" );
                ((Reconfigurable) this.serviceManager).reconfigure(configuration);
            }

            if( this.serviceManager instanceof Suspendable)
            {
                this.getLogger().info( "Calling resume() of the container" );
                ((Suspendable) this.serviceManager).resume();
            }

            this.getLogger().info( "Reconfiguring the container was successful" );
        }
        else
        {
            String[] serviceList = reconfigurationEntry.getServiceList();
            this.getLogger().warn( "Calling reconfigure() on individual services : " + serviceList.length );
            this.serviceLifecycleManager.reconfigure(serviceList);
        }
    }

    /**
     * @return Returns the reconfigurationEntryList.
     */
    private synchronized ReconfigurationEntry [] getReconfigurationEntryList()
    {
        return reconfigurationEntryList;
    }

    /**
     * @param reconfigurationEntryList The reconfigurationEntryList to set.
     */
    private synchronized void setReconfigurationEntryList(
        ReconfigurationEntry [] reconfigurationEntryList)
    {
        this.reconfigurationEntryList = reconfigurationEntryList;
    }
}
