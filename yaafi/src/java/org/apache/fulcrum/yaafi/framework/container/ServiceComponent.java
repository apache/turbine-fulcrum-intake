package org.apache.fulcrum.yaafi.framework.container;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Executable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.activity.Suspendable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Reconfigurable;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.service.Serviceable;

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
 * This service allows access to avalon components.
 * 
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a> 
 */

public interface ServiceComponent
	extends Configurable, Initializable, Startable, Suspendable, 
		Reconfigurable, Disposable, Serviceable, Contextualizable, 
		Parameterizable, LogEnabled, Executable
{
    /**
     * Create an instance of the service class
     *
     * @throws ClassNotFoundException
     */
    public Class loadClass() throws ClassNotFoundException;
    
    /**
     * Create an instance of the service class
     *
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public Object create() throws InstantiationException, IllegalAccessException;
    
    /**
     * @return Returns the if the service instance was already instantiated.
     */
    public boolean isInstantiated();

    /**
     * @return Return true if the service is created on startup
     */
    public boolean isEarlyInit();
    
    /**
     * @return Returns the instance
     */
    public Object getInstance()	throws InstantiationException, IllegalAccessException;    
    
    /**
     * @return Returns the name.
     */
    public String getName();
    
    /**
     * @return Returns the shorthand.
     */
    public String getShorthand();
    
    /**
     * @return the human-readable description of the service
     */
    public String getDescription();
    
    /**
     * @return the type of component
     */
    public String getComponentType();

}