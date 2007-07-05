package org.apache.fulcrum.yaafi.framework.configuration;

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
import java.util.Iterator;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.JNDIConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SystemConfiguration;
import org.apache.commons.configuration.XMLConfiguration;

/**
 * A configuration resolver that uses commons-configuration to lookup
 * replacement values for the YAAFI component configuration
 *
 * The component configuration is carved after the
 * <a href="http://jakarta.apache.org/commons/configuration/howto_configurationfactory.html">CompositeConfiguraton</a>
 * from Commons Configuration and allows similar expressions. The implementation understands the
 * following types of configurations:<br>
 * <pre>
 *
 * &lt;componentConfigurationProperties&gt;
 *    &lt;resolver&gt;org.apache.fulcrum.yaafi.framework.configuration.CommonsConfigurationCCPResolver&lt;/resolver&gt;
 *    &lt;system/&gt;
 *    &lt;jndi prefix="java:comp/env"/&gt;
 *    &lt;properties fileName="test.properties" optional="true"/&gt;
 *    &lt;xml fileName="test.xml" optional="true"/&gt;
 * &lt;/componentConfigurationProperties&gt;
 *
 * </pre>
 * All other XML elements are considered to be keys of a configuration and their contents is
 * treated as string value.
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 */
public class CommonsConfigurationCCPResolver extends
    ComponentConfigurationPropertiesResolverBaseImpl
{
    /** Merged configuration from all configuration sources */
    private CompositeConfiguration configuration = null;

    /**
     * @see org.apache.fulcrum.yaafi.framework.configuration.ComponentConfigurationPropertiesResolverBaseImpl#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration conf) throws ConfigurationException
    {
        super.configure(conf);

        configuration = new CompositeConfiguration();

        Configuration cfgs[] = conf.getChildren();

        if ((cfgs != null && cfgs.length > 0))
        {
            for (int i=0; i<cfgs.length; i++)
            {
                Configuration c = cfgs[i];
                String name = c.getName();

                if (name.equals("resolver"))
                {
                    // skip resolver entry
                    continue;
                }
                else if (name.equals("jndi"))
                {
                    // <jndi prefix="java:comp/env"/>
                    String jndi_context = c.getAttribute("prefix", null);

                    if (jndi_context == null || jndi_context.length() == 0)
                    {
                        throw new ConfigurationException("JNDI Configuration should have a prefix", c);
                    }

                    try
                    {
                        InitialContext ctx = new InitialContext();
                        JNDIConfiguration jndi = new JNDIConfiguration(ctx, jndi_context);
                        configuration.addConfiguration(jndi);
                    }
                    catch (NamingException e)
                    {
                        throw new ConfigurationException("Could not create JNDI context", c, e);
                    }
                }
                else if (name.equals("properties"))
                {
                    String fileName = c.getAttribute("fileName", null);
                    boolean optional = c.getAttributeAsBoolean("optional", false);

                    if (fileName == null || fileName.length() == 0)
                    {
                        throw new ConfigurationException("Property Configuration should have a file name", c);
                    }

                    File confFile = new File(fileName);
                    if(!confFile.exists())
                    {
                        confFile = new File(getApplicationRootDir(), fileName);
                    }

                    if (!confFile.exists() && !optional)
                    {
                        throw new ConfigurationException("Property Configuration file " + fileName + " does not exist", c);
                    }

                    if (confFile.exists())
                    {
                        try
                        {
                            PropertiesConfiguration prop = new PropertiesConfiguration(confFile);
                            configuration.addConfiguration(prop);
                        }
                        catch (org.apache.commons.configuration.ConfigurationException e)
                        {
                            throw new ConfigurationException("Property Configuration could not be created", c, e);
                        }
                    }
                }
                else if (name.equals("xml"))
                {
                    String fileName = c.getAttribute("fileName", null);
                    boolean optional = c.getAttributeAsBoolean("optional", false);

                    if (fileName == null || fileName.length() == 0)
                    {
                        throw new ConfigurationException("XML Configuration should have a file name", c);
                    }

                    File confFile = new File(fileName);
                    if(!confFile.exists())
                    {
                        confFile = new File(getApplicationRootDir(), fileName);
                    }

                    if (!confFile.exists() && !optional)
                    {
                        throw new ConfigurationException("XML Configuration file " + fileName + " does not exist", c);
                    }

                    if (confFile.exists())
                    {
                        try
                        {
                            XMLConfiguration xml = new XMLConfiguration(confFile);
                            configuration.addConfiguration(xml);
                        }
                        catch (org.apache.commons.configuration.ConfigurationException e)
                        {
                            throw new ConfigurationException("XML Configuration could not be created", c, e);
                        }
                    }
                }
                else if (name.equals("system"))
                {
                    SystemConfiguration system = new SystemConfiguration();
                    configuration.addConfiguration(system);
                }
    /*
                else if (name.equals("database"))
                {
                    // <database table="config" keyColumn="col_key" valueColumn="col_value" />
                    String table = c.getAttribute("table", null);
                    String keyColumn = c.getAttribute("keyColumn", null);
                    String valueColumn = c.getAttribute("valueColumn", null);
                }
    */
                else
                {
                    String value = c.getValue();

                    if (value != null)
                    {
                        configuration.addProperty(name, value);
                    }
                    else
                    {
                        throw new ConfigurationException("Invalid configuration key: " + name, c);
                    }
                }
            }
        }
    }

    /**
     * @see org.apache.fulcrum.yaafi.framework.configuration.ComponentConfigurationPropertiesResolver#resolve(java.util.Properties)
     */
    public Properties resolve(Properties defaults) throws Exception
    {
        Properties result = new Properties();

        /* Add defaults if they are available */
        if (defaults != null)
        {
            result.putAll(defaults);
        }

        /* Add Avalon context entries */
        addAvalonContext(result);

        /* Add the complete set of key/value pairs of the CompositeConfiguration */
        if (configuration != null)
        {
            for (Iterator i = configuration.getKeys(); i.hasNext();)
            {
                String key = (String)i.next();
                String value = configuration.getString(key);

                result.setProperty(key, value);
            }
        }

        return result;
    }
}
