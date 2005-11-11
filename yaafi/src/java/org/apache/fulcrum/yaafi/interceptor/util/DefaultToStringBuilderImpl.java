/*
 * Copyright 2002-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.fulcrum.yaafi.interceptor.util;

/**
 * Creates a string representation of java.lang.reflect.Method
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */
public class DefaultToStringBuilderImpl implements InterceptorToStringBuilder
{
    /** the target object */
    private Object target;

    /** the output for a NULL value **/
    private static final String NULL_STRING = "<null>";

    /**
     * Constructor
     */
    public DefaultToStringBuilderImpl()
    {
        // nothing to do
    }

    /**
     * Constructor
     *
     * @param target the object to print
     */
    public DefaultToStringBuilderImpl(Object target)
    {
        this.target = target;
    }

    /**
     * @see org.apache.fulcrum.yaafi.interceptor.util.InterceptorToStringBuilder#setTarget(java.lang.Object)
     */
    public void setTarget(Object target)
    {
        this.target = target;
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
        // not supported
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        String result = null;

        try
        {
            if( this.target == null )
            {
                result = NULL_STRING;
            }
            else
            {
                result = this.target.toString();
            }
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            result = "<" + t + ">";
        }

        return result;
    }
}
