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

package org.apache.fulcrum.yaafi.interceptor.util;

import java.lang.reflect.Method;

/**
 * Creates a string representation of java.lang.reflect.Method
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */
public class MethodToStringBuilderImpl implements InterceptorToStringBuilder
{
    /** include the method return type */
    public static final int INCLUDE_RETURNTYPE = 0x1;

    /** the default mode using class names and hashcode */
    private static int defaultMode = 0x01;

    /** our current formatting mode */
    private int mode;

    /** initial size for the StringBuilder */
    private static final int BUF_SIZE = 512;

    /** the method we are dumping */
    private Method method;

    /**
     * Constructor
     */
    public MethodToStringBuilderImpl()
    {
        this.mode = MethodToStringBuilderImpl.defaultMode;
    }

    /**
     * Constructor
     *
     * @param method the method to print
     */
    public MethodToStringBuilderImpl(Method method)
    {
        this.method = method;
        this.mode = MethodToStringBuilderImpl.defaultMode;
    }

    /**
     * Constructor
     *
     * @param method the method to print
     * @param mode the formatting mode
     */
    public MethodToStringBuilderImpl(Method method, int mode)
    {
        this.method = method;
        this.mode = mode;
    }

    /**
     * @see org.apache.fulcrum.yaafi.interceptor.util.InterceptorToStringBuilder#setMaxArgLength(int)
     */
    public void setMaxArgLength(int maxArgLength)
    {
            // not supported
    }

    /**
     * @see org.apache.fulcrum.yaafi.interceptor.util.InterceptorToStringBuilder#setMode(int)
     */
    public void setMode(int mode)
    {
            this.mode = mode;
    }

    /**
     * @see org.apache.fulcrum.yaafi.interceptor.util.InterceptorToStringBuilder#setTarget(java.lang.Object)
     */
    public void setTarget(Object target)
    {
            this.method = (Method) target;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        try
        {
            StringBuilder buffer = new StringBuilder(BUF_SIZE);

            Class returnType = method.getReturnType();
            Class declaringClass = method.getDeclaringClass();
            Class[] params = method.getParameterTypes();

            // print return type

            if ((this.mode & INCLUDE_RETURNTYPE) == 1)
            {
                buffer.append( returnType.getSimpleName() );
                buffer.append( ' ');
            }

            // print class and method

            buffer.append( declaringClass.getSimpleName() ) ;
            buffer.append( '.');
            buffer.append( method.getName() );
            buffer.append( '(');

            // print the argument list of the method

            for (int i = 0; i < params.length; i++)
            {
                buffer.append( params[i].getSimpleName() );
                if (i < (params.length - 1))
                {
                    buffer.append(",");
                }
            }

            buffer.append(")");

            return buffer.toString();
        }
        catch (Throwable t)
        {
            return "<" + t.getClass().getSimpleName() + ">";
        }
    }
}
