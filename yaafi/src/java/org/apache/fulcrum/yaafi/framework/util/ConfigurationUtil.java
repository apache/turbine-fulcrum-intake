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
package org.apache.fulcrum.yaafi.framework.util;

import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.logger.Logger;

/**
 * Helper class to expand the value and all attributes.
 * 
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */
public class ConfigurationUtil
{
    /**
     * Expand place holders found in values or attrbute values with the 
     * content of the given variables. The implementation assumes that 
     * the given configuration can be cast to a DefaultConfiguration
     * otherwise we can't use any setters. 
     *  
     * @param logger the logger to write diagnostic messages
     * @param defaultConfiguration the configuration
     * @param vars the map holding the variables
     * @throws ConfigurationException parsing the configuration failed
     */
    public static void expand(Logger logger, DefaultConfiguration defaultConfiguration, Map vars) throws ConfigurationException
    {
        if((vars == null) || (vars.size() == 0))
        {
            return;
        }
                
        // update the value of the configuration element
        
        if(defaultConfiguration.getValue(null) != null) 
        {
            String oldValue = defaultConfiguration.getValue();
            String newValue = ConfigurationUtil.expand(oldValue, vars);
            defaultConfiguration.setValue(newValue); 
            
            if(oldValue.equals(newValue) == false)
            {
                logger.debug("Changed element <" 
                    + defaultConfiguration.getName() 
                    + "> from '" 
                    + oldValue 
                    + "' ==> '" 
                    + newValue
                    + "'"
                    );
            }
        }
        
        // update all attributes
        
        String attributeName = null;
        String[] attributeNames = defaultConfiguration.getAttributeNames();
        
        for(int i=0; i<attributeNames.length; i++)
        {
            attributeName = attributeNames[i];
            String oldAttributeValue = defaultConfiguration.getAttribute(attributeName);
            String newAttributeValue = ConfigurationUtil.expand(oldAttributeValue, vars);
            defaultConfiguration.setAttribute(attributeName, newAttributeValue);
            
            if(oldAttributeValue.equals(newAttributeValue) == false)
            {
                logger.debug("Changed attribute '" 
                    + defaultConfiguration.getName() + "@" + attributeName 
                    + "' from '" 
                    + oldAttributeValue 
                    + "' ==> '" 
                    + newAttributeValue
                    + "'"
                    );
            }
        }
        
        // and now recurse through all children (children are in general a lot of work)
        
        Configuration[] children = defaultConfiguration.getChildren();
        
        for(int i=0; i<children.length; i++)
        {
            ConfigurationUtil.expand(logger, ((DefaultConfiguration) children[i]), vars);
        }
    }
    
    /**
     * @return the expand a string
     */
    private static String expand(String value, Map vars)
    {
        return StringUtils.stringSubstitution(value, vars, true).toString(); 
    }
}
