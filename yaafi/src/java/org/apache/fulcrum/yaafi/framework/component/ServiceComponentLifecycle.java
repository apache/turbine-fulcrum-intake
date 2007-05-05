package org.apache.fulcrum.yaafi.framework.component;

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

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceManager;

/**
 * This class implements the lifecycle contract of a service component
 * instance.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public interface ServiceComponentLifecycle
{
    /**
     * Loads the implementaion class.
     *
     * @param classLoader the classloader to use for loading the implementation class
     * @throws ClassNotFoundException loading of the class failed
     */
    void loadImplemtationClass(ClassLoader classLoader) throws ClassNotFoundException;

    /**
     * Incarnates a service component instance.
     * @throws Exception the operation failed
     */
    void incarnate() throws Exception;

    /**
     * Reconfigures a service component instance
     * @throws Exception the operation failed
     */
    void reconfigure() throws Exception;

    /**
     * Decommisions a service component instance.
     * @throws Exception the operation failed
     */
    void decommision() throws Exception;

    /**
     * Dispose a service component instance.
     */
    void dispose();

    /**
     * @return Returns the instance of the singleton
     * @throws Exception the operation failed
     */
    Object getInstance() throws Exception;

    /**
     * Sets the logger to be used by this component.
     *
     * @param logger The logger to set
     */
    void setLogger(Logger logger);

    /**
     * Sets the ServiceManager to be used by this component.
     *
     * @param serviceManager The serviceManager to set.
     */
    void setServiceManager(ServiceManager serviceManager);

    /**
     * Sets the Context to be used by this component.
     *
     * @param context The context to set.
     */
    void setContext(Context context);

    /**
     * Sets the Configuration to be used by this component.
     *
     * @param configuration The configuration to set.
     */
    void setConfiguration(Configuration configuration);

    /**
     * Sets the Parameters to be used by this component.
     *
     * @param parameters The paramaters to set.
     */
    void setParameters(Parameters parameters);
}
