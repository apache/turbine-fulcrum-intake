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
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;


/**
 * Let's try to break the singleton addiction with this service. This
 * service stores the instance of a service manager and allows access
 * to this instance and related information such as
 * 
 * <ul>
 *   <li>Logger instance
 *   <li>ServiceManager instance
 * 	 <li>Context instance
 *   <li>Parameters instance
 * </ul>
 * 
 *  @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public class ServiceManagerService
	extends AbstractLogEnabled
	implements ServiceManager, Contextualizable, Parameterizable, Serviceable, Disposable
{
    /** Store the ServiceContainer on a per instance base */
    private static ServiceManager serviceManager;
    
    /** Store the passed parameters on a per instance base */
    private static Parameters parameters;    

    /** Store the passed parameters on a per instance base */
    private static Context context;    

    /**
     * Constructor
     */
    public ServiceManagerService()
    {
        // nothing to do here
    }

    /** 
     * @return the ServiceManager for the container
     */
    public static ServiceManager getServiceManager()
    {
        return ServiceManagerService.serviceManager;
    }

    /** 
     * @return the Paramters for the container
     */
    public static Parameters getParameters()
    {
        return ServiceManagerService.parameters;
    }

    /** 
     * @return the Context for the container
     */
    public static Context getContext()
    {
        return ServiceManagerService.context;
    }    
    
    /////////////////////////////////////////////////////////////////////////
    // Avalon Lifecycle Implementation
    /////////////////////////////////////////////////////////////////////////

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceContainer)
     */
    public void service(ServiceManager serviceManager) throws ServiceException
    {       
        if( ServiceManagerService.serviceManager == null  )
        {
	        this.getLogger().debug( "Storing the ServiceContainer instance" );
	        ServiceManagerService.serviceManager = serviceManager;
        }
    }
    
    /**
     * @see org.apache.avalon.framework.context.Contextualizable#contextualize(org.apache.avalon.framework.context.Context)
     */
    public void contextualize(Context context) throws ContextException
    {
        if( ServiceManagerService.context == null  )
        {
	        this.getLogger().debug( "Storing the Context instance" );
	        ServiceManagerService.context = context;
        }        
    }
    
    /**
     * @see org.apache.avalon.framework.parameters.Parameterizable#parameterize(org.apache.avalon.framework.parameters.Parameters)
     */
    public void parameterize(Parameters parameters) throws ParameterException
    {
        if( ServiceManagerService.parameters == null  )
        {
	        this.getLogger().debug( "Storing the Parameters instance" );
	        ServiceManagerService.parameters = parameters;
        }
    }
    
    /**
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     */
    public void dispose()
    {
        this.getLogger().debug( "Removing the ServiceContainer instance" );
        ServiceManagerService.serviceManager 	= null;
        ServiceManagerService.parameters		= null;
        ServiceManagerService.context 			= null;
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
