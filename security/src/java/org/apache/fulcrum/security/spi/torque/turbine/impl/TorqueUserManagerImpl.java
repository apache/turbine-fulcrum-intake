package org.apache.fulcrum.security.spi.torque.turbine.impl;
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
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.factory.FactoryService;
import org.apache.fulcrum.security.acl.AccessControlList;
import org.apache.fulcrum.security.entity.Group;
import org.apache.fulcrum.security.entity.Role;
import org.apache.fulcrum.security.entity.User;
import org.apache.fulcrum.security.spi.torque.turbine.ClassicTorqueUser;
import org.apache.fulcrum.security.spi.torque.turbine.InitializationException;
import org.apache.fulcrum.security.spi.torque.turbine.TorqueSecurity;
import org.apache.fulcrum.security.spi.torque.turbine.TorqueUserManager;
import org.apache.fulcrum.security.spi.torque.turbine.peer.TorqueUserPeer;
import org.apache.fulcrum.security.spi.torque.turbine.peer.UserGroupRolePeer;
import org.apache.fulcrum.security.spi.torque.turbine.peermanagers.GroupPeerManager;
import org.apache.fulcrum.security.spi.torque.turbine.peermanagers.RolePeerManager;
import org.apache.fulcrum.security.spi.torque.turbine.peermanagers.UserPeerManager;
import org.apache.fulcrum.security.util.DataBackendException;
import org.apache.fulcrum.security.util.EntityExistsException;
import org.apache.fulcrum.security.util.PasswordMismatchException;
import org.apache.fulcrum.security.util.UnknownEntityException;
import org.apache.torque.TorqueException;
import org.apache.torque.om.NumberKey;
import org.apache.torque.om.Persistent;
import org.apache.torque.util.Criteria;
/**
 * An UserManager performs {@link org.apache.turbine.om.security.User}
 * objects related tasks on behalf of the
 * {@link org.apache.turbine.services.security.BaseSecurityService}.
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
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public class TorqueUserManagerImpl extends TorqueManagerComponent implements TorqueUserManager
{
    /** Logging */
    private static Log log = LogFactory.getLog(TorqueUserManagerImpl.class);
    /** The class of User the SecurityService uses */
    private Class userClass = null;
    
	/** The class of ACL the SecurityService uses */
	private Class aclClass = null;    
	
	/** A factory to construct ACL Objects */
	private FactoryService aclFactoryService = null;	
    /**
     * Initializes the UserManager
     *
     * @param conf A Configuration object to init this Manager
     *
     * @throws InitializationException When something went wrong.
     */
    public void init(Configuration conf) throws InitializationException
    {
        UserPeerManager.init(conf);
    }
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
    public boolean accountExists(User user) throws DataBackendException
    {
        return accountExists(user.getName());
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
    public boolean accountExists(String userName) throws DataBackendException
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
            throw new DataBackendException("Failed to check account's presence", e);
        }
        if (users.size() > 1)
        {
            throw new DataBackendException("Multiple Users with same username '" + userName + "'");
        }
        return (users.size() == 1);
    }
    
    public boolean checkExists(User user) throws DataBackendException{
    	return accountExists(user);
    }
    
	public boolean checkExists(String name) throws DataBackendException{
		return accountExists(name);
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
        Criteria criteria = new Criteria();
        criteria.add(UserPeerManager.getNameColumn(), userName);
        List users;
        try
        {
            users = UserPeerManager.doSelect(criteria);
        }
        catch (Exception e)
        {
            throw new DataBackendException("Failed to retrieve user '" + userName + "'", e);
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
     * Retrieve a list of users that meet the specified criteria.
     *
     * As the keys for the criteria, you should use the constants that
     * are defined in {@link User} interface, plus the names
     * of the custom attributes you added to your user representation
     * in the data storage. Use verbatim names of the attributes -
     * without table name prefix in case of Torque implementation.
     *
     * @param criteria The criteria of selection.
     * @return a List of users meeting the criteria.
     * @throws DataBackendException if there is a problem accessing the
     *         storage.
     */
    public List retrieveList(Criteria criteria) throws DataBackendException
    {
        Iterator keys = criteria.keySet().iterator();
        while (keys.hasNext())
        {
            String key = (String) keys.next();
            // set the table name for all attached criterion
            Criteria.Criterion[] criterion = criteria.getCriterion(key).getAttachedCriterion();
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
    public User getUser(String userName, String password)
        throws PasswordMismatchException, UnknownEntityException, DataBackendException
    {
        User user = getUser(userName);
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
    public void store(User user) throws UnknownEntityException, DataBackendException
    {
        if (!accountExists(user))
        {
            throw new UnknownEntityException("The account '" + user.getName() + "' does not exist");
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
     * Saves User data when the session is unbound. The user account is required
     * to exist in the storage.
     *
     * LastLogin, AccessCounter, persistent pull tools, and any data stored
     * in the permData hashtable that is not mapped to a column will be saved.
     *
     * @exception UnknownEntityException if the user's account does not
     *            exist in the database.
     * @exception DataBackendException if there is a problem accessing the
     *            storage.
     */
    public void saveOnSessionUnbind(User user) throws UnknownEntityException, DataBackendException
    {
        if (!((ClassicTorqueUser) user).hasLoggedIn())
        {
            return;
        }
        //
        // Quinton did some more magic here in the DBSecurityService
        // but I don't think this is necessary, because our Peer will
        // do all the grunt work for us. Maybe: FIXME -- henning
        //
        store(user);
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
        if (!accountExists(user))
        {
            throw new UnknownEntityException("The account '" + user.getName() + "' does not exist");
        }
        // log.debug("Supplied Pass: " + password);
        // log.debug("User Pass: " + user.getPassword());
        /*
         * Unix crypt needs the existing, encrypted password text as
         * salt for checking the supplied password. So we supply it
         * into the checkPassword routine
         */
        if (!TorqueSecurity.checkPassword(password, user.getPassword()))
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
    public void changePassword(User user, String oldPassword, String newPassword)
        throws PasswordMismatchException, UnknownEntityException, DataBackendException
    {
        if (!accountExists(user))
        {
            throw new UnknownEntityException("The account '" + user.getName() + "' does not exist");
        }
        if (!TorqueSecurity.checkPassword(oldPassword, user.getPassword()))
        {
            throw new PasswordMismatchException("The supplied old password for '" + user.getName() + "' was incorrect");
        }
        user.setPassword(TorqueSecurity.encryptPassword(newPassword));
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
    public void forcePassword(User user, String password) throws UnknownEntityException, DataBackendException
    {
        if (!accountExists(user))
        {
            throw new UnknownEntityException("The account '" + user.getName() + "' does not exist");
        }
        user.setPassword(TorqueSecurity.encryptPassword(password));
        // save the changes in the database immediately, to prevent the
        // password being 'reverted' to the old value if the user data
        // is lost somehow before it is saved at session's expiry.
        store(user);
    }
    /**
     * Creates new user account with specified attributes.
     *
     * @param user The object describing account to be created.
     * @param initialPassword the password for the new account
     * @throws DataBackendException if there was an error accessing
     the data backend.
     * @throws EntityExistsException if the user account already exists.
     */
    public void createAccount(User user, String initialPassword) throws EntityExistsException, DataBackendException
    {
        if (StringUtils.isEmpty(user.getName()))
        {
            throw new DataBackendException("Could not create " + "an user with empty name!");
        }
        if (accountExists(user))
        {
            throw new EntityExistsException("The account '" + user.getName() + "' already exists");
        }
        user.setPassword(TorqueSecurity.encryptPassword(initialPassword));
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
            throw new DataBackendException("Failed to create account '" + user.getName() + "'", e);
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
    private void removeAccount(User user) throws UnknownEntityException, DataBackendException
    {
        if (!accountExists(user))
        {
            throw new UnknownEntityException("The account '" + user.getName() + "' does not exist");
        }
        Criteria criteria = new Criteria();
        criteria.add(UserPeerManager.getNameColumn(), user.getName());
        try
        {
            UserPeerManager.doDelete(criteria);
        }
        catch (Exception e)
        {
            throw new DataBackendException("Failed to remove account '" + user.getName() + "'", e);
        }
    }
    /**
    	 * Return a Class object representing the system's chosen implementation of
    	 * of User interface.
    	 *
    	 * @return systems's chosen implementation of User interface.
    	 * @throws UnknownEntityException if the implementation of User interface
    	 *         could not be determined, or does not exist.
    	 */
    public Class getUserClass() throws UnknownEntityException
    {
        if (userClass == null)
        {
            throw new UnknownEntityException("Failed to create a Class object for User implementation");
        }
        return userClass;
    }
    /**
     * Construct a blank User object.
     *
     * This method calls getUserClass, and then creates a new object using
     * the default constructor.
     *
     * @return an object implementing User interface.
     * @throws DataBackendException if the object could not be instantiated.
     */
    public User getUserInstance() throws DataBackendException
    {
        User user;
        try
        {
            user = (User) getUserClass().newInstance();
        }
        catch (Exception e)
        {
            throw new DataBackendException("Failed instantiate an User implementation object", e);
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
     * @throws DataBackendException if the object could not be instantiated.
     */
    public User getUserInstance(String userName) throws DataBackendException
    {
        User user = getUserInstance();
        user.setName(userName);
        return user;
    }
    /**
    	 * Revokes all roles from an User.
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
            lockExclusive();
            userExists = accountExists(user);
            if (userExists)
            {
                // The following would not work, due to an annoying misfeature
                // of Village. Village allows only a single row to be deleted at
                // a time. I wish that it was possible to disable this
                // behaviour!
                // Criteria criteria = new Criteria();
                // criteria.add(UserGroupRolePeer.USER_ID,
                //           ((Persistent) user).getPrimaryKey());
                // UserGroupRolePeer.doDelete(criteria);
                int id = ((NumberKey) ((Persistent) user).getPrimaryKey()).intValue();
                UserGroupRolePeer.deleteAll(UserGroupRolePeer.TABLE_NAME, UserGroupRolePeer.USER_ID, id);
                return;
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException("revokeAll(User) failed", e);
        }
        finally
        {
            unlockExclusive();
        }
        throw new UnknownEntityException("Unknown user '" + user.getName() + "'");
    }
    /**
    	* Saves User's data in the permanent storage. The user account is required
    	* to exist in the storage.
    	*
    	* @param user the User object to save
    	* @throws UnknownEntityException if the user's account does not
    	*         exist in the database.
    	* @throws DataBackendException if there is a problem accessing the storage.
    	*/
    public void saveUser(User user) throws UnknownEntityException, DataBackendException
    {
        store(user);
    }
    /*-----------------------------------------------------------------------
    	 Security management
    	 -----------------------------------------------------------------------*/
    /**
    	* Grant an User a Role in a Group.
    	*
    	* @param user the user.
    	* @param group the group.
    	* @param role the role.
    	* @throws DataBackendException if there was an error accessing the data
    	*         backend.
    	* @throws UnknownEntityException if user account, group or role is not
    	*         present.
    	*/
    public synchronized void grant(User user, Group group, Role role)
        throws DataBackendException, UnknownEntityException
    {
        boolean userExists = false;
        boolean groupExists = false;
        boolean roleExists = false;
        try
        {
            lockExclusive();
            userExists = accountExists(user);
            groupExists = checkExists(group);
            roleExists = checkExists(role);
            if (userExists && groupExists && roleExists)
            {
                Criteria criteria = new Criteria();
                criteria.add(UserGroupRolePeer.USER_ID, ((Persistent) user).getPrimaryKey());
                criteria.add(UserGroupRolePeer.GROUP_ID, ((Persistent) group).getPrimaryKey());
                criteria.add(UserGroupRolePeer.ROLE_ID, ((Persistent) role).getPrimaryKey());
                UserGroupRolePeer.doInsert(criteria);
                return;
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException("grant(User,Group,Role) failed", e);
        }
        finally
        {
            unlockExclusive();
        }
        if (!userExists)
        {
            throw new UnknownEntityException("Unknown user '" + user.getName() + "'");
        }
        if (!groupExists)
        {
            throw new UnknownEntityException("Unknown group '" + group.getName() + "'");
        }
        if (!roleExists)
        {
            throw new UnknownEntityException("Unknown role '" + role.getName() + "'");
        }
    }
    /**
    	* Revoke a Role in a Group from an User.
    	*
    	* @param user the user.
    	* @param group the group.
    	* @param role the role.
    	* @throws DataBackendException if there was an error accessing the data
    	*         backend.
    	* @throws UnknownEntityException if user account, group or role is not
    	*         present.
    	*/
    public synchronized void revoke(User user, Group group, Role role)
        throws DataBackendException, UnknownEntityException
    {
        boolean userExists = false;
        boolean groupExists = false;
        boolean roleExists = false;
        try
        {
            lockExclusive();
            userExists = accountExists(user);
            groupExists = checkExists(group);
            roleExists = checkExists(role);
            if (userExists && groupExists && roleExists)
            {
                Criteria criteria = new Criteria();
                criteria.add(UserGroupRolePeer.USER_ID, ((Persistent) user).getPrimaryKey());
                criteria.add(UserGroupRolePeer.GROUP_ID, ((Persistent) group).getPrimaryKey());
                criteria.add(UserGroupRolePeer.ROLE_ID, ((Persistent) role).getPrimaryKey());
                UserGroupRolePeer.doDelete(criteria);
                return;
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException("revoke(User,Role,Group) failed", e);
        }
        finally
        {
            unlockExclusive();
        }
        if (!userExists)
        {
            throw new UnknownEntityException("Unknown user '" + user.getName() + "'");
        }
        if (!groupExists)
        {
            throw new UnknownEntityException("Unknown group '" + group.getName() + "'");
        }
        if (!roleExists)
        {
            throw new UnknownEntityException("Unknown role '" + role.getName() + "'");
        }
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
    protected boolean checkExists(Group group) throws DataBackendException, Exception
    {
        return GroupPeerManager.checkExists(group);
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
    protected boolean checkExists(Role role) throws DataBackendException, Exception
    {
        return RolePeerManager.checkExists(role);
    }
    /**
    	* This method provides client-side encryption of passwords.
    	*
    	* If <code>secure.passwords</code> are enabled in TurbineResources,
    	* the password will be encrypted, if not, it will be returned unchanged.
    	* The <code>secure.passwords.algorithm</code> property can be used
    	* to chose which digest algorithm should be used for performing the
    	* encryption. <code>SHA</code> is used by default.
    	*
    	* @param password the password to process
    	* @return processed password
    	*/
    public String encryptPassword(String password)
    {
        return encryptPassword(password, null);
    }
    /**
    	* This method provides client-side encryption of passwords.
    	*
    	* If <code>secure.passwords</code> are enabled in TurbineResources,
    	* the password will be encrypted, if not, it will be returned unchanged.
    	* The <code>secure.passwords.algorithm</code> property can be used
    	* to chose which digest algorithm should be used for performing the
    	* encryption. <code>SHA</code> is used by default.
    	*
    	* The used algorithms must be prepared to accept null as a
    	* valid parameter for salt. All algorithms in the Fulcrum Cryptoservice
    	* accept this.
    	*
    	* @param password the password to process
    	* @param salt     algorithms that needs a salt can provide one here
    	* @return processed password
    	*/
    public String encryptPassword(String password, String salt)
    {
        //@todo need to tie into password utils.
        return null;
    }
    /**
    	* Checks if a supplied password matches the encrypted password
    	*
    	* @param checkpw      The clear text password supplied by the user
    	* @param encpw        The current, encrypted password
    	*
    	* @return true if the password matches, else false
    	*
    	*/
    public boolean checkPassword(String checkpw, String encpw)
    {
        String result = encryptPassword(checkpw, encpw);
        return (result == null) ? false : result.equals(encpw);
    }
    /**
    	* Retrieve a set of users that meet the specified criteria.
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
    public List getUserList(Criteria criteria) throws DataBackendException
    {
        try
        {
            return TorqueUserPeer.doSelect(criteria);
        }
        catch (TorqueException te)
        {
            throw new DataBackendException("Problem with getUserList()",te);
        }
    }
    
	/**
	   * Constructs an User object to represent an anonymous user of the
	   * application.
	   *
	   * @return An anonymous Turbine User.
	   * @throws UnknownEntityException if the implementation of User interface
	   *         could not be determined, or does not exist.
	   */
	  public User getAnonymousUser()
			  throws UnknownEntityException
	  {
		  User user;
	      try {
		   user = getUserInstance();
	      }
	      catch (DataBackendException dbe){
	          throw new UnknownEntityException("Coudl not create an anonymous user.",dbe);
	      }
		  user.setName("");
		  return user;
	  }

	  /**
	   * Checks whether a passed user object matches the anonymous user pattern
	   * according to the configured user manager
	   *
	   * @param user An user object
	   *
	   * @return True if this is an anonymous user
	   *
	   */
	  public boolean isAnonymousUser(User user)
	  {
		  // Either just null, the name is null or the name is the empty string
		  return (user == null) || StringUtils.isEmpty(user.getName());
	  }
	  
	/**
	  * Return a Class object representing the system's chosen implementation of
	  * of ACL interface.
	  *
	  * @return systems's chosen implementation of ACL interface.
	  * @throws UnknownEntityException if the implementation of ACL interface
	  *         could not be determined, or does not exist.
	  */
	 public Class getAclClass()
			 throws UnknownEntityException
	 {
		 if (aclClass == null)
		 {
			 throw new UnknownEntityException(
					 "Failed to create a Class object for ACL implementation");
		 }
		 return aclClass;
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
	 public AccessControlList getAclInstance(Map roles, Map permissions)
			 throws UnknownEntityException
	 {
		 Object[] objects = {roles, permissions};
		 String[] signatures = {Map.class.getName(), Map.class.getName()};
		 AccessControlList accessControlList;

		 try
		 {
			 accessControlList =
					 (AccessControlList) aclFactoryService.getInstance(aclClass.getName(),
							 objects,
							 signatures);
		 }
		 catch (Exception e)
		 {
			 throw new UnknownEntityException(
					 "Failed to instantiate an ACL implementation object", e);
		 }

		 return accessControlList;
	 }
	 
	 public AccessControlList getACL(User user){
	 	// @todo need to finish
	 	return null;	
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
		   // revoke all roles form the user
		   revokeAll(user);

		   removeAccount(user);
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
	  public void addUser(User user, String password)
			  throws DataBackendException, EntityExistsException
	  {
		  createAccount(user, password);
	  }

}
