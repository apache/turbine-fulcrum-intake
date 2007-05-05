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

package org.apache.fulcrum.osworkflow;
// Cactus and Junit imports
import junit.awtui.TestRunner;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.fulcrum.testcontainer.BaseUnitTest;

import com.opensymphony.workflow.Workflow;
/**
 * Test the WorkflowServiceFacade.
 *
 * @author <a href="epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class WorkflowServiceFacadeTest extends BaseUnitTest
{
    /**
     * Defines the testcase name for JUnit.
     *
     * @param name the testcase's name.
     */
    public WorkflowServiceFacadeTest(String name)
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
            new String[] { WorkflowServiceFacadeTest.class.getName()});
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
        return new TestSuite(WorkflowServiceFacadeTest.class);
    }

    public void testWorkflowFacadeNotConfigured() throws Exception
    {
        try
        {
            Workflow wf = WorkflowServiceFacade.retrieveWorkflow("caller");
        }
        catch (RuntimeException re)
        {
            //good;
        }
    }

    public void testWorkflowFacadeConfigured() throws Exception
    {
    	// this.lookup causes the workflow service to be configured.
		this.lookup(WorkflowService.ROLE);
        Workflow wf = WorkflowServiceFacade.retrieveWorkflow("caller");
		assertNotNull(wf);
    }

}
