/*
 * Created on Aug 23, 2003
 *
 */
package org.apache.fulcrum.security.spi.memory.simple;
import org.apache.fulcrum.security.model.simple.manager.AbstractUserManagerTest;
/**
 * @author Eric Pugh
 *
 * Test the memory implementation of the Simple model..
 */
public class MemoryUserManagerTest extends AbstractUserManagerTest
{
    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(MemoryUserManagerTest.class);
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
    public MemoryUserManagerTest(String arg0)
    {
        super(arg0);
    }
}
