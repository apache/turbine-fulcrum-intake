package org.apache.fulcrum.pbe;

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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.fulcrum.testcontainer.BaseUnitTest;

/**
 * PBEServiceTest
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */
public class PBEServiceTest extends BaseUnitTest
{
    private PBEService service;

    /**
     * Defines the testcase name for JUnit.
     *
     * @param name the testcase's name.
     */
    public PBEServiceTest(String name)
    {
        super(name);
    }

    /**
     * Test setup
     */
    protected void setUp() throws Exception
    {
        super.setUp();

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
    
    /**
     * Pumps the input stream to the output stream.
     *
     * @param is the source input stream
     * @param os the target output stream
     * @throws IOException the copying failed
     */
    public void copy( InputStream is, OutputStream os )
        throws IOException
    {
        byte[] buf = new byte[1024];
        int n = 0;
        int total = 0;

        while ((n = is.read(buf)) > 0)
        {
            os.write(buf, 0, n);
            total += n;
        }

        is.close();
        
        os.flush();
        os.close();
    }    
        
    /////////////////////////////////////////////////////////////////////////
    // Start of unit tests
    /////////////////////////////////////////////////////////////////////////

    /** 
     * Create the default password do be used
     */    
    public void testCreateDefaultPassword() throws Exception
    {
        char[] result = this.getService().createPassword();
        assertNotNull( result );
        assertTrue( result.length > 0 );
    }

    /** 
     * Create a password with a user-supplied seed value.
     */    
    public void testCreatePassword() throws Exception
    {
        char[] seed = "mysecret".toCharArray();
        char[] result = this.getService().createPassword(seed);
        assertNotNull( result );
        assertTrue( result.length > 0 );
    }

    /** 
     * Test encryption and decryption of Strings 
     */
    public void testEncryptDecryptString() throws Exception
    {
        String source = "Nobody knows the toubles I have seen ...";
        String cipherText = this.getService().encryptString( source, this.getPassword() );
        String plainText = this.getService().decryptString( cipherText, this.getPassword() );
        assertEquals( source, plainText );           
    }
    
    /** 
     * Test encryption and decryption of binary data using the default password.
     */    
    public void testBinaryEncryptDecrypt() throws Exception
    {
        byte[] source = new byte[256];
        byte[] result = null;
        
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
    
    public void testStreamCiphers() throws Exception
    {
        String source = "Nobody knows the toubles I have seen ...";
        byte[] cipherText = null;
        String plainText = null; 
        char[] password = this.getPassword();
        
        // encrypt using a CipherOutputStream 
        ByteArrayInputStream bais1 = new ByteArrayInputStream( source.getBytes() );
        ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
        OutputStream cos = this.getService().getOutputStream( baos1, password );
        this.copy( bais1, cos );
        cipherText = baos1.toByteArray();
        
        // decrypt using a CipherinputStream 
        ByteArrayInputStream bais2 = new ByteArrayInputStream( cipherText );
        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
        InputStream cis = this.getService().getInputStream( bais2, password );
        this.copy( cis, baos2 );
        plainText = new String( baos2.toByteArray() );
        
        // verify the result
        assertEquals( source, plainText );
    }
}
