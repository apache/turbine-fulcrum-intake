package org.apache.fulcrum.security.nt;
/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Turbine" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Turbine", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
import javax.security.auth.login.LoginException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.security.entity.Group;
import org.apache.fulcrum.security.entity.User;
import org.apache.fulcrum.security.model.basic.entity.BasicUser;
import org.apache.fulcrum.security.model.dynamic.entity.DynamicUser;
import org.apache.fulcrum.security.spi.AbstractUserManager;
import org.apache.fulcrum.security.util.DataBackendException;
import org.apache.fulcrum.security.util.EntityExistsException;
import org.apache.fulcrum.security.util.PasswordMismatchException;
import org.apache.fulcrum.security.util.UnknownEntityException;

import com.tagish.auth.win32.NTSystem;
/**
 * This implementation attempts to manager users against NT.
 * 
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class NTUserManagerImpl extends AbstractUserManager
{
    /** Logging */
    private static Log log = LogFactory.getLog(NTUserManagerImpl.class);

    protected User persistNewUser(User user) throws DataBackendException
    {
        throw new RuntimeException("This method is not supported.");
    }

    /**
     * Retrieve a user from persistent storage using username as the key, and
     * authenticate the user. The implementation may chose to authenticate to
     * the server as the user whose data is being retrieved.
     * 
     * @param userName the name of the user.
     * @param password the user supplied password.
     * @return an User object.
     * @exception PasswordMismatchException if the supplied password was
     *                incorrect.
     * @exception UnknownEntityException if the user's account does not exist
     *                in the database.
     * @exception DataBackendException if there is a problem accessing the
     *                storage.
     */
    public User getUser(String userName, String password)
        throws PasswordMismatchException, UnknownEntityException, DataBackendException
    {
        User user = getUserInstance(userName);
        authenticate(user, password);
        return user;
    }

    /**
     * Check whether a specified user's account exists.
     * 
     * The login name is used for looking up the account.
     * 
     * @param user The user to be checked.
     * @return true if the specified account exists
     * @throws DataBackendException if there was an error accessing the data
     *             backend.
     */
    public boolean checkExists(User user) throws DataBackendException
    {
        boolean exists = false;
        try
        {
            authenticate(user, user.getPassword());
            exists = true;
        }
        catch (PasswordMismatchException pme)
        {
            exists = false;
        }
        catch (UnknownEntityException uee)
        {
            exists = false;
        }
        return exists;
    }
    /**
     * Check whether a specified user's account exists.
     * 
     * The login name is used for looking up the account.
     * 
     * @param userName The name of the user to be checked.
     * @return true if the specified account exists
     * @throws DataBackendException if there was an error accessing the data
     *             backend.
     */
    public boolean checkExists(String userName) throws DataBackendException
    {
        throw new RuntimeException("This method is not supported.");
    }

    /**
     * Retrieve a user from persistent storage using username as the key. Not
     * supported currently.
     * 
     * @param userName the name of the user.
     * @return an User object.
     * @exception UnknownEntityException if the user's account does not exist
     *                in the database.
     * @exception DataBackendException if there is a problem accessing the
     *                storage.
     */
    public User getUser(String userName)
        throws UnknownEntityException, DataBackendException
    {
        throw new RuntimeException("Not supported by NT User Manager");
    }

    /**
     * Authenticate an User with the specified password. If authentication is
     * successful the method returns nothing. If there are any problems,
     * exception was thrown.  Additionally, if the User object is of type BasicUser
     * or DynamicUser, then it will populate all the group information as well!
     * 
     * @param user an User object to authenticate.
     * @param password the user supplied password.
     * @exception PasswordMismatchException if the supplied password was
     *                incorrect.
     * @exception UnknownEntityException if the user's account does not exist
     *                in the database.
     * @exception DataBackendException if there is a problem accessing the
     *                storage.
     */
    public void authenticate(User user, String password)
        throws PasswordMismatchException, UnknownEntityException, DataBackendException
    {
        NTSystem ntSystem = new NTSystem();
        char passwordArray[] = password.toCharArray();
        try
        {
            String username = ParseUtils.parseForUsername(user.getName());
            String domain = ParseUtils.parseForDomain(user.getName());
            ntSystem.logon(username, passwordArray, domain);
            if (!ntSystem.getName().equalsIgnoreCase(username))
            {
                throw new PasswordMismatchException(
                    "Could not authenticate user "
                        + username
                        + " against domain "
                        + domain);
            }
            String groups[] = ntSystem.getGroupNames(false);
            for (int i = 0; i < groups.length; i++)
            {
                // Note how it populates groups? This
                // should maybe delegate a call to the
                // group manager to look for groups it
                // knows about instead.
                Group group = getGroupManager().getGroupInstance();
                group.setName(groups[i]);
                group.setId(groups[i]);
                if (user instanceof DynamicUser)
                {
                    ((DynamicUser) user).addGroup(group);
                }
                else if (user instanceof BasicUser)
                {
                    ((BasicUser) user).addGroup(group);
                }
            }
            ntSystem.logoff();
        }
        catch (LoginException le)
        {
            ntSystem.logoff();
            throw new DataBackendException(le.getMessage(), le);
        }
    }

    /**
     * Removes an user account from the system. Not supported currently.
     * 
     * @param user the object describing the account to be removed.
     * @throws DataBackendException if there was an error accessing the data
     *             backend.
     * @throws UnknownEntityException if the user account is not present.
     */
    public void removeUser(User user)
        throws DataBackendException, UnknownEntityException
    {
        throw new RuntimeException("Not supported by NT User Manager");
    }
    /**
     * Creates new user account with specified attributes. Not supported
     * currently.
     * 
     * @param user the object describing account to be created.
     * @param password The password to use for the account.
     * 
     * @throws DataBackendException if there was an error accessing the data
     *             backend.
     * @throws EntityExistsException if the user account already exists.
     */
    public User addUser(User user, String password)
        throws DataBackendException, EntityExistsException
    {
        throw new RuntimeException("Not supported by NT User Manager");
    }
    /**
     * Stores User attributes. The User is required to exist in the system. Not
     * supported currently.
     * 
     * @param role The User to be stored.
     * @throws DataBackendException if there was an error accessing the data
     *             backend.
     * @throws UnknownEntityException if the role does not exist.
     */
    public void saveUser(User user)
        throws DataBackendException, UnknownEntityException
    {
        throw new RuntimeException("Not supported by NT User Manager");
    }

}
