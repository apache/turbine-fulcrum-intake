package org.apache.fulcrum.security.model.dynamic.entity;
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

import java.util.Set;

import org.apache.fulcrum.security.entity.Permission;
import org.apache.fulcrum.security.entity.Role;
import org.apache.fulcrum.security.entity.Group;
import org.apache.fulcrum.security.entity.impl.SecurityEntityImpl;
import org.apache.fulcrum.security.util.GroupSet;
import org.apache.fulcrum.security.util.PermissionSet;

/**
 * Represents the "simple" model where permissions are related to roles,
 * roles are related to groups and groups are related to users,
 * all in many to many relationships.
 *
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class DynamicRole extends SecurityEntityImpl implements Role
{
    private Set permissionSet = new PermissionSet();

    private Set groupSet = new GroupSet();
    /**
     * @return
     */
    public PermissionSet getPermissions()
    {
    	if( permissionSet instanceof PermissionSet )
    		return (PermissionSet) permissionSet;
    	else {
    		permissionSet = new PermissionSet(permissionSet);
    		return (PermissionSet)permissionSet;
    	}
    }
    /**
     * @return
     */
    public Set getPermissionsAsSet()
    {
        return permissionSet;
    }

    public void setPermissionsAsSet(Set permissions)
    {
        this.permissionSet = permissions;;
    }
    /**
     * @param permissionSet
     */
    public void setPermissions(PermissionSet permissionSet)
    {
    	if( permissionSet != null )
    		this.permissionSet = permissionSet;
    	else
    		this.permissionSet = new PermissionSet();
    }

    /**
    * This method should only be used by a RoleManager.  Not directly.
    * @param permission
    */
    public void addPermission(Permission permission)
    {
        getPermissions().add(permission);
    }
    /**
     * This method should only be used by a RoleManager.  Not directly.
     * @param permission
     */
    public void removePermission(Permission permission)
    {
        getPermissions().remove(permission);
    }

    /**
    	* @return
    	*/
    public GroupSet getGroups()
    {
    	if( groupSet instanceof GroupSet )
    		return (GroupSet) groupSet;
    	else {
    		groupSet = new GroupSet(groupSet);
    		return (GroupSet)groupSet;
    	}
    }
    /**
    	* @param groupSet
    	*/
    public void setGroups(GroupSet groupSet)
    {
    	if( groupSet != null )
    		this.groupSet = groupSet;
    	else
    		this.groupSet = new GroupSet();
    }

    /**
    * This method should only be used by a RoleManager.  Not directly.
    * @param group
    */
    public void addGroup(Group group)
    {
        getGroups().add(group);
    }
    /**
	* This method should only be used by a RoleManager.  Not directly.
	* @param group
	*/
    public void removeGroup(Group group)
    {
        getGroups().remove(group);
    }

    public void setGroupsAsSet(Set groups)
    {
        this.groupSet = groups;
    }
    public Set getGroupsAsSet()
    {
        return groupSet;
    }
}
