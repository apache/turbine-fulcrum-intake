package org.apache.fulcrum.security.acl;
import java.util.HashMap;
import java.util.Map;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.fulcrum.factory.FactoryService;
import org.apache.fulcrum.security.PermissionManager;
import org.apache.fulcrum.security.SecurityService;
import org.apache.fulcrum.security.entity.Group;
import org.apache.fulcrum.security.entity.Permission;
import org.apache.fulcrum.security.entity.Role;
import org.apache.fulcrum.security.entity.User;
import org.apache.fulcrum.security.model.simple.entity.SimpleGroup;
import org.apache.fulcrum.security.model.simple.entity.SimpleRole;
import org.apache.fulcrum.security.model.simple.manager.SimpleRoleManager;
import org.apache.fulcrum.security.model.simple.manager.SimpleUserManager;
import org.apache.fulcrum.security.model.simple.manager.SimpleGroupManager;
import org.apache.fulcrum.security.util.GroupSet;
import org.apache.fulcrum.security.util.PermissionSet;
import org.apache.fulcrum.security.util.RoleSet;
import org.apache.fulcrum.testcontainer.BaseUnitTest;
/**
 * @author Eric Pugh
 *
 * Test that we can generate AccessControlLists from the Factory
 */
public class AccessControlListTest extends BaseUnitTest
{
    private FactoryService factoryService = null;
    private SimpleUserManager userManager;
    private SimpleGroupManager groupManager;
    private SimpleRoleManager roleManager;
    private PermissionManager permissionManager;
    private AccessControlList acl;
    private static int counter = 1;
    private User user;
    /**
     * Defines the testcase name for JUnit.
     *
     * @param name the testcase's name.
     */
    public AccessControlListTest(String name)
    {
        super(name);
    }
    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(AccessControlListTest.class);
    }
    protected void setUp()
    {
        super.setUp();
        try
        {
            this.setRoleFileName(null);
            this.setConfigurationFileName("src/test/AccessControlList.xml");
            factoryService = (FactoryService) this.lookup(FactoryService.ROLE);
            SecurityService securityService = (SecurityService) lookup(SecurityService.ROLE);
            userManager = (SimpleUserManager) securityService.getUserManager();
            groupManager = (SimpleGroupManager) securityService.getGroupManager();
            roleManager = (SimpleRoleManager) securityService.getRoleManager();
            permissionManager = securityService.getPermissionManager();
        }
        catch (ComponentException e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    /*
     * Class to test for Object getInstance(String, Object[], String[])
     */
    public void testGetInstanceStringObjectArrayStringArray() throws Exception
    {
        Object params[] = new Object[1];
        String sourceValu = "testing";
        params[0] = sourceValu;
        String signature[] = new String[1];
        signature[0] = "java.lang.String";
        Object object = factoryService.getInstance("java.lang.StringBuffer", params, signature);
        assertTrue(object instanceof StringBuffer);
        assertEquals(sourceValu, object.toString());
    }
    public void testCreatingDefaultAccessControlListViaFactory() throws Exception
    {
        Group group = getGroup();
        Role role = getRole();
        Permission permission = getPermission();
        user = userManager.getUserInstance("User 1");
        userManager.addUser(user, "secretpassword");
        userManager.grant(user, group);
        groupManager.grant(group, role);
        roleManager.grant(role, permission);
        RoleSet roleSet = new RoleSet();
        PermissionSet permissionSet = new PermissionSet();
        roleSet.add(role);
        permissionSet.add(permission);
        Map roleSets = new HashMap();
        Map permissionSets = new HashMap();
        roleSets.put(group, roleSet);
        permissionSets.put(role, permissionSet);
        Object params[] = new Object[2];
        params[0] = roleSets;
        params[1] = permissionSets;
        String signature[] = new String[2];
        signature[0] = "java.util.Map";
        signature[1] = "java.util.Map";
        Object object =
            factoryService.getInstance("org.apache.fulcrum.security.acl.DefaultAccessControlList", params, signature);
        assertTrue(object instanceof DefaultAccessControlList);
    }
    public void testGetRolesGroup() throws Exception
    {
        Group group = getGroup();
        Role role = getRole();
        Role role2 = getRole();
        Role role3 = getRole();
        groupManager.grant(group, role);
        groupManager.grant(group, role2);
        Map roleSets = new HashMap();
        Map permissionSets = new HashMap();
        roleSets.put(group, ((SimpleGroup) group).getRoles());
        acl = new DefaultAccessControlList(roleSets, permissionSets);
        RoleSet resultRoleSet = acl.getRoles(group);
        assertTrue(resultRoleSet.contains(role));
        assertTrue(resultRoleSet.contains(role2));
        assertFalse(resultRoleSet.contains(role3));
    }
    /*
     * Class to test for RoleSet getRoles()
     */
    public void testGetRoles() throws Exception
    {
        Group group = getGroup();
        Group group2 = getGroup();
        Role role = getRole();
        Role role2 = getRole();
        Role role3 = getRole();
        groupManager.grant(group, role);
        groupManager.grant(group, role2);
        groupManager.grant(group2, role2);
        groupManager.grant(group2, role3);
        Map roleSets = new HashMap();
        Map permissionSets = new HashMap();
        roleSets.put(group, ((SimpleGroup) group).getRoles());
        roleSets.put(group2, ((SimpleGroup) group2).getRoles());
        acl = new DefaultAccessControlList(roleSets, permissionSets);
        RoleSet resultRoleSet = acl.getRoles();
        assertTrue(resultRoleSet.contains(role));
        assertTrue(resultRoleSet.contains(role2));
        assertTrue(resultRoleSet.contains(role3));
        assertEquals(3, resultRoleSet.size());
    }
    /*
     * Class to test for PermissionSet getPermissions(Group)
     */
    public void testGetPermissionsGroup() throws Exception
    {
        Group group = getGroup();
        Group group2 = getGroup();
        Role role = getRole();
        Role role2 = getRole();
        Role role3 = getRole();
        Permission permission = getPermission();
        Permission permission2 = getPermission();
        Permission permission3 = getPermission();
        groupManager.grant(group, role);
        groupManager.grant(group, role2);
        groupManager.grant(group2, role3);
        roleManager.grant(role, permission);
        roleManager.grant(role, permission2);
        roleManager.grant(role, permission3);
        roleManager.grant(role2, permission2);
        roleManager.grant(role2, permission3);
        Map roleSets = new HashMap();
        Map permissionSets = new HashMap();
        roleSets.put(group, ((SimpleGroup) group).getRoles());
        permissionSets.put(role, ((SimpleRole) role).getPermissions());
        acl = new DefaultAccessControlList(roleSets, permissionSets);
        PermissionSet resultPermissionSet = acl.getPermissions(group);
        assertEquals(3, resultPermissionSet.size());
        assertTrue(resultPermissionSet.contains(permission));
        assertTrue(resultPermissionSet.contains(permission2));
        assertTrue(resultPermissionSet.contains(permission3));
        resultPermissionSet = acl.getPermissions(group2);
        assertEquals(0, resultPermissionSet.size());
    }
    /*
     * Class to test for PermissionSet getPermissions()
     */
    public void testGetPermissions() throws Exception
    {
        Group group = getGroup();
        Group group2 = getGroup();
        Role role = getRole();
        Role role2 = getRole();
        Role role3 = getRole();
        Permission permission = getPermission();
        Permission permission2 = getPermission();
        Permission permission3 = getPermission();
        groupManager.grant(group, role);
        groupManager.grant(group, role2);
        groupManager.grant(group2, role3);
        roleManager.grant(role, permission);
        roleManager.grant(role, permission2);
        roleManager.grant(role, permission3);
        roleManager.grant(role2, permission2);
        roleManager.grant(role2, permission3);
        Map roleSets = new HashMap();
        Map permissionSets = new HashMap();
        roleSets.put(group, ((SimpleGroup) group).getRoles());
        roleSets.put(group2, ((SimpleGroup) group2).getRoles());
        permissionSets.put(role, ((SimpleRole) role).getPermissions());
        permissionSets.put(role2, ((SimpleRole) role2).getPermissions());
        permissionSets.put(role3, ((SimpleRole) role3).getPermissions());
        acl = new DefaultAccessControlList(roleSets, permissionSets);
        PermissionSet resultPermissionSet = acl.getPermissions();
        assertEquals(3, resultPermissionSet.size());
    }
    /*
     * Class to test for boolean hasRole(Role, Group)
     */
    public void testHasRoleRoleGroup() throws Exception
    {
        Group group = getGroup();
        Group group2 = getGroup();
        Role role = getRole();
        Role role2 = getRole();
        Role role3 = getRole();
        groupManager.grant(group, role);
        groupManager.grant(group, role2);
        groupManager.grant(group2, role);
        groupManager.grant(group2, role3);
        Map roleSets = new HashMap();
        Map permissionSets = new HashMap();
        roleSets.put(group, ((SimpleGroup) group).getRoles());
        roleSets.put(group2, ((SimpleGroup) group2).getRoles());
        acl = new DefaultAccessControlList(roleSets, permissionSets);
        assertTrue(acl.hasRole(role, group));
        assertTrue(acl.hasRole(role, group2));
        assertTrue(acl.hasRole(role2, group));
        assertFalse(acl.hasRole(role2, group2));
        assertTrue(acl.hasRole(role, group2));
        assertFalse(acl.hasRole(role2, group2));
        assertTrue(acl.hasRole(role3, group2));
    }
    /*
     * Class to test for boolean hasRole(Role, GroupSet)
     */
    public void testHasRoleRoleGroupSet() throws Exception
    {
        Group group = getGroup();
        Group group2 = getGroup();
        Role role = getRole();
        Role role2 = getRole();
        Role role3 = getRole();
        groupManager.grant(group, role);
        groupManager.grant(group, role2);
        groupManager.grant(group2, role);
        groupManager.grant(group2, role3);
        Map roleSets = new HashMap();
        Map permissionSets = new HashMap();
        roleSets.put(group, ((SimpleGroup) group).getRoles());
        roleSets.put(group2, ((SimpleGroup) group2).getRoles());
        acl = new DefaultAccessControlList(roleSets, permissionSets);
        GroupSet groupSet = new GroupSet();
        groupSet.add(group);
        assertTrue(acl.hasRole(role, groupSet));
        assertTrue(acl.hasRole(role2, groupSet));
        assertFalse(acl.hasRole(role3, groupSet));
        groupSet.add(group2);
        assertTrue(acl.hasRole(role, groupSet));
        assertTrue(acl.hasRole(role2, groupSet));
        assertTrue(acl.hasRole(role3, groupSet));
        groupSet.add(group2);
    }
    /*
     * Class to test for boolean hasRole(String, String)
     */
    public void testHasRoleStringString() throws Exception
    {
        Group group = getGroup();
        Group group2 = getGroup();
        Role role = getRole();
        Role role2 = getRole();
        Role role3 = getRole();
        groupManager.grant(group, role);
        groupManager.grant(group, role2);
        groupManager.grant(group2, role);
        groupManager.grant(group2, role3);
        Map roleSets = new HashMap();
        Map permissionSets = new HashMap();
        roleSets.put(group, ((SimpleGroup) group).getRoles());
        roleSets.put(group2, ((SimpleGroup) group2).getRoles());
        acl = new DefaultAccessControlList(roleSets, permissionSets);
        assertTrue(acl.hasRole(role.getName(), group.getName()));
        assertTrue(acl.hasRole(role.getName(), group2.getName()));
        assertTrue(acl.hasRole(role2.getName(), group.getName()));
        assertFalse(acl.hasRole(role2.getName(), group2.getName()));
        assertTrue(acl.hasRole(role.getName(), group2.getName()));
        assertFalse(acl.hasRole(role2.getName(), group2.getName()));
        assertTrue(acl.hasRole(role3.getName(), group2.getName()));
    }
    /*
     * Class to test for boolean hasPermission(Permission, Group)
     */
    public void testHasPermissionPermissionGroup() throws Exception
    {
        Group group = getGroup();
        Group group2 = getGroup();
        Role role = getRole();
        Role role2 = getRole();
        Role role3 = getRole();
        Permission permission = getPermission();
        Permission permission2 = getPermission();
        Permission permission3 = getPermission();
        Permission permission4 = getPermission();
        groupManager.grant(group, role);
        groupManager.grant(group, role2);
        groupManager.grant(group2, role3);
        roleManager.grant(role, permission);
        roleManager.grant(role, permission2);
        roleManager.grant(role, permission3);
        roleManager.grant(role2, permission2);
        roleManager.grant(role2, permission3);
        roleManager.grant(role3, permission4);
        Map roleSets = new HashMap();
        Map permissionSets = new HashMap();
        roleSets.put(group, ((SimpleGroup) group).getRoles());
        roleSets.put(group2, ((SimpleGroup) group2).getRoles());
        permissionSets.put(role, ((SimpleRole) role).getPermissions());
        permissionSets.put(role2, ((SimpleRole) role2).getPermissions());
        permissionSets.put(role3, ((SimpleRole) role3).getPermissions());
        acl = new DefaultAccessControlList(roleSets, permissionSets);
        assertTrue(acl.hasPermission(permission, group));
        assertTrue(acl.hasPermission(permission2, group));
        assertTrue(acl.hasPermission(permission3, group));
        assertFalse(acl.hasPermission(permission4, group));
        assertTrue(acl.hasPermission(permission4, group2));
        assertFalse(acl.hasPermission(permission, group2));
    }
    /*
     * Class to test for boolean hasPermission(Permission, GroupSet)
     */
    public void testHasPermissionPermissionGroupSet() throws Exception
    {
        Group group = getGroup();
        Group group2 = getGroup();
        Group group3 = getGroup();
        Role role = getRole();
        Role role2 = getRole();
        Role role3 = getRole();
        Role role4 = getRole();
        Permission permission = getPermission();
        Permission permission2 = getPermission();
        Permission permission3 = getPermission();
        Permission permission4 = getPermission();
        Permission permission5 = getPermission();
        groupManager.grant(group, role);
        groupManager.grant(group, role2);
        groupManager.grant(group2, role3);
        groupManager.grant(group3, role4);
        roleManager.grant(role, permission);
        roleManager.grant(role, permission2);
        roleManager.grant(role, permission3);
        roleManager.grant(role2, permission2);
        roleManager.grant(role2, permission3);
        roleManager.grant(role3, permission4);
        roleManager.grant(role4, permission5);
        Map roleSets = new HashMap();
        Map permissionSets = new HashMap();
        roleSets.put(group, ((SimpleGroup) group).getRoles());
        roleSets.put(group2, ((SimpleGroup) group2).getRoles());
        roleSets.put(group3, ((SimpleGroup) group3).getRoles());
        permissionSets.put(role, ((SimpleRole) role).getPermissions());
        permissionSets.put(role2, ((SimpleRole) role2).getPermissions());
        permissionSets.put(role3, ((SimpleRole) role3).getPermissions());
        permissionSets.put(role4, ((SimpleRole) role4).getPermissions());
        acl = new DefaultAccessControlList(roleSets, permissionSets);
        GroupSet groupSet = new GroupSet();
        groupSet.add(group);
        groupSet.add(group2);
        assertTrue(acl.hasPermission(permission, groupSet));
        assertFalse(acl.hasPermission(permission5, groupSet));
        groupSet.add(group3);
        assertTrue(acl.hasPermission(permission5, groupSet));
    }
    /*
     * Class to test for boolean hasPermission(Permission)
     */
    public void testHasPermissionPermission() throws Exception
    {
        Group group = getGroup();
        Group group2 = getGroup();
        Group group3 = getGroup();
        Role role = getRole();
        Role role2 = getRole();
        Role role3 = getRole();
        Role role4 = getRole();
        Permission permission = getPermission();
        Permission permission2 = getPermission();
        Permission permission3 = getPermission();
        Permission permission4 = getPermission();
        Permission permission5 = getPermission();
        groupManager.grant(group, role);
        groupManager.grant(group, role2);
        groupManager.grant(group2, role3);
        groupManager.grant(group3, role4);
        roleManager.grant(role, permission);
        roleManager.grant(role, permission2);
        roleManager.grant(role, permission3);
        roleManager.grant(role2, permission2);
        roleManager.grant(role2, permission3);
        roleManager.grant(role3, permission4);
        roleManager.grant(role4, permission5);
        Map roleSets = new HashMap();
        Map permissionSets = new HashMap();
        roleSets.put(group, ((SimpleGroup) group).getRoles());
        roleSets.put(group2, ((SimpleGroup) group2).getRoles());
        roleSets.put(group3, ((SimpleGroup) group3).getRoles());
        permissionSets.put(role, ((SimpleRole) role).getPermissions());
        permissionSets.put(role2, ((SimpleRole) role2).getPermissions());
        permissionSets.put(role3, ((SimpleRole) role3).getPermissions());
        permissionSets.put(role4, ((SimpleRole) role4).getPermissions());
        acl = new DefaultAccessControlList(roleSets, permissionSets);
        assertTrue(acl.hasPermission(permission));
        assertTrue(acl.hasPermission(permission2));
        assertTrue(acl.hasPermission(permission3));
        assertTrue(acl.hasPermission(permission4));
        assertTrue(acl.hasPermission(permission5));
    }
   
    private int getId()
    {
        return ++counter;
    }
    private Role getRole() throws Exception
    {
        Role role = roleManager.getRoleInstance("Role " + getId());
        roleManager.addRole(role);
        return role;
    }
    private Group getGroup() throws Exception
    {
        Group group = groupManager.getGroupInstance("Group " + getId());
        groupManager.addGroup(group);
        return group;
    }
    private Permission getPermission() throws Exception
    {
        Permission permission = permissionManager.getPermissionInstance("Permission " + getId());
        permissionManager.addPermission(permission);
        return permission;
    }
}
