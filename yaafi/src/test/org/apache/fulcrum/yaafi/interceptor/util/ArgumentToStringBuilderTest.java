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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test suite for the SmartToStringBuilderImpl.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public class ArgumentToStringBuilderTest
{
    private String result;
    private ArgumentToStringBuilderImpl toStringBuilder;
    private int maxArgLength = 100;
    int mode = 3;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @BeforeEach
    protected void setUp() throws Exception
    {
        this.toStringBuilder = new ArgumentToStringBuilderImpl();
        this.toStringBuilder.setMaxArgLength( this.maxArgLength );
        this.toStringBuilder.setMode( this.mode );
        System.out.println( "=== " + this.getClass().getName() + " ====================================" );
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @AfterEach
    protected void tearDown() throws Exception
    {
        System.out.println( this.result );
    }

    /**
     * Test with a simple String
     */
    @Test
    public void testString()
    {
        String target = "In vino veritas";
        this.toStringBuilder.setTarget( target );
        result = toStringBuilder.toString();
    }

    /**
     * Test with a simple Integer
     */
    @Test
    public void testInteger()
    {
        Integer target = new Integer( 69 );
        this.toStringBuilder.setTarget( target );
        result = toStringBuilder.toString();
    }

    /**
     * Test with an empty array
     */
    @Test
    public void testEmptyArray()
    {
        String[] target = {};
        this.toStringBuilder.setTarget( target );
        result = toStringBuilder.toString();
    }

    /**
     * Test with a simple String[]
     */
    @Test
    public void testStringArray()
    {
        String[] target = { "foo", "bar" };
        this.toStringBuilder.setTarget( target );
        result = toStringBuilder.toString();
    }

    /**
     * Test with a simple Integer[]
     */
    @Test
    public void testIntegerArray()
    {
        Integer[] target = { new Integer( 4711 ), new Integer( 815 ) };
        this.toStringBuilder.setTarget( target );
        result = toStringBuilder.toString();
    }

    /**
     * Test with an Exception
     */
    @Test
    public void testException()
    {
        Exception target = new RuntimeException( this.getClass().getName() );
        this.toStringBuilder.setTarget( target );
        result = toStringBuilder.toString();
    }

    /**
     * Test with an NULL object
     */
    @Test
    public void testNull()
    {
        Object target = null;
        this.toStringBuilder.setTarget( target );
        result = toStringBuilder.toString();
    }

    /**
     * Test with an array containing NULL values
     */
    @Test
    public void testWithANullArray()
    {
        Object target = new String[] { "foo", null, "bar" };
        this.toStringBuilder.setTarget( target );
        result = toStringBuilder.toString();
    }

    /**
     * Create a plain vanilla Java object
     * 
     * @throws Exception generic exception
     */
    @Test
    public void testPlainVanillaObject() throws Exception
    {
        File target = new File( "./LICENSE.txt" );
        this.toStringBuilder.setTarget( target );
        result = toStringBuilder.toString();
    }

    /**
     * Test with a char[]
     */
    @Test
    public void testCharArray()
    {
        char[] target =  this.getClass().getName().toCharArray();
        this.toStringBuilder.setTarget( target );
        result = toStringBuilder.toString();
    }

    /**
     * Test with a long char[] which will be truncated
     */
    @Test
    public void testLongCharArray()
    {
        char[] target = System.getProperties().toString().toCharArray();
        this.toStringBuilder.setTarget( target );
        result = toStringBuilder.toString();
    }

    /**
     * Test with a byte[]
     */
    @Test
    public void testByteArray()
    {
        Exception target = new RuntimeException(  this.getClass().getName() );
        this.toStringBuilder.setTarget( target.toString().getBytes() );
        result = toStringBuilder.toString();
    }

    /**
     * Test with a multidimensional array
     */
    @Test
    public void testMultiDimensionalArray()
    {
        String[] row1 = { "r1.1", "1.2", "r1.3" };
        int[] row2 = { 1, 2, 3 };
        String[] row3 = { "r3.1" };
        Object[] target = { row1, row2, row3, this.getClass().getName().toCharArray() };

        this.toStringBuilder.setTarget( target );
        result = toStringBuilder.toString();
    }

    /**
     * Test with a hashtable
     */
    @Test
    public void testHashtable()
    {
        Hashtable target = new Hashtable();
        target.put( "foo", "foo" );
        target.put( "bar", "bar" );

        this.toStringBuilder.setTarget( target );
        result = toStringBuilder.toString();
    }

    /**
     * Test with a java.util.Properties
     */
    @Test
    public void testProperties()
    {
        Properties target = System.getProperties();

        this.toStringBuilder.setTarget( target );
        result = toStringBuilder.toString();
    }

    /**
     * Test with an ArrayList
     */
    @Test
    public void testArrayList()
    {
        ArrayList<String> target = new ArrayList<>();
        target.add( "foo" );
        target.add( "bar" );

        this.toStringBuilder.setTarget( target );
        result = toStringBuilder.toString();
    }

    /**
     * Test with a non-empty int[]
     */
    @Test
    public void testIntArray()
    {
        int[] target = { 1, 2, 3, 4, 5, 6, 7, 8, 9 };

        this.toStringBuilder.setTarget( target );
        result = toStringBuilder.toString();
    }

    /**
     * Test with an empty int[]
     */
    @Test
    public void testEmptyIntArray()
    {
        int[] target = {};

        this.toStringBuilder.setTarget( target );
        result = toStringBuilder.toString();
    }

    /**
     * Test with an boolean[]
     */
    @Test
    public void testBooleanArray()
    {
        boolean[] target = { true, false };

        this.toStringBuilder.setTarget( target );
        result = toStringBuilder.toString();
    }

    /**
     * Test with an File[]
     */
    @Test
    public void testFileArray()
    {
        File[] target = { new File( "foo" ), new File( "bar" ) };

        this.toStringBuilder.setTarget( target );
        result = toStringBuilder.toString();
    }

}
