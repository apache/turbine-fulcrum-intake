package org.apache.fulcrum.security.spi;
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
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.security.GroupManager;
import org.apache.fulcrum.security.entity.Group;
import org.apache.fulcrum.security.util.DataBackendException;
import org.apache.fulcrum.security.util.EntityExistsException;
import org.apache.fulcrum.security.util.UnknownEntityException;

/**
 * This implementation keeps all objects in memory.  This is mostly meant to help
 * with testing and prototyping of ideas.
 *
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public abstract class AbstractGroupManager
    extends AbstractEntityManager
    implements GroupManager
{
    /** Logging */
    private static Log log = LogFactory.getLog(AbstractGroupManager.class);

    protected abstract Group persistNewGroup(Group group) throws DataBackendException;

    /**
    * Construct a blank Group object.
    *
    * This method calls getGroupClass, and then creates a new object using
    * the default constructor.
    *
    * @return an object implementing Group interface.
    * @throws DataBackendException if the object could not be instantiated.
    */
    public Group getGroupInstance() throws DataBackendException
    {
        Group group;
        try
        {
            group = (Group) Class.forName(getClassName()).newInstance();
        }
        catch (Exception e)
        {
            throw new DataBackendException(
                "Problem creating instance of class " + getClassName(),
                e);
        }

        return group;
    }
    /**
    	* Construct a blank Group object.
    	*
    	* This method calls getGroupClass, and then creates a new object using
    	* the default constructor.
    	*
    	* @param groupName The name of the Group
    	*
    	* @return an object implementing Group interface.
    	*
    	* @throws DataBackendException if the object could not be instantiated.
    	*/
    public Group getGroupInstance(String groupName) throws DataBackendException
    {
        Group group = getGroupInstance();
        group.setName(groupName);
        return group;
    }

    /**
     * Retrieve a Group object with specified name.
     *
     * @param name the name of the Group.
     * @return an object representing the Group with specified name.
     * @throws DataBackendException if there was an error accessing the
     *         data backend.
     * @throws UnknownEntityException if the group does not exist.
     */
    public Group getGroupByName(String name)
        throws DataBackendException, UnknownEntityException
    {
        Group group = getAllGroups().getGroupByName(name);
        if (group == null)
        {
            throw new UnknownEntityException("The specified group does not exist");
        }
        return group;
    }
    /**
     * Retrieve a Group object with specified Id.
     *
     * @param name the name of the Group.
     *
     * @return an object representing the Group with specified name.
     *
     * @throws UnknownEntityException if the permission does not
     *            exist in the database.
     * @throws DataBackendException if there is a problem accessing the
     *            storage.
     */
    public Group getGroupById(Object id)
        throws DataBackendException, UnknownEntityException
    {
        Group group = getAllGroups().getGroupById(id);
        if (group == null)
        {
            throw new UnknownEntityException("The specified group does not exist");
        }
        return group;
    }

    /**
    	* Creates a new group with specified attributes.
    	*
    	* @param group the object describing the group to be created.
    	* @return a new Group object that has id set up properly.
    	* @throws DataBackendException if there was an error accessing the data
    	*         backend.
    	* @throws EntityExistsException if the group already exists.
    	*/
    public synchronized Group addGroup(Group group)
        throws DataBackendException, EntityExistsException
    {
        boolean groupExists = false;
        if (StringUtils.isEmpty(group.getName()))
        {
            throw new DataBackendException("Could not create a group with empty name!");
        }
        if (group.getId() != null)
        {
            throw new DataBackendException("Could not create a group with an id!");
        }
        groupExists = checkExists(group);
        if (!groupExists)
        {

            // return the object with correct id
            return persistNewGroup(group);
        }
        else
        {
            throw new EntityExistsException(
                "Group '" + group + "' already exists");
        }
    }
    
    /**
	* Check whether a specified group exists.
	*
	* The name is used for looking up the group
	*
	* @param role The group to be checked.
	* @return true if the specified group exists
	* @throws DataBackendException if there was an error accessing
	*         the data backend.
	*/
	public boolean checkExists(Group group) throws DataBackendException
	{
	    return checkExists(group.getName());
	}      

}
