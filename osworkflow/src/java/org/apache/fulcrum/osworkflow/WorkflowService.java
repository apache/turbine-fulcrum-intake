package org.apache.fulcrum.osworkflow;


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


import org.apache.avalon.framework.component.Component;

import com.opensymphony.workflow.Workflow;
import com.opensymphony.workflow.WorkflowException;

/**
 * WorkflowService interface.
 *
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public interface WorkflowService
    extends Component
{
    /** Avalon role - used to id the component within the manager */
    String ROLE = WorkflowService.class.getName();

    /** Retrives a workflow based on the caller 
     * @param caller the value of the caller
     * @return the Workflow for this caller
     */
    public Workflow retrieveWorkflow(String caller);
    
    /** For a specific caller and status, return all the workflows.
     * 
     * @param caller the value of the caller
     * @param status the status, defined in the workflow xml file
     * @return an array of longs of the workflow id's
     * @throws com.opensymphony.workflow.WorkflowException if there is a problem
     */
    public long[] retrieveWorkflows(String caller, String status) throws WorkflowException;
    
    


}
