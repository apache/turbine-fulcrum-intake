/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 

package org.apache.fulcrum.quartz;

import java.util.Map;

import org.quartz.Scheduler;

/**
 * @author <a href="mailto:leandro@ibnetwork.com.br">Leandro Rodrigo Saad Cruz</a>
 *
 */
public interface QuartzScheduler 
{
	/** avalon idion */
	public static final String ROLE = QuartzScheduler.class.getName();
	
	Scheduler getScheduler();  
	
	/**
	 * Map containing JobDetail objects. Key is group.name
	 * @return
	 */
	Map getJobDetailsMap();
	
	/**
	 * Map containing Trigger objects. Key is group.name
	 * @return
	 */
	Map getTriggersMap();
}