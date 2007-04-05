package org.apache.fulcrum.security.torque;
/*
 *  Copyright 2001-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import org.apache.fulcrum.security.SecurityService;
import org.apache.fulcrum.security.model.test.AbstractPermissionManagerTest;

/**
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 * @author <a href="jh@byteaction.de">J&#252;rgen Hoffmann</a>
 * @version $Id:$
 */
public class TorquePermissionManagerTest extends AbstractPermissionManagerTest
{
    protected static HsqlDB hsqlDB = null;

    public void setUp() throws Exception
    {
        hsqlDB = new HsqlDB("jdbc:hsqldb:.", "src/test/fulcrum-dynamic-schema.sql");
        hsqlDB.addSQL("src/test/fulcrum-turbine-schema.sql");
        hsqlDB.addSQL("src/test/id-table-schema.sql");
        hsqlDB.addSQL("src/test/fulcrum-dynamic-schema-idtable-init.sql");
        hsqlDB.addSQL("src/test/fulcrum-turbine-schema-idtable-init.sql");

        this.setRoleFileName("src/test/DynamicTorqueRoleConfig.xml");
        this.setConfigurationFileName("src/test/DynamicTorqueComponentConfig.xml");
        securityService = (SecurityService) lookup(SecurityService.ROLE);
        permissionManager = securityService.getPermissionManager();
      
    }

    public void tearDown()
    {
/*
        // cleanup tables
        try
        {
            Criteria criteria = new Criteria();
            criteria.add(TorqueDynamicUserGroupPeer.USER_ID, 0, Criteria.GREATER_THAN);
            TorqueDynamicUserGroupPeer.doDelete(criteria);
            
            criteria.clear();
            criteria.add(TorqueDynamicGroupRolePeer.GROUP_ID, 0, Criteria.GREATER_THAN);
            TorqueDynamicGroupRolePeer.doDelete(criteria);
            
            criteria.clear();
            criteria.add(TorqueDynamicRolePermissionPeer.ROLE_ID, 0, Criteria.GREATER_THAN);
            TorqueDynamicRolePermissionPeer.doDelete(criteria);

            criteria.clear();
            criteria.add(TorqueDynamicUserDelegatesPeer.DELEGATEE_USER_ID, 0, Criteria.GREATER_THAN);
            TorqueDynamicUserDelegatesPeer.doDelete(criteria);

            criteria.clear();
            criteria.add(TorqueUserPeer.USER_ID, 0, Criteria.GREATER_THAN);
            TorqueUserPeer.doDelete(criteria);
            
            criteria.clear();
            criteria.add(TorqueGroupPeer.GROUP_ID, 0, Criteria.GREATER_THAN);
            TorqueGroupPeer.doDelete(criteria);

            criteria.clear();
            criteria.add(TorqueRolePeer.ROLE_ID, 0, Criteria.GREATER_THAN);
            TorqueRolePeer.doDelete(criteria);
            
            criteria.clear();
            criteria.add(TorquePermissionPeer.PERMISSION_ID, 0, Criteria.GREATER_THAN);
            TorquePermissionPeer.doDelete(criteria);
        }
        catch (TorqueException e)
        {
            fail(e.toString());
        }
*/
        permission = null;
        permissionManager = null;
        securityService = null;
    }
    
    /**
     * Constructor for TorquePermissionManagerTest.
     * @param arg0
     */
    public TorquePermissionManagerTest(String arg0)
    {
        super(arg0);
    }
}