package org.apache.fulcrum.yaafi.cli;

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
import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.fulcrum.yaafi.framework.container.ServiceContainer;
import org.apache.fulcrum.yaafi.framework.factory.ServiceContainerConfiguration;
import org.apache.fulcrum.yaafi.framework.factory.ServiceContainerFactory;

/**
 * An example of the embedding of a YAAFI kernel inside an
 * arbitrary application.
 */

public class Main implements Runnable, Disposable
{
    /** parameter for the application name */
    public static final String APPLICATION_NAME = "yaafi.cli.applicationName";

    /** parameter for the application home directory */
    public static final String APPLICATION_HOME = "yaafi.cli.applicationHome";

    /** parameter for the application temporary directory */
    public static final String APPLICATION_TEMP = "yaafi.cli.tempHome";

    /** parameter for the application container configuration file */
    public static final String APPLICATION_CONFIG = "yaafi.cli.config";

    /** parameter for setting a shutdown hook */
    public static final String APPLICATION_HASSHUTDOWNHOOK = "yaafi.cli.hasShutdownHook";

    /** parameter for blocking the main thread in Main.run() */
    public static final String APPLICATION_ISBLOCKING = "yaafi.cli.isBlocking";

    /** the interval to check for termination */
    private static final int SLEEP_TIME = 100;

    /** the timeout for joining the shutdown thread */
    private static final int JOIN_TIME = 1000;

    /** The service manager */
    private ServiceContainer container;

    /** The location of the container configuration */
    private String containerConfigValue;

    /** Thread for processing the shutdown notification of the JVM */
    private Thread shutdownThread;

    /** Do we block the invoking thread until the JVM terminates ?! */
    private boolean isBlocking;

    /** Do we install a shutdown hook for the JVM ?! */
    private boolean hasShutdownHook;

    /** The logger being used */
    private Logger logger;

    /** the name of the application */
    private String applicationName;

    /** the working directory */
    private String applicationHome;

    /** the temp directory */
    private String tempHome;

    /** the command line arguments */
    private String[] args;

    /** is the instance properly initialized */
    private volatile boolean isInitialized;


    /**
     * Constructor
     */
    public Main()
    {
        // default initialization

        this.containerConfigValue   = "./conf/containerConfiguration.xml";
        this.logger                 = new ConsoleLogger();
        this.applicationHome        = ".";
        this.tempHome               = System.getProperty("java.io.tmpdir",".");
        this.applicationName        = "main";
        
        // Arguments are specified in the constructor, but if 
        // null, set to an empty string array
        if ( this.args == null ) { this.args = new String[0]; }

        this.isBlocking             = false;
        this.hasShutdownHook        = true;
        this.isInitialized          = false;

        // query the system properties

        this.containerConfigValue = System.getProperty(
                APPLICATION_CONFIG,
                this.containerConfigValue
                );

        this.applicationName = System.getProperty(
                APPLICATION_NAME,
                this.applicationName
                );

        this.applicationHome = System.getProperty(
            APPLICATION_HOME,
            this.applicationHome
            );

        this.tempHome = System.getProperty(
            APPLICATION_TEMP,
            this.tempHome
            );
    }

    /**
     * Constructor
     *
     * The following command line parameters are supported
     * <ul>
     *   <li>--yaafi.cli.applicationName name</li>
     *   <li>--yaafi.cli.applicationHome dir</li>
     *   <li>--yaafi.cli.tempHome dir</li>
     *   <li>--yaafi.cli.isBlocking [true|false]</li>
     *   <li>--yaafi.cli.hasShutdownHook [true|false]</li>
     *   <li>--yaafi.cli.config file</li>
     * </ul>
     *
     * @param args the command line arguments
     */
    public Main( String[] args )
    {
        this();

        this.args = args;

        // parse the command line

        Getopt getopt = new Getopt(this.args);

        this.setApplicationName(
            getopt.getStringValue( APPLICATION_NAME, this.getApplicationName() )
            );

        this.setApplicationHome(
            getopt.getStringValue( APPLICATION_HOME, this.getApplicationHome() )
            );

        this.setTempHome(
            getopt.getStringValue( APPLICATION_TEMP, this.getTempHome() )
            );

        this.setContainerConfigValue(
            getopt.getStringValue( APPLICATION_CONFIG, this.getContainerConfigValue() )
            );

        this.setIsBlocking(
            getopt.getBooleanValue( APPLICATION_ISBLOCKING, this.isBlocking )
            );

        this.setHasShutdownHook(
            getopt.getBooleanValue( APPLICATION_HASSHUTDOWNHOOK, this.hasShutdownHook )
            );
    }

