/*
 * Created on Aug 21, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.apache.fulcrum.security.model.simple.manager;
import org.apache.fulcrum.security.PermissionManager;
import org.apache.fulcrum.security.SecurityService;
import org.apache.fulcrum.security.entity.Permission;
import org.apache.fulcrum.security.entity.Role;
import org.apache.fulcrum.security.util.PermissionSet;
import org.apache.fulcrum.security.util.UnknownEntityException;
import org.apache.fulcrum.testcontainer.BaseUnitTest;
/**
 * @author Eric Pugh
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public abstract class AbstractPermissionManagerTest extends BaseUnitTest
{
    private Permission permission;
    private PermissionManager permissionManager;
    private SecurityService securityService;
  
  	public abstract void doCustomSetup() throws Exception;
    /**
     * Constructor for PermissionManagerTest.
     * @param arg0
     */
    public AbstractPermissionManagerTest(String arg0)
    {
        super(arg0);
    }
    public void setUp()
    {
        try
        {
        	doCustomSetup();
          
            securityService = (SecurityService) lookup(SecurityService.ROLE);
            permissionManager = securityService.getPermissionManager();
        }
        catch (Exception e)
        {
            fail(e.toString());
        }
    }
    public void tearDown()
    {
        permission = null;
        permissionManager = null;
        securityService = null;
    }
    /*
     * Class to test for Permission getPermissionInstance()
     */
    public void testGetPermissionInstance() throws Exception
    {
        permission = permissionManager.getPermissionInstance();
        assertNotNull(permission);
        assertTrue(permission.getName() == null);
    }
    /*
     * Class to test for Permission getPermissionInstance(String)
     */
    public void testGetPermissionInstanceString() throws Exception
    {
        permission = permissionManager.getPermissionInstance("CAN_TREAT_ANIMALS");
        assertEquals("can_treat_animals", permission.getName());
    }
    public void testGetPermissionByName() throws Exception
    {
        permission = permissionManager.getPermissionInstance("CLEAN_KENNEL");
        permissionManager.addPermission(permission);
        Permission permission2 = permissionManager.getPermissionByName("CLEAN_KENNEL");
        assertEquals(permission.getName(), permission2.getName());
    }
    public void testGetPermissionById() throws Exception
    {
        permission = permissionManager.getPermissionInstance("ADMINSTER_DRUGS");
        permissionManager.addPermission(permission);
        Permission permission2 = permissionManager.getPermissionById(permission.getId());
        assertEquals(permission.getName(), permission2.getName());
    }
    public void testGetAllPermissions() throws Exception
    {
    	int size = permissionManager.getAllPermissions().size();
        permission = permissionManager.getPermissionInstance("WALK_DOGS");
        permissionManager.addPermission(permission);
        PermissionSet permissionSet = permissionManager.getAllPermissions();
        assertEquals(size+1, permissionSet.size());
    }
    public void testRenamePermission() throws Exception
    {
        permission = permissionManager.getPermissionInstance("CLEAN_FRONT_OFFICE");
        permissionManager.addPermission(permission);
        int size = permissionManager.getAllPermissions().size();
        permissionManager.renamePermission(permission, "CLEAN_GROOMING_ROOM");
        Permission permission2 = permissionManager.getPermissionById(permission.getId());
        assertEquals("CLEAN_GROOMING_ROOM".toLowerCase(), permission2.getName());
        assertEquals(size, permissionManager.getAllPermissions().size());
    }
   
    public void testRemovePermission() throws Exception
    {
        permission = permissionManager.getPermissionInstance("CLEAN_CAT_HOUSE");
        permissionManager.addPermission(permission);
        permissionManager.removePermission(permission);
        try
        {
            permission = permissionManager.getPermissionById(permission.getId());
            fail("Should have thrown UnknownEntityException");
        }
        catch (UnknownEntityException uee)
        {
            //good
        }
    }
    public void testAddPermission() throws Exception
    {
        permission = permissionManager.getPermissionInstance("CLEAN_BIG_KENNEL");
        permissionManager.addPermission(permission);
        assertTrue(permission.getId()>0);
        permission = permissionManager.getPermissionById(permission.getId());
        assertNotNull(permission);
    }
    /*
     * Class to test for PermissionSet getPermissions(Role)
     */
    public void testGetPermissionsRole() throws Exception
    {
        permission = permissionManager.getPermissionInstance("GREET_PEOPLE");
        permissionManager.addPermission(permission);
        Permission permission2 = permissionManager.getPermissionInstance("ADMINISTER_DRUGS");
        permissionManager.addPermission(permission2);
        Role role = securityService.getRoleManager().getRoleInstance("VET_TECH");
        securityService.getRoleManager().addRole(role);
        securityService.getRoleManager().grant(role, permission);
        PermissionSet permissionSet = permissionManager.getPermissions(role);
        assertEquals(1, permissionSet.size());
        assertTrue(permissionSet.contains(permission));
        assertFalse(permissionSet.contains(permission2));
    }
    /*
     * Class to test for boolean checkExists(permission)
     */
    public void testCheckExistsPermission() throws Exception
    {
        permission = permissionManager.getPermissionInstance("OPEN_OFFICE");
        permissionManager.addPermission(permission);
        assertTrue(permissionManager.checkExists(permission));
        Permission permission2 = permissionManager.getPermissionInstance("CLOSE_OFFICE");
        assertFalse(permissionManager.checkExists(permission2));
    }
}