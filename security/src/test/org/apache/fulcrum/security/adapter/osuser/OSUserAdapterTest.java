package org.apache.fulcrum.security.adapter.osuser;
/*
 * ==================================================================== The
 * Apache Software License, Version 1.1
 * 
 * Copyright (c) 2001-2003 The Apache Software Foundation. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 1.
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. The end-user documentation
 * included with the redistribution, if any, must include the following
 * acknowledgment: "This product includes software developed by the Apache
 * Software Foundation (http://www.apache.org/)." Alternately, this
 * acknowledgment may appear in the software itself, if and wherever such
 * third-party acknowledgments normally appear. 4. The names "Apache" and
 * "Apache Software Foundation" and "Apache Turbine" must not be used to
 * endorse or promote products derived from this software without prior written
 * permission. For written permission, please contact apache@apache.org. 5.
 * Products derived from this software may not be called "Apache", "Apache
 * Turbine", nor may "Apache" appear in their name, without prior written
 * permission of the Apache Software Foundation.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * APACHE SOFTWARE FOUNDATION OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 * 
 * This software consists of voluntary contributions made by many individuals
 * on behalf of the Apache Software Foundation. For more information on the
 * Apache Software Foundation, please see <http://www.apache.org/> .
 */
import java.util.Collection;
import java.util.List;

import org.apache.fulcrum.security.SecurityService;
import org.apache.fulcrum.security.entity.Group;
import org.apache.fulcrum.security.entity.Permission;
import org.apache.fulcrum.security.entity.Role;
import org.apache.fulcrum.security.model.simple.entity.SimpleUser;
import org.apache.fulcrum.security.model.simple.manager.SimpleGroupManager;
import org.apache.fulcrum.security.model.simple.manager.SimpleRoleManager;
import org.apache.fulcrum.security.model.simple.manager.SimpleUserManager;
import org.apache.fulcrum.testcontainer.BaseUnitTest;

import com.opensymphony.user.UserManager;
import com.opensymphony.user.provider.AccessProvider;

/**
 * Test that we can load up OSUser backed by Fulcrum Security. The fulcrum
 * Security service is just running in memory.
 * 
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id: AccessControlListAdaptorTest.java,v 1.3 2003/10/20 17:40:12
 *          epugh Exp $
 */

public class OSUserAdapterTest extends BaseUnitTest
{

	protected UserManager osUserManager;
	protected org.apache.fulcrum.security.UserManager fulcrumUserManager;
	protected SecurityService securityService;
	public OSUserAdapterTest(String name) throws Exception
	{
		super(name);
	}
	public void setUp()
	{
		try
		{
			/*
			 * this.setRoleFileName(null);
			 * this.setConfigurationFileName("src/test/SimpleMemory.xml");
			 * securityService = (SecurityService)
			 * lookup(SecurityService.ROLE); fulcrumUserManager =
			 * securityService.getUserManager();
			 * 
			 * osUserManager = new UserManager("osuser.xml");
			 */
		}
		catch (Exception e)
		{
			fail(e.toString());
		}
	}
	public void tearDown()
	{

		osUserManager = null;
		fulcrumUserManager = null;
		securityService = null;
	}
	public void testUsingAvalonComponents() throws Exception
	{

		this.setRoleFileName(null);
		this.setConfigurationFileName("src/test/SimpleMemory.xml");
		securityService = (SecurityService) lookup(SecurityService.ROLE);
		fulcrumUserManager = securityService.getUserManager();

		osUserManager = new UserManager("osuser.xml");

		Group fulcrumGroup =
			securityService.getGroupManager().getGroupInstance(
				"TEST_REVOKEALL");
		securityService.getGroupManager().addGroup(fulcrumGroup);
		Group fulcrumGroup2 =
			securityService.getGroupManager().getGroupInstance(
				"TEST_REVOKEALL2");
		securityService.getGroupManager().addGroup(fulcrumGroup2);
		Role fulcrumRole =
			securityService.getRoleManager().getRoleInstance("role1");
		Role fulcrumRole2 =
			securityService.getRoleManager().getRoleInstance("role2");
		securityService.getRoleManager().addRole(fulcrumRole);
		securityService.getRoleManager().addRole(fulcrumRole2);
		Permission fulcrumPermission =
			securityService.getPermissionManager().getPermissionInstance(
				"perm1");
		Permission fulcrumPermission2 =
			securityService.getPermissionManager().getPermissionInstance(
				"perm2");
		Permission fulcrumPermission3 =
			securityService.getPermissionManager().getPermissionInstance(
				"perm3");
		securityService.getPermissionManager().addPermission(fulcrumPermission);
		securityService.getPermissionManager().addPermission(
			fulcrumPermission2);
		securityService.getPermissionManager().addPermission(
			fulcrumPermission3);
		((SimpleRoleManager) securityService.getRoleManager()).grant(
			fulcrumRole,
			fulcrumPermission);
		((SimpleRoleManager) securityService.getRoleManager()).grant(
			fulcrumRole2,
			fulcrumPermission2);
		((SimpleRoleManager) securityService.getRoleManager()).grant(
			fulcrumRole2,
			fulcrumPermission3);
		((SimpleGroupManager) securityService.getGroupManager()).grant(
			fulcrumGroup,
			fulcrumRole);
		((SimpleGroupManager) securityService.getGroupManager()).grant(
			fulcrumGroup,
			fulcrumRole2);
		((SimpleGroupManager) securityService.getGroupManager()).grant(
			fulcrumGroup2,
			fulcrumRole2);
		org.apache.fulcrum.security.entity.User fulcrumUser =
			securityService.getUserManager().getUserInstance("Jeannie");
		securityService.getUserManager().addUser(fulcrumUser, "wyatt");
		((SimpleUserManager) securityService.getUserManager()).grant(
			fulcrumUser,
			fulcrumGroup);
		((SimpleUserManager) securityService.getUserManager()).grant(
			fulcrumUser,
			fulcrumGroup2);
		assertEquals(2, ((SimpleUser) fulcrumUser).getGroups().size());
		
		Collection accessProviders = osUserManager.getAccessProviders();
		assertEquals(1,accessProviders.size());
		AccessProvider accessProvider=(AccessProvider)accessProviders.toArray()[0];
		assertTrue(accessProvider.handles("Jeannie"));
		
		/*
		 * GroupSet groupSet = TurbineSecurity.getService().getAllGroups();
		 * assertEquals(2, groupSet.size()); RoleSet roleSet =
		 * TurbineSecurity.getService().getAllRoles(); assertEquals(2,
		 * roleSet.size()); PermissionSet permissionSet =
		 * TurbineSecurity.getService().getAllPermissions(); assertEquals(3,
		 * permissionSet.size()); User turbineUser =
		 * TurbineSecurity.getService().getUser("Jeannie"); AccessControlList
		 * acl = TurbineSecurity.getService().getACL(turbineUser);
		 * assertNotNull(acl); assertEquals(3, acl.getPermissions().size());
		 * MockHttpSession session = new MockHttpSession();
		 * session.setupGetAttribute(User.SESSION_KEY, turbineUser);
		 */

	}

}
