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

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.Reconfigurable;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.fulcrum.yaafi.framework.interceptor.AvalonInterceptorContext;
import org.apache.fulcrum.yaafi.framework.reflection.Clazz;
import org.apache.fulcrum.yaafi.interceptor.baseservice.BaseInterceptorServiceImpl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;

/**
 * A service using JAMon for performance monitoring. The implementation
 * relies on reflection to invoke JAMON to avoid compile-time coupling.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public class JamonInterceptorServiceImpl
    extends BaseInterceptorServiceImpl
    implements JamonInterceptorService, Reconfigurable, ThreadSafe, Disposable, Initializable
{
	/** are the JAMon classes in the classpath */
	private boolean isJamonAvailable;

    /** the file to hold the report */
    private File reportFile;

    /** the time in ms between two reports */
    private long reportTimeout;

    /** do we create a report during disposal of the service */
    private boolean reportOnExit;

    /** the time when the next report is due */
    private long nextReportTimestamp;

    /** the implementation class name for the performance monitor */
    private String performanceMonitorClassName;

    /** the implementation class name for the performance monitor */
    private Class performanceMonitorClass;

    /** the class name of the JAMon MonitorFactory */
    private static final String MONITORFACTORY_CLASSNAME = "com.jamonapi.MonitorFactory";

    /** the class name of the JAMon MonitorFactory */
    private static final String DEFAULT_PERFORMANCEMONITOR_CLASSNAME = "org.apache.fulcrum.yaafi.interceptor.jamon.Jamon1PerformanceMonitorImpl";

    /////////////////////////////////////////////////////////////////////////
    // Avalon Service Lifecycle Implementation
    /////////////////////////////////////////////////////////////////////////

    /**
     * Constructor
     */
    public JamonInterceptorServiceImpl()
    {
        super();
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration configuration) throws ConfigurationException
    {
        super.configure(configuration);
        this.reportTimeout = configuration.getChild("reportTimeout").getValueAsLong(0);

        // parse the performance monitor class name
        this.performanceMonitorClassName = configuration.getChild("performanceMonitorClassName").getValue(DEFAULT_PERFORMANCEMONITOR_CLASSNAME);

        // parse the report file name
        String reportFileName = configuration.getChild("reportFile").getValue("./jamon.html");
        this.reportFile = this.makeAbsoluteFile( reportFileName );

        // determine when to create the next report
        this.nextReportTimestamp = System.currentTimeMillis() + this.reportTimeout;

        // do we create a report on disposal
        this.reportOnExit = configuration.getChild("reportOnExit").getValueAsBoolean(false);
    }

    /**
     * @see org.apache.avalon.framework.activity.Initializable#initialize()
     */
    public void initialize() throws Exception
    {
        ClassLoader classLoader = this.getClassLoader();

        if (!Clazz.hasClazz(classLoader, MONITORFACTORY_CLASSNAME))
        {
            String msg = "The JamonInterceptorService is disabled since the JAMON classes are not found in the classpath";
            this.getLogger().warn(msg);
            this.isJamonAvailable = false;
            return;
        }
        
        if (!Clazz.hasClazz(classLoader, this.performanceMonitorClassName))
        {
            String msg = "The JamonInterceptorService is disabled since the performance monitor class is not found in the classpath";
            this.getLogger().warn(msg);
            this.isJamonAvailable = false;
            return;
        }

        // load the performance monitor class
        this.performanceMonitorClass = Clazz.getClazz(this.getClassLoader(), this.performanceMonitorClassName);

        // check if we can create an instance of the performance monitor class
        JamonPerformanceMonitor testMonitor = this.createJamonPerformanceMonitor(null, null, true);
        if(testMonitor == null)
        {
            String msg = "The JamonInterceptorService is disabled since the performance monitor can't be instantiated";
            this.getLogger().warn(msg);
            this.isJamonAvailable = false;
            return;
        }

        this.getLogger().debug("The JamonInterceptorService is enabled");
        this.isJamonAvailable = true;
    }

        /**
     * @see org.apache.avalon.framework.configuration.Reconfigurable#reconfigure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void reconfigure(Configuration configuration) throws ConfigurationException
    {
        super.reconfigure(configuration);
        this.configure(configuration);
    }

    /**
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     */
    public void dispose()
    {
        if( this.reportOnExit )
        {
            this.run();
        }

        this.reportFile = null;
    }

    /////////////////////////////////////////////////////////////////////////
    // Service interface implementation
    /////////////////////////////////////////////////////////////////////////

    /**
     * @see org.apache.fulcrum.yaafi.framework.interceptor.AvalonInterceptorService#onEntry(org.apache.fulcrum.yaafi.framework.interceptor.AvalonInterceptorContext)
     */
    public void onEntry(AvalonInterceptorContext interceptorContext)
    {
        if( this.isJamonAvailable()  )
        {
            this.writeReport();

            String serviceShortHand = interceptorContext.getServiceShorthand();
            Method serviceMethod = interceptorContext.getMethod();
            boolean isEnabled = this.isServiceMonitored(interceptorContext );
            JamonPerformanceMonitor monitor = this.createJamonPerformanceMonitor(serviceShortHand, serviceMethod, isEnabled);
            monitor.start();
            interceptorContext.getRequestContext().put(this.getServiceName(), monitor);
        }
    }

    /**
     * @see org.apache.fulcrum.yaafi.framework.interceptor.AvalonInterceptorService#onExit(org.apache.fulcrum.yaafi.framework.interceptor.AvalonInterceptorContext, java.lang.Object)
     */
    public void onExit(AvalonInterceptorContext interceptorContext, Object result)
    {
        if( this.isJamonAvailable() )
        {
            JamonPerformanceMonitor monitor;
            monitor = (JamonPerformanceMonitor) interceptorContext.getRequestContext().remove(this.getServiceName());
            monitor.stop();
        }
    }

    /**
     * @see org.apache.fulcrum.yaafi.framework.interceptor.AvalonInterceptorService#onError(org.apache.fulcrum.yaafi.framework.interceptor.AvalonInterceptorContext, java.lang.Throwable)
     */
    public void onError(AvalonInterceptorContext interceptorContext,Throwable t)
    {
        if( this.isJamonAvailable() )
        {
            JamonPerformanceMonitor monitor;
            monitor = (JamonPerformanceMonitor) interceptorContext.getRequestContext().remove(this.getServiceName());
            monitor.stop(t);
        }
    }

    /**
     * Writes the JAMON report to the file system.
     *
     * @see java.lang.Runnable#run()
     */
    public void run()
    {
        this.writeReport(this.reportFile);
    }

    /////////////////////////////////////////////////////////////////////////
    // Service Implementation
    /////////////////////////////////////////////////////////////////////////

    /**
     * @return Returns the isJamonAvailable.
     */
    protected final boolean isJamonAvailable()
    {
        return this.isJamonAvailable;
    }

    /**
     * Factory method for creating an implementation of a JamonPerformanceMonitor.
     *
     * @param serviceName the service name
     * @param method the method
     * @param isEnabled is the monitor enabled
     * @return the instance or <b>null</b> if the creation failed
     */
    protected JamonPerformanceMonitor createJamonPerformanceMonitor(String serviceName, Method method, boolean isEnabled)
    {
        JamonPerformanceMonitor result = null;

        try
        {
            Class[] signature = { String.class, Method.class, Boolean.class };
            Object[] args = { serviceName, method, (isEnabled) ? Boolean.TRUE : Boolean.FALSE};
            result = (JamonPerformanceMonitor) Clazz.newInstance(this.performanceMonitorClass, signature, args);
            return result;
        }
        catch(Exception e)
        {
            String msg = "Failed to create a performance monitor instance : " + this.performanceMonitorClassName;
            this.getLogger().error(msg, e);
            return result;
        }
    }

    /**
     * Write a report file
     */
    protected void writeReport()
    {
        if( this.reportTimeout > 0 )
        {
            long currTimestamp = System.currentTimeMillis();

            if( currTimestamp > this.nextReportTimestamp )
            {
                this.nextReportTimestamp = currTimestamp + this.reportTimeout;
                this.writeReport(this.reportFile);
            }
        }
    }

    /**
     * Write the HTML report to the given destination.
     *
     * @param reportFile the report destination
     */
    protected void writeReport( File reportFile )
    {
        PrintWriter printWriter = null;

        if( this.isJamonAvailable() )
        {
            try
            {
                if( this.getLogger().isDebugEnabled() )
                {
                    this.getLogger().debug( "Writing JAMOM report to " + reportFile.getAbsolutePath() );
                }

                FileOutputStream fos = new FileOutputStream( reportFile );
                printWriter = new PrintWriter( fos );
                JamonPerformanceMonitor monitor = this.createJamonPerformanceMonitor(null, null, true);
                String report = monitor.createReport();
                printWriter.write( report );
                printWriter.close();
            }
            catch( Throwable t )
            {
                String msg = "Generating the JAMON report failed for " + reportFile.getAbsolutePath();
                this.getLogger().error(msg,t);
            }
            finally
            {
                if( printWriter != null )
                {
                    printWriter.close();
                }
            }
        }
    }
}
