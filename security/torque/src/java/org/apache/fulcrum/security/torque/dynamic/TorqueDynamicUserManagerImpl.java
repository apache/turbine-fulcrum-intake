package org.apache.fulcrum.security.torque.dynamic;
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
import java.sql.Connection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.fulcrum.security.GroupManager;
import org.apache.fulcrum.security.entity.Group;
import org.apache.fulcrum.security.entity.User;
import org.apache.fulcrum.security.model.dynamic.entity.DynamicUser;
import org.apache.fulcrum.security.torque.TorqueAbstractUserManager;
import org.apache.fulcrum.security.torque.om.TorqueDynamicUserDelegatesPeer;
import org.apache.fulcrum.security.torque.om.TorqueDynamicUserGroupPeer;
import org.apache.fulcrum.security.torque.om.TorqueGroup;
import org.apache.fulcrum.security.torque.om.TorqueGroupPeer;
import org.apache.fulcrum.security.torque.om.TorqueUser;
import org.apache.fulcrum.security.torque.om.TorqueUserPeer;
import org.apache.fulcrum.security.util.DataBackendException;
import org.apache.fulcrum.security.util.GroupSet;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;
/**
 * This implementation persists to a database via Torque.
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 * @version $Id:$
 */
public class TorqueDynamicUserManagerImpl extends TorqueAbstractUserManager
{
    /**
     * Provides the attached objects for the given user
     *  
     * @param user the user for which the attached objects should be retrieved  
     * @param con a database connection
     */
    protected void attachObjectsForUser(User user, Connection con)
        throws TorqueException, DataBackendException
    {
        GroupSet groupSet = new GroupSet();
        
        Criteria criteria = new Criteria();
        criteria.addJoin(TorqueDynamicUserGroupPeer.GROUP_ID, TorqueGroupPeer.GROUP_ID);
        criteria.add(TorqueDynamicUserGroupPeer.USER_ID, (Integer)user.getId());
        
        List groups = TorqueGroupPeer.doSelect(criteria, con);
        GroupManager groupManager = getGroupManager();
        
        for (Iterator i = groups.iterator(); i.hasNext();)
        {
            TorqueGroup g = (TorqueGroup)i.next();
            Group group = groupManager.getGroupInstance();
            
            group.setId(g.getId());
            group.setName(g.getName());
            groupSet.add(group);
        }
        
        ((DynamicUser)user).setGroups(groupSet);

        Set delegatorsSet = new HashSet();
        
        criteria.clear();
        criteria.addJoin(TorqueDynamicUserDelegatesPeer.DELEGATOR_USER_ID, TorqueUserPeer.USER_ID);
        criteria.add(TorqueDynamicUserDelegatesPeer.DELEGATEE_USER_ID, (Integer)user.getId());
        
        List users = TorqueUserPeer.doSelect(criteria, con);
        
        for (Iterator i = users.iterator(); i.hasNext();)
        {
            TorqueUser u = (TorqueUser)i.next();
            User delegator = getUserInstance();
            
            delegator.setId(u.getId());
            delegator.setName(u.getName());
            delegator.setPassword(u.getPassword());
            delegatorsSet.add(delegator);
        }
        
        ((DynamicUser)user).setDelegators(delegatorsSet);

        Set delegateesSet = new HashSet();
        
        criteria.clear();
        criteria.addJoin(TorqueDynamicUserDelegatesPeer.DELEGATEE_USER_ID, TorqueUserPeer.USER_ID);
        criteria.add(TorqueDynamicUserDelegatesPeer.DELEGATOR_USER_ID, (Integer)user.getId());
        
        users = TorqueUserPeer.doSelect(criteria, con);
        
        for (Iterator i = users.iterator(); i.hasNext();)
        {
            TorqueUser u = (TorqueUser)i.next();
            User delegatee = getUserInstance();
            
            delegatee.setId(u.getId());
            delegatee.setName(u.getName());
            delegatee.setPassword(u.getPassword());
            delegateesSet.add(delegatee);
        }
        
        ((DynamicUser)user).setDelegatees(delegateesSet);
    }
}
