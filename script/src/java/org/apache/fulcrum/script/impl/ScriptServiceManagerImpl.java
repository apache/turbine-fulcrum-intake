package org.apache.fulcrum.script.impl;

/*
 * Copyright 2005 Apache Software Foundation
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

import java.util.Set;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;


/**
 * A decorator for the ServiceManager interface to restrict access
 * to Avalon services.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */
public class ScriptServiceManagerImpl implements ServiceManager
{
    /** the Avalon service manager to lookup service */
    private ServiceManager serviceManager;
       
    /** the excluded services */
    private Set excludedServices;
    
    /**
     * Constructor.
     * 
     * @param serviceManager the Avalon service manager to lookup service
     * @param excludedServices the excluded services  
     */
    public ScriptServiceManagerImpl(ServiceManager serviceManager, Set excludedServices)
    {
        this.serviceManager = serviceManager;
        this.excludedServices = excludedServices;
    }
            
    /**
     * @see org.apache.avalon.framework.service.ServiceManager#hasService(java.lang.String)
     */
    public boolean hasService(String arg)
    {
        if( this.excludedServices.contains(arg) )
        {
            return false;
        }
        else
        {
            return this.serviceManager.hasService(arg);
        }
    }
    
    /**
     * @see org.apache.avalon.framework.service.ServiceManager#lookup(java.lang.String)
     */
    public Object lookup(String arg) throws ServiceException
    {
        if( this.excludedServices.contains(arg) )
        {
            // just add a space at the end to enforce a ServiceException
            return this.serviceManager.lookup(arg + ' ');
        }
        else
        {
            return this.serviceManager.lookup(arg);
        }                
    }
    
    /**
     * @see org.apache.avalon.framework.service.ServiceManager#release(java.lang.Object)
     */
    public void release(Object obj)
    {
        this.serviceManager.release(obj);
    }
}
