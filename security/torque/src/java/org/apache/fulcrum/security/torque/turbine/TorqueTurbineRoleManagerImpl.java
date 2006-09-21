package org.apache.fulcrum.security.torque.turbine;
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.fulcrum.security.GroupManager;
import org.apache.fulcrum.security.PermissionManager;
import org.apache.fulcrum.security.UserManager;
import org.apache.fulcrum.security.entity.Group;
import org.apache.fulcrum.security.entity.Permission;
import org.apache.fulcrum.security.entity.Role;
import org.apache.fulcrum.security.entity.User;
import org.apache.fulcrum.security.model.turbine.entity.TurbineRole;
import org.apache.fulcrum.security.model.turbine.entity.TurbineUserGroupRole;
import org.apache.fulcrum.security.spi.AbstractRoleManager;
import org.apache.fulcrum.security.torque.om.TorqueGroup;
import org.apache.fulcrum.security.torque.om.TorqueGroupPeer;
import org.apache.fulcrum.security.torque.om.TorquePermission;
import org.apache.fulcrum.security.torque.om.TorquePermissionPeer;
import org.apache.fulcrum.security.torque.om.TorqueRole;
import org.apache.fulcrum.security.torque.om.TorqueRolePeer;
import org.apache.fulcrum.security.torque.om.TorqueTurbineRolePermissionPeer;
import org.apache.fulcrum.security.torque.om.TorqueTurbineUserGroupRole;
import org.apache.fulcrum.security.torque.om.TorqueTurbineUserGroupRolePeer;
import org.apache.fulcrum.security.torque.om.TorqueUser;
import org.apache.fulcrum.security.torque.om.TorqueUserPeer;
import org.apache.fulcrum.security.util.DataBackendException;
import org.apache.fulcrum.security.util.EntityExistsException;
import org.apache.fulcrum.security.util.PermissionSet;
import org.apache.fulcrum.security.util.RoleSet;
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
public class TorqueTurbineRoleManagerImpl extends AbstractRoleManager
{
    /**
    * Renames an existing Role.
    *
    * @param role The object describing the role to be renamed.
    * @param name the new name for the role.
    * @throws DataBackendException if there was an error accessing the data
    *         backend.
    * @throws UnknownEntityException if the role does not exist.
    */
    public synchronized void renameRole(Role role, String name)
        throws DataBackendException, UnknownEntityException
    {
        if (checkExists(role))
        {
            role.setName(name);
            
            try
            {
                TorqueRole r = new TorqueRole();
                r.setId((Integer)role.getId());
                r.setName(name);
                r.setNew(false);
                r.save();
            }
            catch (Exception e)
            {
                throw new DataBackendException("Renaming Role '" + role + "' failed", e);
            }
        }
        else
        {
            throw new UnknownEntityException("Unknown Role '" + role + "'");
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
            Criteria criteria = new Criteria();
            criteria.add(TorqueRolePeer.ROLE_NAME, roleName);

            roles = TorqueRolePeer.doSelect(criteria);
        }
        catch (TorqueException e)
        {
            throw new DataBackendException("Error retrieving role information", e);
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
        Connection con = null;

        try
        {
            con = Transaction.begin(TorqueRolePeer.DATABASE_NAME);
            
            List roles = TorqueRolePeer.doSelect(new Criteria(), con);
            
            for (Iterator i = roles.iterator(); i.hasNext();)
            {
                Role role = getRoleInstance();
                TorqueRole r = (TorqueRole)i.next();
                role.setId(r.getId());
                role.setName(r.getName());

                // Add user/group/role-relations if they exist
                ((TurbineRole)role).setUserGroupRoleSet(getUgrForRole(role, con));

                // Add permissions if they exist
                ((TurbineRole)role).setPermissions(getPermissionsForRole(role, con));

                roleSet.add(role);
            }

            Transaction.commit(con);
        }
        catch (TorqueException e)
        {
            Transaction.safeRollback(con);
            throw new DataBackendException("Error retrieving role information", e);
        }
        catch (UnknownEntityException e)
        {
            Transaction.safeRollback(con);
            throw new DataBackendException("Error creating permission instance", e);
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
        try
        {
            TorqueRole r = new TorqueRole();
            r.setName(role.getName());
            r.save();
            
            role.setId(r.getId());
        }
        catch (Exception e)
        {
            throw new DataBackendException("Adding Role '" + role + "' failed", e);
        }
        
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
        if (checkExists(role))
        {
            try
            {
                TorqueRolePeer.doDelete(SimpleKey.keyFor((Integer)role.getId()));
            }
            catch (TorqueException e)
            {
                throw new DataBackendException("Removing Role '" + role + "' failed", e);
            }
        }
        else
        {
            throw new UnknownEntityException("Unknown role '" + role + "'");
        }
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
        throws DataBackendException, UnknownEntityException 
    {
        Role role = getRoleInstance();

        if (id != null && id instanceof Integer)
        {
            Connection con = null;

            try
            {
                con = Transaction.begin(TorqueRolePeer.DATABASE_NAME);
                
                TorqueRole r = 
                    TorqueRolePeer.retrieveByPK((Integer)id, con);
                role.setId(r.getId());
                role.setName(r.getName());
                
                // Add user/group/role-relations if they exist
                ((TurbineRole)role).setUserGroupRoleSet(getUgrForRole(role, con));

                // Add permissions if they exist
                ((TurbineRole)role).setPermissions(getPermissionsForRole(role, con));

                Transaction.commit(con);
            }
            catch (NoRowsException e)
            {
                Transaction.safeRollback(con);
                throw new UnknownEntityException("Role with id '" + id + "' does not exist.", e);
            }
            catch (TorqueException e)
            {
                Transaction.safeRollback(con);
                throw new DataBackendException("Error retrieving role information", e);
            }
        }
        else
        {
            throw new UnknownEntityException("Invalid role id '" + role.getId() + "'");
        }

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
    public Role getRoleByName(String name)
        throws DataBackendException, UnknownEntityException
    {
        Role role = getRoleInstance();
        List roles = Collections.EMPTY_LIST;
        Connection con = null;

        try
        {
            con = Transaction.begin(TorqueRolePeer.DATABASE_NAME);
            
            Criteria criteria = new Criteria();
            criteria.add(TorqueRolePeer.ROLE_NAME, name);

            roles = TorqueRolePeer.doSelect(criteria, con);

            if (roles.size() == 1)
            {
                TorqueRole r = (TorqueRole) roles.get(0);
                
                role.setId(r.getId());
                role.setName(r.getName());
                
                // Add user/group/role-relations if they exist
                ((TurbineRole)role).setUserGroupRoleSet(getUgrForRole(role, con));

                // Add permissions if they exist
                ((TurbineRole)role).setPermissions(getPermissionsForRole(role, con));
            }

            Transaction.commit(con);
        }
        catch (TorqueException e)
        {
            Transaction.safeRollback(con);
            throw new DataBackendException("Error retrieving role information", e);
        }

        if (roles.size() == 0)
        {
            throw new UnknownEntityException("Could not find role" + name);
        }

        if (roles.size() > 1)
        {
            throw new DataBackendException("Multiple Roles with same name '" + name + "'");
        }

        return role;
    }

    /**
     * Provides the user/group/role-relations for the given role
     *  
     * @param role the role for which the relations should be retrieved  
     * @param con a database connection
     */
    private Set getUgrForRole(Role role, Connection con)
        throws TorqueException, DataBackendException
    {
        Set ugrSet = new HashSet();
        
        Criteria criteria = new Criteria();
        criteria.add(TorqueTurbineUserGroupRolePeer.ROLE_ID, (Integer)role.getId());
        
        List ugrs = TorqueTurbineUserGroupRolePeer.doSelect(criteria, con);
        UserManager userManager = getUserManager();
        GroupManager groupManager = getGroupManager();
        
        for (Iterator i = ugrs.iterator(); i.hasNext();)
        {
            TurbineUserGroupRole ugr = new TurbineUserGroupRole();
            ugr.setRole(role);
            
            TorqueTurbineUserGroupRole tugr = (TorqueTurbineUserGroupRole)i.next();

            User user = userManager.getUserInstance();
            TorqueUser u = TorqueUserPeer.retrieveByPK(tugr.getUserId(), con);
            user.setId(u.getId());
            user.setName(u.getName());
            user.setPassword(u.getPassword());
            ugr.setUser(user);

            Group group = groupManager.getGroupInstance();
            TorqueGroup g = TorqueGroupPeer.retrieveByPK(tugr.getGroupId(), con);
            group.setId(g.getId());
            group.setName(g.getName());
            ugr.setGroup(group);
            
            ugrSet.add(ugr);
        }
        
        return ugrSet;
    }

    /**
     * Provides the permissions for the given role
     *  
     * @param role the role for which the permissions should be retrieved  
     * @param con a database connection
     */
    private PermissionSet getPermissionsForRole(Role role, Connection con)
        throws TorqueException, DataBackendException, UnknownEntityException
    {
        PermissionSet permissionSet = new PermissionSet();
        
        Criteria criteria = new Criteria();
        criteria.addJoin(TorqueTurbineRolePermissionPeer.PERMISSION_ID, TorquePermissionPeer.PERMISSION_ID);
        criteria.add(TorqueTurbineRolePermissionPeer.ROLE_ID, (Integer)role.getId());
        
        List permissions = TorquePermissionPeer.doSelect(criteria, con);
        PermissionManager permissionManager = getPermissionManager();
        
        for (Iterator i = permissions.iterator(); i.hasNext();)
        {
            TorquePermission p = (TorquePermission)i.next();
            Permission permission = permissionManager.getPermissionInstance();
            
            permission.setId(p.getId());
            permission.setName(p.getName());
            permissionSet.add(permission);
        }
        
        return permissionSet;
    }
}
