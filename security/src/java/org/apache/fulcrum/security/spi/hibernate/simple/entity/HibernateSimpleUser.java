/*
 * Created on Aug 24, 2003
 *
 */
package org.apache.fulcrum.security.spi.hibernate.simple.entity;
import java.util.Set;

import org.apache.fulcrum.security.model.simple.entity.SimpleUser;
/**
 * @author Eric Pugh
 *
 * Helper class for Hibernate.  For some reason, hibernate won't 
 * map the darn SecuritySet subclasses to sets, even though they
 * implement set.  
 */
public class HibernateSimpleUser extends SimpleUser
{
    void setHibernateGroups(Set groups)
    {
        getGroups().add(groups);
    }
    Set getHibernateGroups()
    {
        return getGroups().getSet();
    }
}
