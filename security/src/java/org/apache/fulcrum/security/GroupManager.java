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
import org.apache.fulcrum.security.entity.Group;
import org.apache.fulcrum.security.util.DataBackendException;
import org.apache.fulcrum.security.util.EntityExistsException;
import org.apache.fulcrum.security.util.GroupSet;
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
public interface GroupManager
extends Component
{
	
	/** Avalon role - used to id the component within the manager */
	String ROLE = GroupManager.class.getName();
	
    /**
     * Construct a blank Group object.
     *
     * This method calls getGroupClass, and then creates a new object using
     * the default constructor.
     *
     * @return an object implementing Group interface.
     * @throws DataBackendException if the object could not be instantiated.
     */
    Group getGroupInstance() throws DataBackendException;
    /**
     * Construct a blank Group object.
     *
     * This method calls getGroupClass, and then creates a new object using
     * the default constructor.
     *
     * @param groupName The name of the Group
     *
     * @return an object implementing Group interface.
     * @throws DataBackendException if the object could not be instantiated.
     */
    Group getGroupInstance(String groupName) throws DataBackendException;
    /**
    	* Retrieve a Group object with specified name.
    	*
    	* @param name the name of the Group.
    	* @return an object representing the Group with specified name.
    	* @throws DataBackendException if there was an error accessing the data
    	*         backend.
    	* @throws UnknownEntityException if the group does not exist.
    	*/
    Group getGroupByName(String name) throws DataBackendException, UnknownEntityException;
   
    /**
	* Retrieve a Group object with specified Id.
	*
	* @param name the name of the Group.
	*
	* @return an object representing the Group with specified name.
	*
	* @exception UnknownEntityException if the permission does not
	*            exist in the database.
	* @exception DataBackendException if there is a problem accessing the
	*            storage.
	*/
    Group getGroupById(Object id) throws DataBackendException, UnknownEntityException;
    
	 
			
	/**
	 * Renames an existing Group.
	 *
	 * @param group The object describing the group to be renamed.
	 * @param name the new name for the group.
	 * @throws DataBackendException if there was an error accessing the data
	 *         backend.
	 * @throws UnknownEntityException if the group does not exist.
	 */
	void renameGroup(Group group, String name)
			throws DataBackendException, UnknownEntityException;	
			
	/**
	   * Removes a Group from the system.
	   *
	   * @param group The object describing the group to be removed.
	   * @throws DataBackendException if there was an error accessing the data
	   *         backend.
	   * @throws UnknownEntityException if the group does not exist.
	   */
	  void removeGroup(Group group)
			  throws DataBackendException, UnknownEntityException;
			  
	/**
	   * Creates a new group with specified attributes.
	   *
	   * @param group the object describing the group to be created.
	   * @return the new Group object.
	   * @throws DataBackendException if there was an error accessing the data
	   *         backend.
	   * @throws EntityExistsException if the group already exists.
	   */
	  Group addGroup(Group group)
			  throws DataBackendException, EntityExistsException;
			  
	/**
	   * Retrieves all groups defined in the system.
	   *
	   * @return the names of all groups defined in the system.
	   * @throws DataBackendException if there was an error accessing the data
	   *         backend.
	   */
	  GroupSet getAllGroups()
			  throws DataBackendException;
			  
	/**
		   * Determines if the <code>Group</code> exists in the security system.
		   *
		   * @param permission a <code>Group</code> value
		   * @return true if the group exists in the system, false otherwise
		   * @throws DataBackendException when more than one group with
		   *         the same name exists.
		   * @throws Exception A generic exception.
		   */
		public boolean checkExists(Group group) throws DataBackendException;		  
			  
}
