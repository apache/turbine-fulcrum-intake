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
public class NoOpAuthenticatorTest extends TestCase
{
    public void testAuthenticate() throws Exception
    {
    	User user = new SimpleUser();
    	user.setName("Bob");
    	user.setPassword("myPassword");
    	Authenticator authenticator = new NoOpAuthenticator();
		assertTrue(authenticator.authenticate(user,"myPassword"));
		assertTrue(authenticator.authenticate(user,"mypassword"));
		assertTrue(authenticator.authenticate(null,null));
    }
}
