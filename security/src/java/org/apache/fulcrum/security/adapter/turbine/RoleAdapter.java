/*
 * Created on Aug 23, 2003
 *
 */
package org.apache.fulcrum.security.adapter.turbine;

import org.apache.turbine.om.security.Permission;
import org.apache.turbine.om.security.Role;
import org.apache.turbine.util.security.PermissionSet;
import org.apache.turbine.util.security.TurbineSecurityException;

/**
 * @author Eric Pugh
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class RoleAdapter implements Role
{
	
	private org.apache.fulcrum.security.entity.Role role;
    /**
     * 
     */
    public RoleAdapter()
    {
        super();
        role = new org.apache.fulcrum.security.model.simple.entity.SimpleRole();
    }
	public RoleAdapter(org.apache.fulcrum.security.entity.Role role){
		   this.role=role;
	   }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.Role#getPermissions()
     */
    public PermissionSet getPermissions() throws Exception
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.Role#setPermissions(org.apache.turbine.util.security.PermissionSet)
     */
    public void setPermissions(PermissionSet arg0)
    {
        // TODO Auto-generated method stub
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.Role#create(java.lang.String)
     */
    public Role create(String arg0) throws TurbineSecurityException
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.Role#save()
     */
    public void save() throws TurbineSecurityException
    {
        // TODO Auto-generated method stub
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.Role#remove()
     */
    public void remove() throws TurbineSecurityException
    {
        // TODO Auto-generated method stub
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.Role#rename(java.lang.String)
     */
    public void rename(String arg0) throws TurbineSecurityException
    {
        // TODO Auto-generated method stub
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.Role#grant(org.apache.turbine.om.security.Permission)
     */
    public void grant(Permission arg0) throws TurbineSecurityException
    {
        // TODO Auto-generated method stub
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.Role#grant(org.apache.turbine.util.security.PermissionSet)
     */
    public void grant(PermissionSet arg0) throws TurbineSecurityException
    {
        // TODO Auto-generated method stub
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.Role#revoke(org.apache.turbine.om.security.Permission)
     */
    public void revoke(Permission arg0) throws TurbineSecurityException
    {
        // TODO Auto-generated method stub
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.Role#revoke(org.apache.turbine.util.security.PermissionSet)
     */
    public void revoke(PermissionSet arg0) throws TurbineSecurityException
    {
        // TODO Auto-generated method stub
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.SecurityEntity#getName()
     */
    public String getName()
    {
        return role.getName();
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.SecurityEntity#setName(java.lang.String)
     */
    public void setName(String arg0)
    {
        role.setName(arg0);
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.SecurityEntity#getId()
     */
    public int getId()
    {
        return new Integer(role.getId()+"").intValue();
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.SecurityEntity#getIdAsObj()
     */
    public Integer getIdAsObj()
    {
        return new Integer(role.getId()+"");
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.SecurityEntity#setId(int)
     */
    public void setId(int arg0)
    {
    role.setId(arg0);
    }
}
