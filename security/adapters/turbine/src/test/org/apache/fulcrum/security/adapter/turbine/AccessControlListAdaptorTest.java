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
import java.util.HashMap;
import java.util.Map;

import org.apache.fulcrum.security.BaseSecurityService;
import org.apache.fulcrum.security.entity.Group;
import org.apache.fulcrum.security.entity.Permission;
import org.apache.fulcrum.security.entity.Role;
import org.apache.fulcrum.security.model.dynamic.DynamicModelManager;
import org.apache.fulcrum.security.model.dynamic.entity.DynamicUser;
import org.apache.turbine.om.security.User;
import org.apache.turbine.services.security.TurbineSecurity;
import org.apache.turbine.util.security.AccessControlList;
import org.apache.turbine.util.security.GroupSet;
import org.apache.turbine.util.security.PermissionSet;
import org.apache.turbine.util.security.RoleSet;

import com.mockobjects.servlet.MockHttpSession;

/**
 * Test that we can load up a fulcrum ACL in Turbine, without Turbine
 * knowing that anything has changed.
 * 
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */

public class AccessControlListAdaptorTest
    extends AbstractAccessControlListAdaptorTest
{

    public String getTRProps(){
        return "AdapterTestTurbineResources.properties";
    }
   
    public AccessControlListAdaptorTest(String name) throws Exception
    {
        super(name);
    }

    public void testSwappingACL() throws Exception
    {

        Class aclClass = TurbineSecurity.getService().getAclClass();
        if (!aclClass
            .getName()
            .equals(AccessControlListAdapter.class.getName()))
        {
            fail(
                "ACL Class is "
                    + aclClass.getName()
                    + ", expected was "
                    + AccessControlListAdapter.class.getName());
        }
        Map roles = new HashMap();
        Map permissions = new HashMap();
        AccessControlList acl =
            TurbineSecurity.getService().getAclInstance(roles, permissions);
        if (acl == null)
        {
            fail(
                "Security Service failed to deliver a "
                    + aclClass.getName()
                    + " Object");
        }
        assertTrue(acl instanceof AccessControlList);
        assertTrue(acl instanceof AccessControlListAdapter);
    }
    public void testGettingUserFromRunData() throws Exception
    {

        session.setupGetAttribute(User.SESSION_KEY, null);
        User turbineUser = getUserFromRunData(session);
        assertNotNull(turbineUser);
        assertTrue(TurbineSecurity.getService().isAnonymousUser(turbineUser));
    }
    public void testUsingAvalonComponents() throws Exception
    {

        BaseSecurityService securityService =
            (BaseSecurityService) acs.lookup(BaseSecurityService.ROLE);
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
        DynamicModelManager modelManager =
            (DynamicModelManager) securityService.getModelManager();
        modelManager.grant(fulcrumRole, fulcrumPermission);
        modelManager.grant(fulcrumRole2, fulcrumPermission2);
        modelManager.grant(fulcrumRole2, fulcrumPermission3);
        modelManager.grant(fulcrumGroup, fulcrumRole);
        modelManager.grant(fulcrumGroup, fulcrumRole2);
        modelManager.grant(fulcrumGroup2, fulcrumRole2);
        org.apache.fulcrum.security.entity.User fulcrumUser =
            securityService.getUserManager().getUserInstance("Jeannie");
        fulcrumUser =
            securityService.getUserManager().addUser(fulcrumUser, "wyatt");
        modelManager.grant(fulcrumUser, fulcrumGroup);
        modelManager.grant(fulcrumUser, fulcrumGroup2);
        assertEquals(2, ((DynamicUser) fulcrumUser).getGroups().size());
        GroupSet groupSet = TurbineSecurity.getService().getAllGroups();
        assertEquals(2, groupSet.size());
        RoleSet roleSet = TurbineSecurity.getService().getAllRoles();
        assertEquals(2, roleSet.size());
        PermissionSet permissionSet =
            TurbineSecurity.getService().getAllPermissions();
        assertEquals(3, permissionSet.size());
        User turbineUser = TurbineSecurity.getService().getUser("Jeannie");
        AccessControlList acl =
            TurbineSecurity.getService().getACL(turbineUser);
        assertNotNull(acl);
        assertEquals(3, acl.getPermissions().size());
        MockHttpSession session = new MockHttpSession();
        session.setupGetAttribute(User.SESSION_KEY, turbineUser);
        turbineUser = getUserFromRunData(session);
        assertNotNull(turbineUser);
        assertFalse(TurbineSecurity.getService().isAnonymousUser(turbineUser));
    }

    public void testHasRole() throws Exception
    {

        BaseSecurityService securityService =
            (BaseSecurityService) acs.lookup(BaseSecurityService.ROLE);
        Group fulcrumGroup =
            securityService.getGroupManager().getGroupInstance("TEST_GROUP");
        Role fulcrumRole =
            securityService.getRoleManager().getRoleInstance("TEST_ROLE");
        org.apache.fulcrum.security.entity.User fulcrumUser =
            securityService.getUserManager().getUserInstance("Mike");

        securityService.getGroupManager().addGroup(fulcrumGroup);
        securityService.getRoleManager().addRole(fulcrumRole);
        fulcrumUser =
            securityService.getUserManager().addUser(fulcrumUser, "wyatt");

        DynamicModelManager modelManager =
            (DynamicModelManager) securityService.getModelManager();
        modelManager.grant(fulcrumGroup, fulcrumRole);
        modelManager.grant(fulcrumUser, fulcrumGroup);

        User turbineUser = TurbineSecurity.getService().getUser("Mike");

        MockHttpSession session = new MockHttpSession();
        session.setupGetAttribute(User.SESSION_KEY, turbineUser);
        turbineUser = getUserFromRunData(session);
        assertNotNull(turbineUser);
        assertFalse(TurbineSecurity.getService().isAnonymousUser(turbineUser));
        AccessControlList acl =
            TurbineSecurity.getService().getACL(turbineUser);
        assertTrue(acl.hasRole("TEST_ROLE"));

    }

}
