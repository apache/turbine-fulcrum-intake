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
import org.apache.fulcrum.security.impl.db.DBUser;
import org.apache.fulcrum.security.impl.db.DBGroup;
import org.apache.fulcrum.security.impl.db.DBRole;
import org.apache.fulcrum.security.impl.db.DBPermission;
import org.apache.fulcrum.security.impl.db.GroupPeerManager;
import org.apache.fulcrum.security.impl.db.PermissionPeerManager;
import org.apache.fulcrum.security.impl.db.RolePeerManager;
import org.apache.fulcrum.security.impl.db.UserPeerManager;
import org.apache.fulcrum.security.impl.db.entity.TurbineGroupPeer;
import org.apache.fulcrum.security.impl.db.entity.TurbinePermissionPeer;
import org.apache.fulcrum.security.impl.db.entity.TurbineRolePeer;
import org.apache.fulcrum.security.impl.db.entity.TurbineUserPeer;

import org.apache.torque.util.Criteria;
import org.apache.torque.om.Persistent;


public class ManagerTest
    extends TestCase
{
    private static final String PREFIX = "services." +
        SecurityService.SERVICE_NAME + '.';

    public ManagerTest( String name )
    {
        super(name);

        ServiceManager serviceManager = TurbineServices.getInstance();
        serviceManager.setApplicationRoot(".");

        Configuration cfg = new BaseConfiguration();

        cfg.setProperty(PREFIX + "classname", 
                        DBSecurityService.class.getName());

        cfg.setProperty(PREFIX + "user.class", 
                        DBUser.class.getName());
        cfg.setProperty(PREFIX + "group.class", 
                        DBGroup.class.getName());
        cfg.setProperty(PREFIX + "role.class", 
                        DBRole.class.getName());
        cfg.setProperty(PREFIX + "permission.class", 
                        DBPermission.class.getName());

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
        return new TestSuite(ManagerTest.class);
    }
                    
    public void testUserManager()
    {
        try 
        {
            String nameCol       = UserPeerManager.getNameColumn();
            String idCol         = UserPeerManager.getIdColumn();
            String passwordCol   = UserPeerManager.getPasswordColumn();
            String firstNameCol  = UserPeerManager.getFirstNameColumn();
            String lastNameCol   = UserPeerManager.getLastNameColumn();
            String emailCol      = UserPeerManager.getEmailColumn();
            String confirmCol    = UserPeerManager.getConfirmColumn();
            String createDateCol = UserPeerManager.getCreateDateColumn();
            String lastLoginCol  = UserPeerManager.getLastLoginColumn();
            
            String tableName = UserPeerManager.getTableName();

            assertEquals("name Column is "+nameCol+", expected was "+TurbineUserPeer.LOGIN_NAME,
                         nameCol, 
                         TurbineUserPeer.LOGIN_NAME);

            assertEquals("id Column is "+idCol+", expected was "+TurbineUserPeer.USER_ID,
                         idCol,
                         TurbineUserPeer.USER_ID);

            assertEquals("password Column is "+passwordCol+", expected was "+TurbineUserPeer.PASSWORD_VALUE,
                         passwordCol,
                         TurbineUserPeer.PASSWORD_VALUE);

            assertEquals("First Name Column is "+firstNameCol+", expected was "+TurbineUserPeer.FIRST_NAME,
                         firstNameCol,
                         TurbineUserPeer.FIRST_NAME);

            assertEquals("Last Name Column is "+lastNameCol+", expected was "+TurbineUserPeer.LAST_NAME,
                         lastNameCol,
                         TurbineUserPeer.LAST_NAME);

            assertEquals("Email Column is "+emailCol+", expected was "+TurbineUserPeer.EMAIL,
                         emailCol,
                         TurbineUserPeer.EMAIL);

            assertEquals("Confirm Column is "+confirmCol+", expected was "+TurbineUserPeer.CONFIRM_VALUE,
                         confirmCol,
                         TurbineUserPeer.CONFIRM_VALUE);

            assertEquals("Create Date Column is "+createDateCol+", expected was "+TurbineUserPeer.CREATED,
                         createDateCol,
                         TurbineUserPeer.CREATED);

            assertEquals("Last Login Column is "+lastLoginCol+", expected was "+TurbineUserPeer.LAST_LOGIN,
                         lastLoginCol,
                         TurbineUserPeer.LAST_LOGIN);
            
            assertEquals("Table Name is "+tableName+", expected was "+TurbineUserPeer.TABLE_NAME,
                         tableName,
                         TurbineUserPeer.TABLE_NAME);
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testGroupManager()
    {
        try 
        {
            String nameCol   = GroupPeerManager.getNameColumn();
            String idCol     = GroupPeerManager.getIdColumn();
            String tableName = GroupPeerManager.getTableName();
            
            assertEquals("name Column is "+nameCol+", expected was "+TurbineGroupPeer.GROUP_NAME,
                         nameCol,
                         TurbineGroupPeer.GROUP_NAME);
            
            assertEquals("id Column is "+idCol+", expected was "+TurbineGroupPeer.GROUP_ID,
                         idCol,
                         TurbineGroupPeer.GROUP_ID);

            assertEquals("Table Name is "+tableName+", expected was "+TurbineGroupPeer.TABLE_NAME,
                         tableName,
                         TurbineGroupPeer.TABLE_NAME);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testRoleManager()
    {
        try 
        {
            String nameCol   = RolePeerManager.getNameColumn();
            String idCol     = RolePeerManager.getIdColumn();
            String tableName = RolePeerManager.getTableName();

            assertEquals("name Column is "+nameCol+", expected was "+TurbineRolePeer.ROLE_NAME,
                         nameCol,
                         TurbineRolePeer.ROLE_NAME);

            assertEquals("id Column is "+idCol+", expected was "+TurbineRolePeer.ROLE_ID,
                         idCol,
                         TurbineRolePeer.ROLE_ID);

            assertEquals("Table Name is "+tableName+", expected was "+TurbineRolePeer.TABLE_NAME,
                         tableName,
                         TurbineRolePeer.TABLE_NAME);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testPermissionManager()
    {
        try 
        {
            String nameCol   = PermissionPeerManager.getNameColumn();
            String idCol     = PermissionPeerManager.getIdColumn();
            String tableName = PermissionPeerManager.getTableName();

            assertEquals("name Column is "+nameCol+", expected was "+TurbinePermissionPeer.PERMISSION_NAME,
                         nameCol,
                         TurbinePermissionPeer.PERMISSION_NAME);

            assertEquals("id Column is "+idCol+", expected was "+TurbinePermissionPeer.PERMISSION_ID,
                         idCol,
                         TurbinePermissionPeer.PERMISSION_ID);

            assertEquals("Table Name is "+tableName+", expected was "+TurbinePermissionPeer.TABLE_NAME,
                         tableName,
                         TurbinePermissionPeer.TABLE_NAME);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }
}
