package org.apache.fulcrum.jce.crypto;

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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;

/**
 * Interface for creating encrypting/decrypting streams.
 * 
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl </a>
 * @version $Id$
 */

public interface CryptoStreamFactory
{
    /** 
     * Creates an decrypting input stream
     * 
     * @param is the input stream to be wrapped
     * @return an decrypting input stream
     */
    InputStream getInputStream(InputStream is)
    	throws GeneralSecurityException, IOException;

    /** 
     * Creates an decrypting input stream
     * 
     * @param is the input stream to be wrapped
     * @param password the password to be used
     * @return an decrypting input stream
     */
    InputStream getInputStream(InputStream is, char[] password)
    	throws GeneralSecurityException, IOException;

    /** 
     * Creates an encrypting output stream
     * 
     * @param os the output stream to be wrapped
     * @param password the password to be used
     * @return an decrypting input stream
     */
    OutputStream getOutputStream(OutputStream os, char[] password) 
    	throws GeneralSecurityException, IOException;
}