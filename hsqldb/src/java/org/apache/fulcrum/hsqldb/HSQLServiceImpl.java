package org.apache.fulcrum.hsqldb;

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
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.hsqldb.Server;
import org.hsqldb.ServerConstants;
import org.hsqldb.persist.HsqlProperties;

/**
 * The original implementation was taken from
 * http://scarab.tigris.org/source/browse/scarab/src/java/org/tigris/scarab/services/hsql/
 * and tweaked a little bit.
 *
 * <p>
 * The component is configured from the componentConfig.xml file by specifying
 * attributes on the service element
 * </p>
 * <p>
 *
 * <dl>
 * <dt>database</dt>
 * <dd>The directory path where the database files will be stored</dd>
 * <dt>dbname</dt>
 * <dd>The alias path used to refer to the database from the JDBC url.</dd>
 * <dt>trace</dt>
 * <dd>(true/false) a flag enabling tracing in the hsql server.</dd>
 * <dt>silent</dt>
 * <dd>(true/false) a flag to control the logging output of the hsql server.</dd>
 * <dt>start</dt>
 * <dd>(true/false) when true the database is started at configuration time, and does
 * not need to be started under application control.</dd>
 * <dt>port</dt>
 * <dd>The listening port of the hsql server.</dd>
 * </dl>
 *
 * Example:
 *  ...
 *  <HSQLService database="./target" dbname="test" trace="true" silent="false" start="true" port="9001"/>
 *  ...
 *
 * @author <a href="mailto:pti@elex.be">Peter Tillemans</a>
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */
public class HSQLServiceImpl
	extends AbstractLogEnabled
	implements HSQLService, Configurable, Initializable, Startable, Disposable
{
    /** the HSQLDB server instance */
    private Server server;

    /** the configuration properties */
    private HsqlProperties serverProperties;

    /////////////////////////////////////////////////////////////////////////
    // Avalon Service Lifecycle Implementation
    /////////////////////////////////////////////////////////////////////////

    /**
     * Constructor
     */
    public HSQLServiceImpl()
    {
        // nothing to do
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration cfg) throws ConfigurationException
    {
        String[] names = cfg.getAttributeNames();

        for (int i = 0; i < names.length; i++)
        {
            getLogger().debug(names[i] + " --> " + cfg.getAttribute(names[i]));
        }

        this.serverProperties = new HsqlProperties();
        this.serverProperties.setProperty("server.database.0", cfg.getAttribute("database"));
        this.serverProperties.setProperty("server.dbname.0", cfg.getAttribute("dbname"));
        this.serverProperties.setProperty("server.trace", cfg.getAttributeAsBoolean("trace", false));
        this.serverProperties.setProperty("server.silent", cfg.getAttributeAsBoolean("silent", true));
        this.serverProperties.setProperty("server.port", cfg.getAttribute("port"));
        this.serverProperties.setProperty("server.tls", cfg.getAttribute("tls","false"));
    }

    /**
     * @see org.apache.avalon.framework.activity.Initializable#initialize()
     */
    public void initialize() throws Exception
    {
        this.server = new Server();
        this.server.setProperties( this.serverProperties );
    }

    /**
     * Starts the HSQLDB server. The implementation polls to ensure
     * that the HSQLDB server is fully initialized otherwise we get
     * spurious connection exceptions. If the HSQLDB server is not
     * upand running within 10 seconds we throw an exception.
     *
     * @see org.apache.avalon.framework.activity.Startable#start()
     */
    public void start() throws Exception
    {
        // The method start() waits for current state to change from
        // SERVER_STATE_OPENNING. In order to discover the success or failure
        // of this operation, server state must be polled or a subclass of Server
        // must be used that overrides the setState method to provide state
        // change notification.

        server.start();

        // poll for 10 seconds until HSQLDB is up and running

        this.pollForState( ServerConstants.SERVER_STATE_ONLINE, 100 );
    }

    /**
     * Stop the HSQLDB server. The implementation polls to ensure
     * that the HSQLDB server has terminated otherwise someone
     * could call System.exit() and break the database.
     *
     * @see org.apache.avalon.framework.activity.Startable#stop()
     */
    public void stop() throws Exception
    {
        this.server.stop();

        // poll for 10 seconds until HSQLDB is down

        this.pollForState( ServerConstants.SERVER_STATE_SHUTDOWN, 100 );
    }

    /**
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     */
    public void dispose()
    {
        this.server = null;
        this.serverProperties = null;
    }

    /////////////////////////////////////////////////////////////////////////
    // Service Interface Implementation
    /////////////////////////////////////////////////////////////////////////

    public boolean isRunning() {
        // return true id server is online
        return server.getState() == ServerConstants.SERVER_STATE_ONLINE;
    }


    /////////////////////////////////////////////////////////////////////////
    // Service Implementation
    /////////////////////////////////////////////////////////////////////////

    /**
     * Poll the HSQLDB server for a state change.
     *
     * @param desiredState the state we are waiting for
     * @param lim the number of 100ms iteration to wait for
     * @throws Exception something went wrong
     */
    private void pollForState( int desiredState, int lim )
    	throws Exception
    {
        int currentState;
        boolean isSuccessful = false;

        this.getLogger().debug( "Polling for state : " + desiredState );

        for( int i=0; i<lim; i++ )
        {
            currentState = this.server.getState();

            if( desiredState == currentState )
            {
                isSuccessful = true;
                break;
            }

            Thread.sleep(100);
        }

        if( !isSuccessful )
        {

            Throwable serverError = this.server.getServerError();
            String msg = "Unable to change the HSQLDB server to state : " + desiredState;

            if( serverError != null )
            {
                this.getLogger().error( msg, serverError );

	            if( serverError instanceof Exception )
	            {
	                throw (Exception) serverError;
	            }
                else
	            {
	                throw new RuntimeException( serverError.getMessage() );
	            }
            }
            else
            {
	            this.getLogger().error(msg);
	            throw new RuntimeException( msg );
            }
        }
    }
}
