package org.apache.fulcrum.yaafi.service;

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
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.fulcrum.yaafi.DependentTestComponent;
import org.apache.fulcrum.yaafi.framework.container.ServiceContainer;
import org.apache.fulcrum.yaafi.framework.factory.ServiceContainerConfiguration;
import org.apache.fulcrum.yaafi.framework.factory.ServiceContainerFactory;
import org.apache.fulcrum.yaafi.interceptor.logging.LoggingInterceptorService;
import org.apache.fulcrum.yaafi.service.advice.AdviceService;

/**
 * Test suite for the ServiceManagereService.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public class AdviceServiceTest extends TestCase implements DependentTestComponent
{
    private AdviceService service;
    private DependentTestComponent advisedThis;
    private ServiceContainer container;


    /**
     * Constructor
     * @param name the name of the test case
     */
    public AdviceServiceTest( String name )
    {
        super(name);
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        ServiceContainerConfiguration config = new ServiceContainerConfiguration();
        config.loadContainerConfiguration( "./src/test/TestYaafiContainerConfig.xml" );
        this.container = ServiceContainerFactory.create( config );
        service = (AdviceService) this.container.lookup(AdviceService.class.getName());
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        ServiceContainerFactory.dispose(this.container);
        super.tearDown();
    }

    public static Test suite()
    {
        TestSuite suite= new TestSuite();

        suite.addTest( new AdviceServiceTest("testSimpleObject") );
        suite.addTest( new AdviceServiceTest("testDefaultAdvice") );
        suite.addTest( new AdviceServiceTest("testChainedAdvices") );
        suite.addTest( new AdviceServiceTest("testMultipleProxies") );


        return suite;
    }

    /**
     *  Advice a StringBuilder based on the CharSequence interface
     * @throws Exception generic exception
     */
    public void testSimpleAdvice() throws Exception
    {
        String[] interceptorList = { LoggingInterceptorService.class.getName() };
        StringBuilder unadvicedObject = new StringBuilder("foo");
        CharSequence advicedObject = (CharSequence) this.service.advice(unadvicedObject );

        int length = advicedObject.length();
        assertTrue(this.service.isAdviced(advicedObject));
        assertFalse(this.service.isAdviced(unadvicedObject));
        assertTrue(unadvicedObject.length() == length);
    }

    /**
     * Advice a StringBuilder based on the CharSequence interface
     * @throws Exception generic exception
     */
    public void testSimpleObject() throws Exception
    {
        String[] interceptorList = { LoggingInterceptorService.class.getName() };
        StringBuilder unadvicedObject = new StringBuilder("foo");
        CharSequence advicedObject = (CharSequence) this.service.advice("adviced", interceptorList, unadvicedObject );

        int length = advicedObject.length();
        assertTrue(this.service.isAdviced(advicedObject));
        assertFalse(this.service.isAdviced(unadvicedObject));
        assertTrue(unadvicedObject.length() == length);
    }

    /**
     * Advice a StringBuilder based on the CharSequenceInterface with default interceptors
     * @throws Exception generic exception
     */
    public void testDefaultAdvice() throws Exception
    {
        StringBuilder unadvicedObject = new StringBuilder("foo");
        CharSequence advicedObject = (CharSequence) this.service.advice("default adviced", unadvicedObject );

        advicedObject.length();
    }

    /**
     * The test implements the DependentTestComponent interface therefore we
     * are able to intercept the invocation of test(). Whereas test() invokes
     * another advised component.
     * @throws Exception generic exception
     */
    public void testChainedAdvices() throws Exception
    {
        String[] interceptorList = { LoggingInterceptorService.class.getName() };
        this.advisedThis = (DependentTestComponent) this.service.advice(interceptorList, this);
        this.advisedThis.test();
    }

    /**
     * Advice a StringBuilder based on the CharSequenceInterface
     * @throws Exception generic exception
     */
    public void testMultipleProxies() throws Exception
    {
        String[] interceptorList = { LoggingInterceptorService.class.getName() };
        StringBuilder unadvicedObject = new StringBuilder("foo");
        CharSequence advicedObject = (CharSequence) this.service.advice("first advice", interceptorList, unadvicedObject);
        CharSequence advicedAdvicedObject = (CharSequence) this.service.advice("second advice", interceptorList, advicedObject );

        advicedAdvicedObject.length();
        assertTrue(this.service.isAdviced(advicedAdvicedObject));
    }

    /* (non-Javadoc)
     * Advice a StringBuilder based on the CharSequenceInterface
     * @see org.apache.fulcrum.yaafi.DependentTestComponent#test()
     */
    public void test()
    {
        try
        {
            DependentTestComponent testComponent = (DependentTestComponent) this.container.lookup(
                DependentTestComponent.class.getName()
                );

            testComponent.test();
        }
        catch (ServiceException e)
        {
            throw new RuntimeException(e.getMessage());
        }
    }
}
