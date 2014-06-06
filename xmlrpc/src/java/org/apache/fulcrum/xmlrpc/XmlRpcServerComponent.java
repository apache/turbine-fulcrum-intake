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


import java.io.IOException;

import org.apache.xmlrpc.XmlRpcException;

/**
 * The interface a server-side XmlRpcService implements.
 *
 * @avalon.service version="1.0"
 * @author <a href="mailto:josh@stonecottage.com">Josh Lucas</a>
 * @author <a href="mailto:magnus@handtolvur.is">Magnús Þór Torfason</a>
 * @author <a href="mailto:Rafal.Krzewski@e-point.pl">Rafal Krzewski</a>
 * @author <a href="mailto:jvanzyl@periapt.com">Jason van Zyl</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 */
public interface XmlRpcServerComponent
{
    /** XmlRpcServerComponent. */
    public static final String ROLE = XmlRpcServerComponent.class.getName();

    /**
     * Register an object as a handler for the XmlRpc Server part.
     *
     * @param handlerName The name under which we want
     * to register the service
     * @param handler The handler object
     * @exception XmlRpcException
     * @exception IOException
     */
    void registerHandler(String handlerName, Object handler)
        throws XmlRpcException, IOException;

    /**
     * Register an object as a the default handler for
     * the XmlRpc Server part.
     *
     * @param handler The handler object
     * @exception XmlRpcException
     * @exception IOException
     */
    void registerHandler(Object handler)
        throws XmlRpcException, IOException;

    /**
     * Unregister a handler.
     *
     * @param handlerName The name of the handler to unregister.
     */
    void unregisterHandler(String handlerName);

    /**
     * Switch client filtering on/off.
     * @see #acceptClient(java.lang.String)
     * @see #denyClient(java.lang.String)
     */
    void setParanoid(boolean state);

    /**
     * Add an IP address to the list of accepted clients. The parameter can
     * contain '*' as wildcard character, e.g. "192.168.*.*". You must
     * call setParanoid(true) in order for this to have
     * any effect.
     *
     * @see #denyClient(java.lang.String)
     * @see #setParanoid(boolean)
     */
    void acceptClient(String address);

    /**
     * Add an IP address to the list of denied clients. The parameter can
     * contain '*' as wildcard character, e.g. "192.168.*.*". You must call
     * setParanoid(true) in order for this to have any effect.
     *
     * @see #acceptClient(java.lang.String)
     * @see #setParanoid(boolean)
     */
    void denyClient(String address);
}
