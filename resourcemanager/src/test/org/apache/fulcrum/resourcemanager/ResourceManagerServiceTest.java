package org.apache.fulcrum.resourcemanager;

/*
 * Copyright 2004 Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.Properties;

import org.apache.fulcrum.testcontainer.BaseUnitTest;

/**
 * Testing script handling
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */
public class ResourceManagerServiceTest extends BaseUnitTest
{
	private ResourceManagerService service;

    /**
     * Defines the testcase name for JUnit.
     *
     * @param name the testcase's name.
     */
    public ResourceManagerServiceTest(String name)
    {
        super(name);
    }

    protected void setUp() throws Exception
    {
        super.setUp();

        this.service = (ResourceManagerService) this.lookup(
            ResourceManagerService.class.getName()
            );
    }

    /**
     * Test basic CRUD for the domain "script"
     */
    public void testResourceCRUD() throws Exception
    {
        String temp = null;
        String resourceDomain = "groovy";
        String resourceName = "hello.groovy";
        String resourceContent = "println 'Hello World!'";

        // create the resource
        this.service.create( resourceDomain, resourceName, resourceContent );
        assertTrue( this.service.exists( resourceDomain, resourceName ) );

        // read the resource
        temp = new String( this.service.read( resourceDomain, resourceName ) );
        assertEquals( resourceContent, temp );

        // update and read the resource
        this.service.update( resourceDomain, resourceName, "return 'Hello World!'" );
        temp = new String( this.service.read( resourceDomain, resourceName ) );
        assertFalse( resourceContent.equals( temp) );

        // delete the resource
        this.service.delete( resourceDomain, resourceName );
        assertFalse( this.service.exists( resourceDomain, resourceName ) );
    }

    /**
     * Test getting a list of resources for the domain "xslt"
     */
    public void testListResources() throws Exception
    {
        String [] result = null;
        String resourceDomain = "xslt";

        // get a list of available resources for "xslt"

        result = this.service.listResources("xslt");
        assertTrue( result.length == 2 );

        // check that the file name filering works since
        // "invisble.html" should be invisible ... :-)

        result = this.service.listResources("test");
        assertTrue( result.length == 0 );
    }

    /**
     * Test getting a list of domains
     */
    public void testListDomains() throws Exception
    {
        // get a list of available domains
        // 
        // -) crypto
        // -) scripts
        // -) test
        // -) xslt
        //        

        String [] result = this.service.listDomains();
        assertTrue( result.length == 4 );
    }

    /**
     * Test the locator
     */
    public void testLocator() throws Exception
    {
        String resourceDomain = "groovy";
        String result = null;

        // direct match
        String[] resourceContext1 = { "foo" };
        assertTrue( this.service.exists( resourceDomain, resourceContext1, "foo.groovy" ) );
        result = new String( this.service.read( resourceDomain, resourceContext1, "foo.groovy" ) );
        assertEquals( "return \"FOO\";", result );

        // direct match
        String[] resourceContext2 = { "foo", "bar" };
        assertTrue( this.service.exists( resourceDomain, resourceContext2, "bar.groovy" ) );
        result = new String( this.service.read( resourceDomain, resourceContext2, "bar.groovy" ) );
        assertEquals( "return \"BAR\";", result );

        // we have to go up one level to find foo.groofy
        String[] resourceContext3 = { "foo", "bar" };
        assertTrue( this.service.exists( resourceDomain, resourceContext3, "foo.groovy" ) );
        result = new String( this.service.read( resourceDomain, resourceContext3, "foo.groovy" ) );
        assertEquals( "return \"FOO\";", result );

        // test the locate() method to retrieve foo.groovy
        result = this.service.locate( resourceDomain, resourceContext3, "foo.groovy" );
        assertEquals( "foo/foo.groovy", result );
        result = new String( this.service.read( resourceDomain, result ) );
        assertEquals( "return \"FOO\";", result );

        // we have to go up two levels to find empty.groovy
        String[] resourceContext4 = { "foo", "bar" };
        assertTrue( this.service.exists( resourceDomain, resourceContext4, "empty.groovy" ) );
        result = new String( this.service.read( resourceDomain, resourceContext4, "empty.groovy" ) );
        assertEquals( "return true;", result );
        
        // search for a non-existing resource with an existing context
        String[] resourceContext5 = { "foo", "bar" };
        assertFalse( this.service.exists( resourceDomain, resourceContext5, "bogus.groovy" ) );
        try
        {
            this.service.read( resourceDomain, resourceContext5, "bogus.groovy" );
            fail( "We expected an IOExeption when accessing a missing resource");
        }
        catch( IOException e )
        {
            // expected
        }
        
        // search for a non-existing resource with a bogis context
        String[] resourceContext6 = { "bar", "foo" };
        assertFalse( this.service.exists( resourceDomain, resourceContext6, "bogus.groovy" ) );
        try
        {
            this.service.read( resourceDomain, resourceContext6, "bogus.groovy" );
            fail( "We expected an IOExeption when accessing a missing resource");
        }
        catch( IOException e )
        {
            // expected
        }

        // test for a <null> in the context
        String[] resourceContext7 = { "bar", null };
        try
        {
            this.service.exists( resourceDomain, resourceContext7, "bogus.groovy" );
            fail( "We expected an Exeption when using a <null> context");
        }
        catch( Exception e )
        {
            // expected
        }
    }

