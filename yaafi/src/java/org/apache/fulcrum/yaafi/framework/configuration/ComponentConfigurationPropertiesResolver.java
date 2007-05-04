/*
 * Copyright 2002-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.fulcrum.yaafi.framework.configuration;

import java.util.Properties;

/**
 * This interface allows to resolve component configuration properties. These
 * properties are used to expand variables found in the componentConfiguration.xml.
 * The main motivation for this interface is to allow users to hook up 
 * commons-configuration to resolve global parameters easily without 
 * coupling this implementation to any external libraries.
 * 
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */
public interface ComponentConfigurationPropertiesResolver
{
    /** default file name of the component config property file */
    String COMPONENT_CONFIG_PROPERTIES_VALUE = "/componentConfiguration.properties";

    /**
     * Resolve custom properties
     * 
     * @param defaults the default properties
     * @return the custom properties
     */
    Properties resolve(Properties defaults) throws Exception;
}
