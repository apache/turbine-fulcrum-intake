package org.apache.fulcrum.security.impl.db;


/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import org.apache.fulcrum.security.entity.User;

import org.apache.fulcrum.security.util.DataBackendException;
import org.apache.fulcrum.security.util.UnknownEntityException;
import org.apache.fulcrum.security.util.EntityExistsException;
import org.apache.fulcrum.security.util.PasswordMismatchException;

import org.apache.torque.util.Criteria;
import org.apache.torque.om.Persistent;

import org.apache.fulcrum.security.UserManager;
import org.apache.fulcrum.security.TurbineSecurity;

/**
 * An UserManager performs {@link org.apache.fulcrum.security.entity.User}
 * objects related tasks on behalf of the
 * {@link org.apache.fulcrum.security.BaseSecurityService}.
 *
 * This implementation uses a relational database for storing user data. It
 * expects that the User interface implementation will be castable to
 * {@link org.apache.torque.om.BaseObject}.
 *
 * @author <a href="mailto:jon@collab.net">Jon S. Stevens</a>
 * @author <a href="mailto:jmcnally@collab.net">John D. McNally</a>
 * @author <a href="mailto:frank.kim@clearink.com">Frank Y. Kim</a>
 * @author <a href="mailto:cberry@gluecode.com">Craig D. Berry</a>
 * @author <a href="mailto:Rafal.Krzewski@e-point.pl">Rafal Krzewski</a>
 * @version $Id$
 */
public class DBUserManager implements UserManager
{

    /**
     * System.out.println() debugging
     */
    private static final boolean DEBUG = false;

