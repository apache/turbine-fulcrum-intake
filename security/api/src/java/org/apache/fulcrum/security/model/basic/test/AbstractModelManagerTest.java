package org.apache.fulcrum.security.model.basic.test;
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

import org.apache.fulcrum.security.GroupManager;
import org.apache.fulcrum.security.SecurityService;
import org.apache.fulcrum.security.UserManager;
import org.apache.fulcrum.security.entity.Group;
import org.apache.fulcrum.security.entity.Role;
import org.apache.fulcrum.security.entity.User;
import org.apache.fulcrum.security.model.basic.BasicModelManager;
import org.apache.fulcrum.security.model.basic.entity.BasicGroup;
import org.apache.fulcrum.security.model.basic.entity.BasicUser;
import org.apache.fulcrum.testcontainer.BaseUnitTest;
/**
 * @author Eric Pugh
 * 
 * To change the template for this generated type comment go to Window>Preferences>Java>Code
 * Generation>Code and Comments
 */
public abstract class AbstractModelManagerTest extends BaseUnitTest
{
    protected Role role;
    protected BasicModelManager modelManager;
    protected GroupManager groupManager;
    protected UserManager userManager;
    protected SecurityService securityService;

    public void setUp() throws Exception
    {
        super.setUp();
        userManager = securityService.getUserManager();
        groupManager = securityService.getGroupManager();
        modelManager=(BasicModelManager)securityService.getModelManager();
    }
    /**
	 * Constructor for AbstractRoleManagerTest.
	 * 
	 * @param arg0
	 */
    public AbstractModelManagerTest(String arg0)
    {
        super(arg0);
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
        assertEquals(0, ((BasicUser) user).getGroups().size());
        group = securityService.getGroupManager().getGroupByName("TEST_REVOKEALL");
        group2 = securityService.getGroupManager().getGroupByName("TEST_REVOKEALL2");
        assertFalse(((BasicGroup) group).getUsersAsSet().contains(user));
        assertFalse(((BasicGroup) group2).getUsers().contains(user));
    }
    public void testGrantUserGroup() throws Exception
    {
        Group group = securityService.getGroupManager().getGroupInstance();
        group.setName("TEST_GROUP");
        securityService.getGroupManager().addGroup(group);
        User user = userManager.getUserInstance("Clint");
        userManager.addUser(user, "clint");
        modelManager.grant(user, group);
        assertTrue(((BasicUser) user).getGroups().contains(group));
        assertTrue(((BasicGroup) group).getUsers().contains(user));
    }
    public void testRevokeUserGroup() throws Exception
    {
        Group group = securityService.getGroupManager().getGroupInstance();
        group.setName("TEST_REVOKE");
        securityService.getGroupManager().addGroup(group);
        User user = userManager.getUserInstance("Lima");
        userManager.addUser(user, "pet");
        modelManager.revoke(user, group);
        assertFalse(((BasicUser) user).getGroups().contains(group));
        assertFalse(((BasicGroup) group).getUsers().contains(user));
        user = userManager.getUser("Lima");
        assertFalse(((BasicUser) user).getGroups().contains(group));
    }
}
