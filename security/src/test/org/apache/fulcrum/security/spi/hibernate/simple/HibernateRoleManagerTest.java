/*
 * Created on Aug 23, 2003
 *
 */
package org.apache.fulcrum.security.spi.hibernate.simple;
import net.sf.hibernate.avalon.HibernateService;

import org.apache.fulcrum.security.model.simple.manager.AbstractRoleManagerTest;
import org.apache.fulcrum.security.spi.hibernate.HibernateHelper;
/**
 * @author Eric Pugh
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class HibernateRoleManagerTest extends AbstractRoleManagerTest
{
    public void doCustomSetup() throws Exception
    {
        this.setRoleFileName(null);
        this.setConfigurationFileName("src/test/SimpleHibernate.xml");
        HibernateService hibernateService = (HibernateService) lookup(HibernateService.ROLE);
        HibernateHelper.exportSchema(hibernateService.getConfiguration());
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