    /**
     * Check whether a specified user's account exists.
     *
     * The login name is used for looking up the account.
     *
     * @param user The user to be checked.
     * @return true if the specified account exists
     * @throws DataBackendException if there was an error accessing
     *         the data backend.
     */
    public boolean accountExists(User user)
        throws DataBackendException
    {
        return accountExists(user.getUserName());
    }

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
    public boolean accountExists(String userName)
        throws DataBackendException
    {
        Criteria criteria = new Criteria();
        criteria.add(UserPeerManager.getNameColumn(), userName);
        List users;
        try
        {
            users = UserPeerManager.doSelect(criteria);
        }
        catch (Exception e)
        {
            throw new DataBackendException(
                "Failed to check account's presence", e);
        }
        if (users.size() > 1)
        {
            throw new DataBackendException(
                "Multiple Users with same username '" + userName + "'");
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
    public User retrieve(String userName)
        throws UnknownEntityException, DataBackendException
    {
        Criteria criteria = new Criteria();
        criteria.add(UserPeerManager.getNameColumn(), userName);
        List users;
        try
        {
            users = UserPeerManager.doSelect(criteria);
        }
        catch (Exception e)
        {
            throw new DataBackendException("Failed to retrieve user '" +
                                           userName + "'", e);
        }
        if (users.size() > 1)
        {
            throw new DataBackendException(
                "Multiple Users with same username '" + userName + "'");
        }
        if (users.size() == 1)
        {
            return (User) users.get(0);
        }
        throw new UnknownEntityException("Unknown user '" + userName + "'");
    }

    /**
     * @deprecated Use <a href="#retrieveList">retrieveList</a> instead.
     *
     * @param criteria The criteria of selection.
     * @return a List of users meeting the criteria.
     * @throws DataBackendException if there is a problem accessing the
     *         storage.
     */
    public User[] retrieve(Criteria criteria)
        throws DataBackendException
    {
        return (User [])retrieveList(criteria).toArray(new User[0]);
    }

    /**
     * Retrieve a list of users that meet the specified criteria.
     *
     * As the keys for the criteria, you should use the constants that
     * are defined in {@link User} interface, plus the names
     * of the custom attributes you added to your user representation
     * in the data storage. Use verbatim names of the attributes -
     * without table name prefix in case of DB implementation.
     *
     * @param criteria The criteria of selection.
     * @return a List of users meeting the criteria.
     * @throws DataBackendException if there is a problem accessing the
     *         storage.
     */
    public List retrieveList(Criteria criteria)
        throws DataBackendException
    {
        Iterator keys = criteria.keySet().iterator();
        while (keys.hasNext())
        {
            String key = (String) keys.next();

            // set the table name for all attached criterion
            Criteria.Criterion[] criterion = criteria
                .getCriterion(key).getAttachedCriterion();

            for (int i = 0; i < criterion.length; i++)
            {
                String table = criterion[i].getTable();
                if (table == null || "".equals(table))
                {
                    criterion[i].setTable(UserPeerManager.getTableName());
                }
            }
        }
        List users = new ArrayList(0);
        try
        {
            users = UserPeerManager.doSelect(criteria);
        }
        catch (Exception e)
        {
            throw new DataBackendException("Failed to retrieve users", e);
        }
        return users;
    }

    /**
     * Retrieve a user from persistent storage using username as the
     * key, and authenticate the user. The implementation may chose
     * to authenticate to the server as the user whose data is being
     * retrieved.
     *
     * @param userName the name of the user.
     * @param password the user supplied password.
     * @return an User object.
     * @exception PasswordMismatchException if the supplied password was
     *            incorrect.
     * @exception UnknownEntityException if the user's account does not
     *            exist in the database.
     * @exception DataBackendException if there is a problem accessing the
     *            storage.
     */
    public User retrieve(String userName, String password)
        throws PasswordMismatchException, UnknownEntityException,
               DataBackendException
    {
        User user = retrieve(userName);
        authenticate(user, password);
        return user;
    }

    /**
     * Save an User object to persistent storage. User's account is
     * required to exist in the storage.
     *
     * @param user an User object to store.
     * @exception UnknownEntityException if the user's account does not
     *            exist in the database.
     * @exception DataBackendException if there is a problem accessing the
     *            storage.
     */
    public void store(User user)
        throws UnknownEntityException, DataBackendException
    {
        if (!accountExists(user))
        {
            throw new UnknownEntityException("The account '" +
                                             user.getUserName() + "' does not exist");
        }

        try
        {
            // this is to mimic the old behavior of the method, the user 
            // should be new that is passed to this method.  It would be
            // better if this was checked, but the original code did not
            // care about the user's state, so we set it to be appropriate
            ((Persistent) user).setNew(false);
            ((Persistent) user).setModified(true);
            ((Persistent) user).save();
        }
        catch (Exception e)
        {
            throw new DataBackendException("Failed to save user object", e);
        }
    }

    /**
     * Authenticate an User with the specified password. If authentication
     * is successful the method returns nothing. If there are any problems,
     * exception was thrown.
     *
     * @param user an User object to authenticate.
     * @param password the user supplied password.
     * @exception PasswordMismatchException if the supplied password was
     *            incorrect.
     * @exception UnknownEntityException if the user's account does not
     *            exist in the database.
     * @exception DataBackendException if there is a problem accessing the
     *            storage.
     */
    public void authenticate(User user, String password)
        throws PasswordMismatchException, UnknownEntityException,
               DataBackendException
    {
        if (!accountExists(user))
        {
            throw new UnknownEntityException("The account '" +
                                             user.getUserName() + "' does not exist");
        }
        if (DEBUG)
        {
            System.out.println ("Supplied Pass: " + password);
            System.out.println ("User Pass: " + user.getPassword());
        }


        /*
         * Unix crypt needs the existing, encrypted password text as
         * salt for checking the supplied password. So we supply it 
         * into the checkPassword routine
         */

        if (!TurbineSecurity.checkPassword(password, user.getPassword()))
        {
            throw new PasswordMismatchException("The passwords do not match");
        }
    }

    /**
     * Change the password for an User. The user must have supplied the
     * old password to allow the change.
     *
     * @param user an User to change password for.
     * @param oldPassword The old password to verify
     * @param newPassword The new password to set
     * @exception PasswordMismatchException if the supplied password was
     *            incorrect.
     * @exception UnknownEntityException if the user's account does not
     *            exist in the database.
     * @exception DataBackendException if there is a problem accessing the
     *            storage.
     */
    public void changePassword(User user, String oldPassword,
                                String newPassword)
        throws PasswordMismatchException, UnknownEntityException,
               DataBackendException
    {
        if (!accountExists(user))
        {
            throw new UnknownEntityException("The account '" +
                                             user.getUserName() + "' does not exist");
        }

        if (!TurbineSecurity.checkPassword(oldPassword, user.getPassword()))
        {
            throw new PasswordMismatchException(
                "The supplied old password for '" + user.getUserName() +
                "' was incorrect");
        }
        user.setPassword(TurbineSecurity.encryptPassword(newPassword));
        // save the changes in the database imediately, to prevent the password
        // being 'reverted' to the old value if the user data is lost somehow
        // before it is saved at session's expiry.
        store(user);
    }

    /**
     * Forcibly sets new password for an User.
     *
     * This is supposed by the administrator to change the forgotten or
     * compromised passwords. Certain implementatations of this feature
     * would require administrative level access to the authenticating
     * server / program.
     *
     * @param user an User to change password for.
     * @param password the new password.
     * @exception UnknownEntityException if the user's record does not
     *            exist in the database.
     * @exception DataBackendException if there is a problem accessing the
     *            storage.
     */
    public void forcePassword(User user, String password)
        throws UnknownEntityException, DataBackendException
    {
        if (!accountExists(user))
        {
            throw new UnknownEntityException("The account '" +
                                             user.getUserName() + "' does not exist");
        }
        user.setPassword(TurbineSecurity.encryptPassword(password));
        // save the changes in the database immediately, to prevent the
        // password being 'reverted' to the old value if the user data
        // is lost somehow before it is saved at session's expiry.
        store(user);
    }

    /**
     * Creates new user account with specified attributes.
     *
     * @param user The object describing account to be created.
     * @param password The initial password to use.
     *
     * @throws DataBackendException if there was an error accessing
     the data backend.
     * @throws EntityExistsException if the user account already exists.
     */
    public void createAccount(User user, String password)
        throws EntityExistsException, DataBackendException
    {
        if (accountExists(user))
        {
            throw new EntityExistsException("The account '" +
                                            user.getUserName() + "' already exists");
        }

        user.setPassword(TurbineSecurity.encryptPassword(password));

        try
        {
            // this is to mimic the old behavior of the method, the user 
            // should be new that is passed to this method.  It would be
            // better if this was checked, but the original code did not
            // care about the user's state, so we set it to be appropriate
            ((Persistent) user).setNew(true);
            ((Persistent) user).setModified(true);
            ((Persistent) user).save();
        }
        catch (Exception e)
        {
            throw new DataBackendException("Failed to create account '" +
                                           user.getUserName() + "'", e);
        }
    }

    /**
     * Removes an user account from the system.
     *
     * @param user the object describing the account to be removed.
     * @throws DataBackendException if there was an error accessing
     the data backend.
     * @throws UnknownEntityException if the user account is not present.
     */
    public void removeAccount(User user)
        throws UnknownEntityException, DataBackendException
    {
        if (!accountExists(user))
        {
            throw new UnknownEntityException("The account '" +
                                             user.getUserName() + "' does not exist");
        }
        Criteria criteria = new Criteria();
        criteria.add(UserPeerManager.getNameColumn(), user.getUserName());
        try
        {
            UserPeerManager.doDelete(criteria);
        }
        catch (Exception e)
        {
            throw new DataBackendException("Failed to remove account '" +
                                           user.getUserName() + "'", e);
        }
    }
}
