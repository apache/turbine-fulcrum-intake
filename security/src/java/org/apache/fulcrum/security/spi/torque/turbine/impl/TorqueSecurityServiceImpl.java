package org.apache.fulcrum.security.spi.torque.turbine.impl;
/*
 * ==================================================================== The
 * Apache Software License, Version 1.1
 * 
 * Copyright (c) 2001-2003 The Apache Software Foundation. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 1.
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. The end-user documentation
 * included with the redistribution, if any, must include the following
 * acknowledgment: "This product includes software developed by the Apache
 * Software Foundation (http://www.apache.org/)." Alternately, this
 * acknowledgment may appear in the software itself, if and wherever such
 * third-party acknowledgments normally appear. 4. The names "Apache" and
 * "Apache Software Foundation" and "Apache Turbine" must not be used to
 * endorse or promote products derived from this software without prior written
 * permission. For written permission, please contact apache@apache.org. 5.
 * Products derived from this software may not be called "Apache", "Apache
 * Turbine", nor may "Apache" appear in their name, without prior written
 * permission of the Apache Software Foundation.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * APACHE SOFTWARE FOUNDATION OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 * 
 * This software consists of voluntary contributions made by many individuals
 * on behalf of the Apache Software Foundation. For more information on the
 * Apache Software Foundation, please see <http://www.apache.org/> .
 */
import java.util.Map;
import org.apache.fulcrum.security.BaseSecurityService;
import org.apache.fulcrum.security.acl.AccessControlList;
import org.apache.fulcrum.security.entity.Group;
import org.apache.fulcrum.security.entity.Permission;
import org.apache.fulcrum.security.entity.Role;
import org.apache.fulcrum.security.entity.User;
import org.apache.fulcrum.security.model.turbine.manager.TurbineRoleManager;
import org.apache.fulcrum.security.spi.torque.turbine.TorqueGroupManager;
import org.apache.fulcrum.security.spi.torque.turbine.TorquePermissionManager;
import org.apache.fulcrum.security.spi.torque.turbine.TorqueRoleManager;
import org.apache.fulcrum.security.spi.torque.turbine.TorqueSecurityService;
import org.apache.fulcrum.security.spi.torque.turbine.TorqueUserManager;
import org.apache.fulcrum.security.util.DataBackendException;
import org.apache.fulcrum.security.util.EntityExistsException;
import org.apache.fulcrum.security.util.GroupSet;
import org.apache.fulcrum.security.util.PasswordMismatchException;
import org.apache.fulcrum.security.util.PermissionSet;
import org.apache.fulcrum.security.util.RoleSet;
import org.apache.fulcrum.security.util.UnknownEntityException;
import org.apache.torque.util.Criteria;
/**
 * The Security Service manages Users, Groups Roles and Permissions in the
 * system.
 * 
 * The task performed by the security service include creation and removal of
 * accounts, groups, roles, and permissions; assigning users roles in groups;
 * assigning roles specific permissions and construction of objects
 * representing these logical entities.
 * 
 * <p>
 * Because of pluggable nature of the Services, it is possible to create
 * multiple implementations of SecurityService, for example employing database
 * and directory server as the data backend. <br>
 * 
 * @author <a href="mailto:Rafal.Krzewski@e-point.pl">Rafal Krzewski</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:marco@intermeta.de">Marco Kn&uuml;ttel</a>
 * @version $Id: TorqueSecurityServiceImpl.java,v 1.2 2003/08/26 01:57:28 epugh
 *          Exp $
 */
