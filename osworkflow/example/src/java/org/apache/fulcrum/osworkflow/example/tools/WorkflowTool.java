package org.apache.fulcrum.osworkflow.example.tools;

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
import java.util.List;

import org.apache.avalon.framework.component.ComponentException;
import org.apache.fulcrum.osworkflow.WorkflowInstance;
import org.apache.fulcrum.osworkflow.WorkflowService;
import org.apache.turbine.om.security.User;
import org.apache.turbine.services.InstantiationException;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.avaloncomponent.AvalonComponentService;
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
     * Returns  the WorkflowService.  Lazy loads the WorkflowService if it
     * hasn't been loaded from Avalon yet.
     *
     * @return a fulcrum WorkflowService
     */
    public WorkflowService getWorkflowService()
    {
        if (workflowService == null)
        {
            AvalonComponentService acs =
                (AvalonComponentService) TurbineServices
                    .getInstance()
                    .getService(
                    AvalonComponentService.SERVICE_NAME);
            try
            {
                workflowService =
                    (WorkflowService) acs.lookup(WorkflowService.ROLE);
            }
            catch (ComponentException ce)
            {
                throw new InstantiationException(
                    "Problem looking up Localization Service:"
                        + ce.getMessage());
            }
        }
        return workflowService;
    }
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
            getWorkflowService().retrieveWorkflows(caller, status);
        Workflow workflow = getWorkflowService().retrieveWorkflow(caller);
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
        return getWorkflowService().retrieveWorkflow(user.getName());
    }

}
