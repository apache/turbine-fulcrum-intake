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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.fulcrum.security.GroupManager;
import org.apache.fulcrum.security.SecurityService;
import org.apache.fulcrum.security.UserManager;
import org.apache.fulcrum.security.acl.AccessControlList;
import org.apache.fulcrum.security.entity.Group;
import org.apache.fulcrum.security.entity.User;
import org.apache.fulcrum.security.model.simple.SimpleModelManager;
import org.apache.fulcrum.security.model.simple.entity.SimpleGroup;
import org.apache.fulcrum.security.util.PasswordMismatchException;
import org.apache.fulcrum.security.util.UnknownEntityException;
import org.apache.fulcrum.testcontainer.BaseUnitTest;
/**
 * @author Eric Pugh
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public abstract class AbstractUserManagerTest extends BaseUnitTest
{
    protected User user;
    protected UserManager userManager;
    protected SecurityService securityService;
    
    /**
     * Constructor for AbstractUserManagerTest.
     * @param arg0
     */
    public AbstractUserManagerTest(String arg0)
    {
        super(arg0);
    }
    public void testCheckExists() throws Exception
    {
        user = userManager.getUserInstance("Philip");
        userManager.addUser(user, "bobo");
        assertTrue(userManager.checkExists("philip"));
        assertTrue(userManager.checkExists(user));
        assertFalse(userManager.checkExists("ImaginaryFriend"));
        user = userManager.getUserInstance("ImaginaryFriend");
        assertFalse(userManager.checkExists(user));
    }
    /*
     * Class to test for User retrieve(String)
     */
    public void testRetrieveString() throws Exception
    {
        user = userManager.getUserInstance("QuietMike");
        userManager.addUser(user, "bobo");
        user = userManager.getUser("QuietMike");
        assertNotNull(user);
    }
    /*
     * Class to test for User retrieve(String, String)
     */
    public void testRetrieveStringString() throws Exception
    {
        user = userManager.getUserInstance("Richard");
        userManager.addUser(user, "va");
        user = userManager.getUser("Richard", "va");
        assertNotNull(user);
        user = userManager.getUser("richard", "va");
        assertNotNull(user);
        try
        {
            user = userManager.getUser("richard", "VA");
            fail("should have thrown PasswordMismatchException");
        }
        catch (PasswordMismatchException pme)
        {
            //good
        }
    }
    public void testAuthenticate() throws Exception
    {
        user = userManager.getUserInstance("Kay");
        userManager.addUser(user, "jc");
        userManager.authenticate(user, "jc");
        try
        {
            userManager.authenticate(user, "JC");
            fail("should have thrown PasswordMismatchException");
        }
        catch (PasswordMismatchException pme)
        {
            //good
        }
    }
    public void testChangePassword() throws Exception
    {
        user = userManager.getUserInstance("Jonathan");
        userManager.addUser(user, "jc");
        try
        {
            userManager.changePassword(user, "WrongPWD", "JC");
            fail("should have thrown PasswordMismatchException");
        }
        catch (PasswordMismatchException pme)
        {
            //good
        }
        userManager.changePassword(user, "jc", "JC");
        userManager.authenticate(user, "JC");
    }
    public void testForcePassword() throws Exception
    {
        user = userManager.getUserInstance("Connor");
        userManager.addUser(user, "jc_subset");
        userManager.forcePassword(user, "JC_SUBSET");
        userManager.authenticate(user, "JC_SUBSET");
    }
    /*
     * Class to test for User getUserInstance()
     */
    public void testGetUserInstance() throws Exception
    {
        user = userManager.getUserInstance();
        assertNotNull(user);
        assertTrue(user.getName() == null);
    }
    /*
     * Class to test for User getUserInstance(String)
     */
    public void testGetUserInstanceString() throws Exception
    {
        user = userManager.getUserInstance("Philip");
        assertEquals("philip", user.getName());
    }
    
    /**
     * Need to figure out if save is something we want..
     * right now it just bloes up if you actually cahnge anything.
     * @todo figur out what to do here...
     * @throws Exception
     */
    public void testSaveUser() throws Exception
    {
        user = userManager.getUserInstance("Kate");
        userManager.addUser(user, "katiedid");
        user = userManager.getUser(user.getName());
        // user.setName("Katherine");
        userManager.saveUser(user);
        assertEquals("kate", userManager.getUser(user.getName()).getName());
    }
   
    public void testGetACL() throws Exception
    {
        user = userManager.getUserInstance("Tony");
        userManager.addUser(user, "california");
        AccessControlList acl = userManager.getACL(user);
        assertNotNull(acl);
    }
    public void testRemoveUser() throws Exception
    {
        user = userManager.getUserInstance("Rick");
        userManager.addUser(user, "nb");
        userManager.removeUser(user);
        try
        {
            User user2 = userManager.getUser(user.getName());
            fail("Should have thrown UEE");
        }
        catch (UnknownEntityException uee)
        {
            //good
        }
    }
    public void testAddUser() throws Exception
    {
        user = userManager.getUserInstance("Joe1");
		assertNull(user.getId());
        userManager.addUser(user, "mc");
        user = userManager.getUserInstance("Joe2");
		assertNull(user.getId());
        userManager.addUser(user, "mc");
        assertNotNull(user.getId());
        assertNotNull(userManager.getUser(user.getName()));
    }
    public void testRetrieveingUsersByGroup() throws Exception
    {
        user = userManager.getUserInstance("Joe3");
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
        User user = null;
        UserManager userManager = securityService.getUserManager();
        user = userManager.getUser("joe3");
        ((SimpleModelManager)securityService.getModelManager()).grant(user, group);
        assertTrue(((SimpleGroup) group).getUsers().contains(user));
        group = groupManager.getGroupByName(GROUP_NAME);
        Set users = ((SimpleGroup) group).getUsers();
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
