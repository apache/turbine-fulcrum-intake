/*
 * Created on Dec 2, 2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.fulcrum.security.hibernate;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.avalon.HibernateService;

import org.apache.fulcrum.security.entity.SecurityEntity;
import org.apache.fulcrum.security.util.DataBackendException;

/**
 * @author Eric Pugh
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public interface PersistenceHelper
{
	public static final String ROLE = PersistenceHelper.class.getName();
	public void removeEntity(SecurityEntity entity)throws DataBackendException;
	
	public void updateEntity(SecurityEntity entity) throws DataBackendException;
	
	public void addEntity(SecurityEntity entity) throws DataBackendException;
	
	public Session retrieveSession() throws HibernateException;
	public void setHibernateService(HibernateService hibernateService);
	public HibernateService getHibernateService() throws HibernateException;
	

}
