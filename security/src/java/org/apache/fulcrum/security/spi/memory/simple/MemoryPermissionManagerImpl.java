package org.apache.fulcrum.security.spi.memory.simple;
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
import org.apache.fulcrum.security.model.simple.entity.SimplePermission;
import org.apache.fulcrum.security.model.simple.entity.SimpleRole;
import org.apache.fulcrum.security.util.DataBackendException;
import org.apache.fulcrum.security.util.EntityExistsException;
import org.apache.fulcrum.security.util.PermissionSet;
import org.apache.fulcrum.security.util.UnknownEntityException;
/**
 * This implementation keeps all objects in memory.  This is mostly meant to help
 * with testing and prototyping of ideas.
 *
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class MemoryPermissionManagerImpl extends AbstractLogEnabled implements PermissionManager, Composable
{
    /** Logging */
    private static Log log = LogFactory.getLog(MemoryPermissionManagerImpl.class);
    private static List permissions = new ArrayList();
    private ComponentManager manager = null;
    private RoleManager roleManager = null;
    /** Our Unique ID counter */
    private static int uniqueId = 0;
    /**
    	 * @return
    	 */
    RoleManager getRoleManager() throws ComponentException
    {
        if (roleManager == null)
        {
            roleManager = (RoleManager) manager.lookup(RoleManager.ROLE);
        }
        return roleManager;
    }
    /**
     * Construct a blank Permission object.
     *
     * This method calls getPermissionClass, and then creates a new object using
     * the default constructor.
     *
     * @return an object implementing Permission interface.
     * @throws UnknownEntityException if the object could not be instantiated.
     */
    public Permission getPermissionInstance() throws UnknownEntityException
    {
        Permission permission;
        try
        {
            permission = (Permission) new SimplePermission();
        }
        catch (Exception e)
        {
            throw new UnknownEntityException("Failed to instantiate a Permission implementation object", e);
        }
        return permission;
    }
    /**
     * Construct a blank Permission object.
     *
     * This method calls getPermissionClass, and then creates a new object using
     * the default constructor.
     *
     * @param permName The name of the permission.
     *
     * @return an object implementing Permission interface.
     * @throws UnknownEntityException if the object could not be instantiated.
     */
    public Permission getPermissionInstance(String permName) throws UnknownEntityException
    {
        Permission perm = getPermissionInstance();
        perm.setName(permName);
        return perm;
    }
    /**
       * Retrieve a Permission object with specified name.
       *
       * @param name the name of the Permission.
       * @return an object representing the Permission with specified name.
       * @throws DataBackendException if there was an error accessing the
       *         data backend.
       * @throws UnknownEntityException if the permission does not exist.
       */
    public Permission getPermissionByName(String name) throws DataBackendException, UnknownEntityException
    {
        Permission permission = getAllPermissions().getPermissionByName(name);
        if (permission == null)
        {
            throw new UnknownEntityException("The specified permission does not exist");
        }
        return permission;
    }
    /**
     * Retrieve a Permission object with specified Id.
     *
     * @param name the name of the Permission.
     *
     * @return an object representing the Permission with specified name.
     *
     * @throws UnknownEntityException if the permission does not
     *            exist in the database.
     * @throws DataBackendException if there is a problem accessing the
     *            storage.
     */
    public Permission getPermissionById(Object id) throws DataBackendException, UnknownEntityException
    {
        Permission permission = getAllPermissions().getPermissionById(id);
        if (permission == null)
        {
            throw new UnknownEntityException("The specified permission does not exist");
        }
        return permission;
    }
    /**
    	* Retrieves all permissions defined in the system.
    	*
    	* @return the names of all roles defined in the system.
    	* @throws DataBackendException if there was an error accessing the
    	*         data backend.
    	*/
    public PermissionSet getAllPermissions() throws DataBackendException
    {
        return new PermissionSet(permissions);
    }
    /**
	* Renames an existing Permission.
	*
	* @param permission The object describing the permission to be renamed.
	* @param name the new name for the permission.
	* @throws DataBackendException if there was an error accessing the data
	*         backend.
	* @throws UnknownEntityException if the permission does not exist.
	*/
    public synchronized void renamePermission(Permission permission, String name)
        throws DataBackendException, UnknownEntityException
    {
        boolean permissionExists = false;
        try
        {
            permissionExists = checkExists(permission);
            if (permissionExists)
            {
                permissions.remove(permission);
                permission.setName(name);
                permissions.add(permission);
                return;
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException("renamePermission(Permission,name)", e);
        }
        finally
        {
        }
        throw new UnknownEntityException("Unknown permission '" + permission + "'");
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
    public boolean checkExists(Permission permission) throws DataBackendException
    {
        boolean exists = false;
        for (Iterator i = permissions.iterator(); i.hasNext();)
        {
            Permission p = (Permission) i.next();
            if (p.getName().equalsIgnoreCase(permission.getName()) | p.getId() == permission.getId())
            {
                exists = true;
            }
        }
        return exists;
    }
  
    /**
     * Removes a Permission from the system.
     *
     * @param permission The object describing the permission to be removed.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws UnknownEntityException if the permission does not exist.
     */
    public synchronized void removePermission(Permission permission)
        throws DataBackendException, UnknownEntityException
    {
        boolean permissionExists = false;
        try
        {
            permissionExists = checkExists(permission);
            if (permissionExists)
            {
                permissions.remove(permission);
            }
            else
            {
                throw new UnknownEntityException("Unknown permission '" + permission + "'");
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException("removePermission(Permission)", e);
        }
        finally
        {
        }
    }
    /**
	* Creates a new permission with specified attributes.
	*
	* @param permission the object describing the permission to be created.
	* @return a new Permission object that has id set up properly.
	* @throws DataBackendException if there was an error accessing the data
	*         backend.
	* @throws EntityExistsException if the permission already exists.
	*/
    public synchronized Permission addPermission(Permission permission)
        throws DataBackendException, EntityExistsException
    {
        boolean permissionExists = false;
        if (StringUtils.isEmpty(permission.getName()))
        {
            throw new DataBackendException("Could not create a permission with empty name!");
        }
        if (permission.getId() != null)
        {
            throw new DataBackendException("Could not create a permission with an id!");
        }
        try
        {
            permissionExists = checkExists(permission);
            if (!permissionExists)
            {
                permission.setId(getUniqueId());
                permissions.add(permission);
                return permission;
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException("addPermission(Permission) failed", e);
        }
        finally
        {
        }
        // the only way we could get here without return/throw tirggered
        // is that the permissionExists was true.
        throw new EntityExistsException("Permission '" + permission + "' already exists");
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
                return ((SimpleRole)role).getPermissions();
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
        try
        {
            return getRoleManager().checkExists(role);
        }
        catch (ComponentException ce)
        {
            throw new DataBackendException("Problem accessing role manager", ce);
        }
    }
    /**
    	  * Avalon component lifecycle method
    	  */
    public void compose(ComponentManager manager) throws ComponentException
    {
        this.manager = manager;
    }
    private Integer getUniqueId()
    {
        return new Integer(++uniqueId);
    }
}
