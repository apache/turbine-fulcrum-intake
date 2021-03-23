package org.apache.fulcrum.xmlrpc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

import java.net.URL;
import java.util.Vector;

import org.apache.fulcrum.testcontainer.BaseUnit5Test;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Unit testing for the XML-RPC component
 *
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 */
public class XmlRpcComponentTest extends BaseUnit5Test
{

    @Test
    public void testInitialize()
    {
        assertTrue(true);
    }

    @Disabled
    public void OFFtestLookup() throws Exception
    {
        XmlRpcServerComponent xmlrpc = null;
        xmlrpc = (XmlRpcServerComponent) lookup(XmlRpcServerComponent.ROLE);



        release(xmlrpc);
    }

    @Test
    public void testHandler() throws Exception
    {
        // start the xml-rpc server
        XmlRpcServerComponent xmlrpc = null;
        xmlrpc = (XmlRpcServerComponent) lookup(XmlRpcServerComponent.ROLE);


        // create the client
        XmlRpcClientComponent rpcClient = null;

        rpcClient = (XmlRpcClientComponent) lookup(XmlRpcClientComponent.ROLE);

        URL server = new URL("http://localhost:12345/RPC2");

        // setup param from rpc call
        Vector<String> params = new Vector<String>();
        String testMessage = "Test message to be echoed back.";
        params.addElement(testMessage);

        // test calling the component handler
        String result = (String) rpcClient.executeRpc(server, "ComponentHandler.echo",
                params);
        assertEquals(result, testMessage);

        // test calling the class handler
        result = (String) rpcClient.executeRpc(server, "ClassHandler.echo",
                params);
        assertEquals(result, testMessage);

        release( rpcClient);
        release( xmlrpc);
    }
}
