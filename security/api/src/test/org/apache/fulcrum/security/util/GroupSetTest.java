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
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class GroupSetTest extends TestCase
{

    /**
	 * Defines the testcase name for JUnit.
	 * 
	 * @param name the testcase's name.
	 */
    public GroupSetTest(String name)
    {
        super(name);
    }
    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(GroupSetTest.class);
    }

    public void testNullGroup() throws Exception {
        GroupSet groupSet = new GroupSet();
        assertFalse(groupSet.contains(null));             
    }
    
    public void testAddGroups() throws Exception
    {
        Group group = new DynamicGroup();
        group.setId(new Integer(1));
        group.setName("Eric");
        GroupSet groupSet = new GroupSet();
        groupSet.add(group);
        assertTrue(groupSet.contains(group));

        Group group2 = new DynamicGroup();
        group2.setName("Kate");
        group2.setId(new Integer(2));
        groupSet.add(group2);

        Group group3 = new DynamicGroup();
        group3.setId(new Integer(1));
        group3.setName("Eric");
        groupSet.add(group3);
        assertTrue(groupSet.contains(group));
        assertTrue(groupSet.contains((Object) group));
        assertTrue(groupSet.contains(group2));
        assertTrue(groupSet.contains(group3));
        assertTrue(groupSet.contains(group));
    }

    public void testGroupSetWithSubclass() throws Exception
    {
        GroupSet groupSet = new GroupSet();
        Group group = new GroupSubClass();
		group.setId(new Integer(1));
		group.setName("Eric");

        groupSet.add(group);
        assertTrue(groupSet.contains(group));

        Group group2 = new DynamicGroup();
        group2.setId(new Integer(1));
        group2.setName("Eric");
        assertTrue(groupSet.contains(group2));

    }

    class GroupSubClass extends DynamicGroup
    {
        private String extraGroupData;

        /**
		 * @return Returns the extraGroupData.
		 */
        public String getExtraGroupData()
        {
            return extraGroupData;
        }

        /**
		 * @param extraGroupData The extraGroupData to set.
		 */
        public void setExtraGroupData(String extraGroupData)
        {
            this.extraGroupData = extraGroupData;
        }

    }

}