package org.apache.fulcrum.workflow;
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
    private Workflow workflow;
    private long id;
    /**
     * @return
     */
    public long getId()
    {
        return id;
    }
    private WorkflowInstance()
    {
        // can't use this version
    }
    public WorkflowInstance(Workflow workflow, long id)
    {
        this.workflow = workflow;
        this.id = id;
    }
    /**
     * @param arg0
     * @param arg1
     * @return
     * @throws com.opensymphony.workflow.WorkflowException
     */
    public boolean canInitialize(String arg0, int arg1) throws WorkflowException
    {
        return workflow.canInitialize(arg0, arg1);
    }
    /**
     * @param arg0
     * @param arg1
     * @return
     * @throws com.opensymphony.workflow.WorkflowException
     */
    public boolean canModifyEntryState(int arg1) throws WorkflowException
    {
        return workflow.canModifyEntryState(getId(), arg1);
    }
    /**
     * @param arg0
     * @param arg1
     * @throws com.opensymphony.workflow.WorkflowException
     */
    public void changeEntryState(int arg1) throws WorkflowException
    {
        workflow.changeEntryState(getId(), arg1);
    }
    /**
     * @param arg0
     * @param arg1
     * @param arg2
     * @throws com.opensymphony.workflow.InvalidInputException
     * @throws com.opensymphony.workflow.WorkflowException
     */
    public void doAction(int arg1, Map arg2) throws InvalidInputException, WorkflowException
    {
        workflow.doAction(getId(), arg1, arg2);
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj)
    {
        return workflow.equals(obj);
    }
    /**
     * @param arg0
     * @param arg1
     * @throws com.opensymphony.workflow.WorkflowException
     */
    public void executeTriggerFunction(int arg1) throws WorkflowException
    {
        workflow.executeTriggerFunction(getId(), arg1);
    }
    /**
     * @param arg0
     * @param arg1
     * @return
     * @throws com.opensymphony.workflow.WorkflowException
     */
    public int[] getAvailableActions(Map arg1) throws WorkflowException
    {
        return workflow.getAvailableActions(getId(), arg1);
    }
    /**
     * @param arg0
     * @return
     * @throws com.opensymphony.workflow.WorkflowException
     */
    public List getCurrentSteps() throws WorkflowException
    {
        return workflow.getCurrentSteps(getId());
    }
    /**
     * @param arg0
     * @return
     * @throws com.opensymphony.workflow.WorkflowException
     */
    public int getEntryState() throws WorkflowException
    {
        return workflow.getEntryState(getId());
    }
    /**
     * @param arg0
     * @return
     * @throws com.opensymphony.workflow.WorkflowException
     */
    public List getHistorySteps() throws WorkflowException
    {
        return workflow.getHistorySteps(getId());
    }
    /**
     * @param arg0
     * @return
     * @throws com.opensymphony.workflow.WorkflowException
     */
    public PropertySet getPropertySet() throws WorkflowException
    {
        return workflow.getPropertySet(getId());
    }
    /**
     * @param arg0
     * @return
     * @throws com.opensymphony.workflow.WorkflowException
     */
    public List getSecurityPermissions() throws WorkflowException
    {
        return workflow.getSecurityPermissions(getId());
    }
    /**
     * @param arg0
     * @return
     * @throws com.opensymphony.workflow.WorkflowException
     */
    public WorkflowDescriptor getWorkflowDescriptor() throws WorkflowException
    {
        return workflow.getWorkflowDescriptor(getWorkflowName());
    }
    /**
     * @param arg0
     * @return
     * @throws com.opensymphony.workflow.WorkflowException
     */
    public String getWorkflowName() throws WorkflowException
    {
        return workflow.getWorkflowName(getId());
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return workflow.hashCode();
    }
    /**
     * @param arg0
     * @return
     * @throws com.opensymphony.workflow.WorkflowException
     */
    public List query(WorkflowQuery arg0) throws WorkflowException
    {
        return workflow.query(arg0);
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return workflow.toString();
    }
    /**
    * @param arg0
    * @return
    * @throws com.opensymphony.workflow.WorkflowException
    */
    public List getAllAvailableActions() throws WorkflowException
    {
        List actions = new ArrayList();
        int actionIds[] = workflow.getAvailableActions(getId(), Collections.EMPTY_MAP);
        for (int i = 0; i < actionIds.length; i++)
        {
            ActionDescriptor action = getWorkflowDescriptor().getAction(actionIds[i]);
            actions.add(action);
        }
        return actions;
    }
}
