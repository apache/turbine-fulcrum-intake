package org.apache.fulcrum.osworkflow.example.modules.actions;

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

import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.osworkflow.WorkflowInstance;
import org.apache.fulcrum.osworkflow.WorkflowService;
import org.apache.fulcrum.osworkflow.WorkflowServiceFacade;
import org.apache.turbine.modules.actions.VelocityAction;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

//import com.opensymphony.module.user.Group;
//import com.opensymphony.module.user.User;
//import com.opensymphony.module.user.UserManager;
import com.opensymphony.workflow.Workflow;
/**
 * This action contains all the manipulations of a workflow.  Look at the various
 * doXXX methods to see what actions can be performed on workflows via this Action
 * class.
 *
 * @author     <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 */
public class WorkflowAction extends VelocityAction
{
  /** Logger to use */
    private static Log log = LogFactory.getLog(WorkflowAction.class);

    /** The workflowService that will be lazy loaded.  */
    private WorkflowService workflowService;

    /**
     *  This guy deals with actions related to workflows.
     *
     * @param  data           Current RunData information
     * @param  context        Context to populate
     * @exception  Exception  Thrown on error
     */
    public void doPerform(RunData data, Context context) throws Exception
    {
        log.debug("doPerform action event called");
        data.setScreenTemplate("Index.vm");
    }

    /**
     *  This logs you in as user "test".  This is required because
     * the example workflow can only be executed by user "test"
     *
     * @param  data           Current RunData information
     * @param  context        Context to populate
     * @exception  Exception  Thrown on error
     */
    public void doLogin(RunData data, Context context) throws Exception
    {
        log.debug("doLogin action event called");
        data.getUser().setName("test");

        data.getMessages().setMessage(
            "",
            "INFO",
            "You are logged in as user test!");
    }

    /**
     *  This sets up the user "test/test" in OSWorkflow.  This is
     * required because the example workflow only allows user test
     * to create it!  Also, it makes decisions based on the user
     * test being in groups "foo","bars", and "bazs".
     *
     * @param  data           Current RunData information
     * @param  context        Context to populate
     * @exception  Exception  Thrown on error
     */
    public void doSetupuser(RunData data, Context context) throws Exception
    {
        log.debug("doSetupuser action event called");
        data.setScreenTemplate("Index.vm");
       /* UserManager um = UserManager.getInstance();
        User test = um.createUser("test");
        test.setPassword("test");
        Group foos = um.createGroup("foos");
        Group bars = um.createGroup("bars");
        Group bazs = um.createGroup("bazs");
        test.addToGroup(foos);
        test.addToGroup(bars);
        test.addToGroup(bazs);

        data.getMessages().setMessage(
            "",
            "INFO",
            "User test/test is setup in system.  Don't forget to login!");
            */
        data.getMessages().setMessage(
                "",
                "INFO",
                "Please uncomment the code in WorkflowAction.doSetupuser and add OSUser!");
    }

    /**
     *  Create a new Workflow instance.
     *
     * @param  data           Current RunData information
     * @param  context        Context to populate
     * @exception  Exception  Thrown on error
     */
    public void doNew(RunData data, Context context) throws Exception
    {
        log.debug("doNew action event called");
        data.setScreenTemplate("Index.vm");
        try
        {
            Workflow wf =
			WorkflowServiceFacade.retrieveWorkflow(data.getUser().getName());
            long id = wf.initialize("example", 1, null);
            data.getMessages().setMessage(
                "",
                "INFO",
                "New Workflow id " + id + " created and initialized!");
        }
        catch (Exception e)
        {
            log.error(e);
            data.getMessages().setMessage("", "ERROR", e.getMessage());
        }
    }
    /**
    *  View the details of a specific workflow instance.
    *
    * @param  data           Current RunData information
    * @param  context        Context to populate
    * @exception  Exception  Thrown on error
    */
    public void doViewdetail(RunData data, Context context) throws Exception
    {
        log.debug("doViewdetail action event called");
        data.setScreenTemplate("WorkflowDetail.vm");
        try
        {
            context.put("wf", getWorkflowInstance(data, context));
        }
        catch (Exception e)
        {
            log.error(e);
            data.getMessages().setMessage("", "ERROR", e.getMessage());
        }
    }
  
    /**
    *  Perform an action for a workflow that moves it from one state
    * to the next.
    *
    * @param  data           Current RunData information
    * @param  context        Context to populate
    * @exception  Exception  Thrown on error
    */
    public void doAction(RunData data, Context context) throws Exception
    {
        log.debug("doAction action event called");
        try
        {
            WorkflowInstance wf = getWorkflowInstance(data, context);
            int action = data.getParameters().getInt("actionId");
            wf.doAction(action, Collections.EMPTY_MAP);

        }
        catch (Exception e)
        {
            log.error(e);
            data.getMessages().setMessage("", "ERROR", e.getMessage());
        }
        doViewdetail(data, context);
    }

    /**
     * Look up a workflow by the parameter "id".  Creates a workflow
     * instance object to facilitate looking things up.
     *
     * @param  data           Current RunData information
     * @param  context        Context to populate
     * @return a populated workflow instance
     */
    protected WorkflowInstance getWorkflowInstance(
        RunData data,
        Context context)
    {
        long workflowId = data.getParameters().getLong("id");
        Workflow workflow =
            WorkflowServiceFacade.retrieveWorkflow(data.getUser().getName());
        WorkflowInstance wf = new WorkflowInstance(workflow, workflowId);
        return wf;
    }
}
