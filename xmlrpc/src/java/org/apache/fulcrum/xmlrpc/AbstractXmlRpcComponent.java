package org.apache.fulcrum.xmlrpc;

/*
 * Copyright 2000-2004 The Apache Software Foundation.
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


import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;

import org.apache.xmlrpc.XmlRpc;

/**
 * Common code for the XML RPC components. This processes the system-properties and parser entries
 * from the config.
 *
 * @todo Handle XmlRpc.setDebug(boolean)
 *
 * @author <a href="mailto:jason@zenplex.com">Jason van Zyl</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 */
public abstract class AbstractXmlRpcComponent
    extends AbstractLogEnabled
    implements Configurable, Initializable
{
    /** SAX Parser class. */
    protected String saxParserClass;

    protected AbstractXmlRpcComponent()
    {
    }

    // ----------------------------------------------------------------------
    // Lifecycle Management
    // ----------------------------------------------------------------------

    public void configure(Configuration configuration)
        throws ConfigurationException
    {
        // Set system properties. These are required if you're
        // using SSL.
        setSystemPropertiesFromConfiguration(configuration);

        // Set the XML driver to the correct SAX parser class
        saxParserClass = configuration.getChild("parser").getValue("org.apache.xerces.parsers.SAXParser");
    }

    /**
     * Create System properties using the key-value pairs in a given
     * Configuration.  This is used to set system properties and the
     * URL https connection handler needed by JSSE to enable SSL
     * between XMLRPC client and server.
     *
     * @param configuration the Configuration defining the System
     * properties to be set
     */
    protected void setSystemPropertiesFromConfiguration(Configuration configuration)
        throws ConfigurationException
    {
        Configuration[] systemProperties =
                configuration.getChildren("systemProperty");

        getLogger().debug("system properties: " + systemProperties.length);

        for( int i = 0; i < systemProperties.length; i++ )
        {
            Configuration systemProperty = systemProperties[i];

            String key = systemProperty.getAttribute("name");
            String value = systemProperty.getAttribute("value");

            getLogger().debug("System property: " + key + " => " + value);

            System.setProperty(key, value);
        }
    }

    /**
     * This function initializes the XmlRpcService.
     */
    public void initialize()
        throws Exception
    {
        XmlRpc.setDriver ( saxParserClass );
    }
}
