package org.apache.fulcrum.security.model.dynamic.entity;

/*
 *  Copyright 2001-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.fulcrum.security.entity.Group;
import org.apache.fulcrum.security.entity.User;
import org.apache.fulcrum.security.entity.impl.SecurityEntityImpl;
import org.apache.fulcrum.security.util.GroupSet;

/**
 * Represents the "simple" model where permissions are related to roles, roles
 * are related to groups and groups are related to users, all in many to many
 * relationships.
 * 
 * Users have a set of delegates and delegatee's. If user A has B in their
 * delegates - B assumes A's groups,roles and permissions If user C has D in
 * their delegatees - C assumes D's groups,roles and permissions
 * 
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class DynamicUser extends SecurityEntityImpl implements User
{
    /**
     * Serial number
     */
    private static final long serialVersionUID = 2841311062371647853L;

    private String password;

    private Set groupSet = new GroupSet();

    private Set delegators = new HashSet();

    private Set delegatees = new HashSet();

    /**
     * @return Returns the delegatees.
     */
    public Set getDelegatees()
    {
        return delegatees;
    }

    /**
     * @param delegatees
     *            The delegatees to set.
     */
    public void setDelegatees(Set delegatees)
    {
        this.delegatees = delegatees;
    }

    /**
     * @return Returns the delegators.
     */
    public Set getDelegators()
    {
        return delegators;
    }

    /**
     * @param delegates
     *            The delegators to set.
     */
    public void setDelegators(Set delegates)
    {
        this.delegators = delegates;
    }

    /**
     * @return
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * @param password
     */
    public void setPassword(String password)
    {
        this.password = password;
    }

    /**
     * @return
     */
    public GroupSet getGroups()
    {
        if (groupSet instanceof GroupSet)
            return (GroupSet) groupSet;
        else
        {
            groupSet = new GroupSet(groupSet);
            return (GroupSet) groupSet;
        }
    }

    /**
     * @param groups
     */
    public void setGroups(GroupSet groups)
    {
        if (groups != null)
            this.groupSet = groups;
        else
            this.groupSet = new GroupSet();
    }

    public void removeGroup(Group group)
    {
        getGroups().remove(group);
    }

    public void addGroup(Group group)
    {
        getGroups().add(group);
    }

    public void setGroupsAsSet(Set groups)
    {
        this.groupSet = groups;
    }

    public Set getGroupsAsSet()
    {
        return groupSet;
    }

    /**
     * Calculate a hash code for this object
     * 
     * @see org.apache.fulcrum.security.entity.impl.SecurityEntityImpl#hashCode()
     */
    public int hashCode()
    {
        return new HashCodeBuilder(45, 13)
                    .append(getPassword())
                    .appendSuper(super.hashCode())
                    .toHashCode();
    }
}
