package org.apache.fulcrum.security.spi.hibernate.simple;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.hibernate.Hibernate;
import net.sf.hibernate.HibernateException;

import org.apache.avalon.framework.component.ComponentException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.factory.FactoryService;
import org.apache.fulcrum.security.acl.AccessControlList;
import org.apache.fulcrum.security.acl.DefaultAccessControlList;
import org.apache.fulcrum.security.authenticator.Authenticator;
import org.apache.fulcrum.security.entity.Group;
import org.apache.fulcrum.security.entity.Role;
import org.apache.fulcrum.security.entity.User;
import org.apache.fulcrum.security.model.simple.entity.SimpleGroup;
import org.apache.fulcrum.security.model.simple.entity.SimpleRole;
import org.apache.fulcrum.security.model.simple.entity.SimpleUser;
import org.apache.fulcrum.security.model.simple.manager.SimpleUserManager;
import org.apache.fulcrum.security.spi.hibernate.simple.entity.HibernateSimpleUser;
import org.apache.fulcrum.security.util.DataBackendException;
import org.apache.fulcrum.security.util.EntityExistsException;
import org.apache.fulcrum.security.util.GroupSet;
import org.apache.fulcrum.security.util.PasswordMismatchException;
import org.apache.fulcrum.security.util.RoleSet;
import org.apache.fulcrum.security.util.UnknownEntityException;
/**
 * This implementation persists to a database via Hibernate.
 *
 * @todo Need to load up Crypto component and actually encrypt passwords!
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class HibernateUserManagerImpl extends BaseHibernateManager implements SimpleUserManager
{
    /** Logging */
    private static Log log = LogFactory.getLog(HibernateUserManagerImpl.class);
    /** A factory to construct ACL Objects */
    private FactoryService aclFactoryService = null;
    /*** out authenticator to use **/
    private Authenticator authenticator;
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
        return checkExists(user.getName());
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
        List users = null;
        try
        {
            session = hibernateService.openSession();
            users = session.find("from HibernateSimpleUser su where su.name=?", userName, Hibernate.STRING);
			session.close();
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
            session = hibernateService.openSession();
            users =
                session.find("from HibernateSimpleUser su where su.name=?", userName.toLowerCase(), Hibernate.STRING);
			session.close();
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
    public User getUser(String userName, String password)
        throws PasswordMismatchException, UnknownEntityException, DataBackendException
    {
        User user = getUser(userName);
        authenticate(user, password);
        return user;
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
        throws PasswordMismatchException, UnknownEntityException, DataBackendException
    {
        if (authenticator == null)
        {
            try
            {
                authenticator = (Authenticator) getComponentManager().lookup(Authenticator.ROLE);
            }
            catch (ComponentException ce)
            {
                throw new DataBackendException(ce.getMessage(), ce);
            }
        }
        if (!authenticator.authenticate(user,password)){
        	throw new PasswordMismatchException("Can not authenticate user.");
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
    public void changePassword(User user, String oldPassword, String newPassword)
        throws PasswordMismatchException, UnknownEntityException, DataBackendException
    {
        if (!checkExists(user))
        {
            throw new UnknownEntityException("The account '" + user.getName() + "' does not exist");
        }
        if (!oldPassword.equals(user.getPassword()))
        {
            throw new PasswordMismatchException("The supplied old password for '" + user.getName() + "' was incorrect");
        }
        user.setPassword(newPassword);
        // save the changes in the database imediately, to prevent the password
        // being 'reverted' to the old value if the user data is lost somehow
        // before it is saved at session's expiry.
        saveUser(user);
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
    public void forcePassword(User user, String password) throws UnknownEntityException, DataBackendException
    {
        if (!checkExists(user))
        {
            throw new UnknownEntityException("The account '" + user.getName() + "' does not exist");
        }
        user.setPassword(password);
        // save the changes in the database immediately, to prevent the
        // password being 'reverted' to the old value if the user data
        // is lost somehow before it is saved at session's expiry.
        saveUser(user);
    }
    /**
     * Construct a blank User object.
     *
     * This method calls getUserClass, and then creates a new object using
     * the default constructor.
     *
     * @return an object implementing User interface.
     * @throws UnknownEntityException if the object could not be instantiated.
     */
    public User getUserInstance() throws UnknownEntityException
    {
        User user;
        try
        {
            user = (User) new HibernateSimpleUser();
        }
        catch (Exception e)
        {
            throw new UnknownEntityException("Failed instantiate an User implementation object", e);
        }
        return user;
    }
    /**
     * Construct a blank User object.
     *
     * This method calls getUserClass, and then creates a new object using
     * the default constructor.
     *
     * @param userName The name of the user.
     *
     * @return an object implementing User interface.
     *
     * @throws UnknownEntityException if the object could not be instantiated.
     */
    public User getUserInstance(String userName) throws UnknownEntityException
    {
        User user = getUserInstance();
        user.setName(userName);
        return user;
    }
    /**
    	 * Revokes all groups from a user
    	 *
    	 * This method is used when deleting an account.
    	 *
    	 * @param user the User.
    	 * @throws DataBackendException if there was an error accessing the data
    	 *         backend.
    	 * @throws UnknownEntityException if the account is not present.
    	 */
    public synchronized void revokeAll(User user) throws DataBackendException, UnknownEntityException
    {
        boolean userExists = false;
        try
        {
            userExists = checkExists(user);
            if (userExists)
            {
                ((SimpleUser) user).setGroups(new GroupSet());
                updateEntity(user);
                return;
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException("revokeAll(User) failed", e);
        }
        finally
        {
        }
        throw new UnknownEntityException("Unknown user '" + user.getName() + "'");
    }
    /**
    * Determines if the <code>Group</code> exists in the security system.
    *
    * @param group a <code>Group</code> value
    * @return true if the group exists in the system, false otherwise
    * @throws DataBackendException when more than one Group with
    *         the same name exists.
    * @throws Exception A generic exception.
    */
    private boolean checkExists(Group group) throws DataBackendException, Exception
    {
        return getGroupManager().checkExists(group);
    }
    /**
    * Determines if the <code>Role</code> exists in the security system.
    *
    * @param role a <code>Role</code> value
    * @return true if the role exists in the system, false otherwise
    * @throws DataBackendException when more than one Role with
    *         the same name exists.
    * @throws Exception A generic exception.
    */
    private boolean checkExists(Role role) throws DataBackendException, Exception
    {
        return getRoleManager().checkExists(role);
    }
    /**
     * Construct a new ACL object.
     *
     * This constructs a new ACL object from the configured class and
     * initializes it with the supplied roles and permissions.
     *
     * @param roles The roles that this ACL should contain
     * @param permissions The permissions for this ACL
     *
     * @return an object implementing ACL interface.
     * @throws UnknownEntityException if the object could not be instantiated.
     */
    private AccessControlList getAclInstance(Map roles, Map permissions) throws UnknownEntityException
    {
        Object[] objects = { roles, permissions };
        String[] signatures = { Map.class.getName(), Map.class.getName()};
        AccessControlList accessControlList;
        try
        {
            /*
             * 
             @todo I think this is overkill for now..
            accessControlList =
                (AccessControlList) aclFactoryService.getInstance(aclClass.getName(), objects, signatures);
                */
            accessControlList = new DefaultAccessControlList(roles, permissions);
        }
        catch (Exception e)
        {
            throw new UnknownEntityException("Failed to instantiate an ACL implementation object", e);
        }
        return accessControlList;
    }
    public AccessControlList getACL(User user)
    {
        Map roleSets = new HashMap();
        Map permissionSets = new HashMap();
        for (Iterator i = ((SimpleUser) user).getGroups().iterator(); i.hasNext();)
        {
            Group group = (Group) i.next();
            RoleSet roleSet = (RoleSet) ((SimpleGroup) group).getRoles();
            roleSets.put(group, roleSet);
            for (Iterator j = roleSet.iterator(); j.hasNext();)
            {
                SimpleRole role = (SimpleRole) j.next();
                permissionSets.put(role, role.getPermissions());
            }
        }
        try
        {
            return getAclInstance(roleSets, permissionSets);
        }
        catch (UnknownEntityException uue)
        {
            throw new RuntimeException(uue.getMessage(), uue);
        }
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
        // revoke all roles form the user
        revokeAll(user);
        removeEntity(user);
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
    public void addUser(User user, String password) throws DataBackendException, EntityExistsException
    {
        if (StringUtils.isEmpty(user.getName()))
        {
            throw new DataBackendException("Could not create " + "an user with empty name!");
        }
        if (checkExists(user))
        {
            throw new EntityExistsException("The account '" + user.getName() + "' already exists");
        }
        user.setPassword(password);
        addEntity(user);
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
            updateEntity(user);
        }
        else
        {
            throw new UnknownEntityException("Unknown user '" + user + "'");
        }
    }
    /**
     * Puts a user in a group.
     *
     * This method is used when adding a user to a group
     *
     * @param user the User.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws UnknownEntityException if the account is not present.
     */
    public void grant(User user, Group group) throws DataBackendException, UnknownEntityException
    {
        boolean groupExists = false;
        boolean userExists = false;
        try
        {
            groupExists = checkExists(group);
            userExists = checkExists(user);
            if (groupExists && userExists)
            {
				((SimpleUser) user).addGroup(group);
				((SimpleGroup) group).addUser(user);
				updateEntity(user);
				//updateEntity(group);
                return;
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException("grant(Role,Permission) failed", e);
        }
        finally
        {
        }
        if (!groupExists)
        {
            throw new UnknownEntityException("Unknown group '" + group.getName() + "'");
        }
        if (!userExists)
        {
            throw new UnknownEntityException("Unknown user '" + user.getName() + "'");
        }
    }
    /**
    * Removes a user in a group.
    *
    * This method is used when removing a user to a group
    *
    * @param user the User.
    * @throws DataBackendException if there was an error accessing the data
    *         backend.
    * @throws UnknownEntityException if the user or group is not present.
    */
    public void revoke(User user, Group group) throws DataBackendException, UnknownEntityException
    {
        boolean groupExists = false;
        boolean userExists = false;
        try
        {
            groupExists = checkExists(group);
            userExists = checkExists(user);
            if (groupExists && userExists)
            {
				((SimpleUser) user).removeGroup(group);

                updateEntity(user);
                return;
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException("grant(Role,Permission) failed", e);
        }
        finally
        {
        }
        if (!groupExists)
        {
            throw new UnknownEntityException("Unknown group '" + group.getName() + "'");
        }
        if (!userExists)
        {
            throw new UnknownEntityException("Unknown user '" + user.getName() + "'");
        }
    }
}
