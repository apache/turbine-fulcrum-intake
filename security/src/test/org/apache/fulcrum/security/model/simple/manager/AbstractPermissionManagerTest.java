package org.apache.fulcrum.security.model.simple.manager;
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
    protected Permission permission;
    protected PermissionManager permissionManager;
    protected SecurityService securityService;


    /**
     * Constructor for PermissionManagerTest.
     * @param arg0
     */
    public AbstractPermissionManagerTest(String arg0)

    {
        super(arg0);
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
        assertEquals(size + 1, permissionSet.size());
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
		assertNull(permission.getId());
        permissionManager.addPermission(permission);
        assertNotNull(permission.getId());
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
        ((SimpleRoleManager) securityService.getRoleManager()).grant(role, permission);
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
