package org.apache.fulcrum.script;

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

import java.util.Hashtable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import javax.script.SimpleScriptContext;

import org.apache.fulcrum.script.impl.ScriptRunnableImpl;
import org.apache.fulcrum.testcontainer.BaseUnitTest;

/**
 * Common test cases for all scripting languages.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */
public abstract class AbstractScriptTest extends BaseUnitTest
{
    protected ScriptService scriptService;

    /**
     * Defines the test case name for JUnit.
     *
     * @param name the test case name.
     */
    public AbstractScriptTest(String name)
    {
        super(name);
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        this.scriptService = (ScriptService) this.lookup(ScriptService.class.getName());
    }

    /**
     * Taken from the JSR 223 reference implementation.
     *
     * @throws Exception the test failed
     */
    public void testCompilableInterface() throws Exception
    {
        for (int i = 0; i < 3; i++)
        {
            SimpleBindings args = new SimpleBindings();
            args.put("count", new Integer(i));
            args.put("currentTime", new Long(System.currentTimeMillis()));
            this.scriptService.eval("CompilableInterface", args);
        }
    }

    /**
     * Taken from the JSR 223 reference implementation.
     *
     * @throws Exception the test failed
     */
    public void testHelloWorld() throws Exception
    {
        this.scriptService.eval("HelloWorld");
    }

    /**
     * Taken from the JSR 223 reference implementation. This test
     * does not work with the "small" bsh interpreter. The scope
     * of the test is to expose a script over a Java interface
     * where the underlying implementation keeps state.
     *
     * @throws Exception the test failed
     */
    public void testInvocableIntf() throws Exception
    {
        MyInterface myInterface = (MyInterface) scriptService.getInterface(
                MyInterface.class
        );

        System.out.println("Initial value for X: " + myInterface.getX());
        assertEquals("X string", myInterface.getX());
        myInterface.setX("New string");
        System.out.println("Current value for X: " + myInterface.getX());
        assertEquals("New string", myInterface.getX());

        System.out.println("\nInitial value for Y: " + myInterface.getY());
        assertEquals("5", myInterface.getY().toString());
        myInterface.setY(100);
        System.out.println("Current value for Y: " + myInterface.getY());
        assertEquals("100", myInterface.getY().toString());
    }

    /**
     * Taken from the JSR 223 reference implementation.
     *
     * @throws Exception the test failed
     */
    public void testNamespaceDemo2() throws Exception
    {
        ScriptEngine engine = this.scriptService.getScriptEngine();

        // create two simple namespaces
        SimpleBindings eNamespace = new SimpleBindings();
        eNamespace.put("key", new Testobj("Original engine scope."));
        SimpleBindings cNamespace = new SimpleBindings();
        cNamespace.put("key", new Testobj("Original ENGINE_SCOPE in context."));

        // use external namespace instead of the default one
        engine.setBindings(eNamespace, ScriptContext.ENGINE_SCOPE);
        System.out.println("Starting value of key in engine scope is \"" + engine.get("key") + "\"");
        assertEquals("Testobj containing Original engine scope.", engine.get("key").toString().trim());

        //execute script using the namespace
        this.scriptService.eval("NamespaceDemo2");
        System.out.println("Ending value of key in engine scope is \"" + engine.get("key") + "\"");
        assertEquals("new value", engine.get("key"));

        // create a scriptcontext and set its engine scope namespace
        ScriptContext ctxt = new SimpleScriptContext();
        ctxt.setBindings(cNamespace, ScriptContext.ENGINE_SCOPE);

        this.scriptService.eval("NamespaceDemo2", ctxt);
        System.out.println("Ending value of key in engine scope is \"" + engine.get("key") + "\"");
        assertEquals("new value", engine.get("key"));
        System.out.println("Ending value of key in ENGINE_SCOPE of context is " + ctxt.getAttribute("key", ScriptContext.ENGINE_SCOPE));
        assertEquals("new value", ctxt.getAttribute("key", ScriptContext.ENGINE_SCOPE));
    }

