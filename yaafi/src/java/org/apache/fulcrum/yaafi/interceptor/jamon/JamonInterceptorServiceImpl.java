package org.apache.fulcrum.yaafi.interceptor.jamon;

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
import org.apache.fulcrum.yaafi.interceptor.util.MethodToStringBuilderImpl;

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

    /** the class for JAMon MonitorFactory */
    private Class monitorFactoryClass;

    /** the class name of the JAMon MonitorFactory */
    private static final String MONITORFACTOTY_CLASSNAME = "com.jamonapi.MonitorFactory";

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
        String reportFileName = null;

        super.configure(configuration);
        this.reportTimeout = configuration.getChild("reportTimeout").getValueAsLong(0);

        // parse the report file name

        reportFileName = configuration.getChild("reportFile").getValue("./jamon.html");
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

        if (Clazz.hasClazz(classLoader, MONITORFACTOTY_CLASSNAME))
        {
            this.monitorFactoryClass = Clazz.getClazz(
                classLoader,
                MONITORFACTOTY_CLASSNAME
                );

            this.isJamonAvailable = true;
        }
        else
        {
            String msg = "The JamonInterceptorService is disabled since the JAMON classes are not found in the classpath";
            this.getLogger().warn(msg);
            this.isJamonAvailable = false;
        }
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
            this.getLogger().debug( "Creating JAMOM report ..." );
            this.run();
        }

        this.monitorFactoryClass = null;
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
        this.writeReport();

        if( this.isJamonAvailable() && this.isServiceMonitored(interceptorContext ) )
        {
            this.createMonitor(interceptorContext);
        }
    }

    /**
     * @see org.apache.fulcrum.yaafi.framework.interceptor.AvalonInterceptorService#onError(org.apache.fulcrum.yaafi.framework.interceptor.AvalonInterceptorContext, java.lang.Throwable)
     */
    public void onError(AvalonInterceptorContext interceptorContext,Throwable t)
    {
        if( this.isJamonAvailable() && this.isServiceMonitored(interceptorContext) )
        {
            Object monitor = this.getMonitor(interceptorContext);
            this.stopMonitor(monitor);
        }
    }

    /**
     * @see org.apache.fulcrum.yaafi.framework.interceptor.AvalonInterceptorService#onExit(org.apache.fulcrum.yaafi.framework.interceptor.AvalonInterceptorContext, java.lang.Object)
     */
    public void onExit(AvalonInterceptorContext interceptorContext, Object result)
    {
        if( this.isJamonAvailable() && this.isServiceMonitored(interceptorContext) )
        {
            Object monitor = this.getMonitor(interceptorContext);
            this.stopMonitor(monitor);
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
    protected boolean isJamonAvailable()
    {
        return this.isJamonAvailable;
    }

    /**
     * Creates a JAMON monitor
     *
     * @param interceptorContext the current interceptor context
     */
    protected void createMonitor(
        AvalonInterceptorContext interceptorContext )
    {
        Method method = interceptorContext.getMethod();
        MethodToStringBuilderImpl methodToStringBuilder = new MethodToStringBuilderImpl(method,0);
        String monitorCategory = methodToStringBuilder.toString();
        Object monitor = this.createMonitor(monitorCategory);
        interceptorContext.getRequestContext().put(this.getServiceName(),monitor);
    }

    /**
     * Gets the JAMON Monitor
     *
     * @param interceptorContext the current interceptor context
     * @return the monitor
     */
    protected Object getMonitor(
        AvalonInterceptorContext interceptorContext )
    {
        return interceptorContext.getRequestContext().remove(
            this.getServiceName()
            );
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
        if( this.isJamonAvailable() )
        {
            PrintWriter printWriter = null;
            String report = null;

            try
            {
                if( this.getLogger().isDebugEnabled() )
                {
                    this.getLogger().debug( "Writing JAMOM report to " + reportFile.getAbsolutePath() );
                }

                FileOutputStream fos = new FileOutputStream( reportFile );
                printWriter = new PrintWriter( fos );
                report = this.createReport();
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
                    printWriter = null;
                }
            }
        }
    }

    /**
     * Creates a JAMON monitor for the category name.
     *
     * @param category the category name
     * @return the monitor
     */
    private Object createMonitor(String category)
    {
		Object result = null;
		String methodName = "start";
		Class[] signature = { String.class };
		Object[] args = { category };
		
		// invoke MonitorFactory.start(String);

        try
        {
            result = Clazz.invoke( this.monitorFactoryClass, methodName, signature, args );
        }
        catch (Exception e)
        {
            String msg = "Invoking com.jamonapi.MonitorFactory.start() failed";
            this.getLogger().error( msg, e );
        }

        return result;
    }

    /**
     * Stop the JAMON monitor.
     *
     * @param monitor the monitor to be stopped
     */
    private void stopMonitor( Object monitor )
    {
        String methodName = "stop";
        Class[] signature = {};
        Object[] args = {};

        // invoke MonitorFactory.start(String);

        try
        {
            Clazz.invoke(monitor, methodName, signature, args);
        }
        catch (Throwable t)
        {
            String msg = "Invoking com.jamonapi.MonitorFactory.start() failed";
            this.getLogger().error(msg,t);
        }
    }

    /**
     * Create a JAMON report.
     *
     * @return the report
     */
    private String createReport()
    {
        String result = null;
        Object rootMonitor = null;
        Class[] signature = {};
        Object[] args = {};

        try
        {
            // invoke MonitorFactory.getRootMonitor().getReport()

            rootMonitor = Clazz.invoke(this.monitorFactoryClass, "getRootMonitor", signature, args);
            result = (String) Clazz.invoke(rootMonitor, "getReport", signature, args);
        }
        catch (Throwable t)
        {
            String msg = "Invoking com.jamonapi.MonitorFactory.getRootMonitor().getReport()() failed";
            this.getLogger().error(msg,t);
            result = "<" + t + ">";
        }

        return result;
    }
}
