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

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.ExcludeTags;
import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.SuiteDisplayName;
import org.junit.runner.RunWith;


/**
 * Regression test for Rhino Javascript
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */
@RunWith(JUnitPlatform.class)
@SuiteDisplayName("JUnit Rhino Script Test Suite")
@ExcludeTags("Ignore4Rhino")
//@SelectPackages("org.apache.fulcrum.script")
@IncludeClassNamePatterns("^(.*RhinoTest.*|.*AbstractScriptTest.*)$")
public class RhinoTest extends AbstractScriptTest
{
    /**
     * Defines the testcase name for JUnit.
     *
     * @param name the testcase's name.
     */
    public RhinoTest()
    {
        super();       
        this.setConfigurationFileName("./src/test/TestRhinoComponentConfig.xml");
    }
    

    @BeforeEach
    protected void setUp() throws Exception
    {
        super.setUp();
    }

    /**
     * A quick test case for directly evaluating a script using the
     * plain JDK 1.6 or 1.7 implementation.
     * 
     * See also https://docs.oracle.com/javase/10/nashorn/JSNUG.pdf
     */
    @Test
    public void testDirectInvocation() throws Exception
    {
        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName("JavaScript");
        engine.put("n", 10);
        String script =
                "function fib(n){ return n < 2 ? n : fib(n-1) + fib(n-2); }\n" +
                        "fib(n);"; // this will be returned

        Number result = (Number) engine.eval(script);
        Assert.assertEquals(55, result.intValue());
    }
}
