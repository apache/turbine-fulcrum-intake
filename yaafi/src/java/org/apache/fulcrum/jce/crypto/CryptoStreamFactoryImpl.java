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
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

/**
 * Concrete factory for creating encrypting/decrypting streams. The
 * implementation uses the JCE (Java Crypto Extension) either supplied
 * by SUN (using SunJCE 1.42) or an custom provider such as BouncyCastle
 * or the newer Cryptix libraries.
 *
 * The implementation uses as PBEWithMD5AndTripleDES for encryption which
 * should be sufficent for most applications.
 *
 * The implementation also supplies a default password in the case that
 * the programmer don't want to have additional hassles. It is easy to
 * reengineer the password being used but much better than a hard-coded
 * password in the application.
 *
 * The code uses parts from Markus Hahn's Blowfish library found at
 * http://blowfishj.sourceforge.net/
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl </a>
 * @author <a href="mailto:maakus@earthlink.net">Markus Hahn</a>
 */

public final class CryptoStreamFactoryImpl implements CryptoStreamFactory
{
    /** the salt for the PBE algorithm */
    private byte[] salt;

    /** the count paramter for the PBE algorithm */
    private int count;

    /** the name of the JCE provider */
    private String providerName;

    /** the algorithm to use */
    private String algorithm;

    /** the default instance */
    private static CryptoStreamFactory instance;

    /**
     * Factory method to get a default instance
     * @return an instance of the CryptoStreamFactory
     */
    public static CryptoStreamFactory getInstance()
    {
        if( CryptoStreamFactoryImpl.instance == null )
        {
            synchronized( CryptoStreamFactory.class )
            {
                if( CryptoStreamFactoryImpl.instance == null )
                {
                    CryptoStreamFactoryImpl.instance = new CryptoStreamFactoryImpl();
                }
            }
        }

        return instance;
    }

    /**
     * Set the default instance from an external application.
     * @param instance the new default instance
     */
    public static void setInstance( CryptoStreamFactory instance )
    {
        CryptoStreamFactoryImpl.instance = instance;
    }

    /**
     * Constructor
     */
    public CryptoStreamFactoryImpl()
    {
        this.salt = CryptoParameters.SALT;
        this.count = CryptoParameters.COUNT;
        this.providerName = CryptoParameters.PROVIDERNAME;
        this.algorithm = CryptoParameters.ALGORITHM;
    }

    /**
     * Constructor
     *
     * @param salt the salt for the PBE algorithm
     * @param count the iteration for PBEParameterSpec
     * @param algorithm the algorithm to be used
     * @param providerName the name of the JCE provide to b used
     */
    public CryptoStreamFactoryImpl(
        byte[] salt,
        int count,
        String algorithm,
        String providerName )
    {
        this.salt = salt;
        this.count = count;
        this.algorithm = algorithm;
        this.providerName = providerName;
    }

    /**
     * @see org.apache.fulcrum.jce.crypto.CryptoStreamFactory#getInputStream(java.io.InputStream)
     */
    public InputStream getInputStream( InputStream is )
        throws GeneralSecurityException, IOException
    {
        Cipher cipher = this.createCipher( Cipher.DECRYPT_MODE, PasswordFactory.create() );
        CipherInputStream cis = new CipherInputStream( is, cipher );
        return cis;
    }

    /**
     * @see org.apache.fulcrum.jce.crypto.CryptoStreamFactory#getInputStream(java.io.InputStream,char[])
     */
    public InputStream getInputStream( InputStream is, char[] password )
        throws GeneralSecurityException, IOException
    {
        Cipher cipher = this.createCipher( Cipher.DECRYPT_MODE, password );
        CipherInputStream cis = new CipherInputStream( is, cipher );
        return cis;
    }

    /**
     * @see org.apache.fulcrum.jce.crypto.CryptoStreamFactory#getSmartInputStream(java.io.InputStream)
     */
    public InputStream getSmartInputStream(InputStream is)
        throws GeneralSecurityException, IOException
    {
        return this.getSmartInputStream(
            is,
            PasswordFactory.create()
            );
    }

    /**
     * @see org.apache.fulcrum.jce.crypto.CryptoStreamFactory#getSmartInputStream(java.io.InputStream,char[])
     */
    public InputStream getSmartInputStream(InputStream is, char[] password )
        throws GeneralSecurityException, IOException
    {
        SmartDecryptingInputStream result = null;

        result = new SmartDecryptingInputStream(
            getInstance(),
            is,
            password
            );

        return result;
    }

    /**
     * @see org.apache.fulcrum.jce.crypto.CryptoStreamFactory#getOutputStream(java.io.OutputStream, char[])
     */
    public OutputStream getOutputStream( OutputStream os, char[] password )
        throws GeneralSecurityException, IOException
    {
        Cipher cipher = this.createCipher( Cipher.ENCRYPT_MODE, password );
        CipherOutputStream cos = new CipherOutputStream( os, cipher );
        return cos;
    }

    /**
     * @return Returns the algorithm.
     */
    private final String getAlgorithm()
    {
        return algorithm;
    }

    /**
     * @return Returns the count.
     */
    private final int getCount()
    {
        return count;
    }

    /**
     * @return Returns the providerName.
     */
    private final String getProviderName()
    {
        return providerName;
    }

    /**
     * @return Returns the salt.
     */
    private final byte [] getSalt()
    {
        return salt;
    }

    /**
     * Create a PBE key.
     *
     * @param password the password to use.
     * @return the key
     * @throws GeneralSecurityException creating the key failed
     */
    private final Key createKey( char[] password )
        throws GeneralSecurityException
    {
        SecretKeyFactory keyFactory = null;
        String algorithm = this.getAlgorithm();
        PBEKeySpec keySpec =  new PBEKeySpec(password);

        if( this.getProviderName() == null )
        {
            keyFactory = SecretKeyFactory.getInstance( algorithm );
        }
        else
        {
            keyFactory = SecretKeyFactory.getInstance( algorithm, this.getProviderName() );
        }

        Key key = keyFactory.generateSecret(keySpec);
        return key;
    }

    /**
     * Create a Cipher.
     *
     * @param mode the cipher mode
     * @param password the password
     * @return an instance of a cipher
     * @throws GeneralSecurityException creating a cipher failed
     * @throws IOException creating a cipher failed
     */
    private final Cipher createCipher( int mode, char[] password )
        throws GeneralSecurityException, IOException
    {
        Cipher cipher = null;
        PBEParameterSpec paramSpec = new PBEParameterSpec( this.getSalt(), this.getCount() );
        Key key = this.createKey( password );

        if( this.getProviderName() == null )
        {
            cipher = Cipher.getInstance( this.getAlgorithm() );
        }
        else
        {
            cipher = Cipher.getInstance( this.getAlgorithm(), this.getProviderName() );
        }

        cipher.init( mode, key, paramSpec );
        return cipher;
    }
}