/*
 * Created on Aug 21, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.apache.fulcrum.security;
import org.apache.fulcrum.security.entity.Permission;
import org.apache.fulcrum.security.entity.Role;
import org.apache.fulcrum.security.impl.memory.MemoryRoleManager;
import org.apache.fulcrum.security.util.RoleSet;
import org.apache.fulcrum.security.util.UnknownEntityException;
import org.apache.fulcrum.testcontainer.BaseUnitTest;
/**
 * @author Eric Pugh
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class RoleManagerTest extends BaseUnitTest
{
    private Role role;
    private RoleManager roleManager;
    private SecurityService securityService;
    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(RoleManagerTest.class);
    }
    /**
     * Constructor for RoleManagerTest.
     * @param arg0
     */
    public RoleManagerTest(String arg0)
    {
        super(arg0);
    }
    public void setUp()
    {
        try
        {
            this.setRoleFileName(null);
            this.setConfigurationFileName("src/test/InMemorySecurity.xml");
            securityService = (SecurityService) lookup(SecurityService.ROLE);
            //roleManager = (RoleManager)lookup(RoleManager.ROLE);
            roleManager = securityService.getRoleManager();
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
    /*
     * Class to test for Role getRoleInstance()
     */
    public void testGetRoleInstance() throws Exception
    {
        role = roleManager.getRoleInstance();
        assertNotNull(role);
        assertTrue(role.getName() == null);
    }
    /*
     * Class to test for Role getRoleInstance(String)
     */
    public void testGetRoleInstanceString() throws Exception
    {
        role = roleManager.getRoleInstance("DOG_CATCHER");
        assertEquals("DOG_CATCHER", role.getName());
    }
    public void testGetRoleByName() throws Exception
    {
        role = roleManager.getRoleInstance("DOG_CATCHERd");
        roleManager.addRole(role);
        Role role2 = roleManager.getRoleByName("DOG_CATCHERd");
        assertEquals(role.getName(), role2.getName());
    }
    public void testGetRoleById() throws Exception
    {
        role = roleManager.getRoleInstance("CLEAN_KENNEL_A");
        roleManager.addRole(role);
        Role role2 = roleManager.getRoleById(role.getId());
        assertEquals(role.getName(), role2.getName());
    }
    public void testGrant() throws Exception
    {
        Permission permission = securityService.getPermissionManager().getPermissionInstance();
        permission.setName("ANSWER_PHONE");
        securityService.getPermissionManager().addPermission(permission);
        role = roleManager.getRoleInstance("RECEPTIONIST");
        roleManager.addRole(role);
        roleManager.grant(role, permission);
    }
    public void testRevoke() throws Exception
    {
    }
    public void testRevokeAll() throws Exception
    {
    }
    public void testRenameRole() throws Exception
    {
        role = roleManager.getRoleInstance("CLEAN_KENNEL_X");
        roleManager.addRole(role);
        int size = roleManager.getAllRoles().size();
        roleManager.renameRole(role, "CLEAN_GROOMING_ROOM");
        Role role2 = roleManager.getRoleById(role.getId());
        assertEquals("CLEAN_GROOMING_ROOM", role2.getName());
        assertEquals(size, roleManager.getAllRoles().size());
    }
    public void testGetAllRoles() throws Exception
    {
    	int size = roleManager.getAllRoles().size();
        role = roleManager.getRoleInstance("CLEAN_KENNEL_J");
        roleManager.addRole(role);
        RoleSet roleSet = roleManager.getAllRoles();
        assertEquals(size+1, roleSet.size());
    }
    public void testGetPermissions() throws Exception
    {
    }
    public void testGrantUserGroup() throws Exception
    {
        if (roleManager instanceof MemoryRoleManager)
        {
            Permission permission = securityService.getPermissionManager().getPermissionInstance();
            permission.setName("TEST_PERMISSION");
            securityService.getPermissionManager().addPermission(permission);
            role = roleManager.getRoleInstance("TEST_ROLE");
            roleManager.addRole(role);
            ((MemoryRoleManager) roleManager).grant(role, permission);
        }
    }
    public void testRevokeUserGroup() throws Exception
    {
        if (roleManager instanceof MemoryRoleManager)
        {
            Permission permission = securityService.getPermissionManager().getPermissionInstance();
            permission.setName("TEST_PERMISSION2");
            securityService.getPermissionManager().addPermission(permission);
            role = roleManager.getRoleInstance("Lima2");
            roleManager.addRole(role);
            ((MemoryRoleManager) roleManager).grant(role, permission);
            ((MemoryRoleManager) roleManager).revoke(role, permission);
        }
    }
    /*
     * Class to test for boolean checkExists(Permission)
     */
    public void testCheckExistsPermission() throws Exception
    {
    }
    public void testAddRole() throws Exception
    {
        role = roleManager.getRoleInstance("DOG_NAPPER");
        roleManager.addRole(role);
        assertNotNull(roleManager.getRoleById(role.getId()));
    }
    public void testSaveRole() throws Exception
    {
        role = roleManager.getRoleInstance("KENNEL_CLEANER");
        roleManager.addRole(role);
        role.setName("CLEAN_CATTERY");
        roleManager.saveRole(role);
        assertEquals("CLEAN_CATTERY", roleManager.getRoleById(role.getId()).getName());
    }
    public void testRemoveRole() throws Exception
    {
        role = roleManager.getRoleInstance("CLEAN_KENNEL_K");
        roleManager.addRole(role);
        int size = roleManager.getAllRoles().size();
        roleManager.removeRole(role);
        try
        {
            Role role2 = roleManager.getRoleById(role.getId());
            fail("Should have thrown UEE");
        }
        catch (UnknownEntityException uee)
        {
            //good
        }
        assertEquals(size - 1, roleManager.getAllRoles().size());
    }
    public void testCheckExists() throws Exception
    {
        role = roleManager.getRoleInstance("GREET_PEOPLE");
        roleManager.addRole(role);
        assertTrue(roleManager.checkExists(role));
        Role role2 = roleManager.getRoleInstance("WALK_DOGS");
        assertFalse(roleManager.checkExists(role2));
    }
}
