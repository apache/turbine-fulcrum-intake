package org.apache.fulcrum.security.spi.torque.turbine.impl;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.security.entity.Permission;
import org.apache.fulcrum.security.entity.Role;
import org.apache.fulcrum.security.model.turbine.entity.TurbineRole;
import org.apache.fulcrum.security.spi.torque.turbine.TorqueRoleManager;
import org.apache.fulcrum.security.spi.torque.turbine.peer.RolePermissionPeer;
import org.apache.fulcrum.security.spi.torque.turbine.peermanagers.PermissionPeerManager;
import org.apache.fulcrum.security.spi.torque.turbine.peermanagers.RolePeerManager;
import org.apache.fulcrum.security.util.DataBackendException;
import org.apache.fulcrum.security.util.EntityExistsException;
import org.apache.fulcrum.security.util.PermissionSet;
import org.apache.fulcrum.security.util.RoleSet;
import org.apache.fulcrum.security.util.UnknownEntityException;
import org.apache.torque.om.NumberKey;
import org.apache.torque.om.Persistent;
import org.apache.torque.util.Criteria;
/**
 * An UserManager performs {@link org.apache.turbine.om.security.User}
 * objects related tasks on behalf of the
 * {@link org.apache.turbine.services.security.BaseSecurityService}.
 *
 * This implementation uses a relational database for storing user data. It
 * expects that the User interface implementation will be castable to
 * {@link org.apache.torque.om.BaseObject}.
 *
 * @author <a href="mailto:jon@collab.net">Jon S. Stevens</a>
 * @author <a href="mailto:jmcnally@collab.net">John D. McNally</a>
 * @author <a href="mailto:frank.kim@clearink.com">Frank Y. Kim</a>
 * @author <a href="mailto:cberry@gluecode.com">Craig D. Berry</a>
 * @author <a href="mailto:Rafal.Krzewski@e-point.pl">Rafal Krzewski</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public class TorqueRoleManagerImpl extends TorqueManagerComponent implements TorqueRoleManager
{
    /** Logging */
    private static Log log = LogFactory.getLog(TorqueRoleManagerImpl.class);
    /** The class of Role the SecurityService uses */
    private Class roleClass = null;
    /**
    	* Return a Class object representing the system's chosen implementation of
    	* of Role interface.
    	*
    	* @return systems's chosen implementation of Role interface.
    	* @throws UnknownEntityException if the implementation of Role interface
    	*         could not be determined, or does not exist.
    	*/
    public Class getRoleClass() throws UnknownEntityException
    {
        if (roleClass == null)
        {
            throw new UnknownEntityException("Failed to create a Class object for Role implementation");
        }
        return roleClass;
    }
    /**
    	* Construct a blank Role object.
    	*
    	* This method calls getRoleClass, and then creates a new object using
    	* the default constructor.
    	*
    	* @return an object implementing Role interface.
    	* @throws DataBackendException if the object could not be instantiated.
    	*/
    public Role getRoleInstance() throws DataBackendException
    {
        Role role;
        try
        {
            role = (Role) getRoleClass().newInstance();
        }
        catch (Exception e)
        {
            throw new DataBackendException("Failed to instantiate a Role implementation object", e);
        }
        return role;
    }
    /**
    	* Construct a blank Role object.
    	*
    	* This method calls getRoleClass, and then creates a new object using
    	* the default constructor.
    	*
    	* @param roleName The name of the role.
    	*
    	* @return an object implementing Role interface.
    	*
    	* @throws DataBackendException if the object could not be instantiated.
    	*/
    public Role getRoleInstance(String roleName) throws DataBackendException
    {
        Role role = getRoleInstance();
        role.setName(roleName);
        return role;
    }
    /**
    	* Retrieve a Role object with specified name.
    	*
    	* @param name the name of the Role.
    	* @return an object representing the Role with specified name.
    	* @throws DataBackendException if there was an error accessing the
    	*         data backend.
    	* @throws UnknownEntityException if the role does not exist.
    	*/
    public Role getRoleByName(String name) throws DataBackendException, UnknownEntityException
    {
        Role role = getAllRoles().getRoleByName(name);
        if (role == null)
        {
            throw new UnknownEntityException("The specified role does not exist");
        }
        ((TurbineRole)role).setPermissions(getPermissions(role));
        return role;
    }
    /**
    	* Retrieve a Role object with specified Id.
    	*
    	* @param name the name of the Role.
    	*
    	* @return an object representing the Role with specified name.
    	*
    	* @throws UnknownEntityException if the permission does not
    	*            exist in the database.
    	* @throws DataBackendException if there is a problem accessing the
    	*            storage.
    	*/
    public Role getRoleById(Object id) throws DataBackendException, UnknownEntityException
    {
        Role role = getAllRoles().getRoleById(id);
        if (role == null)
        {
            throw new UnknownEntityException("The specified role does not exist");
        }
		((TurbineRole)role).setPermissions(getPermissions(role));
        return role;
    }
    /**
    	* Grants a Role a Permission
    	*
    	* @param role the Role.
    	* @param permission the Permission.
    	* @throws DataBackendException if there was an error accessing the data
    	*         backend.
    	* @throws UnknownEntityException if role or permission is not present.
    	*/
    public synchronized void grant(Role role, Permission permission)
        throws DataBackendException, UnknownEntityException
    {
        boolean roleExists = false;
        boolean permissionExists = false;
        try
        {
            lockExclusive();
            roleExists = checkExists(role);
            permissionExists = checkExists(permission);
            if (roleExists && permissionExists)
            {
                Criteria criteria = new Criteria();
                criteria.add(RolePermissionPeer.ROLE_ID, ((Persistent) role).getPrimaryKey());
                criteria.add(RolePermissionPeer.PERMISSION_ID, ((Persistent) permission).getPrimaryKey());
				RolePermissionPeer.doInsert(criteria);
                return;
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException("grant(Role,Permission) failed", e);
        }
        finally
        {
            unlockExclusive();
        }
        if (!roleExists)
        {
            throw new UnknownEntityException("Unknown role '" + role.getName() + "'");
        }
        if (!permissionExists)
        {
            throw new UnknownEntityException("Unknown permission '" + permission.getName() + "'");
        }
    }
    /**
    	* Revokes a Permission from a Role.
    	*
    	* @param role the Role.
    	* @param permission the Permission.
    	* @throws DataBackendException if there was an error accessing the data
    	*         backend.
    	* @throws UnknownEntityException if role or permission is not present.
    	*/
    public synchronized void revoke(Role role, Permission permission)
        throws DataBackendException, UnknownEntityException
    {
        boolean roleExists = false;
        boolean permissionExists = false;
        try
        {
            lockExclusive();
            roleExists = checkExists(role);
            permissionExists = checkExists(permission);
            if (roleExists && permissionExists)
            {
                Criteria criteria = new Criteria();
                criteria.add(RolePermissionPeer.ROLE_ID, ((Persistent) role).getPrimaryKey());
                criteria.add(RolePermissionPeer.PERMISSION_ID, ((Persistent) permission).getPrimaryKey());
				RolePermissionPeer.doDelete(criteria);
                return;
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException("revoke(Role,Permission) failed", e);
        }
        finally
        {
            unlockExclusive();
        }
        if (!roleExists)
        {
            throw new UnknownEntityException("Unknown role '" + role.getName() + "'");
        }
        if (!permissionExists)
        {
            throw new UnknownEntityException("Unknown permission '" + permission.getName() + "'");
        }
    }
    /**
    	* Revokes all permissions from a Role.
    	*
    	* This method is user when deleting a Role.
    	*
    	* @param role the Role
    	* @throws DataBackendException if there was an error accessing the data
    	*         backend.
    	* @throws UnknownEntityException if the Role is not present.
    	*/
    public synchronized void revokeAll(Role role) throws DataBackendException, UnknownEntityException
    {
        boolean roleExists = false;
        try
        {
            lockExclusive();
            roleExists = checkExists(role);
            if (roleExists)
            {
                // The following would not work, due to an annoying misfeature
                // of Village. see revokeAll( user )
                // Criteria criteria = new Criteria();
                // criteria.add(RolePermissionPeer.ROLE_ID,
                //         role.getPrimaryKey());
                // RolePermissionPeer.doDelete(criteria);
                int id = ((NumberKey) ((Persistent) role).getPrimaryKey()).intValue();
                RolePermissionPeer.deleteAll(
                    RolePermissionPeer.TABLE_NAME,
                    RolePermissionPeer.ROLE_ID,
                    id);
                return;
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException("revokeAll(Role) failed", e);
        }
        finally
        {
            unlockExclusive();
        }
        throw new UnknownEntityException("Unknown role '" + role.getName() + "'");
    }
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
        try
        {
            lockExclusive();
            roleExists = checkExists(role);
            if (roleExists)
            {
                role.setName(name);
                Criteria criteria = RolePeerManager.buildCriteria(role);
                RolePeerManager.doUpdate(criteria);
                return;
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException("renameRole(Role,String)", e);
        }
        finally
        {
            unlockExclusive();
        }
        throw new UnknownEntityException("Unknown role '" + role + "'");
    }
    /**
      * Determines if the <code>Role</code> exists in the security system.
      *
      * @param role a <code>Role</code> value
      * @return true if the role exists in the system, false otherwise
      * @throws DataBackendException when more than one Role with
      *         the same name exists.
      * @throws Exception A generic exception.
      */
    public boolean checkExists(Role role) throws DataBackendException
    {
    	try {
        return RolePeerManager.checkExists(role);
    	}
    	catch (Exception e){
    		throw new DataBackendException("problem checking exists", e);
    	}
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
        return getRoles(new Criteria());
    }
    
	/**
		 * Retrieves all permissions associated with a role.
		 *
		 * @param role the role name, for which the permissions are to be retrieved.
		 * @return A Permission set for the Role.
		 * @throws DataBackendException if there was an error accessing the data
		 *         backend.
		 * @throws UnknownEntityException if the role is not present.
		 */
		public PermissionSet getPermissions(Role role)
			throws DataBackendException, UnknownEntityException
		{
			boolean roleExists = false;
			try
			{
				lockShared();
				roleExists = checkExists(role);
				if (roleExists)
				{
					return PermissionPeerManager.retrieveSet(role);
				}
			}
			catch (Exception e)
			{
				throw new DataBackendException("getPermissions(Role) failed", e);
			}
			finally
			{
				unlockShared();
			}
			throw new UnknownEntityException("Unknown role '"
											 + role.getName() + "'");
		}
		
	/**
		   * Retrieve a set of Role that meet the specified Criteria.
		   *
		   * @param criteria A Criteria of Role selection.
		   * @return a set of Roles that meet the specified Criteria.
		   * @throws DataBackendException if there was an error accessing the data
		   *         backend.
		   */
		  public RoleSet getRoles(Criteria criteria)
			  throws DataBackendException
		  {
			  Criteria torqueCriteria = new Criteria();
			  Iterator keys = criteria.keySet().iterator();
			  while (keys.hasNext())
			  {
				  String key = (String) keys.next();
				  torqueCriteria.put(RolePeerManager.getColumnName(key),
						  criteria.get(key));
			  }
			  List roles = new ArrayList(0);
			  try
			  {
				roles = RolePeerManager.doSelect(criteria);
			  }
			  catch (Exception e)
			  {
				  throw new DataBackendException("getRoles(Criteria) failed", e);
			  }
			  return new RoleSet(roles);
		  }    
		  
	/**
		 * Determines if the <code>Permission</code> exists in the security system.
		 *
		 * @param permission a <code>Permission</code> value
		 * @return true if the permission exists in the system, false otherwise
		 * @throws DataBackendException when more than one Permission with
		 *         the same name exists.
		 * @throws Exception A generic exception.
		 */
		protected boolean checkExists(Permission permission)
			throws DataBackendException, Exception
		{
			return PermissionPeerManager.checkExists(permission);
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
	   public synchronized Role addRole(Role role)
		   throws DataBackendException, EntityExistsException
	   {
		   boolean roleExists = false;

		   if (StringUtils.isEmpty(role.getName()))
		   {
			   throw new DataBackendException("Could not create "
											  + "a role with empty name!");
		   }

		   try
		   {
			   lockExclusive();
			   roleExists = checkExists(role);
			   if (!roleExists)
			   {
				   // add a row to the table
				   Criteria criteria = RolePeerManager.buildCriteria(role);
				   RolePeerManager.doInsert(criteria);
				   // try to get the object back using the name as key.
				   criteria = new Criteria();
				   criteria.add(RolePeerManager.getNameColumn(), role.getName());
				   List results = RolePeerManager.doSelect(criteria);
				   if (results.size() != 1)
				   {
					   throw new DataBackendException(
						   "Internal error - query returned "
						   + results.size() + " rows");
				   }
				   Role newRole = (Role) results.get(0);
				   // add the role to system-wide cache
				   getAllRoles().add(newRole);
				   // return the object with correct id
				   return newRole;
			   }
		   }
		   catch (Exception e)
		   {
			   throw new DataBackendException("addRole(Role) failed", e);
		   }
		   finally
		   {
			   unlockExclusive();
		   }
		   // the only way we could get here without return/throw tirggered
		   // is that the roleExists was true.
		   throw new EntityExistsException("Role '" + role + "' already exists");
	   }
	   
	/**
	   * Stores Role's attributes. The Roles is required to exist in the system.
	   *
	   * @param role The Role to be stored.
	   * @throws DataBackendException if there was an error accessing the data
	   *         backend.
	   * @throws UnknownEntityException if the role does not exist.
	   */
	  public void saveRole(Role role)
		  throws DataBackendException, UnknownEntityException
	  {
		  boolean roleExists = false;
		  try
		  {
			  roleExists = checkExists(role);
			  if (roleExists)
			  {
				  Criteria criteria = RolePeerManager.buildCriteria(role);
				  RolePeerManager.doUpdate(criteria);
				  return;
			  }
		  }
		  catch (Exception e)
		  {
			  throw new DataBackendException("saveRole(Role) failed", e);
		  }
		  throw new UnknownEntityException("Unknown role '" + role + "'");
	  }	   
	  
	/**
		* Removes a Role from the system.
		*
		* @param role The object describing the role to be removed.
		* @throws DataBackendException if there was an error accessing the data
		*         backend.
		* @throws UnknownEntityException if the role does not exist.
		*/
	   public synchronized void removeRole(Role role)
		   throws DataBackendException, UnknownEntityException
	   {
		   boolean roleExists = false;
		   try
		   {
			   lockExclusive();
			   roleExists = checkExists(role);
			   if (roleExists)
			   {
				   // revoke all permissions from the role to be deleted
				   revokeAll(role);
				   Criteria criteria = RolePeerManager.buildCriteria(role);
				   RolePeerManager.doDelete(criteria);
				   getAllRoles().remove(role);
				   return;
			   }
		   }
		   catch (Exception e)
		   {
			   throw new DataBackendException("removeRole(Role)", e);
		   }
		   finally
		   {
			   unlockExclusive();
		   }
		   throw new UnknownEntityException("Unknown role '" + role + "'");
	   }	  
}
