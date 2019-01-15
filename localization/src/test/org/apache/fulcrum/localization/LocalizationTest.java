package org.apache.fulcrum.localization;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Locale;
import java.util.MissingResourceException;

import org.apache.fulcrum.testcontainer.BaseUnit5Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the API of the
 * {@link org.apache.fulcrum.localization.LocalizationService}.
 *
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @author <a href="mailto:jason@zenplex.com">Jason van Zyl</a>
 */
public class LocalizationTest extends BaseUnit5Test
{

    private LocalizationService localizationService = null;

    @BeforeEach
    public void setUp() throws Exception
    {
    	localizationService = (LocalizationService) lookup(LocalizationService.ROLE);
    }
    
    
    /**
     * Test localization
     * @throws Exception generic exception
     */
    @Test
    public void testLocalization() throws Exception
    {
        // Test retrieval of text using multiple default bundles
        String s = localizationService.getString(null, null, "key1");
        assertEquals("value1", s, "Unable to retrieve localized text for locale: default");
        
        s = localizationService.getString(null, new Locale("en", "US"), "key2");
        assertEquals("value2", s, "Unable to retrieve localized text for locale: en-US");
        
        s = localizationService.getString("org.apache.fulcrum.localization.BarBundle", new Locale("ko", "KR"), "key3");
        assertEquals(s, "[ko] value3", "Unable to retrieve localized text for locale: ko-KR");
        
        try
        {
            localizationService.getString("DoesNotExist", new Locale("ko", ""), "key1");
            fail();
        }
        catch (MissingResourceException expectedFailure)
        {
            // Asked for resource bundle which does not exist.
        }
        catch( Throwable e )
        {
            // should not happen
            fail();
        }
        
        // When a locale is used which cannot be produced for a given
        // bundle, fall back to the default locale.
        s = localizationService.getString(null, new Locale("ko", "KR"), "key4");
        assertEquals(s, "value4", "Unable to retrieve localized text for locale: default");
        
        try
        {
            localizationService.getString(null, null, "NoSuchKey");
            fail();
        }
        catch (MissingResourceException expectedFailure)
        {
            // Asked for key from default bundle which does not exist,
        }
    }
    
    @Test
    public void testGetString()
    {
        String key1 = "key1";
        String value1 = "value1";
        String key2 = "key2";
        String value2 = "value2";
        String key3 = "key3";
        String value3 = "value3";
        String key4 = "key4";
        String value4 = "value4";

        assertEquals(value1, localizationService.getString(key1));
        assertEquals(value2, localizationService.getString(key2));
        assertEquals(value3, localizationService.getString(key3));
        assertEquals(value4, localizationService.getString(key4));

    }
    
    /**
     * Putting this in a separate test case because it fails..  Why?  I don't know.  I have never
     * used localization, so I leave it to brains better then mine. -dep
     * 
     * @throws Exception generic exception
     */
    /*
    public void OFFtestRetrievingOddLocale() throws Exception
    {
    	// TODO Figure out why this test fails!
        String s = localizationService.getString(null, new Locale("fr", "US"), "key3");
        assertEquals("[fr] value3", s, "Unable to retrieve localized text for locale: fr");
    }
    */
}
