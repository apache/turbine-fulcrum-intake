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
import java.util.List;
import net.sf.hibernate.Hibernate;
import net.sf.hibernate.HibernateException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.security.PermissionManager;
import org.apache.fulcrum.security.entity.Permission;
import org.apache.fulcrum.security.entity.Role;
import org.apache.fulcrum.security.model.simple.entity.SimplePermission;
import org.apache.fulcrum.security.model.simple.entity.SimpleRole;
import org.apache.fulcrum.security.util.DataBackendException;
import org.apache.fulcrum.security.util.EntityExistsException;
import org.apache.fulcrum.security.util.PermissionSet;
import org.apache.fulcrum.security.util.UnknownEntityException;
/**
 * This implementation persists to a database via Hibernate.
 *
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class HibernatePermissionManagerImpl extends BaseHibernateManager implements PermissionManager
{
    /** Logging */
    private static Log log = LogFactory.getLog(HibernatePermissionManagerImpl.class);
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
        permission = (Permission) new SimplePermission();
      
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
        PermissionSet permissionSet = new PermissionSet();
        try
        {
            
            List permissions = retrieveSession().find("from SimplePermission");
            permissionSet.add(permissions);
	
        }
        catch (HibernateException e)
        {
            throw new DataBackendException("Error retriving permission information", e);
        }
        return permissionSet;
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
        permissionExists = checkExists(permission);
        if (permissionExists)
        {
            permission.setName(name);
            updateEntity(permission);
            return;
        }
        else
        {
            throw new UnknownEntityException("Unknown permission '" + permission + "'");
        }
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
        List permissions;
        try
        {
        
            permissions =
			retrieveSession().find("from SimplePermission sr where sr.name=?", permission.getName(), Hibernate.STRING);
		
        }
        catch (HibernateException e)
        {
            throw new DataBackendException("Error retriving permission information", e);
        }
        if (permissions.size() > 1)
        {
            throw new DataBackendException("Multiple permissions with same name '" + permission.getName() + "'");
        }
        return (permissions.size() == 1);
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
        permissionExists = checkExists(permission);
        if (permissionExists)
        {
            deleteEntity(permission);
        }
        else
        {
            throw new UnknownEntityException("Unknown permission '" + permission + "'");
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
        if (permission.getId()!=null)
        {
            throw new DataBackendException("Could not create a permission with an id!");
        }
        if (checkExists(permission))
        {
            throw new EntityExistsException("The permission '" + permission.getName() + "' already exists");
        }
        addEntity(permission);
        return permission;
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
        return ((SimpleRole)role).getPermissions();
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
        return getRoleManager().checkExists(role);
    }
}
