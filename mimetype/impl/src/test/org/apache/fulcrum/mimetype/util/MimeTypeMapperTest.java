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
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class MimeTypeMapperTest extends TestCase
{
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
    public void testGetSetContentType()
    {
        String mimeType = "text/crazy";
        String fileExtensions = "crazy crzy czy";
        MimeTypeMapper mtm = new MimeTypeMapper();
        mtm.setContentType(mimeType + " " + fileExtensions);
        
		assertEquals(mimeType,mtm.getContentType("crazy"));
		assertEquals(mimeType,mtm.getContentType("crzy"));
		assertEquals(mimeType,mtm.getContentType("czy"));
        
    }
    
	public void testGetDefaultExtension() throws Exception
{
}
}
