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

import org.apache.fulcrum.security.GroupManager;
import org.apache.fulcrum.security.RoleManager;
import org.apache.fulcrum.security.entity.Group;
import org.apache.fulcrum.security.entity.Role;
import org.apache.fulcrum.security.entity.User;
import org.apache.fulcrum.security.model.turbine.entity.TurbineUser;
import org.apache.fulcrum.security.model.turbine.entity.TurbineUserGroupRole;
import org.apache.fulcrum.security.torque.TorqueAbstractUserManager;
import org.apache.fulcrum.security.torque.om.TorqueGroup;
import org.apache.fulcrum.security.torque.om.TorqueGroupPeer;
import org.apache.fulcrum.security.torque.om.TorqueRole;
import org.apache.fulcrum.security.torque.om.TorqueRolePeer;
import org.apache.fulcrum.security.torque.om.TorqueTurbineUserGroupRole;
import org.apache.fulcrum.security.torque.om.TorqueTurbineUserGroupRolePeer;
import org.apache.fulcrum.security.util.DataBackendException;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;
/**
 * This implementation persists to a database via Torque.
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 * @version $Id:$
 */
public class TorqueTurbineUserManagerImpl extends TorqueAbstractUserManager
{
    /**
     * Provides the user/group/role-relations for the given user
     *  
     * @param user the user for which the relations should be retrieved  
     * @param con a database connection
     */
    protected void attachObjectsForUser(User user, Connection con)
        throws TorqueException, DataBackendException
    {
        Set ugrSet = new HashSet();
        
        Criteria criteria = new Criteria();
        criteria.add(TorqueTurbineUserGroupRolePeer.USER_ID, (Integer)user.getId());
        
        List ugrs = TorqueTurbineUserGroupRolePeer.doSelect(criteria, con);
        RoleManager userManager = getRoleManager();
        GroupManager groupManager = getGroupManager();
        
        for (Iterator i = ugrs.iterator(); i.hasNext();)
        {
            TurbineUserGroupRole ugr = new TurbineUserGroupRole();
            ugr.setUser(user);
            
            TorqueTurbineUserGroupRole tugr = (TorqueTurbineUserGroupRole)i.next();

            Role role = userManager.getRoleInstance();
            TorqueRole r = TorqueRolePeer.retrieveByPK(tugr.getRoleId(), con);
            role.setId(r.getId());
            role.setName(r.getName());
            ugr.setRole(role);

            Group group = groupManager.getGroupInstance();
            TorqueGroup g = TorqueGroupPeer.retrieveByPK(tugr.getGroupId(), con);
            group.setId(g.getId());
            group.setName(g.getName());
            ugr.setGroup(group);
            
            ugrSet.add(ugr);
        }
        
        ((TurbineUser)user).setUserGroupRoleSet(ugrSet);
    }
}
