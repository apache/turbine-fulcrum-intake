package org.apache.fulcrum.security.spi.torque.turbine.impl;
/*
 * ==================================================================== The Apache Software
 * License, Version 1.1
 * 
 * Copyright (c) 2001-2003 The Apache Software Foundation. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met: 1. Redistributions of source code must retain
 * the above copyright notice, this list of conditions and the following disclaimer. 2.
 * Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided
 * with the distribution. 3. The end-user documentation included with the redistribution, if any,
 * must include the following acknowledgment: "This product includes software developed by the
 * Apache Software Foundation (http://www.apache.org/)." Alternately, this acknowledgment may
 * appear in the software itself, if and wherever such third-party acknowledgments normally appear. 4.
 * The names "Apache" and "Apache Software Foundation" and "Apache Turbine" must not be used to
 * endorse or promote products derived from this software without prior written permission. For
 * written permission, please contact apache@apache.org. 5. Products derived from this software may
 * not be called "Apache", "Apache Turbine", nor may "Apache" appear in their name, without prior
 * written permission of the Apache Software Foundation.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR ITS CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 * 
 * This software consists of voluntary contributions made by many individuals on behalf of the
 * Apache Software Foundation. For more information on the Apache Software Foundation, please see
 * <http://www.apache.org/> .
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.security.entity.Group;
import org.apache.fulcrum.security.entity.Permission;
import org.apache.fulcrum.security.entity.Role;
import org.apache.fulcrum.security.entity.User;
import org.apache.fulcrum.security.model.turbine.TurbineModelManager;
import org.apache.fulcrum.security.spi.torque.turbine.TorqueUser;
import org.apache.fulcrum.security.spi.torque.turbine.peer.GroupPeer;
import org.apache.fulcrum.security.spi.torque.turbine.peer.PermissionPeer;
import org.apache.fulcrum.security.spi.torque.turbine.peer.RolePeer;
import org.apache.fulcrum.security.spi.torque.turbine.peer.RolePermissionPeer;
import org.apache.fulcrum.security.spi.torque.turbine.peer.UserGroupRolePeer;
import org.apache.fulcrum.security.util.DataBackendException;
import org.apache.fulcrum.security.util.UnknownEntityException;
import org.apache.torque.om.NumberKey;
import org.apache.torque.om.Persistent;
import org.apache.torque.util.Criteria;

/**
 * An UserManager performs {@link org.apache.turbine.om.security.User}objects related tasks on
 * behalf of the {@link org.apache.turbine.services.security.BaseSecurityService}.
 * 
 * This implementation uses a relational database for storing user data. It expects that the User
 * interface implementation will be castable to {@link org.apache.torque.om.BaseObject}.
 * 
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * 
 * @version $Id$
 */
