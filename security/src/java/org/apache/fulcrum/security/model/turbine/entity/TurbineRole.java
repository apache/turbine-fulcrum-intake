
package org.apache.fulcrum.security.model.turbine.entity;
import org.apache.fulcrum.security.entity.Group;
import org.apache.fulcrum.security.entity.Permission;
import org.apache.fulcrum.security.entity.Role;
import org.apache.fulcrum.security.entity.impl.SecurityEntityImpl;
import org.apache.fulcrum.security.util.GroupSet;
import org.apache.fulcrum.security.util.PermissionSet;
/**
 * @author Eric Pugh
 *
* Represents the "turbine" model where there is a many to many to many 
 * relationship between users, groups, and roles.  
 */
public class TurbineRole extends SecurityEntityImpl implements Role
{
    private GroupSet groupSet = new GroupSet();
    private PermissionSet permissionSet = new PermissionSet();
    /**
     * @return
     */
    public PermissionSet getPermissions()
    {
        return permissionSet;
    }

    /**
     * @param permissionSet
     */
    public void setPermissions(PermissionSet permissionSet)
    {
        this.permissionSet = permissionSet;
    }

    /**
     * @return
     */
    GroupSet getGroups()
    {
        return groupSet;
    }
    /**
     * @param groupSet
     */
    void setGroups(GroupSet groupSet)
    {
        this.groupSet = groupSet;
    }
    public void addPermission(Permission permission)
    {
        getPermissions().add(permission);
    }
    public void removePermission(Permission permission)
    {
        getPermissions().remove(permission);
    }
    public void addGroup(Group group)
    {
        getGroups().add(group);
    }
    public void removeGroup(Group group)
    {
        getGroups().remove(group);
    }
}
