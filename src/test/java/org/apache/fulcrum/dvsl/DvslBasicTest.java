package org.apache.fulcrum.dvsl;

import java.io.StringReader;
import java.io.StringWriter;

import junit.framework.TestCase;

public class DvslBasicTest 
    extends TestCase
{
    private String dvsl = "#match(\"element\")Hello from element! $node.value()#end";
    private String input = "<?xml version=\"1.0\"?><document><element>Foo</element></document>";

    public DvslBasicTest( String name )
    {
        super(name);
    }


  public void setUp()
    {
    }

    public void tearDown()
    {
    }

    public void testConstruction()
    {
    }

    public void testSelection()
    {
        try
        {
            doit();
        }
        catch( Exception e )
        {
            fail( e.getMessage() );
        }
    }

    public void doit()
        throws Exception
    {
        /*
         *  kludgy test for now - hit the service class directly
         */
        DefaultDvslService dvslservice = new DefaultDvslService();

        /*
         *  register the stylesheet
         */
        dvslservice.register( "style", new StringReader( dvsl ), null);
    
        /*
         *  render the document
         */

        StringWriter sw = new StringWriter();

        dvslservice.transform( "style", new StringReader( input ), sw );

        if( !sw.toString().equals("Hello from element! Foo"))
            fail( "Result of first test is wrong : " + sw.toString() );
    }
}
