/*
 * Created on Aug 25, 2003
 *
 */
package org.apache.fulcrum.security.authenticator;

import org.apache.fulcrum.security.entity.User;
import org.apache.fulcrum.security.model.simple.entity.SimpleUser;

import junit.framework.TestCase;

/**
 * @author Eric Pugh
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class TextMatchAuthenticatorTest extends TestCase
{
    public void testAuthenticate() throws Exception
    {
    	User user = new SimpleUser();
    	user.setName("Bob");
    	user.setPassword("myPassword");
    	Authenticator authenticator = new TextMatchAuthenticator();
		assertTrue(authenticator.authenticate(user,"myPassword"));
		assertFalse(authenticator.authenticate(user,"mypassword"));
    }
}
