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
import java.util.List;

import net.sf.hibernate.Hibernate;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.Transaction;
import net.sf.hibernate.avalon.HibernateService;

import org.apache.avalon.framework.activity.Disposable;
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
import org.apache.fulcrum.security.model.simple.manager.SimpleGroupManager;
import org.apache.fulcrum.security.util.DataBackendException;
import org.apache.fulcrum.security.util.EntityExistsException;
import org.apache.fulcrum.security.util.GroupSet;
import org.apache.fulcrum.security.util.UnknownEntityException;
/**
 * This implementation persists to a database via Hibernate.
 *
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class HibernateGroupManagerImpl extends AbstractLogEnabled implements SimpleGroupManager, Composable, Disposable
{
    /** Logging */
    private static Log log = LogFactory.getLog(HibernateGroupManagerImpl.class);
    /** Hibernate components */
    private HibernateService hibernateService;
    private Session session;
    private Transaction transaction;
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
    public Group getGroupById(int id) throws DataBackendException, UnknownEntityException
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
        GroupSet groupSet = new GroupSet();
        try
        {
            session = hibernateService.openSession();
            List groups = session.find("from SimpleGroup");
            groupSet.add(groups);
        }
        catch (HibernateException e)
        {
            throw new DataBackendException("Error retriving group information", e);
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
    public synchronized void removeGroup(Group group) throws DataBackendException, UnknownEntityException
    {
        boolean groupExists = false;
        try
        {
            groupExists = checkExists(group);
            if (groupExists)
            {
                session = hibernateService.openSession();
                transaction = session.beginTransaction();
                session.delete(group);
                transaction.commit();
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
                group.setName(name);
                saveGroup(group);
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
                session = hibernateService.openSession();
                transaction = session.beginTransaction();
                session.update(group);
                transaction.commit();
            }
            else
            {
                throw new UnknownEntityException("Unknown group '" + group + "'");
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException("saveGroup(Group) failed", e);
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
        List groups;
        try
        {
            session = hibernateService.openSession();
            groups = session.find("from SimpleGroup sg where sg.name=?", group.getName(), Hibernate.STRING);
        }
        catch (HibernateException e)
        {
            throw new DataBackendException("Error retriving user information", e);
        }
        if (groups.size() > 1)
        {
            throw new DataBackendException("Multiple groups with same name '" + group.getName() + "'");
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
        if (checkExists(group))
        {
            throw new EntityExistsException("The group '" + group.getName() + "' already exists");
        }
        try
        {
            session = hibernateService.openSession();
            transaction = session.beginTransaction();
            session.save(group);
            transaction.commit();
        }
        catch (HibernateException e)
        {
            log.error("Error adding group", e);
            try
            {
                transaction.rollback();
            }
            catch (HibernateException he)
            {
            }
            throw new DataBackendException("Failed to create group '" + group.getName() + "'", e);
        }
        return group;
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
        hibernateService = (HibernateService) manager.lookup(HibernateService.ROLE);
    }
    /**
    	   * DESTRUCTION: step 2
    	   * @see org.apache.avalon.framework.activity.Disposable#dispose()
    	   */
    public void dispose()
    {
        hibernateService = null;
        manager = null;
        roleManager = null;
    }
}
