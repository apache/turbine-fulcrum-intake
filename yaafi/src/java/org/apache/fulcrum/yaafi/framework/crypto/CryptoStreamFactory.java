package org.apache.fulcrum.yaafi.framework.crypto;

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

import org.apache.fulcrum.yaafi.framework.reflection.Clazz;

import java.io.InputStream;

/**
 * Factory class to get a decrypting input stream for reading configuration
 * files. The implementation uses dynamic class loading to make decryption
 * an optional feature which is highly desirable when avoiding the ECCN
 * export code problems.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl </a>
 */

public class CryptoStreamFactory
{
    /** is the instance already initialized */
    private static boolean isInitialized;

    /** the factory to create encrypting input streams */
    private static Object cryptoStreamFactory;

    /** the name of the class to be loaded */
    private static String className = "org.apache.fulcrum.jce.crypto.CryptoStreamFactoryImpl";

    /**
     * Create a (potentially) decrypting input stream using the default
     * password.
     *
     * @param is the input stream to be decrypted
     * @param isEncrypted the encryption mode (true|false|auto)
     * @return a decrypting input stream
     * @throws Exception reading the input stream failed
     */
    public static InputStream getDecryptingInputStream( InputStream is, String isEncrypted )
        throws Exception
    {
        InputStream result;
        
        if( isEncrypted.equalsIgnoreCase("true") )
        {
            // a decrypting input stream was requested
            result = createDecryptingInputStream(is, "getInputStream");
        }
        else if( isEncrypted.equalsIgnoreCase("auto") && hasCryptoStreamFactory())
        {
            // no user-supplied preferences but crypto stream is available
            result = createDecryptingInputStream(is, "getSmartInputStream");
        }
        else if( isEncrypted.equalsIgnoreCase("auto") && !hasCryptoStreamFactory())
        {
            // no user-supplied perferences so we fall back to normal input stream
            result = is;
        }
        else if( isEncrypted.equalsIgnoreCase("false") )
        {
            // just use normal input stream
            result = is;
        }
        else
        {
            throw new IllegalArgumentException("Unknown decryption mode : " + isEncrypted);
        }

        return result;
    }

    /**
     * Factory method to create a decrypting input stream.
     *
     * @param is the input stream to be decrypted
     * @param factoryMethodName the name of the factory method
     * @return a decrypting input stream
     * @throws Exception creating the decrypting input stream failed
     */
    private static InputStream createDecryptingInputStream( InputStream is, String factoryMethodName )
        throws Exception
    {
        Class[] signature = {InputStream.class};
        Object[] args = {is};
        Object cryptoStreamFactory = getCryptoStreamFactory();

        if(cryptoStreamFactory == null)
        {
            throw new IllegalStateException("No CryptoStreamFactory available - unable to create a decrypting input stream");
        }
        else
        {
            return (InputStream) Clazz.invoke(cryptoStreamFactory, factoryMethodName, signature, args);
        }
    }

    /**
     * Factory method to create a CryptoStreamFactory.
     */
    private synchronized static Object getCryptoStreamFactory()
        throws Exception            
    {
        if(!isInitialized)
        {
            isInitialized = true;
            ClassLoader clazzLoader = CryptoStreamFactory.class.getClassLoader();

            if(Clazz.hasClazz(clazzLoader, className))
            {
                Class[] signature = {};
                Object[] args = {};
                Class<?> clazz = Clazz.getClazz(clazzLoader, className);
                cryptoStreamFactory = Clazz.newInstance(clazz, signature, args);
            }
        }
                    
        return cryptoStreamFactory;
    }

    /**
     * @return true if a CryptoStreamFactory is available
     */
    private static boolean hasCryptoStreamFactory()
        throws Exception
    {
        return ( getCryptoStreamFactory() != null );
    }
}
