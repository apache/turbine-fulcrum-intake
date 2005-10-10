package org.apache.fulcrum.hsqldb;

import org.apache.avalon.framework.activity.Startable;

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

/**
 * A definition for a HSQLService component which configures a single hsqldb
 * database.
 * 
 * @author <a href="mailto:pti@elex.be">Peter Tillemans</a>
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */
public interface HSQLService extends Startable
{
    /**
     * Starts the HSQLDB server. The implementation polls to ensure
     * that the HSQLDB server is fully initialized otherwise we get
     * spurious connection exceptions. If the HSQLDB server is not
     * upand running within 10 seconds we throw an exception.
     *  
     * @see org.apache.avalon.framework.activity.Startable#start()
     */
    public void start() throws Exception;
    
    /**
     * Stop the HSQLDB server. The implementation polls to ensure
     * that the HSQLDB server has terminated otherwise someone
     * could call System.exit() and break the database.
     * 
     * @see org.apache.avalon.framework.activity.Startable#stop()
     */
    public void stop() throws Exception;

    /**
     * Check if the server is running
     * 
     * @return the state of the hsqldb server
     */
    public boolean isRunning();
}