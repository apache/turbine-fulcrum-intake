package org.apache.fulcrum.yaafi.framework.factory;

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
import java.io.IOException;

import org.apache.avalon.framework.context.DefaultContext;
import org.apache.fulcrum.yaafi.TestComponent;
import org.apache.fulcrum.yaafi.TestComponentImpl;
import org.apache.fulcrum.yaafi.framework.container.ServiceContainer;

import junit.framework.TestCase;

/**
 * Test suite for the ServiceContainerFactory.
 *
 * @author <a href="mailto:siegfried.goeschl@drei.com">Siegfried Goeschl</a>
 * @version $Id$
 */

public class ServiceContainerFactoryTest extends TestCase
{
    private ServiceContainer container = null;
    
    /**
     * Constructor
     * @param name the name of the test case
     */
    public ServiceContainerFactoryTest( String name )
    {
        super(name);
    }
    
    /**
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        if( this.container != null )
        {
            this.container.dispose();
        }
        
        super.tearDown();
    }
    
    private void checkTectComponent()
    	throws Exception
    {
        TestComponent testComponent = (TestComponent) container.lookup( 
            TestComponent.ROLE 
            );
        
        testComponent.test();  
        
        assertEquals( ((TestComponentImpl) testComponent).bar, "BAR" );
        assertEquals( ((TestComponentImpl) testComponent).foo, "FOO" );      
        assertNotNull( ((TestComponentImpl) testComponent).urnAvalonClassLoader );
        assertNotNull( ((TestComponentImpl) testComponent).urnAvaloneHome );        
        assertNotNull( ((TestComponentImpl) testComponent).urnAvaloneTemp );        
        assertNotNull( ((TestComponentImpl) testComponent).urnAvalonName );
        assertNotNull( ((TestComponentImpl) testComponent).urnAvalonPartition );
        
        assertTrue( ((TestComponentImpl) testComponent).urnAvaloneHome instanceof File );
        assertTrue( ((TestComponentImpl) testComponent).urnAvaloneTemp instanceof File );
        assertTrue( ((TestComponentImpl) testComponent).urnAvalonClassLoader instanceof ClassLoader );
    }
    
    /**
     * Creates a YAAFI container using a container configuration file
     * which already contains most of the required settings
     */
    public void testCreationWithContainerConfiguration() throws Exception
    {
        ServiceContainerConfiguration config = new ServiceContainerConfiguration();                
        config.setContainerConfiguration( "./src/test/TestMerlinContainerConfig.xml", false );        
        this.container = ServiceContainerFactory.create( config );
        this.checkTectComponent();  
    }

    /**
     * Creates a YAAFI container using a non-existent container 
     * configuration file. Therefore the creation should fail.
     */
    public void testCreationWithMissingContainerConfiguration() throws Exception
    {
        ServiceContainerConfiguration config = new ServiceContainerConfiguration();                
        
        try
        {
            config.setContainerConfiguration( "./src/test/MissingTestContainerConfig.xml", false );
            this.container = ServiceContainerFactory.create( config );
            fail("The creation of the YAAFI container must fail");
        }
        catch (IOException e)
        {
            // nothing to do
        }
        catch (Exception e)
        {
            fail("We are expecting an IOException");
        }        
    }

    /**
     * Creates a YAAFI container providing all required settings
     * manually 
     */
    public void testCreationWithManualSettings() throws Exception
    {
        ServiceContainerConfiguration config = new ServiceContainerConfiguration();        
        config.setComponentRolesLocation( "./src/test/TestRoleConfig.xml" );
        config.setComponentConfigurationLocation( "./src/test/TestComponentConfig.xml" );                               
        config.setParametersLocation( "./src/test/TestParameters.properties" );
        this.container = ServiceContainerFactory.create( config );   
        this.checkTectComponent();        
    }

    /**
     * Creates a YAAFI container providing a Phoenix context
     */
    public void testCreationWithPhoenixContext() throws Exception
    {
        ServiceContainer container = null;
        ServiceContainerConfiguration config = new ServiceContainerConfiguration();
        DefaultContext context = new DefaultContext();
        
        // use an existing container configuration
        config.setContainerConfiguration( "./src/test/TestPhoenixContainerConfig.xml", false );
        
        // fill the context with Phoenix settings
        context.put( "app.name", "ServiceContainerFactoryTest" );
        context.put( "block.name", "fulcrum-yaafi" );
        context.put( "app.home", new File( new File("").getAbsolutePath() ) );
        
        // create an instance
        this.container = ServiceContainerFactory.create( config, context );
        
        // execute the test component   
        this.checkTectComponent();        
    }
    
    /**
     * Creates a YAAFI container providing a Fortress context
     */
    public void testCreationWithFortressContext() throws Exception
    {
        ServiceContainer container = null;
        ServiceContainerConfiguration config = new ServiceContainerConfiguration();
        DefaultContext context = new DefaultContext();
        
        // use an existing container configuration
        config.setContainerConfiguration( "./src/test/TestFortressContainerConfig.xml", false );
        
        // fill the context with Phoenix settings
        context.put( "component.id", "ServiceContainerFactoryTest" );
        context.put( "component.logger", "fulcrum-yaafi" );
        context.put( "context-root", new File( new File("").getAbsolutePath() ) );
        context.put( "impl.workDir", new File( new File("").getAbsolutePath() ) );
        
        // create an instance
        this.container = ServiceContainerFactory.create( config, context );
        
        // execute the test component   
        this.checkTectComponent();        
    }    
}
