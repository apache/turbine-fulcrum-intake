package org.apache.fulcrum.testcontainer;
/*
 * Copyright 2004 The Apache Software Foundation.
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

import org.apache.avalon.framework.component.ComponentException;
/**
 * Basic testing of the Container
 * 
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 */
public class EcmContainerTest extends BaseUnitTest
{
    /**
	 * Constructor for test.
	 * 
	 * @param testName name of the test being executed
	 */
    public EcmContainerTest(String testName)
    {
        super(testName);
    }
    
    public void setUp() throws Exception{
        containerType=CONTAINER_ECM;
        super.setUp();
    }

    public void testInitialization()
    {
        assertTrue(true);
    }
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

    public void testLoadingNonExistentFile()
    {
        SimpleComponent sc = null;

        this.setRoleFileName(null);
        this.setConfigurationFileName("BogusFile.xml");
        try
        {
            sc = (SimpleComponent) this.lookup(SimpleComponent.ROLE);
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
