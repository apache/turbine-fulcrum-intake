package org.apache.fulcrum.template;

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


//
import junit.awtui.TestRunner;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.fulcrum.template.TemplateServiceFacade;
import org.apache.fulcrum.testcontainer.BaseUnitTest;


/**
 * Test the WorkflowServiceFacade.  
 *
 * @author <a href="epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class TemplateFacade extends BaseUnitTest
{
    /**
     * Defines the testcase name for JUnit.
     *
     * @param name the testcase's name.
     */
    public TemplateFacade(String name)
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
        TestRunner.main(
            new String[] { TemplateFacade.class.getName()});
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
        return new TestSuite(TemplateFacade.class);
    }

    public void testFacadeNotConfigured() throws Exception
    {
        try
        {
            TemplateServiceFacade.templateExists("boo");
        }
        catch (RuntimeException re)
        {
            //good;
        }
    }

    public void testFacadeConfigured() throws Exception
    {
    	// this.lookup causes the workflow service to be configured.
		this.lookup(TemplateService.ROLE);
		boolean exists = TemplateServiceFacade.templateExists("boo");
		assertFalse(exists);       
    }

}
