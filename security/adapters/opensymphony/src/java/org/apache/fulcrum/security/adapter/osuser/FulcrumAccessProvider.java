package org.apache.fulcrum.security.adapter.osuser;
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
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.security.entity.Role;
import org.apache.fulcrum.security.entity.User;
import org.apache.fulcrum.security.model.dynamic.DynamicAccessControlList;
import org.apache.fulcrum.security.util.DataBackendException;
import org.apache.fulcrum.security.util.UnknownEntityException;

import com.opensymphony.user.Entity.Accessor;
import com.opensymphony.user.provider.AccessProvider;

/**
 * Fulcrum provider for OSUser.  Primarily provides support for requesting
 * whether a user exists in a role.  In OSUser, there are no roles, just groups,
 * so this maps Fulcrum roles on OSUser groups.  This means some the the method
 * names refer to groups, but interact with Fulcrum roles.
 * 
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class FulcrumAccessProvider
	extends BaseFulcrumProvider
	implements AccessProvider
{
	/** Logging */
	private static Log log = LogFactory.getLog(FulcrumAccessProvider.class);
	
	/*
	 * Not implemented.   Should use SecurityService directly.
	 * 
	 * @see com.opensymphony.user.provider.AccessProvider#addToGroup(java.lang.String,
	 *      java.lang.String)
	 */
	public boolean addToGroup(String username, String groupname)
	{
		return false;
	}

	/*
	 * Returns whether a user in part of a what OSUser calls a group. <strong>
	 * However, since Fulcrum Security has the concept of roles being
	 * assignable to groups, then what this method really checks is that the
	 * user has a specific role. </strong> This is because the mapping between
	 * OSUser and Fulcurm Security is not a 1 to 1 mapping.
	 * 
	 * @see com.opensymphony.user.provider.AccessProvider#inGroup(java.lang.String,
	 *      java.lang.String)
	 */
	public boolean inGroup(String username, String groupname)
	{
		try
		{
			User user = getSecurityService().getUserManager().getUser(username);
			DynamicAccessControlList acl =
			(DynamicAccessControlList)getSecurityService().getUserManager().getACL(user);
			Role role = acl.getRoles().getRoleByName(groupname);
			boolean result =acl.hasRole(role); 
			return result;
		}
		catch (UnknownEntityException uee)
		{
			return false;
		}
		catch (DataBackendException dbe)
		{
			throw new RuntimeException(dbe);
		}

	}

	/*
	 * This returns all the ROLES that a user has. This is similar to the
	 * problems with the inGroup() method of this provider.
	 * 
	 * @see com.opensymphony.user.provider.AccessProvider#listGroupsContainingUser(java.lang.String)
	 * @see org.apache.fulcrum.security.adapter.osuser.FulcrumAccessProvider#inGroup(java.lang.String,java.lang.String)
	 */
	public List listGroupsContainingUser(String username)
	{
		List roles = new ArrayList();
		try
		{
			User user = getSecurityService().getUserManager().getUser(username);
			DynamicAccessControlList acl =
				(DynamicAccessControlList)getSecurityService().getUserManager().getACL(user);
			roles.addAll(acl.getRoles().getNames());
			
		}
		catch (UnknownEntityException uee)
		{
			throw new RuntimeException(uee);
		}
		catch (DataBackendException dbe)
		{
			throw new RuntimeException(dbe);
		}
		return roles;

	}

	/*
	 * Not implemented yet.
	 * 
	 * @see com.opensymphony.user.provider.AccessProvider#listUsersInGroup(java.lang.String)
	 */
	public List listUsersInGroup(String groupname)
	{
		return null;
	}

	/*
	 * Not implemented.  Should probably use SecurityService directly.
	 * 
	 * @see com.opensymphony.user.provider.AccessProvider#removeFromGroup(java.lang.String,
	 *      java.lang.String)
	 */
	public boolean removeFromGroup(String username, String groupname)
	{
		return false;
	}

	/*
	 * Not implemented.  Should use SecurityService directly.
	 * 
	 * @see com.opensymphony.user.provider.UserProvider#create(java.lang.String)
	 */
	public boolean create(String name)
	{
		return false;
	}

	/*
	 * Doesn't do anything.
	 * 
	 * @see com.opensymphony.user.provider.UserProvider#flushCaches()
	 */
	public void flushCaches()
	{

	}

	/*
	 * Returns true if the user exists, otherwise returns false.
	 * 
	 * @see com.opensymphony.user.provider.UserProvider#handles(java.lang.String)
	 */
	public boolean handles(String name)
	{
		try
		{
			User user = getSecurityService().getUserManager().getUser(name);
			return true;
		}
		catch (UnknownEntityException uee)
		{
			return false;
		}
		catch (DataBackendException dbe)
		{
			throw new RuntimeException(dbe);
		}
	}



	/*
	 * not implemented.
	 * 
	 * @see com.opensymphony.user.provider.UserProvider#list()
	 */
	public List list()
	{
		return null;
	}



	/*
	 * Not implemented.   Should use SecurityService directly.
	 * 
	 * @see com.opensymphony.user.provider.UserProvider#remove(java.lang.String)
	 */
	public boolean remove(String name)
	{
		return false;
	}

	/*
	 * Not implemented.   Should use SecurityService directly.
	 * 
	 * @see com.opensymphony.user.provider.UserProvider#store(java.lang.String,
	 *      com.opensymphony.user.Entity.Accessor)
	 */
	public boolean store(String arg0, Accessor arg1)
	{	
		return false;
	}

}
