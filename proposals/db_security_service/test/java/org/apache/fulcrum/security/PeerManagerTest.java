package org.apache.fulcrum.security;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;

import org.apache.fulcrum.ServiceManager;
import org.apache.fulcrum.TurbineServices;

import org.apache.fulcrum.factory.FactoryService; 
import org.apache.fulcrum.factory.TurbineFactoryService; 

import org.apache.fulcrum.security.SecurityService;

import org.apache.fulcrum.security.entity.Group;
import org.apache.fulcrum.security.entity.Permission;
import org.apache.fulcrum.security.entity.Role;
import org.apache.fulcrum.security.entity.User;
import org.apache.fulcrum.security.impl.db.DBSecurityService;
import org.apache.fulcrum.security.impl.db.GroupPeerManager;
import org.apache.fulcrum.security.impl.db.PermissionPeerManager;
import org.apache.fulcrum.security.impl.db.RolePeerManager;
import org.apache.fulcrum.security.impl.db.UserPeerManager;
import org.apache.fulcrum.security.impl.db.entity.TurbineGroup;
import org.apache.fulcrum.security.impl.db.entity.TurbinePermission;
import org.apache.fulcrum.security.impl.db.entity.TurbineRole;
import org.apache.fulcrum.security.impl.db.entity.TurbineUser;

import org.apache.torque.util.Criteria;
import org.apache.torque.om.Persistent;


public class PeerManagerTest
    extends TestCase
{
    private static final String PREFIX = "services." +
        SecurityService.SERVICE_NAME + '.';

    public PeerManagerTest( String name )
    {
        super(name);

        ServiceManager serviceManager = TurbineServices.getInstance();
        serviceManager.setApplicationRoot(".");

        Configuration cfg = new BaseConfiguration();

        cfg.setProperty(PREFIX + "classname", 
                        DBSecurityService.class.getName());

        // We must run init! 
        cfg.setProperty(PREFIX+"earlyInit", "true");

        /* Ugh */
        
        cfg.setProperty("services." + FactoryService.SERVICE_NAME + ".classname",
                        TurbineFactoryService.class.getName());

        serviceManager.setConfiguration(cfg);
      
        try
        {
            serviceManager.init();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public static Test suite()
    {
        return new TestSuite(PeerManagerTest.class);
    }
                    
    public void testUserPeerManager()
    {
        try 
        {
            Persistent p = new TurbineUser();
            
            User u = UserPeerManager.getNewUser(p);
            String userClassName = u.getClass().getName();
            
            assertEquals("Didn't get a DBUser object from Peer: " + userClassName, 
                         userClassName, 
                         "org.apache.fulcrum.security.impl.db.DBUser");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testGroupPeerManager()
    {
        try 
        {
            Persistent p = new TurbineGroup();
            
            Group u = GroupPeerManager.getNewGroup(p);
            String groupClassName = u.getClass().getName();
            
            assertEquals("Didn't get a DBGroup object from Peer: " + groupClassName, 
                         groupClassName, 
                         "org.apache.fulcrum.security.impl.db.DBGroup");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testRolePeerManager()
    {
        try 
        {
            Persistent p = new TurbineRole();
            
            Role u = RolePeerManager.getNewRole(p);
            String roleClassName = u.getClass().getName();
            
            assertEquals("Didn't get a DBRole object from Peer: " + roleClassName, 
                         roleClassName, 
                         "org.apache.fulcrum.security.impl.db.DBRole");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testPermissionPeerManager()
    {
        try 
        {
            Persistent p = new TurbinePermission();
            
            Permission u = PermissionPeerManager.getNewPermission(p);
            String permissionClassName = u.getClass().getName();
            
            assertEquals("Didn't get a DBPermission object from Peer: " + permissionClassName, 
                         permissionClassName, 
                         "org.apache.fulcrum.security.impl.db.DBPermission");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }
}
