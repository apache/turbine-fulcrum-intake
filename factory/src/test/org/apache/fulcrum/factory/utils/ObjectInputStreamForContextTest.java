/*
 * Created on Aug 20, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.apache.fulcrum.factory.utils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import junit.framework.TestCase;
/**
 * @author Eric Pugh
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ObjectInputStreamForContextTest extends TestCase
{
    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(ObjectInputStreamForContextTest.class);
    }
    /*
     * Class to test for void ObjectInputStreamForContext(InputStream, ClassLoader)
     */
    public void testObjectInputStreamForContextInputStreamClassLoader() throws Exception
    {
        Object object = new String("I am testing");
        Object object2 = null;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bout);
        out.writeObject(object);
        out.flush();
        ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
        ObjectInputStreamForContext in = new ObjectInputStreamForContext(bin, String.class.getClassLoader());
        object2 = in.readObject();
        assertEquals(object.toString(), object2.toString());
        assertEquals(object, object2);
    }
}
