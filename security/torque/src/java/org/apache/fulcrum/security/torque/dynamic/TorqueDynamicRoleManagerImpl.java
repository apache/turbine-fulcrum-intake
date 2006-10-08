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

import org.apache.fulcrum.security.GroupManager;
import org.apache.fulcrum.security.PermissionManager;
import org.apache.fulcrum.security.entity.Group;
import org.apache.fulcrum.security.entity.Permission;
import org.apache.fulcrum.security.entity.Role;
import org.apache.fulcrum.security.model.dynamic.entity.DynamicRole;
import org.apache.fulcrum.security.torque.TorqueAbstractRoleManager;
import org.apache.fulcrum.security.torque.om.TorqueDynamicGroupRolePeer;
import org.apache.fulcrum.security.torque.om.TorqueDynamicRolePermissionPeer;
import org.apache.fulcrum.security.torque.om.TorqueGroup;
import org.apache.fulcrum.security.torque.om.TorqueGroupPeer;
import org.apache.fulcrum.security.torque.om.TorquePermission;
import org.apache.fulcrum.security.torque.om.TorquePermissionPeer;
import org.apache.fulcrum.security.util.DataBackendException;
import org.apache.fulcrum.security.util.GroupSet;
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
public class TorqueDynamicRoleManagerImpl extends TorqueAbstractRoleManager
{
    /**
     * Provides the groups/permissions for the given role
     *  
     * @param role the role for which the groups/permissions should be retrieved  
     * @param con a database connection
     */
    protected void attachObjectsForRole(Role role, Connection con)
        throws TorqueException, DataBackendException, UnknownEntityException
    {
        GroupSet groupSet = new GroupSet();
        
        Criteria criteria = new Criteria();
        criteria.addJoin(TorqueDynamicGroupRolePeer.GROUP_ID, TorqueGroupPeer.GROUP_ID);
        criteria.add(TorqueDynamicGroupRolePeer.ROLE_ID, (Integer)role.getId());
        
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
        
        ((DynamicRole)role).setGroups(groupSet);

        PermissionSet permissionSet = new PermissionSet();
        
        criteria.clear();
        criteria.addJoin(TorqueDynamicRolePermissionPeer.PERMISSION_ID, TorquePermissionPeer.PERMISSION_ID);
        criteria.add(TorqueDynamicRolePermissionPeer.ROLE_ID, (Integer)role.getId());
        
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
        
        ((DynamicRole)role).setPermissions(permissionSet);
    }
}
