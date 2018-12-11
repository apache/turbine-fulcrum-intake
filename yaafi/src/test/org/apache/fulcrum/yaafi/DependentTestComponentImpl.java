package org.apache.fulcrum.yaafi;


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


import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;

/**
 * Implementation of the mainly artifical component depending on TestComponent
 *
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 */
public class DependentTestComponentImpl
        extends AbstractLogEnabled
        implements Serviceable, DependentTestComponent
{
    /** out test component */
    private TestComponent testComponent;

    /* (non-Javadoc)
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager serviceManager) throws ServiceException
    {
        this.testComponent = (TestComponent) serviceManager.lookup(TestComponent.ROLE);
    }

    /* (non-Javadoc)
     * @see org.apache.fulcrum.yaafi.DependentTestComponent#test()
     */
    public void test()
    {
        this.testComponent.test();
    }
}
