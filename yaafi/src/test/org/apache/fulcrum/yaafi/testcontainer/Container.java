package org.apache.fulcrum.yaafi.testcontainer;
/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Turbine" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Turbine", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
import java.io.File;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.logger.Logger;
import org.apache.fulcrum.yaafi.framework.container.ServiceContainer;
import org.apache.fulcrum.yaafi.framework.container.ServiceContainerImpl;
import org.apache.fulcrum.yaafi.framework.factory.ServiceManagerFactory;
import org.apache.fulcrum.yaafi.service.servicemanager.ServiceManagerService;


/**
 * This is a simple YAAFI based container that can be used in unit test
 * of the fulcrum components.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a> 
 */
public class Container extends AbstractLogEnabled implements Initializable, Disposable
{
	/** Key used in the context for defining the application root */
    public static String COMPONENT_APP_ROOT = "componentAppRoot";

    /** Alternate Merlin Friendly Key used in the context for defining the application root */
    public static String URN_AVALON_HOME = "urn:avalon:home";    

    /** Alternate Merlin Friendly Key used in the context for defining the application root */
    public static String URN_AVALON_TEMP = "urn:avalon:temp";    

    /** Component manager */
    private ServiceContainer manager;
    
    /** Configuration file name */
    private String configFileName;
    
    /** Role file name */
    private String roleFileName;
    
    /** Parameters file name */
    private String parametersFileName;
    
    
    /** 
     * Constructor
     */
    public Container()
    {
        // org.apache.log4j.BasicConfigurator.configure();
        this.manager = new ServiceContainerImpl();
        this.enableLogging( new ConsoleLogger( ConsoleLogger.LEVEL_DEBUG ) );
    }
        
    /**
     * Starts up the container and initializes it.
     *
     * @param configFileName Name of the component configuration file
     * @param roleFileName Name of the role configuration file
     */
    public void startup(String configFileName, String roleFileName, String parametersFileName )
    {
        getLogger().debug("Starting container...");        
        
        this.configFileName = configFileName;
        this.roleFileName = roleFileName;
        this.parametersFileName = parametersFileName;
        
        File configFile = new File(configFileName);        
        
        if (!configFile.exists())
        {            
            throw new RuntimeException(
                "Could not initialize the container because the config file could not be found:" + configFile);
        }

        try
        {
            initialize();
            getLogger().info("YaffiContainer ready.");
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
        DefaultContext context = new DefaultContext();
        String absolutePath = new File("").getAbsolutePath();
        context.put(COMPONENT_APP_ROOT, absolutePath);
        context.put(URN_AVALON_HOME, new File( new File("").getAbsolutePath() ) );
        
        Logger logger = new ConsoleLogger( ConsoleLogger.LEVEL_DEBUG );
        
        this.manager = ServiceManagerFactory.create(
            logger,
            this.roleFileName,
            this.configFileName,
            this.parametersFileName,
            context
            );
    }

    /**
     * Disposes of the container and releases resources
     */
    public void dispose()
    {
        getLogger().debug("Disposing of container...");
        this.manager.dispose();
        getLogger().info("YaffiContainer has been disposed.");
    }
    /**
     * Returns an instance of the named component
     *
     * @param roleName Name of the role the component fills.
     * @throws ComponentException generic exception
     */
    public Object lookup(String roleName) throws ComponentException
    {
        try
        {
            return ServiceManagerService.getServiceManager().lookup(roleName);
        }
        catch( Exception e )
        {
            String msg = "Failed to lookup role " + roleName;
            throw new ComponentException(roleName,msg,e);
        }
    }
    /**
     * Releases the component implementing the Component interface. This
     * interface is depracted but still around in Fulcrum
     *
     * @param component
     */
    public void release(Component component)
    {
        ServiceManagerService.getServiceManager().release(component);
    }
    /**
     * Releases the component
     *
     * @param component
     */
    public void release(Object component)
    {
        ServiceManagerService.getServiceManager().release(component);
    }
}
