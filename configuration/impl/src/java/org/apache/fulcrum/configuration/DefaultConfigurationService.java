package org.apache.fulcrum.configuration;
/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 
import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.commons.configuration.ConfigurationFactory;

/**
 * Starts up a commons configuration Configuration object via an
 * Avalon container.
 * 
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @version $Id$
 * @avalon.component name="config" lifestyle="singleton"
 * @avalon.service type="org.apache.commons.configuration.Configuration"
 * @avalon.attribute key="urn:composition:deployment.timeout" value="0"
 */
public class DefaultConfigurationService
    extends AbstractLogEnabled
    implements org.apache.commons.configuration.Configuration, Configurable, Contextualizable, ThreadSafe
{
    /**
	 * The property specifying the location where to read in the configuration
	 * path from.
	 */
    String CONFIGURATION_PATH = "configurationPath";

    private String applicationRoot;

    private org.apache.commons.configuration.Configuration configuration;

    /**
	 * @see org.apache.commons.configuration.Configuration
	 */
    public void addProperty(String arg0, Object arg1)
    {
        configuration.addProperty(arg0, arg1);
    }

    /**
	 * @see org.apache.commons.configuration.Configuration
	 */
    public void clearProperty(String arg0)
    {
        configuration.clearProperty(arg0);
    }

    /**
	 * @see org.apache.commons.configuration.Configuration
	 * @return
	 */
    public boolean containsKey(String arg0)
    {
        return configuration.containsKey(arg0);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
    public boolean equals(Object obj)
    {
        return configuration.equals(obj);
    }

    /**
	 * @see org.apache.commons.configuration.Configuration
	 * @return
	 */
    public boolean getBoolean(String arg0)
    {
        return configuration.getBoolean(arg0);
    }

    /**
	 * @see org.apache.commons.configuration.Configuration
	 * @return
	 */
    public boolean getBoolean(String arg0, boolean arg1)
    {
        return configuration.getBoolean(arg0, arg1);
    }

    /**
	 * @see org.apache.commons.configuration.Configuration
	 * @return
	 */
    public Boolean getBoolean(String arg0, Boolean arg1)
    {
        return configuration.getBoolean(arg0, arg1);
    }

    /**
	 * @see org.apache.commons.configuration.Configuration
	 * @return
	 */
    public byte getByte(String arg0)
    {
        return configuration.getByte(arg0);
    }

    /**
	 * @see org.apache.commons.configuration.Configuration
	 * @return
	 */
    public byte getByte(String arg0, byte arg1)
    {
        return configuration.getByte(arg0, arg1);
    }

    /**
	 * @see org.apache.commons.configuration.Configuration
	 * @return
	 */
    public Byte getByte(String arg0, Byte arg1)
    {
        return configuration.getByte(arg0, arg1);
    }

    /**
	 * @see org.apache.commons.configuration.Configuration
	 * @return
	 */
    public double getDouble(String arg0)
    {
        return configuration.getDouble(arg0);
    }

    /**
	 * @see org.apache.commons.configuration.Configuration
	 * @return
	 */
    public double getDouble(String arg0, double arg1)
    {
        return configuration.getDouble(arg0, arg1);
    }

    /**
	 * @see org.apache.commons.configuration.Configuration
	 * @return
	 */
    public Double getDouble(String arg0, Double arg1)
    {
        return configuration.getDouble(arg0, arg1);
    }

    /**
	 * @see org.apache.commons.configuration.Configuration
	 * @return
	 */
    public float getFloat(String arg0)
    {
        return configuration.getFloat(arg0);
    }

    /**
	 * @see org.apache.commons.configuration.Configuration
	 * @return
	 */
    public float getFloat(String arg0, float arg1)
    {
        return configuration.getFloat(arg0, arg1);
    }

    /**
	 * @see org.apache.commons.configuration.Configuration
	 * @return
	 */
    public Float getFloat(String arg0, Float arg1)
    {
        return configuration.getFloat(arg0, arg1);
    }

    /**
	 * @see org.apache.commons.configuration.Configuration
	 * @return
	 */
    public int getInt(String arg0)
    {
        return configuration.getInt(arg0);
    }

    /**
	 * @see org.apache.commons.configuration.Configuration
	 * @return
	 */
    public int getInt(String arg0, int arg1)
    {
        return configuration.getInt(arg0, arg1);
    }

    /**
	 * @see org.apache.commons.configuration.Configuration
	 * @return
	 */
    public Integer getInteger(String arg0, Integer arg1)
    {
        return configuration.getInteger(arg0, arg1);
    }

    /**
     * @see org.apache.commons.configuration.Configuration
	 * @return
	 */
    public Iterator getKeys()
    {
        return configuration.getKeys();
    }

    /**
	 * @see org.apache.commons.configuration.Configuration
	 * @return
	 */
    public Iterator getKeys(String arg0)
    {
        return configuration.getKeys(arg0);
    }

    /**
	 * @see org.apache.commons.configuration.Configuration
	 * @return
	 */
    public long getLong(String arg0)
    {
        return configuration.getLong(arg0);
    }

    /**
	 * @see org.apache.commons.configuration.Configuration
	 * @return
	 */
    public Long getLong(String arg0, Long arg1)
    {
        return configuration.getLong(arg0, arg1);
    }

    /**
	 * @see org.apache.commons.configuration.Configuration
	 * @return
	 */
    public long getLong(String arg0, long arg1)
    {
        return configuration.getLong(arg0, arg1);
    }

    /**
	 * @see org.apache.commons.configuration.Configuration
	 * @return
	 */
    public Properties getProperties(String arg0)
    {
        return configuration.getProperties(arg0);
    }

    /**
	 * @see org.apache.commons.configuration.Configuration
	 * @return
	 */
    public Object getProperty(String arg0)
    {
        return configuration.getProperty(arg0);
    }

    /**
	 * @see org.apache.commons.configuration.Configuration
	 * @return
	 */
    public short getShort(String arg0)
    {
        return configuration.getShort(arg0);
    }

    /**
	 * @see org.apache.commons.configuration.Configuration
	 * @return
	 */
    public Short getShort(String arg0, Short arg1)
    {
        return configuration.getShort(arg0, arg1);
    }

    /**
	 * @see org.apache.commons.configuration.Configuration
	 * @return
	 */
    public short getShort(String arg0, short arg1)
    {
        return configuration.getShort(arg0, arg1);
    }

    /**
	 * @see org.apache.commons.configuration.Configuration
	 * @return
	 */
    public String getString(String arg0)
    {
        return configuration.getString(arg0);
    }

    /**
	 * @see org.apache.commons.configuration.Configuration
	 * @return
	 */
    public String getString(String arg0, String arg1)
    {
        return configuration.getString(arg0, arg1);
    }

    /**
	 * @see org.apache.commons.configuration.Configuration
	 * @return
	 */
    public String[] getStringArray(String arg0)
    {
        return configuration.getStringArray(arg0);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
    public int hashCode()
    {
        return configuration.hashCode();
    }

    /**
     * @see org.apache.commons.configuration.Configuration
	 * @return
	 */
    public boolean isEmpty()
    {
        return configuration.isEmpty();
    }

    /**
	 * @see org.apache.commons.configuration.Configuration
	 */
    public void setProperty(String arg0, Object arg1)
    {
        configuration.setProperty(arg0, arg1);
    }

    /**
	 * @see org.apache.commons.configuration.Configuration
	 */
    public org.apache.commons.configuration.Configuration subset(String arg0)
    {
        return configuration.subset(arg0);
    }

    /**
     * @param arg0
     * @return
     */
    public BigDecimal getBigDecimal(String arg0) {
        return configuration.getBigDecimal(arg0);
    }
    /**
     * @param arg0
     * @param arg1
     * @return
     */
    public BigDecimal getBigDecimal(String arg0, BigDecimal arg1) {
        return configuration.getBigDecimal(arg0, arg1);
    }
    /**
     * @param arg0
     * @return
     */
    public BigInteger getBigInteger(String arg0) {
        return configuration.getBigInteger(arg0);
    }
    /**
     * @param arg0
     * @param arg1
     * @return
     */
    public BigInteger getBigInteger(String arg0, BigInteger arg1) {
        return configuration.getBigInteger(arg0, arg1);
    }
    /**
     * @param arg0
     * @return
     */
    public List getList(String arg0) {
        return configuration.getList(arg0);
    }
    /**
     * @param arg0
     * @param arg1
     * @return
     */
    public List getList(String arg0, List arg1) {
        return configuration.getList(arg0, arg1);
    }
    /*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
    public String toString()
    {
        return configuration.toString();
    }

    /**
     * Avalon component lifecycle method
     */
    public void configure(Configuration conf) throws ConfigurationException
    {
        String confPath = conf.getAttribute(CONFIGURATION_PATH);
        /*
        File confFile = new File(confPath);
        if(!confFile.exists()){
            confFile = new File(applicationRoot,confPath);
        }
        */
		//System.out.println( "PATH: " + confPath );
		//System.out.println( "HOME: " + applicationRoot );
		File file = new File( applicationRoot, confPath );
		//System.out.println( "REAL: " + file.getAbsolutePath());

        
        if(!file.exists()){            
            throw new ConfigurationException("XML file for ConfigurationFactory can not be found:" +file.getAbsolutePath());
        }

        ConfigurationFactory configurationFactory =
            new ConfigurationFactory(file.getAbsolutePath());
        configurationFactory.setBasePath(applicationRoot);
        try
        {
            configuration = configurationFactory.getConfiguration();
        }
        catch (Exception e)
        {
            throw new ConfigurationException(
                "Problem loading Configuration with Factory.",
                e);
        }

    }

    /**
     * @see org.apache.avalon.framework.context.Contextualizable
     * @avalon.entry key="urn:avalon:home" type="java.io.File"
     */
    public void contextualize(Context context) throws ContextException
    {
        this.applicationRoot = context.get( "urn:avalon:home" ).toString();
    }

	public void clear() {
		configuration.clear();
		
	}

}
