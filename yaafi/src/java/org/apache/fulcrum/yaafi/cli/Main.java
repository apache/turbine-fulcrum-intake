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

import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.logger.Logger;
import org.apache.fulcrum.yaafi.framework.container.ServiceConstants;
import org.apache.fulcrum.yaafi.framework.container.ServiceContainer;
import org.apache.fulcrum.yaafi.framework.container.ServiceContainerImpl;
import org.apache.fulcrum.yaafi.framework.factory.ServiceContainerFactory;
import org.apache.fulcrum.yaafi.framework.factory.ServiceContainerConfiguration;


/**
 * An example of the embedding of a merlin kernel inside a main
 * method.  The objective of the example is to demonstrate a
 * simple embedded scenario.
 */

public class Main
{
    /** The service manager */
    private ServiceContainer manager;
    
    /** The name of the block config file */
    private String componentRoleValue;
    
    /** The name of the component config file */
	private String componentConfigValue;

	/** The name of the parameters file */
	private String componentParametersValue;

	/** Thread for processing the shutdown notification of the JVM */
	private Thread shutdownThread;
	
	/** Do we terminate the main thread ?! */
	private boolean isServerMode;
	
	/** The logger being used */
	private Logger logger;
    
	/**
	 * Constructor
	 */
    private Main()
    {
        this.isServerMode				= false;
        this.componentRoleValue 		= ServiceConstants.COMPONENT_ROLE_VALUE;
        this.componentConfigValue 		= ServiceConstants.COMPONENT_CONFIG_VALUE;
        this.componentParametersValue	= ServiceConstants.COMPONENT_PARAMETERS_VALUE;
                
        this.logger = new ConsoleLogger( ConsoleLogger.LEVEL_DEBUG );
    }

    /**
     * The main method
     * @param args Command line arguments
     * @throws Exception
     */
    public static void main( String[] args ) throws Exception
    {
       Main impl = new Main();
       
       // Initialize the service manager
       
       impl.initialize();             
       
       boolean terminateNow = ( impl.isServerMode ? false : true );
       
       while( terminateNow == false )
       {
           try
           {
               Thread.sleep(1000);
           }
    	   catch (InterruptedException e)
    	   {
    	       terminateNow = true;
    	   }
	   }
       
       impl.dispose();
    }
    
    protected void initialize() throws Exception
    {
        ServiceContainerConfiguration config = new ServiceContainerConfiguration();
        
        // intialize the service manager
        
        config.setLogger( new ConsoleLogger() );
        config.setComponentRolesLocation( this.componentRoleValue );
        config.setComponentConfigurationLocation( this.componentConfigValue );
        config.setParametersLocation( this.componentParametersValue );
        
        this.manager = ServiceContainerFactory.create(
            config
            );
             
        // initialize shutdown hoook of JVM
        
        Shutdown shutdown = new Shutdown( this.getManager(), this.getLogger() );
        this.shutdownThread = new Thread( shutdown );
        Runtime.getRuntime().addShutdownHook( this.shutdownThread );		       
    }

    protected synchronized void dispose() throws Exception
    {
        if( this.getManager() != null )
        {
            this.getManager().dispose();
        }
    }

    /**
     * @return Returns the logger.
     */
    protected Logger getLogger()
    {
        return this.logger;
    }

    /////////////////////////////////////////////////////////////////////////
    // Generated getters & setters
    /////////////////////////////////////////////////////////////////////////
    
    /**
     * @return Returns the manager.
     */
    public ServiceContainer getManager()
    {
        return this.manager;
    }
    /**
     * @param manager The manager to set.
     */
    public void setManager(ServiceContainerImpl manager)
    {
        this.manager = manager;
    }
    /**
     * @return Returns the componentConfigValue.
     */
    public String getComponentConfigValue()
    {
        return this.componentConfigValue;
    }
    /**
     * @param componentConfigValue The componentConfigValue to set.
     */
    public void setComponentConfigValue(String componentConfigValue)
    {
        this.componentConfigValue = componentConfigValue;
    }
    /**
     * @return Returns the componentRoleValue.
     */
    public String getComponentRoleValue()
    {
        return this.componentRoleValue;
    }
    /**
     * @param componentRoleValue The componentRoleValue to set.
     */
    public void setComponentRoleValue(String componentRoleValue)
    {
        this.componentRoleValue = componentRoleValue;
    }
}
