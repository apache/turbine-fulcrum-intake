package org.apache.fulcrum.security.adapter.turbine;
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.security.model.basic.BasicAccessControlList;
import org.apache.fulcrum.security.model.dynamic.DynamicAccessControlList;
import org.apache.turbine.om.security.Group;
import org.apache.turbine.om.security.Permission;
import org.apache.turbine.om.security.Role;
import org.apache.turbine.util.security.AccessControlList;
import org.apache.turbine.util.security.GroupSet;
import org.apache.turbine.util.security.PermissionSet;
import org.apache.turbine.util.security.RoleSet;
/**
 * This class adaptes the Turbine AccessControlList to the Fulcrum
 * Security service AccessControlList.  All calls back and forth are
 * proxied through this class.
 *
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class AccessControlListAdapter implements AccessControlList
{
    private static Log log = LogFactory.getLog(AccessControlListAdapter.class);
    private org.apache.fulcrum.security.acl.AccessControlList acl;
    /**
     *
     */
    public AccessControlListAdapter()
    {
        super();
    }
    public AccessControlListAdapter(org.apache.fulcrum.security.acl.AccessControlList acl)
    {
        super();
        this.acl = acl;
    }
    public AccessControlListAdapter(Map rolesMap, Map permissionsMap)
    {
        log.info("AccessControlListAdapter is eating call to constructor(Map,Map).");
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.util.security.AccessControlList#getRoles(org.apache.turbine.om.security.Group)
     */
    public RoleSet getRoles(Group arg0)
    {
		throw new RuntimeException("Unsupported operation");
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.util.security.AccessControlList#getRoles()
     */
    public RoleSet getRoles()
    {
		throw new RuntimeException("Unsupported operation");
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.util.security.AccessControlList#getPermissions(org.apache.turbine.om.security.Group)
     */
    public PermissionSet getPermissions(Group arg0)
    {
		throw new RuntimeException("Unsupported operation");
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.util.security.AccessControlList#getPermissions()
     */
    public PermissionSet getPermissions()
    {
        if (!(acl instanceof DynamicAccessControlList)){
			throw new RuntimeException("ACL doesn't support this opperation");
        }
        PermissionSet turbinePS = new PermissionSet();
        org.apache.fulcrum.security.util.PermissionSet fulcrumPS = ((DynamicAccessControlList)acl).getPermissions();
        for (Iterator i = fulcrumPS.iterator(); i.hasNext();)
        {
            org.apache.fulcrum.security.entity.Permission fulcrumPermission =
                (org.apache.fulcrum.security.entity.Permission) i.next();
            Permission turbinePermission = new PermissionAdapter(fulcrumPermission);
            turbinePS.add(turbinePermission);
        }
        return turbinePS;
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.util.security.AccessControlList#hasRole(org.apache.turbine.om.security.Role, org.apache.turbine.om.security.Group)
     */
    public boolean hasRole(Role arg0, Group arg1)
    {
		throw new RuntimeException("Unsupported operation");
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.util.security.AccessControlList#hasRole(org.apache.turbine.om.security.Role, org.apache.turbine.util.security.GroupSet)
     */
    public boolean hasRole(Role arg0, GroupSet arg1)
    {
		throw new RuntimeException("Unsupported operation");
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.util.security.AccessControlList#hasRole(java.lang.String, java.lang.String)
     */
    public boolean hasRole(String arg0, String arg1)
    {
		throw new RuntimeException("Unsupported operation");
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.util.security.AccessControlList#hasRole(java.lang.String, org.apache.turbine.util.security.GroupSet)
     */
    public boolean hasRole(String arg0, GroupSet arg1)
    {
		throw new RuntimeException("Unsupported operation");
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.util.security.AccessControlList#hasRole(org.apache.turbine.om.security.Role)
     */
    public boolean hasRole(Role arg0)
    {
		throw new RuntimeException("Unsupported operation");
    }
    /* For a DynamicACL, checks the role.  But, for a BasicACL, it maps
     * roles onto BasicGroup's.
     * @see org.apache.turbine.util.security.AccessControlList#hasRole(java.lang.String)
     */
    public boolean hasRole(String roleName)
    {
		if (acl instanceof DynamicAccessControlList){
			return ((DynamicAccessControlList)acl).hasRole(roleName);
		}
		else if (acl instanceof BasicAccessControlList){
		    return ((BasicAccessControlList)acl).hasGroup(roleName);
		}
		throw new RuntimeException("Unsupported operation");
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.util.security.AccessControlList#hasPermission(org.apache.turbine.om.security.Permission, org.apache.turbine.om.security.Group)
     */
    public boolean hasPermission(Permission arg0, Group arg1)
    {
		throw new RuntimeException("Unsupported operation");
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.util.security.AccessControlList#hasPermission(org.apache.turbine.om.security.Permission, org.apache.turbine.util.security.GroupSet)
     */
    public boolean hasPermission(Permission arg0, GroupSet arg1)
    {
		throw new RuntimeException("Unsupported operation");
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.util.security.AccessControlList#hasPermission(java.lang.String, java.lang.String)
     */
    public boolean hasPermission(String arg0, String arg1)
    {
		throw new RuntimeException("Unsupported operation");
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.util.security.AccessControlList#hasPermission(java.lang.String, org.apache.turbine.om.security.Group)
     */
    public boolean hasPermission(String arg0, Group arg1)
    {
		throw new RuntimeException("Unsupported operation");
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.util.security.AccessControlList#hasPermission(java.lang.String, org.apache.turbine.util.security.GroupSet)
     */
    public boolean hasPermission(String arg0, GroupSet arg1)
    {
		throw new RuntimeException("Unsupported operation");
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.util.security.AccessControlList#hasPermission(org.apache.turbine.om.security.Permission)
     */
    public boolean hasPermission(Permission arg0)
    {
		throw new RuntimeException("Unsupported operation");
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.util.security.AccessControlList#hasPermission(java.lang.String)
     */
    public boolean hasPermission(String arg0)
    {
		throw new RuntimeException("Unsupported operation");
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.util.security.AccessControlList#getAllGroups()
     */
    public Group[] getAllGroups()
    {
		throw new RuntimeException("Unsupported operation");
    }
}
