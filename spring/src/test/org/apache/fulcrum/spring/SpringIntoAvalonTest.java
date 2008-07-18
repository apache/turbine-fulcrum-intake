package org.apache.fulcrum.spring;

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

import junit.framework.TestCase;

import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.fulcrum.yaafi.framework.container.ServiceContainer;
import org.apache.fulcrum.yaafi.framework.factory.ServiceContainerConfiguration;
import org.apache.fulcrum.yaafi.framework.factory.ServiceContainerFactory;
import org.apache.fulcrum.yaafi.service.systemproperty.SystemPropertyService;
import org.apache.fulcrum.spring.SpringFrameworkService;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.beans.BeansException;

/**
 * Instantiate a Spring framework instance within an Avalon
 * Container and use the services.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public class SpringIntoAvalonTest extends TestCase
{
    private static final String GREETING = "Hello Avalon!!!";
    private ServiceContainer container;
    
    /**
     * Constructor
     * @param name the name of the test case
     */
    public SpringIntoAvalonTest( String name )
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
        config.loadContainerConfiguration( "./src/test/springIntoAvalonContainerConfiguration.xml" );
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
     * Get the SpringFrameworkService.
     *
     * @throws Exception the test failed 
     */
    public void testGetSpringFrameworkService() throws Exception
    {
        SpringFrameworkService service = (SpringFrameworkService) this.container.lookup("springFrameworkService");
        assertNotNull(service);
    }

    /**
     * Test the Avalon into Spring integration.
     *
     * @throws Exception the test failed
     */
    public void testAvalonIntoSpringIntegration() throws Exception
    {
        SpringFrameworkService service = (SpringFrameworkService) this.container.lookup("springFrameworkService");
        AbstractApplicationContext ctx = service.getAbstractApplicationContext();
        
        // ensure that the Avalon SystemPropertyService updated the system properties (so it was properly started)
        assertTrue(System.getProperty("FOO").equals("BAR"));

        CustomAvalonService customAvalonService;
        CustomSpringService customSpringService;
        SystemPropertyService systemPropertyService;
        ServiceManager serviceManager = (ServiceManager) ctx.getBean("avalonContainerBean");

        // lookup and use the Spring bean using Spring's context
        systemPropertyService = (SystemPropertyService) ctx.getBean("systemPropertyService");
        assertNotNull(systemPropertyService);

        // lookup and use the Spring bean using Spring's context
        customSpringService = (CustomSpringService) ctx.getBean("customSpringService");
        customSpringService.sayGretting();
        assertEquals(customSpringService.getGreeting(), GREETING);

        // lookup and use the Spring bean using Avalon's Service Manager
        customSpringService = (CustomSpringService) serviceManager.lookup("customSpringService");
        customSpringService.sayGretting();
        assertEquals(customSpringService.getGreeting(), GREETING);

        // lookup and use the Avalon service using Spring's context
        customAvalonService = (CustomAvalonService) ctx.getBean("customAvalonService");
        customAvalonService.sayGretting();
        assertEquals(customAvalonService.getGreeting(), GREETING);
        
        // lookup and use the Avalon service using Avalon's Service Manager
        customAvalonService = (CustomAvalonService) serviceManager.lookup("customAvalonService");
        customAvalonService.sayGretting();
        assertEquals(customAvalonService.getGreeting(), GREETING);

        // try to find a non-existing service using Avalon's Service Manager
        try
        {
            serviceManager.lookup("foo");
            fail("Looking up a non-existing service must throw a ServiceException");
        }
        catch(ServiceException e)
        {
            // nothing to do
        }
        catch(Exception e)
        {
            fail("Looking up a non-existing service must throw a ServiceException");
        }

        // try to find a non-existing service using Spring's context
        try
        {
            ctx.getBean("foo");
            fail("Looking up a non-existing service must throw a BeansException");
        }
        catch(BeansException e)
        {
            // nothing to do
        }
        catch(Exception e)
        {
            fail("Looking up a non-existing service must throw a BeansException");
        }        
    }
}