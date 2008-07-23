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
 * files. 
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
     * Create a decrypting input stream using the default password.
     *
     * @param is the input stream to be decrypted
     * @param isEncrypted the encryption mode (true|false|auto)
     * @return an decrypting input stream
     * @throws Exception reading the input stream failed
     */
    public static InputStream getDecryptingInputStream( InputStream is, String isEncrypted )
        throws Exception
    {
        InputStream result;
        
        if( isEncrypted.equalsIgnoreCase("true") )
        {
            result = createDecryptingInputStream(is, "getInputStream");
        }
        else if( isEncrypted.equalsIgnoreCase("auto") )
        {
            result = createDecryptingInputStream(is, "getSmartInputStream");
        }
        else if( isEncrypted.equalsIgnoreCase("false") )
        {
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
     */
    private static InputStream createDecryptingInputStream( InputStream is, String methodName )
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
            return (InputStream) Clazz.invoke(cryptoStreamFactory, methodName, signature, args);
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
                Class clazz = Clazz.getClazz(clazzLoader, className);
                cryptoStreamFactory = Clazz.newInstance(clazz, signature, args);
            }
        }
                    
        return cryptoStreamFactory;
    }
}
