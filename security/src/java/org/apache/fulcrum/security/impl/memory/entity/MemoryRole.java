/*
 * Created on Aug 21, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.apache.fulcrum.security.impl.memory.entity;
import org.apache.fulcrum.security.entity.Permission;
import org.apache.fulcrum.security.entity.impl.RoleImpl;
/**
 * @author Eric Pugh
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class MemoryRole extends RoleImpl
{
    public void addPermission(Permission permission)
    {
        getPermissions().add(permission);
    }
    public void removePermission(Permission permission)
    {
        getPermissions().remove(permission);
    }
    
}
