package org.apache.fulcrum.configuration;
/*
 * ==================================================================== The
 * Apache Software License, Version 1.1
 * 
 * Copyright (c) 2001 The Apache Software Foundation. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 1.
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. The end-user documentation
 * included with the redistribution, if any, must include the following
 * acknowledgment: "This product includes software developed by the Apache
 * Software Foundation (http://www.apache.org/)." Alternately, this
 * acknowledgment may appear in the software itself, if and wherever such
 * third-party acknowledgments normally appear. 4. The names "Apache" and
 * "Apache Software Foundation" and "Apache Turbine" must not be used to
 * endorse or promote products derived from this software without prior written
 * permission. For written permission, please contact apache@apache.org. 5.
 * Products derived from this software may not be called "Apache", "Apache
 * Turbine", nor may "Apache" appear in their name, without prior written
 * permission of the Apache Software Foundation.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * APACHE SOFTWARE FOUNDATION OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 * 
 * This software consists of voluntary contributions made by many individuals
 * on behalf of the Apache Software Foundation. For more information on the
 * Apache Software Foundation, please see <http://www.apache.org/> .
 */
import java.io.File;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.commons.configuration.ConfigurationFactory;

/**
 * Starts up a commons configuration Configuration object via the
 * 
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 *  
 */
public class DefaultConfigurationService
    extends AbstractLogEnabled
    implements Config, Component, Configurable, Contextualizable
{
    /**
	 * The property specifying the location where to read in the configuration
	 * path from.
	 */
    String CONFIGURATION_PATH = "configurationPath";

    /** The Avalon Context */
    private Context context = null;

    private String applicationRoot;

    private org.apache.commons.configuration.Configuration configuration;

    /**
	 * @param arg0
	 * @param arg1
	 */
    public void addProperty(String arg0, Object arg1)
    {
        configuration.addProperty(arg0, arg1);
    }

    /**
	 * @param arg0
	 */
    public void clearProperty(String arg0)
    {
        configuration.clearProperty(arg0);
    }

    /**
	 * @param arg0
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
	 * @param arg0
	 * @return
	 */
    public boolean getBoolean(String arg0)
    {
        return configuration.getBoolean(arg0);
    }

    /**
	 * @param arg0
	 * @param arg1
	 * @return
	 */
    public boolean getBoolean(String arg0, boolean arg1)
    {
        return configuration.getBoolean(arg0, arg1);
    }

    /**
	 * @param arg0
	 * @param arg1
	 * @return
	 */
    public Boolean getBoolean(String arg0, Boolean arg1)
    {
        return configuration.getBoolean(arg0, arg1);
    }

    /**
	 * @param arg0
	 * @return
	 */
    public byte getByte(String arg0)
    {
        return configuration.getByte(arg0);
    }

    /**
	 * @param arg0
	 * @param arg1
	 * @return
	 */
    public byte getByte(String arg0, byte arg1)
    {
        return configuration.getByte(arg0, arg1);
    }

    /**
	 * @param arg0
	 * @param arg1
	 * @return
	 */
    public Byte getByte(String arg0, Byte arg1)
    {
        return configuration.getByte(arg0, arg1);
    }

    /**
	 * @param arg0
	 * @return
	 */
    public double getDouble(String arg0)
    {
        return configuration.getDouble(arg0);
    }

    /**
	 * @param arg0
	 * @param arg1
	 * @return
	 */
    public double getDouble(String arg0, double arg1)
    {
        return configuration.getDouble(arg0, arg1);
    }

    /**
	 * @param arg0
	 * @param arg1
	 * @return
	 */
    public Double getDouble(String arg0, Double arg1)
    {
        return configuration.getDouble(arg0, arg1);
    }

    /**
	 * @param arg0
	 * @return
	 */
    public float getFloat(String arg0)
    {
        return configuration.getFloat(arg0);
    }

    /**
	 * @param arg0
	 * @param arg1
	 * @return
	 */
    public float getFloat(String arg0, float arg1)
    {
        return configuration.getFloat(arg0, arg1);
    }

    /**
	 * @param arg0
	 * @param arg1
	 * @return
	 */
    public Float getFloat(String arg0, Float arg1)
    {
        return configuration.getFloat(arg0, arg1);
    }

    /**
	 * @param arg0
	 * @return
	 */
    public int getInt(String arg0)
    {
        return configuration.getInt(arg0);
    }

    /**
	 * @param arg0
	 * @param arg1
	 * @return
	 */
    public int getInt(String arg0, int arg1)
    {
        return configuration.getInt(arg0, arg1);
    }

    /**
	 * @param arg0
	 * @param arg1
	 * @return
	 */
    public Integer getInteger(String arg0, Integer arg1)
    {
        return configuration.getInteger(arg0, arg1);
    }

    /**
	 * @return
	 */
    public Iterator getKeys()
    {
        return configuration.getKeys();
    }

    /**
	 * @param arg0
	 * @return
	 */
    public Iterator getKeys(String arg0)
    {
        return configuration.getKeys(arg0);
    }

    /**
	 * @param arg0
	 * @return
	 */
    public long getLong(String arg0)
    {
        return configuration.getLong(arg0);
    }

    /**
	 * @param arg0
	 * @param arg1
	 * @return
	 */
    public Long getLong(String arg0, Long arg1)
    {
        return configuration.getLong(arg0, arg1);
    }

    /**
	 * @param arg0
	 * @param arg1
	 * @return
	 */
    public long getLong(String arg0, long arg1)
    {
        return configuration.getLong(arg0, arg1);
    }

    /**
	 * @param arg0
	 * @return
	 */
    public Properties getProperties(String arg0)
    {
        return configuration.getProperties(arg0);
    }

    /**
	 * @param arg0
	 * @return
	 */
    public Object getProperty(String arg0)
    {
        return configuration.getProperty(arg0);
    }

    /**
	 * @param arg0
	 * @return
	 */
    public short getShort(String arg0)
    {
        return configuration.getShort(arg0);
    }

    /**
	 * @param arg0
	 * @param arg1
	 * @return
	 */
    public Short getShort(String arg0, Short arg1)
    {
        return configuration.getShort(arg0, arg1);
    }

    /**
	 * @param arg0
	 * @param arg1
	 * @return
	 */
    public short getShort(String arg0, short arg1)
    {
        return configuration.getShort(arg0, arg1);
    }

    /**
	 * @param arg0
	 * @return
	 */
    public String getString(String arg0)
    {
        return configuration.getString(arg0);
    }

    /**
	 * @param arg0
	 * @param arg1
	 * @return
	 */
    public String getString(String arg0, String arg1)
    {
        return configuration.getString(arg0, arg1);
    }

    /**
	 * @param arg0
	 * @return
	 */
    public String[] getStringArray(String arg0)
    {
        return configuration.getStringArray(arg0);
    }

    /**
	 * @param arg0
	 * @return
	 */
    public Vector getVector(String arg0)
    {
        return configuration.getVector(arg0);
    }

    /**
	 * @param arg0
	 * @param arg1
	 * @return
	 */
    public Vector getVector(String arg0, Vector arg1)
    {
        return configuration.getVector(arg0, arg1);
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
	 * @return
	 */
    public boolean isEmpty()
    {
        return configuration.isEmpty();
    }

    /**
	 * @param arg0
	 * @param arg1
	 */
    public void setProperty(String arg0, Object arg1)
    {
        configuration.setProperty(arg0, arg1);
    }

    /**
	 * @param arg0
	 * @return
	 */
    public org.apache.commons.configuration.Configuration subset(String arg0)
    {
        return configuration.subset(arg0);
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
        try
        {
            applicationRoot =
                (context == null)
                    ? null
                    : (String) context.get("componentAppRoot");
        }
        catch (ContextException ce)
        {
            getLogger().error("Could not load Application Root from Context");
        }

        String confPath = conf.getAttribute(CONFIGURATION_PATH);
        File confFile = new File(confPath);
        if(!confFile.exists()){
            confFile = new File(applicationRoot,confPath);
        }
        if(!confFile.exists()){            
            throw new ConfigurationException("XML file for ConfigurationFactory can not be found:" +confFile);
        }

        ConfigurationFactory configurationFactory =
            new ConfigurationFactory(confFile.getAbsolutePath());
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
	 */
    public void contextualize(Context context) throws ContextException
    {
        this.context = context;
    }

}
