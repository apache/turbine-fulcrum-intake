package org.apache.fulcrum.osworkflow.example.tools;


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


import java.util.ArrayList;
import java.util.List;

import org.apache.fulcrum.osworkflow.WorkflowInstance;
import org.apache.fulcrum.osworkflow.WorkflowService;
import org.apache.fulcrum.osworkflow.WorkflowServiceFacade;
import org.apache.turbine.om.security.User;
import org.apache.turbine.services.pull.ApplicationTool;
import org.apache.turbine.util.RunData;

import com.opensymphony.workflow.Workflow;
import com.opensymphony.workflow.WorkflowException;
/**
 * A pull tool which provides lookups of workflows by delegating
 * to the configured Fulcrum <code>WorkflowService</code>.
 *
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 */
public class WorkflowTool implements ApplicationTool
{

    /** Fulcrum Localization component */
    private WorkflowService workflowService;

    /** Turbine rundata object */
    private RunData data;

    /** Turbine User object */
    private User user;


    /**
     * Creates a new instance.  Used by <code>PullService</code>.
     */
    public WorkflowTool()
    {
        refresh();
    }

    /**
     * Initialize the tool with the RunData object.
     */
    public final void init(Object obj)
    {
        data = (RunData) obj;
    }
    /**
     * Remove the Turbine RunData object.
     */
    public void refresh()
    {
        data = null;
    }

    /**
     * Sets the Turbine User object
     * @param user  The User object to set
     */
    public void setUser(User user)
    {
        this.user = user;
    }

    /**
     * Retrieve the Turbine User object
     * @return Turbine User
     */
    public User getUser()
    {
        if (user == null)
        {
            user = data.getUser();
        }
        return user;
    }

    /**
     * Returns all workflows that belong the user and have a
     * certain status specified in the workflow xml file.
     *
     * @param status A string like 'Accepted'
     * @return A list of WorkflowInstance objects
     * @throws WorkflowException is thrown if there is an error.
     */
    public List retrieveWorkflows(String status) throws WorkflowException
    {
        List workflows = new ArrayList();
        String caller = getUser().getName();
        long workflowIds[] =
            WorkflowServiceFacade.retrieveWorkflows(caller, status);
        Workflow workflow = WorkflowServiceFacade.retrieveWorkflow(caller);
        for (int i = 0; i < workflowIds.length; i++)
        {
            WorkflowInstance workflowInstance =
                new WorkflowInstance(workflow, workflowIds[i]);
            workflows.add(workflowInstance);
        }
        return workflows;
    }

    /**
     * Retrieve Workflow
     *
     * @return Workflow object
     * @throws WorkflowException
     */
    public Workflow retrieveWorkflow() throws WorkflowException
    {
        return retrieveWorkflow(getUser());
    }

    /**
     * Retrieve Workflow of given User
     *
     * @param User A user object to look workflows up for
     * @return Workflow The workflow for the specified user.
     * @throws WorkflowException
     */
    public Workflow retrieveWorkflow(User user) throws WorkflowException
    {
        return WorkflowServiceFacade.retrieveWorkflow(user.getName());
    }

}
