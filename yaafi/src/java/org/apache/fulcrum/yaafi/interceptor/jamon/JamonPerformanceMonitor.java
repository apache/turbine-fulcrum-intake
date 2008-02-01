package org.apache.fulcrum.yaafi.interceptor.jamon;
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

/**
 * Expose the start()/stop() methods for performance monitors independent from their concrete
 * implementation.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public interface JamonPerformanceMonitor
{
    /** Start the monitor. */
    public void start();

    /** Stop the monitor. */
    public void stop();

    /**
     * Stop the monitor based on an Throwable.
     * @param throwable the throwable
     */
    public void stop(Throwable throwable);

    /**
     * Create a performance report
     * @return the textual performance report
     * @throws Exception generating the report failed
     */
    public String createReport() throws Exception;
}
