package org.apache.fulcrum.security.torque;
/*
 *  Copyright 2001-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
import java.sql.Connection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.fulcrum.security.entity.Permission;
import org.apache.fulcrum.security.spi.AbstractPermissionManager;
import org.apache.fulcrum.security.torque.om.TorquePermission;
import org.apache.fulcrum.security.torque.om.TorquePermissionPeer;
import org.apache.fulcrum.security.util.DataBackendException;
import org.apache.fulcrum.security.util.EntityExistsException;
import org.apache.fulcrum.security.util.PermissionSet;
import org.apache.fulcrum.security.util.UnknownEntityException;
import org.apache.torque.NoRowsException;
import org.apache.torque.TorqueException;
import org.apache.torque.om.SimpleKey;
import org.apache.torque.util.Criteria;
import org.apache.torque.util.Transaction;
/**
 * This implementation persists to a database via Torque.
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 * @version $Id:$
 */
public abstract class TorqueAbstractPermissionManager extends AbstractPermissionManager
{
    /**
     * Provides the attached object lists for the given permission
     *  
     * @param permission the permission for which the lists should be retrieved  
     * @param con a database connection
     */
    protected abstract void attachObjectsForPermission(Permission permission, Connection con)
        throws TorqueException, DataBackendException;

    /**
     * Retrieves all permissions defined in the system.
     *
     * @return the names of all roles defined in the system.
     * @throws DataBackendException
     *             if there was an error accessing the data backend.
     */
    public PermissionSet getAllPermissions() throws DataBackendException
    {
        PermissionSet permissionSet = new PermissionSet();
        Connection con = null;

        try
        {
            con = Transaction.begin(TorquePermissionPeer.DATABASE_NAME);
            
            List permissions = TorquePermissionPeer.doSelect(new Criteria(), con);
            
            for (Iterator i = permissions.iterator(); i.hasNext();)
            {
                TorquePermission p = (TorquePermission)i.next();

                // TODO: This throws UnknownEntityException.
                Permission permission = getPermissionInstance();
                permission.setId(p.getId());
                permission.setName(p.getName());

                // Add attached objects if they exist
                attachObjectsForPermission(permission, con);

                permissionSet.add(permission);
            }

            Transaction.commit(con);
        }
        catch (Exception e)
        {
            Transaction.safeRollback(con);
            throw new DataBackendException("Error retrieving permission information", e);
        }

        return permissionSet;
    }

    /**
     * Renames an existing Permission.
     *
     * @param permission
     *            The object describing the permission to be renamed.
     * @param name
     *            the new name for the permission.
     * @throws DataBackendException
     *             if there was an error accessing the data backend.
     * @throws UnknownEntityException
     *             if the permission does not exist.
     */
    public synchronized void renamePermission(Permission permission, String name)
        throws DataBackendException, UnknownEntityException
    {
        if (checkExists(permission))
        {
            permission.setName(name);
            
            try
            {
                TorquePermission p = new TorquePermission();
                p.setId((Integer)permission.getId());
                p.setName(name);
                p.setNew(false);
                p.save();
            }
            catch (Exception e)
            {
                throw new DataBackendException("Renaming Permission '" + permission + "' failed", e);
            }
        }
        else
        {
            throw new UnknownEntityException("Unknown permission '" + permission + "'");
        }
    }

    /**
     * Determines if the <code>Permission</code> exists in the security
     * system.
     *
     * @param permissionName
     *            a <code>Permission</code> value
     * @return true if the permission name exists in the system, false otherwise
     * @throws DataBackendException
     *             when more than one Permission with the same name exists.
     */
    public boolean checkExists(String permissionName) throws DataBackendException
    {
        List permissions;
        
        try
        {
            Criteria criteria = new Criteria();
            criteria.add(TorquePermissionPeer.PERMISSION_NAME, permissionName);

            permissions = TorquePermissionPeer.doSelect(criteria);
        }
        catch (TorqueException e)
        {
            throw new DataBackendException("Error retrieving permission information", e);
        }

        if (permissions.size() > 1)
        {
            throw new DataBackendException("Multiple permissions with same name '" + permissionName + "'");
        }

        return (permissions.size() == 1);
    }

