package org.apache.fulcrum.osworkflow.example.modules.actions;
import java.util.Collections;

import org.apache.avalon.framework.component.ComponentException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.osworkflow.WorkflowInstance;
import org.apache.fulcrum.osworkflow.WorkflowService;
import org.apache.turbine.modules.actions.VelocityAction;
import org.apache.turbine.services.InstantiationException;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.avaloncomponent.AvalonComponentService;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;
import com.opensymphony.module.user.Group;
import com.opensymphony.module.user.User;
import com.opensymphony.module.user.UserManager;
import com.opensymphony.workflow.Workflow;
/**
 * @author     <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @created    October 11, 2002
 */
public class WorkflowAction extends VelocityAction
{
    private static Log log = LogFactory.getLog(WorkflowAction.class);
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
        System.out.println("Doperform called");
        data.setScreenTemplate("Index.vm");
    }
    
	/**
		 *  This sets logs you in as user "test"
		 *
		 * @param  data           Current RunData information
		 * @param  context        Context to populate
		 * @exception  Exception  Thrown on error
		 */
		public void doLogin(RunData data, Context context) throws Exception
		{
			data.getUser().setName("test");
	
        
			data.getMessages().setMessage("", "INFO", "You are logged in as user test!");
		}
    
    /**
     *  This sets up the user "test/test"
     *
     * @param  data           Current RunData information
     * @param  context        Context to populate
     * @exception  Exception  Thrown on error
     */
    public void doSetupuser(RunData data, Context context) throws Exception
    {
	
        data.setScreenTemplate("Index.vm");
        UserManager um = UserManager.getInstance();
        User test = um.createUser("test");
        test.setPassword("test");
        Group foos = um.createGroup("foos");
        Group bars = um.createGroup("bars");
        Group bazs = um.createGroup("bazs");
        test.addToGroup(foos);
        test.addToGroup(bars);
        test.addToGroup(bazs);
        
        data.getMessages().setMessage("", "INFO", "User test/test is setup in system.  Don't forget to login!");
    }
    
    
    /**
     *  Create a new Workflow
     *
     * @param  data           Current RunData information
     * @param  context        Context to populate
     * @exception  Exception  Thrown on error
     */
    public void doNew(RunData data, Context context) throws Exception
    {
        System.out.println("doNew called");
        data.setScreenTemplate("Index.vm");
        try
        {
            Workflow wf = getWorkflowService().retrieveWorkflow(data.getUser().getName());
            long id = wf.initialize("example", 1, null);
            data.getMessages().setMessage("", "INFO", "New Workflow id " + id + " created and initialized!");
        }
        catch (Exception e)
        {
            log.error(e);
            data.getMessages().setMessage("", "ERROR", e.getMessage());
        }
    }
    /**
        *  Create a new Workflow
        *
        * @param  data           Current RunData information
        * @param  context        Context to populate
        * @exception  Exception  Thrown on error
        */
    public void doViewdetail(RunData data, Context context) throws Exception
    {
        System.out.println("doViewdetail called");
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
        * Lazy load the WorkflowService.
        * @return a fulcrum WorkflowService
        */
    public WorkflowService getWorkflowService()
    {
        if (workflowService == null)
        {
            AvalonComponentService acs =
                (AvalonComponentService) TurbineServices.getInstance().getService(AvalonComponentService.SERVICE_NAME);
            try
            {
                workflowService = (WorkflowService) acs.lookup(WorkflowService.ROLE);
            }
            catch (ComponentException ce)
            {
                throw new InstantiationException("Problem looking up Workflow Service:" + ce.getMessage());
            }
        }
        return workflowService;
    }
    /**
        *  Perform an action
        *
        * @param  data           Current RunData information
        * @param  context        Context to populate
        * @exception  Exception  Thrown on error
        */
    public void doAction(RunData data, Context context) throws Exception
    {
        
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
        doViewdetail(data,context);
    }
    protected WorkflowInstance getWorkflowInstance(RunData data, Context context)
    {
        long workflowId = data.getParameters().getLong("id");
        Workflow workflow = getWorkflowService().retrieveWorkflow(data.getUser().getName());
        WorkflowInstance wf = new WorkflowInstance(workflow, workflowId);
        return wf;
    }
}
