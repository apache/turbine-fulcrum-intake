package org.apache.fulcrum.yaafi.framework.container;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.fulcrum.yaafi.TestComponent;
import org.apache.fulcrum.yaafi.framework.factory.ServiceContainerConfiguration;
import org.apache.fulcrum.yaafi.framework.factory.ServiceContainerFactory;
import org.apache.fulcrum.yaafi.framework.role.RoleEntry;
import org.apache.fulcrum.yaafi.service.reconfiguration.ReconfigurationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

/**
 * Test suite for the ServiceLifecycleManager.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public class ServiceLifecycleManagerTest
{
    private ServiceLifecycleManager lifecycleManager;
    private ServiceContainer container;

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @BeforeEach
    protected void setUp() throws Exception
    {
        ServiceContainerConfiguration config = new ServiceContainerConfiguration();
        config.loadContainerConfiguration( "./src/test/TestYaafiContainerConfig.xml" );
        this.container = ServiceContainerFactory.create( config );
        this.lifecycleManager = this.container;
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    @AfterEach
    protected void tearDown() throws Exception
    {
        ServiceContainerFactory.dispose( this.container );
    }

    /**
     * Check our TestComponent.
     * 
     * @throws Exception generic exception
     */
    private void checkTestComponent() throws Exception
    {
        TestComponent testComponent = (TestComponent) container.lookup( TestComponent.ROLE );

        testComponent.test();

        assertEquals( testComponent.getBar(), "BAR" );
        assertEquals( testComponent.getFoo(), "FOO" );

        assertNotNull( testComponent.getUrnAvalonClassLoader() );
        assertNotNull( testComponent.getUrnAvaloneHome() );
        assertNotNull( testComponent.getUrnAvaloneTemp() );
        assertNotNull( testComponent.getUrnAvalonName() );
        assertNotNull( testComponent.getUrnAvalonPartition() );
    }

    /**
     * Gets a list of all available services and dumps them on System.out.
     * 
     * @throws Exception generic exception
     */
    @Test
    public void testGetServiceComponents() throws Exception
    {
        RoleEntry[] list = this.lifecycleManager.getRoleEntries();
        assertNotNull( list );
        assertTrue( list.length > 0 );
        for (RoleEntry entry : list)
            System.out.println( entry.toString() );
    }

    /**
     * Reconfigure the all services
     * 
     * @throws Exception generic exception
     */
    @Test
    public void testGeneralReconfiguration() throws Exception
    {
        RoleEntry[] list = this.lifecycleManager.getRoleEntries();

        for (int i = 0; i < list.length; i++)
        {
            String serviceName = list[i].getName();
            System.out.println( "Reconfiguring " + serviceName + " ..." );

            String[] serviceNames = { list[i].getName() };
            this.lifecycleManager.reconfigure( serviceNames );
            assertTrue( this.container.hasService( serviceName ) );
            assertNotNull( this.container.lookup( serviceName ) );
        }
    }

    /**
     * Decommission and resurrect all services
     * 
     * @throws Exception generic exception
     */
    @Test
    public void testGeneralDecommision() throws Exception
    {
        String serviceName = null;
        RoleEntry[] list = this.lifecycleManager.getRoleEntries();

        for (int i = 0; i < list.length; i++)
        {
            serviceName = list[i].getName();
            System.out.println( "Decommissiong " + serviceName + " ..." );

            assertTrue( this.container.hasService( serviceName ) );
            this.lifecycleManager.decommission( serviceName );
            assertTrue( this.container.hasService( serviceName ) );
            this.container.lookup( serviceName );
            assertTrue( this.container.hasService( serviceName ) );
            this.lifecycleManager.decommission( serviceName );
            assertTrue( this.container.hasService( serviceName ) );
        }
    }

    /**
     * Decommission and resurrect all services
     * 
     * @throws Exception generic exception
     */
    @Test
    public void testGeneralReconfigurationAndDecommision() throws Exception
    {
        String serviceName = null;
        RoleEntry[] list = this.lifecycleManager.getRoleEntries();
        DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
        Configuration configuration = builder.buildFromFile( "./src/test/TestReconfigurationConfig.xml" );

        for (int i = 0; i < list.length; i++)
        {
            serviceName = list[i].getName();
            String[] serviceNames = { list[i].getName() };
            System.out.println( "Processing " + serviceName + " ..." );

            // reconfigure/decommission/reconfigure the service
            this.lifecycleManager.reconfigure( serviceNames );
            this.lifecycleManager.decommission( serviceName );
            this.lifecycleManager.reconfigure( serviceNames );

            // run a reconfiguration over all services
            this.container.reconfigure( configuration );

            // reconfigure/decommission/reconfigure the service
            this.container.lookup( serviceName );
            this.lifecycleManager.reconfigure( serviceNames );
            this.lifecycleManager.decommission( serviceName );
            this.lifecycleManager.reconfigure( serviceNames );
        }
    }

    /**
     * Decommissions the TestComponent and ReconfigurationService to start them again.
     * 
     * @throws Exception generic exception
     */
    @Test
    public void testIndividualDecommission() throws Exception
    {
        String serviceName = null;

        // teminate the TestComponent and run it again

        serviceName = TestComponent.class.getName();

        this.checkTestComponent();
        this.lifecycleManager.decommission( serviceName );
        this.checkTestComponent();

        // terminate the ReconfigurationService which is currently
        // not instantiated and resurrect it. The moment the
        // ReconfigurationService is instantiated it is starting to
        // work

        serviceName = ReconfigurationService.class.getName();

        this.lifecycleManager.decommission( ReconfigurationService.class.getName() );
        this.container.lookup( ReconfigurationService.class.getName() );

        // now we should see that the service is starting up

        Thread.sleep( 5000 );

        // and terminate it again

        this.lifecycleManager.decommission( ReconfigurationService.class.getName() );
    }

    /**
     * Create an exception which should be handled by the JAMon interceptor.
     * 
     * @throws Exception generic exception
     */
    @Test
    public void testException() throws Exception
    {
        TestComponent testComponent = (TestComponent) container.lookup( TestComponent.ROLE );

        try
        {
            testComponent.createException( "testException", this );
        } catch (Exception e)
        {
            return;
        }

    }
}
