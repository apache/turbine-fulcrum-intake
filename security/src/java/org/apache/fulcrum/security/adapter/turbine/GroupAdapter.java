/*
 * Created on Aug 22, 2003
 *
 */
package org.apache.fulcrum.security.adapter.turbine;
import org.apache.fulcrum.security.entity.SecurityEntity;
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
public class GroupAdapter extends BaseAdapter implements Group
{
	
	public GroupAdapter(org.apache.fulcrum.security.entity.Group group){
		super((SecurityEntity)group);
	   }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.Group#grant(org.apache.turbine.om.security.User, org.apache.turbine.om.security.Role)
     */
    public void grant(User arg0, Role arg1) throws TurbineSecurityException
    {
        throw new RuntimeException("Unsupported operation");
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.Group#grant(org.apache.turbine.om.security.User, org.apache.turbine.util.security.RoleSet)
     */
    public void grant(User arg0, RoleSet arg1) throws TurbineSecurityException
    {
        throw new RuntimeException("Unsupported operation");
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.Group#revoke(org.apache.turbine.om.security.User, org.apache.turbine.om.security.Role)
     */
    public void revoke(User arg0, Role arg1) throws TurbineSecurityException
    {
        throw new RuntimeException("Unsupported operation");
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.Group#revoke(org.apache.turbine.om.security.User, org.apache.turbine.util.security.RoleSet)
     */
    public void revoke(User arg0, RoleSet arg1) throws TurbineSecurityException
    {
        throw new RuntimeException("Unsupported operation");
    }
}
