package org.apache.fulcrum.security.hibernate;
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

import net.sf.hibernate.avalon.HibernateService;
import net.sf.hibernate.avalon.HibernateServiceImpl;

import org.apache.fulcrum.security.SecurityService;

import org.apache.fulcrum.testcontainer.BaseUnitTest;
/**
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class PersistenceHelperTest extends BaseUnitTest
{

    public void tearDown()
    {

    }
    /**
    	   * Constructor for HibernatePermissionManagerTest.
    	   * @param arg0
    	   */
    public PersistenceHelperTest(String arg0)
    {
        super(arg0);
    }

    public void testPassingInExternalHibernateService() throws Exception
    {
        this.setRoleFileName(null);
        this.setConfigurationFileName("src/test/DynamicHibernate.xml");
        HibernateService hibernateService =
            (HibernateService) lookup(HibernateService.ROLE);
        HibernateHelper.exportSchema(hibernateService.getConfiguration());

        HibernateService hibernateService2 = new HibernateServiceImpl();
        
        assertNotSame(hibernateService,hibernateService2);
        
		SecurityService securityService = (SecurityService) lookup(SecurityService.ROLE);
		HibernateGroupManagerImpl groupManager = (HibernateGroupManagerImpl)securityService.getGroupManager();
		assertSame(hibernateService,groupManager.getPersistenceHelper().getHibernateService());
		groupManager.getPersistenceHelper().setHibernateService(hibernateService2);
		assertSame(hibernateService2,groupManager.getPersistenceHelper().getHibernateService());
		
		HibernateRoleManagerImpl roleManager = (HibernateRoleManagerImpl)securityService.getRoleManager();
		assertSame(hibernateService2,roleManager.getPersistenceHelper().getHibernateService());
		assertNotSame(hibernateService,roleManager.getPersistenceHelper().getHibernateService());
		roleManager.getPersistenceHelper().setHibernateService(hibernateService);
		assertSame(roleManager.getPersistenceHelper().getHibernateService(),groupManager.getPersistenceHelper().getHibernateService());
		
		roleManager = (HibernateRoleManagerImpl)securityService.getRoleManager();
		assertSame(roleManager.getPersistenceHelper().getHibernateService(),groupManager.getPersistenceHelper().getHibernateService());
    }
}
