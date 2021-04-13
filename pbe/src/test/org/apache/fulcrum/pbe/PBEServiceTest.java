package org.apache.fulcrum.pbe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.fulcrum.jce.crypto.StreamUtil;
import org.apache.fulcrum.testcontainer.BaseUnit5Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * PBEServiceTest
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */
public class PBEServiceTest extends BaseUnit5Test
{
    private PBEService service;

    /**
     * Test setup
     */
    @BeforeEach
    protected void setUp() throws Exception
    {

        try
        {
            service = (PBEService) this.lookup(PBEService.class.getName());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    protected PBEService getService()
    {
        return this.service;
    }

    protected char[] getPassword()
    	throws Exception
    {
        return this.getService().createPassword();
    }

    /////////////////////////////////////////////////////////////////////////
    // Start of unit tests
    /////////////////////////////////////////////////////////////////////////

    /**
     * Create the default password do be used.
     * @throws Exception generic exception
     */
    @Test
    public void testCreateDefaultPassword() throws Exception
    {
        char[] result = this.getService().createPassword();
        assertNotNull( result );
        assertTrue( result.length > 0 );
        assertEquals("727a-98b9-93be-4537c", new String(result));
    }

    /**
     * Create a password with a user-supplied seed value.
     * @throws Exception generic exception
     */
    @Test
    public void testCreatePassword() throws Exception
    {
        char[] seed = "mysecret".toCharArray();
        char[] result = this.getService().createPassword(seed);
        assertNotNull( result );
        assertTrue( result.length > 0 );
        assertEquals("62cc-bf14-1814-672da", new String(result));
    }

    /**
     * Test encryption and decryption of Strings
     * @throws Exception generic exception
     */
    @Test
    public void testEncryptDecryptStringUsingDefaultPassword() throws Exception
    {
        String source = "Nobody knows the troubles I have seen ...";
        String cipherText = this.getService().encryptString( source, this.getPassword() );
        String plainText = this.getService().decryptString( cipherText, this.getPassword() );
        assertEquals( source, plainText );
    }

    /**
     * Test encryption and decryption of Strings with
     * a caller-supplied password
     * @throws Exception generic exception
     */
    @Test
    public void testEncryptDecryptStringUsingCustomPassword() throws Exception
    {
        char[] myPassword = this.getService().createPassword("mysecret".toCharArray());
        String source = "Nobody knows the troubles I have seen ...";
        String cipherText = this.getService().encryptString( source, myPassword );
        String plainText = this.getService().decryptString( cipherText, myPassword );
        assertEquals( source, plainText );
    }

    /**
     * Test encryption and decryption of binary data using the default password.
     * @throws Exception generic exception
     */
    @Test
    public void testBinaryEncryptDecrypt() throws Exception
    {
        byte[] result;
        byte[] source = new byte[256];

        for( int i=0; i<source.length; i++ )
        {
            source[i] = (byte) i;
        }

        char[] password = this.getService().createPassword();
        ByteArrayOutputStream cipherText = new ByteArrayOutputStream();
        ByteArrayOutputStream plainText = new ByteArrayOutputStream();

        this.getService().encrypt( source, cipherText, password );
        this.getService().decrypt( cipherText, plainText, password );

        result = plainText.toByteArray();

        for( int i=0; i<source.length; i++ )
        {
            if( source[i] != result[i] )
            {
                fail( "Binary data are different - the test failed" );
            }
        }
    }

    /**
     * Test encryption/decryption based on streams.
     * @throws Exception generic exception
     */
    @Test
    public void testStreamCiphers() throws Exception
    {
        byte[] cipherText;
        String plainText;
        String source = "Nobody knows the troubles I have seen ...";
        char[] password = this.getPassword();

        // encrypt using a CipherOutputStream
        ByteArrayInputStream bais1 = new ByteArrayInputStream( source.getBytes() );
        ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
        OutputStream cos = this.getService().getOutputStream( baos1, password );
        StreamUtil.copy( bais1, cos );
        cipherText = baos1.toByteArray();

        // decrypt using a CipherInputStream
        ByteArrayInputStream bais2 = new ByteArrayInputStream( cipherText );
        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
        InputStream cis = this.getService().getInputStream( bais2, password );
        StreamUtil.copy( cis, baos2 );
        plainText = new String( baos2.toByteArray() );

        // verify the result
        assertEquals( source, plainText );
    }

    /**
     * Test a few of the convinience methods.
     * @throws Exception generic exception
     */
    @Test
    public void testConvinienceEncryption() throws Exception
    {
        String plainText = "Nobody knows the troubles I have seen ...";

        this.getService().encrypt(plainText, new File("./target/temp/plain.enc.txt"), "mysecret".toCharArray());
        this.getService().encrypt(new File("./pom.xml"), new File("./target/temp/pom.enc.xml"), "mysecret".toCharArray());

        this.getService().decrypt(new File("./target/temp/plain.enc.txt"), new File("./target/temp/plain.dec.txt"), "mysecret".toCharArray());
        this.getService().decrypt(new File("./target/temp/pom.enc.xml"), new File("./target/temp/pom.dec.xml"), "mysecret".toCharArray());


    }
}
