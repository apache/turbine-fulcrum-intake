package org.apache.fulcrum.security.torque.dynamic;
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
import org.apache.fulcrum.security.entity.Group;
import org.apache.fulcrum.security.entity.Permission;
import org.apache.fulcrum.security.entity.Role;
import org.apache.fulcrum.security.entity.User;
import org.apache.fulcrum.security.model.dynamic.AbstractDynamicModelManager;
import org.apache.fulcrum.security.model.dynamic.DynamicModelManager;
import org.apache.fulcrum.security.model.dynamic.entity.DynamicGroup;
import org.apache.fulcrum.security.model.dynamic.entity.DynamicPermission;
import org.apache.fulcrum.security.model.dynamic.entity.DynamicRole;
import org.apache.fulcrum.security.model.dynamic.entity.DynamicUser;
import org.apache.fulcrum.security.torque.om.TorqueDynamicGroupRole;
import org.apache.fulcrum.security.torque.om.TorqueDynamicGroupRolePeer;
import org.apache.fulcrum.security.torque.om.TorqueDynamicRolePermission;
import org.apache.fulcrum.security.torque.om.TorqueDynamicRolePermissionPeer;
import org.apache.fulcrum.security.torque.om.TorqueDynamicUserDelegates;
import org.apache.fulcrum.security.torque.om.TorqueDynamicUserDelegatesPeer;
import org.apache.fulcrum.security.torque.om.TorqueDynamicUserGroup;
import org.apache.fulcrum.security.torque.om.TorqueDynamicUserGroupPeer;
import org.apache.fulcrum.security.util.DataBackendException;
import org.apache.fulcrum.security.util.UnknownEntityException;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;
/**
 * This implementation persists to a database via Torque.
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 * @version $Id:$
 */
public class TorqueDynamicModelManagerImpl extends AbstractDynamicModelManager implements DynamicModelManager
{
    /**
     * Revokes a Role from a Group.
     *
     * @param group the Group.
     * @param role the Role.
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws UnknownEntityException if group or role is not present.
     */
    public synchronized void revoke(Group group, Role role)
        throws DataBackendException, UnknownEntityException
    {
        boolean groupExists = false;
        boolean roleExists = false;
        try
        {
            groupExists = getGroupManager().checkExists(group);
            roleExists = getRoleManager().checkExists(role);

            if (groupExists && roleExists)
            {
                ((DynamicGroup) group).removeRole(role);
                ((DynamicRole) role).removeGroup(group);

                Criteria criteria = new Criteria();
                criteria.add(TorqueDynamicGroupRolePeer.ROLE_ID, (Integer)role.getId());
                criteria.add(TorqueDynamicGroupRolePeer.GROUP_ID, (Integer)group.getId());
                TorqueDynamicGroupRolePeer.doDelete(criteria);
            }
        }
        catch (TorqueException e)
        {
            throw new DataBackendException("revoke('" + group.getName() + "', '" + role.getName() + "') failed", e);
        }

        if (!groupExists)
        {
            throw new UnknownEntityException("Unknown group '" + group.getName() + "'");
        }

        if (!roleExists)
        {
            throw new UnknownEntityException("Unknown role '" + role.getName() + "'");
        }
    }

