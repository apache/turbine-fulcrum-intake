
package org.apache.fulcrum.security.model.turbine.entity;
import java.util.HashSet;
import java.util.Set;

import org.apache.fulcrum.security.entity.Group;
import org.apache.fulcrum.security.entity.Role;
import org.apache.fulcrum.security.entity.User;
import org.apache.fulcrum.security.entity.impl.SecurityEntityImpl;
import org.apache.fulcrum.security.util.RoleSet;
/**
 * @author Eric Pugh
 *
*  Represents the "turbine" model where there is a many to many to many 
 * relationship between users, groups, and roles.  
 */
public class TurbineGroup extends SecurityEntityImpl implements Group
{
	private RoleSet roleSet = new RoleSet();
	private Set users = new HashSet();
    /**
     * @return
     */
    public Set getUsers()
    {
        return users;
    }

    /**
     * @param users
     */
    public void setUsers(Set users)
    {
        this.users = users;
    }

    /**
     * @return
     */
    public RoleSet getRoles()
    {
        return roleSet;
    }
    /**
     * @param roleSet
     */
    public void setRoles(RoleSet roleSet)
    {
        this.roleSet = roleSet;
    }
    public void addRole(Role role)
    {
        getRoles().add(role);
    }
    public void removeRole(Role role)
    {
        getRoles().remove(role);
    }
    
	public void addUser(User user)
	{
		getUsers().add(user);
	}
	public void removeUser(User user)
	{
		getUsers().remove(user);
	}
}
