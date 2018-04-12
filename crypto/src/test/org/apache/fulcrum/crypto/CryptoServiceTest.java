package org.apache.fulcrum.crypto;

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


import org.apache.fulcrum.testcontainer.BaseUnitTest;

/**
 * Basic testing of the Container
 *
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @version $Id$
 */
public class CryptoServiceTest extends BaseUnitTest
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


    public void setUp() throws Exception
    {
        super.setUp();
        sc = (CryptoService) this.lookup( CryptoService.ROLE );

    }

    public void testUnixCrypt() throws Exception
    {
        String preDefinedSeed = "z5";
        String preDefinedResult = "z5EQaXpuu059c";

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



    }

    public void testClearCrypt() throws Exception
    {
        String preDefinedResult = "Oeltanks";

        CryptoAlgorithm ca = sc.getCryptoAlgorithm("clear");
        String output = ca.encrypt(preDefinedInput);
        assertEquals("Encryption failed ", preDefinedResult, output);

    }

    public void testOldJavaCryptMd5() throws Exception
    {
        String preDefinedResult = "XSop0mncK19Ii2r2CUe2";

        CryptoAlgorithm ca = sc.getCryptoAlgorithm("oldjava");
        ca.setCipher("MD5");
        String output = ca.encrypt(preDefinedInput);
        assertEquals("MD5 Encryption failed ", preDefinedResult, output);

    }
    public void testOldJavaCryptSha1() throws Exception
    {
        String preDefinedResult = "uVDiJHaavRYX8oWt5ctkaa7j";

        CryptoAlgorithm ca = sc.getCryptoAlgorithm("oldjava");
        ca.setCipher("SHA1");
        String output = ca.encrypt(preDefinedInput);
        assertEquals("SHA1 Encryption failed ", preDefinedResult, output);

    }
    public void testJavaCryptMd5() throws Exception
    {
        String preDefinedResult = "XSop0mncK19Ii2r2CUe29w==";
        CryptoAlgorithm ca = sc.getCryptoAlgorithm("java");
        ca.setCipher("MD5");
        String output = ca.encrypt(preDefinedInput);
        assertEquals("MD5 Encryption failed ", preDefinedResult, output);
    }

    public void testJavaCryptSha1() throws Exception
    {
        String preDefinedResult = "uVDiJHaavRYX8oWt5ctkaa7j1cw=";
        CryptoAlgorithm ca = sc.getCryptoAlgorithm("java");
        ca.setCipher("SHA1");
        String output = ca.encrypt(preDefinedInput);
        assertEquals("SHA1 Encryption failed ", preDefinedResult, output);

    }
    
        public void testJavaCryptSha256() throws Exception
    {
  
        String preDefinedResult = "XBSqev4ilv7P7852G2rL5WgX3FLy8VzfOY+tVq+xjek=";
        CryptoAlgorithm ca = sc.getCryptoAlgorithm("java");
        ca.setCipher("SHA-256");
        String output = ca.encrypt(preDefinedInput);
        assertEquals("SHA256 Encryption failed ", preDefinedResult, output);

    }
    
}
