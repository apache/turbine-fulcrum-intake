package org.apache.fulcrum.factory;
import java.util.ArrayList;

import org.apache.avalon.framework.component.ComponentException;
import org.apache.fulcrum.testcontainer.BaseUnitTest;
/**
 * @author Eric Pugh
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class FactoryServiceTest extends BaseUnitTest
{
    private FactoryService factoryService = null;
    /**
     * Defines the testcase name for JUnit.
     *
     * @param name the testcase's name.
     */
    public FactoryServiceTest(String name)
    {
        super(name);
    }
    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(FactoryServiceTest.class);
    }
    protected void setUp() throws Exception
    {
        super.setUp();
        try
        {
            factoryService = (FactoryService) this.lookup(FactoryService.ROLE);
        }
        catch (ComponentException e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    /*
     * Class to test for Object getInstance(String)
     */
    public void testGetInstanceString() throws Exception
    {
        Object object = factoryService.getInstance("java.lang.StringBuffer");
        assertTrue(object instanceof StringBuffer);
    }
    /*
     * Class to test for Object getInstance(String, ClassLoader)
     */
    public void testGetInstanceStringClassLoader() throws Exception
    {
        Object object = factoryService.getInstance("java.lang.StringBuffer", StringBuffer.class.getClassLoader());
        assertTrue(object instanceof StringBuffer);
    }
    /*
     * Class to test for Object getInstance(String, Object[], String[])
     */
    public void testGetInstanceStringObjectArrayStringArray() throws Exception
    {
        Object params[] = new Object[1];
        String sourceValu = "testing";
        params[0] = sourceValu;
        String signature[] = new String[1];
        signature[0] = "java.lang.String";
        Object object = factoryService.getInstance("java.lang.StringBuffer", params, signature);
        assertTrue(object instanceof StringBuffer);
        assertEquals(sourceValu, object.toString());
    }
    /*
     * Class to test for Object getInstance(String, ClassLoader, Object[], String[])
     */
    public void testGetInstanceStringClassLoaderObjectArrayStringArray() throws Exception
    {
        Object params[] = new Object[1];
        String sourceValu = "testing";
        params[0] = sourceValu;
        String signature[] = new String[1];
        signature[0] = "java.lang.String";
        Object object =
            factoryService.getInstance(
                "java.lang.StringBuffer",
                StringBuffer.class.getClassLoader(),
                params,
                signature);
        assertTrue(object instanceof StringBuffer);
        assertEquals(sourceValu, object.toString());
    }
    /**
     * @todo Need to run a test where the loader is NOT supported.
     * @throws Exception
     */
    public void testIsLoaderSupported() throws Exception
    {
        assertTrue(factoryService.isLoaderSupported("java.lang.String"));
    }
    public void testGetSignature() throws Exception
    {
        Object params[] = new Object[1];
        String sourceValu = "testing";
        params[0] = sourceValu;
        String signature[] = new String[1];
        signature[0] = "java.lang.String";
        Class[] results = factoryService.getSignature(StringBuffer.class, params, signature);
        assertEquals(1, results.length);
        assertTrue(results[0].equals(String.class));

        Integer sourceValueInteger = new Integer(10);
        params[0] = sourceValueInteger;
        signature[0] = "java.lang.Integer";
        results = factoryService.getSignature(ArrayList.class, params, signature);
        assertEquals(1, results.length);
        assertTrue("Result:" + results[0].getName(),results[0].equals(Integer.class));
    }
}
