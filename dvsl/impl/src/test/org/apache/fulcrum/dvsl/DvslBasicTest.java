package org.apache.fulcrum.dvsl;

/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import java.io.StringReader;
import java.io.StringWriter;

import org.apache.fulcrum.testcontainer.BaseUnitTest;

/**
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 */
public class DvslBasicTest extends BaseUnitTest
{
    private String dvsl = "#match(\"element\")Hello from element! $node.value()#end";
    private String input = "<?xml version=\"1.0\"?><document><element>Foo</element></document>";
    
    private DvslService dvslService = null;
    public DvslBasicTest(String name)
    {
        super( name );
    }

    public void setUp() throws Exception
    {
         super.setUp();
         try
         {
           dvslService = (DvslService) this.resolve( DvslService.class.getName() );
         }
         catch (Throwable e)
         {
             
             fail(e.getMessage());
         }
    }

    public void testSelection() throws Exception
    {
       /*
        *  register the stylesheet
        */
        dvslService.register("style", new StringReader(dvsl), null);
        /*
         *  render the document
         */
        StringWriter sw = new StringWriter();
        dvslService.transform("style", new StringReader(input), sw);
        assertEquals("Hello from element! Foo",sw.toString());
        
    }
}
