/*
 * Created on Aug 22, 2003
 *
 */
package org.apache.fulcrum.security.adapter.turbine;

import org.apache.turbine.om.security.Group;
import org.apache.turbine.om.security.Role;
import org.apache.turbine.om.security.User;
import org.apache.turbine.util.security.RoleSet;
import org.apache.turbine.util.security.TurbineSecurityException;

/**
 * @author Eric Pugh
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class GroupAdapter implements Group
{
	private org.apache.fulcrum.security.entity.Group group;
    /**
     * 
     */
    public GroupAdapter()
    {
		super();
    	
        
        // TODO Auto-generated constructor stub
    }
    
    public GroupAdapter(org.apache.fulcrum.security.entity.Group group){
    	this.group=group;
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.Group#save()
     */
    public void save() throws TurbineSecurityException
    {
        // TODO Auto-generated method stub
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.Group#remove()
     */
    public void remove() throws TurbineSecurityException
    {
        // TODO Auto-generated method stub
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.Group#rename(java.lang.String)
     */
    public void rename(String arg0) throws TurbineSecurityException
    {
        // TODO Auto-generated method stub
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.Group#grant(org.apache.turbine.om.security.User, org.apache.turbine.om.security.Role)
     */
    public void grant(User arg0, Role arg1) throws TurbineSecurityException
    {
        // TODO Auto-generated method stub
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.Group#grant(org.apache.turbine.om.security.User, org.apache.turbine.util.security.RoleSet)
     */
    public void grant(User arg0, RoleSet arg1) throws TurbineSecurityException
    {
        // TODO Auto-generated method stub
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.Group#revoke(org.apache.turbine.om.security.User, org.apache.turbine.om.security.Role)
     */
    public void revoke(User arg0, Role arg1) throws TurbineSecurityException
    {
        // TODO Auto-generated method stub
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.Group#revoke(org.apache.turbine.om.security.User, org.apache.turbine.util.security.RoleSet)
     */
    public void revoke(User arg0, RoleSet arg1) throws TurbineSecurityException
    {
        // TODO Auto-generated method stub
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.SecurityEntity#getName()
     */
    public String getName()
    {
       return group.getName();
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.SecurityEntity#setName(java.lang.String)
     */
    public void setName(String arg0)
    {
        group.setName(arg0);
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.SecurityEntity#getId()
     */
    public int getId()
    {
        return group.getId();
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.SecurityEntity#getIdAsObj()
     */
    public Integer getIdAsObj()
    {
        return new Integer(group.getId());
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.SecurityEntity#setId(int)
     */
    public void setId(int arg0)
    {
        group.setId(arg0);
    }
}
