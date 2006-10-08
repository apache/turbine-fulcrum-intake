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
import java.sql.Connection;
import java.util.Iterator;
import java.util.List;

import org.apache.fulcrum.security.UserManager;
import org.apache.fulcrum.security.entity.Group;
import org.apache.fulcrum.security.entity.User;
import org.apache.fulcrum.security.model.basic.entity.BasicGroup;
import org.apache.fulcrum.security.torque.TorqueAbstractGroupManager;
import org.apache.fulcrum.security.torque.om.TorqueBasicUserGroupPeer;
import org.apache.fulcrum.security.torque.om.TorqueUser;
import org.apache.fulcrum.security.torque.om.TorqueUserPeer;
import org.apache.fulcrum.security.util.DataBackendException;
import org.apache.fulcrum.security.util.UserSet;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;
/**
 * This implementation persists to a database via Torque.
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 * @version $Id:$
 */
public class TorqueBasicGroupManagerImpl extends TorqueAbstractGroupManager
{
    /**
     * Provides the users for the given group
     *  
     * @param group the group for which the users should be retrieved  
     * @param con a database connection
     */
    protected void attachObjectsForGroup(Group group, Connection con)
        throws TorqueException, DataBackendException
    {
        UserSet userSet = new UserSet();
        
        Criteria criteria = new Criteria();
        criteria.addJoin(TorqueBasicUserGroupPeer.USER_ID, TorqueUserPeer.USER_ID);
        criteria.add(TorqueBasicUserGroupPeer.GROUP_ID, (Integer)group.getId());
        
        List users = TorqueUserPeer.doSelect(criteria, con);
        UserManager userManager = getUserManager();
        
        for (Iterator i = users.iterator(); i.hasNext();)
        {
            TorqueUser u = (TorqueUser)i.next();
            User user = userManager.getUserInstance();
            
            user.setId(u.getId());
            user.setName(u.getName());
            user.setPassword(u.getPassword());
            userSet.add(user);
        }
        
        ((BasicGroup)group).setUsers(userSet);
    }
}