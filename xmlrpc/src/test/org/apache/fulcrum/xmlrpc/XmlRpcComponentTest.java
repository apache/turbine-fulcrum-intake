package org.apache.fulcrum.xmlrpc;

import org.apache.fulcrum.testcontainer.BaseUnitTest;
import org.apache.avalon.framework.component.ComponentException;

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

}
