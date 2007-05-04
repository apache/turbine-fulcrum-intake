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
 * A implementation to provide out-of-the-box component configuration properties
 * using the following algorithm:
 * 
 * <ul>
 *   <li>add the user-supplied defaults to the result<li>
 * 	 <li>add the system properties to the result<li>
 * 	 <li>add the Merlin context entries to the result<li>
 * </ul>
 * 
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */
public class ComponentConfigurationPropertiesResolverImpl 
	extends ComponentConfigurationPropertiesResolverBaseImpl
{    
    /**
     * @see org.apache.fulcrum.yaafi.framework.configuration.ComponentConfigurationPropertiesResolver#resolve(java.util.Properties)
     */
    public Properties resolve(Properties defaults) throws Exception
    {
        String location = this.getLocation();
        Properties result = this.loadProperties(location);
        
        if(defaults != null) 
        {
            result.putAll(defaults);
        }
            
        result.putAll(System.getProperties());
        this.addAvalonContext(result);
        
        return result;    
    }    
}
