package org.apache.fulcrum.security.nt.dynamic;
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.security.SecurityService;
import org.apache.fulcrum.security.UserManager;
import org.apache.fulcrum.security.acl.AccessControlList;
import org.apache.fulcrum.security.entity.User;
import org.apache.fulcrum.security.model.basic.BasicAccessControlList;
import org.apache.fulcrum.security.model.basic.BasicModelManager;
import org.apache.fulcrum.security.util.UnknownEntityException;
import org.apache.fulcrum.testcontainer.BaseUnitTest;
/**
 * @author Eric Pugh
 * 
 * Test the NT implementation of the user manager. This test traps some exceptions that can be
 * thrown if there is NO nt dll.
 */
public class NTBasicACLTest extends BaseUnitTest
{
    private static Log log = LogFactory.getLog(NTBasicACLTest.class);
    private static final String ERROR_MSG = "Not supported by NT User Manager";
    private static final String USERNAME = "Eric Pugh";
    private static final String DOMAIN = "IQUITOS";
    private static final String PASSWORD = "";
	private static final String GUESTUSER = DOMAIN + "/" + "Guest";
	private static final String TESTUSER = DOMAIN + "/" + USERNAME;
    private BasicModelManager modelManager;
	private SecurityService securityService;
	private UserManager userManager;
	private User user;

    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(NTBasicACLTest.class);
    }
    public void setUp() throws Exception
    {
        this.setRoleFileName(null);
        this.setConfigurationFileName("src/test/BasicNT.xml");
        securityService = (SecurityService) lookup(SecurityService.ROLE);
        userManager = securityService.getUserManager();
        modelManager = (BasicModelManager) securityService.getModelManager();
    }
    public void tearDown()
    {
        user = null;
        userManager = null;
        securityService = null;
    }
    /**
     * Constructor for MemoryPermissionManagerTest.
     * 
     * @param arg0
     */
    public NTBasicACLTest(String arg0)
    {
        super(arg0);
    }
    public void testLoadingUpGroupsForBasicModelACL() throws Exception
    {
        try
        {
			user = userManager.getUser(GUESTUSER, "");
            user.setPassword("");
            AccessControlList acl = userManager.getACL(user);
            assertTrue(acl instanceof BasicAccessControlList);
            BasicAccessControlList bacl = (BasicAccessControlList)acl;
            assertEquals(4,bacl.getGroups().size());
			assertTrue(bacl.hasGroup("Guests"));
			assertTrue(bacl.hasGroup("gUEsts"));
            
        }
        catch (UnknownEntityException re)
        {
            assertTrue(re.getMessage().indexOf("Unknown user") > -1);
        }
    }

   
  
}
