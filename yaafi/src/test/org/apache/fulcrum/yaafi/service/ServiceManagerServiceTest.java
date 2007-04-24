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

import org.apache.avalon.framework.service.ServiceManager;
import org.apache.fulcrum.yaafi.framework.container.ServiceContainer;
import org.apache.fulcrum.yaafi.framework.factory.ServiceContainerConfiguration;
import org.apache.fulcrum.yaafi.framework.factory.ServiceContainerFactory;
import org.apache.fulcrum.yaafi.service.servicemanager.ServiceManagerService;
import org.apache.fulcrum.yaafi.service.servicemanager.ServiceManagerServiceImpl;

/**
 * Test suite for the ServiceManagereService.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public class ServiceManagerServiceTest extends TestCase
{
    private ServiceContainer container = null;

    /**
     * Constructor
     * @param name the name of the test case
     */
    public ServiceManagerServiceTest( String name )
    {
        super(name);
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        ServiceContainerConfiguration config = new ServiceContainerConfiguration();
        config.loadContainerConfiguration( "./src/test/TestYaafiContainerConfig.xml" );
        this.container = ServiceContainerFactory.create( config );
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
     * Access the ServiceManagerService
     */
    public void testServiceManagerService() throws Exception
    {
        ServiceManagerService serviceManagerService = ServiceManagerServiceImpl.getInstance();
        assertNotNull(serviceManagerService);

        assertNotNull( serviceManagerService.getAvalonLogger() );
        assertNotNull( serviceManagerService.getContext().get("urn:avalon:home") );
        assertNotNull( serviceManagerService.getContext().get("urn:avalon:temp") );
        
        // get the parameters
        serviceManagerService.getParameters();
        
        // lookup the service
        serviceManagerService = (ServiceManagerService) serviceManagerService.lookup( ServiceManagerService.class.getName() );
        assertTrue( serviceManagerService.hasService( ServiceManagerService.class.getName() ) );
        assertTrue( serviceManagerService.getServiceManager() instanceof ServiceManager );
        serviceManagerService.release(serviceManagerService);
    }
}
