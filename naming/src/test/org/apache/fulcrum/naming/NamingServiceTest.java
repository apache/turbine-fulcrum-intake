package org.apache.fulcrum.naming;

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


import org.apache.fulcrum.testcontainer.BaseUnitTest;
/**
 * NamingServiceTest
 *
 * @author <a href="epugh@opensourceconnections.com">Eric Pugh</a>
 * @version $Id$
 */
public class NamingServiceTest extends BaseUnitTest
{
    private NamingService namingService = null;



    /**
     * Defines the testcase name for JUnit.
     *
     * @param name the testcase's name.
     */
    public NamingServiceTest(String name)
    {
        super(name);
    }


    /**
     * Simple test that adds, retrieves, and deletes 2 object.
     *
     * @throws Exception generic exception
     */
    public void testGettingNamingService() throws Exception
    {
        NamingService ns = (NamingService) this.lookup(NamingService.ROLE);
        assertNotNull(ns);
    }

}
