package org.apache.fulcrum.xmlrpc;


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


import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.thread.ThreadSafe;

import org.apache.xmlrpc.WebServer;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.secure.SecureWebServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Default implementation of the server-side XML RPC component.
 *
 * @todo Handle XmlRpc.setDebug(boolean)
 *
 * @avalon.component version="1.0" name="xmlrpc-server" lifestyle="singleton"
 * @avalon.service   version="1.0" type="org.apache.fulcrum.xmlrpc.XmlRpcServerComponent"
 *
 * @author <a href="mailto:jason@zenplex.com">Jason van Zyl</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 */
public class DefaultXmlRpcServerComponent
    extends AbstractXmlRpcComponent
    implements Startable, Disposable, Serviceable, ThreadSafe, XmlRpcServerComponent
{
    /** The service manager for this component. */
    private ServiceManager manager;

    /**  The standalone xmlrpc server. */
    private WebServer webserver;

    /** The port to listen on. */
    private int port;

    /** Secure server state. */
    private boolean isSecureServer;

    /** Paranoid state. */
    private boolean isStateOfParanoia;

    /** Handlers. */
    private Configuration handlerConfiguration;

    /** Accepted Clients. */
    private Configuration[] acceptedClients;

    /** Denied Clients. */
    private Configuration[] deniedClients;

    /** Default Constructor. */
    public DefaultXmlRpcServerComponent()
    {
        // nothing to do
    }

    // ----------------------------------------------------------------------
    // Lifecycle Management
    // ----------------------------------------------------------------------

    public void configure(Configuration configuration)
        throws ConfigurationException
    {
        super.configure(configuration);

        // Set the port for the service
        // Need a default value here and make sure we have a valid
        // value or throw a config exception.
        port = configuration.getChild("port").getValueAsInteger();
        getLogger().debug("Server Port: " + port);

        // Determine if the server is secure or not.
        isSecureServer =
                configuration.getChild("secureServer").getValueAsBoolean(false);
        getLogger().debug("Secure Server: " + isSecureServer);

        // Turn on paranoia for the webserver if requested.
        isStateOfParanoia =
                configuration.getChild("paranoid").getValueAsBoolean(false);
        //!! default value

        // Check if there are any handlers to register at startup
        handlerConfiguration = configuration.getChild("handlers");

        // Set the list of clients that can connect
        // to the xmlrpc server. The accepted client list
        // will only be consulted if we are paranoid.
        acceptedClients = configuration.getChildren("acceptedClients");

        // Set the list of clients that can connect
        // to the xmlrpc server. The denied client list
        // will only be consulted if we are paranoid.
        deniedClients = configuration.getChildren("deniedClients");
    }

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     * @avalon.dependency key="handler.0" type="com.apache.xmlrpc.XmlRpcHandler" optional="true"
     * @avalon.dependency key="handler.1" type="com.apache.xmlrpc.XmlRpcHandler" optional="true"
     * @avalon.dependency key="handler.2" type="com.apache.xmlrpc.XmlRpcHandler" optional="true"
     * @avalon.dependency key="handler.3" type="com.apache.xmlrpc.XmlRpcHandler" optional="true"
     * @avalon.dependency key="handler.4" type="com.apache.xmlrpc.XmlRpcHandler" optional="true"
     * @avalon.dependency key="handler.5" type="com.apache.xmlrpc.XmlRpcHandler" optional="true"
     * @avalon.dependency key="handler.6" type="com.apache.xmlrpc.XmlRpcHandler" optional="true"
     * @avalon.dependency key="handler.7" type="com.apache.xmlrpc.XmlRpcHandler" optional="true"
     * @avalon.dependency key="handler.8" type="com.apache.xmlrpc.XmlRpcHandler" optional="true"
     * @avalon.dependency key="handler.9" type="com.apache.xmlrpc.XmlRpcHandler" optional="true"
     */
    public void service(ServiceManager manager)
        throws ServiceException
    {
        this.manager = manager;
    }

    // ------------------------------------------------------------------------
    // S T A R T A B L E
    // ------------------------------------------------------------------------

    public void start()
        throws Exception
    {
        getLogger().debug( "Starting the XML-RPC server." );
        webserver.start();
    }

    /**
     * This function initializes the XmlRpcService.
     */
    public void initialize()
        throws Exception
    {
        super.initialize();

        getLogger().info( "Attempting to initialize the XML-RPC server." );

        // Need a default value here.
        if (isSecureServer)
        {
            webserver = new SecureWebServer(port);
        }
        else
        {
            webserver = new WebServer(port);
        }

        registerStartupHandlers();

        if (isStateOfParanoia)
        {
            webserver.setParanoid(isStateOfParanoia);

            getLogger().info("Operating in a state of paranoia");

            // Only set the accept/deny client lists if we
            // are in a state of paranoia as they will just
            // be ignored so there's no point in setting them.

            for (int i = 0; i < acceptedClients.length; i++)
            {
                Configuration acceptedClient = acceptedClients[i];
                String clientIP = acceptedClient.getAttribute("clientIP");

                if (clientIP != null && ! clientIP.equals(""))
                {
                    webserver.acceptClient(clientIP);

                    getLogger().info("Accepting client -> " + clientIP);
                }
            }

            for (int i = 0; i < deniedClients.length; i++)
            {
                Configuration deniedClient = deniedClients[i];
                String clientIP = deniedClient.getAttribute("clientIP");

                if (clientIP != null && ! clientIP.equals(""))
                {
                    webserver.denyClient(clientIP);

                    getLogger().info("Denying client -> " + clientIP);
                }
            }
        }
    }

    /**
     * Registers any handlers that were defined as part this component's
     * configuration.  A handler may be defined as a class (which will be
     * instantiated) or as a role in which case it will be looked up.
     *
     * @throws Exception If there were errors registering a handler.
     */
    private void registerStartupHandlers() throws Exception
    {
        Configuration[] handlers = handlerConfiguration.getChildren("handler");

        getLogger().info( "We have " + handlers.length
                + " handlers to configure." );

        for (int i = 0; i < handlers.length; i++)
        {
            Configuration c = handlers[i];

            if (c.getName().equals("handler"))
            {
                String handlerName = c.getChild("name").getValue();
                String handlerClass = c.getChild("class").getValue(null);
                String handlerRole = c.getChild("role").getValue(null);

                if (handlerClass != null && handlerRole == null)
                {
                    registerClassHandler(handlerName, handlerClass);
                }
                else if (handlerRole != null && handlerClass == null)
                {
                    registerComponentHandler(handlerName, handlerRole);
                }
                else
                {
                    throw new ConfigurationException(
                        "Handler must define either a 'class' or 'role'");
                }
            }
        }
    }

    /**
     * Shuts down this service, stopping running threads.
     */
    public void stop()
        throws Exception
    {
        getLogger().debug( "Stopping the XML-RPC server." );

        // Stop the XML RPC server.  org.apache.xmlrpc.WebServer blocks in a
        // call to ServerSocket.accept() until a socket connection is made.
        webserver.shutdown();
        try
        {
            Socket interrupt = new Socket(InetAddress.getLocalHost(), port);
            interrupt.close();
        }
        catch (Exception notShutdown)
        {
            // Remotely possible we're leaving an open listener socket around.
            getLogger().warn(
                    "It's possible the xmlrpc server was not shutdown: "
                    + notShutdown.getMessage());
        }
    }

    // ------------------------------------------------------------------------
    // D I S P O S A B L E
    // ------------------------------------------------------------------------

    /**
     * Unregisters all handlers and disposes of the server.
     */
    public void dispose()
    {
        Configuration[] handlers = handlerConfiguration.getChildren("handler");
        for (int i = 0; i < handlers.length; i++)
        {
            Configuration c = handlers[i];
            if (c.getName().equals("handler"))
            {
                unregisterHandler(c.getChild("name").getValue(""));
            }
        }

        webserver = null;
    }

    // ------------------------------------------------------------------------
    // I M P L E M E N A T I O N
    // ------------------------------------------------------------------------

    /**
     * Register an Object as a default handler for the service.
     *
     * @param handler The handler to use.
     * @exception XmlRpcException
     * @exception IOException
     */
    public void registerHandler(Object handler)
        throws XmlRpcException, IOException
    {
        registerHandler("$default", handler);
    }

    /**
     * Register an Object as a handler for the service.
     *
     * @param handlerName The name the handler is registered under.
     * @param handler The handler to use.
     * @throws XmlRpcException If an XmlRpcException occurs.
     * @throws IOException If an IOException occurs.
     */
    public void registerHandler(String handlerName,
                                Object handler)
        throws XmlRpcException, IOException
    {
        webserver.addHandler(handlerName, handler);
    }

    /**
     * A helper method that tries to initialize a handler and register it.
     * The purpose is to check for all the exceptions that may occur in
     * dynamic class loading and throw an Exception on
     * error.
     *
     * @param handlerName The name the handler is registered under.
     * @param handlerClass The name of the class to use as a handler.
     * @exception Exception Couldn't instantiate handler.
     */
    private void registerClassHandler(String handlerName, String handlerClass)
        throws Exception
    {
        try
        {
            Object handler = getClass().getClassLoader().loadClass(
                    handlerClass ).newInstance();
            webserver.addHandler(handlerName,handler);
            getLogger().info("registered: " + handlerName + " with class: "
                    + handlerClass);

        }
        // those two errors must be passed to the VM
        catch( ThreadDeath t )
        {
            throw t;
        }
        catch( OutOfMemoryError t )
        {
            throw t;
        }

        catch( Throwable t )
        {
            throw new Exception ("Failed to instantiate " + handlerClass, t);
        }
    }

    /**
     * Helper that registers a component as a handler with the specified
     * handler name.
     *
     * @param handlerName The name to register this handle as.
     * @param handlerRole The role of the component serving as the handler.
     * @exception Exception If the component could not be looked up.
     */
    private void registerComponentHandler(String handlerName,
                                          String handlerRole)
        throws Exception
    {
        registerHandler(handlerName, manager.lookup(handlerRole));
        getLogger().info("registered: " + handlerName + " with component: "
                + handlerRole);
    }

    /**
     * Unregister a handler.
     *
     * @param handlerName The name of the handler to unregister.
     */
    public void unregisterHandler(String handlerName)
    {
        webserver.removeHandler(handlerName);
    }

    /**
     * Switch client filtering on/off.
     *
     * @param state Whether to filter clients.
     *
     * @see #acceptClient(java.lang.String)
     * @see #denyClient(java.lang.String)
     */
    public void setParanoid(boolean state)
    {
        webserver.setParanoid(state);
    }

    /**
     * Add an IP address to the list of accepted clients. The parameter can
     * contain '*' as wildcard character, e.g. "192.168.*.*". You must
     * call setParanoid(true) in order for this to have
     * any effect.
     *
     * @param address The address to add to the list.
     *
     * @see #denyClient(java.lang.String)
     * @see #setParanoid(boolean)
     */
    public void acceptClient(String address)
    {
        webserver.acceptClient(address);
    }

    /**
     * Add an IP address to the list of denied clients. The parameter can
     * contain '*' as wildcard character, e.g. "192.168.*.*". You must call
     * setParanoid(true) in order for this to have any effect.
     *
     * @param address The address to add to the list.
     *
     * @see #acceptClient(java.lang.String)
     * @see #setParanoid(boolean)
     */
    public void denyClient(String address)
    {
        webserver.denyClient(address);
    }
}
