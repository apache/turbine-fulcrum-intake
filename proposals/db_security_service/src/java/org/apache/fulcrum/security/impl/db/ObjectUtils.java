package org.apache.fulcrum.security.impl.db;


/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.IOException;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Tools pulled together to load and store the permanent storage
 * in the User class. Stolen from various parts of Turbine 2 and
 * Torque. :-)
 * 
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */


public class ObjectUtils
{
    /**
     * Nice method for adding data to a Hashtable in such a way
     * as to not get NPE's. The point being that if the
     * value is null, Hashtable.put() will throw an exception.
     * That blows in the case of this class cause you may want to
     * essentially treat put("Not Null", null) == put("Not Null", "")
     * We will still throw a NPE if the key is null cause that should
     * never happen.
     *
     * Maybe a hashtable isn't the best option here and we
     * should use a Map.
     *
     * @param hash The hashtable to use
     * @param key  The key to use
     * @param value The value to store in the hashtable
     *
     * @throws NullPointerException The supplied key was null
     *
     */
    public static final void safeAddToHashtable(Hashtable hash, Object key, Object value)
        throws NullPointerException
    {
        if (value == null)
        {
            hash.put (key, "");
        }
        else
        {
            hash.put (key, value);
        }
    }

    /**
     * Converts a hashtable to a byte array for storage/serialization.
     *
     * @param hash The Hashtable to convert.
     *
     * @return A byte[] with the converted Hashtable.
     *
     * @exception Exception A generic exception.
     */
    public static byte[] serializeHashtable(Hashtable hash)
        throws Exception
    {
        Hashtable saveData = new Hashtable(hash.size());
        String key = null;
        Object value = null;
        byte[] byteArray = null;

        Enumeration keys = hash.keys();

        while (keys.hasMoreElements())
        {
            key = (String) keys.nextElement();
            value = hash.get(key);
            if (value instanceof Serializable)
            {
                saveData.put (key, value);
            }
        }

        ByteArrayOutputStream baos = null;
        BufferedOutputStream bos = null;
        ObjectOutputStream out = null;
        try
        {
            // These objects are closed in the finally.
            baos = new ByteArrayOutputStream();
            bos  = new BufferedOutputStream(baos);
            out  = new ObjectOutputStream(bos);

            out.writeObject(saveData);
            out.flush();
            bos.flush();

            byteArray = baos.toByteArray();
        }
        finally
        {
            if (out != null) 
            {
                out.close();
            }
            if (bos != null)
            {
                bos.close();
            }
            
            if (baos != null)
            {
                baos.close();
            }
        }
        return byteArray;
    }


    /**
     * Deserializes a single object from an array of bytes.
     *
     * @param objectData The serialized object.
     *
     * @return The deserialized object, or <code>null</code> on failure.
     */

    public static Object deserialize(byte[] objectData)
    {
        Object object = null;

        if (objectData != null)            
        {
            // These streams are closed in finally.
            ByteArrayInputStream bais = new ByteArrayInputStream(objectData);
            BufferedInputStream  bis  = new BufferedInputStream(bais);
            ObjectInputStream    in   = null;

            try                
            {
                in = new ObjectInputStream(bis);

                // If objectData has not been initialized, an
                // exception will occur.
                object = in.readObject();
            }
            catch (Exception e)
            {
            }
            finally                
            {
                try 
                {
                    if (in != null) 
                    {
                        in.close();
                    }
                    if (bis != null)
                    {
                        bis.close();
                    }
                    if (bais != null) 
                    {
                        bais.close();
                    }
                } 
                catch (IOException e)
                {
                }
            }
        }
        return object;
    }

}

