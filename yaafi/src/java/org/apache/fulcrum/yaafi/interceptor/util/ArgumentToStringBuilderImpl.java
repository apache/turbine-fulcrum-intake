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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Iterator;

import org.apache.fulcrum.yaafi.framework.util.StringUtils;

/**
 * Creates a string representation of method argument.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */
public class ArgumentToStringBuilderImpl implements InterceptorToStringBuilder
{
    /** include the class name in the result */
    public static final int INCLUDE_CLASSNAME = 0x1;

    /** include the hashcode in the result */
    public static final int INCLUDE_HASHCODE = 0x02;

    /** the default mode using class names and hashcode */
    private static int defaultMode = INCLUDE_CLASSNAME & INCLUDE_HASHCODE;

    /** our current formatting mode */
    private int mode;

    /** the maximum length of a dumped argument */
    private static final int MAX_LINE_LENGTH = 2000;

    /** seperator for the arguments in the logfile */
    private static final char SEPERATOR = ';';

    /** the output for a NULL value **/
    private static final String NULL_STRING = "<null>";

    /** the output for a length string **/
    private static final String LENGTH_STRING = "length=";

    /** the output for a value string **/
    private static final String VALUE_STRING = "value=";

    /** maximum line length for dumping arguments */
    private int maxArgLength;

    /** the result of the invocation */
    private StringBuilder buffer;

    /** the target object */
    private Object target;

    /**
     * Constructor
     */
    public ArgumentToStringBuilderImpl()
    {
        this.mode = ArgumentToStringBuilderImpl.defaultMode;
        this.maxArgLength = MAX_LINE_LENGTH;
        this.buffer = new StringBuilder();
    }

    /**
     * Constructor
     *
     * @param target the object to print
     */
    public ArgumentToStringBuilderImpl(Object target)
    {
        this(target,MAX_LINE_LENGTH);
    }

    /**
     * Constructor
     *
     * @param target the object to print
     * @param maxArgLength the maximum length
     */
    public ArgumentToStringBuilderImpl(Object target, int maxArgLength)
    {
        this(target,
            maxArgLength,
            ArgumentToStringBuilderImpl.defaultMode
            );
    }

    /**
     * Constructor
     *
     * @param target the object to print
     * @param maxArgLength the maximum length
     * @param mode the formatting mode to use
     */
    public ArgumentToStringBuilderImpl(Object target, int maxArgLength, int mode)
    {
        this.buffer = new StringBuilder();
        this.target = target;
        this.maxArgLength = maxArgLength;
        this.mode = mode;
    }

    /**
     * @see org.apache.fulcrum.yaafi.interceptor.util.InterceptorToStringBuilder#setMaxArgLength(int)
     */
    public void setMaxArgLength(int maxArgLength)
    {
        this.maxArgLength = maxArgLength;
    }

    /**
     * @see org.apache.fulcrum.yaafi.interceptor.util.InterceptorToStringBuilder#setTarget(java.lang.Object)
     */
    public void setTarget(Object target)
    {
        this.target = target;
    }

    /**
     * @see org.apache.fulcrum.yaafi.interceptor.util.InterceptorToStringBuilder#setMode(int)
     */
    public void setMode(int mode)
    {
        this.mode = mode;
    }

    /**
     * @return Returns the mode.
     */
    public int getMode()
    {
        return this.mode;
    }

