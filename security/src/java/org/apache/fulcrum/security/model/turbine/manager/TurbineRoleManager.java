package org.apache.fulcrum.security.model.turbine.manager;
import org.apache.fulcrum.security.RoleManager;
import org.apache.fulcrum.security.entity.Permission;
import org.apache.fulcrum.security.entity.Role;
import org.apache.fulcrum.security.util.DataBackendException;
import org.apache.fulcrum.security.util.UnknownEntityException;
/**
 * @author Eric Pugh
 *
 */
public interface TurbineRoleManager extends RoleManager
{
    /**
     * Puts a permission in a role
     *
     * This method is used when adding a permission to a role
     *
     * @param user the User.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws UnknownEntityException if the account is not present.
     */
    void grant(Role role, Permission permission) throws DataBackendException, UnknownEntityException;
    /**
	 * Removes a permission from a role
	 *
	 *
	 * @param role the Role.
	 * @throws DataBackendException if there was an error accessing the data
	 *         backend.
	 * @throws UnknownEntityException if the user or group is not present.
	 */
    void revoke(Role role, Permission permission) throws DataBackendException, UnknownEntityException;

	    
}
