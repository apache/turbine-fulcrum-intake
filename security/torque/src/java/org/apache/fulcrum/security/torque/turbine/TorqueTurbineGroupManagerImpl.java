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
import java.util.Collections;
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
import org.apache.fulcrum.security.spi.AbstractGroupManager;
import org.apache.fulcrum.security.torque.om.TorqueGroup;
import org.apache.fulcrum.security.torque.om.TorqueGroupPeer;
import org.apache.fulcrum.security.torque.om.TorqueRole;
import org.apache.fulcrum.security.torque.om.TorqueRolePeer;
import org.apache.fulcrum.security.torque.om.TorqueTurbineUserGroupRole;
import org.apache.fulcrum.security.torque.om.TorqueTurbineUserGroupRolePeer;
import org.apache.fulcrum.security.torque.om.TorqueUser;
import org.apache.fulcrum.security.torque.om.TorqueUserPeer;
import org.apache.fulcrum.security.util.DataBackendException;
import org.apache.fulcrum.security.util.EntityExistsException;
import org.apache.fulcrum.security.util.GroupSet;
import org.apache.fulcrum.security.util.UnknownEntityException;
import org.apache.torque.NoRowsException;
import org.apache.torque.TorqueException;
import org.apache.torque.om.SimpleKey;
import org.apache.torque.util.Criteria;
import org.apache.torque.util.Transaction;
/**
 * This implementation persists to a database via Torque.
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 * @version $Id:$
 */
public class TorqueTurbineGroupManagerImpl extends AbstractGroupManager
{
    /**
     * Retrieve a Group object with specified name.
     *
     * @param name the name of the Group.
     * @return an object representing the Group with specified name.
     * @throws DataBackendException if there was an error accessing the
     *         data backend.
     * @throws UnknownEntityException if the group does not exist.
     */
    public Group getGroupByName(String name)
        throws DataBackendException, UnknownEntityException
    {
        Group group = getGroupInstance();
        List groups = Collections.EMPTY_LIST;
        Connection con = null;

        try
        {
            con = Transaction.begin(TorqueGroupPeer.DATABASE_NAME);
            
            Criteria criteria = new Criteria();
            criteria.add(TorqueGroupPeer.GROUP_NAME, name);

            groups = TorqueGroupPeer.doSelect(criteria, con);

            if (groups.size() == 1)
            {
                TorqueGroup g = (TorqueGroup) groups.get(0);
                
                group.setId(g.getId());
                group.setName(g.getName());
                
                // Add user/group/role-relations if they exist
                ((TurbineGroup)group).setUserGroupRoleSet(getUgrForGroup(group, con));
            }

            Transaction.commit(con);
        }
        catch (TorqueException e)
        {
            Transaction.safeRollback(con);
            throw new DataBackendException("Error retrieving group information", e);
        }

        if (groups.size() == 0)
        {
            throw new UnknownEntityException("Could not find group" + name);
        }

        if (groups.size() > 1)
        {
            throw new DataBackendException("Multiple Groups with same name '" + name + "'");
        }

        return group;
    }

    /**
     * Retrieves all groups defined in the system.
     *
     * @return the names of all groups defined in the system.
     * @throws DataBackendException if there was an error accessing the
     *         data backend.
     */
    public GroupSet getAllGroups() throws DataBackendException
    {
        GroupSet groupSet = new GroupSet();
        Connection con = null;

        try
        {
            con = Transaction.begin(TorqueGroupPeer.DATABASE_NAME);

            List groups = TorqueGroupPeer.doSelect(new Criteria(), con);
            
            for (Iterator i = groups.iterator(); i.hasNext();)
            {
                Group group = getGroupInstance();
                TorqueGroup g = (TorqueGroup)i.next();
                group.setId(g.getId());
                group.setName(g.getName());
                
                // Add user/group/role-relations if they exist
                ((TurbineGroup)group).setUserGroupRoleSet(getUgrForGroup(group, con));

                groupSet.add(group);
            }

            Transaction.commit(con);
        }
        catch (TorqueException e)
        {
            Transaction.safeRollback(con);
            throw new DataBackendException("Error retrieving group information", e);
        }

        return groupSet;
    }

