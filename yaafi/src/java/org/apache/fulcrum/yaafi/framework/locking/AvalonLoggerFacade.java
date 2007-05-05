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

package org.apache.fulcrum.yaafi.framework.locking;

import org.apache.avalon.framework.logger.Logger;


/**
 * Facade for all kinds of logging engines.
 *
 * @version $Revision: 1.2 $
 */
public class AvalonLoggerFacade implements LoggerFacade
{
    /** the logger to be used */
    private Logger logger;

    /**
     * Constructor
     *
     * @param logger the logger to use
     */
    public AvalonLoggerFacade(Logger logger)
    {
        this.logger = logger;
    }

    /**
     * Create a logger for the given name
     * @param name the name
     */
    public LoggerFacade createLogger(String name)
    {
        return this;
    }

    /**
     * @see org.apache.fulcrum.yaafi.framework.locking.LoggerFacade#isFineEnabled()
     */
    public boolean isFineEnabled()
    {
        return this.logger.isDebugEnabled();
    }

    /**
     * @see org.apache.fulcrum.yaafi.framework.locking.LoggerFacade#isFinerEnabled()
     */
    public boolean isFinerEnabled()
    {
        return false;
    }

    /**
     * @see org.apache.fulcrum.yaafi.framework.locking.LoggerFacade#isFinestEnabled()
     */
    public boolean isFinestEnabled()
    {
        return false;
    }

    /**
     * @see org.apache.fulcrum.yaafi.framework.locking.LoggerFacade#logFine(java.lang.String)
     */
    public void logFine(String message)
    {
        this.logger.debug(message);
    }

    /**
     * @see org.apache.fulcrum.yaafi.framework.locking.LoggerFacade#logFiner(java.lang.String)
     */
    public void logFiner(String message)
    {
        this.logger.debug(message);
    }

    /**
     * @see org.apache.fulcrum.yaafi.framework.locking.LoggerFacade#logFinest(java.lang.String)
     */
    public void logFinest(String message)
    {
        this.logger.debug(message);
    }

    /**
     * @see org.apache.fulcrum.yaafi.framework.locking.LoggerFacade#logInfo(java.lang.String)
     */
    public void logInfo(String message)
    {
        this.logger.info(message);
    }

    /**
     * @see org.apache.fulcrum.yaafi.framework.locking.LoggerFacade#logSevere(java.lang.String, java.lang.Throwable)
     */
    public void logSevere(String message, Throwable t)
    {
        this.logger.error(message,t);
    }

    /**
     * @see org.apache.fulcrum.yaafi.framework.locking.LoggerFacade#logSevere(java.lang.String)
     */
    public void logSevere(String message)
    {
        this.logger.error(message);
    }

    /**
     * @see org.apache.fulcrum.yaafi.framework.locking.LoggerFacade#logWarning(java.lang.String, java.lang.Throwable)
     */
    public void logWarning(String message, Throwable t)
    {
        this.logger.warn(message,t);
    }

    /**
     * @see org.apache.fulcrum.yaafi.framework.locking.LoggerFacade#logWarning(java.lang.String)
     */
    public void logWarning(String message)
    {
        this.logger.warn(message);
    }
}
