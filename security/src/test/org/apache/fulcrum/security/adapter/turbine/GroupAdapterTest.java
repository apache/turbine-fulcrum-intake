/*
 * Created on Aug 26, 2003
 *
 */
package org.apache.fulcrum.security.adapter.turbine;
import junit.framework.TestCase;

import org.apache.fulcrum.security.entity.Group;
import org.apache.fulcrum.security.model.simple.entity.SimpleGroup;
/**
 * @author Eric Pugh
 *
 * Test that we can use a GroupAdapter with a Group that has various
 * types of Id objects.
 */
public class GroupAdapterTest extends TestCase
{
    /**
     * Constructor for GroupAdapterTest.
     * @param arg0
     */
    public GroupAdapterTest(String arg0)
    {
        super(arg0);
    }
    public void testWithInteger()
    {
        Group group = new SimpleGroup();
        group.setId(new Integer(56));
        GroupAdapter ga = new GroupAdapter(group);
        assertEquals(56, ga.getId());
        assertEquals(new Integer(56), ga.getIdAsObj());
 
    }
    public void testWithLong()
    {
        Group group = new SimpleGroup();
        group.setId(new Long(56));
        GroupAdapter ga = new GroupAdapter(group);
        assertEquals(56, ga.getId());
        assertEquals(new Integer(56), ga.getIdAsObj());
      
    }
    public void testWithString()
    {
        Group group = new SimpleGroup();
        group.setId("56");
        GroupAdapter ga = new GroupAdapter(group);
        assertEquals(56, ga.getId());
        assertEquals(new Integer(56), ga.getIdAsObj());

    }
}
