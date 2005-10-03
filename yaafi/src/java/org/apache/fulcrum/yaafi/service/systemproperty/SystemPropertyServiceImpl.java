package org.apache.fulcrum.yaafi.service.systemproperty;

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

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.Reconfigurable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;


/**
 * Copies the properties found in the configuration into the SystemProperties
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public class SystemPropertyServiceImpl
    extends AbstractLogEnabled
    implements SystemPropertyService, Reconfigurable
{
    /**
     * Constructor
     */
    public SystemPropertyServiceImpl()
    {
        // nothing to do here
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration configuration) throws ConfigurationException
    {
        String key      = null;
        String value    = null;
        String oldValue = null;
        Configuration[] systemProperties = configuration.getChildren("property");

        for( int i=0; i<systemProperties.length; i++ )
        {
            key         = systemProperties[i].getAttribute("name");
            value       = systemProperties[i].getValue();
            oldValue    = System.getProperty(key);

            if( oldValue != null )
            {
                this.getLogger().debug(
                    "Changing the value of " + key + " from " + oldValue + " to " + value
                    );
            }
            else
            {
                this.getLogger().debug(
                    "Setting the value of " + key + " to " + value
                    );
            }

            System.setProperty( key, value );

        }

        this.getLogger().debug( "Processed the following number of properties : " + systemProperties.length );
    }

    /**
     * @see org.apache.avalon.framework.configuration.Reconfigurable#reconfigure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void reconfigure(Configuration configuration)
        throws ConfigurationException
    {
        this.configure(configuration);
    }
}
