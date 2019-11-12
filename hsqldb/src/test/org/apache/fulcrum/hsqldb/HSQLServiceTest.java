package org.apache.fulcrum.hsqldb;

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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.avalon.framework.activity.Startable;
import org.apache.fulcrum.testcontainer.BaseUnitTest;

/**
 * HSQLServiceTest
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */
public class HSQLServiceTest extends BaseUnitTest
{
	private HSQLService service;

    /**
     * Defines the testcase name for JUnit.
     *
     * @param name the testcase's name.
     */
    public HSQLServiceTest(String name)
    {
        super(name);
    }

    protected void setUp() throws Exception
    {
        super.setUp();

        try
        {
            Class.forName("org.hsqldb.jdbcDriver");
            service = (HSQLService) this.lookup(HSQLService.class.getName());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    protected Connection getConnection(String db) throws Exception
    {
        String connectionString = "jdbc:hsqldb:hsql://localhost:9001/" + db;
        return DriverManager.getConnection(
            connectionString,
            "sa",
            ""
            );
    }
    /**
     * Simple test that verify the select from the HSQLDB server.
     *
     * @throws Exception the test failed
     */
    public void testMe() throws Exception
    {
        Connection conn = null;

        try
        {
            assertNotNull( "HSQL Service must be available", service);

            conn = this.getConnection("test");
            Statement stmt = conn.createStatement();
            stmt.execute("SELECT * FROM TURBINE_USER;");
            ResultSet rs = stmt.getResultSet();

            while( rs.next() )
            {
                String loginName = rs.getString("LOGIN_NAME");
                System.out.println("Found the following user : " + loginName);
                assertTrue( loginName.length() > 0 );
            }

            rs.close();
            stmt.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
        finally
        {
            if( conn != null )
            {
                conn.close();
            }
        }
    }

    public void testIsOnline() throws Exception
    {
        assertTrue("Server is online", service.isOnline());
        ((Startable) service).stop();
        assertFalse("Server is offline", service.isOnline());
    }

    /**
     * Terminate the embedded HSQLDB server using the "SHUTDOWN" command.
     *
     * @throws Exception the test failed
     */
    public void testShutdown() throws Exception
    {
        Connection conn = this.getConnection("test");
        Statement stmt = conn.createStatement();
        stmt.execute("SHUTDOWN;"); // IMMEDIATELY does not change time
        // sql shutdown seems to return immediately and not to wait: https://sourceforge.net/p/hsqldb/bugs/1400/ ?
        Thread.sleep(250);
        assertFalse("Server is still running "+ service.toString(), (service).isOnline());
    }
    
}
