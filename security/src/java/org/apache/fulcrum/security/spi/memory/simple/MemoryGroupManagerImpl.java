package org.apache.fulcrum.security.spi.memory.simple;
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

import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.security.RoleManager;
import org.apache.fulcrum.security.entity.Group;
import org.apache.fulcrum.security.entity.Role;
import org.apache.fulcrum.security.model.simple.entity.SimpleGroup;
import org.apache.fulcrum.security.model.simple.entity.SimpleRole;
import org.apache.fulcrum.security.model.simple.manager.*;
import org.apache.fulcrum.security.util.DataBackendException;
import org.apache.fulcrum.security.util.EntityExistsException;
import org.apache.fulcrum.security.util.GroupSet;
import org.apache.fulcrum.security.util.UnknownEntityException;
/**
 * This implementation keeps all objects in memory.  This is mostly meant to help
 * with testing and prototyping of ideas.
 *
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class MemoryGroupManagerImpl extends AbstractLogEnabled implements SimpleGroupManager, Composable
{
    /** Logging */
    private static Log log = LogFactory.getLog(MemoryGroupManagerImpl.class);
    private static List groups = new ArrayList();
    /** Our Unique ID counter */
    private static int uniqueId = 0;
    private ComponentManager manager = null;
    /** Our role Manager **/
    private RoleManager roleManager;
    /**
    	* @return
    	*/
    RoleManager getRoleManager() throws ComponentException
    {
        if (roleManager == null)
        {
            roleManager = (RoleManager) manager.lookup(RoleManager.ROLE);
        }
        return roleManager;
    }
    /**
    	* Construct a blank Group object.
    	*
    	* This method calls getGroupClass, and then creates a new object using
    	* the default constructor.
    	*
    	* @return an object implementing Group interface.
    	* @throws UnknownEntityException if the object could not be instantiated.
    	*/
    public Group getGroupInstance() throws UnknownEntityException
    {
        Group group;
        try
        {
            group = (Group) new SimpleGroup();
        }
        catch (Exception e)
        {
            throw new UnknownEntityException("Failed to instantiate a Group implementation object", e);
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
    	* @throws UnknownEntityException if the object could not be instantiated.
    	*/
    public Group getGroupInstance(String groupName) throws UnknownEntityException
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
    public Group getGroupById(long id) throws DataBackendException, UnknownEntityException
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
            groupExists = checkExists(group);
            if (groupExists)
            {
                groups.remove(group);
                return;
            }
            else
            {
                throw new UnknownEntityException("Unknown group '" + group + "'");
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
        }
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
            groupExists = checkExists(group);
            if (groupExists)
            {
                groups.remove(group);
                group.setName(name);
                groups.add(group);
            }
            else
            {
                throw new UnknownEntityException("Unknown group '" + group + "'");
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException("renameGroup(Group,String)", e);
        }
        finally
        {
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
    public boolean checkExists(Group group) throws DataBackendException
    {
        try
        {
            boolean exists = false;
            for (Iterator i = groups.iterator(); i.hasNext();)
            {
                Group g = (Group) i.next();
                if (g.getName().equalsIgnoreCase(group.getName()) | g.getId() == group.getId())
                {
                    exists = true;
                }
            }
            return exists;
            //return groups.contains(group);
        }
        catch (Exception e)
        {
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
            throw new DataBackendException("Could not create a group with empty name!");
        }
        if (group.getId() > 0)
        {
            throw new DataBackendException("Could not create a group with an id!");
        }
        groupExists = checkExists(group);
        if (!groupExists)
        {
            group.setId(getUniqueId());
            groups.add(group);
            // return the object with correct id
            return group;
        }
        else
        {
            throw new EntityExistsException("Group '" + group + "' already exists");
        }
    }
    /**
    	  * Grants a Group a Role
    	  *
    	  * @param group the Group.
    	  * @param role the Role.
    	  * @throws DataBackendException if there was an error accessing the data
    	  *         backend.
    	  * @throws UnknownEntityException if group or role is not present.
    	  */
    public synchronized void grant(Group group, Role role) throws DataBackendException, UnknownEntityException
    {
        boolean groupExists = false;
        boolean roleExists = false;
        try
        {
            groupExists = checkExists(group);
            roleExists = checkExists(role);
            if (groupExists && roleExists)
            {
				((SimpleGroup) group).addRole(role);
				((SimpleRole) role).addGroup(group);
                return;
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException("grant(Group,Role) failed", e);
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
    	  * Revokes a Role from a Group.
    	  *
    	  * @param group the Group.
    	  * @param role the Role.
    	  * @throws DataBackendException if there was an error accessing the data
    	  *         backend.
    	  * @throws UnknownEntityException if group or role is not present.
    	  */
    public synchronized void revoke(Group group, Role role) throws DataBackendException, UnknownEntityException
    {
        boolean groupExists = false;
        boolean roleExists = false;
        try
        {
            groupExists = checkExists(group);
            roleExists = checkExists(role);
            if (groupExists && roleExists)
            {
				((SimpleGroup) group).removeRole(role);
				((SimpleRole) role).removeGroup(group);
                return;
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException("revoke(Group,Role) failed", e);
        }
        finally
        {
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
     * Determines if the <code>Permission</code> exists in the security system.
     *
     * @param permission a <code>Permission</code> value
     * @return true if the permission exists in the system, false otherwise
     * @throws DataBackendException when more than one Permission with
     *         the same name exists.
     * @throws Exception A generic exception.
     */
    public boolean checkExists(Role role) throws DataBackendException
    {
        try
        {
            return getRoleManager().checkExists(role);
        }
        catch (ComponentException ce)
        {
            throw new DataBackendException("Problem getting role manager", ce);
        }
    }
    /**
      * Avalon component lifecycle method
      */
    public void compose(ComponentManager manager) throws ComponentException
    {
        this.manager = manager;
    }
    private int getUniqueId()
    {
        return ++uniqueId;
    }
}
