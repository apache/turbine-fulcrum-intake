package org.apache.fulcrum.schedule.turbine.nonpersistent;
/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
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
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.fulcrum.schedule.Job;
import org.apache.fulcrum.schedule.ScheduleService;
import org.apache.fulcrum.schedule.turbine.JobEntry;
import org.apache.fulcrum.schedule.turbine.TurbineScheduler;
/**
 * Service for a cron like scheduler that uses the
 * avalon component config XML file instead of the database.
 * The methods that operate on jobs ( get,add,update,remove )
 * only operate on the queue in memory and changes are not reflected
 * to the properties file which was used to initilize the jobs.
 *
 *
 
 * @author <a href="mailto:ekkerbj@netscpae.net">Jeff Brekke</a>
 * @author <a href="mailto:john@zenplex.com">John Thorhauer</a>
 */
public class NonPersistentSchedulerService
    extends TurbineScheduler
    implements ScheduleService, Configurable, Initializable, ThreadSafe
{
    /** Key Prefix for our jobs */
    private static final String JOBS = "jobs";
    
	/** Key Prefix for our scheduler */
	private static final String SCHEDULER_NAME = "NonPersistentSchedulerService";
    /**
     * Constructor.
     *
     * @exception Exception, a generic exception.
     */
    public NonPersistentSchedulerService() throws Exception
    {
        super();
    }
    protected List loadJobs(Configuration conf) throws ConfigurationException
    {
        List jobs = new ArrayList();
        final Configuration jobsConf = conf.getChild(SCHEDULER_NAME).getChild(JOBS, false);
        if (jobsConf != null)
        {
            Configuration[] jobConf = jobsConf.getChildren();
            for (int i = 0; i < jobConf.length; i++)
            {
                String classname = jobConf[i].getChild("classname").getValue();
				int id = jobConf[i].getChild("id").getValueAsInteger();
                int second = jobConf[i].getChild("second").getValueAsInteger();
                int minute = jobConf[i].getChild("minute").getValueAsInteger();
                int hour = jobConf[i].getChild("hour").getValueAsInteger();
                int weekday = jobConf[i].getChild("weekday").getValueAsInteger();
                int dayofmonth = jobConf[i].getChild("dayofmonth").getValueAsInteger();                
                try
                {
                    JobEntry je = new JobEntry(second, minute, hour, weekday, dayofmonth, classname);
                    je.setJobId(id);
                    jobs.add(je);
                }
                catch (Exception e)
                {
                    getLogger().error("Problem creating JobEntry", e);
                    throw new ConfigurationException(e.getMessage());
                }
            }
        }
        return jobs;
    }
    /**
     * This method returns the job element from the internal queue.
     *
     * @param oid The int id for the job.
     * @return A JobEntry.
     * @exception Exception, a generic exception.
     */
    public Job getJob(Object id) throws Exception
    {
        Integer intObject = (Integer) id;
        JobEntry je = new JobEntry();
        je.setJobId(intObject.intValue());
        return scheduleQueue.getJob(je);
    }
    /**
     * Add a new job to the queue.
     *
     * @param je A JobEntry with the job to add.
     * @exception Exception, a generic exception.
     */
    public void addJob(Job je) throws Exception
    {
        // Add to the queue.
        scheduleQueue.add((JobEntry) je);
        restart();
    }
    /**
     * Remove a job from the queue.
     *
     * @param je A JobEntry with the job to remove.
     * @exception Exception, a generic exception.
     */
    public void removeJob(Job je) throws Exception
    {
        // Remove from the queue.
        scheduleQueue.remove((JobEntry) je);
        restart();
    }
    /**
     * Modify a Job.
     *
     * @param je A JobEntry with the job to modify
     * @exception Exception, a generic exception.
     */
    public void updateJob(Job je) throws Exception
    {
        try
        {
            ((JobEntry) je).calcRunTime();
        }
        catch (Exception e)
        {
            // Log problems.
            getLogger().error("Problem updating Scheduled Job: " + e);
        }
        // Update the queue.
        scheduleQueue.modify((JobEntry) je);
        restart();
    }
}
