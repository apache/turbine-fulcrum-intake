package org.apache.fulcrum.security.model;
/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2002 The Apache Software Foundation.  All rights
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

import org.apache.fulcrum.security.acl.AccessControlList;
import org.apache.fulcrum.security.model.basic.BasicAccessControlList;
import org.apache.fulcrum.security.model.basic.entity.BasicGroup;
import org.apache.fulcrum.security.model.basic.entity.BasicUser;
import org.apache.fulcrum.security.model.dynamic.DynamicAccessControlList;
import org.apache.fulcrum.security.model.dynamic.entity.DynamicGroup;
import org.apache.fulcrum.security.model.dynamic.entity.DynamicPermission;
import org.apache.fulcrum.security.model.dynamic.entity.DynamicRole;
import org.apache.fulcrum.security.model.dynamic.entity.DynamicUser;
import org.apache.fulcrum.testcontainer.BaseUnitTest;

/**
 *
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class ACLFactoryTest extends BaseUnitTest
{

    public ACLFactoryTest(String arg0)
    {
        super(arg0);
    }

    public void setUp() throws Exception
    {
        super.setUp();

    }

    public void testCreatingDynamicACL() throws Exception
    {
        this.setRoleFileName(null);
        this.setConfigurationFileName("src/test/DynamicACL.xml");

        ACLFactory factory = (ACLFactory) lookup(ACLFactory.ROLE);
        DynamicUser user = new DynamicUser();
        user.setName("bob");
        user.setId(new Integer(1));
        DynamicGroup group = new DynamicGroup();
        group.setName("group1");
        group.setId(new Integer(1));
        DynamicRole role = new DynamicRole();
        role.setName("role1");
        role.setId(new Integer(1));
        DynamicPermission permission = new DynamicPermission();
        permission.setName("permission1");
        permission.setId(new Integer(1));
        role.addPermission(permission);
        group.addRole(role);
        user.addGroup(group);
        AccessControlList acl = factory.getAccessControlList(user);
        assertTrue(acl instanceof DynamicAccessControlList);
        DynamicAccessControlList dacl = (DynamicAccessControlList) acl;
        assertTrue(dacl.hasPermission(permission));

    }

    public void testCreatingBasicACL() throws Exception
    {
        this.setRoleFileName(null);
        this.setConfigurationFileName("src/test/BasicACL.xml");

        ACLFactory factory = (ACLFactory) lookup(ACLFactory.ROLE);
        BasicUser user = new BasicUser();
        user.setName("bob");
        user.setId(new Integer(1));
        BasicGroup group = new BasicGroup();
        group.setName("group1");
        group.setId(new Integer(1));
        user.addGroup(group);
        AccessControlList acl = factory.getAccessControlList(user);
        assertTrue(acl instanceof BasicAccessControlList);
		BasicAccessControlList bacl = (BasicAccessControlList) acl;
        assertTrue(bacl.hasGroup(group));

    }

}
