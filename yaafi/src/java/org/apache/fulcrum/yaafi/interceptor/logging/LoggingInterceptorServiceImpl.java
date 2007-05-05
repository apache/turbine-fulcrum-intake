package org.apache.fulcrum.yaafi.interceptor.logging;

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

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.Reconfigurable;
import org.apache.fulcrum.yaafi.framework.interceptor.AvalonInterceptorContext;
import org.apache.fulcrum.yaafi.framework.reflection.Clazz;
import org.apache.fulcrum.yaafi.interceptor.baseservice.BaseInterceptorServiceImpl;
import org.apache.fulcrum.yaafi.interceptor.util.DefaultToStringBuilderImpl;
import org.apache.fulcrum.yaafi.interceptor.util.InterceptorToStringBuilder;
import org.apache.fulcrum.yaafi.interceptor.util.MethodToStringBuilderImpl;
import org.apache.fulcrum.yaafi.interceptor.util.ArgumentToStringBuilderImpl;
import org.apache.fulcrum.yaafi.interceptor.util.StopWatch;

/**
 * A service logging of service invocations. The service allows to monitor
 * a list of services defined in the configuration.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public class LoggingInterceptorServiceImpl
    extends BaseInterceptorServiceImpl
    implements LoggingInterceptorService, Reconfigurable, Initializable
{
    /** the maximum length of a dumped argument */
    private static final int MAX_ARG_LENGTH = 2000;

    /** seperator for the arguments in the logfile */
    private static final String SEPERATOR = ";";

    /** maximum argument length for dumping arguments */
    private int maxArgLength;

    /** the class name of the string builder to use */
    private String toStringBuilderClassName;

    /** monitor all excpetions independent from the monitored services */
    private boolean monitorAllExceptions;

    /** the ReflectionToStringBuilder class */
    private Class toStringBuilderClass;

    /////////////////////////////////////////////////////////////////////////
    // Avalon Service Lifecycle Implementation
    /////////////////////////////////////////////////////////////////////////

    /**
     * Constructor
     */
    public LoggingInterceptorServiceImpl()
    {
        super();
        this.maxArgLength = MAX_ARG_LENGTH;
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration configuration) throws ConfigurationException
    {
        super.configure(configuration);

        this.maxArgLength = configuration.getChild("maxArgLength").getValueAsInteger(MAX_ARG_LENGTH);
        this.toStringBuilderClassName = configuration.getChild("toStringBuilderClass").getValue(ArgumentToStringBuilderImpl.class.getName());
        this.monitorAllExceptions = configuration.getChild("monitorAllExceptions").getValueAsBoolean(true);
    }

    /**
     * @see org.apache.avalon.framework.activity.Initializable#initialize()
     */
    public void initialize() throws Exception
    {
        // load the string builder class

        ClassLoader classLoader = this.getClass().getClassLoader();

        if( Clazz.hasClazz(classLoader, this.getToStringBuilderClassName()) )
        {
            this.toStringBuilderClass = Clazz.getClazz(
                classLoader,
                this.getToStringBuilderClassName()
                );
        }

        // create an instance of the StringBuilder to see if everything works

        InterceptorToStringBuilder interceptorToStringBuilder = this.createArgumentToStringBuilder(
            this
            );

        interceptorToStringBuilder.toString();
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
     * @see org.apache.fulcrum.yaafi.framework.interceptor.AvalonInterceptorService#onEntry(org.apache.fulcrum.yaafi.framework.interceptor.AvalonInterceptorContext)
     */
    public void onEntry(AvalonInterceptorContext interceptorContext)
    {
        if( this.isServiceMonitored(interceptorContext ) )
        {
            if( this.getLogger().isInfoEnabled() )
            {
                String msg = this.toString(interceptorContext,null,ON_ENTRY);
                this.getLogger().info(msg);
                this.createStopWatch(interceptorContext);
            }
        }
    }

    /**
     * @see org.apache.fulcrum.yaafi.framework.interceptor.AvalonInterceptorService#onError(org.apache.fulcrum.yaafi.framework.interceptor.AvalonInterceptorContext, java.lang.Throwable)
     */
    public void onError(AvalonInterceptorContext interceptorContext,Throwable t)
    {
        if( this.getLogger().isErrorEnabled() )
        {
	        if( this.isMonitorAllExceptions() || this.isServiceMonitored(interceptorContext) )
	        {
	            StopWatch stopWatch = this.getStopWatch(interceptorContext);
	            stopWatch.stop();
	            String msg = this.toString(interceptorContext, stopWatch, t);
	            this.getLogger().error(msg);
	        }
        }
    }

    /**
     * @see org.apache.fulcrum.yaafi.framework.interceptor.AvalonInterceptorService#onExit(org.apache.fulcrum.yaafi.framework.interceptor.AvalonInterceptorContext, java.lang.Object)
     */
    public void onExit(AvalonInterceptorContext interceptorContext, Object result)
    {
        if( this.isServiceMonitored(interceptorContext) )
        {
            if( this.getLogger().isDebugEnabled() )
            {
                StopWatch stopWatch = this.getStopWatch(interceptorContext);
                stopWatch.stop();
                String msg = this.toString(interceptorContext, stopWatch, result);
                this.getLogger().debug(msg);
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
     * Gets the stop watch. Even if none is defined we return one
     * in a proper state.
     *
     * @param interceptorContext the current interceptor context
     * @return the stop watch
     */
    protected StopWatch getStopWatch(
        AvalonInterceptorContext interceptorContext )
    {
        StopWatch result = (StopWatch) interceptorContext.getRequestContext().remove(
            this.getServiceName()
            );

        if( result == null )
        {
            result = new StopWatch();
            result.start();
        }

        return result;
    }

    /**
     * @return Returns the maxLineLength.
     */
    protected int getMaxArgLength()
    {
        return maxArgLength;
    }

    /**
     * @return Returns the monitorAllExceptions.
     */
    protected boolean isMonitorAllExceptions()
    {
        return monitorAllExceptions;
    }

    /**
     * @return Returns the toStringBuilderClass.
     */
    protected Class getToStringBuilderClass()
    {
        return toStringBuilderClass;
    }

    /**
     * @return Returns the toStringBuilderClassName.
     */
    protected String getToStringBuilderClassName()
    {
        return toStringBuilderClassName;
    }

    /**
     * Create an instance of an InterceptorToStringBuilder
     *
     * @param target the object to stringify
     * @return the string builder
     */
    protected InterceptorToStringBuilder createArgumentToStringBuilder(Object target)
    {
        InterceptorToStringBuilder result = null;

        try
        {
            result = (InterceptorToStringBuilder)
            	this.getToStringBuilderClass().newInstance();
        }
        catch (Exception e)
        {
            String msg = "Unable to create an instance for " + this.getToStringBuilderClassName();
            this.getLogger().error(msg,e);
            result = new DefaultToStringBuilderImpl();
        }

        result.setTarget(target);
        result.setMaxArgLength(this.getMaxArgLength());
        result.setMode(1);

        return result;
    }

    /**
     * Create a string representation of a service invocation returning a result.
     *
     * @param avalonInterceptorContext the interceptor context
     * @param stopWatch the stopwatch for the execution time
     * @param result the result of the service invocation
     * @return the string representation of the result
     */
    protected String toString(
        AvalonInterceptorContext avalonInterceptorContext,
        StopWatch stopWatch,
        Object result )
    {
        StringBuffer methodSignature = new StringBuffer();
        InterceptorToStringBuilder toStringBuilder = this.createArgumentToStringBuilder(result);

        methodSignature.append( this.toString(avalonInterceptorContext, stopWatch, ON_EXIT) );
        methodSignature.append(SEPERATOR);
        methodSignature.append( "result={" );
        methodSignature.append( toStringBuilder.toString() );
        methodSignature.append( "}" );

        return methodSignature.toString();
    }

    /**
     * Create a string representation of a service invocation throwing a Throwable
     *
     * @param avalonInterceptorContext the interceptor context
     * @param stopWatch the stopwatch for the execution time
     * @param throwable the result of the service invocation
     * @return the string representation of the result
     */
    protected String toString(
        AvalonInterceptorContext avalonInterceptorContext,
        StopWatch stopWatch,
        Throwable throwable )
    {
        StringBuffer methodSignature = new StringBuffer();
        InterceptorToStringBuilder toStringBuilder = this.createArgumentToStringBuilder(throwable);

        methodSignature.append( this.toString(avalonInterceptorContext, stopWatch, ON_ERROR) );
        methodSignature.append(SEPERATOR);
        methodSignature.append( throwable.getClass().getName() );
        methodSignature.append(SEPERATOR);
        methodSignature.append( toStringBuilder.toString() );

        return methodSignature.toString();
    }

    /**
     * Create a method signature.
     *
     * @param interceptorContext the avalonInterceptorContext
     * @param stopWatch the stopwatch for the execution time
     * @param mode the mode (onEntry, onExit, onError)
     * @return the debug output
     */
    protected String toString(
        AvalonInterceptorContext interceptorContext, StopWatch stopWatch, int mode )
    {
        StringBuffer result = new StringBuffer();
        Method method = interceptorContext.getMethod();
        Object[] args = interceptorContext.getArgs();
        InterceptorToStringBuilder toStringBuilder = null;
        MethodToStringBuilderImpl methodToStringBuilder = new MethodToStringBuilderImpl(method);

        if( args == null )
        {
            args = new Object[0];
        }

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

        if( stopWatch != null )
        {
            result.append(stopWatch.getTime());
        }
        else
        {
            result.append('0');
        }

        result.append(SEPERATOR);
        result.append(methodToStringBuilder.toString());

        if( (ON_ENTRY == mode) || (ON_ERROR == mode) )
        {
	        for( int i=0; i<args.length; i++ )
	        {
	            toStringBuilder = this.createArgumentToStringBuilder(args[i]);
	            result.append(SEPERATOR);
	            result.append("arg[" + i + "]:={");
	            result.append( toStringBuilder.toString());
	            result.append("}");
	        }
        }

        return result.toString();
    }
}
