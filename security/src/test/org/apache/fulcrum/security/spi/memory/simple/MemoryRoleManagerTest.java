/*
 * Created on Aug 23, 2003
 *
 */
package org.apache.fulcrum.security.spi.memory.simple;
import org.apache.fulcrum.security.model.simple.manager.AbstractRoleManagerTest;
/**
 * @author Eric Pugh
 *
 * Test the memory implementation of the Simple model..
 */
public class MemoryRoleManagerTest extends AbstractRoleManagerTest
{
    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(MemoryRoleManagerTest.class);
    }
    public void doCustomSetup()
    {
        this.setRoleFileName(null);
        this.setConfigurationFileName("src/test/InMemorySecurity.xml");
    }
    /**
    	* Constructor for MemoryPermissionManagerTest.
    	* @param arg0
    	*/
    public MemoryRoleManagerTest(String arg0)
    {
        super(arg0);
    }
}