    /**
     * The main method.
     *
     * @param args Command line arguments
     * @throws Exception the execution failed
     */
    public static void main( String[] args ) throws Exception
    {
       int exitCode = 0;

       Main impl = new Main(args);

       try
       {
           impl.run();
       }
       catch (Throwable t)
       {
           exitCode = 1;
       }

       System.exit(exitCode);
    }

    /**
     * Determines the file location of the given name. If the name denotes
     * a relative file location it will be resolved using the application
     * home directory.
     *
     * @param baseDir the base directory
     * @param name the filename
     * @return the file
     */
    public static File makeAbsoluteFile( File baseDir, String name )
    {
        File result = new File(name);

        if( !result.isAbsolute() )
        {
            result = new File( baseDir, name );
        }

        return result;
    }

    /**
     * Dispose the YAAFI container
     */

    public synchronized void dispose()
    {
        this.shutdown();
    }

    /**
     * Runs the instance by initializing it and potentially blocking
     * the invoking thread depending on the configuration.
     *
     * @see java.lang.Runnable#run()
     */
    public void run()
    {
        try
        {
            this.initialize();
            this.onWait();
        }
        catch (Throwable t)
        {
            String msg = "Failed to run " + this.getClass().getName();
            this.getLogger().error(msg,t);
            throw new RuntimeException(t.getMessage());
        }
    }

    /**
     * Depending on the configuration this method might block
     * the calling thread or return immediately. We currently
     * poll a volatile variable which is not the most elegant
     * solution.
     */
    public void onWait()
    {
        while( this.isBlocking() && this.isInitialized() )
        {
            try
            {
                Thread.sleep(Main.SLEEP_TIME);
            }
            catch (InterruptedException e)
            {
                // ignore
            }
        }
    }

    /**
     * Locates the file for the given file name.
     * @param fileName the filename
     * @return an absolute file
     */
    public File makeAbsoluteFile( String fileName )
    {
        return Main.makeAbsoluteFile(
            new File(this.getApplicationHome()),
            fileName
            );
    }

    /**
     * Locates the file for the given file name.
     * @param fileName the filename
     * @return an absolute path
     */
    public String makeAbsolutePath( String fileName )
    {
        return Main.makeAbsoluteFile(
            new File(this.getApplicationHome()),
            fileName
            ).getAbsolutePath();
    }

    /////////////////////////////////////////////////////////////////////////
    // Generated getters & setters
    /////////////////////////////////////////////////////////////////////////

    /**
     * @return Returns the ServiceContainer interface
     */
    public ServiceContainer getServiceContainer()
    {
        return this.container;
    }

    /**
     * @return Returns the ServiceManager interface
     */
    public ServiceManager getServiceManager()
    {
        return this.container;
    }

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
     * @return Returns the isBlocking.
     */
    public boolean isBlocking()
    {
        return isBlocking;
    }

    /**
     * @param isBlocking The isBlocking to set.
     */
    public void setIsBlocking(boolean isBlocking)
    {
        this.isBlocking = isBlocking;
    }

    /**
     * @param isBlocking The isBlocking to set.
     */
    public void setIsBlocking(Boolean isBlocking)
    {
        this.isBlocking = isBlocking.booleanValue();
    }

