package org.apache.fulcrum.yaafi.interceptor.jamon;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import org.apache.fulcrum.yaafi.interceptor.util.MethodToStringBuilderImpl;

import java.lang.reflect.Method;

/**
 * Ecapsulating the JAMon 1.x related API calls
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public class Jamon1PerformanceMonitorImpl implements JamonPerformanceMonitor
{
    /** is monitoring enabled */
    private boolean isActive;

    /** the method currenty monitored */
    private Method method;

    /** the global JAMON monitor */
    private Monitor monitor;

    /**
     * Constructor.
     *
     * @param serviceName the service name of the service being monitored
     * @param method the method to be monitored
     * @param isActive is this an active monitor
     */
    public Jamon1PerformanceMonitorImpl(String serviceName, Method method, Boolean isActive) {
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
            MethodToStringBuilderImpl methodToStringBuilder = new MethodToStringBuilderImpl(this.method, 0);
            String methodSignature = methodToStringBuilder.toString();
            this.monitor = MonitorFactory.start(methodSignature);
        }
    }

    /**
     * Start the monitor.
     */
    public void stop()
    {
        if(this.isActive)
        {
            this.monitor.stop();
        }
    }

    /**
     * Stop the monitor based on an Throwable.
     */
    public void stop(Throwable throwable)
    {
        this.stop();
    }

    /**
     * Create a performance report
     */
    public String createReport() throws Exception
    {
        String result = "";
        try
        {
            result = MonitorFactory.getRootMonitor().getReport();
        }
        catch (Exception e)
        {

        }
        return result;
    }
}
