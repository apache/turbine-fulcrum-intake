/*
 * Created on Aug 26, 2003
 *
 */
package org.apache.fulcrum.security.adapter.turbine;
import junit.framework.TestCase;

import org.apache.fulcrum.security.entity.SecurityEntity;
import org.apache.fulcrum.security.entity.impl.SecurityEntityImpl;
/**
 * @author Eric Pugh
 *
 * Test that we can use a GroupAdapter with a Group that has various
 * types of Id objects.
 */
public class BaseAdapterTest extends TestCase
{
    /**
     * Constructor for GroupAdapterTest.
     * @param arg0
     */
    public BaseAdapterTest(String arg0)
    {
        super(arg0);
    }
    public void testWithInteger()
    {
        SecurityEntity entity = new SecurityEntityImpl();
		entity.setId(new Integer(56));
		BaseAdapter ba = new BaseAdapter(entity);
        assertEquals(56, ba.getId());
        assertEquals(new Integer(56), ba.getIdAsObj());

    }
    public void testWithLong()
    {
		SecurityEntity entity = new SecurityEntityImpl();
		entity.setId(new Long(56));
		BaseAdapter ba = new BaseAdapter(entity);
        assertEquals(56, ba.getId());
        assertEquals(new Integer(56), ba.getIdAsObj());
     
     
     
    }
    public void testWithString()
    {
		SecurityEntity entity = new SecurityEntityImpl();
		entity.setId("56");
		BaseAdapter ba = new BaseAdapter(entity);
        assertEquals(56, ba.getId());
        assertEquals(new Integer(56), ba.getIdAsObj());
  
    }
}
