/*
 * Created on Aug 23, 2003
 *
 */
package org.apache.fulcrum.security.adapter.turbine;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.turbine.om.security.Group;
import org.apache.turbine.om.security.Permission;
import org.apache.turbine.om.security.Role;
import org.apache.turbine.util.security.AccessControlList;
import org.apache.turbine.util.security.GroupSet;
import org.apache.turbine.util.security.PermissionSet;
import org.apache.turbine.util.security.RoleSet;
/**
 * @author Eric Pugh
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class AccessControlListAdapter implements AccessControlList
{
    private static Log log = LogFactory.getLog(AccessControlListAdapter.class);
    private org.apache.fulcrum.security.acl.AccessControlList acl;
    /**
     * 
     */
    public AccessControlListAdapter()
    {
        super();
    }
    public AccessControlListAdapter(org.apache.fulcrum.security.acl.AccessControlList acl)
    {
        super();
        this.acl = acl;
    }
    public AccessControlListAdapter(Map rolesMap, Map permissionsMap)
    {
        log.info("AccessControlListAdapter is eating call to constructor(Map,Map).");
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.util.security.AccessControlList#getRoles(org.apache.turbine.om.security.Group)
     */
    public RoleSet getRoles(Group arg0)
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.util.security.AccessControlList#getRoles()
     */
    public RoleSet getRoles()
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.util.security.AccessControlList#getPermissions(org.apache.turbine.om.security.Group)
     */
    public PermissionSet getPermissions(Group arg0)
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.util.security.AccessControlList#getPermissions()
     */
    public PermissionSet getPermissions()
    {
        PermissionSet turbinePS = new PermissionSet();
        org.apache.fulcrum.security.util.PermissionSet fulcrumPS = acl.getPermissions();
        for (Iterator i = fulcrumPS.iterator(); i.hasNext();)
        {
            org.apache.fulcrum.security.entity.Permission fulcrumPermission =
                (org.apache.fulcrum.security.entity.Permission) i.next();
            Permission turbinePermission = new PermissionAdapter(fulcrumPermission);
            turbinePS.add(turbinePermission);
        }
        return turbinePS;
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.util.security.AccessControlList#hasRole(org.apache.turbine.om.security.Role, org.apache.turbine.om.security.Group)
     */
    public boolean hasRole(Role arg0, Group arg1)
    {
        // TODO Auto-generated method stub
        return false;
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.util.security.AccessControlList#hasRole(org.apache.turbine.om.security.Role, org.apache.turbine.util.security.GroupSet)
     */
    public boolean hasRole(Role arg0, GroupSet arg1)
    {
        // TODO Auto-generated method stub
        return false;
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.util.security.AccessControlList#hasRole(java.lang.String, java.lang.String)
     */
    public boolean hasRole(String arg0, String arg1)
    {
        // TODO Auto-generated method stub
        return false;
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.util.security.AccessControlList#hasRole(java.lang.String, org.apache.turbine.util.security.GroupSet)
     */
    public boolean hasRole(String arg0, GroupSet arg1)
    {
        // TODO Auto-generated method stub
        return false;
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.util.security.AccessControlList#hasRole(org.apache.turbine.om.security.Role)
     */
    public boolean hasRole(Role arg0)
    {
        // TODO Auto-generated method stub
        return false;
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.util.security.AccessControlList#hasRole(java.lang.String)
     */
    public boolean hasRole(String arg0)
    {
        // TODO Auto-generated method stub
        return false;
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.util.security.AccessControlList#hasPermission(org.apache.turbine.om.security.Permission, org.apache.turbine.om.security.Group)
     */
    public boolean hasPermission(Permission arg0, Group arg1)
    {
        // TODO Auto-generated method stub
        return false;
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.util.security.AccessControlList#hasPermission(org.apache.turbine.om.security.Permission, org.apache.turbine.util.security.GroupSet)
     */
    public boolean hasPermission(Permission arg0, GroupSet arg1)
    {
        // TODO Auto-generated method stub
        return false;
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.util.security.AccessControlList#hasPermission(java.lang.String, java.lang.String)
     */
    public boolean hasPermission(String arg0, String arg1)
    {
        // TODO Auto-generated method stub
        return false;
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.util.security.AccessControlList#hasPermission(java.lang.String, org.apache.turbine.om.security.Group)
     */
    public boolean hasPermission(String arg0, Group arg1)
    {
        // TODO Auto-generated method stub
        return false;
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.util.security.AccessControlList#hasPermission(java.lang.String, org.apache.turbine.util.security.GroupSet)
     */
    public boolean hasPermission(String arg0, GroupSet arg1)
    {
        // TODO Auto-generated method stub
        return false;
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.util.security.AccessControlList#hasPermission(org.apache.turbine.om.security.Permission)
     */
    public boolean hasPermission(Permission arg0)
    {
        // TODO Auto-generated method stub
        return false;
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.util.security.AccessControlList#hasPermission(java.lang.String)
     */
    public boolean hasPermission(String arg0)
    {
        // TODO Auto-generated method stub
        return false;
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.util.security.AccessControlList#getAllGroups()
     */
    public Group[] getAllGroups()
    {
        // TODO Auto-generated method stub
        return null;
    }
}
