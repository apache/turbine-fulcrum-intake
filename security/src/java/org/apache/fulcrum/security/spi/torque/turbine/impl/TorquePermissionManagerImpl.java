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
import org.apache.fulcrum.security.spi.torque.turbine.TorquePermissionManager;
import org.apache.fulcrum.security.spi.torque.turbine.peermanagers.PermissionPeerManager;
import org.apache.fulcrum.security.spi.torque.turbine.peermanagers.RolePeerManager;
import org.apache.fulcrum.security.util.DataBackendException;
import org.apache.fulcrum.security.util.EntityExistsException;
import org.apache.fulcrum.security.util.PermissionSet;
import org.apache.fulcrum.security.util.UnknownEntityException;
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
public class TorquePermissionManagerImpl extends TorqueManagerComponent implements TorquePermissionManager
{
    /** Logging */
    private static Log log = LogFactory.getLog(TorquePermissionManagerImpl.class);
    /** The class of Permission the SecurityService uses */
    private Class permissionClass = null;
    /**
       * Return a Class object representing the system's chosen implementation of
       * of Permission interface.
       *
       * @return systems's chosen implementation of Permission interface.
       * @throws UnknownEntityException if the implementation of Permission interface
       *         could not be determined, or does not exist.
       */
    public Class getPermissionClass() throws UnknownEntityException
    {
        if (permissionClass == null)
        {
            throw new UnknownEntityException("Failed to create a Class object for Permission implementation");
        }
        return permissionClass;
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
            permission = (Permission) getPermissionClass().newInstance();
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
        return getPermissions(new Criteria());
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
            lockExclusive();
            permissionExists = checkExists(permission);
            if (permissionExists)
            {
                permission.setName(name);
                Criteria criteria = PermissionPeerManager.buildCriteria(permission);
                PermissionPeerManager.doUpdate(criteria);
                return;
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException("renamePermission(Permission,name)", e);
        }
        finally
        {
            unlockExclusive();
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
        try
        {
            return PermissionPeerManager.checkExists(permission);
        }
        catch (Exception e)
        {
            throw new DataBackendException("Problem checking if permission exists", e);
        }
    }
    /**
    	* Stores Permission's attributes. The Permissions is required to exist in
    	* the system.
    	*
    	* @param permission The Permission to be stored.
    	* @throws DataBackendException if there was an error accessing the data
    	*         backend.
    	* @throws UnknownEntityException if the permission does not exist.
    	*/
    public void savePermission(Permission permission) throws DataBackendException, UnknownEntityException
    {
        boolean permissionExists = false;
        try
        {
            permissionExists = checkExists(permission);
            if (permissionExists)
            {
                Criteria criteria = PermissionPeerManager.buildCriteria(permission);
                PermissionPeerManager.doUpdate(criteria);
                return;
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException("savePermission(Permission) failed", e);
        }
        throw new UnknownEntityException("Unknown permission '" + permission + "'");
    }
    /**
    	 * Retrieve a set of Permissions that meet the specified Criteria.
    	 *
    	 * @param criteria A Criteria of Permissions selection.
    	 * @return a set of Permissions that meet the specified Criteria.
    	 * @throws DataBackendException if there was an error accessing the data
    	 *         backend.
    	 */
    public PermissionSet getPermissions(Criteria criteria) throws DataBackendException
    {
        Criteria torqueCriteria = new Criteria();
        Iterator keys = criteria.keySet().iterator();
        while (keys.hasNext())
        {
            String key = (String) keys.next();
            torqueCriteria.put(PermissionPeerManager.getColumnName(key), criteria.get(key));
        }
        List permissions = new ArrayList(0);
        try
        {
            permissions = PermissionPeerManager.doSelect(criteria);
        }
        catch (Exception e)
        {
            throw new DataBackendException("getPermissions(Criteria) failed", e);
        }
        return new PermissionSet(permissions);
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
            lockExclusive();
            permissionExists = checkExists(permission);
            if (permissionExists)
            {
                Criteria criteria = PermissionPeerManager.buildCriteria(permission);
                PermissionPeerManager.doDelete(criteria);
                getAllPermissions().remove(permission);
                return;
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException("removePermission(Permission)", e);
        }
        finally
        {
            unlockExclusive();
        }
        throw new UnknownEntityException("Unknown permission '" + permission + "'");
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
            throw new DataBackendException("Could not create " + "a permission with empty name!");
        }
        try
        {
            lockExclusive();
            permissionExists = checkExists(permission);
            if (!permissionExists)
            {
                // add a row to the table
                Criteria criteria = PermissionPeerManager.buildCriteria(permission);
                PermissionPeerManager.doInsert(criteria);
                // try to get the object back using the name as key.
                criteria = new Criteria();
                criteria.add(PermissionPeerManager.getNameColumn(), permission.getName());
                List results = PermissionPeerManager.doSelect(criteria);
                if (results.size() != 1)
                {
                    throw new DataBackendException("Internal error - query returned " + results.size() + " rows");
                }
                Permission newPermission = (Permission) results.get(0);
                // add the permission to system-wide cache
                getAllPermissions().add(newPermission);
                // return the object with correct id
                return newPermission;
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException("addPermission(Permission) failed", e);
        }
        finally
        {
            unlockExclusive();
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
    protected boolean checkExists(Role role) throws DataBackendException, Exception
    {
        return RolePeerManager.checkExists(role);
    }
}