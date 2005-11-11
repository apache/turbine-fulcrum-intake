package org.apache.fulcrum.yaafi.service;

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

import junit.framework.TestCase;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.fulcrum.yaafi.TestComponent;
import org.apache.fulcrum.yaafi.framework.container.ServiceContainer;
import org.apache.fulcrum.yaafi.framework.factory.ServiceContainerConfiguration;
import org.apache.fulcrum.yaafi.framework.factory.ServiceContainerFactory;
import org.apache.fulcrum.yaafi.service.reconfiguration.ReconfigurationService;

/**
 * Test suite for the ReconfigurationService. This test doesn't do
 * anything apart from running a minute so you have some time to tinker
 * with the component configuration file.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public class ReconfigurationTest extends TestCase
{
    private ServiceContainer container = null;

    /**
     * Constructor
     * @param name the name of the test case
     */
    public ReconfigurationTest( String name )
    {
        super(name);
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        ServiceContainerFactory.dispose(this.container);
        super.tearDown();
    }

    /**
     * @return get our simple test component
     */
    private TestComponent getTestComponent() throws ServiceException
    {
        return (TestComponent) container.lookup(
            TestComponent.ROLE
            );
    }

    /**
     * Trigger the ReconfigurationService by instantiating it manually.
     */
    public void testReconfigurationService() throws Exception
    {
        // instantiate a YAAFI container

        ServiceContainerConfiguration config = new ServiceContainerConfiguration();
        config.loadContainerConfiguration( "./src/test/TestYaafiContainerConfig.xml" );
        this.container = ServiceContainerFactory.create( config );

        // the ReconfigurationService is configured to be instantiated on demand
        // get an instance to start monitoring ...

        ReconfigurationService reconfigurationService = null;

        reconfigurationService = (ReconfigurationService) this.container.lookup(
            ReconfigurationService.class.getName()
            );

        assertNotNull(reconfigurationService);
        
        // comment out if you want to tinker with componentConfiguration manually

        // Thread.sleep(60000);

        this.getTestComponent().test();
    }
}
