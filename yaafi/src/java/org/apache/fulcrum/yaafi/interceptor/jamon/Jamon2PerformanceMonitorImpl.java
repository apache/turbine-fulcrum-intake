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

import java.lang.reflect.Method;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import com.jamonapi.RangeHolder;
import org.apache.fulcrum.yaafi.interceptor.util.MethodToStringBuilderImpl;

/**
 * Encapsulating the JAMon 2.x related API calls. JAMon 2.x allows for a much
 * more powerful integration with Avalon services :
 * <ul>
 *  <li>custom ranges/units</li>
 *  <li>exception monitoring</li>
 *  <li>smooth web interface</li>
 * </ul>
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public class Jamon2PerformanceMonitorImpl implements JamonPerformanceMonitor
{
    /** the default label being used */
    private static final String MONITOR_LABEL = "ms.services";

    /** our custom range definition */
    private static RangeHolder rangeHolder;

    /** is monitoring enabled */
    private boolean isActive;

    /** the method currently monitored */
    private Method method;

    /** the global JAMON monitor */
    private Monitor monitor;

    /** the time the monitoring was started */
    private long startTime;

    static
    {
        rangeHolder = Jamon2PerformanceMonitorImpl.createMSHolder();
    }
  
    /**
     * Constructor.
     *
     * @param serviceName the service name of the service being monitored
     * @param method the method to be monitored
     * @param isActive is this an active monitor
     */
    public Jamon2PerformanceMonitorImpl(String serviceName, Method method, Boolean isActive)
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
            // when reseting using the JAMon GUI the custom ranges are discarded
            MonitorFactory.setRangeDefault(MONITOR_LABEL, Jamon2PerformanceMonitorImpl.rangeHolder);
            // do the internal house-keeping
            this.startTime = System.currentTimeMillis();
            MethodToStringBuilderImpl methodToStringBuilder = new MethodToStringBuilderImpl(this.method, 0);
            String methodSignature = methodToStringBuilder.toString();
            this.monitor = MonitorFactory.getMonitor(methodSignature, MONITOR_LABEL);
            this.monitor.start();
        }
    }

    /**
     * Stop the monitor
     */
    public void stop()
    {
        if(this.isActive) 
        {
            long duration = System.currentTimeMillis() - this.startTime;
            this.monitor.add(duration);
            this.monitor.stop();
        }
    }

    /**
     * Stop the monitor
     */
    public void stop(Throwable throwable)
    {
        if(this.isActive)
        {
            // use a negative execution time to mark an exception for an affiliate
            this.monitor.add(-1);
            this.monitor.stop();  
        }
    }

    /**
     * Create a performance report.
     *
     * @return the textual performance report
     * @throws Exception generating the report failed
     */
    public String createReport() throws Exception
    {
        return MonitorFactory.getRootMonitor().getReport();
    }

    /**
     * @return a customized range holder for measuring the execution time for services.
     */
    private static RangeHolder createMSHolder() {
      RangeHolder result = new RangeHolder("<");
      result.add("Exceptions",0);
      result.add("0_10ms",10);
      result.add("10_20ms",20);
      result.add("20_40ms",40);
      result.add("40_80ms",80);
      result.add("80_160ms",160);
      result.add("160_320ms",320);
      result.add("320_640ms",640);
      result.add("640_1280ms",1280);
      result.add("1280_2560ms",2560);
      result.add("2560_5120ms",5120);
      result.add("5120_10240ms",10240);
      result.add("10240_20480ms",20480);
      result.addLastHeader("20480ms_");
      // note last range is always called lastRange and is added automatically
      return result;
    }
}
