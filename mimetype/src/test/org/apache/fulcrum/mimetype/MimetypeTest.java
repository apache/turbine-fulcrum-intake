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
// Cactus and Junit imports
import java.io.File;
import java.util.Locale;
import junit.awtui.TestRunner;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.fulcrum.mimetype.util.MimeType;
import org.apache.fulcrum.testcontainer.BaseUnitTest;
/**
 * CacheTest
 *
 * @author <a href="paulsp@apache.org">Paul Spencer</a>
 * @author <a href="epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class MimetypeTest extends BaseUnitTest
{
    private MimeTypeService mimeTypeService = null;
    private static final String mimeType = "text/crazy";
    private static final String fileExtensions = "crazy crzy czy";
    /**
     * Defines the testcase name for JUnit.
     *
     * @param name the testcase's name.
     */
    public MimetypeTest(String name)
    {
        super(name);
    }
    /**
     * Start the tests.
     *
     * @param args the arguments. Not used
     */
    public static void main(String args[])
    {
        TestRunner.main(new String[] { MimetypeTest.class.getName()});
    }
    /**
     * Creates the test suite.
     *
     * @return a test suite (<code>TestSuite</code>) that includes all methods
     *         starting with "test"
     */
    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(MimetypeTest.class);
    }
    protected void setUp() throws Exception
    {
        super.setUp();
        try
        {
            mimeTypeService = (MimeTypeService) this.lookup(MimeTypeService.ROLE);
        }
        catch (ComponentException e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    public void testGetCharSet() throws Exception
    {
        Locale locale = new Locale("en", "US");
        String s = mimeTypeService.getCharSet(locale);
        assertEquals("ISO-8859-1", s);
    }
    public void testSetGetContentType() throws Exception
    {
        mimeTypeService.setContentType(mimeType + " " + fileExtensions);
        File files[] = new File[3];
        files[0] = new File("test.crazy");
        files[1] = new File("test.crzy");
        files[2] = new File("test.czy");
        assertEquals(mimeType, mimeTypeService.getContentType(files[0]));
        assertEquals(mimeType, mimeTypeService.getContentType(files[1]));
        assertEquals(mimeType, mimeTypeService.getContentType(files[2]));
    }
    public void testGetDefaultExtension() throws Exception
    {
        mimeTypeService.setContentType(mimeType + " " + fileExtensions);
        String result = mimeTypeService.getDefaultExtension(mimeType);
        assertEquals("crazy", result);
        MimeType mt = new MimeType(mimeType);
        result = mimeTypeService.getDefaultExtension(mt);
        assertEquals("crazy", result);
    }
}
