package org.apache.fulcrum.security.model.turbine.test;
/*
 *  Copyright 2001-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import org.apache.fulcrum.security.entity.Group;
import org.apache.fulcrum.security.model.dynamic.test.AbstractDynamicModelManagerTest;
import org.apache.fulcrum.security.model.turbine.TurbineModelManager;


/**
 * @author Eric Pugh
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public abstract class AbstractTurbineModelManagerTest extends AbstractDynamicModelManagerTest
{
   
    /**
     * Constructor for AbstractTurbineModelManagerTest.
     * @param arg0
     */
    public AbstractTurbineModelManagerTest(String arg0)
    {
        super(arg0);
    }
   

	public void testGetGlobalGroup() throws Exception
	{
		TurbineModelManager tgm = (TurbineModelManager)securityService.getModelManager();
		Group global =tgm.getGlobalGroup();
		assertNotNull(global);
		assertEquals(global.getName(),TurbineModelManager.GLOBAL_GROUP_NAME);
	}
}
