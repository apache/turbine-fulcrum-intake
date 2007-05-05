package org.apache.fulcrum.naming;


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


import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.thread.ThreadSafe;

/**
 * This class is the default implementation of NamingService, which
 * provides JNDI naming contexts.
 *
 * @author <a href="mailto:greg@shwoop.com">Greg Ritter</a>
 * @author <a href="mailto:colin.chalmers@maxware.nl">Colin Chalmers</a>
 * @version $Id$
 */
public class DefaultNamingService
    extends AbstractLogEnabled
    implements NamingService, Configurable, Initializable, ThreadSafe
{
    /**
     * A global HashTable of Property objects which are initialised using
     * parameters from the ResourcesFile
     */
    private static Hashtable contextPropsList = null;

    private Hashtable initialContexts = new Hashtable();

    /**
      * Return the Context with the specified name.
      *
      * @param name The name of the context.
      * @return The context with the specified name, or null if no
      * context exists with that name.
      */
    public Context getContext(String contextName)
    {
        // Get just the properties for the context with the specified
        // name.
        Properties contextProps = null;

        if (contextPropsList.containsKey(contextName))
        {
            contextProps = (Properties) contextPropsList.get(contextName);
        }
        else
        {
            contextProps = new Properties();
        }

        // Construct a new context with the properties.
        try
        {
            return new InitialContext(contextProps);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    // ---------------- Avalon Lifecycle Methods ---------------------

    /**
     * Avalon component lifecycle method
     */
    public void configure(Configuration conf) throws ConfigurationException
    {

        contextPropsList = new Hashtable();
        Configuration[] keys = conf.getChildren();
        if (keys != null)
        {
            for (int i = 0; i < keys.length; i++)
            {
                String contextName = keys[i].getName();
                Properties contextProps =
                    toProperties(Parameters.fromConfiguration(keys[i]));

                contextPropsList.put(contextName, contextProps);
            }
        }

    }

    /**
     * Note: this is copied from avalon's Parameter class, which does not
     * appear to have the method in the version I'm compiling against.
     * Creates a <code>java.util.Properties</code> object from an Avalon
     * Parameters object.
     *
     * @param params a <code>Parameters</code> instance
     * @return a <code>Properties</code> instance
     */
    private static Properties toProperties(final Parameters params)
    {
        final Properties properties = new Properties();
        final String[] names = params.getNames();

        for (int i = 0; i < names.length; ++i)
        {
            // "" is the default value, since getNames() proves it will exist
            properties.setProperty(names[i], params.getParameter(names[i], ""));
        }

        return properties;
    }

    /**
     * Avalon component lifecycle method
     */
    public void initialize() throws Exception
    {
        try
        {
            Enumeration contextPropsKeys = contextPropsList.keys();
            while (contextPropsKeys.hasMoreElements())
            {
                String key = (String) contextPropsKeys.nextElement();
                Properties contextProps =
                    (Properties) contextPropsList.get(key);
                InitialContext context = new InitialContext(contextProps);
                initialContexts.put(key, context);
            }

        }
        catch (Exception e)
        {
            getLogger().error("Failed to initialize JDNI contexts!", e);

            throw new Exception("Failed to initialize JDNI contexts!",e);
        }
    }


}
