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

import java.sql.Connection;

import java.util.Iterator;

import org.apache.fulcrum.security.TurbineSecurity;

import org.apache.fulcrum.security.entity.Permission;
import org.apache.fulcrum.security.entity.SecurityEntity;

import org.apache.fulcrum.security.util.PermissionSet;
import org.apache.fulcrum.security.util.TurbineSecurityException;

import org.apache.torque.om.Persistent;
import org.apache.torque.om.ObjectKey;

/**
 * This class represents a permission given to a Role associated with the
 * current Session. It is separated from the actual Torque peer object
 * to be able to replace the Peer with an user supplied Peer (and Object)
 *
 * @author <a href="mailto:frank.kim@clearink.com">Frank Y. Kim</a>
 * @author <a href="mailto:jmcnally@collab.net">John D. McNally</a>
 * @author <a href="mailto:bmclaugh@algx.net">Brett McLaughlin</a>
 * @author <a href="mailto:mpoeschl@marmot.at">Martin Poeschl</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */

public class DBPermission
    implements Permission,
               Comparable,
               Persistent
{
    /** The underlying database Object which is proxied */
    private Persistent obj = null;

    /**
     * Constructs a Permission
     */
    public DBPermission()
    {
    }

    /**
     * Constructs a new Permission with the sepcified name.
     *
     * @param name The name of the new object.
     */
    public DBPermission(String name)
    {
        this.setName(name);
    }

    /**
     * The package private Constructor is used when the PermissionPeerManager
     * has retrieved a list of Database Objects from the peer and
     * must 'wrap' them into DBPermission Objects.
     *
     * @param obj An Object from the peer
     */

    DBPermission(Persistent obj)
    {
        this.obj = obj;
    }

    /**
     * Returns the underlying Object for the Peer
     *
     * Used in the PermissionPeerManager when building a new Criteria.
     *
     * @return The underlying Persistent Object
     *
     */

    public Persistent getPersistentObj()
    {
        if(obj == null)
        {
            obj = PermissionPeerManager.newPersistentInstance();
        }
        return obj;
    }

    /**
     * getter for the object primaryKey.
     *
     * @return the object primaryKey as an Object
     */
    public ObjectKey getPrimaryKey()
    {
        return getPersistentObj().getPrimaryKey();
    }

    /**
     * Sets the PrimaryKey for the object.
     *
     * @param primaryKey The new PrimaryKey for the object.
     * @exception Exception, This method might throw an exceptions
     */
    public void setPrimaryKey(ObjectKey primaryKey) 
        throws Exception
    {
        getPersistentObj().setPrimaryKey(primaryKey);
    }

    /**
     * Sets the PrimaryKey for the object.
     *
     * @param primaryKey the String should be of the form produced by
     *        ObjectKey.toString().
     * @exception Exception, This method might throw an exceptions
     */
    public void setPrimaryKey(String primaryKey)
        throws Exception
    {
        getPersistentObj().setPrimaryKey(primaryKey);
    }

    /**
     * Returns whether the object has been modified, since it was
     * last retrieved from storage.
     *
     * @return True if the object has been modified.
     */
    public boolean isModified()
    {
        return getPersistentObj().isModified();
    }

    /**
     * Returns whether the object has ever been saved.  This will
     * be false, if the object was retrieved from storage or was created
     * and then saved.
     *
     * @return true, if the object has never been persisted.
     */
    public boolean isNew()
    {
        return getPersistentObj().isNew();
    }

    /**
     * Setter for the isNew attribute.  This method will be called
     * by Torque-generated children and Peers.
     *
     * @param b the state of the object.
     */
    public void setNew(boolean b)
    {
        getPersistentObj().setNew(b);
    }

    /**
     * Sets the modified state for the object.
     *
     * @param m The new modified state for the object.
     */
    public void setModified(boolean m)
    {
        getPersistentObj().setModified(m);
    }

    /**
     * Stores the object in the database.  If the object is new,
     * it inserts it; otherwise an update is performed.
     */
    public void save(String dbName) 
        throws Exception
    {
        getPersistentObj().save(dbName);
    }

    /**
     * Stores the object in the database.  If the object is new,
     * it inserts it; otherwise an update is performed.  This method
     * is meant to be used as part of a transaction, otherwise use
     * the save() method and the connection details will be handled
     * internally
     */
    public void save(Connection con) 
        throws Exception
    {
        getPersistentObj().save(con);
    }

    /**
     * Returns the name of this object.
     *
     * @return The name of the object.
     */
    public String getName()
    {
        return PermissionPeerManager.getPermissionName(getPersistentObj());
    }

    /**
     * Sets the name of this object.
     *
     * @param name The name of the object.
     */
    public void setName(String name)
    {
        PermissionPeerManager.setPermissionName(getPersistentObj(), name);
    }


    /**
     * Creates a new Permission in the system.
     *
     * @param name The name of the new Permission.
     * @return An object representing the new Permission.
     * @throws TurbineSecurityException if the Permission could not be created.
     * @deprecated Please use the createPermission method in TurbineSecurity.
     */
    public static Permission create(String name)
        throws TurbineSecurityException
    {
        return TurbineSecurity.createPermission(name);
    }

    /**
     * Makes changes made to the Permission attributes permanent.
     *
     * @throws TurbineSecurityException if there is a problem while
     *  saving data.
     */
    public void save()
        throws TurbineSecurityException
    {
        TurbineSecurity.savePermission(this);
    }

    /**
     * Removes a permission from the system.
     *
     * @throws TurbineSecurityException if the Permission could not be removed.
     */
    public void remove()
        throws TurbineSecurityException
    {
        TurbineSecurity.removePermission(this);
    }

    /**
     * Renames the permission.
     *
     * @param name The new Permission name.
     * @throws TurbineSecurityException if the Permission could not be renamed.
     */
    public void rename(String name)
        throws TurbineSecurityException
    {
        TurbineSecurity.renamePermission(this, name);
    }

    /**
     * Used for ordering SecurityObjects.
     *
     * @param obj The Object to compare to.
     * @return -1 if the name of the other object is lexically greater than this
     *         group, 1 if it is lexically lesser, 0 if they are equal.
     */
    public int compareTo(Object obj)
    {
        if (this.getClass() != obj.getClass())
        {
            throw new ClassCastException();
        }
        String name1 = ((SecurityEntity)obj).getName();
        String name2 = this.getName();
        return name2.compareTo(name1);
    }
}



