package org.apache.fulcrum.security.torque.basic;
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
import org.apache.fulcrum.security.entity.User;
import org.apache.fulcrum.security.model.basic.entity.BasicUser;
import org.apache.fulcrum.security.model.basic.test.AbstractModelManagerTest;
import org.apache.fulcrum.security.torque.om.TorqueBasicUserGroupPeer;
import org.apache.fulcrum.security.torque.om.TorqueGroupPeer;
import org.apache.fulcrum.security.torque.om.TorqueUserPeer;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;

/**
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 * @version $Id:$
 */
public class TorqueBasicModelManagerTest extends AbstractModelManagerTest
{
    public void setUp() throws Exception
    {
        this.setRoleFileName("src/test/BasicTorqueRoleConfig.xml");
        this.setConfigurationFileName("src/test/BasicTorqueComponentConfig.xml");
        securityService = (SecurityService) lookup(SecurityService.ROLE);
        super.setUp();
    }
    
	public void testRevokeAllUser() throws Exception
    {
	    super.testRevokeAllUser();
		User user = userManager.getUserInstance("Clint2");
		assertEquals(0, ((BasicUser) user).getGroups().size());
	}
    
    public void tearDown()
    {
        // cleanup tables
        try
        {
            Criteria criteria = new Criteria();
            criteria.add(TorqueBasicUserGroupPeer.GROUP_ID, 0, Criteria.GREATER_THAN);
            criteria.add(TorqueBasicUserGroupPeer.USER_ID, 0, Criteria.GREATER_THAN);
            TorqueBasicUserGroupPeer.doDelete(criteria);
            
            criteria.clear();
            criteria.add(TorqueUserPeer.USER_ID, 0, Criteria.GREATER_THAN);
            TorqueUserPeer.doDelete(criteria);
            
            criteria.clear();
            criteria.add(TorqueGroupPeer.GROUP_ID, 0, Criteria.GREATER_THAN);
            TorqueGroupPeer.doDelete(criteria);
        }
        catch (TorqueException e)
        {
            fail(e.toString());
        }

        securityService = null;
    }

    /**
     * Constructor for TorqueBasicModelManagerTest.
     *
     * @param arg0
     */
    public TorqueBasicModelManagerTest(String arg0)
    {
        super(arg0);
    }
}
