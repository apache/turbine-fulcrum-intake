package org.apache.fulcrum.security.model.turbine.entity;
import org.apache.fulcrum.security.entity.Permission;
import org.apache.fulcrum.security.entity.impl.SecurityEntityImpl;
/**
 * @author Eric Pugh
 *
 * Represents the "turbine" model where there is a many to many to many 
 * relationship between users, groups, and roles.  
 */
public class TurbinePermission extends SecurityEntityImpl implements Permission
{
}
