package org.apache.fulcrum.security.model.dynamic;
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
import java.util.Iterator;
import java.util.Map;

import org.apache.fulcrum.security.entity.Group;
import org.apache.fulcrum.security.entity.Permission;
import org.apache.fulcrum.security.entity.Role;
import org.apache.fulcrum.security.util.GroupSet;
import org.apache.fulcrum.security.util.PermissionSet;
import org.apache.fulcrum.security.util.RoleSet;
/**
 * This is a control class that makes it easy to find out if a
 * particular User has a given Permission.  It also determines if a
 * User has a a particular Role.
 *
 * @todo Need to rethink the two maps..  Why not just a single list of groups?  That would
 * then cascade down to all the other roles and so on..
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class DynamicAccessControlListImpl implements DynamicAccessControlList
{
    /** The sets of roles that the user has in different groups */
    private Map roleSets;
    /** The sets of permissions that the user has in different groups */
    private Map permissionSets;
    /** The distinct list of groups that this user is part of */
    private GroupSet groupSet = new GroupSet();
    /** The distinct list of roles that this user is part of */
    private RoleSet roleSet = new RoleSet();
    /** the distinct list of permissions that this user has */
    private PermissionSet permissionSet = new PermissionSet();
    /**
     * Constructs a new AccessControlList.
     *
     * This class follows 'immutable' pattern - it's objects can't be modified
     * once they are created. This means that the permissions the users have are
     * in effect form the moment they log in to the moment they log out, and
     * changes made to the security settings in that time are not reflected
     * in the state of this object. If you need to reset an user's permissions
     * you need to invalidate his session. <br>
     * The objects that constructs an AccessControlList must supply hashtables
     * of role/permission sets keyed with group objects. <br>
     *
     * @param roleSets a hashtable containing RoleSet objects keyed with Group objects
     * @param permissionSets a hashtable containing PermissionSet objects keyed with Roles objects
     */
    public DynamicAccessControlListImpl(Map roleSets, Map permissionSets)
    {
        this.roleSets = roleSets;
        this.permissionSets = permissionSets;
        for (Iterator i = roleSets.keySet().iterator(); i.hasNext();)
        {
            Group group = (Group) i.next();
            groupSet.add(group);
            RoleSet rs = (RoleSet) roleSets.get(group);
            roleSet.add(rs);
        }
        for (Iterator i = permissionSets.keySet().iterator(); i.hasNext();)
        {
            Role role = (Role) i.next();
            roleSet.add(role);
            PermissionSet ps = (PermissionSet) permissionSets.get(role);
            permissionSet.add(ps);
        }
    }
    /**
     * Retrieves a set of Roles an user is assigned in a Group.
     *
     * @param group the Group
     * @return the set of Roles this user has within the Group.
     */
    public RoleSet getRoles(Group group)
    {
        if (group == null)
        {
            return null;
        }
        return (RoleSet) roleSets.get(group);
    }
    /**
     * Retrieves a set of Roles an user is assigned in the global Group.
     *
     * @return the set of Roles this user has within the global Group.
     */
    public RoleSet getRoles()
    {
        return roleSet;
    }
    /**
     * Retrieves a set of Permissions an user is assigned in a Group.
     *
     * @param group the Group
     * @return the set of Permissions this user has within the Group.
     */
    public PermissionSet getPermissions(Group group)
    {
        PermissionSet permissionSet = new PermissionSet();
        if (roleSets.containsKey(group))
        {
            RoleSet rs = (RoleSet) roleSets.get(group);
            for (Iterator i = rs.iterator(); i.hasNext();)
            {
                Role role = (Role) i.next();
                if (permissionSets.containsKey(role))
                {
                    permissionSet.add((PermissionSet) permissionSets.get(role));
                }
            }
        }
        return permissionSet;
    }
    /**
     * Retrieves a set of Permissions an user is assigned in the global Group.
     *
     * @return the set of Permissions this user has within the global Group.
     */
    public PermissionSet getPermissions() 
    {
        return permissionSet;
    }
    /**
     * Checks if the user is assigned a specific Role in the Group.
     *
     * @param role the Role
     * @param group the Group
     * @return <code>true</code> if the user is assigned the Role in the Group.
     */
    public boolean hasRole(Role role, Group group)
    {
        RoleSet set = getRoles(group);
        if (set == null || role == null)
        {
            return false;
        }
        return set.contains(role);
    }
    /**
     * Checks if the user is assigned a specific Role in any of the given
     * Groups
     *
     * @param role the Role
     * @param groupset a Groupset
     * @return <code>true</code> if the user is assigned the Role in any of
     *         the given Groups.
     */
    public boolean hasRole(Role role, GroupSet groupset)
    {
        if (role == null)
        {
            return false;
        }
        for (Iterator groups = groupset.iterator(); groups.hasNext();)
        {
            Group group = (Group) groups.next();
            RoleSet roles = getRoles(group);
            if (roles != null)
            {
                if (roles.contains(role))
                {
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * Checks if the user is assigned a specific Role in the Group.
     *
     * @param role the Role
     * @param group the Group
     * @return <code>true</code> if the user is assigned the Role in the Group.
     */
    public boolean hasRole(String role, String group)
    {
        boolean roleFound = false;
        try
        {
            for (Iterator i = roleSets.keySet().iterator(); i.hasNext();)
            {
                Group g = (Group) i.next();
                if (g.getName().equalsIgnoreCase(group))
                {
                    RoleSet rs = (RoleSet) roleSets.get(g);
                    roleFound = rs.containsName(role);
                }
            }
        }
        catch (Exception e)
        {
            roleFound = false;
        }
        return roleFound;
    }
    /**
     * Checks if the user is assigned a specifie Role in any of the given
     * Groups
     *
     * @param rolename the name of the Role
     * @param groupset a Groupset
     * @return <code>true</code> if the user is assigned the Role in any of
     *         the given Groups.
     */
    public boolean hasRole(String rolename, GroupSet groupset)
    {
        Role role;
        try
        {
            role = roleSet.getRoleByName(rolename);
        }
        catch (Exception e)
        {
            return false;
        }
        if (role == null)
        {
            return false;
        }
        for (Iterator groups = groupset.iterator(); groups.hasNext();)
        {
            Group group = (Group) groups.next();
            RoleSet roles = getRoles(group);
            if (roles != null)
            {
                if (roles.contains(role))
                {
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * Checks if the user is assigned a specific Role
     *
     * @param role the Role
     * @return <code>true</code> if the user is assigned the Role in the global Group.
     */
    public boolean hasRole(Role role)
    {
        return roleSet.contains(role);
    }
    /**
     * Checks if the user is assigned a specific Role .
     *
     * @param role the Role
     * @return <code>true</code> if the user is assigned the Role .
     */
    public boolean hasRole(String role)
    {
        try
        {
            return roleSet.containsName(role);
        }
        catch (Exception e)
        {
            return false;
        }
    }
    /**
     * Checks if the user is assigned a specific Permission in the Group.
     *
     * @param permission the Permission
     * @param group the Group
     * @return <code>true</code> if the user is assigned the Permission in the Group.
     */
    public boolean hasPermission(Permission permission, Group group)
    {
        PermissionSet set = getPermissions(group);
        if (set == null || permission == null)
        {
            return false;
        }
        return set.contains(permission);
    }
    /**
     * Checks if the user is assigned a specific Permission in any of the given
     * Groups
     *
     * @param permission the Permission
     * @param groupset a Groupset
     * @return <code>true</code> if the user is assigned the Permission in any
     *         of the given Groups.
     */
    public boolean hasPermission(Permission permission, GroupSet groupset)
    {
        if (permission == null)
        {
            return false;
        }
        for (Iterator groups = groupset.iterator(); groups.hasNext();)
        {
            Group group = (Group) groups.next();
            PermissionSet permissions = getPermissions(group);
            if (permissions != null)
            {
                if (permissions.contains(permission))
                {
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * Checks if the user is assigned a specific Permission in the Group.
     *
     * @param permission the Permission
     * @param group the Group
     * @return <code>true</code> if the user is assigned the Permission in the Group.
     */
    public boolean hasPermission(String permission, String group)
    {
        try
        {
            return hasPermission(permissionSet.getPermissionByName(permission), groupSet.getGroupByName(group));
        }
        catch (Exception e)
        {
            return false;
        }
    }
    /**
     * Checks if the user is assigned a specific Permission in the Group.
     *
     * @param permission the Permission
     * @param group the Group
     * @return <code>true</code> if the user is assigned the Permission in the Group.
     */
    public boolean hasPermission(String permission, Group group)
    {
        try
        {
            return hasPermission(permissionSet.getPermissionByName(permission), group);
        }
        catch (Exception e)
        {
            return false;
        }
    }
    /**
     * Checks if the user is assigned a specifie Permission in any of the given
     * Groups
     *
     * @param permissionName the name of the Permission
     * @param groupset a Groupset
     * @return <code>true</code> if the user is assigned the Permission in any
     *         of the given Groups.
     */
    public boolean hasPermission(String permissionName, GroupSet groupset)
    {
        Permission permission;
        try
        {
            permission = permissionSet.getPermissionByName(permissionName);
        }
        catch (Exception e)
        {
            return false;
        }
        if (permission == null)
        {
            return false;
        }
        for (Iterator groups = groupset.iterator(); groups.hasNext();)
        {
            Group group = (Group) groups.next();
            PermissionSet permissions = getPermissions(group);
            if (permissions != null)
            {
                if (permissions.contains(permission))
                {
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * Checks if the user is assigned a specific Permission.
     *
     * @param permission the Permission
     * @return <code>true</code> if the user is assigned the Permission .
     */
    public boolean hasPermission(Permission permission)
    {
        return permissionSet.contains(permission);
    }
    /**
     * Checks if the user is assigned a specific Permission in the global Group.
     *
     * @param permission the Permission
     * @return <code>true</code> if the user is assigned the Permission in the global Group.
     */
    public boolean hasPermission(String permission)
    {
        try
        {
            return permissionSet.containsName(permission);
        }
        catch (Exception e)
        {
            return false;
        }
    }
}
