package org.apache.fulcrum.security.util;
/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2002 The Apache Software Foundation.  All rights
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

import junit.framework.TestCase;

import org.apache.fulcrum.security.entity.Group;
import org.apache.fulcrum.security.model.dynamic.entity.DynamicGroup;
/**
 * Test using a SecuritySet.  Useing various subclasses since it is
 * Abstract.
 * 
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class SecuritySetTest extends TestCase
{

    /**
     * Defines the testcase name for JUnit.
     * 
     * @param name the testcase's name.
     */
    public SecuritySetTest(String name)
    {
        super(name);
    }
    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(SecuritySetTest.class);
    }

    public void testNull() throws Exception
    {
        SecuritySet securitySet = new GroupSet();
        assertFalse(securitySet.contains(null));
    }

    public void testContainsName()
    {
        SecuritySet securitySet = new GroupSet();
        assertFalse(securitySet.containsName(null));
        Group g = new DynamicGroup();
        g.setName("BOB");

        ((GroupSet) securitySet).add(g);
        assertTrue(((GroupSet) securitySet).containsName("bob"));
        assertTrue(((GroupSet) securitySet).containsName("BOB"));

    }

    public void testRemoveAll()
    {
        SecuritySet securitySet = new GroupSet();
        assertFalse(securitySet.containsName(null));
        Group g = new DynamicGroup();
        g.setName("BOB");
        g.setId("BOB");

        ((GroupSet) securitySet).add(g);

        SecuritySet securitySet2 = new GroupSet();
        assertFalse(securitySet.containsName(null));
        g = new DynamicGroup();
        g.setName("BOB");
        g.setId("BOB");

        ((GroupSet) securitySet2).add(g);
        securitySet.removeAll(securitySet2);
        assertEquals(0, securitySet.size());
    }

    public void testToArray() throws Exception
    {
        SecuritySet securitySet = getTestData();
        Object array[] = securitySet.toArray();
        assertEquals(2, array.length);
        Object array2[] = new Object[2];
        array2[0]="hi";
        Object array3[]= securitySet.toArray(array2);
		assertEquals(2, array3.length);
    }

    private SecuritySet getTestData()
    {
        SecuritySet securitySet = new GroupSet();
        assertFalse(securitySet.containsName(null));
        Group g = new DynamicGroup();
        g.setName("JOE");
        g.setId("JOE");

        Group g2 = new DynamicGroup();
        g2.setName("RICK");
        g2.setId("RICK");

		((GroupSet) securitySet).add(g);
		((GroupSet) securitySet).add(g2);

        return securitySet;
    }

}
