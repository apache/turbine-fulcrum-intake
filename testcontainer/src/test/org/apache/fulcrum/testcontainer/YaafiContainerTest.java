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
import java.io.File;

import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.logger.ConsoleLogger;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
/**
 * Basic testing of the Container
 *
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 */
public class YaafiContainerTest extends BaseUnit4Test
{
    /**
     * Constructor for test.
     */
    public YaafiContainerTest()
    {
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
            sc = (SimpleComponent) this.lookup(SimpleComponent.class.getName());
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
    public void testLoadingContainerWithNoRolesfileFails()
    {
        this.setLogLevel(ConsoleLogger.LEVEL_DISABLED);

        this.setRoleFileName(null);

        try
        {
            this.lookup(SimpleComponent.class.getName());
            fail("We should fail");
        }
        catch (ComponentException e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
        catch (Exception e)
        {
            // We expect to fail with a ConfigurationException
        }
    }
    @Test
    public void testWithLogLevel() throws Exception
    {
        this.setLogLevel(ConsoleLogger.LEVEL_ERROR);
        this.lookup(SimpleComponent.class.getName());
    }
}
