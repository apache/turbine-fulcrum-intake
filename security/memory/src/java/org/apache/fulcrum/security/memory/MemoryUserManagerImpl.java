package org.apache.fulcrum.security.memory;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.security.entity.User;
import org.apache.fulcrum.security.spi.AbstractUserManager;
import org.apache.fulcrum.security.util.DataBackendException;
import org.apache.fulcrum.security.util.UnknownEntityException;

/**
 * This implementation keeps all objects in memory.  This is mostly meant to help
 * with testing and prototyping of ideas.
 *
 * @todo Need to load up Crypto component and actually encrypt passwords!
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class MemoryUserManagerImpl
    extends AbstractUserManager
    
{
    /** Logging */
    private static Log log = LogFactory.getLog(MemoryUserManagerImpl.class);
    private static List users = new ArrayList();
   
    /** Our Unique ID counter */
    private static int uniqueId = 0;


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
    public boolean checkExists(User user) throws DataBackendException
    {
        boolean exists = false;
        for (Iterator i = users.iterator(); i.hasNext();)
        {
            User u = (User) i.next();
            if (u.getName().equalsIgnoreCase(user.getName()) | u.getId()
                == user.getId())
            {
                exists = true;
            }
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
     * @throws DataBackendException if there was an error accessing
     *         the data backend.
     */
    public boolean checkExists(String userName) throws DataBackendException
    {
        List tempUsers = new ArrayList();
        for (Iterator i = users.iterator(); i.hasNext();)
        {
            User user = (User) i.next();
            if (user.getName().equalsIgnoreCase(userName))
            {
                tempUsers.add(user);
            }
        }
        if (tempUsers.size() > 1)
        {
            throw new DataBackendException(
                "Multiple Users with same username '" + userName + "'");
        }
        return (tempUsers.size() == 1);
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
    public User getUser(String userName)
        throws UnknownEntityException, DataBackendException
    {
        List tempUsers = new ArrayList();
        for (Iterator i = users.iterator(); i.hasNext();)
        {
            User user = (User) i.next();
            if (user.getName().equalsIgnoreCase(userName))
            {
                tempUsers.add(user);
            }
        }
        if (tempUsers.size() > 1)
        {
            throw new DataBackendException(
                "Multiple Users with same username '" + userName + "'");
        }
        if (tempUsers.size() == 1)
        {
            return (User) tempUsers.get(0);
        }
        throw new UnknownEntityException("Unknown user '" + userName + "'");
    }
    
    /**
    	* Removes an user account from the system.
    	*
    	* @param user the object describing the account to be removed.
    	* @throws DataBackendException if there was an error accessing the data
    	*         backend.
    	* @throws UnknownEntityException if the user account is not present.
    	*/
    public void removeUser(User user)
        throws DataBackendException, UnknownEntityException
    {
        users.remove(user);
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
    protected User persistNewUser(User user)
        throws DataBackendException
    {
       
            users.remove(user);
            user.setId(getUniqueId());
            users.add(user);
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
    public void saveUser(User user)
        throws DataBackendException, UnknownEntityException
    {
        boolean userExists = false;
        userExists = checkExists(user);
        if (userExists)
        {
            users.remove(user);
            users.add(user);
        }
        else
        {
            throw new UnknownEntityException("Unknown user '" + user + "'");
        }
    }

  
    private Object getUniqueId()
    {
        return new Integer(++uniqueId);
    }
}
