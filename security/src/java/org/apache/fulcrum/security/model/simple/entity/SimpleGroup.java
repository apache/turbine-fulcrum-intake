
package org.apache.fulcrum.security.model.simple.entity;
import org.apache.fulcrum.security.entity.Group;
import org.apache.fulcrum.security.entity.Role;
import org.apache.fulcrum.security.entity.impl.SecurityEntityImpl;
import org.apache.fulcrum.security.util.RoleSet;
/**
 * @author Eric Pugh
 *
 * Represents the "simple" model where permissions are related to roles,
 * roles are related to groups and groups are related to users,
 * all in many to many relationships.  
 */
public class SimpleGroup extends SecurityEntityImpl implements Group
{
    private RoleSet roleSet = new RoleSet();
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
}
