package org.apache.fulcrum.yaafi;


/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import org.apache.fulcrum.yaafi.testcontainer.BaseUnitTest;

/**
 * Test suite for the project
 *
 * @author <a href="mailto:siegfried.goeschl@drei.com">Siegfried Goeschl</a>
 * @version $Id$
 */

public class TestComponentTest extends BaseUnitTest
{
    /**
     * Constructor
     * @param name the name of the test case
     */
    public TestComponentTest( String name )
    {
        super(name);
    }

    
    public void testTestComponent() throws Exception
    {
        TestComponent testComponent = (TestComponent) this.lookup( 
            TestComponent.ROLE 
            );
        
        testComponent.test();
        
        assertEquals( ((TestComponentImpl) testComponent).bar, "BAR" );
        assertEquals( ((TestComponentImpl) testComponent).foo, "FOO" );
        
        assertNotNull( ((TestComponentImpl) testComponent).urnAvalonClassLoader );
        assertNotNull( ((TestComponentImpl) testComponent).urnAvaloneHome );
        assertNotNull( ((TestComponentImpl) testComponent).urnAvaloneTemp );
        assertNotNull( ((TestComponentImpl) testComponent).urnAvalonName );
        assertNotNull( ((TestComponentImpl) testComponent).urnAvalonPartition );
    }
    
    /**
     * Verify bug fix for not calling dispose method of components
     * @throws Exception
     */
    public void testTestComponentDecomissioned() throws Exception
    {
        // lookup the test component
        TestComponent testComponent = (TestComponent) this.lookup( 
            TestComponent.ROLE 
            );
        assertFalse( TestComponentImpl.decomissioned );
        
        // decommision the test component
        this.decommision( TestComponent.ROLE );
        assertTrue( TestComponentImpl.decomissioned );

        // resurrect the test component - resurrecting a decommisioned service
        // might need some reviewing but I'm quite happy with the semantics
        testComponent = (TestComponent) this.lookup( 
            TestComponent.ROLE 
            );
        assertFalse( TestComponentImpl.decomissioned );
    }    
}
