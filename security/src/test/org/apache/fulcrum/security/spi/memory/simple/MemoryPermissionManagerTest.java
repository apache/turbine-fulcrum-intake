/*
 * Created on Aug 23, 2003
 *
 */
package org.apache.fulcrum.security.spi.memory.simple;
import org.apache.fulcrum.security.model.simple.manager.AbstractPermissionManagerTest;
/**
 * @author Eric Pugh
 *
 * Test the memory implementation of the Simple model..
 */
public class MemoryPermissionManagerTest extends AbstractPermissionManagerTest
{
    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(MemoryPermissionManagerTest.class);
    }
    public void doCustomSetup()
    {
        this.setRoleFileName(null);
        this.setConfigurationFileName("src/test/SimpleMemory.xml");
    }
    /**
    	* Constructor for MemoryPermissionManagerTest.
    	* @param arg0
    	*/
    public MemoryPermissionManagerTest(String arg0)
    {
        super(arg0);
    }
}
