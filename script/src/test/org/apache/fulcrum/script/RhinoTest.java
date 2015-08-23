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

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Regression test for Rhino Javascript
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */
public class RhinoTest extends AbstractScriptTest
{
    /**
     * Defines the testcase name for JUnit.
     *
     * @param name the testcase's name.
     */
    public RhinoTest(String name)
    {
        super(name);
        this.setConfigurationFileName("./src/test/TestRhinoComponentConfig.xml");
    }

    protected void setUp() throws Exception
    {
        super.setUp();
    }

    /**
     * Add all of our test suites
     */
    public static Test suite()
    {
        TestSuite suite = new TestSuite("RhinoTest");

        suite.addTest(new RhinoTest("testDirectInvocation"));

        suite.addTest(new RhinoTest("testHelloWorld"));
        suite.addTest(new RhinoTest("testAvalonContext"));
        suite.addTest(new RhinoTest("testExists"));
        suite.addTest(new RhinoTest("testPerformance"));
        suite.addTest(new RhinoTest("testMultithreadingScript"));
        suite.addTest(new RhinoTest("testRuntimeErrorScript"));
        suite.addTest(new RhinoTest("testCall"));
        suite.addTest(new RhinoTest("testLocatorFunctionality"));

        // tests from the JSR-223 Reference implementation
        suite.addTest(new RhinoTest("testCompilableInterface"));
        suite.addTest(new RhinoTest("testInvocableIntf"));
        suite.addTest(new RhinoTest("testNamespaceDemo3"));

        // this test does not work any longer with Nashorn
        // suite.addTest(new RhinoTest("testNamespaceDemo2"));

        return suite;
    }

    /**
     * A quick test case for directly evaluating a script using the
     * plain JDK 1.6 or 1.7 implementation.
     */
    public void testDirectInvocation() throws Exception
    {
        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName("JavaScript");
        engine.put("n", 10);
        String script =
                "function fib(n){ return n < 2 ? n : fib(n-1) + fib(n-2); }\n" +
                        "fib(n);"; // this will be returned

        Number result = (Number) engine.eval(script);
        assertEquals(55.0, result);
    }
}
