package org.apache.fulcrum.security;


/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
