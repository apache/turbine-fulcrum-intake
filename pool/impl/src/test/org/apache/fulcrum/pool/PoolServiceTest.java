/*
 * Created on Aug 20, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.apache.fulcrum.pool;
import org.apache.avalon.merlin.unit.AbstractMerlinTestCase;

/**
 * @author Eric Pugh
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class PoolServiceTest extends AbstractMerlinTestCase
{
    private PoolService poolService = null;
    /**
    	* Defines the testcase name for JUnit.
    	*
    	* @param name the testcase's name.
    	*/
    public PoolServiceTest(String name)
    {
        super(name);
    }
   
    public void setUp() throws Exception
    {
        super.setUp();
        try
        {
            poolService = (PoolService) this.resolve( "/test/pool" );
        }
        catch (Throwable e)
        {
            fail(e.getMessage());
        }
    }

    /*
     * Class to test for Object getInstance(String)
     * @todo Some sort of compile error prevents this..
     */
    public void testGetInstanceString() throws PoolException
    {
        //Object object = poolService.getInstance("java.lang.StringBuffer");
        //assertTrue(object instanceof StringBuffer);
         
    }
    /*
     * Class to test for Object getInstance(String, ClassLoader)
     */
    public void testGetInstanceStringClassLoader()
    {
         
    }
    /*
     * Class to test for Object getInstance(String, Object[], String[])
     */
    public void testGetInstanceStringObjectArrayStringArray()
    {
         
    }

    /*
     * Class to test for Object getInstance(String, ClassLoader, Object[], String[])
     */
    public void testGetInstanceStringClassLoaderObjectArrayStringArray()
    {
         
    }

    public void testIsLoaderSupported()
    {
         
    }

    /*
     * Class to test for Object getInstance(Class)
     */
    public void testGetInstanceClass() throws PoolException
    {
        Object object = poolService.getInstance(StringBuffer.class);
        assertTrue(object instanceof StringBuffer);
        
    }
    /*
     * Class to test for Object getInstance(Class, Object[], String[])
     */
    public void testGetInstanceClassObjectArrayStringArray()
    {
         
    }

    public void testPutInstance()
    {
        String s = "I am a string";
        assertEquals(0, poolService.getSize("java.lang.String"));
        poolService.putInstance(s);
        assertEquals(1, poolService.getSize("java.lang.String"));
        
    }
    public void testGetSetCapacity()
    {
        assertEquals(128, poolService.getCapacity("java.lang.String"));
        poolService.setCapacity("java.lang.String", 278);
        assertEquals(278, poolService.getCapacity("java.lang.String"));
        
    }
    public void testGetSize()
    {
        String s = "I am a string";
        assertEquals(0, poolService.getSize("java.lang.String"));
        poolService.putInstance(s);
        assertEquals(1, poolService.getSize("java.lang.String"));
        
    }
    /*
     * Class to test for void clearPool(String)
     */
    public void testClearPoolString()
    {
        String s = "I am a string";
        assertEquals(0, poolService.getSize("java.lang.String"));
        poolService.putInstance(s);
        assertEquals(1, poolService.getSize("java.lang.String"));
        poolService.clearPool("java.lang.String");
        assertEquals(0, poolService.getSize("java.lang.String"));
        
    }
    /*
     * Class to test for void clearPool()
     */
    public void testClearPool()
    {
        String s = "I am a string";
        assertEquals(0, poolService.getSize("java.lang.String"));
        poolService.putInstance(s);
        poolService.putInstance(new Double(32));
        assertEquals(1, poolService.getSize("java.lang.String"));
        poolService.clearPool();
        assertEquals(0, poolService.getSize("java.lang.String"));
        assertEquals(0, poolService.getSize("java.lang.Double"));
        
    }
}
