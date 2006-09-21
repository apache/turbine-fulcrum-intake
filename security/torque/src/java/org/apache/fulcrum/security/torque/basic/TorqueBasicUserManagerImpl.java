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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.fulcrum.security.GroupManager;
import org.apache.fulcrum.security.entity.Group;
import org.apache.fulcrum.security.entity.User;
import org.apache.fulcrum.security.model.basic.entity.BasicUser;
import org.apache.fulcrum.security.spi.AbstractUserManager;
import org.apache.fulcrum.security.torque.om.TorqueBasicUserGroupPeer;
import org.apache.fulcrum.security.torque.om.TorqueGroup;
import org.apache.fulcrum.security.torque.om.TorqueGroupPeer;
import org.apache.fulcrum.security.torque.om.TorqueUser;
import org.apache.fulcrum.security.torque.om.TorqueUserPeer;
import org.apache.fulcrum.security.util.DataBackendException;
import org.apache.fulcrum.security.util.EntityExistsException;
import org.apache.fulcrum.security.util.GroupSet;
import org.apache.fulcrum.security.util.UnknownEntityException;
import org.apache.fulcrum.security.util.UserSet;
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
public class TorqueBasicUserManagerImpl extends AbstractUserManager
{
    /**
     * Check whether a specified user's account exists.
     *
     * The login name is used for looking up the account.
     *
     * @param userName The name of the user to be checked.
     * @return true if the specified account exists
     * @throws DataBackendException if there was an error accessing
     *         the data backend.
     */
    public boolean checkExists(String userName) throws DataBackendException
    {
        List users;

        try
        {
            Criteria criteria = new Criteria();
            criteria.add(TorqueUserPeer.LOGIN_NAME, userName.toLowerCase());

            users = TorqueUserPeer.doSelect(criteria);
        }
        catch (TorqueException e)
        {
            throw new DataBackendException("Error retrieving user information", e);
        }

        if (users.size() > 1)
        {
            throw new DataBackendException("Multiple Users with same username '" + userName + "'");
        }
        
        return (users.size() == 1);
    }

    /**
     * Retrieve a user from persistent storage using username as the
     * key.
     *
     * @param userName the name of the user.
     * @return an User object.
     * @exception UnknownEntityException if the user's account does not
     *            exist in the database.
     * @exception DataBackendException if there is a problem accessing the
     *            storage.
     */
    public User getUser(String userName) throws UnknownEntityException, DataBackendException
    {
        User user = getUserInstance();
        List users = Collections.EMPTY_LIST;
        Connection con = null;

        try
        {
            con = Transaction.begin(TorqueUserPeer.DATABASE_NAME);
            
            Criteria criteria = new Criteria();
            criteria.add(TorqueUserPeer.LOGIN_NAME, userName.toLowerCase());

            users = TorqueUserPeer.doSelect(criteria, con);

            if (users.size() == 1)
            {
                TorqueUser u = (TorqueUser) users.get(0);
                
                user.setId(u.getId());
                user.setName(u.getName());
                user.setPassword(u.getPassword());
                
                // Add groups if they exist
                ((BasicUser)user).setGroups(getGroupsForUser(user, con));
            }
            
            Transaction.commit(con);
        }
        catch (TorqueException e)
        {
            Transaction.safeRollback(con);
            throw new DataBackendException("Error retrieving user information", e);
        }

        if (users.size() == 0)
        {
            throw new UnknownEntityException("Unknown user '" + userName + "'");
        }

        if (users.size() > 1)
        {
            throw new DataBackendException("Multiple Users with same username '" + userName + "'");
        }

        return user;
    }

    /**
       * Retrieves all users defined in the system.
       *
       * @return the names of all users defined in the system.
       * @throws DataBackendException if there was an error accessing the data
       *         backend.
       */
    public UserSet getAllUsers() throws DataBackendException
    {
        UserSet userSet = new UserSet();
        Connection con = null;

        try
        {
            con = Transaction.begin(TorqueUserPeer.DATABASE_NAME);
            
            List users = TorqueUserPeer.doSelect(new Criteria(), con);

            for (Iterator i = users.iterator(); i.hasNext();)
            {
                User user = getUserInstance();
                TorqueUser u = (TorqueUser)i.next();
                user.setId(u.getId());
                user.setName(u.getName());
                user.setPassword(u.getPassword());

                // Add groups if they exist
                ((BasicUser)user).setGroups(getGroupsForUser(user, con));
                
                userSet.add(user);
            }

            Transaction.commit(con);
        }
        catch (TorqueException e)
        {
            Transaction.safeRollback(con);
            throw new DataBackendException("Error retrieving all users", e);
        }
        
        return userSet;
    }

