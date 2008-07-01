package org.apache.fulcrum.yaafi.framework.factory;

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

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationUtil;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.logger.Logger;
import org.apache.fulcrum.yaafi.framework.container.ServiceConstants;
import org.apache.fulcrum.yaafi.framework.container.ServiceContainer;
import org.apache.fulcrum.yaafi.framework.util.Validate;

/**
 * A factory to hide how to initialize YAFFI since this might change over the time
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl </a>
 */

public class ServiceContainerFactory
{
    /** The logger to be used */
    private static Logger logger;

    /**
     * Create a fully initialized YAFFI service container.
     *
     * @param serviceManagerConfig the configuration to use
     * @return the service container
     * @throws Exception the creation failed
     */
    public static ServiceContainer create(
        ServiceContainerConfiguration serviceManagerConfig)
        throws Exception
    {
        Validate.notNull(serviceManagerConfig,"serviceManagerConfig");
        Context context = serviceManagerConfig.createFinalContext();
        return ServiceContainerFactory.create( serviceManagerConfig, context );
    }

    /**
     * Create a fully initialized YAFFI service container
     *
     * @param serviceManagerConfig the configuration to use
     * @param context the context to use
     * @return the service container
     * @throws Exception the creation failed
     */
    public static ServiceContainer create(
        ServiceContainerConfiguration serviceManagerConfig, Context context )
        throws Exception
    {
        Validate.notNull(serviceManagerConfig,"serviceManagerConfig");
        Validate.notNull(context,"context");

        String clazzName;
        Class clazz = null;
        Configuration configuration = null;
        ServiceContainer result = null;

        // Enforce a logger from the caller

        try
        {
            // bootstrap the logging

            ServiceContainerFactory.logger = serviceManagerConfig.getLogger();

            // bootstrap the configuration settings

            configuration = serviceManagerConfig.createFinalConfiguration();

            // bootstrap the service container

            clazzName = getServiceContainerClazzName(configuration);

            ServiceContainerFactory.logger.debug(
                "Loading the service container class " + clazzName
                );

            clazz = ServiceContainerFactory.class.getClassLoader().loadClass(
                clazzName
                );

            ServiceContainerFactory.logger.debug(
                "Instantiating the service container class " + clazzName
                );

            result = (ServiceContainer) clazz.newInstance();
        }
        catch (Exception e)
        {
            String msg = "Creating the ServiceContainer failed";
            ServiceContainerFactory.logger.error( msg, e );
            throw e;
        }

        Logger serviceContainerLogger = serviceManagerConfig.getLogger();

        serviceContainerLogger.debug(
            "Using the following configuration : "
            + ConfigurationUtil.toString( configuration )
            );

        ContainerUtil.enableLogging( result, serviceManagerConfig.getLogger() );
        ContainerUtil.contextualize( result, context );

        if(serviceManagerConfig.getParentServiceManager() != null)
        {
            ContainerUtil.service(result, serviceManagerConfig.getParentServiceManager());
        }
        
        ContainerUtil.configure( result, configuration );
        ContainerUtil.initialize( result );

        return result;
    }

    /**
     * Disposes the container.
     *
     * @param container the container to be disposed
     * @return true if the disposal was successful or false otherwise
     */
    public static boolean dispose( ServiceContainer container )
    {
        try
        {
            if( container != null )
            {
                container.dispose();
            }

            return true;
        }
        catch( Throwable t )
        {
            String msg = "Disposing the container failed : " + t.getMessage();
            System.err.println(msg);
            t.printStackTrace();
            return false;
        }
    }

    /**
     * Reads the implementation class of the YAAFI container.
     *
     * @param configuration the Avalon configuration
     * @return the implementation class name of the container
     */
    private static String getServiceContainerClazzName( Configuration configuration )
    {
        Configuration containerClazzNameConfig = configuration.getChild(
            ServiceConstants.CONTAINERCLAZZNAME_CONFIG_KEY
            );

        if( containerClazzNameConfig != null )
        {
            return containerClazzNameConfig.getValue(ServiceConstants.CLAZZ_NAME);
        }
        else
        {
            return ServiceConstants.CLAZZ_NAME;
        }
    }

}
