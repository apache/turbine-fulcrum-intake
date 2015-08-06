package org.apache.fulcrum.yaafi.interceptor.javasimon;

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

import java.lang.reflect.Method;

import org.apache.fulcrum.yaafi.interceptor.util.MethodToStringBuilderImpl;
import org.javasimon.SimonManager;
import org.javasimon.Split;
import org.javasimon.Stopwatch;

/**
 * Encapsulating the JAMon 2.x related API calls. JAMon 2.x allows for a much
 * more powerful integration with Avalon services :
 * <ul>
 *  <li>custom ranges/units</li>
 *  <li>exception monitoring</li>
 *  <li>smooth web interface</li>
 * </ul>
 *
 * @since 1.0.7
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public class JavaSimon4PerformanceMonitorImpl implements JavaSimonPerformanceMonitor
{
    /** is monitoring enabled */
    private boolean isActive;

    /** the method currently monitored */
    private Method method;

    /** the split for this invocation */
    private Split split;

    /**
     * Constructor.
     *
     * @param serviceName the service name of the service being monitored
     * @param method the method to be monitored
     * @param isActive is this an active monitor
     */
    public JavaSimon4PerformanceMonitorImpl(String serviceName, Method method, Boolean isActive)
    {
        this.method = method;
        this.isActive = isActive.booleanValue();
    }

    /**
     * Start the monitor.
     */
    public void start()
    {
        if(this.isActive)
        {
            String methodSignature = createMethodSignature(this.method, 0);
            Stopwatch stopwatch = SimonManager.getStopwatch(methodSignature);
            this.split = stopwatch.start();
        }
    }

    /**
     * Stop the monitor
     */
    public void stop()
    {
        if(this.isActive) 
        {
            this.split.stop();
        }
    }

    /**
     * Stop the monitor
     */
    public void stop(Throwable throwable)
    {
        if(this.isActive)
        {
            this.split.stop();
        }
    }

    /**
     * Create a method signature - JavaSimon does not look names with '#' in it.
     */
    private String createMethodSignature(Method method, int mode)
    {
        MethodToStringBuilderImpl methodToStringBuilder = new MethodToStringBuilderImpl(method, mode);
        return methodToStringBuilder.toString().replace("#", "[]");
    }
}