    /**
     * Grants a Role a Permission
     *
     * @param role the Role.
     * @param permission the Permission.
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws UnknownEntityException if role or permission is not present.
     */
    public synchronized void grant(Role role, Permission permission)
        throws DataBackendException, UnknownEntityException
    {
        boolean roleExists = false;
        boolean permissionExists = false;

        try
        {
            roleExists = getRoleManager().checkExists(role);
            permissionExists = getPermissionManager().checkExists(permission);

            if (roleExists && permissionExists)
            {
                ((DynamicRole) role).addPermission(permission);
                ((DynamicPermission) permission).addRole(role);

                TorqueDynamicRolePermission rp = new TorqueDynamicRolePermission();
                rp.setPermissionId((Integer)permission.getId());
                rp.setRoleId((Integer)role.getId());
                rp.save();
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException("grant('" + role.getName() + "', '" + permission.getName() + "') failed", e);
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
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws UnknownEntityException if role or permission is not present.
     */
    public synchronized void revoke(Role role, Permission permission)
        throws DataBackendException, UnknownEntityException
    {
        boolean roleExists = false;
        boolean permissionExists = false;

        try
        {
            roleExists = getRoleManager().checkExists(role);
            permissionExists = getPermissionManager().checkExists(permission);

            if (roleExists && permissionExists)
            {
                ((DynamicRole) role).removePermission(permission);
                ((DynamicPermission) permission).removeRole(role);

                Criteria criteria = new Criteria();
                criteria.add(TorqueDynamicRolePermissionPeer.ROLE_ID, (Integer)role.getId());
                criteria.add(TorqueDynamicRolePermissionPeer.PERMISSION_ID, (Integer)permission.getId());
                TorqueDynamicRolePermissionPeer.doDelete(criteria);
            }
        }
        catch (TorqueException e)
        {
            throw new DataBackendException("revoke('" + role.getName() + "', '" + permission.getName() + "') failed", e);
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
     * Puts a user in a group.
     *
     * This method is used when adding a user to a group
     *
     * @param user the User.
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws UnknownEntityException if the account is not present.
     */
    public synchronized void grant(User user, Group group) throws DataBackendException, UnknownEntityException
    {
        boolean groupExists = false;
        boolean userExists = false;
        
        try
        {
            groupExists = getGroupManager().checkExists(group);
            userExists = getUserManager().checkExists(user);

            if (groupExists && userExists)
            {
                ((DynamicUser) user).addGroup(group);
                ((DynamicGroup) group).addUser(user);
                
                TorqueDynamicUserGroup ug = new TorqueDynamicUserGroup();
                ug.setGroupId((Integer)group.getId());
                ug.setUserId((Integer)user.getId());
                ug.save();

                return;
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException("grant('" + user.getName() + "', '" + group.getName() + "') failed", e);
        }

        if (!groupExists)
        {
            throw new UnknownEntityException("Unknown group '" + group.getName() + "'");
        }

        if (!userExists)
        {
            throw new UnknownEntityException("Unknown user '" + user.getName() + "'");
        }
    }

    /**
     * Removes a user in a group.
     *
     * This method is used when removing a user to a group
     *
     * @param user the User.
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws UnknownEntityException if the user or group is not present.
     */
    public synchronized void revoke(User user, Group group) throws DataBackendException, UnknownEntityException
    {
        boolean groupExists = false;
        boolean userExists = false;
        
        try
        {
            groupExists = getGroupManager().checkExists(group);
            userExists = getUserManager().checkExists(user);

            if (groupExists && userExists)
            {
                ((DynamicUser) user).removeGroup(group);
                ((DynamicGroup) group).removeUser(user);
                
                Criteria criteria = new Criteria();
                criteria.add(TorqueDynamicUserGroupPeer.GROUP_ID, (Integer)group.getId());
                criteria.add(TorqueDynamicUserGroupPeer.USER_ID, (Integer)user.getId());
                TorqueDynamicUserGroupPeer.doDelete(criteria);

                return;
            }
        }
        catch (TorqueException e)
        {
            throw new DataBackendException("revoke('" + user.getName() + "', '" + group.getName() + "') failed", e);
        }

        if (!groupExists)
        {
            throw new UnknownEntityException("Unknown group '" + group.getName() + "'");
        }

        if (!userExists)
        {
            throw new UnknownEntityException("Unknown user '" + user.getName() + "'");
        }
    }

    /**
     * Grants a Group a Role
     *
     * @param group the Group.
     * @param role the Role.
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws UnknownEntityException if group or role is not present.
     */
    public synchronized void grant(Group group, Role role)
        throws DataBackendException, UnknownEntityException
    {
        boolean groupExists = false;
        boolean roleExists = false;
        
        try
        {
            groupExists = getGroupManager().checkExists(group);
            roleExists = getRoleManager().checkExists(role);
            if (groupExists && roleExists)
            {
                ((DynamicGroup) group).addRole(role);
                ((DynamicRole) role).addGroup(group);
                
                TorqueDynamicGroupRole gr = new TorqueDynamicGroupRole();
                gr.setGroupId((Integer)group.getId());
                gr.setRoleId((Integer)role.getId());
                gr.save();
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException("grant('" + group.getName() + "', '" + role.getName() + "') failed", e);
        }

        if (!groupExists)
        {
            throw new UnknownEntityException("Unknown group '" + group.getName() + "'");
        }

        if (!roleExists)
        {
            throw new UnknownEntityException("Unknown role '" + role.getName() + "'");
        }
    }

    /**
     * Allow B to assumes A's roles, groups and permissions
     * @param delegator A
     * @param delegatee B
     */
    public synchronized void addDelegate(User delegator, User delegatee)
            throws DataBackendException, UnknownEntityException 
    {
        boolean delegatorExists = false;
        boolean delegateeExists = false;
        
        try
        {
            delegatorExists = getUserManager().checkExists(delegator);
            delegateeExists = getUserManager().checkExists(delegatee);

            if (delegatorExists && delegateeExists)
            {
                super.addDelegate(delegator, delegatee);

                TorqueDynamicUserDelegates d = new TorqueDynamicUserDelegates();
                d.setDelegatorUserId((Integer)delegator.getId());
                d.setDelegateeUserId((Integer)delegatee.getId());
                d.save();
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException("addDelegate('" 
                    + delegator.getName() + "', '" 
                    + delegatee.getName() + "') failed", e);
        }

        if (!delegatorExists)
        {
            throw new UnknownEntityException("Unknown user '" + delegator.getName() + "'");
        }

        if (!delegateeExists)
        {
            throw new UnknownEntityException("Unknown user '" + delegatee.getName() + "'");
        }
    }

    /**
     * Stop A having B's roles, groups and permissions
     * @param delegate A
     * @param delegatee B
     */
    public synchronized void removeDelegate(User delegator, User delegatee)
            throws DataBackendException, UnknownEntityException 
    {
        boolean delegatorExists = false;
        boolean delegateeExists = false;
        
        try
        {
            delegatorExists = getUserManager().checkExists(delegator);
            delegateeExists = getUserManager().checkExists(delegatee);

            if (delegatorExists && delegateeExists)
            {
                super.removeDelegate(delegator, delegatee);

                Criteria criteria = new Criteria();
                criteria.add(TorqueDynamicUserDelegatesPeer.DELEGATOR_USER_ID, (Integer)delegator.getId());
                criteria.add(TorqueDynamicUserDelegatesPeer.DELEGATEE_USER_ID, (Integer)delegatee.getId());
                TorqueDynamicUserDelegatesPeer.doDelete(criteria);
            }
        }
        catch (TorqueException e)
        {
            throw new DataBackendException("removeDelegate('" 
                    + delegator.getName() + "', '" 
                    + delegatee.getName() + "') failed", e);
        }

        if (!delegatorExists)
        {
            throw new UnknownEntityException("Unknown user '" + delegator.getName() + "'");
        }

        if (!delegateeExists)
        {
            throw new UnknownEntityException("Unknown user '" + delegatee.getName() + "'");
        }
    }
}