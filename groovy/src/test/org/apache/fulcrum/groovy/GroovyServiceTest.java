package org.apache.fulcrum.groovy;

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

import org.apache.avalon.framework.component.ComponentException;
import org.apache.fulcrum.testcontainer.BaseUnitTest;
import org.codehaus.groovy.control.CompilationFailedException;

/**
 * GroovyServiceTest
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */
public class GroovyServiceTest extends BaseUnitTest
{
    /**
     * Defines the testcase name for JUnit.
     *
     * @param name the testcase's name.
     */
    public GroovyServiceTest(String name)
    {
        super(name);
    }

    protected void setUp() throws Exception
    {
        super.setUp();
    }

    protected GroovyService getService()
        throws ComponentException
    {
        return (GroovyService) this.lookup(GroovyService.class.getName());
    }

    /**
     * Runs an empty groovy script.
     */
    public void testEmtpyGroovy() throws Exception
    {
        Object[] args = {};
        Object result = this.getService().execute( "empty.groovy", args );
        assertEquals( Boolean.TRUE, result );
    }

    /**
     * Run the test script twice to test if the caching works.
     */
    public void testTestGroovy() throws Exception
    {
        int value = 2;
        Object result = null;
        Object[] args = { new Integer(value) };

        result = this.getService().execute( "test.groovy", args );
        assertEquals( new Integer(value*2), result );

        result = this.getService().execute( "test.groovy", args );
        assertEquals( new Integer(value*2), result );
    }

    /**
     * Run the test script multiple times
     */
    public void testStressGroovy() throws Exception
    {
        int value = 2;
        Object result = null;
        Object[] args = { new Integer(value) };

        for( int i=0;i<10; i++ )
        {
            result = this.getService().execute( "test.groovy", args );
            result = this.getService().execute( "karma.groovy", args );
            result = this.getService().execute( "empty.groovy", args );
        }
    }

    /**
     * Sample showing some Groovy features
     * @throws Exception
     */
    public void testKarmaGroovy() throws Exception
    {
        Object[] args = {};
        Object result = this.getService().execute( "karma.groovy", args );
    }

    /**
     * Test a script which throws a NPE
     */
    public void testBogusGroovy() throws Exception
    {
        try
        {
            Object[] args = {};
            Object result = this.getService().execute( "bogus.groovy", args );
            fail( "The script must throw a NPE" );
        }
        catch (Exception e)
        {
            // success
        }
    }

    /**
     * Test executing a non-existing script
     */
    public void testUnknownGroovy() throws Exception
    {
        try
        {
            Object[] args = {};
            assertFalse( this.getService().exists( "unknown.groovy" ) );
            Object result = this.getService().execute( "unknown.groovy", args );
            fail( "The script must throw a NPE" );
        }
        catch (Exception e)
        {
            // success
        }
    }

    public void testGroovyRunnable() throws Exception
    {
        int value1 = 2;
        Object result = null;
        Object[] args1 = { new Integer(value1) };

        // run it twice with constant arguments

        GroovyRunnable runnable = this.getService().createGroovyRunnable(
            "test.groovy"
            );

        runnable.setArgs( args1 );

        runnable.run();
        assertTrue( runnable.getIsSuccessful() );
        assertEquals( new Integer(value1*2), runnable.getResult() );

        runnable.run();
        assertTrue( runnable.getIsSuccessful() );
        assertEquals( new Integer(value1*2), runnable.getResult() );

        // run it with different arguments

        int value2 = 3;
        Object[] args2 = { new Integer(value2) };
        runnable.setArgs( args2 );
        runnable.run();
        assertTrue( runnable.getIsSuccessful() );
        assertEquals( new Integer(value2*2), runnable.getResult() );

        // create a NPE

        Object[] args3 = { null };
        runnable.setArgs( args3 );
        runnable.run();
        assertFalse( runnable.getIsSuccessful() );
    }

    public void testGroovyScriptLocator() throws Exception
    {
        Object[] args = {};
        Object result = null;

        // direct match
        result = this.getService().execute( "foo/foo.groovy", args );
        assertEquals( "FOO", result );

        // direct match
        result = this.getService().execute( "foo/bar/bar.groovy", args );
        assertEquals( "BAR", result );

        // we have to go up one level to find foo.groofy
        result = this.getService().execute( "foo/bar/foo.groovy", args );
        assertEquals( "FOO", result );

        // we have to go up two levels to find empty.groofy
        result = this.getService().execute( "foo/bar/empty.groovy", args );
        assertEquals( Boolean.TRUE, result );

        try
        {
            // we have to go up two levels to find no script
            result = this.getService().execute( "foo/bar/unknown.groovy", args );
            fail("There is unknown.groovy");
        }
        catch (Exception e)
        {
            // success
        }
    }

    /**
     * Execute one Groovy script with 3 threads. Since the execution of a script
     * is enforced to be single-threaded the script should be executed one
     * after another.
     */
    public void testMultithradingGroovy() throws Exception
    {
        Object[] args0 = { new Integer(0) };
        GroovyRunnable runnable0 = this.getService().createGroovyRunnable( "test.groovy" );
        runnable0.setArgs( args0 );

        Object[] args1 = { new Integer(1) };
        GroovyRunnable runnable1 = this.getService().createGroovyRunnable( "thread.groovy" );
        runnable1.setArgs( args1 );
        Thread thread1 = new Thread(runnable1,"Groovy1");

        Object[] args2 = { new Integer(2) };
        GroovyRunnable runnable2 = this.getService().createGroovyRunnable( "thread.groovy" );
        runnable2.setArgs( args2 );
        Thread thread2 = new Thread(runnable2,"Groovy2");

        thread1.start();
        thread2.start();
        runnable0.run();

        assertTrue( runnable1.getIsSuccessful() );
        assertTrue( runnable2.getIsSuccessful() );

        thread1.join();
        thread2.join();

        assertEquals( new Integer(0), runnable0.getResult() );
        assertEquals( new Integer(1), runnable1.getResult() );
        assertEquals( new Integer(2), runnable2.getResult() );
    }

    /**
     * Test compiling Groovy scripts
     */
    public void testCompilingGroovy() throws Exception
    {
        // compile a valid Groovy script
        this.getService().compile( "test", "return true;" );

        try
        {
            // compile a invalid Groovy script
            this.getService().compile( "test", "import foo.*." );
            fail( "This script shouldn't compile" );
        }
        catch (CompilationFailedException e)
        {
            // expected
        }
    }

    public void testGroovyServiceWithoutCache() throws Exception
    {
        this.setConfigurationFileName( "./src/test/TestNoCacheComponentConfig.xml");
        testMultithradingGroovy();
    }
}
