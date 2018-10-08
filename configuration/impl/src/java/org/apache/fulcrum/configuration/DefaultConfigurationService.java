package org.apache.fulcrum.configuration;
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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.DefaultConfigurationBuilder;
import org.apache.commons.configuration.JNDIConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SystemConfiguration;
import org.apache.commons.configuration.XMLConfiguration;

/**
 * Starts up a commons configuration Configuration object via an
 * Avalon container.
 *
 *
 * The component configuration is carved after the
 * <a href="http://commons.apache.org/configuration/howto_configurationfactory.html">CompositeConfiguraton</a>
 * from Commons Configuration and allows similar expressions. The implementation understands the
 * following types of configurations:<br>
 * <pre>
 *
 * &lt;ConfigurationService&gt;
 *    &lt;system/&gt;
 *    &lt;jndi prefix="java:comp/env"/&gt;
 *    &lt;properties fileName="test.properties" optional="true"/&gt;
 *    &lt;xml fileName="test.xml" optional="true"/&gt;
 * &lt;/ConfigurationService&gt;
 *
 * </pre>
 * All other XML elements are considered to be keys of a configuration and their contents is
 * treated as string value.
 *
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 * @version $Id$
 * @avalon.component name="config" lifestyle="singleton"
 * @avalon.service type="org.apache.commons.configuration.Configuration"
 * @avalon.attribute key="urn:composition:deployment.timeout" value="0"
 *
 */
