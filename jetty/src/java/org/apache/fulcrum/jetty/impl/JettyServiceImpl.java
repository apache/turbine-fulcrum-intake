package org.apache.fulcrum.jetty.impl;

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

import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.Reconfigurable;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.fulcrum.jetty.JettyService;

import org.mortbay.jetty.Server;
import org.mortbay.xml.XmlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileInputStream;

/**
 * Starts an instance of the Spring Service Framework as Avalon service.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public class JettyServiceImpl
    extends AbstractLogEnabled
    implements LogEnabled, Startable, Contextualizable, Initializable, Reconfigurable, JettyService
{
    /** the Jetty server instance */
    private Server server;

    /** the location of the Jetty XML configuration files */
    private String[] configurationLocations;

    /** the working directory of the service */
    private File serviceApplicationDir;

    /////////////////////////////////////////////////////////////////////////
    // Avalon Lifecycle Implementation
    /////////////////////////////////////////////////////////////////////////

    /**
     * Constructor
     */
    public JettyServiceImpl()
    {
        // nothing to do
    }

    public void contextualize(Context context) throws ContextException
    {
        this.serviceApplicationDir = (File) context.get("context-root");
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration configuration) throws ConfigurationException
    {
        // parse the 'configurations'

        Configuration[] configurationsList = configuration.getChild("configurations").getChildren("configuration");
        this.configurationLocations = new String[configurationsList.length];

        for(int i=0; i<this.configurationLocations.length; i++)
        {
            this.configurationLocations[i] = configurationsList[i].getValue();
        }

        if(this.configurationLocations.length == 0)
        {
            String msg = "No configuration files for the Jetty are defined";
            throw new ConfigurationException(msg);
        }

        // parse the 'properties'

        Configuration[] propertiesConfiguration = configuration.getChild("properties", true).getChildren("property");

        for( int i=0; i<propertiesConfiguration.length; i++ )
        {
            String key = propertiesConfiguration[i].getAttribute("name");
            String value = propertiesConfiguration[i].getValue();
            this.getLogger().info("Setting the system property '" + key + "'==>'" + value + "'");            
            System.setProperty( key, value );
        }
    }

    /**
     * @see org.apache.avalon.framework.activity.Initializable#initialize()
     */
    public void initialize() throws Exception
    {
        // locate the serviceConfiguration file and initialize Jetty server

        Server currServer = new Server();

        for(int i=0; i<this.configurationLocations.length; i++)
        {
            String currConfigurationLocation = this.configurationLocations[i]; 
            this.getLogger().info("Loading the Jetty serviceConfiguration file : " + currConfigurationLocation);
            InputStream is = this.locate(this.serviceApplicationDir, currConfigurationLocation);
            XmlConfiguration configuration = new XmlConfiguration(is);
            configuration.configure(currServer);
            is.close();
        }

        this.server = currServer;
    }

    /**
     * @see org.apache.avalon.framework.activity.Startable#start()
     */
    public void start() throws Exception
    {
        this.getServer().start();
    }

    /**
     * @see org.apache.avalon.framework.activity.Startable#stop()
     */
    public void stop() throws Exception
    {
        this.getServer().stop();
    }

    /**
     * @see org.apache.avalon.framework.configuration.Reconfigurable#reconfigure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void reconfigure(Configuration configuration) throws ConfigurationException
    {
        if(configuration != null)
        {
            this.configure(configuration);
        }

        try
        {
            this.initialize();
        }
        catch(Exception e)
        {
            String msg = "Initializing the new server failed";
            throw new ConfigurationException(msg, e);
        }
    }

    /////////////////////////////////////////////////////////////////////////
    // Service Interface Implementation
    /////////////////////////////////////////////////////////////////////////

    public Server getServer()
    {
        return this.server;
    }

    /////////////////////////////////////////////////////////////////////////
    // Service Implementation
    /////////////////////////////////////////////////////////////////////////

    /**
     * Locate the configuration file using the file system or a classpath.
     *
     * @param applicationDir the directory where to start the search
     * @param location the location of the source to be loaded
     * @return the input stream of the resource
     * @throws IOException the operation failed
     */
    private InputStream locate( File applicationDir, String location ) throws IOException
    {
        if( ( location == null ) || ( location.length() == 0 ) )
        {
            throw new IllegalArgumentException("location is null or empty");
        }

        File file = null;
        InputStream is = null;

        // try to load a relative location with the given root dir
        // e.g. "jetty.xml" located in the current working directory

        if( !location.startsWith("/") )
        {
            file = new File( applicationDir, location );

            this.getLogger().debug("Looking for " + location + " in the application directory");

            if( file.exists() )
            {
                is = new FileInputStream( file );
                this.getLogger().debug("Found " + location + " as " + file.getAbsolutePath() );
            }
        }

        // try to load an absolute location as file
        // e.g. "/foo/jetty.xml" from the root of the file system

        if( ( is == null ) && (location.startsWith("/")) )
        {
            file = new File( location );

            this.getLogger().debug("Looking for " + location + " as absolute file location");

            if( file.isAbsolute() && file.exists() )
            {
                is = new FileInputStream( file );
                this.getLogger().debug("Found " + location + " as " + file.getAbsolutePath() );
            }
        }

        // try to load an absolute location through the classpath
        // e.g. "/jetty.xml" located in the classpath

        if( ( is == null ) && (location.startsWith("/")) )
        {
            this.getLogger().debug("Looking for " + location + " using the class loader");
            is =  getClass().getResourceAsStream( location );

            if( is != null )
            {
                this.getLogger().debug("Successfully located " + location);
            }
        }

        if( is == null )
        {
            this.getLogger().warn("Unable to find any resource with the name '" + location + "'");
        }

        return is;
    }
}