/*
 * Created on Aug 23, 2003
 *
 */
package org.apache.fulcrum.security.spi.memory.simple;
import org.apache.fulcrum.security.SecurityService;
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
	public void setUp()
   {
	   try
	   {
		   this.setRoleFileName(null);
		   this.setConfigurationFileName("src/test/SimpleMemory.xml");
		   securityService = (SecurityService) lookup(SecurityService.ROLE);
		   groupManager = securityService.getGroupManager();
	   }
	   catch (Exception e)
	   {
		   fail(e.toString());
	   }
   }
   public void tearDown()
   {
	   group = null;
	   groupManager = null;
	   securityService = null;
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
