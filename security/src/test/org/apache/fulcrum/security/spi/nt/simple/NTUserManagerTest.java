/*
 * Created on Aug 23, 2003
 *
 */
package org.apache.fulcrum.security.spi.nt.simple;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.security.SecurityService;
import org.apache.fulcrum.security.acl.AccessControlList;
import org.apache.fulcrum.security.entity.Group;
import org.apache.fulcrum.security.model.simple.entity.SimpleUser;
import org.apache.fulcrum.security.model.simple.manager.AbstractUserManagerTest;
import org.apache.fulcrum.security.model.simple.manager.SimpleUserManager;
import com.tagish.auth.win32.NTSystem;
/**
 * @author Eric Pugh
 *
 * Test the NT implementation of the user manager.  This test traps
 * some exceptions that can be thrown if there is NO nt dll.
 */
public class NTUserManagerTest extends AbstractUserManagerTest
{
    private static Log log = LogFactory.getLog(NTUserManagerTest.class);
    private static final String ERROR_MSG = "Not supported by NT User Manager";
    private static final String USERNAME = "Eric Pugh";
    private static final String DOMAIN = "IQUITOS";
    private static final String PASSWORD = "";
    private static final String COMBO_USERNAME = DOMAIN + "/" + "Guest";
    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(NTUserManagerTest.class);
    }
    public void setUp()
    {
        try
        {
            this.setRoleFileName(null);
            this.setConfigurationFileName("src/test/SimpleNT.xml");
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
    /**
    	* Constructor for MemoryPermissionManagerTest.
    	* @param arg0
    	*/
    public NTUserManagerTest(String arg0)
    {
        super(arg0);
    }
    public void testCheckExists() throws Exception
    {
        try
        {
            user = new SimpleUser();
            user.setName("IQUITOS/Guest");
            ((SimpleUser) user).setPassword("");
            assertTrue(userManager.checkExists(user));
        }
        catch (UnsatisfiedLinkError ule)
        {
            log.info("Unit test not being run due to missing NT DDLL");
        }
    }
    public void testCheckExistsFails() throws Exception
    {
        try
        {
            user = new SimpleUser();
            user.setName("MCD\\Ronald Mcdonaled");
            ((SimpleUser) user).setPassword("");
            assertFalse(userManager.checkExists(user));
        }
        catch (NoClassDefFoundError ule)
        {
            log.info("Unit test not being run due to missing NT DDLL");
        }
    }
    /**
    *  tests getting an NT username
    */
    public void testNTGetName() throws Exception
    {
        try
        {
            NTSystem ntSystem = new NTSystem();
            assertTrue("Name is eric:" + ntSystem.getName(), USERNAME.equals(ntSystem.getName()));
        }
        catch (NoClassDefFoundError ule)
        {
            log.info("Unit test not being run due to missing NT DDLL");
        }
    }
    /**
    	*  tests getting an NT Domain
    	*/
    public void testGetDomain() throws Exception
    {
        try
        {
            NTSystem ntSystem = new NTSystem();
            assertTrue("Domain is:" + ntSystem.getDomain(), DOMAIN.equals(ntSystem.getDomain()));
        }
        catch (NoClassDefFoundError ule)
        {
            log.info("Unit test not being run due to missing NT DDLL");
        }
    }
    /**
    	*  tests logging on a different user
    	*/
    public void testLoginAsUser() throws Exception
    {
        try
        {
            NTSystem ntSystem = new NTSystem();
            char password[] = "editor!".toCharArray();
            ntSystem.logon(USERNAME, password, DOMAIN);
            String groups[] = ntSystem.getGroupNames(false);
            for (int i = 0; i < groups.length; i++)
            {
                System.out.println("Groups :" + groups[i]);
            }
            ntSystem.logoff();
            assertTrue("User is:" + ntSystem.getName(), USERNAME.equals(ntSystem.getName()));
            assertTrue("Domain is:" + ntSystem.getName(), "IQUITOS".equals(ntSystem.getDomain()));
            assertTrue(
                "Primary Group is:" + ntSystem.getPrimaryGroupName(),
                "None".equals(ntSystem.getPrimaryGroupName()));
        }
        catch (NoClassDefFoundError ule)
        {
            log.info("Unit test not being run due to missing NT DDLL");
        }
    }
    /*
       * Class to test for User retrieve(String, String)
       */
    public void testRetrieveStringString() throws Exception
    {
        try
        {
            user = userManager.getUser(COMBO_USERNAME, PASSWORD);
            assertNotNull(user);
            assertTrue(((SimpleUser) user).getGroups().size() > 0);
        }
        catch (NoClassDefFoundError ule)
        {
            log.info("Unit test not being run due to missing NT DDLL");
        }
    }
    public void testAuthenticate() throws Exception
    {
        try
        {
            user = userManager.getUserInstance(COMBO_USERNAME);
            userManager.authenticate(user, PASSWORD);
        }
        catch (NoClassDefFoundError ule)
        {
            log.info("Unit test not being run due to missing NT DDLL");
        }
    }
    public void testGetACL() throws Exception
    {
        try
        {
            user = userManager.getUserInstance(COMBO_USERNAME);
            userManager.authenticate(user, PASSWORD);
            AccessControlList acl = userManager.getACL(user);
            assertNotNull(acl);
        }
        catch (NoClassDefFoundError ule)
        {
            log.info("Unit test not being run due to missing NT DDLL");
        }
    }
    /********* ALL BELOW HERE THROW RUNTIME EXCEPTIONS *********/
    /*
    	* Class to test for User retrieve(String)
    	*/
    public void testRetrieveString() throws Exception
    {
        try
        {
            user = userManager.getUser("QuietMike");
            fail("Should throw runtime exception");
        }
        catch (RuntimeException re)
        {
            assertTrue(re.getMessage().equals(ERROR_MSG));
        }
    }
    public void testChangePassword() throws Exception
    {
        try
        {
            userManager.changePassword(user, "WrongPWD", "JC");
            fail("Should throw runtime exception");
        }
        catch (RuntimeException re)
        {
            assertTrue(re.getMessage().equals(ERROR_MSG));
        }
    }
    public void testForcePassword() throws Exception
    {
        user = userManager.getUserInstance("BOB");
        try
        {
            userManager.forcePassword(user, "JC_SUBSET");
            fail("Should throw runtime exception");
        }
        catch (RuntimeException re)
        {
            assertTrue(re.getMessage().equals(ERROR_MSG));
        }
    }
    public void testRevokeAll() throws Exception
    {
        try
        {
            userManager.revokeAll(user);
            fail("Should throw runtime exception");
        }
        catch (RuntimeException re)
        {
            assertTrue(re.getMessage().equals(ERROR_MSG));
        }
    }
    public void testSaveUser() throws Exception
    {
        try
        {
            userManager.saveUser(user);
            fail("Should throw runtime exception");
        }
        catch (RuntimeException re)
        {
            assertTrue(re.getMessage().equals(ERROR_MSG));
        }
    }
    public void testGrantUserGroup() throws Exception
    {
        Group group = securityService.getGroupManager().getGroupInstance();
        group.setName("TEST_GROUP");
        securityService.getGroupManager().addGroup(group);
        try
        {
            ((SimpleUserManager) userManager).grant(user, group);
            fail("Should throw runtime exception");
        }
        catch (RuntimeException re)
        {
            assertTrue(re.getMessage().equals(ERROR_MSG));
        }
    }
    public void testRevokeUserGroup() throws Exception
    {
        try
        {
            Group group = securityService.getGroupManager().getGroupInstance();
            group.setName("TEST_REVOKE");
            securityService.getGroupManager().addGroup(group);
            ((SimpleUserManager) userManager).revoke(user, group);
            fail("Should throw runtime exception");
        }
        catch (RuntimeException re)
        {
            assertTrue(re.getMessage().equals(ERROR_MSG));
        }
    }
    public void testRemoveUser() throws Exception
    {
        try
        {
            userManager.removeUser(user);
            fail("Should throw runtime exception");
        }
        catch (RuntimeException re)
        {
            assertTrue(re.getMessage().equals(ERROR_MSG));
        }
    }
    public void testAddUser() throws Exception
    {
        try
        {
            user = userManager.getUserInstance("Joe1");
            userManager.addUser(user, "mc");
        }
        catch (RuntimeException re)
        {
            assertTrue(re.getMessage().equals(ERROR_MSG));
        }
    }
    public void testRetrieveingUsersByGroup() throws Exception
    {
        try
        {
            user = userManager.getUserInstance("Joe1");
            userManager.addUser(user, "mc");
        }
        catch (RuntimeException re)
        {
            assertTrue(re.getMessage().equals(ERROR_MSG));
        }
    }
}
