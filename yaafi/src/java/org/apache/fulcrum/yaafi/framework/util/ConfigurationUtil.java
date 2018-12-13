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
    public static void expand(Logger logger, DefaultConfiguration defaultConfiguration, Map<?, ?> vars) throws ConfigurationException
    {
        if( vars == null || vars.size() == 0)
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
     * Perform a series of substitutions. The substitutions
     * are performed by replacing ${variable} in the target
     * string with the value of provided by the key "variable"
     * in the provided hashtable.
     *
     * The unexpanded ${variable} is always written to 
     * the string buffer. 
     *
     * @param argStr target string
     * @param vars name/value pairs used for substitution
     * @return String target string with replacements.
     */
    private static String expand(String argStr, Map<?, ?> vars)
    {
    	// ignore failures
    	boolean isLenient = true;
    	
    	StringBuilder argBuf = new StringBuilder();
        int argStrLength = argStr.length();

        for (int cIdx = 0 ; cIdx < argStrLength;)
        {
            char ch = argStr.charAt(cIdx);
            char del = ' ';

            switch (ch)
            {
                case '$':
                    StringBuilder nameBuf = new StringBuilder();
                    del = argStr.charAt(cIdx+1);
                    if( del == '{')
                    {
                        cIdx++;

                        for (++cIdx ; cIdx < argStr.length(); ++cIdx)
                        {
                            ch = argStr.charAt(cIdx);
                            if (ch != '}')
                                nameBuf.append(ch);
                            else
                                break;
                        }

                        if (nameBuf.length() > 0)
                        {
                            Object value = vars.get(nameBuf.toString());

                            if (value != null)
                            {
                                argBuf.append(value.toString());
                            }
                            else
                            {
                                if (!isLenient)
                                {
                                    throw new RuntimeException("No value found for : " + nameBuf );
                                }
                                else
                                {
                                    argBuf.append("${").append(nameBuf).append("}");
                                }
                            }

                            del = argStr.charAt(cIdx);

                            if( del != '}')
                            {
                                throw new RuntimeException("Delimineter not found for : " + nameBuf );
                            }
                        }

                        cIdx++;
                    }
                    else
                    {
                        argBuf.append(ch);
                        ++cIdx;
                    }

                    break;

                default:
                    argBuf.append(ch);
                    ++cIdx;
                    break;
            }
        }

        return argBuf.toString();    	
    }
}
