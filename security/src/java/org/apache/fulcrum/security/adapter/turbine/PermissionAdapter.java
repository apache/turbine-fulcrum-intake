/*
 * Created on Aug 23, 2003
 *
 */
package org.apache.fulcrum.security.adapter.turbine;
import org.apache.turbine.om.security.Permission;
import org.apache.turbine.util.security.TurbineSecurityException;
/**
 * @author Eric Pugh
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class PermissionAdapter implements Permission
{
    private org.apache.fulcrum.security.entity.Permission permission;
    /**
     * 
     */
    public PermissionAdapter()
    {
        super();
        permission = new org.apache.fulcrum.security.entity.impl.PermissionImpl();
    }
    public PermissionAdapter(org.apache.fulcrum.security.entity.Permission permission)
    {
        super();
        this.permission = permission;
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.Permission#save()
     */
    public void save() throws TurbineSecurityException
    {
        // TODO Auto-generated method stub
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.Permission#remove()
     */
    public void remove() throws TurbineSecurityException
    {
        // TODO Auto-generated method stub
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.Permission#rename(java.lang.String)
     */
    public void rename(String arg0) throws TurbineSecurityException
    {
        // TODO Auto-generated method stub
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.SecurityEntity#getName()
     */
    public String getName()
    {
        return permission.getName();
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.SecurityEntity#setName(java.lang.String)
     */
    public void setName(String arg0)
    {
        permission.setName(arg0);
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.SecurityEntity#getId()
     */
    public int getId()
    {
        return permission.getId();
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.SecurityEntity#getIdAsObj()
     */
    public Integer getIdAsObj()
    {
        return new Integer(permission.getId());
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.SecurityEntity#setId(int)
     */
    public void setId(int arg0)
    {
        permission.setId(arg0);
    }
}
