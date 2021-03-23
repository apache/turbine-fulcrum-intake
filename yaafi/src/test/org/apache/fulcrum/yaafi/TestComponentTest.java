package org.apache.fulcrum.yaafi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.fulcrum.yaafi.testcontainer.BaseUnit5Test;

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


import org.junit.jupiter.api.Test;

/**
 * Test suite for the project
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public class TestComponentTest extends BaseUnit5Test
{

    @Test
    public void testTestComponent() throws Exception
    {
        TestComponent testComponent = (TestComponent) this.lookup(
            TestComponent.ROLE
            );

        testComponent.test();
        testComponent.doSomething(100, new Object[10]);

        assertEquals( testComponent.getBar(), "BAR" );
        assertEquals( testComponent.getFoo(), "FOO" );

        assertNotNull( testComponent.getUrnAvalonClassLoader() );
        assertNotNull( testComponent.getUrnAvaloneHome() );
        assertNotNull( testComponent.getUrnAvaloneTemp() );
        assertNotNull( testComponent.getUrnAvalonName() );
        assertNotNull( testComponent.getUrnAvalonPartition() );

        // Not sure what this code was for, but causing a findbugs error
        //Object [] temp = new Object[10];
        //System.out.println(temp.toString());

        try
        {
            testComponent.createException("enforce exception", this);
        }
        catch( Exception e )
        {
            // nothing to do
        }
    }

    /**
     * Verify bug fix for not calling dispose method of components
     * @throws Exception generic exception
     */
    public void testTestComponentDecomissioned() throws Exception
    {
        // lookup the test component
        TestComponent testComponent = (TestComponent) this.lookup(
            TestComponent.ROLE
            );

        assertFalse( testComponent.isDecomissioned() );

        // decommision the test component
        this.decommision( TestComponent.ROLE );
        assertTrue( testComponent.isDecomissioned() );

        // resurrect the test component - resurrecting a decommisioned service
        // might need some reviewing but I'm quite happy with the semantics
        testComponent = (TestComponent) this.lookup(
            TestComponent.ROLE
            );

        assertFalse( testComponent.isDecomissioned() );
    }
}
