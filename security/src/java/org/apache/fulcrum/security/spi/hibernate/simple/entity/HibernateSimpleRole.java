/*
 * Created on Aug 24, 2003
 *
 */
package org.apache.fulcrum.security.spi.hibernate.simple.entity;

import java.util.Set;

import org.apache.fulcrum.security.model.simple.entity.SimpleRole;

/**
 * @author Eric Pugh
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class HibernateSimpleRole extends SimpleRole
{
	
	void setHibernatePermissions(Set permissions){
		this.getPermissions().add(permissions);
	}
	
	Set getHibernatePermissions(){
		return getPermissions().getSet();	
	}
	
	void setHibernateGroups(Set groups){
			this.getGroups().add(groups);
		}
	
		Set getHibernateGroups(){
			return getGroups().getSet();	
		}
	
	
}
