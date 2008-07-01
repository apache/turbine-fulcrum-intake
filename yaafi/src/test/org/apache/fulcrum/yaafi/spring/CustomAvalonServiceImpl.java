package org.apache.fulcrum.yaafi.spring;

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

import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceException;


/**
 * Copies the properties found in the configuration into the SystemProperties
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public class CustomAvalonServiceImpl
    implements CustomAvalonService, Serviceable
{
    // a spring bean resolved by the Avalon ServiceManager */
    private CustomSpringService customSpringService;
    
    /**
     * Constructor
     */
    public CustomAvalonServiceImpl()
    {
        // nothing to do here
    }

    public void service(ServiceManager serviceManager) throws ServiceException
    {
        this.customSpringService = (CustomSpringService) serviceManager.lookup("customSpringService");
    }

    public void sayGretting()
    {
        // delegate the functionality to the Spring bean
        this.customSpringService.sayGretting();
    }

    public String getGreeting()
    {
        // delegate the functionality to the Spring bean
        return this.customSpringService.getGreeting();
    }
}