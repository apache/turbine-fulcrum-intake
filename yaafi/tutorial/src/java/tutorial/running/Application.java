package tutorial.running;

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


import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.fulcrum.yaafi.cli.Main;

/**
 * Test suite for exercising the command line integration.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public class Application implements Runnable
{
    /** the YAAFI command line interface */
    private Main cli;

    /**
     * Main routine
     * @param args the command line arguments
     */
    public static void main( String[] args )
    {
        try
        {
            new Application(args).init().run();
        }
        catch( Throwable t )
        {
            String msg = "Execution of the server failed : " + t.getMessage();
            System.err.println(msg);
        }
    }

    /**
     * Constructor
     *
     * @param args the command line parameters
     */
    public Application(String[] args)
    {
        this.cli = new Main(args);
    }

    /**
     * Initialize the application
     *
     * @return the initialized instance
     * @throws Exception the initialization failed
     */
    protected Application init() throws Exception
    {
        // 1) initialize the YAAFI Main class

        // 1.1) set the temp directory to be used
        this.cli.setTempHome( "./tutorial/temp" );

        // 1.2) set the container configuration to bootstrap the YAAFI container
        this.cli.setContainerConfigValue( "./tutorial/conf/containerConfiguration.xml" );

        // 1.3) block the main thread until the JVM is terminated
        this.cli.setIsBlocking(true);

        // 1.4) install a JVM shutdown hook to dispose the YAAFI container
        this.cli.setHasShutdownHook(true);

        // 2) initialize the console logger

        ConsoleLogger consoleLogger = new ConsoleLogger(ConsoleLogger.LEVEL_DEBUG);
        this.cli.setLogger( consoleLogger );

        return this;
    }

    /**)
     * @see java.lang.Runnable#run()
     */
    public void run()
    {
		try
        {
            this.cli.initialize();
            this.cli.getLogger().info( "The application is up and running ..." );
            this.cli.onWait();
            this.cli.getLogger().info( "The application is terminating ..." );
        }
        catch (Throwable t)
        {
            String msg = "Running the server failed due to : " + t.getMessage();
            this.cli.getLogger().error(msg,t);
            throw new RuntimeException(msg);
        }
    }
 }
