
package org.apache.fulcrum.security.model.simple.entity;
import org.apache.fulcrum.security.entity.Permission;
import org.apache.fulcrum.security.entity.impl.SecurityEntityImpl;
/**
 * @author Eric Pugh
 *
 * Represents the "simple" model where permissions are related to roles,
 * roles are related to groups and groups are related to users,
 * all in many to many relationships.  
 */
public class SimplePermission extends SecurityEntityImpl implements Permission
{
	
    
}
