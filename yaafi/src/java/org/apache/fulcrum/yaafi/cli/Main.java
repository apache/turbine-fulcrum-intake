package org.apache.fulcrum.yaafi.cli;

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

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.logger.Logger;
import org.apache.fulcrum.yaafi.framework.container.ServiceContainer;
import org.apache.fulcrum.yaafi.framework.factory.ServiceContainerConfiguration;
import org.apache.fulcrum.yaafi.framework.factory.ServiceContainerFactory;


/**
 * An example of the embedding of a merlin kernel inside a main
 * method.  The objective of the example is to demonstrate a
 * simple embedded scenario.
 */

public class Main implements Runnable, Disposable
{
    /** The service manager */
    private ServiceContainer container;
    
    /** The location of the container configuration */
	private String containerConfigValue;    
    
	/** Thread for processing the shutdown notification of the JVM */
	private Thread shutdownThread;
	
	/** Do we terminate the main thread ?! */
	private boolean isServerMode;
	
	/** The logger being used */
	private Logger logger;
	
	/** the interval to check for termination */
	private int sleepTime = 100; 
    
	/** the name of the application */
	private String applicationName;
	
	/** the working directory */
	private File applicationHome;

	/** the temp directory */
	private File tempHome;	 
	
	/** is the instance properly initialized */
	private boolean isInitialized;
	
	/**
	 * Constructor
	 */
    public Main()
    {
        this.containerConfigValue   = "./conf/containerConfiguration.xml";
        this.isServerMode			= true;   
        this.logger 				= new ConsoleLogger();
        this.applicationHome 		= new File( new File("").getAbsolutePath() );
        this.tempHome				= new File( System.getProperty("java.io.tmpdir",".") );
        this.applicationName		= "yaafi.cli";
    }

    /**
     * The main method
     * @param args Command line arguments
     * @throws Exception
     */
    public static void main( String[] args ) throws Exception
    {
       int exitCode = 0;
       
       Main impl = new Main();
       
       try
	   {
	       impl.run();
	   }
	   catch (Throwable t)
	   {
	       exitCode = 1;
	   }
	   finally
	   {
	       System.exit(exitCode);
	   }
    }
        
    /**
     * Dispose the YAAFI container
     */

    public synchronized void dispose()
    {
        this.shutdown();
    }        
   
    /**
     * @see java.lang.Runnable#run()
     */
    public void run()
    {
        try
        {
            this.initialize();             

            if( this.isServerMode() == false )
            {
                this.shutdown();
            }                                                
        }        
        catch (Throwable t)
        {
            String msg = "Failed to run " + this.getClass().getName(); 
            this.getLogger().error(msg,t);
            throw new RuntimeException(t.getMessage());
        }        
    }
    
    /////////////////////////////////////////////////////////////////////////
    // Generated getters & setters
    /////////////////////////////////////////////////////////////////////////
    
    /**
     * @return Returns the manager.
     */
    public ServiceContainer getServiceContainer()
    {
        return this.container;
    }
        
    /**
     * @return Returns the applicationHome.
     */
    public File getApplicationHome()
    {
        return applicationHome;
    }
    
    /**
     * @param applicationHome The applicationHome to set.
     */
    public void setApplicationHome(File applicationHome)
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
     * @return Returns the isServerMode.
     */
    public boolean isServerMode()
    {
        return isServerMode;
    }
    
    /**
     * @param isServerMode The isServerMode to set.
     */
    public void setServerMode(boolean isServerMode)
    {
        this.isServerMode = isServerMode;
    }
    
    /**
     * @return Returns the tempHome.
     */
    public File getTempHome()
    {
        return tempHome;
    }
    
    /**
     * @param tempHome The tempHome to set.
     */
    public void setTempHome(File tempHome)
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
     * @return Returns the isInitialized.
     */
    protected boolean isInitialized()
    {
        return isInitialized;
    }
    
    /**
     * @param isInitialized The isInitialized to set.
     */
    protected void setInitialized(boolean isInitialized)
    {
        this.isInitialized = isInitialized;
    }
    
    /////////////////////////////////////////////////////////////////////////
    // Implementation
    /////////////////////////////////////////////////////////////////////////

    /**
     * Initialize the instance
     * 
     * @throws Exception the initialization failed
     */
    protected void initialize() throws Exception
    {       
        this.getLogger().info( "Initializing " + this.getClass().getName() );
        
        ServiceContainerConfiguration config = new ServiceContainerConfiguration();
        
        // intialize the Avalon container
                                
        config.setLogger( this.getLogger() );
        config.setApplicationRootDir( this.getApplicationHome() );
        config.setTempRootDir( this.getTempHome() );
        config.loadContainerConfiguration( this.getContainerConfigValue(), "auto" );
        this.container = ServiceContainerFactory.create( config );            
             
        // initialize shutdown hook of JVM for a server application

        if( this.isServerMode() )
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
     * 
     * @throws Exception the termination failed
     */
    protected void shutdown()
    {
        if( this.isInitialized() == false )
        {
            return;
        }
        
        this.getLogger().info( "Terminating " + this.getClass().getName() );

        try
        {            
            // wait for the shutdown thread
            
            if( this.shutdownThread != null )
            {
                try
                {
                    this.getLogger().debug( "Waiting for shutdown handler thread to terminate" );
                    this.shutdownThread.join(1000);
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
