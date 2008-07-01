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

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.apache.avalon.framework.service.ServiceManager;

/**
 * A Spring factoryy bean to lookup Avalon service and inject
 * them to other Spring beans.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public class AvalonServiceFactoryBean implements FactoryBean, InitializingBean {

    /** the Avalon ServiceManager */
    private ServiceManager serviceManager;

    /** the name of the Avalon service to resolve */
    private String serviceName;

    /** the resolved Avalon service */
    private Object service;

    /**
     * Resolve the service name to a service.
     * 
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception {
        this.service = this.serviceManager.lookup(this.serviceName);            
    }

    /**
     * Set the Avalon ServiceManager to lookup the Avalon service.
     *
     * @param serviceManager the Avalon ServiceManager
     */
    public void setServiceManager(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    /**
     * Set the name of the Avalon service to be resolved by
     * this Spring bean factory.
     *
     * @param serviceName the Avalon service name
     */
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /** @see org.springframework.beans.factory.FactoryBean#getObject() */
    public Object getObject() throws Exception {
        return this.service;
    }

    /** @see org.springframework.beans.factory.FactoryBean#getObjectType() */
    public Class getObjectType() {
        return this.service.getClass();
    }

    /** @see org.springframework.beans.factory.FactoryBean#isSingleton() */
    public boolean isSingleton() {
        return true;
    }
}
