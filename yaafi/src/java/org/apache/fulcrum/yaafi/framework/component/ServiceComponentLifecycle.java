package org.apache.fulcrum.yaafi.framework.component;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceManager;

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
 * This class implements the lifecycle contract of a service component
 * instance.
 * 
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a> 
 */

public interface ServiceComponentLifecycle
{
    /**
     * Incarnates a service component instance.
     */
    void incarnate() throws Exception;
    
    /**
     * Reconfigures a service component instance
     */
    void reconfigure() throws Exception;
    
    /**
     * Decommisions a service component instance
     */
    void decommision() throws Exception;

    /**
     * @return Returns the instance of the singleton
     */
    Object getInstance() throws Exception;

    /**
     * @param logger The logger to set.
     */
    void setLogger(Logger logger);
    
    /**
     * @param serviceManager The serviceManager to set.
     */
    void setServiceManager(ServiceManager serviceManager);

    /**
     * @param context The context to set.
     */
    void setContext(Context context);
    
    /**
     * @param configuration The configuration to set.
     */
    void setConfiguration(Configuration configuration);

    /**
     * @param parameters The paramaters to set.
     */
    void setParameters(Parameters parameters);
}