public class TorqueSecurityServiceImpl
    extends BaseSecurityService
    implements TorqueSecurityService
{
    /*
	 * -----------------------------------------------------------------------
	 * Management of User objects
	 */
    /**
	 * Returns the Class object for the implementation of User interface used
	 * by the system.
	 * 
	 * @return the implementation of User interface used by the system.
	 * @throws UnknownEntityException if the system's implementation of User
	 *             interface could not be determined.
	 */
    public Class getUserClass() throws UnknownEntityException
    {
        return ((TorqueUserManager) getUserManager()).getUserClass();
    }
    /**
	 * Construct a blank User object.
	 * 
	 * This method calls getUserClass, and then creates a new object using the
	 * default constructor.
	 * 
	 * @return an object implementing User interface.
	 * @throws UnknownEntityException if the object could not be instantiated.
	 */
    public User getUserInstance()
    {
        return null;
    }
    /**
	 * Construct a blank User object.
	 * 
	 * This method calls getUserClass, and then creates a new object using the
	 * default constructor.
	 * 
	 * @param userName The name of the user.
	 * 
	 * @return an object implementing User interface.
	 * @throws UnknownEntityException if the object could not be instantiated.
	 */
    public User getUserInstance(String userName) throws UnknownEntityException
    {
        try
        {
            return ((TorqueUserManager) getUserManager()).getUserInstance(
                userName);
        }
        catch (DataBackendException dbe)
        {
            throw new UnknownEntityException(dbe.getMessage(), dbe);
        }
    }
    /**
	 * Returns the Class object for the implementation of Group interface used
	 * by the system.
	 * 
	 * @return the implementation of Group interface used by the system.
	 * @throws UnknownEntityException if the system's implementation of Group
	 *             interface could not be determined.
	 */
    public Class getGroupClass() throws UnknownEntityException
    {
        return ((TorqueGroupManager) getGroupManager()).getGroupClass();
    }
    /**
	 * Construct a blank Group object.
	 * 
	 * This method calls getGroupClass, and then creates a new object using the
	 * default constructor.
	 * 
	 * @return an object implementing Group interface.
	 * @throws UnknownEntityException if the object could not be instantiated.
	 */
    public Group getGroupInstance() throws UnknownEntityException
    {
        try
        {
            return getGroupManager().getGroupInstance();
        }
        catch (DataBackendException dbe)
        {
            throw new UnknownEntityException(dbe.getMessage(), dbe);
        }
    }
    /**
	 * Construct a blank Group object.
	 * 
	 * This method calls getGroupClass, and then creates a new object using the
	 * default constructor.
	 * 
	 * @param groupName The name of the Group
	 * 
	 * @return an object implementing Group interface.
	 * @throws UnknownEntityException if the object could not be instantiated.
	 */
    public Group getGroupInstance(String groupName)
        throws UnknownEntityException
    {
        try
        {
            return getGroupManager().getGroupInstance(groupName);
        }
        catch (DataBackendException dbe)
        {
            throw new UnknownEntityException(dbe.getMessage(), dbe);
        }
    }
    /**
	 * Returns the Class object for the implementation of Permission interface
	 * used by the system.
	 * 
	 * @return the implementation of Permission interface used by the system.
	 * @throws UnknownEntityException if the system's implementation of
	 *             Permission interface could not be determined.
	 */
    public Class getPermissionClass() throws UnknownEntityException
    {
        return ((TorquePermissionManager) getPermissionManager())
            .getPermissionClass();
    }
    /**
	 * Construct a blank Permission object.
	 * 
	 * This method calls getPermissionClass, and then creates a new object
	 * using the default constructor.
	 * 
	 * @return an object implementing Permission interface.
	 * @throws UnknownEntityException if the object could not be instantiated.
	 */
    public Permission getPermissionInstance() throws UnknownEntityException
    {
        return getPermissionManager().getPermissionInstance();
    }
    /**
	 * Construct a blank Permission object.
	 * 
	 * This method calls getPermissionClass, and then creates a new object
	 * using the default constructor.
	 * 
	 * @param permName The name of the Permission
	 * 
	 * @return an object implementing Permission interface.
	 * @throws UnknownEntityException if the object could not be instantiated.
	 */
    public Permission getPermissionInstance(String permName)
        throws UnknownEntityException
    {
        return getPermissionManager().getPermissionInstance(permName);
    }
    /**
	 * Returns the Class object for the implementation of Role interface used
	 * by the system.
	 * 
	 * @return the implementation of Role interface used by the system.
	 * @throws UnknownEntityException if the system's implementation of Role
	 *             interface could not be determined.
	 */
    public Class getRoleClass() throws UnknownEntityException
    {
        return ((TorqueRoleManager) getRoleManager()).getRoleClass();
    }
    /**
	 * Construct a blank Role object.
	 * 
	 * This method calls getRoleClass, and then creates a new object using the
	 * default constructor.
	 * 
	 * @return an object implementing Role interface.
	 * @throws UnknownEntityException if the object could not be instantiated.
	 */
    public Role getRoleInstance() throws UnknownEntityException
    {
        try
        {
            return getRoleManager().getRoleInstance();
        }
        catch (DataBackendException dbe)
        {
            throw new UnknownEntityException(dbe.getMessage(), dbe);
        }
    }
    /**
	 * Construct a blank Role object.
	 * 
	 * This method calls getRoleClass, and then creates a new object using the
	 * default constructor.
	 * 
	 * @param roleName The name of the Role
	 * 
	 * @return an object implementing Role interface.
	 * @throws UnknownEntityException if the object could not be instantiated.
	 */
    public Role getRoleInstance(String roleName) throws UnknownEntityException
    {
        try
        {
            return getRoleManager().getRoleInstance(roleName);
        }
        catch (DataBackendException dbe)
        {
            throw new UnknownEntityException(dbe.getMessage(), dbe);
        }
    }
    /**
	 * Returns the Class object for the implementation of AccessControlList
	 * interface used by the system.
	 * 
	 * @return the implementation of AccessControlList interface used by the
	 *         system.
	 * @throws UnknownEntityException if the system's implementation of
	 *             AccessControlList interface could not be determined.
	 */
    public Class getAclClass() throws UnknownEntityException
    {
        return ((TorqueUserManager) getUserManager()).getAclClass();
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
        return ((TorqueUserManager) getUserManager()).getAclInstance(
            roles,
            permissions);
    }
    /**
	 * Constructs an User object to represent an anonymous user of the
	 * application.
	 * 
	 * @return An anonymous Turbine User.
	 * @throws UnknownEntityException if the anonymous User object couldn't be
	 *             constructed.
	 */
    public User getAnonymousUser() throws UnknownEntityException
    {
        return ((TorqueUserManager) getUserManager()).getAnonymousUser();
    }
    /**
	 * Checks whether a passed user object matches the anonymous user pattern
	 * according to the configured user manager
	 * 
	 * @param An user object
	 * 
	 * @return True if this is an anonymous user
	 *  
	 */
    public boolean isAnonymousUser(User u)
    {
        return ((TorqueUserManager) getUserManager()).isAnonymousUser(u);
    }
    /**
	 * Saves User's data in the permanent storage. The user account is required
	 * to exist in the storage.
	 * 
	 * @param user The user object to save.
	 * @throws UnknownEntityException if the user's account does not exist in
	 *             the database.
	 * @throws DataBackendException if there is a problem accessing the
	 *             storage.
	 */
    public void saveUser(User user)
        throws UnknownEntityException, DataBackendException
    {
        getUserManager().saveUser(user);
    }
    /**
	 * Saves User data when the session is unbound. The user account is
	 * required to exist in the storage.
	 * 
	 * LastLogin, AccessCounter, persistent pull tools, and any data stored in
	 * the permData hashtable that is not mapped to a column will be saved.
	 * 
	 * @exception UnknownEntityException if the user's account does not exist
	 *                in the database.
	 * @exception DataBackendException if there is a problem accessing the
	 *                storage.
	 */
    public void saveOnSessionUnbind(User user)
        throws UnknownEntityException, DataBackendException
    {
        ((TorqueUserManager) getUserManager()).saveOnSessionUnbind(user);
    }
    /*
	 * -----------------------------------------------------------------------
	 * Account management
	 */
    /**
	 * Creates new user account with specified attributes.
	 * 
	 * @param user the object describing account to be created.
	 * @param password The password to use.
	 * 
	 * @throws DataBackendException if there was an error accessing the data
	 *             backend.
	 * @throws EntityExistsException if the user account already exists.
	 */
    public void addUser(User user, String password)
        throws DataBackendException, EntityExistsException
    {
        ((TorqueUserManager) getUserManager()).addUser(user, password);
    }
    /**
	 * Removes an user account from the system.
	 * 
	 * @param user the object describing the account to be removed.
	 * @throws DataBackendException if there was an error accessing the data
	 *             backend.
	 * @throws UnknownEntityException if the user account is not present.
	 */
    public void removeUser(User user)
        throws DataBackendException, UnknownEntityException
    {
        getUserManager().removeUser(user);
    }
    /*
	 * -----------------------------------------------------------------------
	 * Management of passwords
	 */
    /**
	 * This method provides client-side encryption mechanism for passwords.
	 * 
	 * This is an utility method that is used by other classes to maintain a
	 * consistent approach to encrypting password. The behavior of the method
	 * can be configured in service's properties.
	 * 
	 * @param password the password to process
	 * @return processed password
	 */
    public String encryptPassword(String password)
    {
        return ((TorqueUserManager) getUserManager()).encryptPassword(password);
    }
    /**
	 * This method provides client-side encryption mechanism for passwords.
	 * 
	 * This is an utility method that is used by other classes to maintain a
	 * consistent approach to encrypting password. The behavior of the method
	 * can be configured in service's properties.
	 * 
	 * Algorithms that must supply a salt for encryption can use this method to
	 * provide it.
	 * 
	 * @param password the password to process
	 * @param salt Salt parameter for some crypto algorithms
	 * 
	 * @return processed password
	 */
    public String encryptPassword(String password, String salt)
    {
        return ((TorqueUserManager) getUserManager()).encryptPassword(
            password,
            salt);
    }
    /**
	 * Checks if a supplied password matches the encrypted password when using
	 * the current encryption algorithm
	 * 
	 * @param checkpw The clear text password supplied by the user
	 * @param encpw The current, encrypted password
	 * 
	 * @return true if the password matches, else false
	 *  
	 */
    public boolean checkPassword(String checkpw, String encpw)
    {
        return ((TorqueUserManager) getUserManager()).checkPassword(
            checkpw,
            encpw);
    }
    /**
	 * Change the password for an User.
	 * 
	 * @param user an User to change password for.
	 * @param oldPassword the current password supplied by the user.
	 * @param newPassword the current password requested by the user.
	 * @exception PasswordMismatchException if the supplied password was
	 *                incorrect.
	 * @exception UnknownEntityException if the user's record does not exist in
	 *                the database.
	 * @exception DataBackendException if there is a problem accessing the
	 *                storage.
	 */
    public void changePassword(
        User user,
        String oldPassword,
        String newPassword)
        throws PasswordMismatchException, UnknownEntityException, DataBackendException
    {
        ((TorqueUserManager) getUserManager()).changePassword(
            user,
            oldPassword,
            newPassword);
    }
    /**
	 * Forcibly sets new password for an User.
	 * 
	 * This is supposed by the administrator to change the forgotten or
	 * compromised passwords. Certain implementatations of this feature would
	 * require administrative level access to the authenticating server /
	 * program.
	 * 
	 * @param user an User to change password for.
	 * @param password the new password.
	 * @exception UnknownEntityException if the user's record does not exist in
	 *                the database.
	 * @exception DataBackendException if there is a problem accessing the
	 *                storage.
	 */
    public void forcePassword(User user, String password)
        throws UnknownEntityException, DataBackendException
    {
        ((TorqueUserManager) getUserManager()).forcePassword(user, password);
    }
    /*
	 * -----------------------------------------------------------------------
	 * Retrieval of security information
	 */
    /**
	 * Constructs an AccessControlList for a specific user.
	 * 
	 * @param user the user for whom the AccessControlList are to be retrieved
	 * 
	 * @return A new AccessControlList object.
	 * 
	 * @throws DataBackendException if there was an error accessing the data
	 *             backend.
	 * @throws UnknownEntityException if user account is not present.
	 */
    public AccessControlList getACL(User user)
        throws DataBackendException, UnknownEntityException
    {
        return getUserManager().getACL(user);
    }
    /**
	 * Retrieves all permissions associated with a role.
	 * 
	 * @param role the role name, for which the permissions are to be
	 *            retrieved.
	 * 
	 * @return A Permission set for the Role.
	 * 
	 * @throws DataBackendException if there was an error accessing the data
	 *             backend.
	 * @throws UnknownEntityException if the role is not present.
	 */
    public PermissionSet getPermissions(Role role)
        throws DataBackendException, UnknownEntityException
    {
        return getPermissionManager().getPermissions(role);
    }
    /*
	 * -----------------------------------------------------------------------
	 * Manipulation of security information
	 */
    /**
	 * Grant an User a Role in a Group.
	 * 
	 * @param user the user.
	 * @param group the group.
	 * @param role the role.
	 * @throws DataBackendException if there was an error accessing the data
	 *             backend.
	 * @throws UnknownEntityException if user account, group or role is not
	 *             present.
	 */
    public void grant(User user, Group group, Role role)
        throws DataBackendException, UnknownEntityException
    {
        ((TorqueUserManager) getUserManager()).grant(user, group, role);
    }
    /**
	 * Revoke a Role in a Group from an User.
	 * 
	 * @param user the user.
	 * @param group the group.
	 * @param role the role.
	 * @throws DataBackendException if there was an error accessing the data
	 *             backend.
	 * @throws UnknownEntityException if user account, group or role is not
	 *             present.
	 */
    public void revoke(User user, Group group, Role role)
        throws DataBackendException, UnknownEntityException
    {
        ((TorqueUserManager) getUserManager()).revoke(user, group, role);
    }
    /**
	 * Revokes all roles from an User.
	 * 
	 * This method is used when deleting an account.
	 * 
	 * @param user the User.
	 * @throws DataBackendException if there was an error accessing the data
	 *             backend.
	 * @throws UnknownEntityException if the account is not present.
	 */
    public void revokeAll(User user)
        throws DataBackendException, UnknownEntityException
    {
        getUserManager().revokeAll(user);
    }
    /**
	 * Grants a Role a Permission
	 * 
	 * @param role the Role.
	 * @param permission the Permission.
	 * @throws DataBackendException if there was an error accessing the data
	 *             backend.
	 * @throws UnknownEntityException if role or permission is not present.
	 */
    public void grant(Role role, Permission permission)
        throws DataBackendException, UnknownEntityException
    {
        ((TurbineRoleManager) getRoleManager()).grant(role, permission);
    }
    /**
	 * Revokes a Permission from a Role.
	 * 
	 * @param role the Role.
	 * @param permission the Permission.
	 * @throws DataBackendException if there was an error accessing the data
	 *             backend.
	 * @throws UnknownEntityException if role or permission is not present.
	 */
    public void revoke(Role role, Permission permission)
        throws DataBackendException, UnknownEntityException
    {
        ((TurbineRoleManager) getRoleManager()).revoke(role, permission);
    }
    /**
	 * Revokes all permissions from a Role.
	 * 
	 * This method is user when deleting a Role.
	 * 
	 * @param role the Role
	 * @throws DataBackendException if there was an error accessing the data
	 *             backend.
	 * @throws UnknownEntityException if the Role is not present.
	 */
    public void revokeAll(Role role)
        throws DataBackendException, UnknownEntityException
    {
        getRoleManager().revokeAll(role);
    }
    /*
	 * -----------------------------------------------------------------------
	 * Retrieval & storage of SecurityObjects
	 */
    /**
	 * Provides a reference to the Group object that represents the
	 * <a href="#global">global group</a>.
	 * 
	 * @return A Group object that represents the global group.
	 */
    public Group getGlobalGroup() throws DataBackendException
    {
        return ((TorqueGroupManager) getGroupManager()).getGlobalGroup();
    }
    /**
	 * Retrieve a Group object with specified name.
	 * 
	 * @param name the name of the Group.
	 * 
	 * @return an object representing the Group with specified name.
	 * 
	 * @exception UnknownEntityException if the permission does not exist in
	 *                the database.
	 * @exception DataBackendException if there is a problem accessing the
	 *                storage.
	 */
    public Group getGroup(String name)
        throws DataBackendException, UnknownEntityException
    {
        return getGroupManager().getGroupByName(name);
    }
    /**
	 * Retrieve a Group object with specified name.
	 * 
	 * @param name the name of the Group.
	 * @return an object representing the Group with specified name.
	 * @throws DataBackendException if there was an error accessing the data
	 *             backend.
	 * @throws UnknownEntityException if the group does not exist.
	 */
    public Group getGroupByName(String name)
        throws DataBackendException, UnknownEntityException
    {
        return getGroupManager().getGroupByName(name);
    }
    /**
	 * Retrieve a Group object with specified Id.
	 * 
	 * @param name the name of the Group.
	 * 
	 * @return an object representing the Group with specified name.
	 * 
	 * @exception UnknownEntityException if the permission does not exist in
	 *                the database.
	 * @exception DataBackendException if there is a problem accessing the
	 *                storage.
	 */
    public Group getGroupById(Object id)
        throws DataBackendException, UnknownEntityException
    {
        return getGroupManager().getGroupById(id);
    }
    /**
	 * Retrieve a Role object with specified name.
	 * 
	 * @param name the name of the Role.
	 * @return an object representing the Role with specified name.
	 * @throws DataBackendException if there was an error accessing the data
	 *             backend.
	 * @throws UnknownEntityException if the role does not exist.
	 */
    public Role getRoleByName(String name)
        throws DataBackendException, UnknownEntityException
    {
        return getRoleManager().getRoleByName(name);
    }
    /**
	 * Retrieve a Role object with specified Id.
	 * 
	 * @param name the name of the Role.
	 * 
	 * @return an object representing the Role with specified name.
	 * 
	 * @exception UnknownEntityException if the permission does not exist in
	 *                the database.
	 * @exception DataBackendException if there is a problem accessing the
	 *                storage.
	 */
    public Role getRoleById(Object id)
        throws DataBackendException, UnknownEntityException
    {
        return getRoleManager().getRoleById(id);
    }
    /**
	 * Retrieve a Permission object with specified name.
	 * 
	 * @param name the name of the Permission.
	 * @return an object representing the Permission with specified name.
	 * @throws DataBackendException if there was an error accessing the data
	 *             backend.
	 * @throws UnknownEntityException if the permission does not exist.
	 */
    public Permission getPermissionByName(String name)
        throws DataBackendException, UnknownEntityException
    {
        return getPermissionManager().getPermissionByName(name);
    }
    /**
	 * Retrieve a Permission object with specified Id.
	 * 
	 * @param name the name of the Permission.
	 * 
	 * @return an object representing the Permission with specified name.
	 * 
	 * @exception UnknownEntityException if the permission does not exist in
	 *                the database.
	 * @exception DataBackendException if there is a problem accessing the
	 *                storage.
	 */
    public Permission getPermissionById(Object id)
        throws DataBackendException, UnknownEntityException
    {
        return getPermissionManager().getPermissionById(id);
    }
    /**
	 * Retrieve a set of Groups that meet the specified Criteria.
	 * 
	 * @param criteria a Criteria of Group selection.
	 * @return a set of Groups that meet the specified Criteria.
	 * 
	 * @exception DataBackendException if there is a problem accessing the
	 *                storage.
	 */
    public GroupSet getGroups(Criteria criteria) throws DataBackendException
    {
        return ((TorqueGroupManager) getGroupManager()).getGroups(criteria);
    }
    /**
	 * Retrieve a set of Roles that meet the specified Criteria.
	 * 
	 * @param criteria a Criteria of Roles selection.
	 * @return a set of Roles that meet the specified Criteria.
	 * 
	 * @exception DataBackendException if there is a problem accessing the
	 *                storage.
	 */
    public RoleSet getRoles(Criteria criteria) throws DataBackendException
    {
        return ((TorqueRoleManager) getRoleManager()).getRoles(criteria);
    }
    /**
	 * Retrieve a set of Permissions that meet the specified Criteria.
	 * 
	 * @param criteria a Criteria of Permissions selection.
	 * @return a set of Permissions that meet the specified Criteria.
	 * 
	 * @exception DataBackendException if there is a problem accessing the
	 *                storage.
	 */
    public PermissionSet getPermissions(Criteria criteria)
        throws DataBackendException
    {
        return (
            (TorquePermissionManager) getPermissionManager()).getPermissions(
            criteria);
    }
    /**
	 * Retrieves all groups defined in the system.
	 * 
	 * @return the names of all groups defined in the system.
	 * @throws DataBackendException if there was an error accessing the data
	 *             backend.
	 */
    public GroupSet getAllGroups() throws DataBackendException
    {
        return getGroupManager().getAllGroups();
    }
    /**
	 * Retrieves all roles defined in the system.
	 * 
	 * @return the names of all roles defined in the system.
	 * @throws DataBackendException if there was an error accessing the data
	 *             backend.
	 */
    public RoleSet getAllRoles() throws DataBackendException
    {
        return getRoleManager().getAllRoles();
    }
    /**
	 * Retrieves all permissions defined in the system.
	 * 
	 * @return the names of all roles defined in the system.
	 * @throws DataBackendException if there was an error accessing the data
	 *             backend.
	 */
    public PermissionSet getAllPermissions() throws DataBackendException
    {
        return getPermissionManager().getAllPermissions();
    }
    /**
	 * Stores Group's attributes. The Groups is required to exist in the
	 * system.
	 * 
	 * @param group The Group to be stored.
	 * @throws DataBackendException if there was an error accessing the data
	 *             backend.
	 * @throws UnknownEntityException if the group does not exist.
	 */
    public void saveGroup(Group group)
        throws DataBackendException, UnknownEntityException
    {
        ((TorqueGroupManager) getGroupManager()).saveGroup(group);
    }
    /**
	 * Stores Role's attributes. The Roles is required to exist in the system.
	 * 
	 * @param role The Role to be stored.
	 * @throws DataBackendException if there was an error accessing the data
	 *             backend.
	 * @throws UnknownEntityException if the role does not exist.
	 */
    public void saveRole(Role role)
        throws DataBackendException, UnknownEntityException
    {
        ((TorqueRoleManager) getRoleManager()).saveRole(role);
    }
    /**
	 * Stores Permission's attributes. The Permissions is required to exist in
	 * the system.
	 * 
	 * @param permission The Permission to be stored.
	 * @throws DataBackendException if there was an error accessing the data
	 *             backend.
	 * @throws UnknownEntityException if the permission does not exist.
	 */
    public void savePermission(Permission permission)
        throws DataBackendException, UnknownEntityException
    {
        TorquePermissionManager tpm =
            (TorquePermissionManager) getPermissionManager();
        tpm.savePermission(permission);
    }
    /*
	 * -----------------------------------------------------------------------
	 * Group/Role/Permission management
	 */
    /**
	 * Creates a new group with specified attributes.
	 * 
	 * @param group the object describing the group to be created.
	 * @return the new Group object.
	 * @throws DataBackendException if there was an error accessing the data
	 *             backend.
	 * @throws EntityExistsException if the group already exists.
	 */
    public Group addGroup(Group group)
        throws DataBackendException, EntityExistsException
    {
        return getGroupManager().addGroup(group);
    }
    /**
	 * Creates a new role with specified attributes.
	 * 
	 * @param role The object describing the role to be created.
	 * @return the new Role object.
	 * @throws DataBackendException if there was an error accessing the data
	 *             backend.
	 * @throws EntityExistsException if the role already exists.
	 */
    public Role addRole(Role role)
        throws DataBackendException, EntityExistsException
    {
        return getRoleManager().addRole(role);
    }
    /**
	 * Creates a new permission with specified attributes.
	 * 
	 * @param permission The object describing the permission to be created.
	 * @return the new Permission object.
	 * @throws DataBackendException if there was an error accessing the data
	 *             backend.
	 * @throws EntityExistsException if the permission already exists.
	 */
    public Permission addPermission(Permission permission)
        throws DataBackendException, EntityExistsException
    {
        return getPermissionManager().addPermission(permission);
    }
    /**
	 * Removes a Group from the system.
	 * 
	 * @param group The object describing group to be removed.
	 * @throws DataBackendException if there was an error accessing the data
	 *             backend.
	 * @throws UnknownEntityException if the group does not exist.
	 */
    public void removeGroup(Group group)
        throws DataBackendException, UnknownEntityException
    {
        getGroupManager().removeGroup(group);
    }
    /**
	 * Removes a Role from the system.
	 * 
	 * @param role The object describing role to be removed.
	 * @throws DataBackendException if there was an error accessing the data
	 *             backend.
	 * @throws UnknownEntityException if the role does not exist.
	 */
    public void removeRole(Role role)
        throws DataBackendException, UnknownEntityException
    {
        getRoleManager().removeRole(role);
    }
    /**
	 * Removes a Permission from the system.
	 * 
	 * @param permission The object describing permission to be removed.
	 * @throws DataBackendException if there was an error accessing the data
	 *             backend.
	 * @throws UnknownEntityException if the permission does not exist.
	 */
    public void removePermission(Permission permission)
        throws DataBackendException, UnknownEntityException
    {
        getPermissionManager().removePermission(permission);
    }
    /**
	 * Renames an existing Group.
	 * 
	 * @param group The object describing the group to be renamed.
	 * @param name the new name for the group.
	 * @throws DataBackendException if there was an error accessing the data
	 *             backend.
	 * @throws UnknownEntityException if the group does not exist.
	 */
    public void renameGroup(Group group, String name)
        throws DataBackendException, UnknownEntityException
    {
        getGroupManager().renameGroup(group, name);
    }
    /**
	 * Renames an existing Role.
	 * 
	 * @param role The object describing the role to be renamed.
	 * @param name the new name for the role.
	 * @throws DataBackendException if there was an error accessing the data
	 *             backend.
	 * @throws UnknownEntityException if the role does not exist.
	 */
    public void renameRole(Role role, String name)
        throws DataBackendException, UnknownEntityException
    {
        getRoleManager().renameRole(role, name);
    }
    /**
	 * Renames an existing Permission.
	 * 
	 * @param permission The object describing the permission to be renamed.
	 * @param name the new name for the permission.
	 * @throws DataBackendException if there was an error accessing the data
	 *             backend.
	 * @throws UnknownEntityException if the permission does not exist.
	 */
    public void renamePermission(Permission permission, String name)
        throws DataBackendException, UnknownEntityException
    {
        getPermissionManager().renamePermission(permission, name);
    }
}
