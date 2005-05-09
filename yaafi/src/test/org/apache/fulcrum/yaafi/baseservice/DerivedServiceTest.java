package org.apache.fulcrum.yaafi.baseservice;


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


import java.io.File;

import org.apache.fulcrum.yaafi.testcontainer.BaseUnitTest;

/**
 * Test suite for the project
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public class DerivedServiceTest extends BaseUnitTest
{
    /**
     * Constructor
     * @param name the name of the test case
     */
    public DerivedServiceTest( String name )
    {
        super(name);
    }

    /**
     * Lookup the service and invoke the test() method
     * @throws Exception invocation failed
     */
    public void testDerivedService() throws Exception
    {
        DerivedService derivedService = (DerivedService) this.lookup(
            DerivedService.class.getName()
            );

        // invoke the test() method on the service
        
        derivedService.test();
        
        // determine absolute paths and files
        
        String fileName = "./src/test/TestRoleConfig.xml";
        String absolutePath = derivedService.createAbsolutePath(fileName);
        File absoluteFile = derivedService.createAbsoluteFile(fileName);
        
        assertTrue(absoluteFile.isAbsolute());
        assertTrue(absoluteFile.exists());
        assertTrue(new File(absolutePath).isAbsolute());
        assertTrue(new File(absolutePath).exists());        
    }
}
