package org.apache.fulcrum.osworkflow.example.modules.actions;


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
