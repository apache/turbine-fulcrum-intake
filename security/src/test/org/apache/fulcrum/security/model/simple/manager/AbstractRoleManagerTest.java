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

import org.apache.fulcrum.security.RoleManager;
import org.apache.fulcrum.security.SecurityService;
import org.apache.fulcrum.security.entity.Role;
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
    protected RoleManager roleManager;
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

    
    public void testAddRole() throws Exception
    {
        role = roleManager.getRoleInstance("DOG_NAPPER");
        assertNull(role.getId());
        roleManager.addRole(role);
        assertNotNull(role.getId());
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
