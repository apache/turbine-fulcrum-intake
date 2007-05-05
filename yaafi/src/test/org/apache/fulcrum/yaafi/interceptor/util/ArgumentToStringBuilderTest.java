package org.apache.fulcrum.yaafi.interceptor.util;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Properties;

import junit.framework.TestCase;

/**
 * Test suite for the SmartToStringBuilderImpl.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public class ArgumentToStringBuilderTest extends TestCase
{
    private String result;
    private ArgumentToStringBuilderImpl toStringBuilder;
    private int maxArgLength = 100;
    int mode = 3;


    /**
     * Constructor
     * @param name the name of the test case
     */
    public ArgumentToStringBuilderTest( String name )
    {
        super(name);
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        this.toStringBuilder = new ArgumentToStringBuilderImpl();
        this.toStringBuilder.setMaxArgLength(this.maxArgLength);
        this.toStringBuilder.setMode(this.mode);
        System.out.println( "=== " + this.getName() + " ====================================");
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void tearDown() throws Exception
    {
        System.out.println(this.result);
        super.tearDown();
    }

    /**
     * Test with a simple String
     */
    public void testString()
    {
        String target = "In vino veritas";
        this.toStringBuilder.setTarget(target);
        result = toStringBuilder.toString();
    }

    /**
     * Test with a simple Integer
     */
    public void testInteger()
    {
        Integer target = new Integer(69);
        this.toStringBuilder.setTarget(target);
        result = toStringBuilder.toString();
    }

    /**
     * Test with an empty array
     */
    public void testEmptyArray()
    {
        String[] target = {};
        this.toStringBuilder.setTarget(target);
        result = toStringBuilder.toString();
    }

    /**
     * Test with a simple String[]
     */
    public void testStringArray()
    {
        String[] target = {"foo","bar"};
        this.toStringBuilder.setTarget(target);
        result = toStringBuilder.toString();
    }

    /**
     * Test with a simple Integer[]
     */
    public void testIntegerArray()
    {
        Integer[] target = {new Integer(4711), new Integer(815)};
        this.toStringBuilder.setTarget(target);
        result = toStringBuilder.toString();
    }

    /**
     * Test with an Exception
     */
    public void testException()
    {
        Exception target = new RuntimeException(this.getName());
        this.toStringBuilder.setTarget(target);
        result = toStringBuilder.toString();
    }

    /**
     * Test with an NULL object
     */
    public void testNull()
    {
        Object target = null;
        this.toStringBuilder.setTarget(target);
        result = toStringBuilder.toString();
    }

    /**
     * Test with an array containing NULL values
     */
    public void testWithANullArray()
    {
        Object target = new String[] { "foo", null, "bar" };
        this.toStringBuilder.setTarget(target);
        result = toStringBuilder.toString();
    }

    /**
     * Create a plain vanilla Java object
     */
    public void testPlainVanillaObject() throws Exception
    {
        File target = new File("./LICENSE.txt");
        this.toStringBuilder.setTarget(target);
        result = toStringBuilder.toString();
    }

    /**
     * Test with a char[]
     */
    public void testCharArray()
    {
        char[] target = this.getName().toCharArray();
        this.toStringBuilder.setTarget(target);
        result = toStringBuilder.toString();
    }

    /**
     * Test with a long char[] which will be truncated
     */
    public void testLongCharArray()
    {
        char[] target = System.getProperties().toString().toCharArray();
        this.toStringBuilder.setTarget(target);
        result = toStringBuilder.toString();
    }

    /**
     * Test with a byte[]
     */
    public void testByteArray()
    {
        Exception target = new RuntimeException(this.getName());
        this.toStringBuilder.setTarget(target.toString().getBytes());
        result = toStringBuilder.toString();
    }

    /**
     * Test with a multidimensional array
     */
    public void  testMultiDimensionalArray()
    {
        String[] row1 = {"r1.1", "1.2", "r1.3" };
        int[] row2 = { 1, 2, 3 };
        String[] row3 = {"r3.1" };
        Object[] target = { row1, row2, row3, this.getName().toCharArray() };

        this.toStringBuilder.setTarget(target);
        result = toStringBuilder.toString();
    }

    /**
     * Test with a hashtable
     */
    public void testHashtable()
    {
        Hashtable target = new Hashtable();
        target.put("foo","foo");
        target.put("bar","bar");

        this.toStringBuilder.setTarget(target);
        result = toStringBuilder.toString();
    }

    /**
     * Test with a java.util.Properties
     */
    public void testProperties()
    {
        Properties target = System.getProperties();

        this.toStringBuilder.setTarget(target);
        result = toStringBuilder.toString();
    }

    /**
     * Test with an ArrayList
     */
    public void testArrayList()
    {
        ArrayList target = new ArrayList();
        target.add("foo");
        target.add("bar");

        this.toStringBuilder.setTarget(target);
        result = toStringBuilder.toString();
    }

    /**
     * Test with a non-empty int[]
     */
    public void testIntArray()
    {
        int[] target = { 1, 2, 3, 4, 5, 6, 7, 8, 9 };

        this.toStringBuilder.setTarget(target);
        result = toStringBuilder.toString();
    }

    /**
     * Test with an empty int[]
     */
    public void testEmptyIntArray()
    {
        int[] target = {};

        this.toStringBuilder.setTarget(target);
        result = toStringBuilder.toString();
    }

    /**
     * Test with an boolean[]
     */
    public void testBooleanArray()
    {
        boolean[] target = { true, false };

        this.toStringBuilder.setTarget(target);
        result = toStringBuilder.toString();
    }

    /**
     * Test with an File[]
     */
    public void testFileArray()
    {
        File[] target = { new File("foo"), new File("bar") };

        this.toStringBuilder.setTarget(target);
        result = toStringBuilder.toString();
    }


}