    /**
    * Removes an user account from the system.
    *
    * @param user the object describing the account to be removed.
    * @throws DataBackendException if there was an error accessing the data
    *         backend.
    * @throws UnknownEntityException if the user account is not present.
    */
    public synchronized void removeUser(User user) throws DataBackendException, UnknownEntityException
    {
        try
        {
            TorqueUserPeer.doDelete(SimpleKey.keyFor((Integer)user.getId()));
        }
        catch (TorqueException e)
        {
            throw new DataBackendException("Removing User '" + user + "' failed", e);
        }
    }

    /**
       * Creates new user account with specified attributes.
       *
       * @param user the object describing account to be created.
       * @param password The password to use for the account.
       *
       * @throws DataBackendException if there was an error accessing the
       *         data backend.
       * @throws EntityExistsException if the user account already exists.
       */
    protected synchronized User persistNewUser(User user) throws DataBackendException
    {
        try
        {
            TorqueUser u = new TorqueUser();
            
            u.setName(user.getName());
            u.setPassword(user.getPassword());
            u.save();

            user.setId(u.getId());
        }
        catch (Exception e)
        {
            throw new DataBackendException("Adding User '" + user + "' failed", e);
        }
        
        return user;
    }

    /**
       * Stores User attributes. The User is required to exist in the system.
       *
       * @param role The User to be stored.
       * @throws DataBackendException if there was an error accessing the data
       *         backend.
       * @throws UnknownEntityException if the role does not exist.
       */
    public synchronized void saveUser(User user) throws DataBackendException, UnknownEntityException
    {
        if (checkExists(user))
        {
            try
            {
                TorqueUser u = new TorqueUser();
                
                u.setId((Integer)user.getId());
                u.setName(user.getName());
                u.setPassword(user.getPassword());
                u.setNew(false);
                u.save();
            }
            catch (Exception e)
            {
                throw new DataBackendException("Saving User '" + user + "' failed", e);
            }
        }
        else
        {
            throw new UnknownEntityException("Unknown user '" + user + "'");
        }
    }

    /**
     * Retrieve a User object with specified id.
     *
     * @param id
     *            the id of the User.
     * @return an object representing the User with specified id.
     * @throws DataBackendException
     *             if there was an error accessing the data backend.
     * @throws UnknownEntityException
     *             if the user does not exist.
     */
    public User getUserById(Object id)
        throws DataBackendException, UnknownEntityException
    {
        User user = getUserInstance();

        if (id != null && id instanceof Integer)
        {
            Connection con = null;
            
            try
            {
                con = Transaction.begin(TorqueUserPeer.DATABASE_NAME);
                
                TorqueUser u = TorqueUserPeer.retrieveByPK((Integer)id, con);

                user.setId(u.getId());
                user.setName(u.getName());
                user.setPassword(u.getPassword());

                // Add groups if they exist
                ((BasicUser)user).setGroups(getGroupsForUser(user, con));
                
                Transaction.commit(con);
            }
            catch (NoRowsException e)
            {
                Transaction.safeRollback(con);
                throw new UnknownEntityException("User with id '" + id + "' does not exist.", e);
            }
            catch (TorqueException e)
            {
                Transaction.safeRollback(con);
                throw new DataBackendException("Error retrieving user information", e);
            }
        }
        else
        {
            throw new UnknownEntityException("Invalid user id '" + user.getId() + "'");
        }

        return user;
    }
    
    /**
     * Provides the groups for the given user
     *  
     * @param user the user for which the groups should be retrieved  
     * @param con a database connection
     */
    private GroupSet getGroupsForUser(User user, Connection con)
        throws TorqueException, DataBackendException
    {
        GroupSet groupSet = new GroupSet();
        
        Criteria criteria = new Criteria();
        criteria.addJoin(TorqueBasicUserGroupPeer.GROUP_ID, TorqueGroupPeer.GROUP_ID);
        criteria.add(TorqueBasicUserGroupPeer.USER_ID, (Integer)user.getId());
        
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
        
        return groupSet;
    }
}