    /**
     * Removes a Permission from the system.
     *
     * @param permission
     *            The object describing the permission to be removed.
     * @throws DataBackendException
     *             if there was an error accessing the data backend.
     * @throws UnknownEntityException
     *             if the permission does not exist.
     */
    public synchronized void removePermission(Permission permission)
        throws DataBackendException, UnknownEntityException
    {
        if (checkExists(permission))
        {
            try
            {
                TorquePermissionPeer.doDelete(SimpleKey.keyFor((Integer)permission.getId()));
            }
            catch (TorqueException e)
            {
                throw new DataBackendException("Removing Permission '" + permission + "' failed", e);
            }
        }
        else
        {
            throw new UnknownEntityException("Unknown permission '" + permission + "'");
        }
    }

    /**
     * Creates a new permission with specified attributes.
     *
     * @param permission
     *            the object describing the permission to be created.
     * @return a new Permission object that has id set up properly.
     * @throws DataBackendException
     *             if there was an error accessing the data backend.
     * @throws EntityExistsException
     *             if the permission already exists.
     */
    protected synchronized Permission persistNewPermission(Permission permission)
        throws DataBackendException
    {
        try
        {
            TorquePermission p = new TorquePermission();
            p.setName(permission.getName());
            p.save();
            
            permission.setId(p.getId());
        }
        catch (Exception e)
        {
            throw new DataBackendException("Adding Permission '" + permission + "' failed", e);
        }

        return permission;
    }

    /**
     * Retrieve a Permission object with specified id.
     *
     * @param id
     *            the id of the Permission.
     * @return an object representing the Permission with specified id.
     * @throws DataBackendException
     *             if there was an error accessing the data backend.
     * @throws UnknownEntityException
     *             if the permission does not exist.
     */
    public Permission getPermissionById(Object id)
        throws DataBackendException, UnknownEntityException
    {
        Permission permission = getPermissionInstance();

        if (id != null && id instanceof Integer)
        {
            Connection con = null;

            try
            {
                con = Transaction.begin(TorquePermissionPeer.DATABASE_NAME);
                
                TorquePermission p = 
                    TorquePermissionPeer.retrieveByPK((Integer)id, con);
                permission.setId(p.getId());
                permission.setName(p.getName());

                // Add attached objects if they exist
                attachObjectsForPermission(permission, con);

                Transaction.commit(con);
            }
            catch (NoRowsException e)
            {
                Transaction.safeRollback(con);
                throw new UnknownEntityException("Permission with id '" + id + "' does not exist.", e);
            }
            catch (TorqueException e)
            {
                Transaction.safeRollback(con);
                throw new DataBackendException("Error retrieving permission information", e);
            }
        }
        else
        {
            throw new UnknownEntityException("Invalid permission id '" + permission.getId() + "'");
        }

        return permission;
    }

    /**
     * Retrieve a Permission object with specified name.
     *
     * @param name the name of the Group.
     * @return an object representing the Group with specified name.
     * @throws DataBackendException if there was an error accessing the
     *         data backend.
     * @throws UnknownEntityException if the group does not exist.
     */
    public Permission getPermissionByName(String name)
        throws DataBackendException, UnknownEntityException
    {
        Permission permission = getPermissionInstance();
        List permissions = Collections.EMPTY_LIST;
        Connection con = null;

        try
        {
            con = Transaction.begin(TorquePermissionPeer.DATABASE_NAME);
            
            Criteria criteria = new Criteria();
            criteria.add(TorquePermissionPeer.PERMISSION_NAME, name);

            permissions = TorquePermissionPeer.doSelect(criteria, con);

            if (permissions.size() == 1)
            {
                TorquePermission p = (TorquePermission) permissions.get(0);
                
                permission.setId(p.getId());
                permission.setName(p.getName());
                
                // Add attached objects if they exist
                attachObjectsForPermission(permission, con);
            }

            Transaction.commit(con);
        }
        catch (TorqueException e)
        {
            Transaction.safeRollback(con);
            throw new DataBackendException("Error retrieving permission information", e);
        }

        if (permissions.size() == 0)
        {
            throw new UnknownEntityException("Could not find permission " + name);
        }

        if (permissions.size() > 1)
        {
            throw new DataBackendException("Multiple Permissions with same name '" + name + "'");
        }

        return permission;
    }
}
