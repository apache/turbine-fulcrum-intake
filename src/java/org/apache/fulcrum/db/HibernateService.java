package org.apache.fulcrum.db;

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

import java.sql.Connection;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.component.Component;
import org.apache.torque.Torque;
import org.apache.torque.adapter.DB;
import org.apache.torque.map.DatabaseMap;
import net.sf.hibernate.*;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.cfg.Configuration;
import net.sf.hibernate.dialect.*;
import net.sf.hibernate.tool.hbm2ddl.SchemaExport;
import net.sf.hibernate.HibernateException;

/**
 * Fulcrum's default implementation of the Hibernate Object Modeling tool.
 *
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class HibernateService extends AbstractLogEnabled implements Initializable, Configurable, Startable, Component
{
    private String xmlFile;
    private net.sf.hibernate.cfg.Configuration configuration;
    private SessionFactory sessionFactory;

    /** Avalon role - used to id the component within the manager */
    public static final String ROLE = "org.apache.fulcrum.db.HibernateService";

    public static final String SERVICE_NAME = "HibernateService";
    public static final String XML_FILE = "xmlFile";
    public net.sf.hibernate.cfg.Configuration getConfiguration()
    {
        return configuration;
    }

    /** 
     * returns a Hibernate Session object that can then be used from now on! 
     * 
     */
    public Session openSession() throws HibernateException
    {
        return sessionFactory.openSession();
    }



    /**
     * Initializes the service by setting up Hibernate.
     */
    public void initialize() throws Exception
    {
        try
        {
            
            //getLogger().info("Preparing HibernateService with xml file " + xmlFile);
            configuration =new net.sf.hibernate.cfg.Configuration().configure();
            
            
            //      Then build a session to the database
            sessionFactory = configuration.buildSessionFactory();
        }
        catch (Exception e)
        {
            throw new Exception("Can't initialize Hibernate: " + e.getMessage());
        }
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(org.apache.avalon.framework.configuration.Configuration configuration) throws ConfigurationException
    {
        getLogger().info("Configuring HibernateService");
        xmlFile = configuration.getAttribute(XML_FILE, null);
        
        if (xmlFile == null){
            getLogger().warn("No XmlFile provided for configuration.");
        }

    }

    /**
     * @see org.apache.avalon.framework.activity.Startable#start()
     */
    public void start() throws Exception
    {
        // do nothing
    }

    /**
     * Shuts down the service.
     *
     *
     * @see org.apache.avalon.framework.activity.Startable#stop()
     */
    public void stop() throws Exception
    {}

}