package org.apache.fulcrum.yaafi.service.servicemanager;

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

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;


/**
 * Let's try to break the singleton addiction with this service. This
 * service stores the instance of a service manager and allows access
 * to this instance.
 */

public class ServiceManagerService
	extends AbstractLogEnabled
	implements ServiceManager, Serviceable, Disposable
{
    /** Store the ServiceContainer on a per instance base */
    private static ServiceManager serviceManager;
    
    /**
     * Constructor
     */
    public ServiceManagerService()
    {
    }

    public static ServiceManager getServiceManager()
    {
        return ServiceManagerService.serviceManager;
    }

    /////////////////////////////////////////////////////////////////////////
    // Avalon Lifecycle Implementation
    /////////////////////////////////////////////////////////////////////////

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceContainer)
     */
    public void service(ServiceManager serviceManager) throws ServiceException
    {
        this.getLogger().debug( "Storing the ServiceContainer instance" );
        ServiceManagerService.serviceManager = serviceManager;
    }

    /**
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     */
    public void dispose()
    {
        this.getLogger().debug( "Removing the ServiceContainer instance" );
        ServiceManagerService.serviceManager = null;
    }

    /////////////////////////////////////////////////////////////////////////
    // ServiceContainer Implementation
    /////////////////////////////////////////////////////////////////////////
    
    /**
     * @see org.apache.avalon.framework.service.ServiceContainer#hasService(java.lang.String)
     */
    public boolean hasService(String name)
    {
        return ServiceManagerService.serviceManager.hasService(name);
    }
    
    /**
     * @see org.apache.avalon.framework.service.ServiceContainer#lookup(java.lang.String)
     */
    public Object lookup(String name) throws ServiceException
    {
        return ServiceManagerService.serviceManager.lookup(name);    
    }
    
    /**
     * @see org.apache.avalon.framework.service.ServiceContainer#release(java.lang.Object)
     */
    public void release(Object object)
    {
        ServiceManagerService.serviceManager.release(object);   
    }
}
