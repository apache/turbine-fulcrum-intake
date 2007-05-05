package org.apache.fulcrum.osworkflow;



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

import com.opensymphony.workflow.Workflow;
import com.opensymphony.workflow.WorkflowException;


/**
 * This is a Facade class for WorkflowService.
 *
 * This class provides static methods that call related methods of the
 * implementation of the WorkflowService used by the System.
 *
 * @version $Id$
 */
public class WorkflowServiceFacade
{

    /** Static instance of the WorkflowService.  */
    private static WorkflowService workflowService;

    /**
     * Utility method for accessing the service
     * implementation
     *
     * @return a WorkflowService implementation instance
     */
    private static WorkflowService getService()
    {
    	if(workflowService==null){
    		throw new RuntimeException("Workflow Service has not been set yet.");
    	}
        return workflowService;
    }
    static void setWorkflowService(WorkflowService service)
    {
        workflowService = service;
    }

    /** Retrives a workflow based on the caller */
    public static Workflow retrieveWorkflow(String caller)
    {
        return getService().retrieveWorkflow(caller);
    }

    /** For a specific caller and status, return all the workflows. */
    public static long[] retrieveWorkflows(String caller, String status)
            throws WorkflowException
    {
        return getService().retrieveWorkflows(caller, status);
    }
}
