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

import java.io.File;

import org.apache.fulcrum.intake.model.Group;
import org.apache.fulcrum.testcontainer.BaseUnitTest;

/**
 * @author Eric Pugh
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class IntakeServiceTest extends BaseUnitTest
{
    private static final File BASEDIR = new File( System.getProperty( "basedir" ));

    private IntakeService intakeService = null;

    /**
      * Defines the testcase name for JUnit.
      *
      * @param name the testcase's name.
      */
    public IntakeServiceTest(String name) {
        super(name);
    }

    public void setUp() throws Exception
    {
        super.setUp();
        File appData = new File( BASEDIR, "target/appData.ser");
        if(appData.exists()){
            appData.delete();
        }
        try {
            intakeService = (IntakeService) this.resolve( IntakeService.class.getName() );
        } catch (Throwable e) {
            fail(e.getMessage());
        }
        assertNotNull(intakeService);        
        
    }

    public void testBasicConfigLoads() throws Exception {

        Group group = intakeService.getGroup("LoginGroup");
        
        File file = new File( BASEDIR, "target/appData.ser");
        assertTrue(
            "Make sure serialized data file exists:" + file,
            file.exists());
        
        assertNotNull(group);
        assertEquals("loginGroupKey", group.getGID());
        assertEquals("LoginGroup", group.getIntakeGroupName());

        Group group2 = intakeService.getGroup("AnotherGroup");
        assertNotNull(group2);
        assertEquals("anotherGroupKey", group2.getGID());
        assertEquals("AnotherGroup", group2.getIntakeGroupName());
    }

}
