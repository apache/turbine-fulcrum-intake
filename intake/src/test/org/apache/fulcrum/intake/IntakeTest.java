package org.apache.fulcrum.intake;

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

import org.apache.fulcrum.testcontainer.BaseUnitTest;
import org.apache.fulcrum.intake.model.Group;
/**
 * Test the facade class for the service
 *
 * @author <a href="epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class IntakeTest extends BaseUnitTest
{
    /**
     * Defines the testcase name for JUnit.
     *
     * @param name the testcase's name.
     */
    public IntakeTest(String name)
    {
        super(name);
    }
 

    public void testFacadeNotConfigured() throws Exception
    {
		assertFalse(Intake.isInitialized());
        try
        {
            Intake.getGroup("test");
        }
        catch (RuntimeException re)
        {
            //good;
        }
    }

    public void testFacadeConfigured() throws Exception
    {
        // this.lookup causes the workflow service to be configured.
        IntakeService is = (IntakeService) this.resolve( IntakeService.class.getName() );
        Group group = is.getGroup("LoginGroup");
        assertNotNull(group);
        assertTrue(Intake.isInitialized());
        group = Intake.getGroup("LoginGroup");
		assertNotNull(group);
    }

}
