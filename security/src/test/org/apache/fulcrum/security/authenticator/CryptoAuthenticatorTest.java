/*
 * Created on Aug 25, 2003
 *
 */
package org.apache.fulcrum.security.authenticator;
import org.apache.fulcrum.security.entity.User;
import org.apache.fulcrum.security.model.simple.entity.SimpleUser;
import org.apache.fulcrum.testcontainer.BaseUnitTest;
/**
 * @author Eric Pugh
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CryptoAuthenticatorTest extends BaseUnitTest
{
    private static final String preDefinedInput = "Oeltanks";
    private static final String preDefinedResult = "uVDiJHaavRYX8oWt5ctkaa7j1cw=";
    /**
    	* Constructor for CryptoAuthenticatorTest.
    	* @param arg0
    	*/
    public CryptoAuthenticatorTest(String arg0)
    {
        super(arg0);
    }
    public void setUp()
    {
        try
        {
            this.setRoleFileName(null);
            this.setConfigurationFileName("src/test/CryptoAuthenticator.xml");
        }
        catch (Exception e)
        {
            fail(e.toString());
        }
    }
    public void testAuthenticate() throws Exception
    {
        User user = new SimpleUser();
        user.setName("Bob");
        user.setPassword(preDefinedResult);
        Authenticator authenticator = (Authenticator)lookup(Authenticator.ROLE);
        assertTrue(authenticator.authenticate(user, preDefinedInput));
        assertFalse(authenticator.authenticate(user, "mypassword"));
    }
}
