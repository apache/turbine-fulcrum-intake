package org.apache.fulcrum.security.spi.memory.simple;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.security.entity.Group;
import org.apache.fulcrum.security.entity.Permission;
import org.apache.fulcrum.security.entity.Role;
import org.apache.fulcrum.security.entity.User;
import org.apache.fulcrum.security.model.simple.SimpleModelManager;
import org.apache.fulcrum.security.model.simple.entity.SimpleGroup;
import org.apache.fulcrum.security.model.simple.entity.SimpleRole;
import org.apache.fulcrum.security.model.simple.entity.SimpleUser;
import org.apache.fulcrum.security.util.DataBackendException;
import org.apache.fulcrum.security.util.GroupSet;
import org.apache.fulcrum.security.util.PermissionSet;
import org.apache.fulcrum.security.util.UnknownEntityException;

/**
 * This implementation keeps all objects in memory. This is mostly meant to help with testing and
 * prototyping of ideas.
 * 
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class MemoryModelManagerImpl
    extends BaseMemoryManager
    implements SimpleModelManager
{
    /** Logging */
    private static Log log = LogFactory.getLog(MemoryModelManagerImpl.class);
    private static List groups = new ArrayList();
    /** Our Unique ID counter */
    private static int uniqueId = 0;


    /**
	 * Puts a user in a group.
	 * 
	 * This method is used when adding a user to a group
	 * 
	 * @param user the User.
	 * @throws DataBackendException if there was an error accessing the data backend.
	 * @throws UnknownEntityException if the account is not present.
	 */
    public void grant(User user, Group group) throws DataBackendException, UnknownEntityException
    {
        boolean groupExists = false;
        boolean userExists = false;
        try
        {
            groupExists = getGroupManager().checkExists(group);
            userExists = getUserManager().checkExists(user);
            if (groupExists && userExists)
            {
                ((SimpleUser) user).addGroup(group);
                ((SimpleGroup) group).addUser(user);
                return;
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException("grant(Role,Permission) failed", e);
        }
        finally
        {
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
    public void revoke(User user, Group group) throws DataBackendException, UnknownEntityException
    {
        boolean groupExists = false;
        boolean userExists = false;
        try
        {
            groupExists = getGroupManager().checkExists(group);
            userExists = getUserManager().checkExists(user);
            if (groupExists && userExists)
            {
                ((SimpleUser) user).removeGroup(group);
                ((SimpleGroup) group).removeUser(user);
                return;
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException("grant(Role,Permission) failed", e);
        }
        finally
        {
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
                ((SimpleGroup) group).addRole(role);
                ((SimpleRole) role).addGroup(group);
                return;
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException("grant(Group,Role) failed", e);
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
                ((SimpleGroup) group).removeRole(role);
                ((SimpleRole) role).removeGroup(group);
                return;
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException("revoke(Group,Role) failed", e);
        }
        finally
        {
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
                ((SimpleRole) role).addPermission(permission);
                return;
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException("grant(Role,Permission) failed", e);
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
                ((SimpleRole) role).removePermission(permission);
                return;
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException("revoke(Role,Permission) failed", e);
        }
        finally
        {
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
	 * @throws DataBackendException if there was an error accessing the data backend.
	 * @throws UnknownEntityException if the Role is not present.
	 */
    public synchronized void revokeAll(Role role)
        throws DataBackendException, UnknownEntityException
    {
        boolean roleExists = false;
        try
        {
            roleExists = getRoleManager().checkExists(role);
            if (roleExists)
            {
                ((SimpleRole) role).setPermissions(new PermissionSet());
                return;
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException("revokeAll(Role) failed", e);
        }
        finally
        {
        }
        throw new UnknownEntityException("Unknown role '" + role.getName() + "'");
    }
    /**
	 * Revokes all groups from a user
	 * 
	 * This method is used when deleting an account.
	 * 
	 * @param user the User.
	 * @throws DataBackendException if there was an error accessing the data backend.
	 * @throws UnknownEntityException if the account is not present.
	 */
    public synchronized void revokeAll(User user)
        throws DataBackendException, UnknownEntityException
    {
        boolean userExists = false;
        try
        {
            userExists = getUserManager().checkExists(user);
            if (userExists)
            {
                for (Iterator i = ((SimpleUser) user).getGroups().iterator(); i.hasNext();)
                {
                    SimpleGroup group = (SimpleGroup) i.next();
                    group.removeUser(user);
                }
                ((SimpleUser) user).setGroups(new GroupSet());
                return;
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException("revokeAll(User) failed:" + e.getMessage(), e);
        }
        finally
        {
        }
        throw new UnknownEntityException("Unknown user '" + user.getName() + "'");
    }


}
