package org.apache.fulcrum.yaafi.interceptor.jamon;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

import java.lang.reflect.Method;

import org.apache.fulcrum.yaafi.interceptor.util.MethodToStringBuilderImpl;

/**
 * Ecapsulating the JAMon 2.x related API calls. JAMon 2.x allows for a much
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
    public Jamon2PerformanceMonitorImpl(String serviceName, Method method, Boolean isActive) {
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
        return MonitorFactory.getRootMonitor().getReport();
    }
}