    /**
     * Try to access an unknown domain
     */
    public void testUnknownDomain() throws Exception
    {
        String[] resourceContext = { "foo" };

        assertFalse( this.service.exists( "bogus" ) );

        try
        {
            this.service.read( "bogus", resourceContext, "foo.groovy" );
        }
        catch (Exception e)
        {
            // that's fine
        }
    }

    /**
     * Create a resource file using the various input data types
     */
    public void testCreateResources() throws Exception
    {
        String result = null;
        String resourceDomain = "test";
        String resourceName = null;
        String resourceContent = null;

        // use a byte[]

        resourceName = "byte.txt";
        resourceContent = "Hello World";
        assertFalse( this.service.exists( resourceDomain, resourceName ) );
        this.service.create( resourceDomain, resourceName, resourceContent.getBytes( "utf-16") );
        assertTrue( this.service.exists( resourceDomain, resourceName ) );
        result = new String( this.service.read( resourceDomain, resourceName ), "utf-16" );
        assertEquals( result, resourceContent );
        this.service.delete( resourceDomain, resourceName );
        assertFalse( this.service.exists( resourceDomain, resourceName ) );

        // use a java.io.InputStream

        resourceName = "inputstream.txt";
        resourceContent = "Hello World";
        FileInputStream fis = new FileInputStream( "project.xml" );
        this.service.create( resourceDomain, resourceName, fis );
        fis.close();
        assertTrue( this.service.read(resourceDomain,resourceName).length > 0 );
        this.service.delete( resourceDomain, resourceName );
        assertFalse( this.service.exists( resourceDomain, resourceName ) );

        // use java.util.Properties

        resourceName = "properties.txt";
        Properties props = new Properties(); 
        props.setProperty("foo","bar");
        this.service.create( resourceDomain, resourceName, props );
        this.service.delete( resourceDomain, resourceName );
        assertFalse( this.service.exists( resourceDomain, resourceName ) );

        // use a java.util.Date - throws an exception

        resourceName = "date.txt";
        Date date = new Date();
        try
        {
            this.service.create( resourceDomain, resourceName, date );
        }
        catch (RuntimeException e)
        {
            // that's expected
        }
    }

    /**
     * Get the URL of the underlying resource.
     * @throws Exception
     */
    public void testGetResourceURL() throws Exception
    {
        String resourceDomain = "groovy";
        String[] resourceContext = { "foo" };        
        URL location = null;
        
        // look for a non-existent resource
        location = this.service.getResourceURL( resourceDomain, resourceContext, "bar.xsl" );  
        assertNull( location );

        // look for an existing resource
        location = this.service.getResourceURL( resourceDomain, resourceContext, "foo.groovy" );   
        assertNotNull( location );
    }
    
    /**
     * Create a resource file using the various input data types
     */
    public void testAutoDecrytpion() throws Exception
    {
        String result = null;
        String resourceDomain = "crypto";
        String resourceName = null;
        String resourceContent = null;

        // dump a text which is automatically encrypted

        resourceName = "lyrics.txt";
        resourceContent = "Nobody knows the troubles I have seen ...";
        assertFalse( this.service.exists( resourceDomain, resourceName ) );
        this.service.create( resourceDomain, resourceName, resourceContent.getBytes() );
        assertTrue( this.service.exists( resourceDomain, resourceName ) );
        result = new String( this.service.read( resourceDomain, resourceName ) );
        assertEquals( result, resourceContent );
        this.service.delete( resourceDomain, resourceName );
        assertFalse( this.service.exists( resourceDomain, resourceName ) );

        // load a plain text resource
        
        resourceName = "plain.txt";
        assertTrue( this.service.exists( resourceDomain, resourceName ) );
        result = new String( this.service.read( resourceDomain, resourceName ) );
        assertTrue( result.indexOf("http://www.apache.org/licenses/LICENSE-2.0") > 0 );        
    }
    
    /**
     * Use a proper limit and run the test in a seperate VM to find
     * instabilities. Don't use this in Maven since it blows up due
     * to the caching of the JUNIT reports and output onstdout.
     */
    public void _testLongRunningBehaviour() throws Exception
    {
        for( int i=0; i<10; i++ )
        {
            if( i % 10 == 0 )
            {
                System.out.println( "########################################################################" );
                System.out.println( "We already have " + i + " iterations ..." );
                System.out.println( "########################################################################" );
            }
            this.testCreateResources();
            this.testListDomains();
            this.testLocator();
            this.testResourceCRUD();
            this.testUnknownDomain();
        }
    }

}
