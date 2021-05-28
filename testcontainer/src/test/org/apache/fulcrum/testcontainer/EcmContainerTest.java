package org.apache.fulcrum.testcontainer;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;

import org.apache.avalon.framework.component.ComponentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


/**
 * Basic testing of the Container
 *
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 */

public class EcmContainerTest extends BaseUnit5Test
{
    /**
	 * Constructor for test.
	 */
    public EcmContainerTest()
    {
    }

    @BeforeEach
    public void setUp() throws Exception{
        setContainerType( CONTAINER_ECM);
    }

    @Test
    public void testInitialization()
    {
        assertTrue(true);
    }
    @Test
    public void testComponentUsage()
    {
        SimpleComponent sc = null;
        try
        {
            sc = (SimpleComponent) this.lookup(SimpleComponent.ROLE);
        }
        catch (ComponentException e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
        assertNotNull(sc);
        sc.test();
        assertEquals(sc.getAppRoot(),sc.getAppRoot2());
        this.release(sc);
    }

    @Test
    public void testAlternativeRoles()
    {
        SimpleComponent sc = null;
        File f = new File("src/test/TestAlternativeRoleConfig.xml");
        assertTrue(f.exists());
        this.setRoleFileName("src/test/TestAlternativeRoleConfig.xml");
        try
        {
            sc = (SimpleComponent) this.lookup(SimpleComponent.ROLE);
        }
        catch (ComponentException e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
        assertTrue(sc instanceof AlternativeComponentImpl);
        assertNotNull(sc);
        sc.test();
        this.release(sc);
    }

    @Test
    public void testLoadingContainerWithNoRolesfile()
    {
        SimpleComponent sc = null;

        this.setRoleFileName(null);
        this.setConfigurationFileName(
            "src/test/TestComponentConfigIntegratedRoles.xml");
        try
        {
            sc = (SimpleComponent) this.lookup(SimpleComponent.ROLE);
        }
        catch (ComponentException e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
        assertTrue(sc instanceof AlternativeComponentImpl);
        assertNotNull(sc);
        sc.test();
        this.release(sc);
    }

    @Test
    public void testLoadingNonExistentFile()
    {
        this.setRoleFileName(null);
        this.setConfigurationFileName("BogusFile.xml");
        try
        {
            this.lookup(SimpleComponent.ROLE);
        }
        catch(RuntimeException re){
            //good
        }
        catch (ComponentException e)
        {

            fail(e.getMessage());
        }
    }
}
