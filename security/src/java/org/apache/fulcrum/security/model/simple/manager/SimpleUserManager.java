package org.apache.fulcrum.security.model.simple.manager;
import org.apache.fulcrum.security.UserManager;
import org.apache.fulcrum.security.entity.Group;
import org.apache.fulcrum.security.entity.User;
import org.apache.fulcrum.security.util.DataBackendException;
import org.apache.fulcrum.security.util.UnknownEntityException;
/**
 * @author Eric Pugh
 *
 */
public interface SimpleUserManager extends UserManager
{
    /**
     * Puts a user in a group.
     *
     * This method is used when adding a user to a group
     *
     * @param user the User.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws UnknownEntityException if the account is not present.
     */
    void grant(User user, Group group) throws DataBackendException, UnknownEntityException;
    /**
	 * Removes a user from a group
	 *
	 *
	 * @param user the User.
	 * @throws DataBackendException if there was an error accessing the data
	 *         backend.
	 * @throws UnknownEntityException if the user or group is not present.
	 */
    void revoke(User user, Group group) throws DataBackendException, UnknownEntityException;
}
