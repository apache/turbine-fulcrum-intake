package org.apache.fulcrum.security.hibernate;
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
import java.util.List;

import net.sf.hibernate.Hibernate;
import net.sf.hibernate.HibernateException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.security.entity.Role;
import org.apache.fulcrum.security.spi.AbstractRoleManager;
import org.apache.fulcrum.security.util.DataBackendException;
import org.apache.fulcrum.security.util.RoleSet;
import org.apache.fulcrum.security.util.UnknownEntityException;
/**
 *
 * This implementation persists to a database via Hibernate.
 *
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class HibernateRoleManagerImpl extends AbstractRoleManager
{
    /** Logging */
    private static Log log = LogFactory.getLog(HibernateRoleManagerImpl.class);
    
	private PersistenceHelper persistenceHelper;
   
   
   
    /**
    * Renames an existing Role.
    *
    * @param role The object describing the role to be renamed.
    * @param name the new name for the role.
    * @throws DataBackendException if there was an error accessing the data
    *         backend.
    * @throws UnknownEntityException if the role does not exist.
    */
    public synchronized void renameRole(Role role, String name) throws DataBackendException, UnknownEntityException
    {
        boolean roleExists = false;
        roleExists = checkExists(role);
        if (roleExists)
        {
            role.setName(name);
            getPersistenceHelper().updateEntity(role);
            return;
        }
        else
        {
            throw new UnknownEntityException("Unknown role '" + role + "'");
        }
    }
    /**
      * Determines if the <code>Role</code> exists in the security system.
      *
      * @param roleName a <code>Role</code> value
      * @return true if the role name exists in the system, false otherwise
      * @throws DataBackendException when more than one Role with
      *         the same name exists.
      */
    public boolean checkExists(String roleName) throws DataBackendException
    {
        List roles;
        try
        {
            
            roles = getPersistenceHelper().retrieveSession().find("from " + Role.class.getName() + " sr where sr.name=?", roleName, Hibernate.STRING);

        }
        catch (HibernateException e)
        {
            throw new DataBackendException("Error retriving role information", e);
        }
        if (roles.size() > 1)
        {
            throw new DataBackendException("Multiple roles with same name '" + roleName + "'");
        }
        return (roles.size() == 1);
    }
    /**
     * Retrieves all roles defined in the system.
     *
     * @return the names of all roles defined in the system.
     * @throws DataBackendException if there was an error accessing the
     *         data backend.
     */
    public RoleSet getAllRoles() throws DataBackendException
    {
        RoleSet roleSet = new RoleSet();
        try
        {
            List roles = getPersistenceHelper().retrieveSession().find("from " + Role.class.getName() + "");
            roleSet.add(roles);
        }
        catch (HibernateException e)
        {
            throw new DataBackendException("Error retriving role information", e);
        }
        return roleSet;
    }
   
   
    /**
    * Creates a new role with specified attributes.
    *
    * @param role the object describing the role to be created.
    * @return a new Role object that has id set up properly.
    * @throws DataBackendException if there was an error accessing the data
    *         backend.
    * @throws EntityExistsException if the role already exists.
    */
    protected synchronized Role persistNewRole(Role role) throws DataBackendException
    {
       
		getPersistenceHelper().addEntity(role);
        return role;
    }
    /**
    * Removes a Role from the system.
    *
    * @param role The object describing the role to be removed.
    * @throws DataBackendException if there was an error accessing the data
    *         backend.
    * @throws UnknownEntityException if the role does not exist.
    */
    public synchronized void removeRole(Role role) throws DataBackendException, UnknownEntityException
    {
        boolean roleExists = false;
        try
        {
            roleExists = checkExists(role);
            if (roleExists)
            {
				getPersistenceHelper().removeEntity(role);
            }
            else
            {
                throw new UnknownEntityException("Unknown role '" + role + "'");
            }
        }
        catch (Exception e)
        {
            log.error("Failed to delete a Role");
            log.error(e);
            throw new DataBackendException("removeRole(Role) failed", e);
        }
    }
    
	/**
	 * @return Returns the persistenceHelper.
	 */
	public PersistenceHelper getPersistenceHelper() throws DataBackendException
	{
		if (persistenceHelper == null)
		{
			persistenceHelper = (PersistenceHelper)resolve(PersistenceHelper.ROLE);
		}
		return persistenceHelper;
	}
	
	/**
	 * Retrieve a Role object with specified id.
	 * 
	 * @param id
	 *            the id of the Role.
	 * @return an object representing the Role with specified id.
	 * @throws DataBackendException
	 *             if there was an error accessing the data backend.
	 * @throws UnknownEntityException
	 *             if the role does not exist.
	 */
	public Role getRoleById(Object id)
	throws DataBackendException, UnknownEntityException {
		
		Role role = null;

		if (id != null)
			try {
				List roles =
					getPersistenceHelper().retrieveSession().find(
							"from " + Role.class.getName() + " sr where sr.id=?",
							id,
							Hibernate.LONG);
				if (roles.size() == 0) {
					throw new UnknownEntityException(
							"Could not find role by id " + id);
				}
				role = (Role) roles.get(0);
				
			} catch (HibernateException e) {
				throw new DataBackendException(
						"Error retriving role information",
						e);
			}
			
		return role;
	}
}
