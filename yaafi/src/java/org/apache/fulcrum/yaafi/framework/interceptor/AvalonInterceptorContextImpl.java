package org.apache.fulcrum.yaafi.framework.interceptor;

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
import java.util.HashMap;
import java.util.Map;

import org.apache.fulcrum.yaafi.framework.tls.ThreadLocalStorage;
import org.apache.fulcrum.yaafi.framework.tls.ThreadLocalStorageImpl;
import org.apache.fulcrum.yaafi.framework.util.ToStringBuilder;
import org.apache.fulcrum.yaafi.framework.util.Validate;

/**
 * Contains context information for the interceptors being invoked. The
 * class contains a request context which allows to store data from within an
 * interceptor. It also provides access to a ThreadLocalStorage to associate
 * data with the current thread.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public class AvalonInterceptorContextImpl implements AvalonInterceptorContext
{
    /** key for looking up the transaction id */
    private static final String TRANSACTIONID_KEY =
        "$org.apache.fulcrum.yaafi.framework.interceptor.AvalonInterceptorContext#transactionId";

    /** key for looking up the service invocation depth */
    private static final String INVOCATIONDEPTH_KEY =
        "$org.apache.fulcrum.yaafi.framework.interceptor.AvalonInterceptorContext#invocationDepth";

    /** the name of the service being intercepted */
    private String serviceName;

    /** the shorthand of the service being intercepted */
    private String serviceShorthand;

    /** the real service implementation */
    private Object serviceDelegate;

    /** the method being invoked */
    private Method method;

    /** the arguments for the method invocation */
    private Object[] args;

    /** context information associated with the current invocation */
    private HashMap requestContext;
    
    /** context information associated with the current thread */
    private static ThreadLocalStorageImpl tls = new ThreadLocalStorageImpl();
    
    /** works as invocation counter */
    private static volatile long invocationCounter = 0L;
    
    /** the associated transaction id */
    private Long invocationId;

    /**
     * Constructor.
     *
     * @param serviceName the name of the service being intercepted
     * @param serviceShorthand the shorthand of the service being intercepted
     * @param serviceDelegate the real service implementation
     * @param method the method being invoked
     * @param args the list of arguments for the method invocation
     */
    public AvalonInterceptorContextImpl(
        String serviceName, String serviceShorthand, Object serviceDelegate, Method method, Object[] args )
    {
        Validate.notEmpty(serviceName,"serviceName");
        Validate.notEmpty(serviceShorthand,"serviceShorthand");
        Validate.notNull(serviceDelegate,"serviceDelegate");
        Validate.notNull(method,"method");

        this.invocationId = new Long(++AvalonInterceptorContextImpl.invocationCounter);    
        this.serviceName = serviceName;
        this.serviceShorthand = serviceShorthand;
        this.serviceDelegate = serviceDelegate;
        this.method = method;
        this.args = args;
        this.requestContext = new HashMap();
    }

    /**
     * @return Returns the context for the given request.
     */
    public final Map getRequestContext()
    {
        return requestContext;
    }

    /**
     * @return Returns the serviceDelegate.
     */
    public final Object getServiceDelegate()
    {
        return serviceDelegate;
    }

    /**
     * @return Returns the serviceName.
     */
    public final String getServiceName()
    {
        return serviceName;
    }

    /**
     * @return Returns the serviceShorthand.
     */
    public String getServiceShorthand()
    {
        return serviceShorthand;
    }

    /**
     * @return Returns the args.
     */
    public final Object [] getArgs()
    {
        return args;
    }

    /**
     * @return Returns the method.
     */
    public final Method getMethod()
    {
        return method;
    }

    /**
     * @return Returns the ThreadLocalStorage
     */
    public final ThreadLocalStorage getThreadContext()
    {
        return AvalonInterceptorContextImpl.tls;
    }

    /**
     * @return is a transaction id defined for the current thread
     */
    public boolean hasTransactionId()
    {
        return ( this.getTransactionId() != null ? true : false );
    }
    
    /**
     * @return get the transaction id defined for the current thread
     */
    public Object getTransactionId()
    {
        return this.getThreadContext().get(TRANSACTIONID_KEY);
    }

    /**
     * Set the transaction id for the current thread.
     * @param transactionId the transaction id
     */
    public void setTransactionId( Object transactionId )
    {
        this.getThreadContext().put(TRANSACTIONID_KEY,transactionId);
    }

    /**
     * Clears the transaction id for the current thread.
     */
    public void clearTransactionId()
    {
        this.setTransactionId(null);
    }

    /**
     * Increment the current service invocation depth
     */
    public void incrementInvocationDepth()
    {
        Integer invocationDepth = (Integer) this.getThreadContext().get(INVOCATIONDEPTH_KEY);
        
        if( invocationDepth != null )
        {
            int currInvocationDepth = invocationDepth.intValue();
            this.getThreadContext().put(INVOCATIONDEPTH_KEY, new Integer(++currInvocationDepth));
        }
        else
        {
            this.getThreadContext().put(INVOCATIONDEPTH_KEY, new Integer(0));
        }
    }
    
    /**
     * Decrement the current service invocation depth
     */
    public void decrementInvocationDepth()
    {
        Integer invocationDepth = (Integer) this.getThreadContext().get(INVOCATIONDEPTH_KEY);
        
        if( invocationDepth != null )
        {
            int currInvocationDepth = invocationDepth.intValue();
            this.getThreadContext().put(INVOCATIONDEPTH_KEY, new Integer(--currInvocationDepth));
        }
    }
    
    /**
     * Get the current service invocation depth
     * @return the current service invocation depth
     */
    public int getInvocationDepth()
    {
        Integer invocationDepth = (Integer) this.getThreadContext().get(INVOCATIONDEPTH_KEY);
        
        if( invocationDepth != null )
        {
            return invocationDepth.intValue();
        }
        else
        {
            return 0;
        }
    }
    
    /**
     * @return Returns the invocationId.
     */
    public final Long getInvocationId()
    {
        return invocationId;
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        ToStringBuilder toStringBuilder = new ToStringBuilder(this);
        
        toStringBuilder.append("serviceShorthand",this.serviceShorthand);
        toStringBuilder.append("serviceName",this.serviceName);
        toStringBuilder.append("serviceDelegate",this.serviceDelegate);
        toStringBuilder.append("method",this.method.getName());
        toStringBuilder.append("args",this.args.length);
        toStringBuilder.append("transactionId",this.getTransactionId());
        toStringBuilder.append("invocationId",this.invocationId);
        toStringBuilder.append("invocationDepth",this.getInvocationDepth());
        toStringBuilder.append("requestContext",this.requestContext);
        
        return toStringBuilder.toString();
    }
}