        /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        try
        {
            if( this.target == null )
            {
                this.buffer.append(NULL_STRING);
            }
            else if( this.target instanceof Object[] )
            {
                this.appendClassName(target);
                this.appendHashCode(target);
                this.appendChar('[');
                this.append( this.toString((Object[]) this.target) );
                this.appendChar(']');
            }
            else if( this.target instanceof boolean[] )
            {
                this.appendClassName(target);
                this.appendHashCode(target);
                this.appendChar('[');
                this.append( this.toString((boolean[]) this.target) );
                this.appendChar(']');
            }
            else if( this.target instanceof char[] )
            {
                this.appendClassName(target);
                this.appendHashCode(target);
                this.appendChar('[');
                this.append( this.toString((char[]) this.target) );
                this.appendChar(']');
            }
            else if( this.target instanceof byte[] )
            {
                this.appendClassName(target);
                this.appendHashCode(target);
                this.appendChar('[');
                this.append( this.toString((byte[]) this.target) );
                this.appendChar(']');
            }
            else if( this.target instanceof short[] )
            {
                this.appendClassName(target);
                this.appendHashCode(target);
                this.appendChar('[');
                this.append( this.toString((short[]) this.target) );
                this.appendChar(']');
            }
            else if( this.target instanceof int[] )
            {
                this.appendClassName(target);
                this.appendHashCode(target);
                this.appendChar('[');
                this.append( this.toString((int[]) this.target) );
                this.appendChar(']');
            }
            else if( this.target instanceof long[] )
            {
                this.appendClassName(target);
                this.appendHashCode(target);
                this.appendChar('[');
                this.append( this.toString((long[]) this.target) );
                this.appendChar(']');
            }
            else if( this.target instanceof float[] )
            {
                this.appendClassName(target);
                this.appendHashCode(target);
                this.appendChar('[');
                this.append( this.toString((float[]) this.target) );
                this.appendChar(']');
            }
            else if( this.target instanceof double[] )
            {
                this.appendClassName(target);
                this.appendHashCode(target);
                this.appendChar('[');
                this.append( this.toString((double[]) this.target) );
                this.appendChar(']');
            }
            else if( this.target instanceof String )
            {
                this.appendClassName(target);
                this.appendHashCode(target);
                this.appendChar('[');
                this.append( this.toString((String) this.target) );
                this.appendChar(']');
            }
            else if( this.target instanceof Collection )
            {
                this.appendClassName(target);
                this.appendHashCode(target);
                this.appendChar('[');
                this.append( this.toString((Collection) this.target) );
                this.appendChar(']');
            }
            else if( this.target instanceof Dictionary )
            {
                this.appendClassName(target);
                this.appendHashCode(target);
                this.appendChar('[');
                this.append( this.toString((Dictionary) this.target) );
                this.appendChar(']');
            }
            else if( this.target instanceof Throwable )
            {
                this.append( this.toString((Throwable) this.target) );
            }
            else
            {
                this.append( this.toString( (Object) this.target ) );
            }
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            return "<" + t + ">";
        }

