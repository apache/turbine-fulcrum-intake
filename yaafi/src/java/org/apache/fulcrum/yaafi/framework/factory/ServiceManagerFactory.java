package org.apache.fulcrum.yaafi.framework.factory;

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

import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.logger.Logger;

import org.apache.fulcrum.yaafi.framework.container.ServiceConstants;
import org.apache.fulcrum.yaafi.framework.container.ServiceContainer;


/**
 * A factory to hide how to initialize YAFFI since this might change
 * over the time
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public class ServiceManagerFactory
{
    /** Our default implementation class of the service container */
    private static String serviceContainerClazzName = "org.apache.fulcrum.yaafi.framework.container.ServiceContainerImpl";

    /** Our logger */
    private static Logger logger;    
    
    static
    {
        // as default use the console logger         
        logger = new ConsoleLogger();
    }
    
    /**
     * Create a fully initialized YAFFI service container
     * 
     * @param logger the logger to use
     * @param componentRolesLocation the location of role definition file
     * @param componentConfigurationLocation the location of configuration file
     * @param parametersLocation The location of the additional parameters
     * @throws Exception the creation failed
     */
    public static ServiceContainer create(
        Logger logger,
        String componentRolesLocation,
        String componentConfigurationLocation,
        String parametersLocation)
    	throws Exception
    {
        return create(
            logger,
            componentRolesLocation,
            componentConfigurationLocation,
            parametersLocation,
            null
            );  
    }

    /**
     * Create a fully initialized YAFFI service container
     * 
     * @param logger the logger to use
     * @param componentRolesLocation the location of role definition file
     * @param componentConfigurationLocation the location of configuration file
     * @param parametersLocation The location of the additional parameters
     * @param context the context to pass
     * @throws Exception the creation failed
     */
    public static ServiceContainer create( 
        Logger logger,
        String componentRolesLocation,
        String componentConfigurationLocation,
        String parametersLocation,
        Context context)
    	throws Exception
    {    
        Class clazz = null;
        ServiceContainer result = null;
        
        // Enforce a logger from the caller
        
        if( logger == null )
        {
            String msg = "An instance of a logger is required";
            ServiceManagerFactory.logger.error(msg);
            throw new IllegalArgumentException(msg);
        }
        
        try
        {
            // bootstrap the logging
            
            ServiceManagerFactory.logger = logger;
            ServiceManagerFactory.logger.debug( "Loading the service container class " + serviceContainerClazzName );
            
            // bootstrap the service container
            
            clazz = ServiceManagerFactory.class.getClassLoader().loadClass(serviceContainerClazzName);
            ServiceManagerFactory.logger.debug( "Instantiating the service container class " + serviceContainerClazzName );
            result = (ServiceContainer) clazz.newInstance();
        }
        catch( Exception e )
        {
            String msg = "Creating the ServiceContainer failed";
            ServiceManagerFactory.logger.error( msg, e );
            throw e;
        }
        
        // LogEnabled.enableLogging() to set the logger 
        
        Logger serviceContainerLogger = logger;
        
        result.enableLogging( serviceContainerLogger );
        
        // Contextualizable.contextualize() to set the context
        
        if( context != null )
        {
            result.contextualize(context);
        }
        
        // Configurable.configure() to set the configurattion files
        
        DefaultConfiguration configuration = new DefaultConfiguration(
            ServiceConstants.ROLE_NAME
            );
        
        configuration = createConfiguration(
            componentRolesLocation,
            componentConfigurationLocation,
            parametersLocation );
            
        result.configure( configuration );
        
        // Initializable.initialie() to start the container
        
        result.initialize();
        
        return result;
    }

    /**
     * Create a default configuration (i.e. XML) with the given parameters.
     * 
     * @param componentRolesLocation the location of role definition file
     * @param componentConfigurationLocation the location of configuration file
     * @param parametersLocation The location of the additional parameters 
     */
    private static DefaultConfiguration createConfiguration(
        String componentRolesLocation,
        String componentConfigurationLocation,
        String parametersLocation )
    {
        DefaultConfiguration result = new DefaultConfiguration(
            ServiceConstants.ROLE_NAME
            );
        
        // 1) componentRolesLocation
        
        DefaultConfiguration componentRolesLocationConfig = new DefaultConfiguration(
            ServiceConstants.COMPONENT_ROLE_KEYS
            );
           
        componentRolesLocationConfig.setValue( 
            componentRolesLocation 
            );
        
        result.addChild( componentRolesLocationConfig );
            
        // 2) componentConfigurationLocation
        
        DefaultConfiguration componentConfigurationLocationConfig = new DefaultConfiguration(
            ServiceConstants.COMPONENT_CONFIG_KEY
            );
           
        componentConfigurationLocationConfig.setValue( 
            componentConfigurationLocation 
            );
        
        result.addChild( componentConfigurationLocationConfig );
        
        // 3) parametersLocation

        DefaultConfiguration parametersLocationConfig = new DefaultConfiguration(
            ServiceConstants.COMPONENT_PARAMETERS_KEY
            );
           
        parametersLocationConfig.setValue( 
            parametersLocation 
            );
        
        result.addChild( parametersLocationConfig );

        return result;
    }    
}
