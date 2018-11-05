package org.apache.fulcrum.mimetype.util;

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

import junit.framework.TestCase;

/**
 * Tests for the MimeTypeMapper
 * 
 * @author Eric Pugh
 * @author Daniel Rall
 *
 */
public class MimeTypeMapperTest extends TestCase
{
    /**
     * The MIME type used by most of our tests.
     */
    public static final String MIME_TYPE = "text/crazy";

    /**
     * The recognized extensions we map to {@link #MIME_TYPE}.
     */
    public static final String KNOWN_EXTENSIONS = "crazy crzy czy";

    /**
     * The file extensions used as input.
     */
    public static final String[] INPUT_EXTENSIONS =
    {
		"crazy", "crzy", "czy", "CZY", "cRAZy"
    };

    private MimeTypeMapper mtm;

    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(MimeTypeMapperTest.class);
    }

    /**
     * Constructor for MimeTypeMapperTest.
     * @param arg0 default arguments
     */
    public MimeTypeMapperTest(String arg0)
    {
        super(arg0);
    }

    public void setUp()
    {
        mtm = new MimeTypeMapper();
        mtm.setContentType(MIME_TYPE + ' ' + KNOWN_EXTENSIONS);
    }

    public void testGetSetContentType()
    {
        for (int i = 0; i < INPUT_EXTENSIONS.length; i++)
        {
            assertEquals(MIME_TYPE, mtm.getContentType(INPUT_EXTENSIONS[i]));
        }
    }

    /* ### This is actually a test case for MimeTypeMap.
	public void testGetDefaultExtension() throws Exception
    {
        String result = mtm.getDefaultExtension(MIME_TYPE);
        assertEquals("crazy", result);
        MimeType mt = new MimeType(MIME_TYPE);
        result = mtm.getDefaultExtension(mt);
        assertEquals("crazy", result);
    }*/
}
