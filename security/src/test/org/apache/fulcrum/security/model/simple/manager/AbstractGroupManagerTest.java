/*
 * Created on Aug 21, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.apache.fulcrum.security.model.simple.manager;
import org.apache.fulcrum.security.GroupManager;
import org.apache.fulcrum.security.SecurityService;
import org.apache.fulcrum.security.entity.Group;
import org.apache.fulcrum.security.entity.Role;
import org.apache.fulcrum.security.model.simple.entity.SimpleGroup;
import org.apache.fulcrum.security.model.simple.entity.SimpleRole;
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
    private Group group;
    private GroupManager groupManager;
    private SecurityService securityService;
    public abstract void doCustomSetup() throws Exception;
    public void setUp()
    
    {
        try
        {
            doCustomSetup();
            securityService = (SecurityService) lookup(SecurityService.ROLE);
            groupManager = securityService.getGroupManager();
        }
        catch (Exception e)
        {
            fail(e.toString());
        }
    }
    public void tearDown()
    {
        group = null;
        groupManager = null;
        securityService = null;
    }
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
		assertEquals(0,((SimpleGroup)group).getUsers().size());
		assertEquals(0,((SimpleGroup)group).getRoles().size());
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
        groupManager.addGroup(group);
        assertNotNull(groupManager.getGroupById(group.getId()));
    }
    public void testGrantGroupRole() throws Exception
    {
        Role role = securityService.getRoleManager().getRoleInstance();
        role.setName("TEST_PERMISSION");
        securityService.getRoleManager().addRole(role);
        group = groupManager.getGroupInstance("TEST_ROLE");
        groupManager.addGroup(group);
        ((SimpleGroupManager) groupManager).grant(group, role);
        group = groupManager.getGroupByName("TEST_ROLE");
		assertTrue(((SimpleGroup) group).getRoles().contains(role));
		assertTrue(((SimpleRole) role).getGroups().contains(group));
		
    }
    public void testRevokeGroupRole() throws Exception
    {
        Role role = securityService.getRoleManager().getRoleInstance();
        role.setName("TEST_PERMISSION2");
        securityService.getRoleManager().addRole(role);
        group = groupManager.getGroupInstance("Lima2");
        groupManager.addGroup(group);
        ((SimpleGroupManager) groupManager).grant(group, role);
        ((SimpleGroupManager) groupManager).revoke(group, role);
        group = groupManager.getGroupByName("Lima2");
		assertFalse(((SimpleGroup) group).getRoles().contains(role));
		assertFalse(((SimpleRole) role).getGroups().contains(group));
    }
}
