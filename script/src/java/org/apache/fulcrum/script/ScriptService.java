package org.apache.fulcrum.script;

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
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * An Avalon service to execute scripts based on JSR-223.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */
public interface ScriptService
{
    /**
     * @return the underlying ScriptEngineManager.
     */
    ScriptEngineManager getScriptEngineManager();

    /**
     * @return the underlying default ScriptEngine.
     */
    ScriptEngine getScriptEngine();

    /**
     * Does the script exist?
     *
     * @param scriptName the name of the script
     * @return true if the script exists
     */
    boolean exists(String scriptName);

    /**
     * Executes the specified script. The default ScriptContext
     * for the ScriptEngine  is used.
     *
     * @param scriptName the name of the script
     * @return result from the executed script
     * @throws IOException loading the script failed
     * @throws ScriptException if an error occurrs during script execution.
     */
    Object eval(String scriptName) throws IOException, ScriptException;

    /**
     * Executes the script using the Namespace argument
     * as the ENGINE_SCOPE  Namespace of the ScriptEngine
     * during the script execution.
     *
     * @param scriptName the name of the script
     * @param bindings The binding of attributes
     * @return result from the executed script
     * @throws IOException loading the script failed
     * @throws ScriptException if an error occurs during script execution.
     */
    Object eval(String scriptName, Bindings bindings) throws IOException, ScriptException;

    /**
     * Causes the immediate execution of the script. State left in the engine
     * from previous executions, including variable values
     * and compiled procedures may be visible during this
     * execution.
     *
     * @param scriptName the name of the script
     * @param context The ScriptContext passed to the script engine.
     * @return result from the executed script
     * @throws IOException loading the script failed
     * @throws ScriptException if an error occurs during script execution.
     */
    Object eval(String scriptName, ScriptContext context) throws IOException, ScriptException;

    /**
     * Calls a procedure compiled during a previous script execution,
     * which is retained in the state of the ScriptEngine.
     *
     * @param name The name of the procedure to be called.
     * @param args An array of arguments to pass to the procedure.
     * @return The value returned by the procedure
     * @throws ScriptException if an error occurrs during script execution
     * @throws NoSuchMethodException If method with given name or matching argument types cannot be found.
     */
    Object call(String name, Object[] args) throws ScriptException, NoSuchMethodException;

    /**
     * Return an object implementing the given interface. It
     * uses the default scripting engine defined in the role
     * configuration. This method only works if it is supported
     * by underlying scripting engine, e.g. Javascript but not
     * Groovy, by implementing the interface <bold>Invocable</bold>
     *
     * @param clazz the interface to implement
     * @return an scripted object implementing the given interface.
     */
    Object getInterface(Class clazz);
}