        return this.buffer.toString();
    }


    /**
     * Create a String representation for a Throwable.
     *
     * @param throwable the Throwable
     * @return the string representation
     */
    protected String toString(Throwable throwable)
    {
        String result = null;

        if( throwable == null )
        {
            result = NULL_STRING;
        }
        else
        {
            result = this.getStackTrace(throwable);
        }

        return result;
    }

    /**
     * Create a string representation of an object array.
     *
     * @param array the array to print
     * @return the result
     */
    protected String toString(Object[] array)
    {
        StringBuilder temp = new StringBuilder();
        ArgumentToStringBuilderImpl toStringBuilder = null;

        if( array == null )
        {
            return NULL_STRING;
        }
        else
        {
            temp.append(LENGTH_STRING);
            temp.append(array.length);
            temp.append(',');

            for( int i=0; i<array.length; i++ )
            {
                temp.append('[');
                temp.append(i);
                temp.append(']');
                temp.append('=');
                toStringBuilder = new ArgumentToStringBuilderImpl(array[i],this.getMaxArgLength(),this.getMode());
                temp.append(toStringBuilder.toString());

                if( i<array.length-1)
                {
                    temp.append(',');
                }

                if( temp.length() > this.getMaxArgLength() )
                {
                    break;
                }
            }
        }

        return temp.toString();
    }

    /**
     * Create a string representation of a boolean[].
     *
     * @param array the array to print
     * @return the result
     */
    protected String toString(boolean[] array)
    {
        StringBuilder temp = new StringBuilder();

        if( array == null )
        {
            return NULL_STRING;
        }
        else
        {
            temp.append(LENGTH_STRING);
            temp.append(array.length);
            temp.append(',');
            temp.append(VALUE_STRING);

            for( int i=0; i<array.length; i++ )
            {
                temp.append(array[i]);
                if( i<array.length-1)
                {
                    temp.append(',');
                }

                if( temp.length() > this.getMaxArgLength() )
                {
                    break;
                }
            }
        }

        return temp.toString();
    }

    /**
     * Create a string representation of a char[].
     *
     * @param array the array to print
     * @return the result
     */
    protected String toString(char[] array)
    {
        StringBuilder temp = new StringBuilder();

        if( array == null )
        {
            return NULL_STRING;
        }
        else
        {
            temp.append(LENGTH_STRING);
            temp.append(array.length);
            temp.append(',');
            temp.append(VALUE_STRING);

            for( int i=0; i<array.length; i++ )
            {
                temp.append(array[i]);
                if( i<array.length-1)
                {
                    temp.append('.');
                }

                if( temp.length() > this.getMaxArgLength() )
                {
                    break;
                }
            }
        }

        return temp.toString();
    }

    /**
     * Create a string representation of a short[].
     *
     * @param array the array to print
     * @return the result
     */
    protected String toString(short[] array)
    {
        StringBuilder temp = new StringBuilder();

        if( array == null )
        {
            return NULL_STRING;
        }
        else
        {
            temp.append(LENGTH_STRING);
            temp.append(array.length);
            temp.append(',');
            temp.append(VALUE_STRING);

            for( int i=0; i<array.length; i++ )
            {
                temp.append(array[i]);
                if( i<array.length-1)
                {
                    temp.append(',');
                }

                if( temp.length() > this.getMaxArgLength() )
                {
                    break;
                }
            }
        }

        return temp.toString();
    }

    /**
     * Create a string representation of a int[].
     *
     * @param array the array to print
     * @return the result
     */
    protected String toString(int[] array)
    {
        StringBuilder temp = new StringBuilder();

        if( array == null )
        {
            return NULL_STRING;
        }
        else
        {
            temp.append(LENGTH_STRING);
            temp.append(array.length);
            temp.append(',');
            temp.append(VALUE_STRING);

            for( int i=0; i<array.length; i++ )
            {
                temp.append(array[i]);
                if( i<array.length-1)
                {
                    temp.append(',');
                }

                if( temp.length() > this.getMaxArgLength() )
                {
                    break;
                }
            }
        }

        return temp.toString();
    }

    /**
     * Create a string representation of a char[].
     *
     * @param array the array to print
     * @return the result
     */
    protected String toString(long[] array)
    {
        StringBuilder temp = new StringBuilder();

        if( array == null )
        {
            return NULL_STRING;
        }
        else
        {
            temp.append(LENGTH_STRING);
            temp.append(array.length);
            temp.append(',');
            temp.append(VALUE_STRING);

            for( int i=0; i<array.length; i++ )
            {
                temp.append(array[i]);
                if( i<array.length-1)
                {
                    temp.append(',');
                }

                if( temp.length() > this.getMaxArgLength() )
                {
                    break;
                }
            }
        }

        return temp.toString();
    }

    /**
     * Create a string representation of a float[].
     *
     * @param array the array to print
     * @return the result
     */
    protected String toString(float[] array)
    {
        StringBuilder temp = new StringBuilder();

        if( array == null )
        {
            return NULL_STRING;
        }
        else
        {
            temp.append(LENGTH_STRING);
            temp.append(array.length);
            temp.append(',');
            temp.append(VALUE_STRING);

            for( int i=0; i<array.length; i++ )
            {
                temp.append(array[i]);
                if( i<array.length-1)
                {
                    temp.append(',');
                }

                if( temp.length() > this.getMaxArgLength() )
                {
                    break;
                }
            }
        }

        return temp.toString();
    }

    /**
     * Create a string representation of a double[].
     *
     * @param array the array to print
     * @return the result
     */
    protected String toString(double[] array)
    {
        StringBuilder temp = new StringBuilder();

        if( array == null )
        {
            return NULL_STRING;
        }
        else
        {
            temp.append(LENGTH_STRING);
            temp.append(array.length);
            temp.append(',');
            temp.append(VALUE_STRING);

            for( int i=0; i<array.length; i++ )
            {
                temp.append(array[i]);
                if( i<array.length-1)
                {
                    temp.append(',');
                }

                if( temp.length() > this.getMaxArgLength() )
                {
                    break;
                }
            }
        }

        return temp.toString();
    }

    /**
     * Create a string representation of a String.
     *
     * @param string the string to print
     */
    protected String toString(String string)
    {
        StringBuilder temp = new StringBuilder();

        if( string == null )
        {
            return NULL_STRING;
        }
        else
        {
            temp.append(LENGTH_STRING);
            temp.append(string.length());
            temp.append(',');
            temp.append(VALUE_STRING);
            temp.append(string);
        }

        return temp.toString();
    }

    /**
     * Create a string representation of a char[].
     *
     * @param array the array to print
     * @return the result
     */
    protected String toString(byte[] array)
    {
        StringBuilder temp = new StringBuilder();

        if( array == null )
        {
            temp.append(NULL_STRING);
        }
        else
        {
            temp.append(LENGTH_STRING);
            temp.append(array.length);
        }

        return temp.toString();
    }

    /**
     * Create a string representation of a java.util.Collection.
     *
     * @param collection the collection to print
     * @return the result
     */
    protected String toString(Collection collection)
    {
        int index = 0;
        StringBuilder temp = new StringBuilder();
        ArgumentToStringBuilderImpl toStringBuilder = null;

        if( collection == null )
        {
          return NULL_STRING;
        }
        else
        {
            temp.append(LENGTH_STRING);
            temp.append(collection.size());
            temp.append(',');

            Iterator iterator = collection.iterator();

            while (iterator.hasNext())
            {
                temp.append('[');
                temp.append(index++);
                temp.append(']');
                temp.append('=');

                toStringBuilder = new ArgumentToStringBuilderImpl(
                    iterator.next(),
                    this.getMaxArgLength(),
                    this.getMode()
                    );

                temp.append(toStringBuilder.toString());

                if( index<collection.size()-1)
                {
                    temp.append(',');
                }

                if( temp.length() > this.getMaxArgLength() )
                {
                    break;
                }
            }
        }

        return temp.toString();
    }

    /**
     * Create a string representation of a Dictionary.
     *
     * @param dictionary the collection to print
     * @return the result
     */
    protected String toString(Dictionary dictionary)
    {
        StringBuilder temp = new StringBuilder();

        if( dictionary == null )
        {
            return NULL_STRING;
        }
        else
        {
            temp.append(LENGTH_STRING);
            temp.append(dictionary.size());
            temp.append(',');
            temp.append(VALUE_STRING);
            temp.append(dictionary.toString());
        }

        return temp.toString();
    }

    /**
     * Create a String representation for an arbitrary object.
     *
     * @param object the object
     * @return string representation
     */
    protected String toString(Object object)
    {
        String result = null;
        String temp = null;
        String className = null;

        if( object == null )
        {
            result = NULL_STRING;
        }
        else
        {
            temp = object.toString();

            className = StringUtils.replace(
                object.getClass().getName(),
                "java.lang.", ""
                );

            if( temp.startsWith(className) == false )
            {
                int hashCode = object.hashCode();
                StringBuilder tempBuffer = new StringBuilder();
                tempBuffer.append(className);
                tempBuffer.append('@');
                tempBuffer.append(hashCode);
                tempBuffer.append('[');
                tempBuffer.append(temp);
                tempBuffer.append(']');

                result = tempBuffer.toString();
            }
            else
            {
                result = temp;
            }
        }

        return result;
    }

    /**
     * Append the hash code.
     * @param target the object to print
     */
    protected void appendHashCode(Object target)
    {
            if ((this.mode & INCLUDE_HASHCODE) == 0)
            {
                    return;
            }

        if( this.target != null )
        {
            this.buffer.append('@');
            this.buffer.append(Integer.toHexString(target.hashCode()));
        }
    }

    /**
     * Append the class name.
     * @param target the object to print
     */
    protected void appendClassName(Object target)
    {
        boolean skipClassName = true;

        if ((this.mode & INCLUDE_CLASSNAME) == 0)
        {
            return;
        }

        if( this.target != null )
        {
            String className = target.getClass().getName();

            if( target instanceof boolean[] )
            {
                this.buffer.append("boolean[]");
            }
            else if( target instanceof byte[] )
            {
                this.buffer.append("byte[]");
            }
            else if( target instanceof char[] )
            {
                this.buffer.append("char[]");
            }
            else if( target instanceof short[] )
            {
                this.buffer.append("short[]");
            }
            else if( target instanceof int[] )
            {
                this.buffer.append("int[]");
            }
            else if( target instanceof long[] )
            {
                this.buffer.append("[ong[]");
            }
            else if( target instanceof float[] )
            {
                this.buffer.append("float[]");
            }
            else if( target instanceof double[] )
            {
                this.buffer.append("double[]");
            }
            else if( target instanceof Boolean )
            {
                this.buffer.append("Boolean");
            }
            else if( target instanceof Character )
            {
                this.buffer.append("Character");
            }
            else if( target instanceof Short )
            {
                this.buffer.append("Short");
            }
            else if( target instanceof Integer )
            {
                this.buffer.append("Integer");
            }
            else if( target instanceof Long )
            {
                this.buffer.append("Long");
            }
            else if( target instanceof Float )
            {
                this.buffer.append("Float");
            }
            else if( target instanceof Double )
            {
                this.buffer.append("Double");
            }
            else if( target instanceof String )
            {
                this.buffer.append("String");
            }
            else if( target instanceof Boolean[] )
            {
                this.buffer.append("Boolean[]");
            }
            else if( target instanceof Character[] )
            {
                this.buffer.append("Character[]");
            }
            else if( target instanceof Short[] )
            {
                this.buffer.append("Short[]");
            }
            else if( target instanceof Integer[] )
            {
                this.buffer.append("Integer[]");
            }
            else if( target instanceof Long[] )
            {
                this.buffer.append("Long[]");
            }
            else if( target instanceof Float[] )
            {
                this.buffer.append("Float[]");
            }
            else if( target instanceof Double[] )
            {
                this.buffer.append("Double[]");
            }
            else if( target instanceof String[] )
            {
                this.buffer.append("String[]");
            }
            else
            {
                skipClassName = false;
            }

            if( skipClassName == false )
            {
                className = StringUtils.replace(className, "java.lang.", "");

                if( className.endsWith(";") )
                {
                    this.buffer.append(className.substring(0,className.length()-1));
                }
                else
                {
                    this.buffer.append(className);
                }
            }
        }
    }

    /**
     * Append the hash code.
     * @param ch the object to print
     */
    protected void appendChar(char ch)
    {
        this.buffer.append(ch);
    }

    /**
     * @return Returns the maxLineLength.
     */
    protected int getMaxArgLength()
    {
        return maxArgLength;
    }

    /**
     * <p>Gets the stack trace from a Throwable as a String.</p>
     *
     * @param throwable  the <code>Throwable</code> to be examined
     * @return the stack trace as generated by the exception's
     *  <code>printStackTrace(PrintWriter)</code> method
     */
    protected String getStackTrace(Throwable throwable)
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter( sw, true );
        throwable.printStackTrace( pw );
        return sw.getBuffer().toString();
    }

    /**
     * Append a string to the internal buffer
     * @param source the string to append
     */
    protected void append(String source)
    {
        String formattedSource = this.format(source);
        this.buffer.append(formattedSource);
    }

    /**
     * Format the buffer by replacing the whitespaces and cutting
     * away excessive fluff.
     *
     * @param source the source string
     */
    protected String format( String source )
    {
        boolean isTruncated = false;
        StringBuilder stringBuilder = new StringBuilder(source);

        // trim the string to avoid dumping tons of data

        if( stringBuilder.length() > this.getMaxArgLength() )
        {
            stringBuilder.delete(this.getMaxArgLength()-1, stringBuilder.length());
            isTruncated = true;
        }

        // remove the line breaks and tabs for logging output and replace

        for( int i=0; i<stringBuilder.length(); i++ )
        {
            if( ( stringBuilder.charAt(i) == '\r' ) ||
                ( stringBuilder.charAt(i) == '\n' ) ||
                ( stringBuilder.charAt(i) == '\t' )  )
            {
                stringBuilder.setCharAt(i,' ');
            }

            if( ( stringBuilder.charAt(i) == SEPERATOR ) )
            {
                stringBuilder.setCharAt(i,' ');
            }
        }

        // show the user that we truncated the ouptut

        if( isTruncated )
        {
            if (source.endsWith("]"))
            {
                stringBuilder.append(" ...]");
            }
            else
            {
                stringBuilder.append(" ...");
            }
        }

        return stringBuilder.toString();
    }
}
