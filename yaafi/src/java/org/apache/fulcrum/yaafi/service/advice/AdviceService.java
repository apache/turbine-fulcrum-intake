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

package org.apache.fulcrum.yaafi.service.advice;

/**
 * Simple service providing interceptor advices for ordinary POJOs. Since the
 * implementation uses Dynamic Proxies only methods invoked by an interface
 * can be advised.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public interface AdviceService
{
    /**
     * Is the given object already adviced?
     * @param object the object to check
     * @return true if the object is an dynamic proxy
     */
    boolean isAdviced(Object object);

    /**
     * Advice the object with a the list of default AvalonInterceptorServices.
     * @param object the object to be advised
     * @return the advised object
     */
    Object advice(Object object);

    /**
     * Advice the object with a the list of default AvalonInterceptorServices.
     * @param name the name of the object
     * @param object the object to be advised
     * @return the advised object
     */
    Object advice(String name, Object object);

    /**
     * Advice the object with a list of AvalonInterceptorServices.
     * @param object the object to be advised
     * @param interceptorList the list of service names
     * @return the advised object
     */
    Object advice(String[] interceptorList, Object object );

    /**
     * Advice the object with a list of AvalonInterceptorServices.
     * @param name the associated name of the object
     * @param object the object to be advised
     * @param interceptorList the list of service names
     * @return the advised object
     */
    Object advice(String name, String[] interceptorList, Object object);
}
