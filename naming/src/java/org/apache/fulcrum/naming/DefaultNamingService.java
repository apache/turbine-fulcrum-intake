package org.apache.fulcrum.naming;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Turbine" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Turbine", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
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

            throw new Exception("Failed to initialize JDNI contexts!");
        }
    }

  
}
