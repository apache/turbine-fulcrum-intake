package org.apache.fulcrum.crypto;

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


import org.apache.avalon.merlin.unit.AbstractMerlinTestCase;
import org.apache.avalon.composition.util.ExceptionHelper;
import junit.framework.TestSuite;

/**
 * Basic testing of the Container
 *
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class CryptoServiceTest extends AbstractMerlinTestCase
{
    private CryptoService sc = null;
    private static final String preDefinedInput = "Oeltanks";

    /**
     * Constructor for test.
     *
     * @param testName name of the test being executed
     */
    public CryptoServiceTest(String testName)
    {
        super(testName);
    }

    /**
     * Factory method for creating a TestSuite for this class.
     *
     * @return the test suite
     */
    public static TestSuite suite()
    {
        TestSuite suite = new TestSuite(CryptoServiceTest.class);
        return suite;
    }

    public void setUp() throws Exception
    {
        super.setUp();
        try
        {
            sc = (CryptoService) this.resolve( "crypto" );
        }
        catch ( Exception e)
        {
            final String error = 
              ExceptionHelper.packException( e.getMessage(), e, true );
            getLogger().error( error );
            fail( error );
        }
    }

    public void testUnixCrypt()
    {
        String preDefinedSeed = "z5";
        String preDefinedResult = "z5EQaXpuu059c";
        try
        {
            CryptoAlgorithm ca = sc.getCryptoAlgorithm("unix");
            /*
           	* Test predefined Seed
           	*/
            ca.setSeed(preDefinedSeed);
            String output = ca.encrypt(preDefinedInput);
            assertEquals("Encryption failed ", preDefinedResult, output);
            /*
           	* Test random Seed
           	*
           	*/
            ca.setSeed(null);
            String result = ca.encrypt(preDefinedInput);
            ca.setSeed(result);
            output = ca.encrypt(preDefinedInput);
            assertEquals("Encryption failed ", output, result);
            getLogger().info( "ok" );
        }
        catch (Exception e)
        {
            final String error = 
              ExceptionHelper.packException( e.getMessage(), e, true );
            getLogger().error( error );
            fail();
        }
    }

    public void testClearCrypt()
    {
        String preDefinedResult = "Oeltanks";
        try
        {
            CryptoAlgorithm ca = sc.getCryptoAlgorithm("clear");
            String output = ca.encrypt(preDefinedInput);
            assertEquals("Encryption failed ", preDefinedResult, output);
            getLogger().info( "ok" );
        }
        catch (Exception e)
        {
            final String error = 
              ExceptionHelper.packException( e.getMessage(), e, true );
            getLogger().error( error );
            fail();
        }
    }

    public void testOldJavaCryptMd5()
    {
        String preDefinedResult = "XSop0mncK19Ii2r2CUe2";
        try
        {
            CryptoAlgorithm ca = sc.getCryptoAlgorithm("oldjava");
            ca.setCipher("MD5");
            String output = ca.encrypt(preDefinedInput);
            assertEquals("MD5 Encryption failed ", preDefinedResult, output);
            getLogger().info( "ok" );
        }
        catch (Exception e)
        {
            final String error = 
              ExceptionHelper.packException( e.getMessage(), e, true );
            getLogger().error( error );
            fail();
        }
    }
    public void testOldJavaCryptSha1()
    {
        String preDefinedResult = "uVDiJHaavRYX8oWt5ctkaa7j";
        try
        {
            CryptoAlgorithm ca = sc.getCryptoAlgorithm("oldjava");
            ca.setCipher("SHA1");
            String output = ca.encrypt(preDefinedInput);
            assertEquals("SHA1 Encryption failed ", preDefinedResult, output);
            getLogger().info( "ok" );
        }
        catch (Exception e)
        {
            final String error = 
              ExceptionHelper.packException( e.getMessage(), e, true );
            getLogger().error( error );
            fail();
        }
    }
    public void testJavaCryptMd5()
    {
        String preDefinedResult = "XSop0mncK19Ii2r2CUe29w==";
        try
        {
            CryptoAlgorithm ca = sc.getCryptoAlgorithm("java");
            ca.setCipher("MD5");
            String output = ca.encrypt(preDefinedInput);
            assertEquals("MD5 Encryption failed ", preDefinedResult, output);
            getLogger().info( "ok" );
        }
        catch (Exception e)
        {
            final String error = 
              ExceptionHelper.packException( e.getMessage(), e, true );
            getLogger().error( error );
            fail();
        }
    }

    public void testJavaCryptSha1()
    {
        String preDefinedResult = "uVDiJHaavRYX8oWt5ctkaa7j1cw=";
        try
        {
            CryptoAlgorithm ca = sc.getCryptoAlgorithm("java");
            ca.setCipher("SHA1");
            String output = ca.encrypt(preDefinedInput);
            assertEquals("SHA1 Encryption failed ", preDefinedResult, output);
            getLogger().info( "ok" );
        }
        catch (Exception e)
        {
            final String error = 
              ExceptionHelper.packException( e.getMessage(), e, true );
            getLogger().error( error );
            fail();
        }
    }
}
