/*
 * Created on Aug 23, 2003
 *
 */
package org.apache.fulcrum.security.spi.hibernate.simple;
import net.sf.hibernate.avalon.HibernateService;

import org.apache.fulcrum.security.SecurityService;
import org.apache.fulcrum.security.model.simple.manager.AbstractUserManagerTest;
import org.apache.fulcrum.security.spi.hibernate.HibernateHelper;
/**
 * @author Eric Pugh
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class HibernateUserManagerTest extends AbstractUserManagerTest
{
    public void setUp()
    {
        try
        {
            this.setRoleFileName(null);
            this.setConfigurationFileName("src/test/SimpleHibernate.xml");
            HibernateService hibernateService = (HibernateService) lookup(HibernateService.ROLE);
            HibernateHelper.exportSchema(hibernateService.getConfiguration());
            securityService = (SecurityService) lookup(SecurityService.ROLE);
            userManager = securityService.getUserManager();
            
            ((BaseHibernateManager) userManager).setHibernateSession(hibernateService.openSession());
        }
        catch (Exception e)
        {
            fail(e.toString());
        }
    }
    public void tearDown()
    {
        try
        {
            ((BaseHibernateManager) userManager).getHibernateSession().close();
        }
        catch (Exception e)
        {
        	fail(e.getMessage());
        }
        user = null;
        userManager = null;
        securityService = null;
    }
    /**
    	   * Constructor for HibernatePermissionManagerTest.
    	   * @param arg0
    	   */
    public HibernateUserManagerTest(String arg0)
    {
        super(arg0);
    }
}
