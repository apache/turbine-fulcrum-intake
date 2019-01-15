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

import org.apache.fulcrum.testcontainer.BaseUnit5Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


/**
 * Test case for the locale tokenizer.
 *
 * @author <a href="mailto:dlr@collab.net">Daniel Rall</a>
 * @author <a href="mailto:jason@zenplex.com">Jason van Zyl</a>
 * @version $Id$
 */
public class LocaleTokenizerTest extends BaseUnit5Test
{
    private static final String HEADER = "en, es;q=0.8, zh-TW;q=0.1";

    @BeforeEach
    public void setup() {
    	
    }
    
    @Test
    public void testLocaleTokenizer()
    {
        try
        {
            LocaleTokenizer tok = new LocaleTokenizer(HEADER);
            Locale locale = (Locale) tok.next();
            assertEquals(locale.getLanguage(), "en",
            		"Either wrong language or order parsing: " + locale);
            
            locale = (Locale) tok.next();
            assertEquals(locale.getLanguage(), "es",
            		"Either wrong language or order parsing: " + locale);
            
            locale = (Locale) tok.next();
            assertEquals(locale.getLanguage(), "zh",
            		"Either wrong language or order parsing: " + locale);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }
}
