package org.apache.fulcrum.security.model.turbine.entity;

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

import java.io.Serializable;

import org.apache.fulcrum.security.entity.Group;
import org.apache.fulcrum.security.entity.Role;
import org.apache.fulcrum.security.entity.User;


/**
 * Represents the "turbine" model where permissions are in a many to many
 * relationship to roles, roles are related to groups are related to users, all
 * in many to many relationships.
 * 
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh </a>
 * @version $Id$
 */
public class TurbineUserGroupRole implements Serializable {
    private User user;
    private Group group;
    private Role role;
    private int hashCode;
    private boolean hashCodeGenerated=false;
    
    /**
     * @return Returns the group.
     */
    public Group getGroup() {
        return group;
    }
    /**
     * @return Returns the role.
     */
    public Role getRole() {
        return role;
    }
    /**
     * @return Returns the user.
     */
    public User getUser() {
        return user;
    }
    /**
     * @param group The group to set.
     */
    public void setGroup(Group group) {
        this.group = group;
    }
    /**
     * @param role The role to set.
     */
    public void setRole(Role role) {
        this.role = role;
    }
    /**
     * @param user The user to set.
     */
    public void setUser(User user) {
        this.user = user;
    }
    
    public boolean equals(Object obj)
    {
        if (null == obj)
        {
            return false;
        }
        if (!(obj instanceof TurbineUserGroupRole))
        {
            return false;
        }
        else
        {
            TurbineUserGroupRole mObj =(TurbineUserGroupRole) obj;
            if (null != this.getRole() && null != mObj.getRole())
            {
                if (!this.getRole().equals(mObj.getRole()))
                {
                    return false;
                }
            }
            else
            {
                return false;
            }
            if (null != this.getUser() && null != mObj.getUser())
            {
                if (!this.getUser().equals(mObj.getUser()))
                {
                    return false;
                }
            }
            else
            {
                return false;
            }
            if (null != this.getGroup() && null != mObj.getGroup())
            {
                if (!this.getGroup().equals(mObj.getGroup()))
                {
                    return false;
                }
            }
            else
            {
                return false;
            }
            return true;
        }
    }


    public int hashCode()
    {
        if (!hashCodeGenerated)
        {
            StringBuffer sb = new StringBuffer();
            if (null != this.getRole())
            {
                sb.append(this.getRole().hashCode());
                sb.append(":");
            }
            else
            {
                return super.hashCode();
            }
            if (null != this.getUser())
            {
                sb.append(this.getUser().hashCode());
                sb.append(":");
            }
            else
            {
                return super.hashCode();
            }
            if (null != this.getGroup())
            {
                sb.append(this.getGroup().hashCode());
                sb.append(":");
            }
            else
            {
                return super.hashCode();
            }
            this.hashCode = sb.toString().hashCode();
        }
        return this.hashCode;
    }


    public String toString()
    {
        return super.toString();
    }    
}