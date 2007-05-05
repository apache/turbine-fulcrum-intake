package org.apache.fulcrum.script.impl;

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

import javax.script.CompiledScript;

/**
 * Keeping track of the metadata of a cached script.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */
public class ScriptCacheEntry
{
    /** the name of the scripting engine */
    private String engineName;

    /** the plain script */
    private String plainScript;

    /** the precompiled script */
    private CompiledScript compiledScript;

    /** the name of the script */
    private String scriptName;

    /**
     * Constructor.
     *
     * @param engineName the name of the script engine
     * @param scriptName the name of the script
     * @param plainScript the plain text script
     * @param compiledScript an optional compiled script
     */
    public ScriptCacheEntry( String engineName, String scriptName, String plainScript, CompiledScript compiledScript )
    {
        Validate.notEmpty(engineName, "engineName");
        Validate.notEmpty(scriptName, "scriptName");
        Validate.notEmpty(plainScript, "plainScript");

        this.engineName = engineName;
        this.scriptName = scriptName;
        this.plainScript = plainScript;
        this.compiledScript = compiledScript;
    }

    /**
     * @return Returns the compiledScript.
     */
    public CompiledScript getCompiledScript()
    {
        return compiledScript;
    }

    /**
     * @param compiledScript The compiledScript to set.
     */
    public void setCompiledScript(CompiledScript compiledScript)
    {
        this.compiledScript = compiledScript;
    }

    /**
     * @return Returns the plainScript.
     */
    public String getPlainScript()
    {
        return plainScript;
    }

    /**
     * @return Returns the scriptName.
     */
    public String getScriptName()
    {
        return scriptName;
    }

    /**
     * @return Returns the engineName.
     */
    public String getEngineName()
    {
        return engineName;
    }

    /**
     * Is the script already compiled
     */
    public boolean isCompiled()
    {
        return ( this.getCompiledScript() != null ? true : false );
    }
}