    /**
        * Removes a Group from the system.
        *
        * @param group The object describing the group to be removed.
        * @throws DataBackendException if there was an error accessing the data
        *         backend.
        * @throws UnknownEntityException if the group does not exist.
        */
    public synchronized void removeGroup(Group group)
        throws DataBackendException, UnknownEntityException
    {
        try
        {
            TorqueGroupPeer.doDelete(SimpleKey.keyFor((Integer)group.getId()));
        }
        catch (TorqueException e)
        {
            throw new DataBackendException("Removing Group '" + group + "' failed", e);
        }
    }

    /**
        * Renames an existing Group.
        *
        * @param group The object describing the group to be renamed.
        * @param name the new name for the group.
        * @throws DataBackendException if there was an error accessing the data
        *         backend.
        * @throws UnknownEntityException if the group does not exist.
        */
    public synchronized void renameGroup(Group group, String name)
        throws DataBackendException, UnknownEntityException
    {
        if (checkExists(group))
        {
            group.setName(name);

            try
            {
                TorqueGroup g = new TorqueGroup();
                g.setId((Integer)group.getId());
                g.setName(name);
                g.setNew(false);
                g.save();
            }
            catch (Exception e)
            {
                throw new DataBackendException("Renaming Group '" + group + "' failed", e);
            }
        }
        else
        {
            throw new UnknownEntityException("Unknown group '" + group + "'");
        }
    }

    /**
     * Determines if the <code>Group</code> exists in the security system.
     *
     * @param groupName a <code>Group</code> value
     * @return true if the group name exists in the system, false otherwise
     * @throws DataBackendException when more than one Group with
     *         the same name exists.
     */
    public boolean checkExists(String groupName) throws DataBackendException
    {
        List groups;

        try
        {
            Criteria criteria = new Criteria();
            criteria.add(TorqueGroupPeer.GROUP_NAME, groupName);

            groups = TorqueGroupPeer.doSelect(criteria);
        }
        catch (TorqueException e)
        {
            throw new DataBackendException("Error retrieving group information", e);
        }

        if (groups.size() > 1)
        {
            throw new DataBackendException(
                    "Multiple groups with same name '" + groupName + "'");
        }

        return (groups.size() == 1);
    }

    /**
    * Creates a new group with specified attributes.
    *
    * @param group the object describing the group to be created.
    * @return a new Group object that has id set up properly.
    * @throws DataBackendException if there was an error accessing the data
    *         backend.
    * @throws EntityExistsException if the group already exists.
    */
    protected synchronized Group persistNewGroup(Group group)
        throws DataBackendException
    {
        try
        {
            TorqueGroup g = new TorqueGroup();
            g.setName(group.getName());
            g.save();
            
            group.setId(g.getId());
        }
        catch (Exception e)
        {
            throw new DataBackendException("Adding Group '" + group + "' failed", e);
        }
        
        return group;
    }

    /**
     * Retrieve a Group object with specified id.
     *
     * @param id
     *            the id of the Group.
     * @return an object representing the Group with specified id.
     * @throws DataBackendException
     *             if there was an error accessing the data backend.
     * @throws UnknownEntityException
     *             if the group does not exist.
     */
    public Group getGroupById(Object id)
        throws DataBackendException, UnknownEntityException
    {
        Group group = getGroupInstance();

        if (id != null && id instanceof Integer)
        {
            Connection con = null;

            try
            {
                con = Transaction.begin(TorqueGroupPeer.DATABASE_NAME);
                
                TorqueGroup g = TorqueGroupPeer.retrieveByPK((Integer)id, con);
                
                group.setId(g.getId());
                group.setName(g.getName());
                
                // Add user/group/role-relations if they exist
                ((TurbineGroup)group).setUserGroupRoleSet(getUgrForGroup(group, con));

                Transaction.commit(con);
            }
            catch (NoRowsException e)
            {
                Transaction.safeRollback(con);
                throw new UnknownEntityException("Group with id '" + id + "' does not exist.", e);
            }
            catch (TorqueException e)
            {
                Transaction.safeRollback(con);
                throw new DataBackendException("Error retrieving group information", e);
            }
        }
        else
        {
            throw new UnknownEntityException("Invalid group id '" + group.getId() + "'");
        }

        return group;
    }

    /**
     * Provides the user/group/role-relations for the given group
     *  
     * @param group the group for which the relations should be retrieved  
     * @param con a database connection
     */
    private Set getUgrForGroup(Group group, Connection con)
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
        
        return ugrSet;
    }
}