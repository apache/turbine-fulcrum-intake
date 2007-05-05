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

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Regression test for Groovy
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */
public class GroovyTest extends AbstractScriptTest
{
    /**
     * Defines the testcase name for JUnit.
     *
     * @param name the testcase's name.
     */
    public GroovyTest(String name)
    {
        super(name);
        this.setConfigurationFileName("./src/test/TestGroovyComponentConfig.xml");
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
        TestSuite suite= new TestSuite("GroovyTest");

        suite.addTest( new GroovyTest("testCompilableInterface") );
        suite.addTest( new GroovyTest("testHelloWorld") );
        suite.addTest( new GroovyTest("testNamespaceDemo2") );
        suite.addTest( new GroovyTest("testNamespaceDemo3") );

        suite.addTest( new GroovyTest("testAvalonContext") );
        suite.addTest( new GroovyTest("testExists") );
        suite.addTest( new GroovyTest("testPerformance") );
        suite.addTest( new GroovyTest("testMultithreadingScript") );
        suite.addTest( new GroovyTest("testRuntimeErrorScript") );

        return suite;
    }
}
