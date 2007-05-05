package org.apache.fulcrum.groovy;

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

import java.io.IOException;

import org.codehaus.groovy.control.CompilationFailedException;

/**
 * An Avalon service to execute Groovy scripts.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */
public interface GroovyService
{
    /**
     * Does the script exits?
     *
     * @param scriptName the name of the script
     * @return true if the script exists
     */
    boolean exists( String scriptName );

    /**
     * Executes a Groovy script.
     *
     * @param scriptName the name of the script
     * @param args the arguments passed to the script
     * @return result from the executed Groovy script
     * @throws CompilationFailedException the script failed to compile
     * @throws IOException the script couldn't be loaded
     */
    Object execute( String scriptName, Object[] args )
        throws CompilationFailedException, IOException;

    /**
     * Create a GroovyRunnable. You need to set the arguments
     * before you run it.
     *
     * @param scriptName the name of the script
     * @return the runnable
     */
    GroovyRunnable createGroovyRunnable( String scriptName );

    /**
     * Compiles a Groovy script.
     *
     * @param scriptName the name of tje scipt to compile
     * @param scriptContent the name  scipt to compile
     * @throws IOException error parsing the script file
     * @throws CompilationFailedException failed to compile the script
     */
    void compile( String scriptName, String scriptContent )
        throws IOException, CompilationFailedException;
}
