package org.apache.fulcrum.script;

/*
 * Copyright 2005 Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
        TestSuite suite= new TestSuite("RhinoTest");
        
        // tests from the JSR-223 Reference implementation
        suite.addTest( new RhinoTest("testHelloWorld") );
        suite.addTest( new RhinoTest("testCompilableInterface") );
        suite.addTest( new RhinoTest("testNamespaceDemo2") );
        suite.addTest( new RhinoTest("testNamespaceDemo3") );
        suite.addTest( new RhinoTest("testInvocableIntf") );
        
        suite.addTest( new RhinoTest("testAvalonContext") );     
        suite.addTest( new RhinoTest("testExists") );        
        suite.addTest( new RhinoTest("testPerformance") );
        suite.addTest( new RhinoTest("testMultithreadingScript") );        
        suite.addTest( new RhinoTest("testRuntimeErrorScript") );        
        suite.addTest( new RhinoTest("testCall") );
        suite.addTest( new RhinoTest("testLocatorFunctionality") );
                                
        return suite;
    }    
}
