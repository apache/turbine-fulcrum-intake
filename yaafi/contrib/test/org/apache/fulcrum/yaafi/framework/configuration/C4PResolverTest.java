package org.apache.fulcrum.yaafi.framework.configuration;

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

import org.apache.fulcrum.yaafi.framework.container.ServiceContainer;
import org.apache.fulcrum.yaafi.framework.factory.ServiceContainerConfiguration;
import org.apache.fulcrum.yaafi.framework.factory.ServiceContainerFactory;

/**
 * A test for the CommonsConfigurationCCPResolver
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 *
 */
public class C4PResolverTest extends TestCase
{
    ServiceContainer container;
    TestComponent tc;

    /**
     * @param testName
     */
    public C4PResolverTest(String testName)
    {
        super(testName);
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        ServiceContainerConfiguration config = new ServiceContainerConfiguration();
        config.loadContainerConfiguration( "./test/TestYaafiContainerConfig.xml" );
        this.container = ServiceContainerFactory.create( config );
        tc = (TestComponent)container.lookup(TestComponent.ROLE);
    }

    public void testTestComponent()
    {
        assertNotNull("TestComponent should not be null", tc);
        assertEquals(tc.getFoo(), "yaafi-test");
        assertEquals(tc.getFooBar(), "yaafi-test1");
    }
}
