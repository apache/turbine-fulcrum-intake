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

import java.util.Iterator;
import java.util.List;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.workflow.Workflow;
import com.opensymphony.workflow.WorkflowException;
import com.opensymphony.workflow.basic.BasicWorkflow;
import com.opensymphony.workflow.query.WorkflowQuery;

/**
 * This service provides a simple interface to the
 * OSWorkflow Engine.  You can also directly access
 * the OSWorkflow engine.
 *
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class DefaultWorkflowService
    extends AbstractLogEnabled
    implements WorkflowService, Configurable, Initializable, Disposable, ThreadSafe
{

	/** The log. */
	private static Log log = LogFactory.getLog(DefaultWorkflowService.class);
    /**
     * For a specific caller and status, return all the workflows.
     *
     * @param caller The name of the caller.
     * @param status The status of the workflows to retreive.  Definied by the workflow.xml file
     * @return An array of long's for the workflow ID's.
     */
    public long[] retrieveWorkflows(String caller, String status)
        throws WorkflowException
    {
        Workflow wf = retrieveWorkflow(caller);
        WorkflowQuery queryLeft =
            new WorkflowQuery(
                WorkflowQuery.OWNER,
                WorkflowQuery.CURRENT,
                WorkflowQuery.EQUALS,
                caller);
        WorkflowQuery queryRight =
            new WorkflowQuery(
                WorkflowQuery.STATUS,
                WorkflowQuery.CURRENT,
                WorkflowQuery.EQUALS,
                status);
        WorkflowQuery query =
            new WorkflowQuery(queryLeft, WorkflowQuery.AND, queryRight);
        List workflows = wf.query(query);
        long workflowIds[] = new long[workflows.size()];
        int counter = 0;
        for (Iterator i = workflows.iterator(); i.hasNext(); counter++)
        {
            Long workflowId = (Long) i.next();
            workflowIds[counter] = workflowId.longValue();
        }
        return workflowIds;
    }
    /**
     * Retrives a workflow based on the caller
     *
     * @param caller The workflow for this caller.
     */
    public Workflow retrieveWorkflow(String caller)
    {
        return new BasicWorkflow(caller);
    }

    // ---------------- Avalon Lifecycle Methods ---------------------
    /**
     * Avalon component configure lifecycle method
     */
    public void configure(Configuration conf) throws ConfigurationException
    {
    }
    /**
     * Avalon component initialize lifecycle method
     */
    public void initialize() throws Exception
    {
		WorkflowServiceFacade.setWorkflowService(this);
		if (log.isInfoEnabled())
        {
            log.info("OSWorkflow Service is Initialized now..");
        }
    }

    /**
     * Avalon component disposelifecycle method
     */
    public void dispose()
    {
    }
}
