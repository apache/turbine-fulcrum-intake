package org.apache.fulcrum.security.torque.turbine;
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

import org.apache.fulcrum.security.RoleManager;
import org.apache.fulcrum.security.UserManager;
import org.apache.fulcrum.security.entity.Group;
import org.apache.fulcrum.security.entity.Role;
import org.apache.fulcrum.security.entity.User;
import org.apache.fulcrum.security.model.turbine.entity.TurbineGroup;
import org.apache.fulcrum.security.model.turbine.entity.TurbineUserGroupRole;
import org.apache.fulcrum.security.torque.TorqueAbstractGroupManager;
import org.apache.fulcrum.security.torque.om.TorqueRole;
import org.apache.fulcrum.security.torque.om.TorqueRolePeer;
import org.apache.fulcrum.security.torque.om.TorqueTurbineUserGroupRole;
import org.apache.fulcrum.security.torque.om.TorqueTurbineUserGroupRolePeer;
import org.apache.fulcrum.security.torque.om.TorqueUser;
import org.apache.fulcrum.security.torque.om.TorqueUserPeer;
import org.apache.fulcrum.security.util.DataBackendException;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;
/**
 * This implementation persists to a database via Torque.
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 * @version $Id:$
 */
public class TorqueTurbineGroupManagerImpl extends TorqueAbstractGroupManager
{
    /**
     * Provides the user/group/role-relations for the given group
     *  
     * @param group the group for which the relations should be retrieved  
     * @param con a database connection
     */
    protected void attachObjectsForGroup(Group group, Connection con)
        throws TorqueException, DataBackendException
    {
        Set ugrSet = new HashSet();
        
        Criteria criteria = new Criteria();
        criteria.add(TorqueTurbineUserGroupRolePeer.GROUP_ID, (Integer)group.getId());
        
        List ugrs = TorqueTurbineUserGroupRolePeer.doSelect(criteria, con);
        UserManager userManager = getUserManager();
        RoleManager roleManager = getRoleManager();
        
        for (Iterator i = ugrs.iterator(); i.hasNext();)
        {
            TurbineUserGroupRole ugr = new TurbineUserGroupRole();
            ugr.setGroup(group);
            
            TorqueTurbineUserGroupRole tugr = (TorqueTurbineUserGroupRole)i.next();

            User user = userManager.getUserInstance();
            TorqueUser u = TorqueUserPeer.retrieveByPK(tugr.getUserId(), con);
            user.setId(u.getId());
            user.setName(u.getName());
            user.setPassword(u.getPassword());
            ugr.setUser(user);

            Role role = roleManager.getRoleInstance();
            TorqueRole r = TorqueRolePeer.retrieveByPK(tugr.getRoleId(), con);
            role.setId(r.getId());
            role.setName(r.getName());
            ugr.setRole(role);
            
            ugrSet.add(ugr);
        }
        
        ((TurbineGroup)group).setUserGroupRoleSet(ugrSet);
    }
}