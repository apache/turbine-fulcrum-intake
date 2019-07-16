package org.apache.turbine.services.yafficomponent;

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

import org.apache.fulcrum.yaafi.framework.container.ServiceConstants;
import org.apache.turbine.services.Service;

/**
 * This service allows access to avalon components.
 *
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */
public interface YaafiComponentService
        extends Service, ServiceConstants
{
    /** The publicly visible name of the service */
    String SERVICE_NAME = "AvalonComponentService";

    /** Where we write the Avalon Logger messages */
    String AVALON_LOG_CATEGORY = "avalon";

    /** property specifing file name of the component config file */
    String COMPONENT_CONFIG_KEY = "componentConfiguration";

    /** property specifing file name of the component config file */
    String COMPONENT_CONFIG_VALUE = "/WEB-INF/conf/componentConfiguration.xml";

    /** property specifing file name of the component role file */
    String COMPONENT_ROLE_KEY = "componentRoles";

    /** property specifing file name of the component role file */
    String COMPONENT_ROLE_VALUE = "/WEB-INF/conf/roleConfiguration.xml";

    /** property for the Components to look up */
    String COMPONENT_LOOKUP_KEY = "lookup";

    /** Key used in the context for defining the application root */
    String COMPONENT_APP_ROOT = "componentAppRoot";

    /**
     * Returns an instance of the named component
     *
     * @param roleName Name of the role the component fills.
     * @return an instance of the named component
     * @throws Exception generic exception
     */
    public Object lookup(String path) throws Exception;

    /**
     * Releases the component
     *
     * @param source. The path to the handler for this component
     * For example, if the component is a java.sql.Connection
     * object sourced from the "/turbine-merlin/datasource"
     * component, the call would be :-
     * release("/turbine-merlin/datasource", conn);
     * @param component the component to release
     */
    public void release(Object component);
}
