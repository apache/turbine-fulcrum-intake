package org.apache.fulcrum.security;
/*
 * ==================================================================== The Apache Software
 * License, Version 1.1
 * 
 * Copyright (c) 2001 The Apache Software Foundation. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met: 1. Redistributions of source code must retain
 * the above copyright notice, this list of conditions and the following disclaimer. 2.
 * Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided
 * with the distribution. 3. The end-user documentation included with the redistribution, if any,
 * must include the following acknowledgment: "This product includes software developed by the
 * Apache Software Foundation (http://www.apache.org/)." Alternately, this acknowledgment may
 * appear in the software itself, if and wherever such third-party acknowledgments normally appear. 4.
 * The names "Apache" and "Apache Software Foundation" and "Apache Turbine" must not be used to
 * endorse or promote products derived from this software without prior written permission. For
 * written permission, please contact apache@apache.org. 5. Products derived from this software may
 * not be called "Apache", "Apache Turbine", nor may "Apache" appear in their name, without prior
 * written permission of the Apache Software Foundation.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR ITS CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 * 
 * This software consists of voluntary contributions made by many individuals on behalf of the
 * Apache Software Foundation. For more information on the Apache Software Foundation, please see
 * <http://www.apache.org/> .
 */
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.thread.ThreadSafe;
/**
 * This a basis implementation of the Fulcrum security service.
 * 
 * Provided functionality includes:
 * <ul>
 * <li>methods for retrieving different types of managers.
 * <li>avalon lifecyle managers.
 * </ul>
 * 
 * @author <a href="mailto:epugh@upstate.com">Eric PUgh</a>
 * @version $Id$
 */
public class BaseSecurityService
    extends AbstractLogEnabled
    implements SecurityService, Configurable, Initializable, Composable, ThreadSafe
{
    private boolean disposed = false;
    private ComponentManager manager = null;
    // management of Groups/Role/Permissions

    // temporary storage of the classnames prior to initialization
    String userClassName;
    String groupClassName;
    String permissionClassName;
    String roleClassName;
    String aclClassName;
    /** The instance of UserManager the SecurityService uses */
    protected UserManager userManager = null;
    /** The instance of RoleManager the SecurityService uses */
    protected RoleManager roleManager = null;
    /** The instance of GroupManager the SecurityService uses */
    protected GroupManager groupManager = null;
    /** The instance of PermissionManager the SecurityService uses */
    protected PermissionManager permissionManager = null;
    /** The instance of ModelManager the SecurityService uses */
    protected ModelManager modelManager = null;
    /**
	 * Returns the configured UserManager.
	 * 
	 * @return An UserManager object
	 */
    public UserManager getUserManager()
    {
        if (userManager == null)
        {
            try
            {
                userManager = (UserManager) manager.lookup(UserManager.ROLE);

            }
            catch (ComponentException ce)
            {
                throw new RuntimeException(ce.getMessage(), ce);
            }
        }
        return userManager;
    }
    /**
	 * Returns the configured GroupManager.
	 * 
	 * @return An UserManager object
	 */
    public GroupManager getGroupManager()
    {
        if (groupManager == null)
        {
            try
            {
                groupManager = (GroupManager) manager.lookup(GroupManager.ROLE);

            }
            catch (ComponentException ce)
            {
                throw new RuntimeException(ce.getMessage(), ce);
            }
        }
        return groupManager;
    }
    /**
	 * Returns the configured RoleManager.
	 * 
	 * @return An RoleManager object
	 */
    public RoleManager getRoleManager()
    {
        if (roleManager == null)
        {
            try
            {
                roleManager = (RoleManager) manager.lookup(RoleManager.ROLE);

            }
            catch (ComponentException ce)
            {
                throw new RuntimeException(ce.getMessage(), ce);
            }
        }
        return roleManager;
    }
    /**
	 * Returns the configured PermissionManager.
	 * 
	 * @return An PermissionManager object
	 */
    public PermissionManager getPermissionManager()
    {
        if (permissionManager == null)
        {
            try
            {
                permissionManager = (PermissionManager) manager.lookup(PermissionManager.ROLE);

            }
            catch (ComponentException ce)
            {
                throw new RuntimeException(ce.getMessage(), ce);
            }
        }
        return permissionManager;
    }
    /**
	 * Returns the configured ModelManager.
	 * 
	 * @return An ModelManager object
	 */
    public ModelManager getModelManager()
    {
        if (modelManager == null)
        {
            try
            {
                modelManager = (ModelManager) manager.lookup(ModelManager.ROLE);

            }
            catch (ComponentException ce)
            {
                throw new RuntimeException(ce.getMessage(), ce);
            }
        }
        return modelManager;
    }
    /**
	 * Configure a new role Manager.
	 * 
	 * @param -ermissionManager An PermissionManager object
	 */
    // void setPermissionManager(PermissionManager permissionManager);
    /**
	 * Avalon component lifecycle method
	 */
    public void configure(Configuration conf) throws ConfigurationException
    {

    }
    /**
	 * Avalon component lifecycle method
	 */
    public void compose(ComponentManager manager) throws ComponentException
    {
        this.manager = manager;
        

    }
    /**
	 * Avalon component lifecycle method Initializes the SecurityService, locating the apropriate
	 * UserManager
	 * 
	 * @throws Exception A Problem occured while initializing the User Manager.
	 */
    public void initialize() throws Exception
    {

        userClassName = null;
        groupClassName = null;
        permissionClassName = null;
        roleClassName = null;
        aclClassName = null;
    }
    /**
	 * Avalon component lifecycle method
	 */
    public void dispose()
    {

        manager.release(userManager);
        manager.release(roleManager);
        manager.release(groupManager);
        manager.release(permissionManager);
        manager.release(modelManager);
        manager = null;
    }
}
