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

import java.util.Hashtable;
import java.util.Map;

/**
 * Type-safe wrapper for storing ScriptCacheEntry.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */
public class ScriptCache
{
    /** mapping from script name to cached scripts */
    private Hashtable scriptCache;

    /**
     * Constructor
     */
    public ScriptCache()
    {
        this.scriptCache = new Hashtable();
    }

    /**
     * Does the cache contains the script?.
     * @param scriptName the name of the script
     * @return true if the script is already in the cache
     */
    public boolean contains( String scriptName )
    {
        return this.getScriptCache().containsKey( scriptName );
    }

    /**
     * Gets a cached script
     * @param scriptName the name of the script
     * @return the ScriptCacheEntry
     */
    public ScriptCacheEntry get( String scriptName )
    {
        return (ScriptCacheEntry) this.getScriptCache().get( scriptName );
    }

    /**
     * Puts a new script into the cache.
     * @param scriptCacheEntry the ScriptCacheEntry to add
     */
    public void put( ScriptCacheEntry scriptCacheEntry )
    {
        this.getScriptCache().put( scriptCacheEntry.getScriptName(), scriptCacheEntry );
    }

    /**
     * Clears the cache.
     */
    public void clear()
    {
        this.getScriptCache().clear();
    }

    /**
     * @return Returns the scriptCache.
     */
    private Map getScriptCache()
    {
        return scriptCache;
    }
}
