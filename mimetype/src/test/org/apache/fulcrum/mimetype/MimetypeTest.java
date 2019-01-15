package org.apache.fulcrum.mimetype;

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

import java.io.File;
import java.util.Locale;

import org.apache.fulcrum.mimetype.util.MimeType;
import org.apache.fulcrum.mimetype.util.MimeTypeMapperTest;
import org.apache.fulcrum.testcontainer.BaseUnit5Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link DefaultMimeTypeService}.
 *
 * @author <a href="paulsp@apache.org">Paul Spencer</a>
 * @author <a href="epugh@upstate.com">Eric Pugh</a>
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @author Daniel Rall
 * @version $Id$
 */
public class MimetypeTest extends BaseUnit5Test
{
    private MimeTypeService mimeTypeService = null;

    @BeforeEach
    public void setUp() throws Exception
    {
        mimeTypeService = (MimeTypeService) lookup(MimeTypeService.ROLE);
        mimeTypeService.setContentType(MimeTypeMapperTest.MIME_TYPE + ' ' +
                                       MimeTypeMapperTest.KNOWN_EXTENSIONS);
    }

    /**
     * @throws Exception generic exception
     */
    @Test
    public void testGetCharSet() throws Exception
    {
        Locale locale = new Locale("en", "US");
        String s = mimeTypeService.getCharSet(locale);
        assertEquals("ISO-8859-1", s);
    }

    /**
     * @throws Exception generic exception
     */
    @Test
    public void testSetGetContentType() throws Exception
    {
        File f;
        for (int i = 0; i < MimeTypeMapperTest.INPUT_EXTENSIONS.length; i++)
        {
            // Construct a File object with a name based on the
            // extensions used in our MimeTypeMapperTest test.
            f = new File("test." + MimeTypeMapperTest.INPUT_EXTENSIONS[i]);
            assertEquals(MimeTypeMapperTest.MIME_TYPE,
                         mimeTypeService.getContentType(f));
        }
    }

    /**
     * @throws Exception generic exception
     */
    @Test
    public void testGetDefaultExtensionForCrazy() throws Exception
    {
        String result = mimeTypeService.getDefaultExtension(MimeTypeMapperTest.MIME_TYPE);
        assertEquals("crazy", result);
        MimeType mt = new MimeType(MimeTypeMapperTest.MIME_TYPE);
        result = mimeTypeService.getDefaultExtension(mt);
        assertEquals("crazy", result);
    }

    /**
     * @throws Exception generic exception
     */
    @Test
    public void testGetDefaultExtensionForPdf() throws Exception
    {
        assertEquals("pdf", mimeTypeService.getDefaultExtension("application/pdf"));
    }

    /**
     * @throws Exception generic exception
     */
    @Test
    public void testGetContentTypeForPdf() throws Exception
    {
        assertEquals("application/pdf", mimeTypeService.getContentType("foo.pdf"));
        assertEquals("application/pdf", mimeTypeService.getContentType("foo.PDF"));
        assertEquals("application/pdf", mimeTypeService.getContentType("foo.Pdf"));
    }
}
