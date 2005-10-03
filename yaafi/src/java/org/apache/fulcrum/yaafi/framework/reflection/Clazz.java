package org.apache.fulcrum.yaafi.framework.reflection;

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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Helper clazz to do a little bit of reflection magic.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public class Clazz
{
    /**
     * Determine if the class can be loaded.
     *
     * @param classLoader the classloader to be used
     * @param clazzName the name of the class to be loaded
     * @return true if the class was found
     */
    public static boolean hasClazz( ClassLoader classLoader, String clazzName )
    {
        try
        {
            classLoader.loadClass( clazzName );
            return true;
        }
        catch (ClassNotFoundException e)
        {
            return false;
        }
    }

    /**
     * Loads a class with the given name.
     * @param classLoader the class loader to be used
     * @param clazzName the name of the clazz to be loaded
     * @return the loaded class
     * @throws ClassNotFoundException the class was nout found
     */
    public static Class getClazz( ClassLoader classLoader, String clazzName )
        throws ClassNotFoundException
    {
        return classLoader.loadClass( clazzName );
    }

    /**
     * Creates a new instance of the class
     * @param clazz the class to be instantiated
     * @param signature the signature of the constructor
     * @param args the arguments to be passed
     * @return the newly created instance
     * @throws NoSuchMethodException the method was not found
     * @throws InvocationTargetException an exception was thrown in the constructor
     * @throws InstantiationException the target class could not be instantiated
     * @throws IllegalAccessException an field couldn't be accessed
     */
    public static Object newInstance( Class clazz, Class[] signature, Object[] args )
        throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException
    {
        Object result = null;
        Constructor constructor = clazz.getConstructor( signature );
        result = constructor.newInstance( args );
        return result;
    }

    /**
     * Invokes a given method on the instance.
     * @param instance the instance
     * @param methodName the name of the method to be invoked
     * @param signature the signature of the method
     * @param args the arguments for the method invocation
     * @return the result of the method invocation
     * @throws NoSuchMethodException the method was not found
     * @throws InvocationTargetException an exception was thrown in the constructor
     * @throws IllegalAccessException an field couldn't be accessed
     */
    public static Object invoke( Object instance, String methodName, Class[] signature, Object[] args )
        throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        Object result = null;
        Method method = instance.getClass().getMethod( methodName, signature );
        result = method.invoke( instance, args );
        return result;
    }

    /**
     * Invokes a static method on a class.
     * @param clazz the class instance to work on
     * @param methodName the name of the method to be invoked
     * @param signature the signature of the method
     * @param args the arguments for the method invocation
     * @return the result of the method invocation
     * @throws NoSuchMethodException the method was not found
     * @throws InvocationTargetException an exception was thrown in the constructor
     * @throws IllegalAccessException an field couldn't be accessed
     */

    public static Object invoke( Class clazz, String methodName, Class[] signature, Object[] args )
        throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        Object result = null;
        Method method = clazz.getMethod( methodName, signature );
        result = method.invoke( null, args );
        return result;
    }

    /**
     * <p>Gets a <code>List</code> of all interfaces implemented by the given
     * class and its superclasses.</p>
     *
     * <p>The order is determined by looking through each interface in turn as
     * declared in the source file and following its hierarchy up. Then each
     * superclass is considered in the same way. Later duplicates are ignored,
     * so the order is maintained.</p>
     *
     * @param cls  the class to look up, may be <code>null</code>
     * @return the <code>List</code> of interfaces in order,
     *  <code>null</code> if null input
     */
    public static List getAllInterfaces(Class cls)
    {
        if (cls == null)
        {
            return null;
        }
        List list = new ArrayList();
        while (cls != null)
        {
            Class [] interfaces = cls.getInterfaces();
            for (int i = 0; i < interfaces.length; i++)
            {
                if (list.contains( interfaces[i] ) == false)
                {
                    list.add( interfaces[i] );
                }
                List superInterfaces = getAllInterfaces( interfaces[i] );
                for (Iterator it = superInterfaces.iterator(); it.hasNext();)
                {
                    Class intface = (Class) it.next();
                    if (list.contains( intface ) == false)
                    {
                        list.add( intface );
                    }
                }
            }
            cls = cls.getSuperclass();
        }
        return list;
    }
}