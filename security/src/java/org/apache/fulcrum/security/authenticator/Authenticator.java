/*
 * Created on Aug 25, 2003
 *
 */
package org.apache.fulcrum.security.authenticator;
import org.apache.fulcrum.security.entity.User;
import org.apache.fulcrum.security.util.DataBackendException;
/**
 * @author Eric Pugh
 *
 * Interface for creating an authenticator.
 */
public interface Authenticator
{
	
	/** Avalon role - used to id the component within the manager */
	String ROLE = Authenticator.class.getName();
	
    public boolean authenticate(User user, String password)
        throws  DataBackendException;
}
