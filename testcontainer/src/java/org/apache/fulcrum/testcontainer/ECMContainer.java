package org.apache.fulcrum.testcontainer;
/*
 * Copyright 2004 The Apache Software Foundation.
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

import org.apache.avalon.excalibur.component.DefaultRoleManager;
import org.apache.avalon.excalibur.component.ExcaliburComponentManager;
import org.apache.avalon.excalibur.logger.Log4JLoggerManager;
import org.apache.avalon.excalibur.logger.LoggerManager;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
/**
 * This is a simple ECM based container that can be used in unit test
 * of the fulcrum components.
 *
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 */
public class ECMContainer extends AbstractLogEnabled implements Container
{
	/** Key used in the context for defining the application root */
    public static String COMPONENT_APP_ROOT = "componentAppRoot";

    /** Alternate Merlin Friendly Key used in the context for 
     * defining the application root */
    public static String URN_AVALON_HOME = "urn:avalon:home";    
    
    /** Component manager */
    private ExcaliburComponentManager manager = new ExcaliburComponentManager();
    /** Configurqation file */
    private String configFileName;
    /** Role file name */
    private String roleFileName;
    /** LogManager for logging */
    private LoggerManager lm = new Log4JLoggerManager();
    /**
     * Constructor
     */
    public ECMContainer()
    {
        org.apache.log4j.BasicConfigurator.configure();
        this.enableLogging(lm.getLoggerForCategory("org.apache.fulcrum.testcontainer.Container"));
    }
    /**
     * Starts up the container and initializes it.
     *
     * @param configFileName Name of the component configuration file
     * @param roleFileName Name of the role configuration file
     */
    public void startup(String configFileName, String roleFileName,String parametersFileName)
    {
        getLogger().debug("Starting container...");
        this.configFileName = configFileName;
        this.roleFileName = roleFileName;
        File configFile = new File(configFileName);        
        if (!configFile.exists())
        {            
            throw new RuntimeException(
                "Could not initialize the container because the config file could not be found:" + configFile);
        }
        try
        {
            initialize();
            getLogger().info("Container ready.");
        }
        catch (Exception e)
        {
            getLogger().error("Could not initialize the container", e);
            throw new RuntimeException("Could not initialize the container");
        }
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
        boolean useRoles = true;
        File roleFile = new File(roleFileName+"");
        if (!roleFile.exists())
        {
            useRoles = false;
            getLogger().info("Not using seperate roles file");
        }
        // process configuration files
        DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
        Configuration sysConfig = builder.buildFromFile(configFileName);
        if (useRoles)
        {
            Configuration roleConfig = builder.buildFromFile(roleFileName);
            // Setup the RoleManager
            DefaultRoleManager roles = new DefaultRoleManager();
            roles.enableLogging(lm.getLoggerForCategory("org.apache.fulcrum"));
            roles.configure(roleConfig);
			this.manager.setRoleManager(roles);
        }
        // Setup ECM
        this.manager.setLoggerManager(lm);
        this.manager.enableLogging(lm.getLoggerForCategory("org.apache.fulcrum"));
        DefaultContext context = new DefaultContext();
        String absolutePath = new File("").getAbsolutePath();
        context.put(COMPONENT_APP_ROOT, absolutePath);
        context.put(URN_AVALON_HOME, absolutePath);
        this.manager.contextualize(context);
        
        this.manager.configure(sysConfig);
        // Init ECM!!!!
        this.manager.initialize();
    }
    /**
     * Disposes of the container and releases resources
     */
    public void dispose()
    {
        getLogger().debug("Disposing of container...");
        this.manager.dispose();
        getLogger().info("Container has been disposed.");
    }
    /**
     * Returns an instance of the named component
     *
     * @param roleName Name of the role the component fills.
     * @throws ComponentException generic exception
     */
    public Object lookup(String roleName) throws ComponentException
    {
        return this.manager.lookup(roleName);
    }
    /**
     * Releases the component
     *
     * @param component
     */
    public void release(Component component)
    {
        this.manager.release(component);
    }
    
    public void release(Object component)
    {
        this.manager.release((Component)component);
    }    
}
