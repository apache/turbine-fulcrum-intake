package org.apache.fulcrum.groovy.impl;

/*
 * Copyright 2004 Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import org.apache.avalon.framework.logger.Logger;

import groovy.lang.Script;

/**
 * An entry of the GroovyScriptCache.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */
public class GroovyScriptCacheEntry
{
    /** the precompiled script */
    private Script script;

    /** the name of the script */
    private String name;

    /** the semaphore avoid multi-threading problems */
    private GroovySemaphore semaphore;

    /**
     * Constructor
     * @param name the name of the Groovy script
     * @param script the precompiled Groovy script
     * @param logger the Avalon logger
     */
    public GroovyScriptCacheEntry( String name, Script script, Logger logger )
    {
        this.name = name;
        this.script = script;
        this.semaphore = new GroovySemaphore( name, logger, 1);
    }

    /**
     * @return Returns the name.
     */
    public String getName()
    {
        return name;
    }
    /**
     * @return Returns the script.
     */
    public Script getScript()
    {
        return script;
    }

    /**
     * Acquire the semaphore
     */
    public void acquire()
    {
        semaphore.acquire();
    }

    /**
     * Release the semaphore
     */
    public void release()
    {
        semaphore.release();
    }
}
