/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *     "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache" or
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
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
import com.opensymphony.module.user.Group;
import com.opensymphony.module.user.User;
import com.opensymphony.module.user.UserManager;
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
     * Simple test to verify we have all dependencies
     * @throws Exception
     */
    public void testVerifyDependencies() throws Exception
    {
        UserManager um = UserManager.getInstance();
        User test = um.createUser("test");
        test.setPassword("test");
        Group foos = um.createGroup("foos");
        Group bars = um.createGroup("bars");
        Group bazs = um.createGroup("bazs");
        test.addToGroup(foos);
        test.addToGroup(bars);
        test.addToGroup(bazs);
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

