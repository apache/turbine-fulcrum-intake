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

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.Reconfigurable;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.fulcrum.yaafi.framework.interceptor.AvalonInterceptorContext;
import org.apache.fulcrum.yaafi.framework.reflection.Clazz;
import org.apache.fulcrum.yaafi.interceptor.baseservice.BaseInterceptorServiceImpl;

/**
 * A service using JavaSimon for performance monitoring. The implementation
 * relies on reflection to invoke JavaSimon to avoid compile-time coupling.
 *
 * @since 1.0.7
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public class JavaSimonInterceptorServiceImpl
    extends BaseInterceptorServiceImpl
    implements JavaSimonInterceptorService, Reconfigurable, ThreadSafe, Disposable, Initializable
{
	/** are the JavaSimon classes in the classpath */
	private boolean isJavaSimonAvailable;

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

    /** the class name of the JavaSimon factory */
    private static final String MONITORFACTORY_CLASSNAME = "org.javasimon.SimonManager";

    /** the class name of the JavaSimon MonitorFactory */
    private static final String DEFAULT_PERFORMANCEMONITOR_CLASSNAME = "org.apache.fulcrum.yaafi.interceptor.javasimon.JavaSimon4PerformanceMonitorImpl";

    /////////////////////////////////////////////////////////////////////////
    // Avalon Service Lifecycle Implementation
    /////////////////////////////////////////////////////////////////////////

    /**
     * Constructor
     */
    public JavaSimonInterceptorServiceImpl()
    {
        super();
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(Configuration)
     */
    public void configure(Configuration configuration) throws ConfigurationException
    {
        super.configure(configuration);
        this.reportTimeout = configuration.getChild("reportTimeout").getValueAsLong(0);

        // parse the performance monitor class name
        this.performanceMonitorClassName = configuration.getChild("performanceMonitorClassName").getValue(DEFAULT_PERFORMANCEMONITOR_CLASSNAME);

        // parse the report file name
        String reportFileName = configuration.getChild("reportFile").getValue("./javasimon.html");
        this.reportFile = this.makeAbsoluteFile( reportFileName );

        // determine when to create the next report
        this.nextReportTimestamp = System.currentTimeMillis() + this.reportTimeout;

        // do we create a report on disposal
        this.reportOnExit = configuration.getChild("reportOnExit").getValueAsBoolean(false);
    }

    /**
     * @see Initializable#initialize()
     */
    public void initialize() throws Exception
    {
        ClassLoader classLoader = this.getClassLoader();

        if (!Clazz.hasClazz(classLoader, MONITORFACTORY_CLASSNAME))
        {
            String msg = "The JavaSimonInterceptorService is disabled since the JavaSimon classes are not found in the classpath";
            this.getLogger().warn(msg);
            this.isJavaSimonAvailable = false;
            return;
        }

        if (!Clazz.hasClazz(classLoader, this.performanceMonitorClassName))
        {
            String msg = "The JavaSimonInterceptorService is disabled since the performance monitor class is not found in the classpath";
            this.getLogger().warn(msg);
            this.isJavaSimonAvailable = false;
            return;
        }

        // load the performance monitor class
        this.performanceMonitorClass = Clazz.getClazz(this.getClassLoader(), this.performanceMonitorClassName);

        // check if we can create an instance of the performance monitor class
        JavaSimonPerformanceMonitor testMonitor = this.createJavaSimonPerformanceMonitor(null, null, true);
        if(testMonitor == null)
        {
            String msg = "The JavaSimonInterceptorService is disabled since the performance monitor can't be instantiated";
            this.getLogger().warn(msg);
            this.isJavaSimonAvailable = false;
            return;
        }

        this.getLogger().debug("The JavaSimonInterceptorService is enabled");
        this.isJavaSimonAvailable = true;
    }

        /**
     * @see Reconfigurable#reconfigure(Configuration)
     */
    public void reconfigure(Configuration configuration) throws ConfigurationException
    {
        super.reconfigure(configuration);
        this.configure(configuration);
    }

    /**
     * @see Disposable#dispose()
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
     * @see org.apache.fulcrum.yaafi.framework.interceptor.AvalonInterceptorService#onEntry(AvalonInterceptorContext)
     */
    @SuppressWarnings("unchecked")
    public void onEntry(AvalonInterceptorContext interceptorContext)
    {
        if( this.isJavaSimonAvailable()  )
        {
            this.writeReport();

            String serviceShortHand = interceptorContext.getServiceShorthand();
            Method serviceMethod = interceptorContext.getMethod();
            boolean isEnabled = this.isServiceMonitored(interceptorContext );
            JavaSimonPerformanceMonitor monitor = this.createJavaSimonPerformanceMonitor(serviceShortHand, serviceMethod, isEnabled);
            monitor.start();
            interceptorContext.getRequestContext().put(this.getServiceName(), monitor);
        }
    }

    /**
     * @see org.apache.fulcrum.yaafi.framework.interceptor.AvalonInterceptorService#onExit(AvalonInterceptorContext, Object)
     */
    public void onExit(AvalonInterceptorContext interceptorContext, Object result)
    {
        if( this.isJavaSimonAvailable() )
        {
            JavaSimonPerformanceMonitor monitor;
            monitor = (JavaSimonPerformanceMonitor) interceptorContext.getRequestContext().remove(this.getServiceName());
            monitor.stop();
        }
    }

    /**
     * @see org.apache.fulcrum.yaafi.framework.interceptor.AvalonInterceptorService#onError(AvalonInterceptorContext, Throwable)
     */
    public void onError(AvalonInterceptorContext interceptorContext,Throwable t)
    {
        if( this.isJavaSimonAvailable() )
        {
            JavaSimonPerformanceMonitor monitor;
            monitor = (JavaSimonPerformanceMonitor) interceptorContext.getRequestContext().remove(this.getServiceName());
            monitor.stop(t);
        }
    }

    /**
     * Writes the JavaSimon report to the file system.
     *
     * @see Runnable#run()
     */
    public void run()
    {
        this.writeReport(this.reportFile);
    }

    /////////////////////////////////////////////////////////////////////////
    // Service Implementation
    /////////////////////////////////////////////////////////////////////////

    /**
     * @return Returns the isJavaSimonAvailable.
     */
    protected final boolean isJavaSimonAvailable()
    {
        return this.isJavaSimonAvailable;
    }

    /**
     * Factory method for creating an implementation of a JavaSimonPerformanceMonitor.
     *
     * @param serviceName the service name
     * @param method the method
     * @param isEnabled is the monitor enabled
     * @return the instance or <b>null</b> if the creation failed
     */
    protected JavaSimonPerformanceMonitor createJavaSimonPerformanceMonitor(String serviceName, Method method, boolean isEnabled)
    {
        JavaSimonPerformanceMonitor result = null;

        try
        {
            Class[] signature = { String.class, Method.class, Boolean.class };
            Object[] args = { serviceName, method, (isEnabled) ? Boolean.TRUE : Boolean.FALSE};
            result = (JavaSimonPerformanceMonitor) Clazz.newInstance(this.performanceMonitorClass, signature, args);
        }
        catch(Exception e)
        {
            String msg = "Failed to create a performance monitor instance : " + this.performanceMonitorClassName;
            this.getLogger().error(msg, e);
        }

        return result;
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

        if( this.isJavaSimonAvailable() )
        {
            try
            {
                if( this.getLogger().isDebugEnabled() )
                {
                    this.getLogger().debug( "Writing JavaSimon report to " + reportFile.getAbsolutePath() );
                }

                FileOutputStream fos = new FileOutputStream( reportFile );
                printWriter = new PrintWriter( fos );
                // JavaSimonPerformanceMonitor monitor = this.createJavaSimonPerformanceMonitor(null, null, true);
                String report = "Not implemented yet ...";
                printWriter.write( report );
                printWriter.close();
            }
            catch( Throwable t )
            {
                String msg = "Generating the JavaSimon report failed for " + reportFile.getAbsolutePath();
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
