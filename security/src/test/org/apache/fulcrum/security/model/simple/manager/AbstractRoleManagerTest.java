/*
 * Created on Aug 21, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.apache.fulcrum.security.model.simple.manager;
import org.apache.fulcrum.security.SecurityService;
import org.apache.fulcrum.security.entity.Permission;
import org.apache.fulcrum.security.entity.Role;
import org.apache.fulcrum.security.model.simple.entity.SimpleRole;
import org.apache.fulcrum.security.util.PermissionSet;
import org.apache.fulcrum.security.util.RoleSet;
import org.apache.fulcrum.security.util.UnknownEntityException;
import org.apache.fulcrum.testcontainer.BaseUnitTest;
/**
 * @author Eric Pugh
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public abstract class AbstractRoleManagerTest extends BaseUnitTest
{
    protected Role role;
    protected SimpleRoleManager roleManager;
    protected SecurityService securityService;

    /**
     * Constructor for AbstractRoleManagerTest.
     * @param arg0
     */
    public AbstractRoleManagerTest(String arg0)
    
    {
        super(arg0);
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
        assertEquals("dog_catcher", role.getName());
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
		((SimpleRoleManager)roleManager).grant(role, permission);
        role = roleManager.getRoleById(role.getId());
        PermissionSet permissions = securityService.getPermissionManager().getPermissions(role);
        assertEquals(1, permissions.size());
    }
    public void testRevoke() throws Exception
    {
        Permission permission = securityService.getPermissionManager().getPermissionInstance();
        permission.setName("ANSWER_FAX");
        securityService.getPermissionManager().addPermission(permission);
        role = roleManager.getRoleInstance("SECRETARY");
        roleManager.addRole(role);
        roleManager.grant(role, permission);
        role = roleManager.getRoleById(role.getId());
        PermissionSet permissions = securityService.getPermissionManager().getPermissions(role);
        assertEquals(1, permissions.size());
        roleManager.revoke(role, permission);
        role = roleManager.getRoleById(role.getId());
        permissions = securityService.getPermissionManager().getPermissions(role);
        assertEquals(0, permissions.size());
    }
    public void testRevokeAll() throws Exception
    {
        Permission permission = securityService.getPermissionManager().getPermissionInstance();
        Permission permission2 = securityService.getPermissionManager().getPermissionInstance();
        permission.setName("SEND_SPAM");
        permission2.setName("ANSWER_EMAIL");
        securityService.getPermissionManager().addPermission(permission);
        securityService.getPermissionManager().addPermission(permission2);
        role = roleManager.getRoleInstance("HELPER");
        roleManager.addRole(role);
        roleManager.grant(role, permission);
        roleManager.grant(role, permission2);
        role = roleManager.getRoleById(role.getId());
        PermissionSet permissions = securityService.getPermissionManager().getPermissions(role);
        assertEquals(2, permissions.size());
        roleManager.revokeAll(role);
        role = roleManager.getRoleById(role.getId());
        permissions = securityService.getPermissionManager().getPermissions(role);
        assertEquals(0, permissions.size());
    }
    public void testRenameRole() throws Exception
    {
        role = roleManager.getRoleInstance("CLEAN_KENNEL_X");
        roleManager.addRole(role);
        int size = roleManager.getAllRoles().size();
        roleManager.renameRole(role, "CLEAN_GROOMING_ROOM");
        Role role2 = roleManager.getRoleById(role.getId());
        assertEquals("clean_grooming_room", role2.getName());
        assertEquals(size, roleManager.getAllRoles().size());
    }
    public void testGetAllRoles() throws Exception
    {
        int size = roleManager.getAllRoles().size();
        role = roleManager.getRoleInstance("CLEAN_KENNEL_J");
        roleManager.addRole(role);
        RoleSet roleSet = roleManager.getAllRoles();
        assertEquals(size + 1, roleSet.size());
    }
  
    public void testGrantUserGroup() throws Exception
    {
        Permission permission = securityService.getPermissionManager().getPermissionInstance();
        permission.setName("TEST_PERMISSION");
        securityService.getPermissionManager().addPermission(permission);
        role = roleManager.getRoleInstance("TEST_ROLE");
        roleManager.addRole(role);
        ((SimpleRoleManager) roleManager).grant(role, permission);
		assertTrue(((SimpleRole) role).getPermissions().contains(permission));
    }
    public void testRevokeUserGroup() throws Exception
    {
        Permission permission = securityService.getPermissionManager().getPermissionInstance();
        permission.setName("TEST_PERMISSION2");
        securityService.getPermissionManager().addPermission(permission);
        role = roleManager.getRoleInstance("Lima2");
        roleManager.addRole(role);
        ((SimpleRoleManager) roleManager).grant(role, permission);
        ((SimpleRoleManager) roleManager).revoke(role, permission);
		
		assertFalse(((SimpleRole) role).getPermissions().contains(permission));
		
    }
    public void testAddRole() throws Exception
    {
        role = roleManager.getRoleInstance("DOG_NAPPER");
        roleManager.addRole(role);
        assertNotNull(roleManager.getRoleById(role.getId()));
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
