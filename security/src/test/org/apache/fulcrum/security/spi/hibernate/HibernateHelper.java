/*
 * Created on Aug 23, 2003
 *
 */
package org.apache.fulcrum.security.spi.hibernate;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.cfg.Configuration;
import net.sf.hibernate.tool.hbm2ddl.SchemaExport;
/**
 * @author Eric Pugh
 *
 * This class allows us to dynamically populate the hsql database with our schema.
 */
public class HibernateHelper
{
    private static SessionFactory sessions;
    /**
     * @return
     */
    public static SessionFactory getSessions()
    {
        return sessions;
    }

    public static void exportSchema(Configuration cfg) throws Exception
    {
        
        new SchemaExport(cfg).create(true, true);
        sessions = cfg.buildSessionFactory();
    }
}
