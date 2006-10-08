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
import org.apache.fulcrum.security.PermissionManager;
import org.apache.fulcrum.security.UserManager;
import org.apache.fulcrum.security.entity.Group;
import org.apache.fulcrum.security.entity.Permission;
import org.apache.fulcrum.security.entity.Role;
import org.apache.fulcrum.security.entity.User;
import org.apache.fulcrum.security.model.turbine.entity.TurbineRole;
import org.apache.fulcrum.security.model.turbine.entity.TurbineUserGroupRole;
import org.apache.fulcrum.security.torque.TorqueAbstractRoleManager;
import org.apache.fulcrum.security.torque.om.TorqueGroup;
import org.apache.fulcrum.security.torque.om.TorqueGroupPeer;
import org.apache.fulcrum.security.torque.om.TorquePermission;
import org.apache.fulcrum.security.torque.om.TorquePermissionPeer;
import org.apache.fulcrum.security.torque.om.TorqueTurbineRolePermissionPeer;
import org.apache.fulcrum.security.torque.om.TorqueTurbineUserGroupRole;
import org.apache.fulcrum.security.torque.om.TorqueTurbineUserGroupRolePeer;
import org.apache.fulcrum.security.torque.om.TorqueUser;
import org.apache.fulcrum.security.torque.om.TorqueUserPeer;
import org.apache.fulcrum.security.util.DataBackendException;
import org.apache.fulcrum.security.util.PermissionSet;
import org.apache.fulcrum.security.util.UnknownEntityException;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;
/**
 * This implementation persists to a database via Torque.
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 * @version $Id:$
 */
public class TorqueTurbineRoleManagerImpl extends TorqueAbstractRoleManager
{
    /**
     * Provides the user/group/role-relations for the given role
     *  
     * @param role the role for which the relations should be retrieved  
     * @param con a database connection
     */
    protected void attachObjectsForRole(Role role, Connection con)
        throws TorqueException, DataBackendException, UnknownEntityException
    {
        Set ugrSet = new HashSet();
        
        Criteria criteria = new Criteria();
        criteria.add(TorqueTurbineUserGroupRolePeer.ROLE_ID, (Integer)role.getId());
        
        List ugrs = TorqueTurbineUserGroupRolePeer.doSelect(criteria, con);
        UserManager userManager = getUserManager();
        GroupManager groupManager = getGroupManager();
        
        for (Iterator i = ugrs.iterator(); i.hasNext();)
        {
            TurbineUserGroupRole ugr = new TurbineUserGroupRole();
            ugr.setRole(role);
            
            TorqueTurbineUserGroupRole tugr = (TorqueTurbineUserGroupRole)i.next();

            User user = userManager.getUserInstance();
            TorqueUser u = TorqueUserPeer.retrieveByPK(tugr.getUserId(), con);
            user.setId(u.getId());
            user.setName(u.getName());
            user.setPassword(u.getPassword());
            ugr.setUser(user);

            Group group = groupManager.getGroupInstance();
            TorqueGroup g = TorqueGroupPeer.retrieveByPK(tugr.getGroupId(), con);
            group.setId(g.getId());
            group.setName(g.getName());
            ugr.setGroup(group);
            
            ugrSet.add(ugr);
        }
        
        ((TurbineRole)role).setUserGroupRoleSet(ugrSet);

        PermissionSet permissionSet = new PermissionSet();
        
        criteria.clear();
        criteria.addJoin(TorqueTurbineRolePermissionPeer.PERMISSION_ID, TorquePermissionPeer.PERMISSION_ID);
        criteria.add(TorqueTurbineRolePermissionPeer.ROLE_ID, (Integer)role.getId());
        
        List permissions = TorquePermissionPeer.doSelect(criteria, con);
        PermissionManager permissionManager = getPermissionManager();
        
        for (Iterator i = permissions.iterator(); i.hasNext();)
        {
            TorquePermission p = (TorquePermission)i.next();
            Permission permission = permissionManager.getPermissionInstance();
            
            permission.setId(p.getId());
            permission.setName(p.getName());
            permissionSet.add(permission);
        }
        
        ((TurbineRole)role).setPermissions(permissionSet);
    }
}
