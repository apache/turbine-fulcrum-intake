package org.apache.fulcrum.security.model.dynamic.test;
/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Turbine" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Turbine", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.fulcrum.security.GroupManager;
import org.apache.fulcrum.security.PermissionManager;
import org.apache.fulcrum.security.RoleManager;
import org.apache.fulcrum.security.SecurityService;
import org.apache.fulcrum.security.UserManager;
import org.apache.fulcrum.security.entity.Group;
import org.apache.fulcrum.security.entity.Permission;
import org.apache.fulcrum.security.entity.Role;
import org.apache.fulcrum.security.entity.User;
import org.apache.fulcrum.security.model.dynamic.DynamicModelManager;
import org.apache.fulcrum.security.model.dynamic.entity.DynamicGroup;
import org.apache.fulcrum.security.model.dynamic.entity.DynamicRole;
import org.apache.fulcrum.security.model.dynamic.entity.DynamicUser;
import org.apache.fulcrum.security.util.PermissionSet;
import org.apache.fulcrum.security.util.UnknownEntityException;
import org.apache.fulcrum.testcontainer.BaseUnitTest;
/**
 * @author Eric Pugh
 * 
 * To change the template for this generated type comment go to Window>Preferences>Java>Code
 * Generation>Code and Comments
 */
public abstract class AbstractDynamicModelManagerTest extends BaseUnitTest
{
    protected Role role;
    protected DynamicModelManager modelManager;
    protected RoleManager roleManager;
    protected GroupManager groupManager;
    protected PermissionManager permissionManager;
    protected UserManager userManager;
    protected SecurityService securityService;

    public void setUp() throws Exception
    {
        super.setUp();
        roleManager = securityService.getRoleManager();
        userManager = securityService.getUserManager();
        groupManager = securityService.getGroupManager();
        permissionManager = securityService.getPermissionManager();
        modelManager = (DynamicModelManager) securityService.getModelManager();
    }

    public void tearDown()
    {
        this.release(roleManager);
        this.release(userManager);
        this.release(groupManager);
        this.release(permissionManager);
        this.release(modelManager);
    }
    /**
     * Constructor for AbstractRoleManagerTest.
     * 
     * @param arg0
     */
    public AbstractDynamicModelManagerTest(String arg0)
    {
        super(arg0);
    }

