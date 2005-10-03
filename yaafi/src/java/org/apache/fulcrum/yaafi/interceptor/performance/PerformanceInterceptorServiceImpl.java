package org.apache.fulcrum.yaafi.interceptor.performance;

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

import java.lang.reflect.Method;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.Reconfigurable;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.fulcrum.yaafi.framework.interceptor.AvalonInterceptorContext;
import org.apache.fulcrum.yaafi.interceptor.baseservice.BaseInterceptorServiceImpl;
import org.apache.fulcrum.yaafi.interceptor.util.MethodToStringBuilder;
import org.apache.fulcrum.yaafi.interceptor.util.StopWatch;

/**
 * A service logging the execution time of service invocations.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public class PerformanceInterceptorServiceImpl
    extends BaseInterceptorServiceImpl
    implements PerformanceInterceptorService, Reconfigurable, Contextualizable, ThreadSafe
{
    /** default length of the StringBuffer */
    private static final int BUFFER_LENGTH = 1000;

    /** seperator for the arguments in the logfile */
    private static final String SEPERATOR = ";";

    /** the tresholds in milliseconds to determine the loglevel */
    private int[] tresholdList;
    
    /////////////////////////////////////////////////////////////////////////
    // Avalon Service Lifecycle Implementation
    /////////////////////////////////////////////////////////////////////////

    /**
     * Constructor
     */
    public PerformanceInterceptorServiceImpl()
    {
        super();
        this.tresholdList = new int[5];
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration configuration) throws ConfigurationException
    {
        super.configure(configuration);
        
        Configuration tresholdConfiguration = configuration.getChild("tresholds");
        this.tresholdList[0] = tresholdConfiguration.getChild("fatal").getAttributeAsInteger("millis", 5000);
        this.tresholdList[1] = tresholdConfiguration.getChild("error").getAttributeAsInteger("millis", 1000);
        this.tresholdList[2] = tresholdConfiguration.getChild("warn").getAttributeAsInteger("millis", 500);
        this.tresholdList[3] = tresholdConfiguration.getChild("info").getAttributeAsInteger("millis", 100);
        this.tresholdList[4] = tresholdConfiguration.getChild("debug").getAttributeAsInteger("millis", 10);
    }

    /**
     * @see org.apache.avalon.framework.configuration.Reconfigurable#reconfigure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void reconfigure(Configuration configuration) throws ConfigurationException
    {
        super.reconfigure(configuration);
        this.configure(configuration);
    }

    /////////////////////////////////////////////////////////////////////////
    // Service interface implementation
    /////////////////////////////////////////////////////////////////////////

    /**
     * @see org.apache.fulcrum.yaafi.framework.proxy.AvalonInterceptorService#onEntry(java.lang.String, java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
     */
    public void onEntry(AvalonInterceptorContext interceptorContext)
    {
        if( this.isServiceMonitored(interceptorContext ) )
        {
            this.createStopWatch(interceptorContext);
        }
    }

    /**
     * @see org.apache.fulcrum.yaafi.framework.proxy.AvalonInterceptorService#onError(java.lang.String, java.lang.Object, java.lang.reflect.Method, java.lang.Object[], java.lang.Throwable)
     */
    public void onError(AvalonInterceptorContext interceptorContext,Throwable t)
    {
        if( this.isServiceMonitored(interceptorContext) )
        {
            StopWatch stopWatch = this.getStopWatch(interceptorContext);
            stopWatch.stop();
            this.log( ON_ERROR, interceptorContext, stopWatch );
        }
    }

    /**
     * @see org.apache.fulcrum.yaafi.framework.proxy.AvalonInterceptorService#onExit(java.lang.String, java.lang.Object, java.lang.reflect.Method, java.lang.Object[], java.lang.Object)
     */
    public void onExit(AvalonInterceptorContext interceptorContext, Object result)
    {
        if( this.isServiceMonitored(interceptorContext) )
        {
            if( this.isServiceMonitored(interceptorContext) )
            {
                StopWatch stopWatch = this.getStopWatch(interceptorContext);
                stopWatch.stop();
                this.log( ON_EXIT, interceptorContext, stopWatch );
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////
    // Service Implementation
    /////////////////////////////////////////////////////////////////////////

    /**
     * Creates a stop watch
     * 
     * @param interceptorContext the current interceptor context
     */
    protected void createStopWatch(
        AvalonInterceptorContext interceptorContext )
    {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        interceptorContext.getRequestContext().put(this.getServiceName(),stopWatch);        
    }

    /**
     * Gets the stop watch
     * 
     * @param interceptorContext the current interceptor context
     * @return the stop watch
     */
    protected StopWatch getStopWatch( 
        AvalonInterceptorContext interceptorContext )
    {
        return (StopWatch) interceptorContext.getRequestContext().remove(
            this.getServiceName()
            );
    }
        
    /**
     * Logs the execution time.
     * 
     * @param onError invoked from onError or not
     * @param interceptorContext the current interceptor context
     * @param stopWatch the stop watch
     */
    protected void log(
        int mode,
        AvalonInterceptorContext interceptorContext, 
        StopWatch stopWatch
        )
    {
        String msg = null;
        long time = stopWatch.getTime();
        
        if( time >= tresholdList[0] )
        {
            if( this.getLogger().isFatalErrorEnabled() )
            {
	            msg = this.toString(mode,interceptorContext,stopWatch);
	            this.getLogger().fatalError(msg);
            }
        }
        else if( time >= tresholdList[1] )
        {
            if( this.getLogger().isErrorEnabled() )
            {
	            msg = this.toString(mode,interceptorContext,stopWatch);
	            this.getLogger().error(msg);
            }
        }
        else if( time >= tresholdList[2] )
        {
            if( this.getLogger().isWarnEnabled() )
            {
	            msg = this.toString(mode,interceptorContext,stopWatch);
	            this.getLogger().warn(msg);
            }
        }
        else if( time >= tresholdList[3] )
        {
            if( this.getLogger().isInfoEnabled() )
            {
	            msg = this.toString(mode,interceptorContext,stopWatch);
	            this.getLogger().info(msg);
            }
        }
        else if( time >= tresholdList[4] )
        {
            if( this.getLogger().isDebugEnabled() )
            {
	            msg = this.toString(mode,interceptorContext,stopWatch);
	            this.getLogger().debug(msg);
            }            
        }
        else
        {
            // nothing to do
        }
    }
    
    /**
     * Create the log message for the performance logfile.
     * 
     * @param interceptorContext the context
     * @param stopWatch the stopwatch
     * @return the log message
     */
    protected String toString(
        int mode,
        AvalonInterceptorContext interceptorContext, 
        StopWatch stopWatch 
        )
    {
        Method method = interceptorContext.getMethod();
        MethodToStringBuilder methodToStringBuilder = new MethodToStringBuilder(method);
        StringBuffer result = new StringBuffer(BUFFER_LENGTH);

        result.append(interceptorContext.getTransactionId());
        result.append(SEPERATOR);
        result.append(interceptorContext.getInvocationId());
        result.append(SEPERATOR);
        result.append(interceptorContext.getInvocationDepth());
        result.append(SEPERATOR);
        result.append(mode);
        result.append(SEPERATOR);        
        result.append(interceptorContext.getServiceShorthand());
        result.append(SEPERATOR);
        result.append(method.getName());
        result.append(SEPERATOR);
        result.append(stopWatch.getTime());        
        result.append(SEPERATOR);
        result.append(methodToStringBuilder.toString());

        return result.toString();
    }
}
