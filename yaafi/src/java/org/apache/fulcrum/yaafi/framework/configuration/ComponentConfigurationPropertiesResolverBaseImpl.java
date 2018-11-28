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
package org.apache.fulcrum.yaafi.framework.configuration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.fulcrum.yaafi.framework.constant.AvalonYaafiConstants;
import org.apache.fulcrum.yaafi.framework.util.InputStreamLocator;

/**
 * Base class to expand the value and all attributes. This class is intentend
 * to be sub-classed if you hook up your own configuration mechanism.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */
public abstract class ComponentConfigurationPropertiesResolverBaseImpl
	implements ComponentConfigurationPropertiesResolver, LogEnabled, Contextualizable, Configurable
{
    /** the logger of the container */
    private Logger logger;

    /** the Avalon context */
    private Context context;

    /** the container configuration */
    private Configuration configuration;

    /**
     * @see org.apache.avalon.framework.logger.LogEnabled#enableLogging(org.apache.avalon.framework.logger.Logger)
     * @param logger the logger instance 
     */
    public void enableLogging(Logger logger)
    {
        this.logger = logger;
    }

    /**
     * @see org.apache.avalon.framework.context.Contextualizable#contextualize(org.apache.avalon.framework.context.Context)
     * @param context the Context to add
     */
    public void contextualize(Context context) throws ContextException
    {
        this.context = context;
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     * @param configuration the configuration object to use
     */
    public void configure(Configuration configuration) throws ConfigurationException
    {
        this.configuration = configuration;
    }

    /**
     * @return Returns the logger.
     */
    protected Logger getLogger()
    {
        return logger;
    }

    /**
     * @return Returns the context.
     */
    protected Context getContext()
    {
        return context;
    }

    /**
     * @return the home directory of the application
     */
    protected File getApplicationRootDir()
    {
        try
        {
            return (File) this.getContext().get(AvalonYaafiConstants.URN_AVALON_HOME);
        }
        catch(Exception e)
        {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * @return Returns the configuration.
     */
    protected Configuration getConfiguration()
    {
        return configuration;
    }

    /**
     * @return Returns the componentConfigurationPropertiesLocation.
     */
    protected String getLocation()
    {
        return configuration.getChild("location").getValue(COMPONENT_CONFIG_PROPERTIES_VALUE );
    }

    /**
     * Creates an InputStream using a Locator.
     * @return the InputStrem or null if the resource was not found
     * @param location the location of the file
     * @throws IOException if file not found
     */
    protected InputStream createInputStream(String location) throws IOException
    {
        InputStreamLocator locator = new InputStreamLocator(this.getApplicationRootDir(), this.getLogger());
        return locator.locate(location);
    }

    /**
     * Add the Avalon context variables.
     * @param properties properties to be set
     * @throws ContextException if context not found
     */
    protected void addAvalonContext(Properties properties) throws ContextException
    {
        properties.put(
            AvalonYaafiConstants.URN_AVALON_NAME,
            this.getContext().get(AvalonYaafiConstants.URN_AVALON_NAME)
            );

        properties.put(
            AvalonYaafiConstants.URN_AVALON_PARTITION,
            this.getContext().get(AvalonYaafiConstants.URN_AVALON_PARTITION)
            );

        properties.put(
            AvalonYaafiConstants.URN_AVALON_HOME,
            this.getContext().get(AvalonYaafiConstants.URN_AVALON_HOME)
            );

        properties.put(
            AvalonYaafiConstants.URN_AVALON_TEMP,
            this.getContext().get(AvalonYaafiConstants.URN_AVALON_TEMP)
            );
    }

    
    /**
     * Set properties from a file location
     * @param fileLocation file location of properties properties to be set
     * @return the properties
     * @throws Exception if unable to parse the properties file
     */
    protected Properties loadProperties(String fileLocation) throws Exception
    {
        Properties result = new Properties();
        InputStream is = this.createInputStream(fileLocation);

        try
        {
            if(is != null)
            {
		        result.load(is);
		        is.close();
		        is = null;
            }
            else
            {
                this.getLogger().debug("Unable to load the following optional file :" + fileLocation);
            }

            return result;
        }
        catch ( Exception e )
        {
            String msg = "Unable to parse the following file : " + fileLocation;
            this.getLogger().error( msg , e );
            throw e;
        }
    }
}