    public void testGrantRolePermission() throws Exception
    {
        Permission permission = permissionManager.getPermissionInstance();
        permission.setName("ANSWER_PHONE");
        permissionManager.addPermission(permission);
        role = roleManager.getRoleInstance("RECEPTIONIST");
        roleManager.addRole(role);
        modelManager.grant(role, permission);
        role = roleManager.getRoleById(role.getId());
        PermissionSet permissions = ((DynamicRole) role).getPermissions();
        assertEquals(1, permissions.size());
        assertTrue(((DynamicRole) role).getPermissions().contains(permission));
    }
    public void testRevokeRolePermission() throws Exception
    {
        Permission permission =
            securityService.getPermissionManager().getPermissionInstance();
        permission.setName("ANSWER_FAX");
        securityService.getPermissionManager().addPermission(permission);
        role = roleManager.getRoleInstance("SECRETARY");
        roleManager.addRole(role);
        modelManager.grant(role, permission);
        role = roleManager.getRoleById(role.getId());
        PermissionSet permissions = ((DynamicRole) role).getPermissions();
        assertEquals(1, permissions.size());
        modelManager.revoke(role, permission);
        role = roleManager.getRoleById(role.getId());
        permissions = ((DynamicRole) role).getPermissions();
        assertEquals(0, permissions.size());
        assertFalse(((DynamicRole) role).getPermissions().contains(permission));
    }
    public void testRevokeAllRole() throws Exception
    {
        Permission permission =
            securityService.getPermissionManager().getPermissionInstance();
        Permission permission2 =
            securityService.getPermissionManager().getPermissionInstance();
        permission.setName("SEND_SPAM");
        permission2.setName("ANSWER_EMAIL");
        securityService.getPermissionManager().addPermission(permission);
        securityService.getPermissionManager().addPermission(permission2);
        role = roleManager.getRoleInstance("HELPER");
        roleManager.addRole(role);
        modelManager.grant(role, permission);
        modelManager.grant(role, permission2);
        role = roleManager.getRoleById(role.getId());
        PermissionSet permissions = ((DynamicRole) role).getPermissions();
        assertEquals(2, permissions.size());
        modelManager.revokeAll(role);
        role = roleManager.getRoleById(role.getId());
        permissions = ((DynamicRole) role).getPermissions();
        assertEquals(0, permissions.size());
    }
    public void testRevokeAllUser() throws Exception
    {
        Group group = securityService.getGroupManager().getGroupInstance();
        group.setName("TEST_REVOKEALL");
        securityService.getGroupManager().addGroup(group);
        Group group2 = securityService.getGroupManager().getGroupInstance();
        group2.setName("TEST_REVOKEALL2");
        securityService.getGroupManager().addGroup(group2);
        User user = userManager.getUserInstance("Clint2");
        userManager.addUser(user, "clint");
        modelManager.grant(user, group);
        modelManager.grant(user, group2);

        modelManager.revokeAll(user);
        assertEquals(0, ((DynamicUser) user).getGroups().size());
        group =
            securityService.getGroupManager().getGroupByName("TEST_REVOKEALL");
        group2 =
            securityService.getGroupManager().getGroupByName("TEST_REVOKEALL2");
        assertFalse(((DynamicGroup) group).getUsers().contains(user));
        assertFalse(((DynamicGroup) group2).getUsers().contains(user));
    }
    public void testGrantUserGroup() throws Exception
    {
        Group group = securityService.getGroupManager().getGroupInstance();
        group.setName("TEST_GROUP");
        securityService.getGroupManager().addGroup(group);
        User user = userManager.getUserInstance("Clint");
        userManager.addUser(user, "clint");
        modelManager.grant(user, group);
        assertTrue(((DynamicUser) user).getGroups().contains(group));
        assertTrue(((DynamicGroup) group).getUsers().contains(user));
    }
    public void testRevokeUserGroup() throws Exception
    {
        Group group = securityService.getGroupManager().getGroupInstance();
        group.setName("TEST_REVOKE");
        securityService.getGroupManager().addGroup(group);
        User user = userManager.getUserInstance("Lima");
        userManager.addUser(user, "pet");
        modelManager.revoke(user, group);
        assertFalse(((DynamicUser) user).getGroups().contains(group));
        assertFalse(((DynamicGroup) group).getUsers().contains(user));
        user = userManager.getUser("Lima");
        assertFalse(((DynamicUser) user).getGroups().contains(group));
    }
    public void testGrantGroupRole() throws Exception
    {
        Role role = securityService.getRoleManager().getRoleInstance();
        role.setName("TEST_PERMISSION");
        securityService.getRoleManager().addRole(role);
        Group group = groupManager.getGroupInstance("TEST_GROUP2");
        groupManager.addGroup(group);
        modelManager.grant(group, role);
        group = groupManager.getGroupByName("TEST_GROUP2");
        assertTrue(((DynamicGroup) group).getRoles().contains(role));
        assertTrue(((DynamicRole) role).getGroups().contains(group));

    }
    public void testRevokeGroupRole() throws Exception
    {
        Role role = securityService.getRoleManager().getRoleInstance();
        role.setName("TEST_PERMISSION2");
        securityService.getRoleManager().addRole(role);
        Group group = groupManager.getGroupInstance("Lima2");
        groupManager.addGroup(group);
        modelManager.grant(group, role);
        modelManager.revoke(group, role);
        group = groupManager.getGroupByName("Lima2");
        assertFalse(((DynamicGroup) group).getRoles().contains(role));
        assertFalse(((DynamicRole) role).getGroups().contains(group));
    }

    public void testRetrieveingUsersByGroup() throws Exception
    {
        User user = userManager.getUserInstance("Joe3");
        userManager.addUser(user, "mc");
        String GROUP_NAME = "oddbug2";
        Group group = null;
        GroupManager groupManager = securityService.getGroupManager();
        try
        {
            group = groupManager.getGroupByName("");
        }
        catch (UnknownEntityException uue)
        {
            group = groupManager.getGroupInstance(GROUP_NAME);
            groupManager.addGroup(group);
        }
        assertNotNull(group);
        user = null;
        UserManager userManager = securityService.getUserManager();
        user = userManager.getUser("joe3");
        ((DynamicModelManager) securityService.getModelManager()).grant(
            user,
            group);
        assertTrue(((DynamicGroup) group).getUsers().contains(user));
        group = groupManager.getGroupByName(GROUP_NAME);
        Set users = ((DynamicGroup) group).getUsers();
        int size = users.size();
        assertEquals(1, size);
        // assertTrue("Check class:" + users.getClass().getName(),users instanceof UserSet);
        boolean found = false;
        Set newSet = new HashSet();
        for (Iterator i = users.iterator(); i.hasNext();)
        {
            User u = (User) i.next();
            System.out.println(u);
            if (u.equals(user))
            {
                found = true;
                newSet.add(u);
            }
        }
        assertTrue(found);
        assertTrue(users.contains(user));
    }
}