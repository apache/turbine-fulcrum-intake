package org.apache.fulcrum.security.util;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
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
        StringBuffer sbuf = new StringBuffer(12 * size());
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

