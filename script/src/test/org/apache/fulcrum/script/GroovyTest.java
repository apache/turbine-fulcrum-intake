package org.apache.fulcrum.script;

import org.junit.jupiter.api.BeforeEach;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.ExcludeTags;
import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.SuiteDisplayName;
import org.junit.runner.RunWith;

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


/**
 * Regression test for Groovy
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

@RunWith(JUnitPlatform.class)
@SuiteDisplayName("JUnit Groovy Script Test Suite")
@ExcludeTags("Ignore4Groovy")
//@SelectPackages("org.apache.fulcrum.script")
@IncludeClassNamePatterns("^(.*GroovyTest.*|.*AbstractScriptTest.*)$")
public class GroovyTest extends AbstractScriptTest
{
    /**
     * Defines the testcase name for JUnit.
     *
     * @param name the testcase's name.
     */
    public GroovyTest()
    {
        super();
        this.setConfigurationFileName("./src/test/TestGroovyComponentConfig.xml");
    }
    
    @BeforeEach
    protected void setUp() throws Exception
    {
        super.setUp();
    }
    
}
