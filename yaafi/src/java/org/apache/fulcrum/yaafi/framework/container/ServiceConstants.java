package org.apache.fulcrum.yaafi.framework.container;

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

import org.apache.fulcrum.yaafi.framework.constant.AvalonYaafiConstants;

/**
 * Commonly used constants.
 *
 *  @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public interface ServiceConstants extends AvalonYaafiConstants
{
    /** The name of this component */
    String ROLE_NAME = "fulcrum-yaafi";

    /** The default implementation class for YAAFI */
    String CLAZZ_NAME = "org.apache.fulcrum.yaafi.framework.container.ServiceContainerImpl";

    /////////////////////////////////////////////////////////////////////////
    // Entries for the YAAFI configuration files
    /////////////////////////////////////////////////////////////////////////

    /** property to lookup the container type */
    String CONTAINERFLAVOUR_CONFIG_KEY = "containerFlavour";

    /** property to lookup the implementation class of the container */
    String CONTAINERCLAZZNAME_CONFIG_KEY = "containerClazzName";

    /** property to lookup the component config file */
    String COMPONENT_CONFIG_KEY = "componentConfiguration";

    /** property to lookup the component config property file */
    String COMPONENT_CONFIG_PROPERTIES_KEY = "componentConfigurationProperties";

    /** property to lookup the component role file */
    String COMPONENT_ROLE_KEYS = "componentRoles";

    /** property to lookup the parameters file */
    String COMPONENT_PARAMETERS_KEY = "parameters";

    /** property to lookup the decryption handling */
    String COMPONENT_ISENCRYPTED_KEY = "isEncrypted";

    /** property to lookup the  lcoation */
    String COMPONENT_LOCATION_KEY = "location";

    /** property to lookup the usage of dynamic proxies */
    String DYNAMICPROXY_ENABLED_KEY = "hasDynamicProxies";

    /** property to lookup the list of interceptors */
    String INTERCEPTOR_LIST_KEY = "interceptors";

    /** property to lookup a single interceptor */
    String INTERCEPTOR_KEY = "interceptor";

    /** property to lookup the reconfigurationDelay */
    String RECONFIGURATION_DELAY_KEY = "reconfigurationDelay";

    /////////////////////////////////////////////////////////////////////////
    // Default values for YAAFI configuration files
    /////////////////////////////////////////////////////////////////////////

    /** default file name of the component config file */
    String COMPONENT_CONFIG_VALUE = "/componentConfiguration.xml";

    /** default file name of the component role file */
    String COMPONENT_ROLE_VALUE = "/componentRoles.xml";

    /** default file name of the parameters file */
    String COMPONENT_PARAMETERS_VALUE = "/parameters.properties";

    /** default value for container flavour */
    String COMPONENT_CONTAINERFLAVOUR_VALUE = "yaafi";

    /** default value for role config flavour */
    String COMPONENT_ROLECONFIGFLAVOUR_VALUE = "yaafi";

}
