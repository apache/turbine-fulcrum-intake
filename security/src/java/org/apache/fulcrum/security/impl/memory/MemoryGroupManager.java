
package org.apache.fulcrum.security.impl.memory;

import org.apache.fulcrum.security.GroupManager;
import org.apache.fulcrum.security.entity.Group;
import org.apache.fulcrum.security.entity.Role;
import org.apache.fulcrum.security.util.DataBackendException;
import org.apache.fulcrum.security.util.UnknownEntityException;

/**
 * @author Eric Pugh
 *
 */
public interface MemoryGroupManager extends GroupManager
{
	/**
	 * Puts a role into a group
	 *
	 * This method is used when adding a role to a group.
	 *
	 * @param group the group to use
	 * @param role the role that will join the group
	 * @throws DataBackendException if there was an error accessing the data
	 *         backend.
	 * @throws UnknownEntityException if the group or role is not present.
	 */
	void grant(Group group, Role role) throws DataBackendException, UnknownEntityException;
	
	/**
	 * Remove a role from a group
	 *
	 * This method is used when removeing a role to a group.
	 *
	 * @param group the group to use
	 * @param role the role that will join the group
	 * @throws DataBackendException if there was an error accessing the data
	 *         backend.
	 * @throws UnknownEntityException if the group or role is not present.
	 */
	void revoke(Group group, Role role) throws DataBackendException, UnknownEntityException;
	
		
}
