package org.apache.fulcrum.security;
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
 * 3. The end-group documentation included with the redistribution,
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
import org.apache.avalon.framework.component.Component;
import org.apache.fulcrum.security.entity.Permission;
import org.apache.fulcrum.security.entity.Role;
import org.apache.fulcrum.security.util.DataBackendException;
import org.apache.fulcrum.security.util.EntityExistsException;
import org.apache.fulcrum.security.util.PermissionSet;
import org.apache.fulcrum.security.util.UnknownEntityException;
/**
 * An GroupManager performs {@link org.apache.fulcrum.security.entity.Group} objects
 * related tasks on behalf of the {@link org.apache.fulcrum.security.BaseSecurityService}.
 *
 * The responsibilities of this class include loading data of an group from the
 * storage and putting them into the {@link org.apache.fulcrum.security.entity.Group} objects,
 * saving those data to the permanent storage.
 *
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public interface PermissionManager
extends Component
{
	
	/** Avalon role - used to id the component within the manager */
	String ROLE = PermissionManager.class.getName();
    /**
       * Construct a blank Permission object.
       *
       * This method calls getPermissionClass, and then creates a new object using
       * the default constructor.
       *
       * @return an object implementing Permission interface.
       * @throws UnknownEntityException if the object could not be instantiated.
       */
    Permission getPermissionInstance() throws UnknownEntityException;
    /**
     * Construct a blank Permission object.
     *
     * This method calls getPermissionClass, and then creates a new object using
     * the default constructor.
     *
     * @param permName The name of the Permission
     *
     * @return an object implementing Permission interface.
     * @throws UnknownEntityException if the object could not be instantiated.
     */
    Permission getPermissionInstance(String permName) throws UnknownEntityException;
    /**
    	 * Retrieves all permissions associated with a role.
    	 *
    	 * @param role the role name, for which the permissions are to be retrieved.
    	 * @return the permissions associated with the role
    	 * @throws DataBackendException if there was an error accessing the data
    	 *         backend.
    	 * @throws UnknownEntityException if the role is not present.
    	 */
    PermissionSet getPermissions(Role role) throws DataBackendException, UnknownEntityException;
    /**
    	 * Retrieve a Permission object with specified name.
    	 *
    	 * @param name the name of the Permission.
    	 * @return an object representing the Permission with specified name.
    	 * @throws DataBackendException if there was an error accessing the data
    	 *         backend.
    	 * @throws UnknownEntityException if the permission does not exist.
    	 */
    Permission getPermissionByName(String name) throws DataBackendException, UnknownEntityException;
    /**
    	* Retrieve a Permission object with specified Id.
    	*
    	* @param name the name of the Permission.
    	*
    	* @return an object representing the Permission with specified name.
    	*
    	* @exception UnknownEntityException if the permission does not
    	*            exist in the database.
    	* @exception DataBackendException if there is a problem accessing the
    	*            storage.
    	*/
    Permission getPermissionById(int id) throws DataBackendException, UnknownEntityException;
    /**
       * Retrieves all permissions defined in the system.
       *
       * @return the names of all roles defined in the system.
       * @throws DataBackendException if there was an error accessing the data
       *         backend.
       */
    PermissionSet getAllPermissions() throws DataBackendException;
    /**
    	* Stores Permission's attributes. The Permission is required to exist in
    	* the system.
    	*
    	* @param permission The Permission to be stored.
    	* @throws DataBackendException if there was an error accessing the data
    	*         backend.
    	* @throws UnknownEntityException if the permission does not exist.
    	*/
    void savePermission(Permission permission) throws DataBackendException, UnknownEntityException;
    /**
    	* Creates a new permission with specified attributes.
    	*
    	* @param permission The object describing the permission to be created.
    	* @return the new Permission object.
    	* @throws DataBackendException if there was an error accessing the data
    	*         backend.
    	* @throws EntityExistsException if the permission already exists.
    	*/
    Permission addPermission(Permission permission) throws DataBackendException, EntityExistsException;
    /**
    	 * Removes a Permission from the system.
    	 *
    	 * @param permission The object describing the permission to be removed.
    	 * @throws DataBackendException if there was an error accessing the data
    	 *         backend.
    	 * @throws UnknownEntityException if the permission does not exist.
    	 */
    void removePermission(Permission permission) throws DataBackendException, UnknownEntityException;
    /**
       * Renames an existing Permission.
       *
       * @param permission The object describing the permission to be renamed.
       * @param name the new name for the permission.
       * @throws DataBackendException if there was an error accessing the data
       *         backend.
       * @throws UnknownEntityException if the permission does not exist.
       */
    void renamePermission(Permission permission, String name) throws DataBackendException, UnknownEntityException;
    
	/**
		   * Determines if the <code>Permission</code> exists in the security system.
		   *
		   * @param permission a <code>Permission</code> value
		   * @return true if the permission exists in the system, false otherwise
		   * @throws DataBackendException when more than one Permission with
		   *         the same name exists.
		   * @throws Exception A generic exception.
		   */
	 boolean checkExists(Permission permission) throws DataBackendException;    
}
