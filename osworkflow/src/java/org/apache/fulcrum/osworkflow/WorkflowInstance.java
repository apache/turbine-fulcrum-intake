package org.apache.fulcrum.osworkflow;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
 *    "Apache Turbine" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Turbine", nor may "Apache" appear in their name, without
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.InvalidInputException;
import com.opensymphony.workflow.Workflow;
import com.opensymphony.workflow.WorkflowException;
import com.opensymphony.workflow.loader.ActionDescriptor;
import com.opensymphony.workflow.loader.WorkflowDescriptor;
import com.opensymphony.workflow.query.WorkflowQuery;
/**
 * WorkflowInstance represents a specific instance of a workflow.  Therefore it has
 * all the context information like what state it is in, what it's actions are etc.
 *
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class WorkflowInstance
{
    /** The workflow that this instance is a type of */
    private Workflow workflow;

    /** The id of this workflow */
    private long id;

    /**
     * @return long id of this workflow
     */
    public long getId()
    {
        return id;
    }
    /**
     * Make sure that the user can't construct a workflow instance directly.
     * Must use the service instead.
     *
     */
    private WorkflowInstance()
    {
        // can't use this version
    }

    /**
     * Simple constructor to create a workflow instance with the workflow, and the id of an
     * instance of the workflow.
     * @param workflow The workflow this instance belongs to
     * @param id The id of this workflow instance
     */
    public WorkflowInstance(Workflow workflow, long id)
    {
        this.workflow = workflow;
        this.id = id;
    }
    /**
     * @param String workflowName The name of the workflow to be checked
     * @param int initialStep  The initial step I want to check
     * @return boolean whether we can initialize at this step or not
     * @throws com.opensymphony.workflow.WorkflowException If there is a problem
     */
    public boolean canInitialize(String workflowName, int initialStep)
        throws WorkflowException
    {
        return workflow.canInitialize(workflowName, initialStep);
    }
    /**
     * Whether you can modify the entry state of this workflow instance
     *
     * @param int step The step to check
     * @return Whether we can modify the entry state
     * @throws com.opensymphony.workflow.WorkflowException If there is a problem
     */
    public boolean canModifyEntryState(int step) throws WorkflowException
    {
        return workflow.canModifyEntryState(getId(), step);
    }
    /**
     * Modify the state of the specified workflow instance.
     * @param entryState the new state to change the workflow instance to.
     * @throws com.opensymphony.workflow.WorkflowException If a problem occurs
     */
    public void changeEntryState(int entryState) throws WorkflowException
    {
        workflow.changeEntryState(getId(), entryState);
    }
    /**
     * Perform an action on the the workflow instance.
     * @param actionId The action id to perform (action id's are listed in the workflow descriptor).
     * @param inputs The inputs to the workflow instance.
     * @throws InvalidInputException if a validator is specified and an input is invalid.
     * @throws com.opensymphony.workflow.WorkflowException If a problem occurs
     */
    public void doAction(int actionId, Map inputs)
        throws InvalidInputException, WorkflowException
    {
        workflow.doAction(getId(), actionId, inputs);
    }
    /**
     * Returns whether another workflow equals this workflow.  Doesn't compare
     * the id's.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj)
    {
        return workflow.equals(obj);
    }
    /**
     * Executes a special trigger-function using the context of the workflow instance.
     *
     * @param triggerId The id of the special trigger-function
     * @throws com.opensymphony.workflow.WorkflowException If there is a problem 
     */
    public void executeTriggerFunction(int triggerId) throws WorkflowException
    {
        workflow.executeTriggerFunction(getId(), triggerId);
    }
    /**
     * Get the available actions for the workflow instance.
     * @param inputs The inputs map to pass on to conditions
     * @return An array of action id's that can be performed on the specified entry
     * @throws IllegalArgumentException if the specified id does not exist, or if its workflow
     * descriptor is no longer available or has become invalid.
     * @throws com.opensymphony.workflow.WorkflowException If there is a problem
     */
    public int[] getAvailableActions(Map inputs) throws WorkflowException
    {
        return workflow.getAvailableActions(getId(), inputs);
    }
    /**
     * Returns a Collection of Step objects that are the current steps of the workflow instance.
     *
     * @return The steps that the workflow instance is currently in.
     * @throws com.opensymphony.workflow.WorkflowException If there is a problem
     */
    public List getCurrentSteps() throws WorkflowException
    {
        return workflow.getCurrentSteps(getId());
    }
    /**
     * Return the state of the workflow instance.
     * @return int The state id of the specified workflow
     * @throws com.opensymphony.workflow.WorkflowException If there is a problem
     */
    public int getEntryState() throws WorkflowException
    {
        return workflow.getEntryState(getId());
    }
    /**
     * Returns a list of all steps that are completed for the workflow instance.
     *
     * @return a List of Steps
     * @see com.opensymphony.workflow.spi.Step
     * @throws com.opensymphony.workflow.WorkflowException If there is a problem
     */
    public List getHistorySteps() throws WorkflowException
    {
        return workflow.getHistorySteps(getId());
    }
    /**
     * Get the PropertySet for the workflow instance.
     * @throws com.opensymphony.workflow.WorkflowException If there is a problem
     */
    public PropertySet getPropertySet() throws WorkflowException
    {
        return workflow.getPropertySet(getId());
    }
    /**
     * Get a collection (Strings) of currently defined permissions for the workflow instance.
     * @return A List of permissions specified currently (a permission is a string name).
     * @throws com.opensymphony.workflow.WorkflowException If there is a problem
     */
    public List getSecurityPermissions() throws WorkflowException
    {
        return workflow.getSecurityPermissions(getId());
    }
    /**
     * Get the workflow descriptor for the workflow instance.
     * 
     * @throws com.opensymphony.workflow.WorkflowException If there is a problem
     */
    public WorkflowDescriptor getWorkflowDescriptor() throws WorkflowException
    {
        return workflow.getWorkflowDescriptor(getWorkflowName());
    }
    /**
     * Get the name of the specified workflow instance.
     * @return The name
     * @throws com.opensymphony.workflow.WorkflowException If there is a problem
     */
    public String getWorkflowName() throws WorkflowException
    {
        return workflow.getWorkflowName(getId());
    }
    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return workflow.hashCode();
    }
    /**
     * Execute a workflow query and returns the list of workflows
     * @param query The workflow query
     * @return a List of workflows
     * @throws com.opensymphony.workflow.WorkflowException If there is a problem
     */
    public List query(WorkflowQuery query) throws WorkflowException
    {
        return workflow.query(query);
    }
    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return workflow.toString();
    }
    /**
     * Get the available actions for the workflow instance.
     * @return An list of actions that can be performed on the specified entry
     * @throws IllegalArgumentException if the specified id does not exist, or if its workflow
     * descriptor is no longer available or has become invalid.
     * @throws com.opensymphony.workflow.WorkflowException If there is a problem
     */
    public List getAllAvailableActions() throws WorkflowException
    {
        List actions = new ArrayList();
        int actionIds[] =
            workflow.getAvailableActions(getId(), Collections.EMPTY_MAP);
        for (int i = 0; i < actionIds.length; i++)
        {
            ActionDescriptor action =
                getWorkflowDescriptor().getAction(actionIds[i]);
            actions.add(action);
        }
        return actions;
    }
}
