package org.apache.fulcrum.groovy.impl;

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

import org.apache.avalon.framework.logger.Logger;

import groovy.lang.Script;


/**
 * Simple cache for Groovy scripts.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */
public class GroovyScriptCache
{
    /** mapping from script name to cached scripts */
    private Hashtable scriptCache;

    /** the logger to use */
    private Logger logger;

    /**
     * Constructor
     * @param logger the Avalon logger
     */
    public GroovyScriptCache( Logger logger )
    {
        this.logger = logger;
        this.scriptCache = new Hashtable();
    }

    /**
     * Does the cache contains the script?.
     * @param scriptName the name of the script
     * @return true if the script is already in the cache
     */
    public synchronized boolean contains( String scriptName )
    {
        return this.getScriptCache().containsKey( scriptName );
    }

    /**
     * Puts a new script into the cache.
     * @param scriptName the name of the script
     * @param script the Groovy script
     */
    public synchronized void put( String scriptName, Script script )
    {
        GroovyScriptCacheEntry entry = new GroovyScriptCacheEntry(
            scriptName,
            script,
            this.getLogger()
            );

        this.getScriptCache().put( scriptName, entry );
    }

    /**
     * Aquire the semaphore to lock the script.
     * @param scriptName the name of the script
     * @return the cached Groovy script
     */
    public Script aquire( String scriptName )
    {
        Script result = null;
        GroovyScriptCacheEntry entry = this.getGroovyScriptCacheEntry( scriptName );
        entry.acquire();
        result = entry.getScript();
        return result;
    }

    /**
     * Release the semaphore to unlock the script.
     * @param scriptName the name of the script
     */
    public void release( String scriptName )
    {
        GroovyScriptCacheEntry entry = this.getGroovyScriptCacheEntry( scriptName );

        if( entry != null )
        {
            entry.release();
        }
    }

    /**
     * Clears the cache.
     */
    public synchronized void clear()
    {
        this.getScriptCache().clear();
    }

    /**
     * @param name the name of the script
     * @return the GroovyScriptCacheEntry
     */
    private synchronized GroovyScriptCacheEntry getGroovyScriptCacheEntry( String name )
    {
        return (GroovyScriptCacheEntry) this.getScriptCache().get( name );
    }

    /**
     * @return Returns the scriptCache.
     */
    private Hashtable getScriptCache()
    {
        return scriptCache;
    }

    /**
     * @return Returns the logger.
     */
    private Logger getLogger()
    {
        return logger;
    }
}
