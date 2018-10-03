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


import java.util.Collection;
import java.util.Iterator;

import org.apache.fulcrum.security.entity.Role;

/**
 * This class represents a set of Roles.  It makes it easy to build a
 * UI that would allow someone to add a group of Roles to a User. 
 * It enforces that only Role objects are
 * allowed in the set and only relevant methods are available.
 *
 * @author <a href="mailto:jmcnally@collab.net">John D. McNally</a>
 * @author <a href="mailto:bmclaugh@algx.net">Brett McLaughlin</a>
 * @author <a href="mailto:marco@intermeta.de">Marco Kn&uuml;ttel</a>
 * @version $Id$
 */
public class RoleSet
    extends SecuritySet
{
    /**
     * Constructs an empty RoleSet
     */
    public RoleSet()
    {
        super();
    }

    /**
     * Constructs a new RoleSet with specified contents.
     *
     * If the given collection contains multiple objects that are
     * identical WRT equals() method, some objects will be overwriten.
     *
     * @param roles A collection of roles to be contained in the set.
     */
    public RoleSet(Collection roles)
    {
        super(roles);
    }

    /**
     * Adds a Role to this RoleSet.
     *
     * @param role A Role.
     * @return True if Role was added; false if RoleSet already
     * contained the Role.
     */
    public boolean add(Role role)
    {
        return set.add((Object) role);
    }

    /**
     * Adds the Roles in another RoleSet to this RoleSet.
     *
     * @param roleSet A RoleSet.
     * @return True if this RoleSet changed as a result; false if no
     * change to this RoleSet occurred (this RoleSet already contained
     * all members of the added RoleSet).
     */
    public boolean add(RoleSet roleSet)
    {
        return set.addAll(roleSet.getSet());
    }

    /**
     * Removes a Role from this RoleSet.
     *
     * @param role A Role.
     * @return True if this RoleSet contained the Role before it was
     * removed.
     */
    public boolean remove(Role role)
    {
        return set.remove((Object) role);
    }

    /**
     * Checks whether this RoleSet contains a Role.
     *
     * @param role A Role.
     * @return True if this RoleSet contains the Role, false
     * otherwise.
     */
    public boolean contains(Role role)
    {
        return set.contains((Object) role);
    }

    /**
     * Returns a Role with the given name, if it is contained in this
     * RoleSet.
     *
     * @param roleName Name of Role.
     * @return Role if argument matched a Role in this RoleSet; null
     * if no match.
     */
    public Role getRole(String roleName)
    {
        Iterator iter = set.iterator();
        while (iter.hasNext())
        {
            Role role = (Role) iter.next();
            if (roleName != null  &&
                roleName.equals(role.getName()))
            {
                return role;
            }
        }
        return null;
    }

    /**
     * Returns an Roles [] of Roles in this RoleSet.
     *
     * @return A Role [].
     */
    public Role [] getRolesArray()
    {
        return (Role []) set.toArray(new Role[0]);
    }

    /**
     * Print out a RoleSet as a String
     *
     * @returns The Role Set as String
     *
     */
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("RoleSet contains:\n");

        for(Iterator it = elements(); it.hasNext(); )
        {
            sb.append("  Role "+((Role)it.next()).getName()+"\n");
        }

        return sb.toString();
    }
}
