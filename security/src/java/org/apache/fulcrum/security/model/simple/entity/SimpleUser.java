package org.apache.fulcrum.security.model.simple.entity;
import org.apache.fulcrum.security.entity.Group;
import org.apache.fulcrum.security.entity.User;
import org.apache.fulcrum.security.entity.impl.SecurityEntityImpl;
import org.apache.fulcrum.security.util.GroupSet;
/**
 * @author Eric Pugh
 *
 * Represents the "simple" model where permissions are related to roles,
 * roles are related to groups and groups are related to users,
 * all in many to many relationships.  
 */
public class SimpleUser extends SecurityEntityImpl implements User
{
    private String password;
    private GroupSet groups = new GroupSet();
    
   
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
        return groups;
    }
    /**
     * @param groups
     */
    public void setGroups(GroupSet groups)
    {
        this.groups = groups;
    }
    public void removeGroup(Group group)
    {
        getGroups().remove(group);
    }
    public void addGroup(Group group)
    {
        getGroups().add(group);
    }
}
