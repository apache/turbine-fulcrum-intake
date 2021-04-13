package org.apache.fulcrum.pbe;

import java.io.ByteArrayOutputStream;

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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.fulcrum.jce.crypto.CryptoParameters;
import org.apache.fulcrum.jce.crypto.CryptoStreamFactory;
import org.apache.fulcrum.jce.crypto.CryptoStreamFactoryImpl;
import org.apache.fulcrum.jce.crypto.CryptoUtil;
import org.apache.fulcrum.jce.crypto.HexConverter;
import org.apache.fulcrum.jce.crypto.PasswordFactory;
import org.apache.fulcrum.jce.crypto.PasswordParameters;
import org.apache.fulcrum.jce.crypto.StreamUtil;

/**
 * Encapsulates an PBE (Password Based Encryption) functionality
 * from the JCE (Java Crypto Extension).
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public class PBEServiceImpl
    extends AbstractLogEnabled
    implements PBEService, Configurable
{
    /** the internally used factory to create cipher streams */
    private CryptoStreamFactory cryptoStreamFactory;

    /** the salt for generating the password */
    private byte[] passwordSalt;

    /** the invocations of MessageDigest */
    private int passwordCount;

    /** the default password */
    private char[] defaultPassword;
    
    private String algorithm = "";

    /**
     * Constructor
     */
    public PBEServiceImpl()
    {
        // nothing to do
    }

    /////////////////////////////////////////////////////////////////////////
    // Avalon Service Lifecycle Implementation
    /////////////////////////////////////////////////////////////////////////

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration configuration)
        throws ConfigurationException
    {
        // read the parameters for CryptoStreamFactory

        byte[] cryptoSalt = CryptoParameters.Salt();
        int cryptoCount = configuration.getChild("cyrptoCount").getValueAsInteger(CryptoParameters.COUNT);
        String tempCryptoSalt = configuration.getChild("cryptoSalt").getValue("");
        
        this.algorithm = configuration.getChild("algo").getValue("SHA1");

        if( tempCryptoSalt.length() > 0 )
        {
            cryptoSalt = HexConverter.toBytes( tempCryptoSalt );
        }

        // create the CryptoStreamFactory to be used

        this.cryptoStreamFactory = new CryptoStreamFactoryImpl(
            cryptoSalt,
            cryptoCount
            );

        // read the parameters for PasswordFactory

        this.passwordSalt = PasswordParameters.Salt();
        this.passwordCount = configuration.getChild("passwordCount").getValueAsInteger(PasswordParameters.COUNT);
        this.defaultPassword = PasswordParameters.DefaultPassword();
    }


    /////////////////////////////////////////////////////////////////////////
    // PBE Service Implementation
    /////////////////////////////////////////////////////////////////////////

    /**
     * @see org.apache.fulcrum.pbe.PBEService#createPassword()
     */
    public char[] createPassword() throws Exception
    {
        //"SHA-256", default "SHA1"
        return PasswordFactory.getInstance(algorithm).create(
            this.defaultPassword,
            this.passwordSalt,
            this.passwordCount
            );
    }

    /**
     * @see org.apache.fulcrum.pbe.PBEService#createPassword(char[])
     */
    public char [] createPassword(char [] seed) throws Exception
    {
        return PasswordFactory.getInstance(algorithm).create(
            seed,
            this.passwordSalt,
            this.passwordCount
            );
    }

    /**
     * @see org.apache.fulcrum.pbe.PBEService#decryptString(java.lang.String, char[])
     */
    public String decryptString(String cipherText, char [] password)
        throws GeneralSecurityException, IOException
    {
        return CryptoUtil.getInstance().decryptString(
            this.getCryptoStreamFactory(),
            cipherText,
            password
            );
    }

    /**
     * @see org.apache.fulcrum.pbe.PBEService#encryptString(java.lang.String, char[])
     */
    public String encryptString(String plainText, char [] password)
        throws GeneralSecurityException, IOException
    {
        return CryptoUtil.getInstance().encryptString(
            this.getCryptoStreamFactory(),
            plainText,
            password,
            false
            );
    }

    /**
     * @see org.apache.fulcrum.pbe.PBEService#getInputStream(java.io.InputStream, char[])
     */
    public InputStream getInputStream(InputStream is, char [] password)
        throws GeneralSecurityException, IOException
    {
        return this.getCryptoStreamFactory().getInputStream(
            is,
            password
            );
    }

    /**
     * @see org.apache.fulcrum.pbe.PBEService#getSmartInputStream(java.io.InputStream, char[])
     */
    public InputStream getSmartInputStream(InputStream is, char [] password)
        throws GeneralSecurityException, IOException
    {
        return this.getCryptoStreamFactory().getSmartInputStream(
            is,
            password
            );
    }

    /**
     * @see org.apache.fulcrum.pbe.PBEService#getOutputStream(java.io.OutputStream, char[])
     */
    public OutputStream getOutputStream(OutputStream os, char [] password)
        throws GeneralSecurityException, IOException
    {
        return this.getCryptoStreamFactory().getOutputStream(
            os,
            password
            );
    }

    /**
     * @see org.apache.fulcrum.pbe.PBEService#decrypt(java.lang.Object, java.lang.Object, char[])
     */
    public void decrypt(Object source, Object target, char [] password)
        throws GeneralSecurityException, IOException
    {
        InputStream is = StreamUtil.createInputStream(source);
        OutputStream os = StreamUtil.createOutputStream(target);
        InputStream dis = this.getCryptoStreamFactory().getInputStream(is, password);
        StreamUtil.copy(dis, os);               
        // protected
//        CryptoUtil.getInstance().decrypt(
//            this.getCryptoStreamFactory(),
//            source,
//            target,
//            password
//            );
    }

    /**
     * @see org.apache.fulcrum.pbe.PBEService#encrypt(java.lang.Object, java.lang.Object, char[])
     */
    public void encrypt(Object source, Object target, char [] password)
        throws GeneralSecurityException, IOException
    {
        CryptoUtil.getInstance().encrypt(
            this.getCryptoStreamFactory(),
            source,
            target,
            password
            );
    }

    /////////////////////////////////////////////////////////////////////////
    // Service Implementation
    /////////////////////////////////////////////////////////////////////////

    /**
     * @return Returns the cryptoStreamFactory.
     */
    private CryptoStreamFactory getCryptoStreamFactory()
    {
        return cryptoStreamFactory;
    }
}
