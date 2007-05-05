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
import java.util.Iterator;
import java.util.List;
import junit.awtui.TestRunner;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.fulcrum.testcontainer.BaseUnitTest;
//import com.opensymphony.module.user.Group;
//import com.opensymphony.module.user.User;
//import com.opensymphony.module.user.UserManager;
import com.opensymphony.workflow.Workflow;
import com.opensymphony.workflow.basic.BasicWorkflow;
import com.opensymphony.workflow.query.WorkflowQuery;
/**
 * CacheTest
 *
 * @author <a href="paulsp@apache.org">Paul Spencer</a>
 * @author <a href="epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class WorkflowServiceTest extends BaseUnitTest
{
    private WorkflowService workflowService = null;
    /**
     * Defines the testcase name for JUnit.
     *
     * @param name the testcase's name.
     */
    public WorkflowServiceTest(String name)
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
        TestRunner.main(new String[] { WorkflowServiceTest.class.getName()});
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
        return new TestSuite(WorkflowServiceTest.class);
    }
    protected void setUp()  throws Exception
    {
        super.setUp();
        try
        {
            workflowService = (WorkflowService) this.lookup(WorkflowService.ROLE);
        }
        catch (ComponentException e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    /**
     * Simple test to verify we have all dependencies.  Turn
     * off until we can get a released version of OSUser.
     * @throws Exception
     */
    public void OFFtestVerifyDependencies() throws Exception
    {
       /* UserManager um = UserManager.getInstance();
        User test = um.createUser("test");
        test.setPassword("test");
        Group foos = um.createGroup("foos");
        Group bars = um.createGroup("bars");
        Group bazs = um.createGroup("bazs");
        test.addToGroup(foos);
        test.addToGroup(bars);
        test.addToGroup(bazs);*/
        Workflow wf2 = new BasicWorkflow("test");
        long id = wf2.initialize("example", 1, null);
        assertEquals(1, id);
        Workflow wf = new BasicWorkflow("test");
        WorkflowQuery queryLeft =
            new WorkflowQuery(WorkflowQuery.OWNER, WorkflowQuery.CURRENT, WorkflowQuery.EQUALS, "test");
        WorkflowQuery queryRight =
            new WorkflowQuery(WorkflowQuery.STATUS, WorkflowQuery.CURRENT, WorkflowQuery.EQUALS, "Underway");
        WorkflowQuery query = new WorkflowQuery(queryLeft, WorkflowQuery.AND, queryRight);
        List workflows = wf.query(query);
        assertEquals(1, workflows.size());
        for (Iterator iterator = workflows.iterator(); iterator.hasNext();)
        {
            Long wfId = (Long) iterator.next();
            System.out.println("Workflow ID:" + wfId);
        }
    }

    /** Retrives a workflow based on the caller */
    public void testRetrieveWorkflow() throws Exception
    {
        Workflow wf = workflowService.retrieveWorkflow("caller");
        assertNotNull(wf);
    }

    public void testRetrieveWorkflows(String caller, String status) throws Exception
    {
        Workflow wf = workflowService.retrieveWorkflow("caller");
        long workflowId = wf.initialize("example", 1, null);
        long workflowId2 = wf.initialize("example", 1, null);
        long[]workflowIds = workflowService.retrieveWorkflows("caller","Underway");
        assertEquals(2,workflowIds.length);
        assertEquals(workflowId,workflowIds[0]);
        assertEquals(workflowId2,workflowIds[1]);

    }
}

