package org.apache.fulcrum.security.impl.db;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
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

import java.beans.PropertyDescriptor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.fulcrum.InitializationException;

import org.apache.fulcrum.security.entity.Group;
import org.apache.fulcrum.security.entity.Role;
import org.apache.fulcrum.security.entity.User;

import org.apache.fulcrum.security.impl.db.DBSecurityService;

import org.apache.fulcrum.security.impl.db.entity.TurbineUserGroupRolePeer;
import org.apache.fulcrum.security.impl.db.entity.TurbineUserPeer;

import org.apache.fulcrum.security.util.DataBackendException;
import org.apache.fulcrum.security.util.RoleSet;
import org.apache.fulcrum.security.util.UnknownEntityException;

import org.apache.commons.configuration.Configuration;

import org.apache.torque.TorqueException;

import org.apache.torque.om.Persistent;

import org.apache.torque.util.BasePeer;
import org.apache.torque.util.Criteria;

/**
 * This class capsulates all direct Peer access for the Role entities.
 * It allows the exchange of the default Fulcrum supplied TurbineRolePeer
 * class against a custom class
 *
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 *
 */

public class RolePeerManager
    implements RolePeerManagerConstants
{
    /** The class of the Peer the DBSecurityService uses */
    private static Class rolePeerClass = null;

    /** The class name of the objects returned by the configured peer. */
    private static Class roleObject = null;

    /** The name of the Table used for Group Object queries  */
    private static String tableName = null;

    /** The name of the column used as "Name" Column */
    private static String nameColumn = null;

    /** The name of the column used as "Id" Column */
    private static String idColumn = null;

    /** The "Name" property descriptor */
    private static PropertyDescriptor namePropDesc = null;

    /** The "Id" property descriptor */
    private static PropertyDescriptor idPropDesc = null;

    /**
     * Initializes the RolePeerManager, loading the class object for the 
     * Peer used to retrieve Role objects
     *
     * @exception InitializationException A problem occured during initialization
     */

    public static void init(Configuration conf)
        throws InitializationException
    {
        String rolePeerClassName = conf.getString(ROLE_PEER_CLASS_KEY, 
                                                  ROLE_PEER_CLASS_DEFAULT);
        String roleObjectName = null;

        try
        {
            rolePeerClass = Class.forName(rolePeerClassName);

            tableName  = (String)rolePeerClass.getField("TABLE_NAME").get(null);

            //
            // We have either an user configured Object class or we use the default
            // as supplied by the Peer class
            //
            roleObject = getPersistenceClass(); // Default from Peer, can be overridden
            
            roleObjectName = (String)conf
                .getString(ROLE_CLASS_KEY,
                           roleObject.getName());
            
            roleObject = Class.forName(roleObjectName); // Maybe the user set a new value...

            /* If any of the following Field queries fails, the role subsystem is unusable. So check
             * this right here at init time, which saves us much time and hassle if it fails...
             */

            nameColumn       = (String)rolePeerClass.getField((String)conf
                                                              .getString(ROLE_NAME_COLUMN_KEY,
                                                                         ROLE_NAME_COLUMN_DEFAULT)
                                                              ).get(null);

            idColumn         = (String)rolePeerClass.getField((String)conf
                                                              .getString(ROLE_ID_COLUMN_KEY,
                                                                         ROLE_ID_COLUMN_DEFAULT)  
                                                              ).get(null);


            namePropDesc       = new PropertyDescriptor((String)conf.getString(ROLE_NAME_PROPERTY_KEY,
                                                                               ROLE_NAME_PROPERTY_DEFAULT),
                                                        roleObject);

            idPropDesc         = new PropertyDescriptor((String)conf.getString(ROLE_ID_PROPERTY_KEY,
                                                                               ROLE_ID_PROPERTY_DEFAULT),
                                                        roleObject);

        }
        catch(Exception e)
        {
            if(rolePeerClassName == null || rolePeerClass == null)
            {
                throw new InitializationException(
                    "Could not find RolePeer class ("+ rolePeerClassName+ ")", e);
            }
            if(tableName == null)
            {
                throw new InitializationException(
                    "Failed to get the table name from the Peer object",e);
            }
            if(roleObject == null || roleObjectName == null)
            {
                throw new InitializationException(
                    "Failed to get the object type from the Peer object",e);
            }


            if(nameColumn == null || namePropDesc == null)
            {
                throw new InitializationException(
                    "RolePeer " + rolePeerClassName + " has no name column information!",e);
            }
            if(idColumn == null || idPropDesc == null)
            {
                throw new InitializationException(
                    "RolePeer " + rolePeerClassName + " has no id column information!",e);
            }
        }
    }

    /**
     * Get the name of this table.
     *
     * @return A String with the name of the table.
     */
    public static String getTableName()
    {
        return tableName;
    }

    /**
     * Returns the fully qualified name of the Column to
     * use as the Name Column for a group
     *
     * @return A String containing the column name
     */
    public static String getNameColumn()
    {
        return nameColumn;
    }

    /**
     * Returns the fully qualified name of the Column to
     * use as the Id Column for a group
     *
     * @return A String containing the column id
     */
    public static String getIdColumn()
        throws Exception
    {
        return idColumn;
    }

    /**
     * Returns the full name of a column.
     *
     * @return A String with the full name of the column.
     */
    public static String getColumnName(String name)
    {
        StringBuffer sb = new StringBuffer();
        sb.append(getTableName());
        sb.append(".");
        sb.append(name);
        return sb.toString();
    }

    /**
     * Returns a new, empty object for the underlying peer.
     * Used to create a new underlying object
     *
     * @return A new object which is compatible to the Peer
     *         and can be used as a User object
     *
     */

    public static Persistent newPersistentInstance()
    {
        Persistent obj;
        try
        {
            obj = (Persistent)roleObject.newInstance();
        }
        catch(Exception e)
        {
            obj = null; // FIXME ? 
        }
        return obj;
    }

    /**
     * Retrieves/assembles a RoleSet based on the Criteria passed in
     *
     * @param criteria A criteria containing a pre-assembled set of criterias
     *         for the RoleSet
     *
     * @return A Set of roles which fulfil the required criterias
     *
     * @exception Exception A generic exception.
     *
     */
    public static RoleSet retrieveSet(Criteria criteria) 
        throws Exception
    {
        List results = doSelect(criteria);
        RoleSet rs = new RoleSet();
        for (int i=0; i<results.size(); i++)
        {
            rs.add( (Role)results.get(i) );
        }
        return rs;
    }

    /**
     * Retrieves a set of Roles that an User was assigned in a Group
     *
     * @param user An user object
     * @param group A group object
     * 
     * @return A Set of Roles of this User in the Group
     *
     * @exception Exception A generic exception.
     */
    public static RoleSet retrieveSet(User user, Group group)
        throws Exception
    {
        Criteria criteria = new Criteria();

        criteria.add(UserPeerManager.getNameColumn(), user.getUserName());

        criteria.add(TurbineUserGroupRolePeer.GROUP_ID,
                     ((Persistent)group).getPrimaryKey());

        criteria.addJoin(TurbineUserPeer.USER_ID, TurbineUserGroupRolePeer.USER_ID);
        criteria.addJoin(TurbineUserGroupRolePeer.ROLE_ID, getIdColumn());

        return retrieveSet(criteria);
    }

    /**
     * Checks if a Role is defined in the system. The name
     * is used as query criteria.
     *
     * @param role The Role to be checked.
     * @return <code>true</code> if given Role exists in the system.
     * @throws DataBackendException when more than one Role with
     *         the same name exists.
     * @throws Exception, a generic exception.
     */
    public static boolean checkExists(Role role)
        throws DataBackendException, Exception
    {
        Criteria criteria = new Criteria();

        criteria.addSelectColumn(getIdColumn());

        criteria.add(getNameColumn(), role.getName());

        List results = BasePeer.doSelect(criteria);

        if (results.size() > 1)
        {
            throw new DataBackendException("Multiple roles named '" +
                                           role.getName() + "' exist!");
        }
        return (results.size() == 1);
    }

    /*
     * ========================================================================
     *
     * WARNING! Do not read on if you have a weak stomach. What follows here
     * are some abominations thanks to the braindead static peers of Torque 
     * and the rigidity of Java....
     *
     * ========================================================================
     *
     */

    /**
     * Calls buildCriteria(Role role) in the configured RolePeer. If you get
     * a ClassCastException in this routine, you put a Role object into this
     * method which can't be cast into an object for the DBSecurityService. This is a
     * configuration error most of the time.
     *
     * @param role An object which implements the Role interface
     *
     * @return A criteria for the supplied role object 
     */

    public static Criteria buildCriteria(Role role)
    {
        Criteria crit;

        try
        {
            Class[] clazz = new Class[] { roleObject };
            Object[] params = new Object[] { ((DBRole)role).getPersistentObj() };

            crit =  (Criteria)rolePeerClass
                .getMethod("buildCriteria", clazz)
                .invoke(null, params);
        }
        catch(Exception e)
        {
            crit = null;
        }

        return crit;
    }

    /**
     * Invokes doUpdate(Criteria c) on the configured Peer Object
     *
     * @param criteria  A Criteria Object
     *
     * @exception TorqueException A problem occured.
     */

    public static void doUpdate(Criteria criteria)
        throws TorqueException
    {
        try
        {
            Class[] clazz = new Class[] { Class.forName("org.apache.torque.util.Criteria") };
            Object[] params = new Object[] { criteria };

            rolePeerClass
                .getMethod("doUpdate", clazz)
                .invoke(null, params);
        }
        catch(Exception e)
        {
            throw new TorqueException("doUpdate failed", e);
        }
    }

    /**
     * Invokes doInsert(Criteria c) on the configured Peer Object
     *
     * @param criteria  A Criteria Object
     *
     * @exception TorqueException A problem occured.
     */

    public static void doInsert(Criteria criteria)
        throws TorqueException
    {
        try
        {
            Class[] clazz = new Class[] { Class.forName("org.apache.torque.util.Criteria") };
            Object[] params = new Object[] { criteria };

            rolePeerClass
                .getMethod("doInsert", clazz)
                .invoke(null, params);
        }
        catch(Exception e)
        {
            throw new TorqueException("doInsert failed", e);
        }
    }

    /**
     * Invokes doSelect(Criteria c) on the configured Peer Object
     *
     * @param criteria  A Criteria Object
     *
     * return A List of Role Objects selected by the Criteria
     *
     * @exception TorqueException A problem occured.
     */
    public static List doSelect(Criteria criteria)
        throws TorqueException
    {
        List list;

        try
        {
            Class[] clazz = new Class[] { Class.forName("org.apache.torque.util.Criteria") };
            Object[] params = new Object[] { criteria };

            list = (List)rolePeerClass
                .getMethod("doSelect", clazz)
                .invoke(null, params);
        }
        catch(Exception e)
        {
            throw new TorqueException("doSelect failed", e);
        }
        List newList = new ArrayList(list.size());

        //
        // Wrap the returned Objects into DBRoles.
        //
        for(Iterator it = list.iterator(); it.hasNext(); )
        {
            DBRole dr = new DBRole((Persistent)it.next());
            newList.add(dr);
        }

        return newList;
    }

    /**
     * Invokes doDelete(Criteria c) on the configured Peer Object
     *
     * @param criteria  A Criteria Object
     *
     * @exception TorqueException A problem occured.
     */
    public static void doDelete(Criteria criteria)
        throws TorqueException
    {
        try
        {
            Class[] clazz = new Class[] { Class.forName("org.apache.torque.util.Criteria") };
            Object[] params = new Object[] { criteria };

            rolePeerClass
                .getMethod("doDelete", clazz)
                .invoke(null, params);
        }
        catch(Exception e)
        {
            throw new TorqueException("doDelete failed", e);
        }
    }

    /**
     * Invokes setName(String s) on the supplied base object
     *
     * @param obj The object to use for setting the name
     * @param name The Name to set
     *
     * @exception TorqueException A problem occured.
     */
    public static void setRoleName(Persistent obj, String name)
    {
        try
        {
            Object[] params = new Object[] { name };
            namePropDesc.getWriteMethod().invoke(obj, params);
        }
        catch(ClassCastException cce)
        {
            throw new RuntimeException(obj.getClass().getName()+" does not seem to be a Role Object!");
        }
        catch(Exception e)
        {
            // Not much we can do; setName returns no exception
        }
    }

    /**
     * Invokes getName() on the supplied base object
     *
     * @param obj The object to use for getting the name
     *
     * @return A string containing the name
     *
     * @exception TorqueException A problem occured.
     */
    public static String getRoleName(Persistent obj)
    {
        String name = null;
        try
        {
            name = (String)namePropDesc
                .getReadMethod()
                .invoke(obj, new Object[] {});
        }
        catch(ClassCastException cce)
        {
            throw new RuntimeException(obj.getClass().getName()+" does not seem to be a Role Object!");
        }
        catch(Exception e)
        {
            // see setRoleName
        }
        return name;
    }

    /**
     * Returns the Class of the configured Object class
     * from the peer
     *
     * @return The class of the objects returned by the configured peer
     *
     */

    private static Class getPersistenceClass()
    {
        Class persistenceClass = null;

        try
        {
            Object[] params = new Object[0];

            persistenceClass =  (Class)rolePeerClass
                .getMethod("getOMClass", null)
                .invoke(null, params);
        }
        catch(Exception e)
        {
            persistenceClass = null;
        }

        return persistenceClass;
    }

}

