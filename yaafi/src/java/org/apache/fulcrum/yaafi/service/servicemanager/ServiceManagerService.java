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

import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceManager;

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

public interface ServiceManagerService extends ServiceManager
{
    /**
     * @return the Logger of the container
     */
    Logger getAvalonLogger();
    
    /** 
     * @return the ServiceManager for the container
     */
    ServiceManager getServiceManager();

    /** 
     * @return the Paramters for the container
     */
    Parameters getParameters();

    /** 
     * @return the Context for the container
     */
    Context getContext();
}
