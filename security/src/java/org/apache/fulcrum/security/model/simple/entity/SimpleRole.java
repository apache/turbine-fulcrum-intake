package org.apache.fulcrum.security.model.simple.entity;
import org.apache.fulcrum.security.entity.Permission;
import org.apache.fulcrum.security.entity.Role;
import org.apache.fulcrum.security.entity.impl.SecurityEntityImpl;
import org.apache.fulcrum.security.util.PermissionSet;
/**
 * @author Eric Pugh
 *
 * Represents the "simple" model where permissions are related to roles,
 * roles are related to groups and groups are related to users,
 * all in many to many relationships.  
 */
public class SimpleRole extends SecurityEntityImpl implements Role
{
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
    * This method should only be used by a RoleManager.  Not directly.
    * @param permission
    */
    public void addPermission(Permission permission)
    {
        getPermissions().add(permission);
    }
    /**
     * This method should only be used by a RoleManager.  Not directly.
     * @param permission
     */
    public void removePermission(Permission permission)
    {
        getPermissions().remove(permission);
    }
}
