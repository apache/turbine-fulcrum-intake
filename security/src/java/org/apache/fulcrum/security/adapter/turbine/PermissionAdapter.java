/*
 * Created on Aug 23, 2003
 *
 */
package org.apache.fulcrum.security.adapter.turbine;
import org.apache.fulcrum.security.entity.SecurityEntity;
import org.apache.turbine.om.security.Permission;
/**
 * @author Eric Pugh
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class PermissionAdapter extends BaseAdapter implements Permission
{
    public PermissionAdapter(org.apache.fulcrum.security.entity.Permission permission)
    {
        super((SecurityEntity)permission);
        
    }
}
