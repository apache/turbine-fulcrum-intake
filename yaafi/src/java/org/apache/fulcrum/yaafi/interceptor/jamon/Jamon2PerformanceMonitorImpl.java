package org.apache.fulcrum.yaafi.interceptor.jamon;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import com.jamonapi.RangeHolder;
import org.apache.fulcrum.yaafi.interceptor.util.MethodToStringBuilderImpl;

import java.lang.reflect.Method;

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
    /** the default label being used */
    private static final String MONITOR_LABEL = "ms.services";

    /** is monitoring enabled */
    private boolean isActive;

    /** the method currenty monitored */
    private Method method;

    /** the global JAMON monitor */
    private Monitor monitor;

    /** the time the monitoring was started */
    private long startTime;

    static
    {
        // configure the unit/ranges only once
        MonitorFactory.setRangeDefault(MONITOR_LABEL, createMSHolder());
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
