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


import org.apache.fulcrum.testcontainer.BaseUnit5Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Basic testing of the Container
 *
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @version $Id$
 */
@DisplayName("Crypto Service Test")
@RunWith(JUnitPlatform.class)
public class CryptoServiceTest extends BaseUnit5Test
{
    private CryptoService sc = null;
    private static final String preDefinedInput = "Oeltanks";

    /**
     * Constructor for test.
     *
     * @param testInfo Junit 5 info object of the test being executed
     */
    public CryptoServiceTest(TestInfo testInfo)
    {

    }

    @BeforeEach
    public void setUp() throws Exception
    {
        sc = (CryptoService) this.lookup( CryptoService.ROLE );
    }


    @Test
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
        assertEquals( preDefinedResult, output, "Encryption failed ");
        /*
         * Test random Seed
         *
         */
        ca.setSeed(null);
        String result = ca.encrypt(preDefinedInput);
        ca.setSeed(result);
        output = ca.encrypt(preDefinedInput);
        assertEquals( output, result, "Encryption failed ");

    }
    @Test
    public void testClearCrypt() throws Exception
    {
        String preDefinedResult = "Oeltanks";

        CryptoAlgorithm ca = sc.getCryptoAlgorithm("clear");
        String output = ca.encrypt(preDefinedInput);
        assertEquals( preDefinedResult, output, "Encryption failed ");

    }
    @Test
    @DisplayName("OldJavaCrypt: Truncated base64 from MD5 (Turbine 2.1) Test")
    public void testOldJavaCryptMd5() throws Exception
    {
        String preDefinedResult = "XSop0mncK19Ii2r2CUe2";

        CryptoAlgorithm ca = sc.getCryptoAlgorithm("oldjava");
        ca.setCipher("MD5");
        String output = ca.encrypt(preDefinedInput);
        assertEquals( preDefinedResult, output, "MD5 Encryption failed ");

    }
    @Test
    public void testOldJavaCryptSha1() throws Exception
    {
        String preDefinedResult = "uVDiJHaavRYX8oWt5ctkaa7j";

        CryptoAlgorithm ca = sc.getCryptoAlgorithm("oldjava");
        ca.setCipher("SHA1");
        String output = ca.encrypt(preDefinedInput);
        assertEquals( preDefinedResult, output, "SHA1 Encryption failed ");

    }
    @Test
    public void testJavaCryptMd5() throws Exception
    {
        String preDefinedResult = "XSop0mncK19Ii2r2CUe29w==";
        CryptoAlgorithm ca = sc.getCryptoAlgorithm("java");
        ca.setCipher("MD5");
        String output = ca.encrypt(preDefinedInput);
        assertEquals( preDefinedResult, output, "MD5 Encryption failed ");
    }
    @Test
    public void testJavaCryptSha1() throws Exception
    {
        String preDefinedResult = "uVDiJHaavRYX8oWt5ctkaa7j1cw=";
        CryptoAlgorithm ca = sc.getCryptoAlgorithm("java");
        ca.setCipher("SHA1");
        String output = ca.encrypt(preDefinedInput);
        assertEquals( preDefinedResult, output, "SHA1 Encryption failed ");

    }
    @Test
    public void testJavaCryptSha256() throws Exception
    {
        String preDefinedResult = "XBSqev4ilv7P7852G2rL5WgX3FLy8VzfOY+tVq+xjek=";
        CryptoAlgorithm ca = sc.getCryptoAlgorithm("java");
        ca.setCipher("SHA-256");
        String output = ca.encrypt(preDefinedInput);
        assertEquals( preDefinedResult, output, "SHA256 Encryption failed ");
    }
    
    @Test
    public void testJavaCryptSha512() throws Exception
    {
        String preDefinedResult = "QlxxOMtVn0FFAXF+DRQl8o/b+WKEG6Nc7QRdqf/LTTz/+bOaoE/JihM8uJqTW7JQm/l/TmnmVKuLaD7jdVAtJw==";
        CryptoAlgorithm ca = sc.getCryptoAlgorithm("java");
        ca.setCipher("SHA-512");
        String output = ca.encrypt(preDefinedInput);
        assertEquals( preDefinedResult, output, "SHA512 Encryption failed ");
    }
}
