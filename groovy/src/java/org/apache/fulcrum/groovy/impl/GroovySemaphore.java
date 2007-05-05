package org.apache.fulcrum.groovy.impl;

import org.apache.avalon.framework.logger.Logger;

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

/**
 * A simple semaphore implementation.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */
public class GroovySemaphore
{
    /** the numbers of 'acquire()' before the caller is forced to wait */
    private int count;

    /** The logger to use */
    private Logger logger;

    /** The name of the script wo which the semaphore belogs */
    private String scriptName;

    /**
     * Constructor
     * @param scriptName the name of the script to be managed
     * @param logger to Avalon logger
     * @param count the numbers of 'acquire()' before the caller is forced to wait
     */
    public GroovySemaphore(String scriptName, Logger logger, int count)
    {
       this.scriptName = scriptName;
       this.logger = logger;
       this.count = count;
    }

    /**
     * Acquire the semaphore
     */
    public synchronized void acquire()
    {
       while( this.count == 0)
       {
          try
          {
              if( logger.isDebugEnabled() )
              {
                  this.logger.debug(
                      "Thread "
                      + Thread.currentThread().getName()
                      + " is waiting for the sempahore "
                      + this.scriptName
                      );
              }

              wait();
          }
          catch (InterruptedException e)
          {
             // we keep trying
          }
       }

       if( logger.isDebugEnabled() )
       {
           this.logger.debug(
               "Thread "
               + Thread.currentThread().getName()
               + " acquired the sempahore "
               + this.scriptName
               );
       }

       --this.count;
    }

    /**
     * Release the semaphore
     */
    public synchronized void release()
    {
        if( logger.isDebugEnabled() )
        {
            this.logger.debug(
                "Thread "
                + Thread.currentThread().getName()
                + " released the sempahore "
                + this.scriptName
                );
        }

       this.count++;
       notify();
    }
}
