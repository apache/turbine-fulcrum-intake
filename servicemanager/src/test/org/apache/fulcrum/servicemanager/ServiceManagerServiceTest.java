package org.apache.fulcrum.servicemanager;

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

import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.logger.Logger;
import org.apache.fulcrum.testcontainer.BaseUnitTest;

/**
 * Test suite for the ServiceManagereService.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public class ServiceManagerServiceTest extends BaseUnitTest
{
    /** the service under test */
    private ServiceManagerService service;

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
        this.service = (ServiceManagerService) this.resolve(ServiceManagerService.class.getName());
    }

    /**
     * Access the ServiceManagerService
     */
    public void testServiceManagerService() throws Exception
    {
        assertNotNull(service);

        // test acessing the logger
        assertNotNull( this.service.getLogger() );
        Logger logger = this.service.getLogger();
        logger.warn("Hello Avalon");

        // test accessing the context
        assertNotNull( this.service.getContext() );

        // get the parameters
        assertNotNull(this.service.getParameters());

        // get the configuration and access <foo>bar</foo>
        Configuration configuration = this.service.getConfiguration();
        assertEquals(configuration.getChild("foo").getValue(), "bar");
        
        // lookup the service
        service = (ServiceManagerService) this.service.lookup( ServiceManagerService.class.getName() );
        assertTrue( service.hasService( ServiceManagerService.class.getName() ) );
        assertTrue( service.getServiceManager() instanceof ServiceManager );
        service.release(service);
    }
}
