package org.apache.fulcrum.security.util;


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


import java.io.Serializable;

import java.util.Collection;
import java.util.Iterator;

import org.apache.fulcrum.security.entity.Permission;
import org.apache.fulcrum.security.entity.SecurityEntity;

/**
 * This class represents a set of Permissions.  It makes it easy to
 * build a UI that would allow someone to add a group of Permissions
 * to a Role.  It enforces that only
 * Permission objects are allowed in the set and only relevant methods
 * are available.
 *
 * @author <a href="mailto:jmcnally@collab.net">John D. McNally</a>
 * @author <a href="mailto:bmclaugh@algx.net">Brett McLaughlin</a>
 * @author <a href="mailto:marco@intermeta.de">Marco Kn&uuml;ttel</a>
 * @version $Id$
 */
public class PermissionSet
    extends SecuritySet
{
    /**
     * Constructs an empty PermissionSet
     */
    public PermissionSet()
    {
        super();
    }

    /**
     * Constructs a new PermissionSet with specifed contents.
     *
     * If the given collection contains multiple objects that are
     * identical WRT equals() method, some objects will be overwriten.
     *
     * @param permissions A collection of permissions to be contained in the set.
     */
    public PermissionSet(Collection permissions)
    {
        super(permissions);
    }

    /**
     * Adds a Permission to this PermissionSet.
     *
     * @param permission A Permission.
     * @return True if Permission was added; false if PermissionSet
     * already contained the Permission.
     */
    public boolean add(Permission permission)
    {
        return set.add((Object) permission);
    }

    /**
     * Adds the Permissions in another PermissionSet to this
     * PermissionSet.
     *
     * @param permissionSet A PermissionSet.
     *
     * @return True if this PermissionSet changed as a result; false
     * if no change to this PermissionSet occurred (this PermissionSet
     * already contained all members of the added PermissionSet).
     */
    public boolean add(PermissionSet permissionSet)
    {
        return set.addAll(permissionSet.getSet());
    }

    /**
     * Removes a Permission from this PermissionSet.
     *
     * @param permission A Permission.
     * @return True if this PermissionSet contained the Permission
     * before it was removed.
     */
    public boolean remove(Permission permission)
    {
        return set.remove((Object) permission);
    }

    /**
     * Checks whether this PermissionSet contains a Permission.
     *
     * @param permission A Permission.
     * @return True if this PermissionSet contains the Permission,
     * false otherwise.
     */
    public boolean contains(Permission permission)
    {
        return set.contains((Object) permission);
    }

    /**
     * Returns a Permission with the given name, if it is contained in
     * this PermissionSet.
     *
     * @param permissionName Name of Permission.
     * @return Permission if argument matched a Permission in this
     * PermissionSet; null if no match.
     */
    public Permission getPermission(String permissionName)
    {
        Iterator iter = set.iterator();
        while (iter.hasNext())
        {
            Permission permission = (Permission) iter.next();
            if (permissionName != null  &&
                 permissionName.equals(permission.getName()))
            {
                return permission;
            }
        }
        return null;
    }

    /**
     * Returns an Permissions [] of Permissions in this PermissionSet.
     *
     * @return A Permission [].
     */
    public Permission [] getPermissionsArray()
    {
        return (Permission []) set.toArray(new Permission[0]);
    }

    /**
     * Print out a PermissionSet as a String
     *
     * @returns The Permission Set as String
     *
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("PermissionSet contains:\n");

        for(Iterator it = elements(); it.hasNext(); )
        {
            sb.append("  Permission "+((Permission)it.next()).getName()+"\n");
        }

        return sb.toString();
    }
}
