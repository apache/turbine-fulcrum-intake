/*
 * Created on Aug 23, 2003
 *
 */
package org.apache.fulcrum.security.adapter.turbine;
import org.apache.fulcrum.security.entity.SecurityEntity;
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
public class RoleAdapter extends BaseAdapter implements Role
{
   
   public RoleAdapter(org.apache.fulcrum.security.entity.Role role){
   	super((SecurityEntity)role);
   }
    
    public void setPermissions(PermissionSet arg0)
    {
		throw new RuntimeException("Unsupported operation");
    }
    public PermissionSet getPermissions()
    {
		throw new RuntimeException("Unsupported operation");
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.Role#create(java.lang.String)
     */
    public Role create(String arg0) throws TurbineSecurityException
    {
		throw new RuntimeException("Unsupported operation");
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.Role#grant(org.apache.turbine.om.security.Permission)
     */
    public void grant(Permission arg0) throws TurbineSecurityException
    {
		throw new RuntimeException("Unsupported operation");
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.Role#grant(org.apache.turbine.util.security.PermissionSet)
     */
    public void grant(PermissionSet arg0) throws TurbineSecurityException
    {
		throw new RuntimeException("Unsupported operation");
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.Role#revoke(org.apache.turbine.om.security.Permission)
     */
    public void revoke(Permission arg0) throws TurbineSecurityException
    {
		throw new RuntimeException("Unsupported operation");
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.Role#revoke(org.apache.turbine.util.security.PermissionSet)
     */
    public void revoke(PermissionSet arg0) throws TurbineSecurityException
    {
		throw new RuntimeException("Unsupported operation");
    }
}
