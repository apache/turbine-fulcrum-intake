package org.apache.fulcrum.security.spi.hibernate.simple.entity;

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

import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.apache.fulcrum.security.entity.User;
import org.apache.fulcrum.security.util.SecuritySet;

/**
 * This class represents a set of Users.  It is based on UserSet.
 * Hibernate doesn't return the right kind of set, so this is used to
 * force the type of set.
 *
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class UserSet
        extends SecuritySet
{
    /**
     * Constructs an empty UserSet
     */
    public UserSet()
    {
        super();
    }

    /**
     * Constructs a new UserSet with specified contents.
     *
     * If the given collection contains multiple objects that are
     * identical WRT equals() method, some objects will be overwritten.
     *
     * @param users A collection of users to be contained in the set.
     */
    public UserSet(Collection users)
    {
        super();
        add(users);
    }

    /**
     * Adds a User to this UserSet.
     *
     * @param user A User.
     * @return True if User was added; false if UserSet already
     * contained the User.
     */
    public boolean add(User user)
    {
        boolean res = contains(user);
        nameMap.put(user.getName(), user);
        idMap.put(user.getId(), user);
        return res;
    }

    /**
     * Adds the Users in a Collection to this UserSet.
     *
     * @param users A Collection of Users.
     * @return True if this UserSet changed as a result; false
     * if no change to this UserSet occurred (this UserSet
     * already contained all members of the added UserSet).
     */
    public boolean add(Collection users)
    {
        boolean res = false;
        for (Iterator it = users.iterator(); it.hasNext();)
        {
            User r = (User) it.next();
            res |= add(r);
        }
        return res;
    }

    /**
     * Adds the Users in another UserSet to this UserSet.
     *
     * @param userSet A UserSet.
     * @return True if this UserSet changed as a result; false
     * if no change to this UserSet occurred (this UserSet
     * already contained all members of the added UserSet).
     */
    public boolean add(UserSet userSet)
    {
        boolean res = false;
        for( Iterator it = userSet.iterator(); it.hasNext();)
        {
            User r = (User) it.next();
            res |= add(r);
        }
        return res;
    }

    /**
     * Removes a User from this UserSet.
     *
     * @param user A User.
     * @return True if this UserSet contained the User
     * before it was removed.
     */
    public boolean remove(User user)
    {
        boolean res = contains(user);
        nameMap.remove(user.getName());
        idMap.remove(user.getId());
        return res;
    }

    /**
     * Checks whether this UserSet contains a User.
     *
     * @param user A User.
     * @return True if this UserSet contains the User,
     * false otherwise.
     */
    public boolean contains(User user)
    {
        return nameMap.containsValue((Object) user);
    }


    /**
     * Returns a User with the given name, if it is contained in
     * this UserSet.
     *
     * @param userName Name of User.
     * @return User if argument matched a User in this
     * UserSet; null if no match.
     */
    public User getUserByName(String userName)
    {
		userName=userName.toLowerCase();
        return (StringUtils.isNotEmpty(userName))
                ? (User) nameMap.get(userName) : null;
    }

    /**
     * Returns a User with the given id, if it is contained in this
     * UserSet.
     *
     * @param userId id of the User.
     * @return User if argument matched a User in this UserSet; null
     * if no match.
     */
    public User getUserById(Object userId)
    {
        return (userId != null) 
                ? (User) idMap.get(userId) : null;
    }

    /**
     * Returns an Array of Users in this UserSet.
     *
     * @return An Array of User objects.
     */
    public User[] getUsersArray()
    {
        return (User[]) getSet().toArray(new User[0]);
    }

    /**
     * Print out a UserSet as a String
     *
     * @returns The User Set as String
     *
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("UserSet: ");

        for(Iterator it = iterator(); it.hasNext();)
        {
            User r = (User) it.next();
            sb.append('[');
            sb.append(r.getName());
            sb.append(" -> ");
            sb.append(r.getId());
            sb.append(']');
            if (it.hasNext())
            {
                sb.append(", ");
            }
        }

        return sb.toString();
    }
}
