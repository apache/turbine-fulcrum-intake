package org.apache.fulcrum.pbe;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;

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


/**
 * Encapsulates an PBE (Password Based Encryption) functionality
 * from the JCE (Java Crypto Extension).
 *
 * The service provides
 * <ul>
 *   <li>method to create more or less secure passwords</li>
 *   <li>creation of cipher streams for transparent encryption/decryption</li>
 *   <li>generic encryption/decryption methods</li>
 * </ul>
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public interface PBEService
{
    /**
     * Create a password based on the pre-defined seed.
     *
     * @return the password
     * @throws Exception the password creation failed
     */
    char[] createPassword()
        throws Exception;

    /**
     * Create a password based on the supplied seed.
     *
     * @param seed the seed value for the password generation
     * @return the password
     * @throws Exception the password creation failed
     */
    char[] createPassword( char[] seed )
        throws Exception;

    /**
     * Creates a decrypting input stream.
     *
     * @param is the input stream to be wrapped
     * @param password the password to be used
     * @return an decrypting input stream
     * @throws GeneralSecurityException accessing the JCE failed
     * @throws IOException an IOException occured during processing
     */
    InputStream getInputStream( InputStream is, char[] password )
        throws GeneralSecurityException, IOException;

    /**
     * Creates a smart decrypting input stream.
     *
     * @param is the input stream to be wrapped
     * @param password the password to be used
     * @return an decrypting input stream
     * @throws GeneralSecurityException accessing the JCE failed
     * @throws IOException an IOException occured during processing
     */
    InputStream getSmartInputStream( InputStream is, char[] password )
        throws GeneralSecurityException, IOException;

    /**
     * Creates an encrypting output stream.
     *
     * @param os the output stream to be wrapped
     * @param password the password to be used
     * @return an decrypting input stream
     * @throws GeneralSecurityException accessing the JCE failed
     * @throws IOException an IOException occured during processing
     */
    OutputStream getOutputStream( OutputStream os, char[] password )
        throws GeneralSecurityException, IOException;

    /**
     * Copies from a source to a target object using encryption.
     *
     * For he souce object the following data types are supported
     * <ul>
     *  <li>String</li>
     *  <li>File</li>
     *  <li>byte[]</li>
     *  <li>char[]</li>
     *  <li>ByteArrayOutputStream</li>
     *  <li>InputStream</li>
     * </ul>
     *
     * For target object the following data types are supported
     *
     * <ul>
     *  <li>File</li>
     *  <li>OutputStream</li>
     * </ul>
     *
     * @param source the source object
     * @param target the target object
     * @param password the password to use for encryption
     * @throws GeneralSecurityException accessing the JCE failed
     * @throws IOException an IOException occured during processing
     */
    public void encrypt( Object source, Object target, char[] password )
        throws GeneralSecurityException, IOException;

    /**
     * Copies from a source to a target object using decrpytion.
     *
     * For he souce object the following data types are supported
     * <ul>
     *  <li>String</li>
     *  <li>File</li>
     *  <li>byte[]</li>
     *  <li>char[]</li>
     *  <li>ByteArrayOutputStream</li>
     *  <li>InputStream</li>
     * </ul>
     *
     * For target object the following data types are supported
     *
     * <ul>
     *  <li>File</li>
     *  <li>OutputStream</li>
     * </ul>
     *
     * @param source the source object
     * @param target the target object
     * @param password the password to use for decryption
     * @throws GeneralSecurityException accessing the JCE failed
     * @throws IOException an IOException occured during processing
     */
    public void decrypt( Object source, Object target, char[] password )
        throws GeneralSecurityException, IOException;

    /**
     * Encrypts a string into a hex string.
     *
     * @param plainText the plain text to be encrypted
     * @param password the password for encryption
     * @return the encrypted string
     * @throws GeneralSecurityException accessing the JCE failed
     * @throws IOException an IOException occured during processing
     */
    String encryptString( String plainText, char[] password )
        throws GeneralSecurityException, IOException;

    /**
     * Decrypts an encrypted string into the plain text. The encrypted
     * string must be a hex string created by encryptString.
     *
     * @param cipherText the encrypted text to be decrypted
     * @param password the password for decryption
     * @return the decrypted string
     * @throws GeneralSecurityException accessing the JCE failed
     * @throws IOException an IOException occured during processing
     */
    String decryptString( String cipherText, char[] password )
        throws GeneralSecurityException, IOException;
}
