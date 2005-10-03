/*
 * $Header: /usr/local/cvsroot/it20one/service/it20one-service-framework/src/java/org/apache/fulcrum/yaafi/framework/locking/LoggerFacade.java,v 1.1 2005/09/22 11:04:12 sigi Exp $
 * $Revision: 1.1 $
 * $Date: 2005/09/22 11:04:12 $
 *
 * ====================================================================
 *
 * Copyright 1999-2002 The Apache Software Foundation 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.fulcrum.yaafi.framework.locking;


/**
 * Facade for all kinds of logging engines.
 *   
 * @version $Revision: 1.1 $
 */
public interface LoggerFacade {

    public LoggerFacade createLogger(String name);

    public void logInfo(String message);
    public void logFine(String message);
    public boolean isFineEnabled();
    public void logFiner(String message);
    public boolean isFinerEnabled();
    public void logFinest(String message);
    public boolean isFinestEnabled();
    public void logWarning(String message);
    public void logWarning(String message, Throwable t);
    public void logSevere(String message);
    public void logSevere(String message, Throwable t);
}
