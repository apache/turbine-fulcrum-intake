/*
 * Created on Aug 23, 2003
 *
 */
package org.apache.fulcrum.security.spi.memory.simple;
import org.apache.fulcrum.security.model.simple.manager.AbstractGroupManagerTest;
/**
 * @author Eric Pugh
 *
 * Test the memory implementation of the Simple model..
 */
public class MemoryGroupManagerTest extends AbstractGroupManagerTest
{
    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(MemoryGroupManagerTest.class);
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
    public MemoryGroupManagerTest(String arg0)
    {
        super(arg0);
    }
}
