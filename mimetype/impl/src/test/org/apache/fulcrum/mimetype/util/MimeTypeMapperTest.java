/*
 * Created on Aug 20, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.apache.fulcrum.mimetype.util;

import junit.framework.TestCase;

/**
 * @author Eric Pugh
 * @author Daniel Rall
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
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
     * @param arg0
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
