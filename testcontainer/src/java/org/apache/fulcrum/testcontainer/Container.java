package org.apache.fulcrum.testcontainer;

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

import org.apache.avalon.excalibur.component.DefaultRoleManager;
import org.apache.avalon.excalibur.component.ExcaliburComponentManager;
import org.apache.avalon.excalibur.logger.LoggerManager;
import org.apache.avalon.excalibur.logger.Log4JLoggerManager;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
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
public class Container
        extends AbstractLogEnabled
        implements Initializable, Disposable
{
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
    public Container()
    {
        org.apache.log4j.BasicConfigurator.configure();
        this.enableLogging(lm.getLoggerForCategory(
                "org.apache.fulcrum.testcontainer.Container"));
    }

    /**
     * Starts up the container and initializes it.
     *
     * @param configFileName Name of the component configuration file
     * @param roleFileName Name of the role configuration file
     */
    public void startup( String configFileName, String roleFileName )
    {
        getLogger().debug("Starting container...");

        this.configFileName = configFileName;
        this.roleFileName = roleFileName;
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
        // process configuration files
        DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
        Configuration sysConfig = builder.buildFromFile(configFileName);
        Configuration roleConfig = builder.buildFromFile(roleFileName);

        // Setup the RoleManager
        DefaultRoleManager roles = new DefaultRoleManager();
        roles.enableLogging(
                lm.getLoggerForCategory("org.apache.fulcrum"));
        roles.configure(roleConfig);

        // Setup ECM
        this.manager.setLoggerManager(lm);
        this.manager.enableLogging(
                lm.getLoggerForCategory("org.apache.fulcrum"));
        DefaultContext context = new DefaultContext();
        context.put("ComponentAppRoot", (new File("")).getAbsolutePath());
        this.manager.contextualize(context);
        this.manager.setRoleManager(roles);
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
    public Component lookup(String roleName)
            throws ComponentException
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

}
