package org.apache.fulcrum.security.model.basic.entity;
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

import java.util.Set;

import org.apache.fulcrum.security.entity.Group;
import org.apache.fulcrum.security.entity.User;
import org.apache.fulcrum.security.entity.impl.SecurityEntityImpl;
import org.apache.fulcrum.security.util.UserSet;

/**
 * Represents the "basic" model where users are part of groups, but nothing
 * else.
 * 
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class BasicGroup extends SecurityEntityImpl implements Group
{
	private Set userSet = new UserSet();
	/**
	 * @return
	 */
	public UserSet getUsers()
	{
	    if( userSet instanceof UserSet )
	        return (UserSet) userSet;
	    else {
	        userSet = new UserSet(userSet);
	        return (UserSet)userSet;
	    }
	}

	/**
	 * @param userSet
	 */
	public void setUsers(UserSet userSet)
	{
	    if( userSet != null )
	        this.userSet = userSet;
	    else
	        this.userSet = new UserSet();
	}
    
	/**
	 * @return
	 */
	public Set getUsersAsSet()
	{
	    return userSet;
	}

	/**
	 * @param userSet
	 */
	public void setUsersAsSet(Set users)
	{
	    this.userSet = users;
	}    

	
	
	public void addUser(User user)
	{
		getUsers().add(user);
	}
	public void removeUser(User user)
	{
		getUsers().remove(user);
	}
    
	
}
