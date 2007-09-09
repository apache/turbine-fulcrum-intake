package org.apache.fulcrum.yaafi.interceptor.jamon;

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
