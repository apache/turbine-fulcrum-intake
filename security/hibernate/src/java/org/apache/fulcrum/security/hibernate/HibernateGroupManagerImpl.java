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
import org.apache.fulcrum.security.entity.Group;
import org.apache.fulcrum.security.spi.AbstractGroupManager;
import org.apache.fulcrum.security.util.DataBackendException;
import org.apache.fulcrum.security.util.GroupSet;
import org.apache.fulcrum.security.util.UnknownEntityException;
/**
 * This implementation persists to a database via Hibernate.
 *
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class HibernateGroupManagerImpl extends AbstractGroupManager
{
    /** Logging */
    private static Log log = LogFactory.getLog(HibernateGroupManagerImpl.class);
    private PersistenceHelper persistenceHelper;

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
        Group group = null;
        try
        {

            List groups =
			getPersistenceHelper().retrieveSession().find(
                    "from "
                        + Group.class.getName()
                        + " g where g.name=?",
                    name.toLowerCase(),
                    Hibernate.STRING);
            if (groups.size() == 0)
            {
                throw new UnknownEntityException("Could not find group" + name);
            }
            group = (Group) groups.get(0);
            //session.close();
        }
        catch (HibernateException e)
        {
            throw new DataBackendException(
                "Error retriving group information",
                e);
        }
        return group;
    }

    /**
     * Retrieves all groups defined in the system.
     *
     * @return the names of all groups defined in the system.
     * @throws DataBackendException if there was an error accessing the
     *         data backend.
     */
    public GroupSet getAllGroups() throws DataBackendException
    {
        GroupSet groupSet = new GroupSet();
        try
        {

            List groups =
			getPersistenceHelper().retrieveSession().find(
                    "from " + Group.class.getName() + "");
            groupSet.add(groups);
        }
        catch (HibernateException e)
        {
            throw new DataBackendException(
                "Error retriving group information",
                e);
        }
        return groupSet;
    }
    /**
    	* Removes a Group from the system.
    	*
    	* @param group The object describing the group to be removed.
    	* @throws DataBackendException if there was an error accessing the data
    	*         backend.
    	* @throws UnknownEntityException if the group does not exist.
    	*/
    public synchronized void removeGroup(Group group)
        throws DataBackendException, UnknownEntityException
    {
        boolean groupExists = false;
		getPersistenceHelper().removeEntity(group);
    }
    /**
    	* Renames an existing Group.
    	*
    	* @param group The object describing the group to be renamed.
    	* @param name the new name for the group.
    	* @throws DataBackendException if there was an error accessing the data
    	*         backend.
    	* @throws UnknownEntityException if the group does not exist.
    	*/
    public synchronized void renameGroup(Group group, String name)
        throws DataBackendException, UnknownEntityException
    {
        boolean groupExists = false;
        groupExists = checkExists(group);
        if (groupExists)
        {
            group.setName(name);
			getPersistenceHelper().updateEntity(group);
        }
        else
        {
            throw new UnknownEntityException("Unknown group '" + group + "'");
        }
    }

    /**
     * Determines if the <code>Group</code> exists in the security system.
     *
     * @param groupName a <code>Group</code> value
     * @return true if the group name exists in the system, false otherwise
     * @throws DataBackendException when more than one Group with
     *         the same name exists.
     */
    public boolean checkExists(String groupName) throws DataBackendException
	{
    	List groups;
    	try
		{

    		groups =
    			getPersistenceHelper().retrieveSession().find(
    					"from "
    					+ Group.class.getName()
						+ " sg where sg.name=?",
						groupName,
						Hibernate.STRING);
    	}
    	catch (HibernateException e)
		{
    		throw new DataBackendException(
    				"Error retriving user information",
					e);
    	}
    	if (groups.size() > 1)
    	{
    		throw new DataBackendException(
    				"Multiple groups with same name '" + groupName + "'");
    	}
    	return (groups.size() == 1);
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
    protected synchronized Group persistNewGroup(Group group)
        throws DataBackendException
    {

		getPersistenceHelper().addEntity(group);
        return group;
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
     * Retrieve a Group object with specified id.
     * 
     * @param id
     *            the id of the Group.
     * @return an object representing the Group with specified id.
     * @throws DataBackendException
     *             if there was an error accessing the data backend.
     * @throws UnknownEntityException
     *             if the group does not exist.
     */
    public Group getGroupById(Object id)
	throws DataBackendException, UnknownEntityException {
    	
    	Group group = null;

    	if (id != null)
    		try {
    			List groups =
    				getPersistenceHelper().retrieveSession().find(
    						"from " + Group.class.getName() + " sr where sr.id=?",
							id,
							Hibernate.LONG);
    			if (groups.size() == 0) {
    				throw new UnknownEntityException(
    						"Could not find group by id " + id);
    			}
    			group = (Group) groups.get(0);
    			
    		} catch (HibernateException e) {
    			throw new DataBackendException(
    					"Error retriving group information",
						e);
    		}
    		
    		return group;
    }
    
    
}