public class TurbineModelManagerImpl extends TorqueManagerComponent implements TurbineModelManager
{
    /** Logging */
    private static Log log = LogFactory.getLog(TurbineModelManagerImpl.class);

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
            lockExclusive();
            roleExists = RolePeer.checkExists(role);
            permissionExists = PermissionPeer.checkExists(permission);
            if (roleExists && permissionExists)
            {
                Criteria criteria = new Criteria();
                criteria.add(RolePermissionPeer.ROLE_ID, ((Persistent) role).getPrimaryKey());
                criteria.add(
                    RolePermissionPeer.PERMISSION_ID,
                    ((Persistent) permission).getPrimaryKey());
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
            lockExclusive();
            roleExists = RolePeer.checkExists(role);
            permissionExists = PermissionPeer.checkExists(permission);
            if (roleExists && permissionExists)
            {
                Criteria criteria = new Criteria();
                criteria.add(RolePermissionPeer.ROLE_ID, ((Persistent) role).getPrimaryKey());
                criteria.add(
                    RolePermissionPeer.PERMISSION_ID,
                    ((Persistent) permission).getPrimaryKey());
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
	 * @param role the Role
	 * @throws DataBackendException if there was an error accessing the data backend.
	 * @throws UnknownEntityException if the Role is not present.
	 */
    public synchronized void revokeAll(Role role)
        throws DataBackendException, UnknownEntityException
    {
        boolean roleExists = false;
        try
        {
            lockExclusive();
            roleExists = RolePeer.checkExists(role);
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
	 * Grant an User a Role in a Group.
	 * 
	 * @param user the user.
	 * @param group the group.
	 * @param role the role.
	 * @throws DataBackendException if there was an error accessing the data backend.
	 * @throws UnknownEntityException if user account, group or role is not present.
	 */
    public synchronized void grant(User user, Group group, Role role)
        throws DataBackendException, UnknownEntityException
    {
        boolean userExists = false;
        boolean groupExists = false;
        boolean roleExists = false;
        try
        {
            lockExclusive();
            userExists = getUserManager().checkExists(user);
            groupExists = GroupPeer.checkExists(group);
            roleExists = RolePeer.checkExists(role);
            if (userExists && groupExists && roleExists)
            {
                Criteria criteria = new Criteria();
                criteria.add(UserGroupRolePeer.USER_ID, ((Persistent) user).getPrimaryKey());
                criteria.add(UserGroupRolePeer.GROUP_ID, ((Persistent) group).getPrimaryKey());
                criteria.add(UserGroupRolePeer.ROLE_ID, ((Persistent) role).getPrimaryKey());
                UserGroupRolePeer.doInsert(criteria);
                return;
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException("grant(User,Group,Role) failed", e);
        }
        finally
        {
            unlockExclusive();
        }
        if (!userExists)
        {
            throw new UnknownEntityException("Unknown user '" + user.getName() + "'");
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
	 * Revoke a Role in a Group from an User.
	 * 
	 * @param user the user.
	 * @param group the group.
	 * @param role the role.
	 * @throws DataBackendException if there was an error accessing the data backend.
	 * @throws UnknownEntityException if user account, group or role is not present.
	 */
    public synchronized void revoke(User user, Group group, Role role)
        throws DataBackendException, UnknownEntityException
    {
        boolean userExists = false;
        boolean groupExists = false;
        boolean roleExists = false;
        try
        {
            lockExclusive();
            userExists = getUserManager().checkExists(user);
            groupExists = GroupPeer.checkExists(group);
            roleExists = RolePeer.checkExists(role);
            if (userExists && groupExists && roleExists)
            {
                Criteria criteria = new Criteria();
                criteria.add(UserGroupRolePeer.USER_ID, ((Persistent) user).getPrimaryKey());
                criteria.add(UserGroupRolePeer.GROUP_ID, ((Persistent) group).getPrimaryKey());
                criteria.add(UserGroupRolePeer.ROLE_ID, ((Persistent) role).getPrimaryKey());
                UserGroupRolePeer.doDelete(criteria);
                return;
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException("revoke(User,Role,Group) failed", e);
        }
        finally
        {
            unlockExclusive();
        }
        if (!userExists)
        {
            throw new UnknownEntityException("Unknown user '" + user.getName() + "'");
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
	 * Revoke a all groups from a user
	 * 
	 * @param user the user.
	 * 
	 * @throws DataBackendException if there was an error accessing the data backend.
	 * @throws UnknownEntityException if user account, group or role is not present.
	 */
    public synchronized void revokeAll(User user)
        throws DataBackendException, UnknownEntityException
    {
        boolean userExists = false;

        try
        {
            lockExclusive();
            userExists = getUserManager().checkExists(user);
            if (!userExists)
            {
                throw new UnknownEntityException("Unknown user '" + user.getName() + "'");
            }
            // The following would not work, due to an annoying misfeature
            // of Village. see revokeAll( user )
            // Criteria criteria = new Criteria();
            // criteria.add(RolePermissionPeer.ROLE_ID,
            //         role.getPrimaryKey());
            // RolePermissionPeer.doDelete(criteria);
            int id = ((NumberKey) ((TorqueUser) user).getPrimaryKey()).intValue();
            // UPeer.deleteAll(
            //		 RolePermissionPeer.TABLE_NAME,
            //		 RolePermissionPeer.ROLE_ID,
            //		 id);
            return;

        }
        catch (Exception e)
        {
            throw new DataBackendException("revoke(User,Role,Group) failed", e);
        }
        finally
        {
            unlockExclusive();
        }

    }
}