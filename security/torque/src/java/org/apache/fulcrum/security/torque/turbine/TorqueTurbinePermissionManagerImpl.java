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
import java.util.Iterator;
import java.util.List;

import org.apache.fulcrum.security.RoleManager;
import org.apache.fulcrum.security.entity.Permission;
import org.apache.fulcrum.security.entity.Role;
import org.apache.fulcrum.security.model.turbine.entity.TurbinePermission;
import org.apache.fulcrum.security.torque.TorqueAbstractPermissionManager;
import org.apache.fulcrum.security.torque.om.TorqueRole;
import org.apache.fulcrum.security.torque.om.TorqueRolePeer;
import org.apache.fulcrum.security.torque.om.TorqueTurbineRolePermissionPeer;
import org.apache.fulcrum.security.util.DataBackendException;
import org.apache.fulcrum.security.util.RoleSet;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;
/**
 * This implementation persists to a database via Torque.
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 * @version $Id:$
 */
public class TorqueTurbinePermissionManagerImpl extends TorqueAbstractPermissionManager
{
    /**
     * Provides the roles for the given permission
     *  
     * @param permission the permission for which the roles should be retrieved  
     * @param con a database connection
     */
    protected void attachObjectsForPermission(Permission permission, Connection con)
        throws TorqueException, DataBackendException
    {
        RoleSet roleSet = new RoleSet();
        
        Criteria criteria = new Criteria();
        criteria.addJoin(TorqueTurbineRolePermissionPeer.ROLE_ID, TorqueRolePeer.ROLE_ID);
        criteria.add(TorqueTurbineRolePermissionPeer.PERMISSION_ID, (Integer)permission.getId());
        
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
        
        ((TurbinePermission)permission).setRoles(roleSet);
    }
}
