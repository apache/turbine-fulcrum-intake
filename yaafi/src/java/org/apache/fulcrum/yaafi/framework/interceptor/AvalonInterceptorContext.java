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
/*
 * Created on 14.09.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.apache.fulcrum.yaafi.framework.interceptor;

import java.lang.reflect.Method;
import java.util.Map;

import org.apache.fulcrum.yaafi.framework.tls.ThreadLocalStorage;

/**
 * Contains context information for the interceptors being invoked. The
 * class contains a request context which allows to store data from within an
 * interceptor. It also provides access to a ThreadLocalStorage to associate
 * data with the current thread.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */
public interface AvalonInterceptorContext
{
    /**
     * @return Returns the context for the given request.
     */
    Map getRequestContext();

    /**
     * @return Returns the serviceDelegate.
     */
    Object getServiceDelegate();

    /**
     * @return Returns the serviceName.
     */
    String getServiceName();

    /**
     * @return Returns the serviceShorthand.
     */
    String getServiceShorthand();

    /**
     * @return Returns the args.
     */
    Object [] getArgs();

    /**
     * @return Returns the method.
     */
    Method getMethod();

    /**
     * @return Returns the ThreadLocalStorage
     */
    ThreadLocalStorage getThreadContext();

    /**
     * @return is a transaction id defined for the current thread
     */
    boolean hasTransactionId();

    /**
     * @return get the transaction id defined for the current thread
     */
    Object getTransactionId();

    /**
     * Set the transaction id for the current thread.
     * @param transactionId the transaction id
     */
    void setTransactionId(Object transactionId);

    /**
     * Clears the transaction id for the current thread.
     */
    void clearTransactionId();

    /**
     * Increment the current service invocation depth
     */
    void incrementInvocationDepth();

    /**
     * Decrement the current service invocation depth
     */
    void decrementInvocationDepth();

    /**
     * Get the current service invocation depth
     * @return the current service invocation depth
     */
    int getInvocationDepth();

    /**
     * @return Returns the invocationId.
     */
    Long getInvocationId();
}
