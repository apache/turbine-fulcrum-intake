/*
 * Created on Aug 21, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.apache.fulcrum.security.entity.impl;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.fulcrum.security.entity.SecurityEntity;
/**
 * @author Eric Pugh
 *
 * Base class for all objects implementing SecurityEnitity.  This
 * class automatically lowercases the name.  So the permission "EDIT"
 * will equal "eDit" and "edit";
 */
public class SecurityEntityImpl implements SecurityEntity
{
    private String name;
    private long id;
    /**
     * @return
     */
    public long getId()
    {
        return id;
    }
    /**
     * @param id
     */
    public void setId(long id)
    {
        this.id = id;
    }
    /**
     * @return
     */
    public String getName()
    {
        return name;
    }
    /**
     * @param name
     */
    public void setName(String name)
    {
        this.name = name.toLowerCase();
    }
    public String toString()
    {
        return getClass().getName() + "id " + getId() + " name " + getName();
    }
    public boolean equals(Object o)
    {
        boolean equals = true;
        if (o == null)
        {
            equals = false;
        }
        else
        {
            equals = (getId() == ((SecurityEntityImpl) o).getId());
        }
        return equals;
    }
    public int hashCode(Object o)
    {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
