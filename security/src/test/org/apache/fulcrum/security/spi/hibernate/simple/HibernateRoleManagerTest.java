/*
 * Created on Aug 23, 2003
 *
 */
package org.apache.fulcrum.security.spi.hibernate.simple;
import net.sf.hibernate.avalon.HibernateService;
import org.apache.fulcrum.security.SecurityService;
import org.apache.fulcrum.security.model.simple.manager.AbstractRoleManagerTest;
import org.apache.fulcrum.security.model.simple.manager.SimpleRoleManager;
import org.apache.fulcrum.security.spi.hibernate.HibernateHelper;
/**
 * @author Eric Pugh
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class HibernateRoleManagerTest extends AbstractRoleManagerTest
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
            roleManager = (SimpleRoleManager) securityService.getRoleManager();
			((BaseHibernateManager) roleManager).setHibernateSession(hibernateService.openSession());
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
            ((BaseHibernateManager) roleManager).getHibernateSession().close();
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
        role = null;
        roleManager = null;
        securityService = null;
    }
    /**
    	   * Constructor for HibernatePermissionManagerTest.
    	   * @param arg0
    	   */
    public HibernateRoleManagerTest(String arg0)
    {
        super(arg0);
    }
}
