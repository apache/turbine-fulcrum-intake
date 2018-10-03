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

import java.io.Serializable;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.apache.fulcrum.security.entity.SecurityEntity;

/**
 * This class represents a set of Security Entities. 
 * It makes it easy to build a UI.  
 * It wraps a TreeSet object to enforce that only relevant 
 * methods are available.  
 * TreeSet's contain only unique Objects (no duplicates).
 *
 * @author <a href="mailto:jmcnally@collab.net">John D. McNally</a>
 * @author <a href="mailto:bmclaugh@algx.net">Brett McLaughlin</a>
 * @author <a href="mailto:marco@intermeta.de">Marco Kn&uuml;ttel</a>
 * @version $Id$
 */
public abstract class SecuritySet 
    implements Serializable
{
    /** Set to hold the Objects */
    protected TreeSet set;

    /**
     * Constructs an empty Set
     */
    public SecuritySet()
    {
        set = new TreeSet();
    }

    /**
     * Constructs a new Set with specified contents.
     *
     * If the given collection contains multiple objects that are
     * identical WRT equals() method, some objects will be overwriten.
     *
     * @param roles A collection of roles to be contained in the set.
     */
    public SecuritySet(Collection objects)
    {
        this();
        add(objects);
    }

    /**
     * Returns the underlying Set object
     *
     * @return A set Object
     *
     */

    public Set getSet()
    {
        return set;
    }

    /**
     * Adds the Objects in a Collection to this SecuritySet.
     *
     * @param roles A Collection of SecurityObjects.
     *
     * @return True if this Set changed as a result; false
     * if no change to this Set occurred (this Set
     * already contained all members of the added Set).
     */
    public boolean add(Collection objs)
    {
        return set.addAll(objs);
    }

    /**
     * Removes all Objects from this Set.
     */
    public void clear()
    {
        set.clear();
    }

    /**
     * Searches if an Object with a given name is in the
     * Set
     *
     * @param roleName Name of the Security Object.
     * @return True if argument matched an Object in this Set; false
     * if no match.
     */
    public boolean contains(String name)
    {
        Iterator iter = set.iterator();
        while (iter.hasNext())
        {
            SecurityEntity se = (SecurityEntity) iter.next();
            if (name != null  &&
                name.equals(se.getName()))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns an Iterator for Objects in this Set.
     *
     * @return An iterator for the Set
     */
    public Iterator elements()
    {
        return set.iterator();
    }

    /**
     * Returns size (cardinality) of this set.
     *
     * @return The cardinality of this Set.
     */
    public int size()
    {
        return set.size();
    }

    /**
     * list of role names in this set
     *
     * @return The string representation of this Set.
     */
    public String toString()
    {
        StringBuilder sbuf = new StringBuilder(12 * size());
        Iterator i = set.iterator();
        while (i.hasNext()) 
        {
            sbuf.append(((SecurityEntity) i.next()).getName());

            if(i.hasNext())
            {
                sbuf.append(", ");
            }

        }
        return sbuf.toString();
    }

}

