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


import java.util.Locale;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Test case for the locale tokenizer.
 *
 * @author <a href="mailto:dlr@collab.net">Daniel Rall</a>
 * @author <a href="mailto:jason@zenplex.com">Jason van Zyl</a>
 * @version $Id$
 */
public class LocaleTokenizerTest
    extends TestCase
{
    private static final String HEADER = "en, es;q=0.8, zh-TW;q=0.1";

    public LocaleTokenizerTest(String name)
    {
        super(name);
    }

    public static Test suite()
    {
        return new TestSuite(LocaleTokenizerTest.class);
    }

    public void testLocaleTokenizer()
    {
        try
        {
            LocaleTokenizer tok = new LocaleTokenizer(HEADER);
            Locale locale = (Locale) tok.next();
            assertEquals("Either wrong language or order parsing: " + locale,
                         locale.getLanguage(), "en");
            locale = (Locale) tok.next();
            assertEquals("Either wrong language or order parsing: " + locale,
                         locale.getLanguage(), "es");
            locale = (Locale) tok.next();
            assertEquals("Either wrong country or order parsing: " + locale,
                         locale.getCountry(), "TW");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }
}
