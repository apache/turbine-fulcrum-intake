package org.apache.fulcrum.security.memory.turbine;
/*
 *  Copyright 2001-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import org.apache.fulcrum.security.SecurityService;
import org
    .apache
    .fulcrum
    .security
    .model
    .turbine
    .test
    .AbstractTurbineModelManagerTest;

/**
 * @author Eric Pugh
 *
 * Test the memory implementation of the turbine model..
 */
public class MemoryTurbineModelManagerTest
    extends AbstractTurbineModelManagerTest
{
    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(MemoryTurbineModelManagerTest.class);
    }
    public void setUp() throws Exception
    {

        this.setRoleFileName(null);
        this.setConfigurationFileName("src/test/TurbineMemory.xml");
        securityService = (SecurityService) lookup(SecurityService.ROLE);
        super.setUp();

    }
    public void tearDown()
    {
        super.tearDown();
		modelManager = null;
        securityService = null;
    }
    /**
    	* Constructor for MemoryPermissionManagerTest.
    	* @param arg0
    	*/
    public MemoryTurbineModelManagerTest(String arg0)
    {
        super(arg0);
    }
}
