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

import org.springframework.beans.factory.BeanFactory;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceException;

/**
 * Wraps a Spring bean factory to implement an Avalon service
 * lookup - this allows to delegate a service lookup to Spring.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */
public class BeanFactoryServiceManager implements ServiceManager
{
    /** Spring's bean factory */
    private BeanFactory beanFactory;

    /**
     * Constructor
     *
     * @param beanFactory Spring's bean factory
     */
    public BeanFactoryServiceManager(BeanFactory beanFactory)
    {
        this.beanFactory = beanFactory;
    }

    /** @see org.apache.avalon.framework.service.ServiceManager#lookup(String) */
    public Object lookup(String key) throws ServiceException
    {
        try
        {
            return beanFactory.getBean(key);
        }
        catch(Exception e)
        {
            throw new ServiceException(key, "Unable to lookup service using Spring's BeanFactory", e);
        }
    }

    /** @see org.apache.avalon.framework.service.ServiceManager#hasService(String) */
    public boolean hasService(String key)
    {
        return beanFactory.containsBean(key);
    }

    /** @see org.apache.avalon.framework.service.ServiceManager#release(Object) */
    public void release(Object o)
    {
        // nothing to do for Spring beans
    }
}