package org.apache.fulcrum.db;

import org.apache.plexus.PlexusTestCase;

import cirrus.hibernate.Datastore;
import cirrus.hibernate.Session;
import cirrus.hibernate.SessionFactory;
import cirrus.hibernate.tools.SchemaExport;

/**
 * HibernateServiceTest
 * 
 * @author <a href="epugh@upstate.com">Eric Pugh</a>
 * @since Jan 27, 2003
 */
public class HibernateServiceTest extends PlexusTestCase
{

    protected static SessionFactory sessions;
   

    public HibernateServiceTest(String name)
    {
        super(name);
    }

    /**
     * This testcase loads up the labor object via the Labor.hbm.xml file and creates the
     * schema in an inmemory hsql database.  It then tests save and loading a Labor object.
     * @throws Exception
     */
    public void testExecution() throws Exception
    {

        HibernateService hibernateService = (HibernateService) getComponent(HibernateService.ROLE);

        Datastore ds = hibernateService.getDatastore();

        new SchemaExport(ds).create(true, true);

        Labor labor = new Labor();
        Labor labor2 = new Labor();
        labor.setOperator("Eric Pugh");
        labor2.setOperator("John Doe");

        Session s = hibernateService.openSession();
        // Session s = sessions.openSession();
        //Transaction t = s.beginTransaction();


        s.save(labor);
        s.save(labor2);
        
        
        //t.commit();
        s.flush();
        s.close();
        System.out.println("labor:" + labor.getOperator() + "id:" + labor.getId());
        
     
        assertTrue(labor.getId() == 0);
        assertTrue(labor2.getId() == 1);
        assertTrue(labor.getId() != labor2.getId());
        
        s = hibernateService.openSession();
        Labor newLabor = (Labor)s.load(Labor.class,new Integer(labor.getId()));
        assertEquals(newLabor.getId(),labor.getId());

    }

}
