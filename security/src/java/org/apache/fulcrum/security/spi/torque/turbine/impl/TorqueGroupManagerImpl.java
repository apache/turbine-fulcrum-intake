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
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.security.entity.Group;
import org.apache.fulcrum.security.spi.torque.turbine.TorqueGroupManager;
import org.apache.fulcrum.security.spi.torque.turbine.peermanagers.GroupPeerManager;
import org.apache.fulcrum.security.util.DataBackendException;
import org.apache.fulcrum.security.util.EntityExistsException;
import org.apache.fulcrum.security.util.GroupSet;
import org.apache.fulcrum.security.util.UnknownEntityException;
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
public class TorqueGroupManagerImpl extends TorqueManagerComponent implements TorqueGroupManager
{
    /** Logging */
    private static Log log = LogFactory.getLog(TorqueGroupManagerImpl.class);
    private Group globalGroup;
    /** The class of Group the SecurityService uses */
    private Class groupClass = null;
    /**
    	* Return a Class object representing the system's chosen implementation of
    	* of Group interface.
    	*
    	* @return systems's chosen implementation of Group interface.
    	* @throws UnknownEntityException if the implementation of Group interface
    	*         could not be determined, or does not exist.
    	*/
    public Class getGroupClass() throws UnknownEntityException
    {
        if (groupClass == null)
        {
            throw new UnknownEntityException("Failed to create a Class object for Group implementation");
        }
        return groupClass;
    }
    /**
     * Provides a reference to the Group object that represents the
     * <a href="#global">global group</a>.
     *
     * @return a Group object that represents the global group.
     */
    public Group getGlobalGroup() throws DataBackendException
    {
        if (globalGroup == null)
        {
            synchronized (TorqueGroupManagerImpl.class)
            {
                if (globalGroup == null)
                {
                    globalGroup = getAllGroups().getGroupByName(TorqueGroupManager.GLOBAL_GROUP_NAME);
                }
            }
        }
        return globalGroup;
    }
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
            group = (Group) getGroupClass().newInstance();
        }
        catch (Exception e)
        {
            throw new DataBackendException("Failed to instantiate a Group implementation object", e);
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
    	 * @deprecated Use <a href="#getGroupByName">getGroupByName</a> instead.
    	 */
    public Group getGroup(String name) throws DataBackendException, UnknownEntityException
    {
        return getGroupByName(name);
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
    public Group getGroupByName(String name) throws DataBackendException, UnknownEntityException
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
    public Group getGroupById(Object id) throws DataBackendException, UnknownEntityException
    {
        Group group = getAllGroups().getGroupById(id);
        if (group == null)
        {
            throw new UnknownEntityException("The specified group does not exist");
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
        return getGroups(new Criteria());
    }
    /**
       * Retrieve a set of Groups that meet the specified Criteria.
       *
       * @param criteria A Criteria of Group selection.
       * @return a set of Groups that meet the specified Criteria.
       * @throws DataBackendException if there was an error accessing the data
       *         backend.
       */
    public GroupSet getGroups(Criteria criteria) throws DataBackendException
    {
        Criteria torqueCriteria = new Criteria();
        Iterator keys = criteria.keySet().iterator();
        while (keys.hasNext())
        {
            String key = (String) keys.next();
            torqueCriteria.put(GroupPeerManager.getColumnName(key), criteria.get(key));
        }
        List groups = new ArrayList(0);
        try
        {
            groups = GroupPeerManager.doSelect(criteria);
        }
        catch (Exception e)
        {
            throw new DataBackendException("getGroups(Criteria) failed", e);
        }
        return new GroupSet(groups);
    }
    /**
    	* Removes a Group from the system.
    	*
    	* @param group The object describing the group to be removed.
    	* @throws DataBackendException if there was an error accessing the data
    	*         backend.
    	* @throws UnknownEntityException if the group does not exist.
    	*/
    public synchronized void removeGroup(Group group) throws DataBackendException, UnknownEntityException
    {
        boolean groupExists = false;
        try
        {
            lockExclusive();
            groupExists = checkExists(group);
            if (groupExists)
            {
                Criteria criteria = GroupPeerManager.buildCriteria(group);
                GroupPeerManager.doDelete(criteria);
                getAllGroups().remove(group);
                return;
            }
        }
        catch (Exception e)
        {
            log.error("Failed to delete a Group");
            log.error(e);
            throw new DataBackendException("removeGroup(Group) failed", e);
        }
        finally
        {
            unlockExclusive();
        }
        throw new UnknownEntityException("Unknown group '" + group + "'");
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
    public synchronized void renameGroup(Group group, String name) throws DataBackendException, UnknownEntityException
    {
        boolean groupExists = false;
        try
        {
            lockExclusive();
            groupExists = checkExists(group);
            if (groupExists)
            {
                group.setName(name);
                Criteria criteria = GroupPeerManager.buildCriteria(group);
                GroupPeerManager.doUpdate(criteria);
                return;
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException("renameGroup(Group,String)", e);
        }
        finally
        {
            unlockExclusive();
        }
        throw new UnknownEntityException("Unknown group '" + group + "'");
    }
    /**
    	* Stores Group's attributes. The Groups is required to exist in the system.
    	*
    	* @param group The Group to be stored.
    	* @throws DataBackendException if there was an error accessing the data
    	*         backend.
    	* @throws UnknownEntityException if the group does not exist.
    	*/
    public void saveGroup(Group group) throws DataBackendException, UnknownEntityException
    {
        boolean groupExists = false;
        try
        {
            groupExists = checkExists(group);
            if (groupExists)
            {
                Criteria criteria = GroupPeerManager.buildCriteria(group);
                GroupPeerManager.doUpdate(criteria);
                return;
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException("saveGroup(Group) failed", e);
        }
        throw new UnknownEntityException("Unknown group '" + group + "'");
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
    public boolean checkExists(Group group) throws DataBackendException
    {
    	try {
        return GroupPeerManager.checkExists(group);
    	}
    	catch (Exception e){
    		throw new DataBackendException("Problem checking if groups exists", e);
    	}
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
    public synchronized Group addGroup(Group group) throws DataBackendException, EntityExistsException
    {
        boolean groupExists = false;
        if (StringUtils.isEmpty(group.getName()))
        {
            throw new DataBackendException("Could not create " + "a group with empty name!");
        }
        try
        {
            lockExclusive();
            groupExists = checkExists(group);
            if (!groupExists)
            {
                // add a row to the table
                Criteria criteria = GroupPeerManager.buildCriteria(group);
                GroupPeerManager.doInsert(criteria);
                // try to get the object back using the name as key.
                criteria = new Criteria();
                criteria.add(GroupPeerManager.getNameColumn(), group.getName());
                List results = GroupPeerManager.doSelect(criteria);
                if (results.size() != 1)
                {
                    throw new DataBackendException("Internal error - query returned " + results.size() + " rows");
                }
                Group newGroup = (Group) results.get(0);
                // add the group to system-wide cache
                getAllGroups().add(newGroup);
                // return the object with correct id
                return newGroup;
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException("addGroup(Group) failed", e);
        }
        finally
        {
            unlockExclusive();
        }
        // the only way we could get here without return/throw tirggered
        // is that the groupExists was true.
        throw new EntityExistsException("Group '" + group + "' already exists");
    }
}