    /**
     * Taken from the JSR 223 reference implementation.
     *
     * @throws Exception the test failed
     */
    public void testNamespaceDemo3() throws Exception
    {
        final int STATE1 = 1;
        final int STATE2 = 2;
        final ScriptEngine engine = this.scriptService.getScriptEngine();
        final ScriptService scriptService = this.scriptService;

        //set engine-scope namespace with state=1
        SimpleBindings n = new SimpleBindings();
        engine.setBindings(n, ScriptContext.ENGINE_SCOPE);
        n.put("state", new Integer(STATE1));

        //create a new thread to run script
        Thread t = new Thread(new Runnable()
        {
            public void run()
            {
                try
                {
                    scriptService.eval("NamespaceDemo3");
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
        t.start();

        // wait for the script engine to start and execute the script
        Thread.sleep(2000);

        //changes state
        n.put("state", new Integer(STATE2));
        t.join();
        System.out.println("Script has executed.. current state is " +
                n.get("state"));
    }

    /**
     * Test access to the Avalon infrastructure.
     *
     * @throws Exception the test failed
     */
    public void testAvalonContext() throws Exception
    {
        SimpleBindings args = new SimpleBindings();
        args.put("bar", new Integer(2));
        Object result = this.scriptService.eval("Avalon", args);
        System.out.println("RESULT ==> " + result);
    }

    /**
     * Test the general performance of the JSR 223 integration.
     *
     * @throws Exception the test failed
     */
    public void testPerformance() throws Exception
    {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 500; i++)
        {
            this.testHelloWorld();
            this.testCompilableInterface();
            this.testAvalonContext();
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        System.out.println("=== Execution took " + duration + " ms ===");
    }

    /**
     * Check ScriptService.exist().
     *
     * @throws Exception the test failed
     */
    public void testExists() throws Exception
    {
        assertTrue(this.scriptService.exists("HelloWorld"));
        assertFalse(this.scriptService.exists("grmpff"));
    }

    /**
     * Execute a script using multiple threads.
     *
     * @throws Exception the test failed
     */
    public void testMultithreadingScript() throws Exception
    {
        Hashtable args0 = new Hashtable();
        args0.put("foo", new Double(1));
        ScriptRunnable runnable0 = new ScriptRunnableImpl(this.scriptService, "MultiThreaded", args0);
        Thread thread0 = new Thread(runnable0, "Thread0");

        Hashtable args1 = new Hashtable();
        args1.put("foo", new Double(2));
        ScriptRunnable runnable1 = new ScriptRunnableImpl(this.scriptService, "MultiThreaded", args1);
        Thread thread1 = new Thread(runnable1, "Thread1");

        Hashtable args2 = new Hashtable();
        args2.put("foo", new Double(4));
        ScriptRunnable runnable2 = new ScriptRunnableImpl(this.scriptService, "MultiThreaded", args2);
        Thread thread2 = new Thread(runnable2, "Thread2");

        thread0.start();
        thread1.start();
        thread2.start();

        thread0.join();
        thread1.join();
        thread2.join();

        assertEquals(new Double(2), runnable0.getResult());
        assertEquals(new Double(4), runnable1.getResult());
        assertEquals(new Double(8), runnable2.getResult());
    }

    /**
     * Execute a script resulting in a ScriptException.
     *
     * @throws Exception the test failed
     */
    public void testRuntimeErrorScript() throws Exception
    {
        try
        {
            this.scriptService.eval("RuntimeError");
            fail("Expected ScriptException");
        } catch (ScriptException e)
        {
            // expected
        }
    }

    /**
     * Tests the call() method of an Invocable (e.g. InvocableIntf.js).
     *
     * @throws Exception the test failed
     */
    public void testCall() throws Exception
    {
        String newX = "New X String";
        String oldX = (String) this.scriptService.call("getX", new Object[0]);
        assertFalse(newX.equals(oldX));
        this.scriptService.call("setX", new Object[]{newX});
        newX = (String) this.scriptService.call("getX", new Object[0]);
        this.scriptService.call("setX", new Object[]{oldX});
        String currX = (String) this.scriptService.call("getX", new Object[0]);
        assertFalse(currX.equals(newX));
    }

    /**
     * Tests the locator functionality.
     *
     * @throws Exception the test failed
     */
    public void testLocatorFunctionality() throws Exception
    {
        String result = null;

        // execute locator/foo.extension
        result = this.scriptService.eval("locator/foo").toString();
        assertTrue(result.startsWith("locator/foo."));

        // execute locator/foo/foo.extension which is not found but locator/foo.extension is
        result = this.scriptService.eval("locator/foo/foo").toString();
        assertTrue(result.startsWith("locator/foo."));

        // execute locator/bar/foo.extension
        result = this.scriptService.eval("locator/bar/foo").toString();
        assertTrue(result.startsWith("locator/bar/foo."));
    }
}
