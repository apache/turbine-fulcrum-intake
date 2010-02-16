package org.apache.fulcrum.jetty;

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

import org.apache.fulcrum.testcontainer.BaseUnitTest;

/**
 * Testing the embedded Jetty container.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */
public class JettyTestRunner extends BaseUnitTest
{
    /**
     * Defines the testcase name for JUnit.
     *
     * @param name the testcase's name.
     */
    public JettyTestRunner(String name)
    {
        super(name);
    }

    /**
     * Test setup
     */
    protected void setUp() throws Exception
    {
        super.setUp();

        try
        {
            this.lookup(JettyService.class.getName());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    /////////////////////////////////////////////////////////////////////////
    // Start of unit tests
    /////////////////////////////////////////////////////////////////////////

    /**
     * Run the Jetty instance for 600 seconds to do some manual testing.
     *
     * @throws Exception the test failed
     */
    public void testRunJetty() throws Exception
    {
        Thread.sleep(600000);
    }
}