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
import java.util.Iterator;
import java.util.List;

import org.apache.fulcrum.security.RoleManager;
import org.apache.fulcrum.security.UserManager;
import org.apache.fulcrum.security.entity.Group;
import org.apache.fulcrum.security.entity.Role;
import org.apache.fulcrum.security.entity.User;
import org.apache.fulcrum.security.model.dynamic.entity.DynamicGroup;
import org.apache.fulcrum.security.torque.TorqueAbstractGroupManager;
import org.apache.fulcrum.security.torque.om.TorqueDynamicGroupRolePeer;
import org.apache.fulcrum.security.torque.om.TorqueDynamicUserGroupPeer;
import org.apache.fulcrum.security.torque.om.TorqueRole;
import org.apache.fulcrum.security.torque.om.TorqueRolePeer;
import org.apache.fulcrum.security.torque.om.TorqueUser;
import org.apache.fulcrum.security.torque.om.TorqueUserPeer;
import org.apache.fulcrum.security.util.DataBackendException;
import org.apache.fulcrum.security.util.RoleSet;
import org.apache.fulcrum.security.util.UserSet;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;
/**
 * This implementation persists to a database via Torque.
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 * @version $Id:$
 */
public class TorqueDynamicGroupManagerImpl extends TorqueAbstractGroupManager
{
    /**
     * Provides the users and roles for the given group
     *  
     * @param group the group for which the users/roles should be retrieved  
     * @param con a database connection
     */
    protected void attachObjectsForGroup(Group group, Connection con)
        throws TorqueException, DataBackendException
    {
        UserSet userSet = new UserSet();
        
        Criteria criteria = new Criteria();
        criteria.addJoin(TorqueDynamicUserGroupPeer.USER_ID, TorqueUserPeer.USER_ID);
        criteria.add(TorqueDynamicUserGroupPeer.GROUP_ID, (Integer)group.getId());
        
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
        
        ((DynamicGroup)group).setUsers(userSet);

        RoleSet roleSet = new RoleSet();
        
        criteria.clear();
        criteria.addJoin(TorqueDynamicGroupRolePeer.ROLE_ID, TorqueRolePeer.ROLE_ID);
        criteria.add(TorqueDynamicGroupRolePeer.GROUP_ID, (Integer)group.getId());
        
        List roles = TorqueRolePeer.doSelect(criteria, con);
        RoleManager roleManager = getRoleManager();
        
        for (Iterator i = roles.iterator(); i.hasNext();)
        {
            TorqueRole r = (TorqueRole)i.next();
            Role role = roleManager.getRoleInstance();
            
            role.setId(r.getId());
            role.setName(r.getName());
            roleSet.add(role);
        }
        
        ((DynamicGroup)group).setRoles(roleSet);
    }
}