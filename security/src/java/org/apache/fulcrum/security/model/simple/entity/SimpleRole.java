package org.apache.fulcrum.security.model.simple.entity;
import org.apache.fulcrum.security.entity.Permission;
import org.apache.fulcrum.security.entity.Role;
import org.apache.fulcrum.security.entity.Group;
import org.apache.fulcrum.security.entity.impl.SecurityEntityImpl;
import org.apache.fulcrum.security.util.GroupSet;
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
	
	private GroupSet groupSet = new GroupSet();
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
    
	/**
		* @return
		*/
	   public GroupSet getGroups()
	   {
		   return groupSet;
	   }
	   /**
		* @param groupSet
		*/
	   public void setGroups(GroupSet groupSet)
	   {
		   this.groupSet = groupSet;
	   }
    
	   /**
	   * This method should only be used by a RoleManager.  Not directly.
	   * @param group
	   */
	   public void addGroup(Group group)
	   {
		   getGroups().add(group);
	   }
	   /**
		* This method should only be used by a RoleManager.  Not directly.
		* @param group
		*/
	   public void removeGroup(Group group)
	   {
		   getGroups().remove(group);
	   }    
}
