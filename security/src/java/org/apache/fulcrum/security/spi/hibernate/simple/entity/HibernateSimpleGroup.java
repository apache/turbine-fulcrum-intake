/*
 * Created on Aug 24, 2003
 *
 */
package org.apache.fulcrum.security.spi.hibernate.simple.entity;
import java.util.Iterator;
import java.util.Set;
import org.apache.fulcrum.security.entity.User;
import org.apache.fulcrum.security.model.simple.entity.SimpleGroup;
/**
 * @author Eric Pugh
 *
 * Helper class for Hibernate.  For some reason, hibernate won't 
 * map the darn SecuritySet subclasses to sets, even though they
 * implement set.  
 */
public class HibernateSimpleGroup extends SimpleGroup
{
    private UserSet users = new UserSet();
    public void addUser(User user)
    {
        users.add(user);
    }
    public void removeUser(User user)
    {
        users.remove(user);
    }
    public void setUsers(Set users)
    {
        this.users.addAll(users);
    }
    public Set getUsers()
    {
        return this.users;
    }
    void setHibernateUsers(Set users)
    {
        for (Iterator i = users.iterator(); i.hasNext();)
        {
            User user = (User) i.next();
            this.users.add(user);
        }
    }
    Set getHibernateUsers()
    {
        return users.getSet();
    }
    void setHibernateRoles(Set roles)
    {
        this.getRoles().add(roles);
    }
    Set getHibernateRoles()
    {
        return getRoles().getSet();
    }
}
