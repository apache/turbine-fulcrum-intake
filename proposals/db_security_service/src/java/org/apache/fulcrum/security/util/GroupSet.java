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

import org.apache.fulcrum.security.entity.Group;

/**
 * This class represents a set of Groups. It's useful for building
 * administration UI.  It enforces that only
 * Group objects are allowed in the set and only relevant methods
 * are available. 
 *
 * @author <a href="mailto:jmcnally@collab.net">John D. McNally</a>
 * @author <a href="mailto:bmclaugh@algx.net">Brett McLaughlin</a>
 * @author <a href="mailto:marco@intermeta.de">Marco Kn&uuml;ttel</a>
 * @version $Id$
 */
public class GroupSet
    extends SecuritySet
{
    /**
     * Constructs an empty GroupSet
     */
    public GroupSet()
    {
        super();
    }

    /**
     * Constructs a new GroupSet with specified contents.
     *
     * If the given collection contains multiple objects that are
     * identical WRT equals() method, some objects will be overwriten.
     *
     * @param groups A collection of groups to be contained in the set.
     */
    public GroupSet(Collection groups)
    {
        super(groups); 
    }

    /**
     * Adds a Group to this GroupSet.
     *
     * @param group A Group.
     * @return True if Group was added; false if GroupSet
     * already contained the Group.
     */
    public boolean add(Group group)
    {
        return set.add((Object) group);
    }

    /**
     * Adds the Groups in another GroupSet to this GroupSet.
     *
     * @param groupSet A GroupSet.
     * @return True if this GroupSet changed as a result; false
     * if no change to this GroupSet occurred (this GroupSet
     * already contained all members of the added GroupSet).
     */
    public boolean add(GroupSet groupSet)
    {
        return set.addAll(groupSet.getSet());
    }

    /**
     * Removes a Group from this GroupSet.
     *
     * @param group A Group.
     * @return True if this GroupSet contained the Group
     * before it was removed.
     */
    public boolean remove(Group group)
    {
        return set.remove((Object) group);
    }

    /**
     * Checks whether this GroupSet contains a Group.
     *
     * @param group A Group.
     * @return True if this GroupSet contains the Group,
     * false otherwise.
     */
    public boolean contains(Group group)
    {
        return set.contains((Object) group);
    }

    /**
     * Returns a Group with the given name, if it is contained in
     * this GroupSet.
     *
     * @param groupName Name of Group.
     * @return Group if argument matched a Group in this
     * GroupSet; null if no match.
     */
    public Group getGroup(String groupName)
    {
        Iterator iter = set.iterator();
        while (iter.hasNext())
        {
            Group group = (Group) iter.next();
            if (groupName != null  &&
                 groupName.equals(group.getName()))
            {
                return group;
            }
        }
        return null;
    }

    /**
     * Returns an Groups [] of Groups in this GroupSet.
     *
     * @return A Group [].
     */
    public Group [] getGroupsArray()
    {
        return (Group []) set.toArray(new Group[0]);
    }

    /**
     * Print out a GroupSet as a String
     *
     * @returns The Group Set as String
     *
     */
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("GroupSet contains:\n");

        for(Iterator it = elements(); it.hasNext(); )
        {
            sb.append("  Group "+((Group)it.next()).getName()+"\n");
        }

        return sb.toString();
    }
}
