package org.apache.fulcrum.security.spi.memory.turbine;
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

import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.security.PermissionManager;
import org.apache.fulcrum.security.RoleManager;
import org.apache.fulcrum.security.entity.Permission;
import org.apache.fulcrum.security.entity.Role;
import org.apache.fulcrum.security.model.simple.entity.SimpleRole;
import org.apache.fulcrum.security.util.DataBackendException;
import org.apache.fulcrum.security.util.EntityExistsException;
import org.apache.fulcrum.security.util.PermissionSet;
import org.apache.fulcrum.security.util.RoleSet;
import org.apache.fulcrum.security.util.UnknownEntityException;

/**
 *
 * This implementation keeps all objects in memory.  This is mostly meant to help
 * with testing and prototyping of ideas.
 *
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class MemoryRoleManagerImpl extends AbstractLogEnabled implements RoleManager, Composable
{
    boolean composed = false;
    /** Logging */
    private static Log log = LogFactory.getLog(MemoryRoleManagerImpl.class);
    /** List to store all our roles in */
    private static List roles = new ArrayList();
    private ComponentManager manager = null;
    /** Our permissionManager **/
    private PermissionManager permissionManager;
    /** Our Unique ID counter */
    private static int uniqueId = 0;
    /**
     * @return
     */
    PermissionManager getPermissionManager() throws ComponentException
    {
        if (permissionManager == null)
        {
            permissionManager = (PermissionManager) manager.lookup(PermissionManager.ROLE);
        }
        return permissionManager;
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
        role = (Role) new SimpleRole();
        
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
        return role;
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
            roleExists = checkExists(role);
            if (roleExists)
            {
                roles.remove(role);
                role.setName(name);
                roles.add(role);
                return;
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException("renameRole(Role,String)", e);
        }
        finally
        {
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
        boolean exists = false;
        for (Iterator i = roles.iterator(); i.hasNext();)
        {
            Role r = (Role) i.next();
            if (r.getName().equalsIgnoreCase(role.getName()) | r.getId() == role.getId())
            {
                exists = true;
            }
        }
        return exists;
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
        return new RoleSet(roles);
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
    public PermissionSet getPermissions(Role role) throws DataBackendException, UnknownEntityException
    {
        boolean roleExists = false;
        try
        {
            roleExists = checkExists(role);
            if (roleExists)
            {
                return getPermissionManager().getPermissions(role);
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException("getPermissions(Role) failed", e);
        }
        finally
        {
        }
        throw new UnknownEntityException("Unknown role '" + role.getName() + "'");
    }
    /**
	 * 	 if the <code>Permission</code> exists in the security system.
	 *
	 * @param permission a <code>Permission</code> value
	 * @return true if the permission exists in the system, false otherwise
	 * @throws DataBackendException when more than one Permission with
	 *         the same name exists.
	 * @throws Exception A generic exception.
	 */
    public boolean checkExists(Permission permission) throws DataBackendException
    {
        try
        {
            return getPermissionManager().checkExists(permission);
        }
        catch (ComponentException ce)
        {
            throw new DataBackendException("Problem getting permission manager", ce);
        }
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
    public synchronized Role addRole(Role role) throws DataBackendException, EntityExistsException
    {
        boolean roleExists = false;
        if (StringUtils.isEmpty(role.getName()))
        {
            throw new DataBackendException("Could not create a role with empty name!");
        }
        if (role.getId() != null)
        {
            throw new DataBackendException("Could not create a role with an id!");
        }
        try
        {
            roleExists = checkExists(role);
            if (!roleExists)
            {
                role.setId(getUniqueId());
                roles.add(role);
                // add the role to system-wide cache
                getAllRoles().add(role);
                // return the object with correct id
                return role;
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException("addRole(Role) failed", e);
        }
        finally
        {
        }
        // the only way we could get here without return/throw tirggered
        // is that the roleExists was true.
        throw new EntityExistsException("Role '" + role + "' already exists");
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
                roles.remove(role);
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
        }
        throw new UnknownEntityException("Unknown role '" + role + "'");
    }
    /**
   * Avalon component lifecycle method
   */
    public void compose(ComponentManager manager) throws ComponentException
    {
        this.manager = manager;
    }
    private Object getUniqueId()
    {
        return new Integer(++uniqueId);
    }
}
