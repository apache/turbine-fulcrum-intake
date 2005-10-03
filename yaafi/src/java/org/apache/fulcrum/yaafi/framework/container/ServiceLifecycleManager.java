package org.apache.fulcrum.yaafi.framework.container;

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


import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.fulcrum.yaafi.framework.role.RoleEntry;

/**
 * Interface for managing the lifecycle of services. It provides
 * methods to get
 *
 * <ul>
 *   <li>metadata about the service components</li>
 *   <li>reconfiguring a single service</li>
 *   <li>decommissioning a signle service</li>
 * </ul>
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public interface ServiceLifecycleManager
{
    /**
     * Get a RoleEntryImpl for a given service
     *
     * @param name the name of the service component
     * @return the RoleEntryImpl
     * @throws ServiceException the service was not found
     */
    RoleEntry getRoleEntry( String name )
        throws ServiceException;

    /**
     * Get a list of all RoleEntries.
     *
     * @return a list of RoleEntries
     */
    RoleEntry[] getRoleEntries();

    /**
     * Reconfigures a set of services  by calling Suspendable.suspend(),
     * Reconfigurable.reconfigure() and Suspendable.resume().
     *
     * @param names the set of services to be reconfigured
     * @exception ServiceException one of the service was not found
     * @throws ConfigurationException the reconfiguration failed
     */
    void reconfigure( String[] names )
        throws ServiceException, ConfigurationException;

    /**
     * Decommision the given service by calling Startable.stop()
     * and Disposable.dispose().
     *
     * The state of the service component is the same as using lazy
     * initialization. Therefore a new service instance will be created
     * if the service is reused again. If you are keeping an instance
     * of the service you are out of luck.
     *
     * @param name the name of the service
     * @exception ServiceException the service was not found
     */
    void decommision( String name )
        throws ServiceException;
}