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