public class DefaultConfigurationService
    extends AbstractLogEnabled
    implements org.apache.commons.configuration.Configuration, Configurable, Contextualizable, ThreadSafe
{
    /**
     * The property specifying the location where to read in the configuration
     * path from.
     */
    private static final String CONFIGURATION_PATH = "configurationPath";

    private String applicationRoot;

    private CompositeConfiguration configuration;

    /**
     * @see org.apache.commons.configuration.Configuration#addProperty(java.lang.String, java.lang.Object)
     */
    @Override
	public void addProperty(String arg0, Object arg1)
    {
        configuration.addProperty(arg0, arg1);
    }

    /**
     * @see org.apache.commons.configuration.Configuration#clearProperty(java.lang.String)
     */
    @Override
	public void clearProperty(String arg0)
    {
        configuration.clearProperty(arg0);
    }

    /**
     * @see org.apache.commons.configuration.Configuration#containsKey(java.lang.String)
     */
    @Override
	public boolean containsKey(String arg0)
    {
        return configuration.containsKey(arg0);
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
	@Override
	public boolean equals(Object obj)
    {
        if (obj == configuration) {
            return true;
          }
          if (obj == null) {
            return false;
          }
          if (configuration.getClass() == obj.getClass()) {
            return configuration.equals(((CompositeConfiguration)obj));
          }
          return false;    	
    }

    /**
     * @see org.apache.commons.configuration.Configuration#getBoolean(java.lang.String)
     */
    @Override
	public boolean getBoolean(String arg0)
    {
        return configuration.getBoolean(arg0);
    }

    /**
     * @see org.apache.commons.configuration.Configuration#getBoolean(java.lang.String, boolean)
     */
    @Override
	public boolean getBoolean(String arg0, boolean arg1)
    {
        return configuration.getBoolean(arg0, arg1);
    }

    /**
     * @see org.apache.commons.configuration.Configuration#getBoolean(java.lang.String, java.lang.Boolean)
     */
    @Override
	public Boolean getBoolean(String arg0, Boolean arg1)
    {
        return configuration.getBoolean(arg0, arg1);
    }

    /**
     * @see org.apache.commons.configuration.Configuration#getByte(java.lang.String)
     */
    @Override
	public byte getByte(String arg0)
    {
        return configuration.getByte(arg0);
    }

    /**
     * @see org.apache.commons.configuration.Configuration#getByte(java.lang.String, byte)
     */
    @Override
	public byte getByte(String arg0, byte arg1)
    {
        return configuration.getByte(arg0, arg1);
    }

    /**
     * @see org.apache.commons.configuration.Configuration#getByte(java.lang.String, java.lang.Byte)
     */
    @Override
	public Byte getByte(String arg0, Byte arg1)
    {
        return configuration.getByte(arg0, arg1);
    }

    /**
     * @see org.apache.commons.configuration.Configuration#getDouble(java.lang.String)
     */
    @Override
	public double getDouble(String arg0)
    {
        return configuration.getDouble(arg0);
    }

    /**
     * @see org.apache.commons.configuration.Configuration#getDouble(java.lang.String, double)
     */
    @Override
	public double getDouble(String arg0, double arg1)
    {
        return configuration.getDouble(arg0, arg1);
    }

    /**
     * @see org.apache.commons.configuration.Configuration#getDouble(java.lang.String, java.lang.Double)
     */
    @Override
	public Double getDouble(String arg0, Double arg1)
    {
        return configuration.getDouble(arg0, arg1);
    }

    /**
     * @see org.apache.commons.configuration.Configuration#getFloat(java.lang.String)
     */
    @Override
	public float getFloat(String arg0)
    {
        return configuration.getFloat(arg0);
    }

    /**
     * @see org.apache.commons.configuration.Configuration#getFloat(java.lang.String, float)
     */
    @Override
	public float getFloat(String arg0, float arg1)
    {
        return configuration.getFloat(arg0, arg1);
    }

    /**
     * @see org.apache.commons.configuration.Configuration#getFloat(java.lang.String, java.lang.Float)
     */
    @Override
	public Float getFloat(String arg0, Float arg1)
    {
        return configuration.getFloat(arg0, arg1);
    }

    /**
     * @see org.apache.commons.configuration.Configuration#getInt(java.lang.String)
     */
    @Override
	public int getInt(String arg0)
    {
        return configuration.getInt(arg0);
    }

    /**
     * @see org.apache.commons.configuration.Configuration#getInt(java.lang.String, int)
     */
    @Override
	public int getInt(String arg0, int arg1)
    {
        return configuration.getInt(arg0, arg1);
    }

    /**
     * @see org.apache.commons.configuration.Configuration#getInteger(java.lang.String, java.lang.Integer)
     */
    @Override
	public Integer getInteger(String arg0, Integer arg1)
    {
        return configuration.getInteger(arg0, arg1);
    }

    /**
     * @see org.apache.commons.configuration.Configuration#getKeys()
     */
    @Override
	public Iterator<String> getKeys()
    {
        return configuration.getKeys();
    }

    /**
     * @see org.apache.commons.configuration.Configuration#getKeys(java.lang.String)
     */
    @Override
	public Iterator<String> getKeys(String arg0)
    {
        return configuration.getKeys(arg0);
    }

    /**
     * @see org.apache.commons.configuration.Configuration#getLong(java.lang.String)
     */
    @Override
	public long getLong(String arg0)
    {
        return configuration.getLong(arg0);
    }

    /**
     * @see org.apache.commons.configuration.Configuration#getLong(java.lang.String, java.lang.Long)
     */
    @Override
	public Long getLong(String arg0, Long arg1)
    {
        return configuration.getLong(arg0, arg1);
    }

    /**
     * @see org.apache.commons.configuration.Configuration#getLong(java.lang.String, long)
     */
    @Override
	public long getLong(String arg0, long arg1)
    {
        return configuration.getLong(arg0, arg1);
    }

    /**
     * @see org.apache.commons.configuration.Configuration#getProperties(java.lang.String)
     */
    @Override
	public Properties getProperties(String arg0)
    {
        return configuration.getProperties(arg0);
    }

    /**
     * @see org.apache.commons.configuration.Configuration#getProperty(java.lang.String)
     */
    @Override
	public Object getProperty(String arg0)
    {
        return configuration.getProperty(arg0);
    }

    /**
     * @see org.apache.commons.configuration.Configuration#getShort(java.lang.String)
     */
    @Override
	public short getShort(String arg0)
    {
        return configuration.getShort(arg0);
    }

    /**
     * @see org.apache.commons.configuration.Configuration#getShort(java.lang.String, java.lang.Short)
     */
    @Override
	public Short getShort(String arg0, Short arg1)
    {
        return configuration.getShort(arg0, arg1);
    }

    /**
     * @see org.apache.commons.configuration.Configuration#getShort(java.lang.String, short)
     */
    @Override
	public short getShort(String arg0, short arg1)
    {
        return configuration.getShort(arg0, arg1);
    }

    /**
     * @see org.apache.commons.configuration.Configuration#getString(java.lang.String)
     */
    @Override
	public String getString(String arg0)
    {
        return configuration.getString(arg0);
    }

    /**
     * @see org.apache.commons.configuration.Configuration#getString(java.lang.String, java.lang.String)
     */
    @Override
	public String getString(String arg0, String arg1)
    {
        return configuration.getString(arg0, arg1);
    }

    /**
     * @see org.apache.commons.configuration.Configuration#getStringArray(java.lang.String)
     */
    @Override
	public String[] getStringArray(String arg0)
    {
        return configuration.getStringArray(arg0);
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
	public int hashCode()
    {
        return configuration.hashCode();
    }

    /**
     * @see org.apache.commons.configuration.Configuration#isEmpty()
     */
    @Override
	public boolean isEmpty()
    {
        return configuration.isEmpty();
    }

    /**
     * @see org.apache.commons.configuration.Configuration#setProperty(java.lang.String, java.lang.Object)
     */
    @Override
	public void setProperty(String arg0, Object arg1)
    {
        configuration.setProperty(arg0, arg1);
    }

    /**
     * @see org.apache.commons.configuration.Configuration#subset(java.lang.String)
     */
    @Override
	public org.apache.commons.configuration.Configuration subset(String arg0)
    {
        return configuration.subset(arg0);
    }

    /**
     * @see org.apache.commons.configuration.Configuration#getBigDecimal(java.lang.String)
     */
    @Override
	public BigDecimal getBigDecimal(String arg0)
    {
        return configuration.getBigDecimal(arg0);
    }

    /**
     * @see org.apache.commons.configuration.Configuration#getBigDecimal(java.lang.String, java.math.BigDecimal)
     */
    @Override
	public BigDecimal getBigDecimal(String arg0, BigDecimal arg1)
    {
        return configuration.getBigDecimal(arg0, arg1);
    }

    /**
     * @see org.apache.commons.configuration.Configuration#getBigInteger(java.lang.String)
     */
    @Override
	public BigInteger getBigInteger(String arg0)
    {
        return configuration.getBigInteger(arg0);
    }

    /**
     * @see org.apache.commons.configuration.Configuration#getBigInteger(java.lang.String, java.math.BigInteger)
     */
    @Override
	public BigInteger getBigInteger(String arg0, BigInteger arg1)
    {
        return configuration.getBigInteger(arg0, arg1);
    }

    /**
     * @see org.apache.commons.configuration.Configuration#getList(java.lang.String)
     */
    @Override
	public List<Object> getList(String arg0)
    {
        return configuration.getList(arg0);
    }

    /**
     * @see org.apache.commons.configuration.Configuration#getList(java.lang.String, java.util.List)
     */
    @Override
	public List<Object> getList(String arg0, List<?> arg1)
    {
        return configuration.getList(arg0, arg1);
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
	public String toString()
    {
        return configuration.toString();
    }

    /**
     * @see org.apache.commons.configuration.Configuration#clear()
     */
    @Override
	public void clear()
    {
        configuration.clear();
    }

    /**
     * Avalon component lifecycle method
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    @Override
	public void configure(Configuration conf) throws ConfigurationException
    {
        configuration = new CompositeConfiguration();

        // for backward compatibility
        String confPath = conf.getAttribute(CONFIGURATION_PATH, null);

        Configuration cfgs[] = conf.getChildren();

        if ((cfgs == null || cfgs.length == 0) && (confPath == null || confPath.length() == 0))
        {
            throw new ConfigurationException("Configuration should not be empty", conf);
        }

        if (confPath != null && confPath.length() > 0)
        {
            File file = new File( applicationRoot, confPath );

            if(!file.exists())
            {
                throw new ConfigurationException("XML file for ConfigurationFactory can not be found:" +file.getAbsolutePath());
            }

            DefaultConfigurationBuilder configurationBuilder = new DefaultConfigurationBuilder(file);
            configurationBuilder.setConfigurationBasePath(applicationRoot);
            try
            {
                configuration.addConfiguration(configurationBuilder.getConfiguration());
            }
            catch (Exception e)
            {
                throw new ConfigurationException(
                    "Problem loading Configuration with Factory.",
                    e);
            }
        }

        for (int i=0; i<cfgs.length; i++)
        {
            Configuration c = cfgs[i];
            String name = c.getName();

            if (name.equals("jndi"))
            {
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
                    confFile = new File(applicationRoot, fileName);
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
                    confFile = new File(applicationRoot, fileName);
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

    /**
     * @see org.apache.avalon.framework.context.Contextualizable#contextualize(org.apache.avalon.framework.context.Context)
     * @avalon.entry key="urn:avalon:home" type="java.io.File"
     */
    @Override
	public void contextualize(Context context) throws ContextException
    {
        try
        {
            // first try Merlin and Yaafi context entries
            applicationRoot = ((File)context.get("urn:avalon:home")).getAbsolutePath();
        }
        catch (ContextException e)
        {
            // Context entry not found
            applicationRoot = null;
        }

        if (applicationRoot == null || applicationRoot.length() == 0)
        {
            // try ECM, let exception throw on failure
            applicationRoot = (String)context.get("componentAppRoot");
        }

        if (applicationRoot == null || applicationRoot.length() == 0)
        {
            throw new ContextException("Invalid Application Root");
        }
	}
}
