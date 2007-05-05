package tutorial.running;

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


import org.apache.fulcrum.yaafi.framework.container.ServiceContainer;
import org.apache.fulcrum.yaafi.framework.factory.ServiceContainerConfiguration;
import org.apache.fulcrum.yaafi.framework.factory.ServiceContainerFactory;

/**
 * Test suite for exercising the command line integration.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public class Factory
{
    /**
     * Main routine
     * @param args the command line arguments
     */
    public static void main( String[] args )
    {
        try
        {
            ServiceContainer container = null;
            ServiceContainerConfiguration config = null;

            config = new ServiceContainerConfiguration();
            config.loadContainerConfiguration( "./tutorial/conf/containerConfiguration.xml" );
            container = ServiceContainerFactory.create( config );

            container.dispose();
        }
        catch( Throwable t )
        {
            String msg = "Execution failed : " + t.getMessage();
            System.err.println(msg);
        }
    }
 }
