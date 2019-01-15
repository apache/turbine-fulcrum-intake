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



import java.io.File;

import org.apache.fulcrum.intake.model.Group;
import org.apache.fulcrum.testcontainer.BaseUnit5Test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author Eric Pugh
 *
 */
public class IntakeServiceTest extends BaseUnit5Test
{
    private static final File BASEDIR = new File(".");// System.getProperty( "basedir" )

    private IntakeService intakeService = null;

    /**
     * Defines the testcase for JUnit5.
     * 
     * @param testInfo defining the test
     */
    public IntakeServiceTest(TestInfo testInfo) {
    	
    }

    /**
     * @throws Exception generic exception
     */
    @BeforeEach
    public void setUp() throws Exception
    {
        File appData = new File( BASEDIR, "target/appData.ser");
        if(appData.exists()){
            appData.delete();
        }
        try {
            intakeService = (IntakeService) this.lookup( IntakeService.class.getName() );
        } catch (Throwable e) {
            fail(e.getMessage());
        }
        assertNotNull(intakeService);

    }

    /**
     * @throws Exception generic exception
     */
    @Test
    public void testBasicConfigLoads() throws Exception {

        Group group = intakeService.getGroup("LoginGroup");

        File file = new File( BASEDIR, "target/appData.ser");
        assertTrue(file.exists(), "Make sure serialized data file exists:" + file);

        assertNotNull(group);
        assertEquals("loginGroupKey", group.getGID());
        assertEquals("LoginGroup", group.getIntakeGroupName());

        Group group2 = intakeService.getGroup("AnotherGroup");
        assertNotNull(group2);
        assertEquals("anotherGroupKey", group2.getGID());
        assertEquals("AnotherGroup", group2.getIntakeGroupName());
    }

}
