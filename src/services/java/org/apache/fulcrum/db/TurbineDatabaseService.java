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

import org.apache.fulcrum.BaseService;
import org.apache.fulcrum.InitializationException;
import org.apache.fulcrum.TurbineServices;
import org.apache.torque.Torque;
import org.apache.torque.TorqueException;
import org.apache.torque.adapter.DB;
import org.apache.torque.map.DatabaseMap;
import org.apache.torque.pool.DBConnection;

/**
 * Turbine's default implementation of {@link DatabaseService}.
 *
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @version $Id$
 */
public class TurbineDatabaseService
    extends BaseService
    implements DatabaseService
{
    /**
     * Initializes the service.
     */
    public void init()
        throws InitializationException
    {
        Torque.setConfiguration(getConfiguration());
        Torque.setCategory(getCategory());

        try
        {
            Torque.init();
        }
        catch (Exception e)
        {
            throw new InitializationException("Can't initialize Torque!", e);
        }

        // indicate that the service initialized correctly
        setInit(true);
    }

    /**
     * Shuts down the service.
     *
     * This method halts the IDBroker's daemon thread in all of
     * the DatabaseMap's.
     */
    public void shutdown()
    {
        Torque.shutdown();
    }

    /**
     * Returns the default database map information.
     *
     * @return A DatabaseMap.
     * @throws ServiceException Any exceptions caught during procssing will be
     *         rethrown wrapped into a ServiceException.
     */
    public DatabaseMap getDatabaseMap()
        throws Exception
    {
        return Torque.getDatabaseMap();
    }

    /**
     * Returns the database map information. Name relates to the name
     * of the connection pool to associate with the map.
     *
     * @param name The name of the <code>DatabaseMap</code> to
     * retrieve.
     * @return The named <code>DatabaseMap</code>.
     * @throws ServiceException Any exceptions caught during procssing will be
     *         rethrown wrapped into a ServiceException.
     */
    public DatabaseMap getDatabaseMap(String name)
        throws Exception
    {
        return Torque.getDatabaseMap(name);
    }

    /**
     * This method returns a DBConnection from the default pool.
     *
     * @return The requested connection.
     * @throws ServiceException Any exceptions caught during processing will be
     *         rethrown wrapped into a ServiceException.
     */
    public DBConnection getConnection()
        throws Exception
    {
        return Torque.getConnection();
    }

    /**
     * This method returns a DBConnection from the pool with the
     * specified name.  The pool must either have been registered
     * with the {@link #registerPool(String,String,String,String,String)}
     * method, or be specified in the property file using the
     * following syntax:
     *
     * <pre>
     * database.[name].driver
     * database.[name].url
     * database.[name].username
     * database.[name].password
     * </pre>
     *
     * @param name The name of the pool to get a connection from.
     * @return     The requested connection.
     * @throws ServiceException Any exceptions caught during processing will be
     *         rethrown wrapped into a ServiceException.
     */
    public DBConnection getConnection(String name)
        throws Exception
    {
        // The getPool method ensures the validity of the returned pool.
        return Torque.getConnection(name);
    }

    /**
     * This method returns a DBConnecton using the given parameters.
     *
     * @param driver The fully-qualified name of the JDBC driver to use.
     * @param url The URL of the database from which the connection is
     * desired.
     * @param username The name of the database user.
     * @param password The password of the database user.
     * @return A DBConnection.
     * @throws ServiceException Any exceptions caught during processing will be
     *         rethrown wrapped into a ServiceException.
     *
     * @deprecated Database parameters should not be specified each
     * time a DBConnection is fetched from the service.
     */
    public DBConnection getConnection(String driver,
                                      String url,
                                      String username,
                                      String password)
        throws Exception
    {
        return Torque.getConnection(driver, url, username, password);
    }

    /**
     * Release a connection back to the database pool.  <code>null</code>
     * references are ignored.
     *
     * @throws ServiceException Any exceptions caught during processing will be
     *         rethrown wrapped into a ServiceException.
     * @exception Exception A generic exception.
     */
    public void releaseConnection(DBConnection dbconn)
        throws Exception
    {
        Torque.releaseConnection(dbconn);
    }

    /**
     * This method registers a new pool using the given parameters.
     *
     * @param name The name of the pool to register.
     * @param driver The fully-qualified name of the JDBC driver to use.
     * @param url The URL of the database to use.
     * @param username The name of the database user.
     * @param password The password of the database user.
     *
     * @throws Exception Any exceptions caught during processing will be
     *         rethrown wrapped into a ServiceException.
     */
    public void registerPool( String name,
                              String driver,
                              String url,
                              String username,
                              String password )
        throws Exception
    {
        Torque.registerPool(name,driver,url,username,password);
    }

    /**
     * This thread-safe method registers a new pool using the given parameters.
     *
     * @param name The name of the pool to register.
     * @param driver The fully-qualified name of the JDBC driver to use.
     * @param url The URL of the database to use.
     * @param username The name of the database user.
     * @param password The password of the database user.
     * @exception Exception A generic exception.
     */
    public void registerPool( String name,
                              String driver,
                              String url,
                              String username,
                              String password,
                              int maxCons,
                              long expiryTime,
                              long maxConnectionAttempts,
                              long connectionWaitTimeout)
        throws Exception
    {
        Torque.registerPool(name,driver,url,username,password,maxCons,expiryTime,
            maxConnectionAttempts, connectionWaitTimeout);
    }

    /**
     * Returns the database adapter for the default connection pool.
     *
     * @return The database adapter.
     * @throws ServiceException Any exceptions caught during processing will be
     *         rethrown wrapped into a ServiceException.
     */
    public DB getDB()
        throws Exception
    {
        return Torque.getDB();
    }

    /**
     * Returns database adapter for a specific connection pool.
     *
     * @param name A pool name.
     * @return     The corresponding database adapter.
     * @throws ServiceException Any exceptions caught during processing will be
     *         rethrown wrapped into a ServiceException.
     */
    public DB getDB(String name)
        throws Exception
    {
        return Torque.getDB(name);
    }

    public String getDefaultDB()
    {
        return Torque.getDefaultDB();
    }

    public String getDefaultMap()
    {
        return Torque.getDefaultMap();
    }
}
