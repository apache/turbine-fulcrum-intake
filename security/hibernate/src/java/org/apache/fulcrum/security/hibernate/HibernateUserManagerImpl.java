package org.apache.fulcrum.security.hibernate;
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
import java.util.List;

import net.sf.hibernate.Hibernate;
import net.sf.hibernate.HibernateException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.security.entity.User;
import org.apache.fulcrum.security.spi.AbstractUserManager;
import org.apache.fulcrum.security.util.DataBackendException;
import org.apache.fulcrum.security.util.UnknownEntityException;
import org.apache.fulcrum.security.util.UserSet;
/**
 * This implementation persists to a database via Hibernate.
 *
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class HibernateUserManagerImpl extends AbstractUserManager
{
    /** Logging */
    private static Log log = LogFactory.getLog(HibernateUserManagerImpl.class);

	private PersistenceHelper persistenceHelper;
   
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
        List users = null;
        try
        {
            users = getPersistenceHelper().retrieveSession().find("from " + User.class.getName() + " su where su.name=?", userName, Hibernate.STRING);
        }
        catch (HibernateException e)
        {
            throw new DataBackendException("Error retriving user information", e);
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
        List users = null;
        try
        {
            users =
			getPersistenceHelper().retrieveSession().find(
                    "from " + User.class.getName() + " su where su.name=?",
                    userName.toLowerCase(),
                    Hibernate.STRING);
        }
        catch (HibernateException e)
        {
            throw new DataBackendException("Error retriving user information", e);
        }
        if (users.size() > 1)
        {
            throw new DataBackendException("Multiple Users with same username '" + userName + "'");
        }
        if (users.size() == 1)
        {
            return (User) users.get(0);
        }
        throw new UnknownEntityException("Unknown user '" + userName + "'");
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
		try
		{

			List users =
			getPersistenceHelper().retrieveSession().find(
					"from " + User.class.getName() + "");
			userSet.add(users);
		}
		catch (HibernateException e)
		{
			throw new DataBackendException(
				"Error retriving all users",
				e);
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
    public void removeUser(User user) throws DataBackendException, UnknownEntityException
    {    
		getPersistenceHelper().removeEntity(user);
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
    public User persistNewUser(User user) throws DataBackendException
    {
  
		getPersistenceHelper().addEntity(user);
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
    public void saveUser(User user) throws DataBackendException, UnknownEntityException
    {
        boolean userExists = false;
        userExists = checkExists(user);
        if (userExists)
        {
			getPersistenceHelper().updateEntity(user);
        }
        else
        {
            throw new UnknownEntityException("Unknown user '" + user + "'");
        }
    }
    
	/**
	 * @return Returns the persistenceHelper.
	 */
	public PersistenceHelper getPersistenceHelper() throws DataBackendException
	{
		if (persistenceHelper == null)
		{
			persistenceHelper = (PersistenceHelper)resolve(PersistenceHelper.ROLE);
		}
		return persistenceHelper;
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
	throws DataBackendException, UnknownEntityException {
		
		User user = null;

		if (id != null)
			try {
				List users =
					getPersistenceHelper().retrieveSession().find(
							"from " + User.class.getName() + " su where su.id=?",
							id,
							Hibernate.LONG);
				if (users.size() == 0) {
					throw new UnknownEntityException(
							"Could not find user by id " + id);
				}
				user = (User) users.get(0);
				//session.close();
			} catch (HibernateException e) {
				throw new DataBackendException(
						"Error retriving user information",
						e);
			}
			
		return user;
	}
}
