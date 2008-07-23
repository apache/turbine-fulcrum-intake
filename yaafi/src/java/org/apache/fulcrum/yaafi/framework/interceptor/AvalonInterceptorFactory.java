package org.apache.fulcrum.yaafi.framework.interceptor;

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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.List;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.fulcrum.yaafi.framework.constant.AvalonYaafiConstants;
import org.apache.fulcrum.yaafi.framework.reflection.Clazz;
import org.apache.fulcrum.yaafi.framework.util.Validate;


/**
 * A factory for creating dynamic proxies for Avalon services.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public class AvalonInterceptorFactory
{
    /**
     * Creates a dynamic proxy wrapping a service instance.
     *
     * @param serviceName the name of the service
     * @param serviceShorthand the shorthand of the service being intercepted
     * @param serviceManager the corresponding service manager
     * @param serviceInterceptorList the list of interceptors to be installed
     * @param serviceDelegate the service implementation
     * @return a dynamic proxy
     * @throws ServiceException an interceptor was not found
     */
    public static Object create(
        String serviceName,
        String serviceShorthand,
        ServiceManager serviceManager,
        String[] serviceInterceptorList,
        Object serviceDelegate )
        throws ServiceException
    {
        Validate.notEmpty(serviceName,"serviceName");
        Validate.notEmpty(serviceShorthand,"serviceShorthand");
        Validate.notNull(serviceManager,"serviceManager");
        Validate.notNull(serviceInterceptorList,"serviceInterceptorList");
        Validate.notNull(serviceDelegate,"serviceDelegate");

        Object result = null;

        Class clazz = serviceDelegate.getClass();
        ClassLoader classLoader = clazz.getClassLoader();
        List interfaceList = Clazz.getAllInterfaces(clazz);

        // get the service interfaces to avoid lookups

        AvalonInterceptorService[] avalonInterceptorServices = resolve(
            serviceManager,
            serviceInterceptorList
            );

        InvocationHandler invocationHandler = new AvalonInterceptorInvocationHandler(
            serviceName,
            serviceShorthand,
            serviceDelegate,
            avalonInterceptorServices
            );

        result = Proxy.newProxyInstance(
            classLoader,
            (Class[]) interfaceList.toArray(new Class[interfaceList.size()]),
            invocationHandler
            );

        return result;
    }

    /**
     * Resolve all interceptor service names to service interfaces.
     *
     * @param serviceManager to lookup the services
     * @param interceptorList the list of service names
     * @return a list of interceptor services
     * @throws ServiceException an interceptor service was not found
     */
    private static AvalonInterceptorService[] resolve( ServiceManager serviceManager, String[] interceptorList )
        throws ServiceException
    {
        String interceptorServiceName = null;
        AvalonInterceptorService[] result = new AvalonInterceptorService[interceptorList.length];

        for( int i=0; i<interceptorList.length; i++ )
        {
            interceptorServiceName = interceptorList[i];
            Object currService = serviceManager.lookup(interceptorServiceName);

            if (currService instanceof AvalonInterceptorService)
            {
                result[i] = (AvalonInterceptorService) currService;
            }
            else
            {
                String msg = "The following service is not an AvalonInterceptorService : " + interceptorServiceName;
                throw new ServiceException(AvalonYaafiConstants.AVALON_CONTAINER_YAAFI,msg);
            }
        }

        return result;
    }
}
