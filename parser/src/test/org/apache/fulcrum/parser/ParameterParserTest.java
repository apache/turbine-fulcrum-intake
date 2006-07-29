package org.apache.fulcrum.parser;

/*
 * Copyright 2000-2004 The Apache Software Foundation.
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


import org.apache.avalon.framework.component.ComponentException;
import org.apache.fulcrum.testcontainer.BaseUnitTest;
/**
 * Basic test that ParameterParser instantiates.
 *
 * @author <a href="epugh@opensourceconnections.com">Eric Pugh</a>
 * @version $Id$
 */
public class ParameterParserTest extends BaseUnitTest
{
    private ParameterParser parameterParser = null;

    /**
     * Defines the testcase name for JUnit.
     *
     * @param name the testcase's name.
     */
    public ParameterParserTest(String name)
    {
        super(name);
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        try
        {
            parameterParser = (ParameterParser) this.lookup(ParameterParser.class.getName());
        }
        catch (ComponentException e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    public void testConfiguredUrlCaseFolding() throws Exception
    {
        assertTrue(parameterParser.getUrlFolding() == ParameterParser.URL_CASE_FOLDING_NONE);
    }
    
    /**
     * Simple test to verify that URL Case Folding works properly
     * 
     * @throws Exception
     */
    public void testRepositoryExists() throws Exception
    {
        assertEquals("TRIMMED_and_Not_Modified",parameterParser.convertAndTrim(" TRIMMED_and_Not_Modified ", ParameterParser.URL_CASE_FOLDING_NONE));
        assertEquals("trimmed_and_lower_case",parameterParser.convertAndTrim(" TRIMMED_and_Lower_Case ", ParameterParser.URL_CASE_FOLDING_LOWER));
        assertEquals("TRIMMED_AND_UPPER_CASE",parameterParser.convertAndTrim(" TRIMMED_and_Upper_Case ", ParameterParser.URL_CASE_FOLDING_UPPER));
    }
}
