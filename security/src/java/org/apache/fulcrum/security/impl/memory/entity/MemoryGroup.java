/*
 * Created on Aug 21, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.apache.fulcrum.security.impl.memory.entity;
import org.apache.fulcrum.security.entity.Role;
import org.apache.fulcrum.security.entity.impl.GroupImpl;
import org.apache.fulcrum.security.util.RoleSet;
/**
 * @author Eric Pugh
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class MemoryGroup extends GroupImpl
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
    public void addRole(Role permission)
    {
        getRoles().add(permission);
    }
    public void removeRole(Role permission)
    {
        getRoles().remove(permission);
    }
}
