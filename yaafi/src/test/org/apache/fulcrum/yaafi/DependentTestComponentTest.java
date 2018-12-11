package org.apache.fulcrum.yaafi;

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


import org.apache.fulcrum.yaafi.testcontainer.BaseUnitTest;

/**
 * Test suite for ensuring that a dependent test component
 * properly works even if it is declared before the service
 * it depends on.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public class DependentTestComponentTest extends BaseUnitTest
{
    /**
     * Constructor
     * @param name the name of the test case
     */
    public DependentTestComponentTest( String name )
    {
        super(name);
    }

    /**
     * DependentTestComponentImpl uses a TestComponent which. Make
     * sure that the container resolves this cyclic dependency.
     *
     * @throws Exception generic exception
     */
    public void testDependentTestComponent() throws Exception
    {
        DependentTestComponent dependentTestComponent = (DependentTestComponent) this.lookup(
            DependentTestComponent.ROLE
            );

        dependentTestComponent.test();
        dependentTestComponent.toString();
    }
}
