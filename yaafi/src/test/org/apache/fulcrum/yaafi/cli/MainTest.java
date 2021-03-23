package org.apache.fulcrum.yaafi.cli;

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

import junit.framework.TestCase;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.fulcrum.yaafi.TestComponent;
import org.junit.After;
import org.junit.jupiter.api.Test;

/**
 * Test suite for exercising the command line integration.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public class MainTest extends TestCase
{
    private Main main;


    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    @After
    protected void tearDown() throws Exception
    {
        this.main.dispose();
        super.tearDown();
    }

    /**
     * @return get our simple test component
     * @throws ServiceException if the component is not found
     */
    private TestComponent getTestComponent() throws ServiceException
    {
        return (TestComponent) main.getServiceManager().lookup(
            TestComponent.ROLE
            );
    }

    /**
     * Initialize the CLI using a valid container configuration
     * @throws Exception generic exception
     */
    @Test
    public void testValidContainerConfiguration() throws Exception
    {
        String[] args = {
            "--yaafi.cli.config",
            "./src/test/TestYaafiContainerConfig.xml"
            };

        this.main = new Main(args);
        this.main.run();

        this.getTestComponent();
    }

    /**
     * Test the toString() method provding diagnostic information
     * @throws Exception generic exception
     */
    @Test
    public void testToString() throws Exception
    {
        String[] args = {
            "--yaafi.cli.applicationHome",
            ".",
            "--yaafi.cli.config",
            "./src/test/TestYaafiContainerConfig.xml"
            };

        this.main = new Main(args);
        System.out.println(this.main.toString());
        return;
    }

    /**
     * Initialize the CLI using an invalid container configuration
     * @throws Exception generic exception
     */
    @Test
    public void testInvlidContainerConfiguration() throws Exception
    {
        String[] args = {
            "--yaafi.cli.config",
            " ./src/test/foo.xml"
            };

        this.main = new Main(args);

        try
        {
            this.main.run();
        }
        catch (RuntimeException e)
        {
            // that's what we expect
            return;
        }

        TestCase.fail("The YAAFI CLI should throw an exception");
    }
}
