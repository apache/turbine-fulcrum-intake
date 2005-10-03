package org.apache.fulcrum.yaafi.framework.util;

/*
 * Copyright 2002-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.avalon.framework.logger.Logger;
import org.apache.fulcrum.yaafi.framework.locking.AvalonLoggerFacade;


/**
 * A simple lock manager supporting read locks and write locks. The main
 * intention is to shield the application from the implementation details.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */
public class ReadWriteLock
{
    /** Read/Write lock to synchronize acess to services */
    private final org.apache.fulcrum.yaafi.framework.locking.ReadWriteLock lock;

    /**
     * Constructor
     * 
     * @param name the name of the lock
     * @param logger the logger to be used
     */
    public ReadWriteLock( String name, Logger logger )
    {
        this.lock = new org.apache.fulcrum.yaafi.framework.locking.ReadWriteLock(
            name,
            new AvalonLoggerFacade(logger)
            );
    }

    /**
     * @return a read lock
     */
    public Object getReadLock(String ownerId)
    	throws InterruptedException
    {
        this.lock.acquireRead(ownerId,0);
        return this;
    }

    /**
     * @return a write lock
     */
    public Object getWriteLock(String ownerId)
    	throws InterruptedException
    {
        this.lock.acquireWrite(ownerId,0);
        return this;
    }
    
    /**
     * releases the lock
     * @param lock the lock
     */
    public void releaseLock(Object lock, String ownerId)
    {
        this.lock.release(ownerId);
    }
}