    /**
     * @param isBlocking The isBlocking to set.
     */
    public void setIsBlocking(String isBlocking)
    {
        this.isBlocking = Boolean.valueOf(isBlocking).booleanValue();
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

    /**
     * @return Returns the applicationName.
     */
    public String getApplicationName()
    {
        return applicationName;
    }

    /**
     * @param applicationName The applicationName to set.
     */
    public void setApplicationName(String applicationName)
    {
        this.applicationName = applicationName;
    }

    /**
     * @return Returns the args.
     */
    public String [] getArgs()
    {
        return args;
    }
    /**
     * @param args The args to set.
     */
    public void setArgs(String [] args)
    {
        this.args = args;
    }

    /**
     * @return Returns the hasShutdownHook.
     */
    public boolean hasShutdownHook()
    {
        return hasShutdownHook;
    }

    /**
     * @param hasShutdownHook The hasShutdownHook to set.
     */
    public void setHasShutdownHook(boolean hasShutdownHook)
    {
        this.hasShutdownHook = hasShutdownHook;
    }

    /**
     * @param hasShutdownHook The hasShutdownHook to set.
     */
    public void setHasShutdownHook(Boolean hasShutdownHook)
    {
        this.hasShutdownHook = hasShutdownHook.booleanValue();
    }

    /**
     * @param hasShutdownHook The hasShutdownHook to set.
     */
    public void setHasShutdownHook(String hasShutdownHook)
    {
        this.hasShutdownHook = Boolean.valueOf(hasShutdownHook).booleanValue();
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        StringBuilder argsLine = new StringBuilder();

        result.append(getClass().getName() + "@" + Integer.toHexString(hashCode()));

        result.append('[');
        result.append("workingDir=" + new File("").getAbsolutePath());
        result.append(',');

        result.append("args=");

        for( int i=0; i<this.getArgs().length; i++ )
        {
            argsLine.append( this.getArgs()[i] );

            if( (i+1) < this.getArgs().length )
            {
                argsLine.append( " " );
            }
        }

        result.append( argsLine.toString() );
        result.append(',');

        result.append("applicationName=" + this.getApplicationName());
        result.append(',');
        result.append("applicationHome=" + this.getApplicationHome());
        result.append(',');
        result.append("tempHome=" + this.getTempHome());
        result.append(',');
        result.append("logger=" + this.getLogger().getClass().getName());
        result.append(',');
        result.append("isBlocking=" + this.isBlocking);
        result.append(',');
        result.append("hasShutdownHook=" + this.hasShutdownHook());
        result.append(',');
        result.append("containerConfigValue=" + this.getContainerConfigValue());
        result.append(']');

        return result.toString();
    }

    /**
     * @return Returns the isInitialized.
     */
    public boolean isInitialized()
    {
        return isInitialized;
    }

    /////////////////////////////////////////////////////////////////////////
    // Implementation
    /////////////////////////////////////////////////////////////////////////

    /**
     * @param isInitialized The isInitialized to set.
     */
    protected void setInitialized(boolean isInitialized)
    {
        this.isInitialized = isInitialized;
    }

    /**
     * Initialize the instance
     *
     * @throws Exception the initialization failed
     */
    public void initialize() throws Exception
    {
        this.getLogger().debug( "Initializing " + this.getClass().getName() );

        ServiceContainerConfiguration config = new ServiceContainerConfiguration();

        // initialize the Avalon container

        config.setLogger( this.getLogger() );
        config.setApplicationRootDir( this.getApplicationHome() );
        config.setTempRootDir( this.getTempHome() );
        config.loadContainerConfiguration( this.getContainerConfigValue(), "auto" );

        this.container = ServiceContainerFactory.create( config );

        // initialize shutdown hook of JVM for a server application

        if( this.hasShutdownHook() )
        {
            this.getLogger().debug( "Registering shutdown hook" );
            Shutdown shutdown = new Shutdown( this );
            this.shutdownThread = new Thread( shutdown, "ShutdownThread" );
            Runtime.getRuntime().addShutdownHook( this.shutdownThread );
        }

        this.setInitialized(true);
    }

    /**
     * Terminates the instance
     */
    protected void shutdown()
    {
        if( !this.isInitialized())
        {
            return;
        }

        this.getLogger().debug( "Terminating " + this.getClass().getName() );

        try
        {
            // wait for the shutdown thread

            if( this.shutdownThread != null )
            {
                try
                {
                    this.getLogger().debug( "Waiting for shutdown handler thread to terminate" );
                    this.shutdownThread.join(JOIN_TIME);
                    this.shutdownThread = null;
                    this.getLogger().debug( "Shutdown handler thread is terminated" );
                }
                catch (InterruptedException e)
                {
                    // nothing to do
                }
            }

            // dispose the service container

            if( this.getServiceContainer() != null )
            {
                this.getServiceContainer().dispose();
                this.container = null;
            }

            this.setInitialized(false);
        }

        catch (Exception e)
        {
            String msg = "Failed to terminate " + this.getClass().getName();
            this.getLogger().error(msg,e);
        }
    }
}
