package org.apache.fulcrum.yaafi.framework.interceptor;

/*
 * Copyright 2004 Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


/**
 * Defining the common interface of all interceptors.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public interface AvalonInterceptorService
{
    /** indicating entering a service method */
    int ON_ENTRY = 0;

    /** indicating exiting a service method without throwing an exception */
    int ON_EXIT = 1;

    /** indicating exiting a service method throwing an exception */
    int ON_ERROR = 2;

    /**
     * Called before a service method is invoked.
     *
     * @param avalonInterceptorContext shared interceptor context
     */
    void onEntry( AvalonInterceptorContext avalonInterceptorContext );

    /**
     * Called after a service method was invoked.
     *
     * @param avalonInterceptorContext shared interceptor context
     * @param result the result of the invocation
     */
    void onExit( AvalonInterceptorContext avalonInterceptorContext, Object result );

    /**
     * Called when a service method throws an exeption
     *
     * @param avalonInterceptorContext shared interceptor context
     * @param t the resulting exception
     */
    void onError( AvalonInterceptorContext avalonInterceptorContext, Throwable t);
}