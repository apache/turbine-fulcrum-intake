package org.apache.fulcrum.schedule.turbine.nonpersistent;

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

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.avalon.framework.component.ComponentException;
import org.apache.fulcrum.schedule.ScheduleService;
import org.apache.fulcrum.schedule.turbine.JobEntry;
import org.apache.fulcrum.schedule.turbine.SimpleJob;
import org.apache.fulcrum.testcontainer.BaseUnitTest;

/**
 * Unit testing for the non-persistent implementation of the scheduler service.
 *
 * @author <a href="mailto:epugh@upstate.com">epugh@upstate.com</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 */
public class NonPersistentSchedulerServiceTest extends BaseUnitTest
{
	private ScheduleService scheduler = null;

	public NonPersistentSchedulerServiceTest(String name)
	{
		super(name);
	}

    public static Test suite()
    {
        return new TestSuite(NonPersistentSchedulerServiceTest.class);
    }
	public void setUp() throws Exception
	  {
		  super.setUp();		  
		  scheduler = (ScheduleService) this.lookup(ScheduleService.ROLE);
		  
	  }
    /**
     * Tests the ability to enable and disable the service.
     */
    public void testEnableDisable()
    {
        try
        {
        	/*
            TurbineScheduler.startScheduler();
            assertEquals(true, TurbineScheduler.isEnabled());

            TurbineScheduler.stopScheduler();
            assertEquals(false, TurbineScheduler.isEnabled());
            */
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * Tests the ability to add and remove a job.  A list of jobs will be obtained from
     * the service to determine if the operation were successful.
     */
    public void testAddRemoveJob()
    {
        try
        {
            // get the current job count for later comparison
            int jobCount = scheduler.listJobs().size();

            // Add a new job entry
            JobEntry je = new JobEntry();
            je.setJobId(jobCount + 1);
            je.setSecond(0);
            je.setMinute(1);
            je.setHour(-1);
            je.setDayOfMonth(-1);
            je.setWeekDay(-1);
            je.setTask("SimpleJob");

			scheduler.addJob(je);
            assertEquals(jobCount + 1, scheduler.listJobs().size());

			scheduler.removeJob(je);
            assertEquals(jobCount, scheduler.listJobs().size());

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * Tests the ability to retrieve the job added during initialization.
     */
    public void testGetJob() throws Exception
    {
            Integer jobId = new Integer(1);
            JobEntry je = (JobEntry)scheduler.getJob(jobId);
            assertNotNull("Could not find job, maybe not loaded.",je);
            assertEquals(je.getJobId(), 1);
            assertEquals(je.getSecond(), 10);
            assertEquals(je.getMinute(), -1);
            assertEquals(je.getHour(), -1);
            assertEquals(je.getDayOfMonth(), -1);
            assertEquals(je.getWeekDay(), -1);
            assertEquals(je.getTask(), SimpleJob.class.getName());
    }

    /** Test to make sure a job actually runs.
     *
     */
    public void testRunningJob() throws Exception
    {
           int beforeCount = SimpleJob.getCounter();
           Thread.sleep(12000);
           int afterCount = SimpleJob.getCounter();
           assertTrue(beforeCount < afterCount);
    }

}
