package org.apache.fulcrum.xmlrpc;

import java.net.URL;
import java.util.Vector;

import org.apache.fulcrum.testcontainer.BaseUnitTest;

import junit.framework.TestSuite;

/**
 * Unit testing for the XML-RPC component
 *
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 */
public class XmlRpcComponentTest extends BaseUnitTest
{
    /**
     * Constructor for test.
     *
     * @param testName name of the test being executed
     */
    public XmlRpcComponentTest(String testName)
    {
        super(testName);
    }


    public void testInitialize()
    {
        assertTrue(true);
    }

    public void OFFtestLookup() throws Exception
    {
        XmlRpcServerComponent xmlrpc = null;
        xmlrpc = (XmlRpcServerComponent) lookup(XmlRpcServerComponent.ROLE);
        
        

        release(xmlrpc);
    }

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
        Vector params = new Vector();
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
