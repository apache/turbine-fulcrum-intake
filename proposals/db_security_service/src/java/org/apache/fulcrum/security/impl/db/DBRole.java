package org.apache.fulcrum.security.impl.db;


/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import java.sql.Connection;

import java.util.Iterator;

import org.apache.fulcrum.security.TurbineSecurity;

import org.apache.fulcrum.security.entity.Permission;
import org.apache.fulcrum.security.entity.Role;

import org.apache.fulcrum.security.util.PermissionSet;
import org.apache.fulcrum.security.util.TurbineSecurityException;

import org.apache.torque.om.Persistent;

/**
 * This class represents a role played by the User associated with the
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

public class DBRole
    extends DBObject
    implements Role,
               Comparable,
               Persistent
{
    /** The permissions for this role. */
    private PermissionSet permissionSet = null;

    /**
     * Constructs a new Role
     */
    public DBRole()
    {
        super();
    }

    /**
     * Constructs a new Role with the specified name.
     *
     * @param name The name of the new object.
     */
    public DBRole(String name)
    {
        super(name);
    }

    /**
     * The package private Constructor is used when the RolePeerManager
     * has retrieved a list of Database Objects from the peer and
     * must 'wrap' them into DBRole Objects. You should not use it directly!
     *
     * @param obj An Object from the peer
     */
    public DBRole(Persistent obj)
    {
        super(obj);
    }

    /**
     * Returns the underlying Object for the Peer
     *
     * Used in the RolePeerManager when building a new Criteria.
     *
     * @return The underlying persistent object
     *
     */

    public Persistent getPersistentObj()
    {
        if (obj == null)
        {
            obj = RolePeerManager.newPersistentInstance();
        }
        return obj;
    }

    /**
     * Returns the name of this role.
     *
     * @return The name of the role.
     */
    public String getName()
    {
        return RolePeerManager.getRoleName(getPersistentObj());
    }

    /**
     * Sets the name of this Role
     *
     * @param name The name of the role.
     */
    public void setName(String name)
    {
        RolePeerManager.setRoleName(getPersistentObj(), name);
    }

    /**
     * Returns the set of Permissions associated with this Role.
     *
     * @return A PermissionSet.
     *
     * @exception Exception a generic exception.
     */
    public PermissionSet getPermissions()
        throws Exception
    {
        return permissionSet;
    }

    /**
     * Sets the Permissions associated with this Role.
     *
     * @param permissionSet A PermissionSet.
     */
    public void setPermissions(PermissionSet permissionSet)
    {
        this.permissionSet = permissionSet;
    }

    // These following methods are wrappers around TurbineSecurity

    /**
     * Creates a new Role in the system.
     *
     * @param name The name of the new Role.
     * @return An object representing the new Role.
     * @throws TurbineSecurityException if the Role could not be created.
     */
    public Role create(String name)
        throws TurbineSecurityException
    {
        return TurbineSecurity.createRole(name);
    }

    /**
     * Makes changes made to the Role attributes permanent.
     *
     * @throws TurbineSecurityException if there is a problem while
     *  saving data.
     */
    public void save()
        throws TurbineSecurityException
    {
        TurbineSecurity.saveRole(this);
    }

    /**
     * Removes a role from the system.
     *
     * @throws TurbineSecurityException if the Role could not be removed.
     */
    public void remove()
        throws TurbineSecurityException
    {
        TurbineSecurity.removeRole(this);
    }

    /**
     * Renames the role.
     *
     * @param name The new Role name.
     * @throws TurbineSecurityException if the Role could not be renamed.
     */
    public void rename(String name)
        throws TurbineSecurityException
    {
        TurbineSecurity.renameRole(this, name);
    }

    /**
     * Grants a Permission to this Role.
     *
     * @param permission A Permission.
     * @throws TurbineSecurityException if there is a problem while assigning
     * the Permission.
     */
    public void grant(Permission permission)
        throws TurbineSecurityException
    {
        TurbineSecurity.grant(this, permission);
    }

    /**
     * Grants Permissions from a PermissionSet to this Role.
     *
     * @param permissionSet A PermissionSet.
     * @throws TurbineSecurityException if there is a problem while assigning
     * the Permissions.
     */
    public void grant(PermissionSet permissionSet)
        throws TurbineSecurityException
    {
        Iterator permissions = permissionSet.elements();
        while (permissions.hasNext())
        {
            TurbineSecurity.grant(this, (Permission) permissions.next());
        }
    }

    /**
     * Revokes a Permission from this Role.
     *
     * @param permission A Permission.
     * @throws TurbineSecurityException if there is a problem while unassigning
     * the Permission.
     */
    public void revoke(Permission permission)
        throws TurbineSecurityException
    {
        TurbineSecurity.revoke(this, permission);
    }

    /**
     * Revokes Permissions from a PermissionSet from this Role.
     *
     * @param permissionSet A PermissionSet.
     * @throws TurbineSecurityException if there is a problem while unassigning
     * the Permissions.
     */
    public void revoke(PermissionSet permissionSet)
        throws TurbineSecurityException
    {
        Iterator permissions = permissionSet.elements();
        while (permissions.hasNext())
        {
            TurbineSecurity.revoke(this, (Permission) permissions.next());
        }
    }
}
