/*
 * Created on Aug 23, 2003
 *
 */
package org.apache.fulcrum.security.spi.memory.simple;
import org.apache.fulcrum.security.SecurityService;
import org.apache.fulcrum.security.model.simple.manager.AbstractRoleManagerTest;
import org.apache.fulcrum.security.model.simple.manager.SimpleRoleManager;
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
    public void setUp()
    {
        try
        {
            this.setRoleFileName(null);
            this.setConfigurationFileName("src/test/SimpleMemory.xml");
            securityService = (SecurityService) lookup(SecurityService.ROLE);
            roleManager = (SimpleRoleManager) securityService.getRoleManager();
        }
        catch (Exception e)
        {
            fail(e.toString());
        }
    }
    public void tearDown()
    {
        role = null;
        roleManager = null;
        securityService = null;
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
