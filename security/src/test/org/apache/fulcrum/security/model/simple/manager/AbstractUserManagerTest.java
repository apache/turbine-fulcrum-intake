/*
 * Created on Aug 22, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.apache.fulcrum.security.model.simple.manager;
import org.apache.fulcrum.security.SecurityService;
import org.apache.fulcrum.security.UserManager;
import org.apache.fulcrum.security.acl.AccessControlList;
import org.apache.fulcrum.security.entity.Group;
import org.apache.fulcrum.security.entity.User;
import org.apache.fulcrum.security.model.simple.entity.SimpleGroup;
import org.apache.fulcrum.security.model.simple.entity.SimpleUser;
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
    public abstract void doCustomSetup() throws Exception;
    /**
     * Constructor for AbstractUserManagerTest.
     * @param arg0
     */
    public AbstractUserManagerTest(String arg0)
    
    {
        super(arg0);
    }
    public void setUp()
    {
        try
        {
            doCustomSetup();
            securityService = (SecurityService) lookup(SecurityService.ROLE);
            userManager = securityService.getUserManager();
        }
        catch (Exception e)
        {
            fail(e.toString());
        }
    }
    public void tearDown()
    {
        user = null;
        userManager = null;
        securityService = null;
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
    public void testRevokeAll() throws Exception
    {
        Group group = securityService.getGroupManager().getGroupInstance();
        group.setName("TEST_REVOKEALL");
        securityService.getGroupManager().addGroup(group);
        Group group2 = securityService.getGroupManager().getGroupInstance();
        group2.setName("TEST_REVOKEALL2");
        securityService.getGroupManager().addGroup(group2);
        user = userManager.getUserInstance("Clint2");
        userManager.addUser(user, "clint");
        ((SimpleUserManager) userManager).grant(user, group);
        ((SimpleUserManager) userManager).grant(user, group2);
        userManager.revokeAll(user);
		assertEquals(0, ((SimpleUser) user).getGroups().size());
		group = securityService.getGroupManager().getGroupByName("TEST_REVOKEALL");
		group2 = securityService.getGroupManager().getGroupByName("TEST_REVOKEALL2");
		assertFalse(((SimpleGroup) group).getUsers().contains(user));
		assertFalse(((SimpleGroup) group2).getUsers().contains(user));
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
    public void testGrantUserGroup() throws Exception
    {
        Group group = securityService.getGroupManager().getGroupInstance();
        group.setName("TEST_GROUP");
        securityService.getGroupManager().addGroup(group);
        user = userManager.getUserInstance("Clint");
        userManager.addUser(user, "clint");
        ((SimpleUserManager) userManager).grant(user, group);
        assertTrue(((SimpleUser) user).getGroups().contains(group));
		assertTrue(((SimpleGroup)group).getUsers().contains(user));
        
    }
    public void testRevokeUserGroup() throws Exception
    {
        Group group = securityService.getGroupManager().getGroupInstance();
        group.setName("TEST_REVOKE");
        securityService.getGroupManager().addGroup(group);
        user = userManager.getUserInstance("Lima");
        userManager.addUser(user, "pet");
        ((SimpleUserManager) userManager).revoke(user, group);
        assertFalse(((SimpleUser) user).getGroups().contains(group));
		assertFalse(((SimpleGroup)group).getUsers().contains(user));
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
        userManager.addUser(user, "mc");
        user = userManager.getUserInstance("Joe2");
        userManager.addUser(user, "mc");
        assertTrue(user.getId() > 0);
        assertNotNull(userManager.getUser(user.getName()));
    }
}
