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

import org.apache.fulcrum.yaafi.framework.container.ServiceContainer;
import org.apache.fulcrum.yaafi.framework.factory.ServiceContainerConfiguration;
import org.apache.fulcrum.yaafi.framework.factory.ServiceContainerFactory;

/**
 * Test suite for the ReconfigurationService. This test doesn't do
 * anything apart from running a minute so you have some time to tinker
 * with the component configuration file.
 *
 * @author <a href="mailto:siegfried.goeschl@drei.com">Siegfried Goeschl</a>
 * @version $Id$
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
        if( this.container != null )
        {
            this.container.dispose();
        }

        super.tearDown();
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
        Thread.sleep(10000);
    }
}
