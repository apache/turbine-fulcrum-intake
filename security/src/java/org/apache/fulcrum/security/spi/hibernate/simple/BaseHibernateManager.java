package org.apache.fulcrum.security.spi.hibernate.simple;
/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Turbine" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Turbine", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.Transaction;
import net.sf.hibernate.avalon.HibernateService;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.security.GroupManager;
import org.apache.fulcrum.security.PermissionManager;
import org.apache.fulcrum.security.RoleManager;
import org.apache.fulcrum.security.entity.SecurityEntity;
import org.apache.fulcrum.security.util.DataBackendException;
import org.apache.fulcrum.security.util.UnknownEntityException;
/**
 *
 * This implementation persists to a database via Hibernate.
 *
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class BaseHibernateManager extends AbstractLogEnabled implements Composable, Disposable
{
    boolean composed = false;
    /** Logging */
    private static Log log = LogFactory.getLog(BaseHibernateManager.class);
    protected HibernateService hibernateService;
    protected Session session;
    protected Transaction transaction;
    protected ComponentManager manager = null;
    protected PermissionManager permissionManager;
    protected RoleManager roleManager;
    protected GroupManager groupManager;
    /**
    	 * @return
    	 */
    ComponentManager getComponentManager()
    {
        return manager;
    }
    /**
     * @return
     */
    PermissionManager getPermissionManager() throws DataBackendException
    {
        if (permissionManager == null)
        {
            try
            {
                permissionManager = (PermissionManager) manager.lookup(PermissionManager.ROLE);
            }
            catch (ComponentException ce)
            {
                throw new DataBackendException(ce.getMessage(), ce);
            }
        }
        return permissionManager;
    }
    /**
     * @return
     */
    RoleManager getRoleManager() throws DataBackendException
    {
        if (roleManager == null)
        {
            try
            {
                roleManager = (RoleManager) manager.lookup(RoleManager.ROLE);
            }
            catch (ComponentException ce)
            {
                throw new DataBackendException(ce.getMessage(), ce);
            }
        }
        return roleManager;
    }
    /**
    * @return
    */
    GroupManager getGroupManager() throws DataBackendException
    {
        if (groupManager == null)
        {
            try
            {
                groupManager = (GroupManager) manager.lookup(GroupManager.ROLE);
            }
            catch (ComponentException ce)
            {
                throw new DataBackendException(ce.getMessage(), ce);
            }
        }
        return groupManager;
    }
    void removeEntity(SecurityEntity entity) throws DataBackendException, UnknownEntityException
    {
        try
        {
            session = hibernateService.openSession();
            transaction = session.beginTransaction();
            session.delete(entity);
            transaction.commit();
			session.close();
        }
        catch (HibernateException he)
        {
            try
            {
                transaction.rollback();
            }
            catch (HibernateException hex)
            {
            }
            throw new DataBackendException("Problem removing entity:" + he.getMessage(), he);
        }
    }
    /**
      * Stores changes made to an object
      *
      * @param role The object to be saved
      * @throws DataBackendException if there was an error accessing the data
      *         backend.
      * @throws UnknownEntityException if the role does not exist.
      */
    void updateEntity(SecurityEntity entity) throws DataBackendException
    {
        try
        {
            session = hibernateService.openSession();
            transaction = session.beginTransaction();
            session.update(entity);
            transaction.commit();
            session.close();
        }
        catch (HibernateException he)
        {
            try
            {
                transaction.rollback();
            }
            catch (HibernateException hex)
            {
            }
            throw new DataBackendException("updateEntity(" + entity+")", he);
        }
        return;
    }
    /**
    	  * adds an entity
    	  *
    	  * @param role The object to be saved
    	  * @throws DataBackendException if there was an error accessing the data
    	  *         backend.
    	  * @throws UnknownEntityException if the role does not exist.
    	  */
    void addEntity(SecurityEntity entity) throws DataBackendException
    {
        try
        {
            session = hibernateService.openSession();
            transaction = session.beginTransaction();
            session.save(entity);
            transaction.commit();
			session.close();
        }
        catch (HibernateException he)
        {
            try
            {
                transaction.rollback();
            }
            catch (HibernateException hex)
            {
            }
            throw new DataBackendException("addEntity(s,name)", he);
        }
        return;
    }
    /**
    	  * Deletes an entity object
    	  *
    	  * @param role The object to be saved
    	  * @throws DataBackendException if there was an error accessing the data
    	  *         backend.
    	  * @throws UnknownEntityException if the role does not exist.
    	  */
    void deleteEntity(SecurityEntity entity) throws DataBackendException
    {
        try
        {
            session = hibernateService.openSession();
            transaction = session.beginTransaction();
            session.delete(entity);
            transaction.commit();
			session.close();
        }
        catch (HibernateException he)
        {
            try
            {
                transaction.rollback();
            }
            catch (HibernateException hex)
            {
            }
            throw new DataBackendException("delete()", he);
        }
        return;
    }
    /**
    * Avalon component lifecycle method
    */
    public void compose(ComponentManager manager) throws ComponentException
    {
        this.manager = manager;
        hibernateService = (HibernateService) manager.lookup(HibernateService.ROLE);
    }
    public void dispose()
    {
        hibernateService = null;
        manager = null;
        permissionManager = null;
        roleManager = null;
        groupManager = null;
    }
}
