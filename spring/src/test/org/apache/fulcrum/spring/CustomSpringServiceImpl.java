package org.apache.fulcrum.spring;

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

import org.apache.fulcrum.yaafi.service.systemproperty.SystemPropertyService;
import org.apache.avalon.framework.service.ServiceManager;

/**
 * A Spring bean using Avalon services.
 */
public class CustomSpringServiceImpl implements CustomSpringService {

    /** message injected from the Spring configuration */
    private String greeting;

    /** directly injected as reference*/
    private ServiceManager serviceManager;

    /** injected using AvalonServiceFactoryBean (the service is instantiated by YAAFI) */
    private SystemPropertyService systemPropertyService;

    public CustomSpringServiceImpl() {
    }

    public String getGreeting() {
        return greeting;
    }

    public void setGreeting(String greeting) {
        this.greeting = greeting;
    }

    public void setServiceManager(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    public void setSystemPropertyService(SystemPropertyService systemPropertyService) {
        this.systemPropertyService = systemPropertyService;
    }

    public void sayGretting() {
        System.out.println(this.greeting);
        System.out.println(this.serviceManager.toString());
        System.out.println(this.systemPropertyService.toString());
    }
}
