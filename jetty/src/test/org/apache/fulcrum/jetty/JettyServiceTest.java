package org.apache.fulcrum.jetty;

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

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.fulcrum.testcontainer.BaseUnitTest;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.configuration.Reconfigurable;

import java.net.URL;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Testing the embedded Jetty container. We are basically
 * invoking the various URL to make sure that everything
 * is properly wired.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */
public class JettyServiceTest extends BaseUnitTest
{
    /** the service to test */
    private JettyService service;

    /**
     * Defines the testcase name for JUnit.
     *
     * @param name the testcase's name.
     */
    public JettyServiceTest(String name)
    {
        super(name);
    }

    /**
     * Test setup
     */
    protected void setUp() throws Exception
    {
        super.setUp();

        try
        {
            service = (JettyService) this.lookup(JettyService.class.getName());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    /**
     * Add all of our test suites
     */
    public static Test suite()
    {
        TestSuite suite= new TestSuite();
        suite.addTest( new JettyServiceTest("testGetJettyService") );
        suite.addTest( new JettyServiceTest("testInvokeTestServlets") );
        suite.addTest( new JettyServiceTest("testInvokeTestJsps") );
        suite.addTest( new JettyServiceTest("testStartStop") );
        return suite;
    }

    /**
     * @return the JettyService service to be used
     */
    protected JettyService getService()
    {
        return this.service;
    }
    
    /////////////////////////////////////////////////////////////////////////
    // Start of unit tests
    /////////////////////////////////////////////////////////////////////////

    /**
     * Ensure that we start Jetty properly.
     *
     * @throws Exception the test failed
     */
    public void testGetJettyService() throws Exception
    {
        assertTrue(this.service.getServer() != null);
    }

    /**
     * Fetch the start page.
     *
     * @throws Exception the test failed
     */
    public void testGetIndexPage() throws Exception
    {
        String result = this.getURLContent("http://localhost:8080/test/");
        assertTrue(result.length() > 100);
    }

    /**
     * Invoke all servlet examples.
     *
     * @throws Exception the test failed
     */
    public void testInvokeTestServlets() throws Exception
    {
        String result = null;

        // 'Hello World' servlet
        result = this.getURLContent("http://localhost:9080/test/hello/");
        assertTrue(result.indexOf("Hello") > 0);

        // 'Request Dump' servlet
        result = this.getURLContent("http://localhost:9080/test/dump/info");
        assertTrue(result.length() > 100);

        // 'Session Dump' servlet
        result = this.getURLContent("http://localhost:9080/test/session/");
        assertTrue(result.length() > 100);

        // 'Cookie Dump' servlet
        result = this.getURLContent("http://localhost:9080/test/cookie/");
        assertTrue(result.length() > 100);
    }

    /**
     * Invoke a JSP examples.
     *
     * @throws Exception the test failed
     */
    public void testInvokeTestJsps() throws Exception
    {
        String result = null;

        // invoke Snoop JSP
        result = this.getURLContent("http://localhost:9080/test/snoop.jsp");
        assertTrue(result.indexOf("WebApp JSP Snoop page") > 0);

        // invoke Snoop JSP
        result = this.getURLContent("http://localhost:9080/test/jsp/bean1.jsp");
        assertTrue(result.indexOf("Counter accessed 1 times.") > 0);
        
    }

    /**
     * Reconfigure a running Jetty using the Avalon Lifycyle 'stop', 'reconfigure', 'start'.
     *
     * @throws Exception the test failed
     */
    public void testStartStop() throws Exception
    {
        String result = null;

        ((Startable) this.getService()).stop();

        try
        {
            result = this.getURLContent("http://localhost:9080/test/hello/");
            fail("The Jetty instance should be stopped");
        }
        catch(Exception e)
        {
            // expected
        }

        ((Reconfigurable) this.getService()).reconfigure(null);
        ((Startable) this.getService()).start();

        result = this.getURLContent("http://localhost:9080/test/hello/");
        assertTrue(result.indexOf("Hello") > 0);
    }

    /**
     * Quick-and-dirty implementation to downlaod te content from an URL.
     *
     * @param urlString the URL to be invoked
     * @return the content loaded from the URL
     * @throws Exception invoking the URL failed
     */
    private String getURLContent(String urlString) throws Exception
    {
        StringBuffer result = new StringBuffer(8192);
        URL url = new URL(urlString);
        InputStream is = url.openStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        char[] buffer = new char[8192];
        while(reader.read(buffer) > 0) {
            result.append(buffer);
        }
        is.close();
        return result.toString();
    }
}
