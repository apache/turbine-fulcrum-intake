package org.apache.fulcrum.intake;

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
import org.apache.fulcrum.intake.model.Field;
import org.apache.fulcrum.intake.model.Group;
import org.apache.fulcrum.intake.validator.BooleanValidator;
/**
 * Test the facade class for the service
 *
 * @author <a href="epugh@upstate.com">Eric Pugh</a>
 * @author <a href="mailto:jh@byteaction.de">J&uuml;rgen Hoffmann</a>
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

    public void testEmptyBooleanField() throws Exception
    {
        IntakeService is = (IntakeService) this.resolve( IntakeService.class.getName() );
        Group group = is.getGroup("BooleanTest");
        assertNotNull(group);
        assertTrue(Intake.isInitialized());
        group = Intake.getGroup("BooleanTest");
        Field booleanField = group.get("EmptyBooleanTestField");
        assertTrue("The Default Validator of an intake Field type boolean should be BooleanValidator", (booleanField.getValidator() instanceof BooleanValidator));
        assertFalse("An Empty intake Field type boolean should not be required", booleanField.isRequired());
    }

    public void testBooleanField() throws Exception
    {
        IntakeService is = (IntakeService) this.resolve( IntakeService.class.getName() );
        Group group = is.getGroup("BooleanTest");
        assertNotNull(group);
        assertTrue(Intake.isInitialized());
        group = Intake.getGroup("BooleanTest");
        Field booleanField = group.get("BooleanTestField");
        assertTrue("The Default Validator of an intake Field type boolean should be BooleanValidator", (booleanField.getValidator() instanceof BooleanValidator));
        assertFalse("An intake Field type boolean, which is not required, should not be required", booleanField.isRequired());
    }

    public void testRequiredBooleanField() throws Exception
    {
        IntakeService is = (IntakeService) this.resolve( IntakeService.class.getName() );
        Group group = is.getGroup("BooleanTest");
        assertNotNull(group);
        assertTrue(Intake.isInitialized());
        group = Intake.getGroup("BooleanTest");
        Field booleanField = group.get("RequiredBooleanTestField");
        assertTrue("The Default Validator of an intake Field type boolean should be BooleanValidator", (booleanField.getValidator() instanceof BooleanValidator));
        assertTrue("An intake Field type boolean, which is required, should be required", booleanField.isRequired());
    }
}
