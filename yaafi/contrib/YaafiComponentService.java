package org.apache.turbine.services.yafficomponent;


/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import org.apache.fulcrum.yaafi.framework.container.ServiceConstants;
import org.apache.turbine.services.Service;

/**
 * This service allows access to avalon components.
 *
 * @version $Id$
 */
public interface YaafiComponentService
        extends Service, ServiceConstants
{
    /** The publically visible name of the service */
    String SERVICE_NAME = "YaafiComponentService";
	
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
