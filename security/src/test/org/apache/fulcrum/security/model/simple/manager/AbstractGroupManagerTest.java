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

import org.apache.fulcrum.security.GroupManager;
import org.apache.fulcrum.security.SecurityService;
import org.apache.fulcrum.security.entity.Group;
import org.apache.fulcrum.security.model.simple.entity.SimpleGroup;
import org.apache.fulcrum.security.util.GroupSet;
import org.apache.fulcrum.security.util.UnknownEntityException;
import org.apache.fulcrum.testcontainer.BaseUnitTest;

/**
 * @author Eric Pugh
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public abstract class AbstractGroupManagerTest extends BaseUnitTest
{
    protected Group group;
    protected GroupManager groupManager;
    protected SecurityService securityService;

    /**
     * Constructor for AbstractGroupManagerTest.
     * @param arg0
     */
    public AbstractGroupManagerTest(String arg0)
    {
        super(arg0);
    }
    /*
     * Class to test for Group getGroupInstance()
     */
    public void testGetGroupInstance() throws Exception
    {
        group = groupManager.getGroupInstance();
        assertNotNull(group);
        assertTrue(group.getName() == null);
    }
    /*
     * Class to test for Group getGroupInstance(String)
     */
    public void testGetGroupInstanceString() throws Exception
    {
        group = groupManager.getGroupInstance("DOG_CATCHER");
        assertEquals("DOG_CATCHER".toLowerCase(), group.getName());
    }
    public void testGetGroup() throws Exception
    {
        group = groupManager.getGroupInstance("DOG_CATCHER2");
        groupManager.addGroup(group);
        Group group2 = groupManager.getGroupByName("DOG_CATCHER2");
        assertEquals(group.getName(), group2.getName());
    }
    public void testGetGroupByName() throws Exception
    {
        group = groupManager.getGroupInstance("CLEAN_KENNEL");
        groupManager.addGroup(group);
        Group group2 = groupManager.getGroupByName("CLEAN_KENNEL");
        assertEquals(group.getName(), group2.getName());
        group2 = groupManager.getGroupByName("Clean_KeNNel");
        assertEquals(group.getName(), group2.getName());
    }
    public void testGetGroupById() throws Exception
    {
        group = groupManager.getGroupInstance("CLEAN_KENNEL_A");
        groupManager.addGroup(group);
        Group group2 = groupManager.getGroupById(group.getId());
        assertEquals(group.getName(), group2.getName());
    }
    public void testGetAllGroups() throws Exception
    {
        int size = groupManager.getAllGroups().size();
        group = groupManager.getGroupInstance("CLEAN_KENNEL_J");
        groupManager.addGroup(group);
        GroupSet groupSet = groupManager.getAllGroups();
        assertEquals(size + 1, groupSet.size());
    }
    public void testRemoveGroup() throws Exception
    {
        group = groupManager.getGroupInstance("CLEAN_KENNEL_K");
        groupManager.addGroup(group);
        int size = groupManager.getAllGroups().size();
        assertEquals(0, ((SimpleGroup) group).getUsers().size());
        assertEquals(0, ((SimpleGroup) group).getRoles().size());
        groupManager.removeGroup(group);
        try
        {
            Group group2 = groupManager.getGroupById(group.getId());
            fail("Should have thrown UEE");
        }
        catch (UnknownEntityException uee)
        {
            //good
        }
        assertEquals(size - 1, groupManager.getAllGroups().size());
    }
    public void testRenameGroup() throws Exception
    {
        group = groupManager.getGroupInstance("CLEAN_KENNEL_X");
        groupManager.addGroup(group);
        int size = groupManager.getAllGroups().size();
        groupManager.renameGroup(group, "CLEAN_GROOMING_ROOM");
        Group group2 = groupManager.getGroupById(group.getId());
        assertEquals("CLEAN_GROOMING_ROOM".toLowerCase(), group2.getName());
        assertEquals(size, groupManager.getAllGroups().size());
    }
    public void testCheckExists() throws Exception
    {
        group = groupManager.getGroupInstance("GREET_PEOPLE");
        groupManager.addGroup(group);
        assertTrue(groupManager.checkExists(group));
        Group group2 = groupManager.getGroupInstance("WALK_DOGS");
        assertFalse(groupManager.checkExists(group2));
    }
    public void testAddGroup() throws Exception
    {
        group = groupManager.getGroupInstance("CLEAN_RABBIT_HUTCHES");
        assertNull(group.getId());
        groupManager.addGroup(group);
        assertNotNull(group.getId());
        assertNotNull(groupManager.getGroupById(group.getId()));
    }

}
