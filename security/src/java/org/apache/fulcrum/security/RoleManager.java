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
import org.apache.fulcrum.security.entity.Role;
import org.apache.fulcrum.security.util.DataBackendException;
import org.apache.fulcrum.security.util.EntityExistsException;
import org.apache.fulcrum.security.util.RoleSet;
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
public interface RoleManager extends Component
{
    /** Avalon role - used to id the component within the manager */
    String ROLE = RoleManager.class.getName();
    /**
     * Construct a blank Role object.
     *
     * This method calls getRoleClass, and then creates a new object using
     * the default constructor.
     *
     * @return an object implementing Role interface.
     * @throws UnknownEntityException if the object could not be instantiated.
     */
    public Role getRoleInstance() throws UnknownEntityException;
    /**
    * Construct a blank Role object.
    *
    * This method calls getRoleClass, and then creates a new object using
    * the default constructor.
    *
    * @param roleName The name of the Role
    *
    * @return an object implementing Role interface.
    * @throws UnknownEntityException if the object could not be instantiated.
    */
    Role getRoleInstance(String roleName) throws UnknownEntityException;
    /**
    	* Revokes all permissions from a Role.
    	*
    	* This method is user when deleting a Role.
    	*
    	* @param role the Role
    	* @throws DataBackendException if there was an error accessing the data
    	*         backend.
    	* @throws  UnknownEntityException if the Role is not present.
    	*/
    void revokeAll(Role role) throws DataBackendException, UnknownEntityException;
    /**
    	 * Retrieve a Role object with specified name.
    	 *
    	 * @param name the name of the Role.
    	 * @return an object representing the Role with specified name.
    	 * @throws DataBackendException if there was an error accessing the data
    	 *         backend.
    	 * @throws UnknownEntityException if the role does not exist.
    	 */
    Role getRoleByName(String name) throws DataBackendException, UnknownEntityException;
    /**
    	* Retrieve a Role object with specified Id.
    	*
    	* @param name the name of the Role.
    	*
    	* @return an object representing the Role with specified name.
    	*
    	* @exception UnknownEntityException if the permission does not
    	*            exist in the database.
    	* @exception DataBackendException if there is a problem accessing the
    	*            storage.
    	*/
    Role getRoleById(long id) throws DataBackendException, UnknownEntityException;
    /**
     * Retrieves all roles defined in the system.
     *
     * @return the names of all roles defined in the system.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     */
    RoleSet getAllRoles() throws DataBackendException;

    /**
      * Creates a new role with specified attributes.
      *
      * @param role The object describing the role to be created.
      * @return the new Role object.
      * @throws DataBackendException if there was an error accessing the data
      *         backend.
      * @throws EntityExistsException if the role already exists.
      */
    Role addRole(Role role) throws DataBackendException, EntityExistsException;
    /**
    	* Removes a Role from the system.
    	*
    	* @param role The object describing the role to be removed.
    	* @throws DataBackendException if there was an error accessing the data
    	*         backend.
    	* @throws UnknownEntityException if the role does not exist.
    	*/
    void removeRole(Role role) throws DataBackendException, UnknownEntityException;
    /**
    	* Renames an existing Role.
    	*
    	* @param role The object describing the role to be renamed.
    	* @param name the new name for the role.
    	* @throws DataBackendException if there was an error accessing the data
    	*         backend.
    	* @throws UnknownEntityException if the role does not exist.
    	*/
    void renameRole(Role role, String name) throws DataBackendException, UnknownEntityException;
 
    /**
    		   * Determines if the <code>Role</code> exists in the security system.
    		   *
    		   * @param role a <code>Role</code> value
    		   * @return true if the role exists in the system, false otherwise
    		   * @throws DataBackendException when more than one Role with
    		   *         the same name exists.
    		   * @throws Exception A generic exception.
    		   */
    boolean checkExists(Role role) throws DataBackendException;
}
