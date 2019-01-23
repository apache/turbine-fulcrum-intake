package org.apache.fulcrum.xslt;

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

import org.apache.fulcrum.testcontainer.BaseUnit5Test;
import org.junit.jupiter.api.Test;

/**
 * Test the XSLTServiceFacade.
 *
 * @author <a href="epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class XSLTServiceFacadeTest extends BaseUnit5Test 
{

	/**
	 * @throws Exception generic exception
	 */
	@Test
	public void testWorkflowFacadeNotConfigured() throws Exception 
	{
		try 
		{
			XSLTServiceFacade.getService();
		} catch (RuntimeException re) {
			// good;
		}
	}

	/**
	 * @throws Exception generic exception
	 */
	@Test
	public void testWorkflowFacadeConfigured() throws Exception 
	{
		// this.lookup causes the workflow service to be configured.
		this.lookup(XSLTService.ROLE);
		XSLTServiceFacade.getService();
	}

}
