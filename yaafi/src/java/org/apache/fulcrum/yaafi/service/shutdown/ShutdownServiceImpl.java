package org.apache.fulcrum.yaafi.service.shutdown;

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

import java.io.File;
import java.security.MessageDigest;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.Reconfigurable;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;


/**
 * Monitors the componentConfiguration.xml and triggers a reconfiguration
 * if the content of the component configuration file  has changed.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public class ShutdownServiceImpl
    extends AbstractLogEnabled
    implements ShutdownService, Serviceable, Contextualizable,
        Reconfigurable, Initializable, Runnable, Startable, Disposable
{
    /** the interval between two checks in ms */
    private int interval;

    /** shall the worker thread terminate immediately */
    private boolean terminateNow;

    /** the worker thread polling the resource */
    private Thread workerThread;

    /** the ServiceManager to use */
    private ServiceManager serviceManager;

    /** the application directory */
    private File applicationDir;

    /** our own and only shutdown entry */
    private ShutdownEntry shutdownEntry;

    /////////////////////////////////////////////////////////////////////////
    // Avalon Service Lifecycle Implementation
    /////////////////////////////////////////////////////////////////////////

    /**
     * Constructor
     */
    public ShutdownServiceImpl()
    {
        this.terminateNow = false;
    }

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager manager) throws ServiceException
    {
        this.serviceManager = manager;
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

        if( configuration.getChild("entry",false) != null )
        {
            Configuration shutdownConfig = configuration.getChild("entry");

            String shutdownEntryLocation = shutdownConfig.getChild("location").getValue();

            this.shutdownEntry = new ShutdownEntry(
                this.getLogger(),
                this.applicationDir,
                shutdownEntryLocation,
                shutdownConfig.getChild("useSystemExit").getValueAsBoolean(false)
                );

            this.getLogger().debug( "Using a shutdown entry : " + shutdownEntryLocation );
        }
        else
        {
            this.shutdownEntry = null;
            this.getLogger().debug( "No shutdown entry defined" );
        }
    }

    /**
     * @see org.apache.avalon.framework.activity.Initializable#initialize()
     */
    public void initialize() throws Exception
    {
        // request a SHA-1 to make sure that it is supported

        MessageDigest.getInstance( "SHA1" );

        // check that the ServiceManager inplements Disposable

        if( (this.serviceManager instanceof Disposable) == false )
        {
            String msg = "The ServiceManager instance does not implement Disposable?!";
            throw new IllegalArgumentException( msg );
        }

        // create the worker thread polling the target

        this.workerThread = new Thread( this, "ShutdownService" );
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
     * @see java.lang.Runnable#run()
     */
    public void run()
    {
        while( this.terminateNow == false )
        {
            try
            {
                Thread.sleep( this.interval );
            }
            catch (InterruptedException e)
            {
                // nothing to do
            }

            if( this.hasShutdownEntry() && this.getShutdownEntry().hasChanged() )
            {
                if( this.serviceManager instanceof Disposable )
                {
                    if( this.getShutdownEntry().isUseSystemExit() )
                    {
                        this.getLogger().warn( "Forcing a shutdown using System.exit() ..." );
                    }
                    else
                    {
                        this.getLogger().warn( "Forcing a shutdown ..." );
                    }

                    // create a demon thread to shutdown the container

                    Shutdown shutdown = new Shutdown(
                        (Disposable) this.serviceManager,
                        this.getShutdownEntry().isUseSystemExit()
                        );

                    Thread shutdownThread = new Thread( shutdown, "ShutdownServiceThread" );
                    shutdownThread.setDaemon(true);
                    shutdownThread.start();
                }
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////
    // Service implementation
    /////////////////////////////////////////////////////////////////////////

    /**
     * @return Returns the shutdownEntry.
     */
    private ShutdownEntry getShutdownEntry()
    {
        return this.shutdownEntry;
    }

    /**
     * @return Is a shutdown entry defined?
     */
    private boolean hasShutdownEntry()
    {
        return ( this.shutdownEntry != null ? true : false );
    }
}
