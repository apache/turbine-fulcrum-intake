/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *     "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache" or
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.fulcrum.mimetype;

import java.io.File;
import java.util.Locale;

import org.apache.avalon.merlin.unit.AbstractMerlinTestCase;
import org.apache.fulcrum.mimetype.util.MimeType;
import org.apache.fulcrum.mimetype.util.MimeTypeMapperTest;

/**
 * Tests for {@link TurbineMimeTypeService}.
 *
 * @author <a href="paulsp@apache.org">Paul Spencer</a>
 * @author <a href="epugh@upstate.com">Eric Pugh</a> 
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @author Daniel Rall
 * @version $Id$
 */
public class MimetypeTest extends AbstractMerlinTestCase
{
    private MimeTypeService mimeTypeService = null;

    /**
     * Defines the testcase name for JUnit.
     *
     * @param name the testcase's name.
     */
    public MimetypeTest(String name)
    {
        super(name);
    }

    public void setUp() throws Exception
    {
        super.setUp();
        try
        {
            mimeTypeService = (MimeTypeService) resolve("/test/mimetype");
        }
        catch (Throwable e)
        {
            fail(e.getMessage());
        }
        mimeTypeService.setContentType(MimeTypeMapperTest.MIME_TYPE + ' ' +
                                       MimeTypeMapperTest.KNOWN_EXTENSIONS);
    }

    public void testGetCharSet() throws Exception
    {
        Locale locale = new Locale("en", "US");
        String s = mimeTypeService.getCharSet(locale);
        assertEquals("ISO-8859-1", s);
    }

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

    public void testGetDefaultExtension() throws Exception
    {
        String result =
            mimeTypeService.getDefaultExtension(MimeTypeMapperTest.MIME_TYPE);
        assertEquals("crazy", result);
        MimeType mt = new MimeType(MimeTypeMapperTest.MIME_TYPE);
        result = mimeTypeService.getDefaultExtension(mt);
        assertEquals("crazy", result);
    }
}
