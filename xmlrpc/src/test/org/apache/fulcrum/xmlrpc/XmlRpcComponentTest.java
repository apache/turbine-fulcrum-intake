package org.apache.fulcrum.xmlrpc;

import java.util.Vector;

import org.apache.fulcrum.testcontainer.BaseUnitTest;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.xmlrpc.XmlRpcClient;

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

    /**
     * Factory method for creating a TestSuite for this class.
     *
     * @return the test suite
     */
    public static TestSuite suite()
    {
        TestSuite suite = new TestSuite(XmlRpcComponentTest.class);
        return suite;
    }

    public void testInitialize()
    {
        assertTrue(true);
    }

    public void testLookup()
    {
        XmlRpcComponent xmlrpc = null;
        try
        {
            xmlrpc = (XmlRpcComponent) lookup(XmlRpcComponent.ROLE);
        }
        catch (ComponentException e)
        {
            e.printStackTrace();
            fail("Could not lookup component");
        }
    }

    public void testHandler() throws Exception
    {
        // start the xml-rpc server
        XmlRpcComponent xmlrpc = null;
        try
        {
            xmlrpc = (XmlRpcComponent) lookup(XmlRpcComponent.ROLE);
        }
        catch (ComponentException e)
        {
            e.printStackTrace();
            fail("Could not lookup component");
        }

        // create the client
        XmlRpcClient rpcClient =
                new XmlRpcClient("http://localhost:12345/RPC2");

        // setup param from rpc call
        Vector params = new Vector();
        String testMessage = "Test message to be echoed back.";
        params.addElement(testMessage);

        // test calling the component handler
        String result = (String) rpcClient.execute("ComponentHandler.echo",
                params);
        assertEquals(result, testMessage);

        // test calling the class handler
        result = (String) rpcClient.execute("ClassHandler.echo",
                params);
        assertEquals(result, testMessage);




    }

}
