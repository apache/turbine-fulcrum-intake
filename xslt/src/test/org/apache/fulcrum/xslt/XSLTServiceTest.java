package org.apache.fulcrum.xslt;


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


import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.component.ComponentException;
import org.apache.fulcrum.testcontainer.BaseUnit5Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * XSLTServiceTest
 *
 * @author <a href="paulsp@apache.org">Paul Spencer</a>
 * @author <a href="epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class XSLTServiceTest extends BaseUnit5Test
{
    private XSLTService xsltService = null;


    @BeforeEach
    protected void setUp() throws Exception
    {
        try
        {
            xsltService = (XSLTService) this.lookup(XSLTService.ROLE);
        }
        catch (ComponentException e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    /**
     * Simple test that verify an object can be created and deleted.
     * @throws Exception the test failed
     */
    @Test
    public void testPath() throws Exception
    {
        assertNotNull(xsltService);
    }

    /**
     * Test an identity transformation to make sure that the service
     * works.
     *
     * @throws Exception the test failed
     */
    @Test
    public void testTransform() throws Exception
    {
        Reader reader = new FileReader("./pom.xml");
        Writer writer = new FileWriter("./target/testTransform.xml");
        xsltService.transform("identity.xslt", reader, writer);
        reader.close();
        writer.close();
    }

     /**
     * Test invocation of a XSLT having no source document.
     *
     * @throws Exception the test failed
     */
    @Test
    public void testTransformXsltOnly() throws Exception
    {
        Map values = new HashMap();
        values.put("name", "Fulcrum");
        String result = xsltService.transform("hello.xslt", values );
        assertTrue(result.contains("Hello Fulcrum"));
    }
}
