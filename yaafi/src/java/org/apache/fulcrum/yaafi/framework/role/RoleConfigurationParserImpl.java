package org.apache.fulcrum.yaafi.framework.role;

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

import java.util.ArrayList;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.fulcrum.yaafi.framework.constant.AvalonFortressConstants;
import org.apache.fulcrum.yaafi.framework.constant.AvalonPhoenixConstants;
import org.apache.fulcrum.yaafi.framework.constant.AvalonYaafiConstants;
import org.apache.fulcrum.yaafi.framework.util.Validate;

/**
 * Parses the role configuration file of various Avalon containers.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public class RoleConfigurationParserImpl
    implements RoleConfigurationParser
{
    /** The flavour of Avalon container */
    private String containerFlavour;

    /**
     * Constructor
     * @param containerFlavour The flavour of Avalon container
     */
    public RoleConfigurationParserImpl( String containerFlavour )
    {
        Validate.notEmpty( containerFlavour, "containerFlavour" );
        this.containerFlavour = containerFlavour;
    }

    /**
     * Parses a role configuration file.
     *
     * @param roleConfiguration the role configuration file to parse
     * @return the parsed RoleEntries
     * @throws ConfigurationException the configuration couldn't be processsed
     */
    public RoleEntry[] parse( Configuration roleConfiguration )
        throws ConfigurationException
    {
        Validate.notNull( roleConfiguration, "roleConfiguration" );

        if( AvalonYaafiConstants.AVALON_CONTAINER_YAAFI.equals(containerFlavour) )
        {
            return mapFromYaafi(roleConfiguration);

        }
        if( AvalonPhoenixConstants.AVALON_CONTAINER_PHOENIX.equals(containerFlavour) )
        {
            return mapFromPhoenix(roleConfiguration);

        }
        else if( AvalonFortressConstants.AVALON_CONTAINER_FORTESS.equals(containerFlavour) )
        {
            return mapFromFortress(roleConfiguration);

        }
        else
        {
            String msg = "Don't know the following container flavour : " + containerFlavour;
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Parses a YAAFI role configuration file.
     * 
     * @param roleConfiguration the role configuration
     * @return the role entries from the configuration file
     * @throws ConfigurationException the configuration couldn't be processsed
     */
    private RoleEntry[] mapFromYaafi( Configuration roleConfiguration )
        throws ConfigurationException
    {
        Validate.notNull(roleConfiguration, "roleConfiguration");

        String clazzName = null;
        String name = null;
        String shorthand = null;
        boolean isEarlyInit = false;
        String description = null;
        String componentType = null;
        String componentFlavour = null;
        boolean hasProxy = false;
        ArrayList interceptorList = null;
        String logCategory = null;
        RoleEntry roleEntry = null;

        Configuration[] list = roleConfiguration.getChildren( "role" );
        RoleEntry[] result = new RoleEntry[list.length];

        for( int i=0; i<list.length; i++ )
        {
            clazzName = list[i].getAttribute("default-class");
            name = list[i].getAttribute("name",clazzName);
            shorthand = list[i].getAttribute("shorthand",name);
            isEarlyInit = list[i].getAttributeAsBoolean("early-init",true);
            description = list[i].getAttribute("description",null);
            componentType = list[i].getAttribute("component-type","avalon");
            componentFlavour = list[i].getAttribute("component-flavour", AvalonYaafiConstants.AVALON_CONTAINER_YAAFI);
            hasProxy = list[i].getAttributeAsBoolean("has-proxy",true);
            logCategory = list[i].getAttribute("logger",shorthand);

            // parse the list of defined interceptors

            Configuration[] interceptorConfigList = list[i].getChild("interceptors").getChildren("interceptor");
            interceptorList = new ArrayList();

            for( int j=0; j<interceptorConfigList.length; j++ )
            {
                interceptorList.add(interceptorConfigList[j].getValue("interceptor"));
            }

            // create a role entry

            roleEntry = new RoleEntryImpl(
                name,
                clazzName,
                shorthand,
                isEarlyInit,
                description,
                componentType,
                componentFlavour,
                hasProxy,
                interceptorList,
                logCategory
                );

            result[i] = roleEntry;
        }

        return result;
    }

    private RoleEntry[] mapFromPhoenix( Configuration roleConfiguration )
        throws ConfigurationException
    {
        Validate.notNull(roleConfiguration, "roleConfiguration");
        throw new ConfigurationException("Not supported yet");
    }

    private RoleEntry[] mapFromFortress( Configuration roleConfiguration )
        throws ConfigurationException
    {
        Validate.notNull(roleConfiguration, "roleConfiguration");
        throw new ConfigurationException("Not supported yet");
    }
}