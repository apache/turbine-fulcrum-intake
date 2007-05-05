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


import org.apache.xmlrpc.XmlRpcClient;

import java.io.IOException;
import java.net.URL;
import java.util.Vector;

import org.apache.xmlrpc.XmlRpcException;

/**
 * Default implementation of the client-side XML RPC component.
 *
 * @avalon.component version="1.0" name="xmlrpc-client" lifestyle="singleton"
 * @avalon.service   version="1.0" type="org.apache.fulcrum.xmlrpc.XmlRpcClientComponent"
 * @author <a href="mailto:jason@zenplex.com">Jason van Zyl</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 */
public class DefaultXmlRpcClientComponent
    extends AbstractXmlRpcComponent
    implements XmlRpcClientComponent
{
    /** Default Constructor. */
    public DefaultXmlRpcClientComponent()
    {
        // nothing to do
    }

    /**
     * Client's interface to XML-RPC.
     *
     * The return type is Object which you'll need to cast to
     * whatever you are expecting.
     *
     * @param url A URL.
     * @param methodName A String with the method name.
     * @param params A Vector with the parameters.
     * @return An Object.
     * @exception XmlRpcException
     * @exception IOException
     */
    public Object executeRpc(URL url,
                             String methodName,
                             Vector params)
        throws IOException, XmlRpcException
    {
        XmlRpcClient client = new XmlRpcClient ( url );
        return client.execute(methodName, params);
    }
}
