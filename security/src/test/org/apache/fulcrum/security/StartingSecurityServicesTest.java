package org.apache.fulcrum.security;
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
import org.apache.fulcrum.security.spi.hibernate.simple.HibernateGroupManagerImpl;
import org.apache.fulcrum.security.spi.hibernate.simple.HibernatePermissionManagerImpl;
import org.apache.fulcrum.security.spi.hibernate.simple.HibernateRoleManagerImpl;
import org.apache.fulcrum.security.spi.hibernate.simple.HibernateUserManagerImpl;
import org.apache.fulcrum.security.spi.memory.simple.MemoryGroupManagerImpl;
import org.apache.fulcrum.security.spi.memory.simple.MemoryPermissionManagerImpl;
import org.apache.fulcrum.security.spi.memory.simple.MemoryRoleManagerImpl;
import org.apache.fulcrum.security.spi.memory.simple.MemoryUserManagerImpl;
import org.apache.fulcrum.security.spi.torque.turbine.TorqueGroupManager;
import org.apache.fulcrum.security.spi.torque.turbine.TorquePermissionManager;
import org.apache.fulcrum.security.spi.torque.turbine.TorqueRoleManager;
import org.apache.fulcrum.security.spi.torque.turbine.TorqueUserManager;
import org.apache.fulcrum.testcontainer.BaseUnitTest;
public class StartingSecurityServicesTest extends BaseUnitTest
{
    private SecurityService securityService = null;
    public StartingSecurityServicesTest(String name)
    {
        super(name);
    }
    public void setUp()
    {
        super.setUp();
        //        this.release(sc);
    }
    public void testStartingTorqueSecurity() throws Exception
    {
        this.setRoleFileName(null);
        this.setConfigurationFileName("src/test/TorqueSecurity.xml");
        securityService = (SecurityService) lookup(SecurityService.ROLE);
        assertNotNull(securityService.getUserManager());
        assertNotNull(securityService.getRoleManager());
        assertNotNull(securityService.getPermissionManager());
        assertNotNull(securityService.getGroupManager());
        assertTrue(securityService.getUserManager() instanceof TorqueUserManager);
        assertTrue(securityService.getRoleManager() instanceof TorqueRoleManager);
        assertTrue(securityService.getPermissionManager() instanceof TorquePermissionManager);
        assertTrue(securityService.getGroupManager() instanceof TorqueGroupManager);
    }
    public void testStartingInMemorySecurity() throws Exception
    {
        this.setRoleFileName(null);
        this.setConfigurationFileName("src/test/InMemorySecurity.xml");
        securityService = (SecurityService) lookup(SecurityService.ROLE);
        assertNotNull(securityService.getUserManager());
        assertNotNull(securityService.getRoleManager());
        assertNotNull(securityService.getPermissionManager());
        assertNotNull(securityService.getGroupManager());
        assertTrue(securityService.getUserManager() instanceof MemoryUserManagerImpl);
        assertTrue(securityService.getRoleManager() instanceof MemoryRoleManagerImpl);
        assertTrue(securityService.getPermissionManager() instanceof MemoryPermissionManagerImpl);
        assertTrue(securityService.getGroupManager() instanceof MemoryGroupManagerImpl);
    }
    public void testStartingHibernateSecurity() throws Exception
    {
        this.setRoleFileName(null);
        this.setConfigurationFileName("src/test/SimpleHibernate.xml");
        securityService = (SecurityService) lookup(SecurityService.ROLE);
        assertNotNull(securityService.getUserManager());
        assertNotNull(securityService.getRoleManager());
        assertNotNull(securityService.getPermissionManager());
        assertNotNull(securityService.getGroupManager());
        assertTrue(securityService.getUserManager() instanceof HibernateUserManagerImpl);
        assertTrue(securityService.getRoleManager() instanceof HibernateRoleManagerImpl);
        assertTrue(securityService.getPermissionManager() instanceof HibernatePermissionManagerImpl);
        assertTrue(securityService.getGroupManager() instanceof HibernateGroupManagerImpl);
    }
}
