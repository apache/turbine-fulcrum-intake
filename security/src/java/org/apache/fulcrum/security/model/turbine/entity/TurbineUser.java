
package org.apache.fulcrum.security.model.turbine.entity;
import org.apache.fulcrum.security.entity.Group;
import org.apache.fulcrum.security.entity.Role;
import org.apache.fulcrum.security.entity.User;
import org.apache.fulcrum.security.entity.impl.SecurityEntityImpl;

import org.apache.fulcrum.security.util.GroupSet;
import org.apache.fulcrum.security.util.RoleSet;
/**
 * @author Eric Pugh
 *
 * Represents the "turbine" model where there is a many to many to many 
 * relationship between users, groups, and roles.  
 */
public class TurbineUser extends SecurityEntityImpl implements User
{
    private GroupSet groups = new GroupSet();
    private RoleSet roles = new RoleSet();
    private String password;
    /**
     * @return
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * @param password
     */
    public void setPassword(String password)
    {
        this.password = password;
    }

    /**
     * @return
     */
    RoleSet getRoles()
    {
        return roles;
    }
    /**
     * @param roles
     */
    void setRoles(RoleSet roles)
    {
        this.roles = roles;
    }
    public void addRole(Role role)
    {
        getRoles().add(role);
    }
    public void removeRole(Role role)
    {
        getRoles().remove(role);
    }
    /**
    * @return
    */
    public GroupSet getGroups()
    {
        return groups;
    }
    /**
     * @param groups
     */
    public void setGroups(GroupSet groups)
    {
        this.groups = groups;
    }
    public void removeGroup(Group group)
    {
        getGroups().remove(group);
    }
    public void addGroup(Group group)
    {
        getGroups().add(group);
    }
}
