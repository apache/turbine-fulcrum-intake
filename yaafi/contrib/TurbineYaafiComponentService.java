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


import java.io.File;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.logger.Log4JLogger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.yaafi.framework.container.ServiceContainer;
import org.apache.fulcrum.yaafi.framework.factory.ServiceManagerFactory;
import org.apache.turbine.Turbine;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.TurbineBaseService;

/**
 * An implementation of YaafiComponentService which loads all the
 * components given in the TurbineResources.properties File.
 * <p>
 * For component which require the location of the application or
 * context root, there are two ways to get it.
 * <ol>
 * <li>
 *   Implement the Contextualizable interface.  The full path to the
 *   correct OS directory can be found under the ComponentAppRoot key.
 * </li>
 * <li>
 *   The system property "applicationRoot" is also set to the full path
 *   of the correct OS directory.
 * </li>
 * </ol>
 * If you want to initialize Torque by using the AvalonComponentService, you
 * must activate Torque at initialization time by specifying
 *
 * services.AvalonComponentService.lookup = org.apache.torque.Torque
 *
 * in your TurbineResources.properties.
 *
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public class TurbineYaafiComponentService
        extends TurbineBaseService
        implements YaafiComponentService, Initializable, Disposable
{
    /** Logging */
    private static Log log = LogFactory.getLog(TurbineYaafiComponentService.class);

    /** YAFFI container */
    private ServiceContainer container = null;

    // -------------------------------------------------------------
    // Service initialization
    // -------------------------------------------------------------

    /**
	 * Load all configured components and initialize them. This is a zero parameter variant which
	 * queries the Turbine Servlet for its config.
	 *
	 * @throws InitializationException Something went wrong in the init stage
	 */
    public void init( Object data )
    	throws InitializationException
    {
        try
        {
            initialize();
            setInit(true);
        }
        catch (Exception e)
        {
            log.error("Exception caught initialising service: ", e);
            throw new InitializationException("init failed", e);
        }
    }

    /**
	 * Shuts the Component Service down, calls dispose on the components that implement this
	 * interface
	 *
	 */
    public void shutdown()
    {
        dispose();
        setInit(false);
    }

    // -------------------------------------------------------------
    // Avalon lifecycle interfaces
    // -------------------------------------------------------------

    /**
	 * Initializes the container
	 *
	 * @throws Exception generic exception
	 */
    public void initialize() throws Exception
    {
		org.apache.commons.configuration.Configuration conf = getConfiguration();

		// determine the home directory

        String homePath = Turbine.getApplicationRoot();
        File home = new File(homePath);

        // determine the location of the role configuraton file

        String roleConfigurationFileName = conf.getString(
            this.COMPONENT_ROLE_KEYS,
            this.COMPONENT_ROLE_VALUE
            );

        // determine the location of component configuration file

        String componentConfigurationFileName = conf.getString(
            this.COMPONENT_CONFIG_KEY,
            this.COMPONENT_CONFIG_VALUE
            );

        // determine the location of parameters file

        String parametersFileName = conf.getString(
            this.COMPONENT_PARAMETERS_KEY,
            this.COMPONENT_PARAMETERS_VALUE
            );

        // build up a default context

        DefaultContext context = new DefaultContext();
        context.put(COMPONENT_APP_ROOT, homePath);
        context.put(URN_AVALON_HOME, new File( homePath ) );
        context.put(URN_AVALON_TEMP, new File( homePath ) );

        try
        {
            this.container = ServiceManagerFactory.create(
                new Log4JLogger( org.apache.log4j.Logger.getLogger( TurbineYaafiComponentService.class ) ),
                roleConfigurationFileName,
                componentConfigurationFileName,
                parametersFileName,
                context
                );
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            log.error(t);
        }
    }

    /**
	 * Disposes of the container and releases resources
	 */
    public void dispose()
    {
        if (this.container != null)
        {
            this.container.dispose();
            this.container = null;
        }
    }

    /**
	 * Returns an instance of the named component
	 *
	 * @param roleName Name of the role the component fills.
	 * @return an instance of the named component
	 * @throws Exception generic exception
	 */
    public Object lookup(String path) throws Exception
    {
        return this.container.lookup(path);
    }

    /**
	 * Releases the component
	 *
	 * @param source. The path to the handler for this component For example, if the object is a
	 *            java.sql.Connection object sourced from the "/turbine-merlin/datasource"
	 *            component, the call would be :- release("/turbine-merlin/datasource", conn);
	 * @param component the component to release
	 */
    public void release(Object component)
    {
        this.container.release( component );
    }
}
