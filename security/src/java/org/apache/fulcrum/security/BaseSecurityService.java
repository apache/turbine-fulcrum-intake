package org.apache.fulcrum.security;
/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
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
 * This is a common subset of SecurityService implementation.
 *
 * Provided functionality includes:
 * <ul>
 * <li> methods for retrieving User objects, that delegates functionality
 *      to the pluggable implementations of the User interface.
 * <li> synchronization mechanism for methods reading/modifying the security
 *      information, that guarantees that multiple threads may read the
 *      information concurrently, but threads that modify the information
 *      acquires exclusive access.
 * <li> implementation of convenience methods for retrieving security entities
 *      that maintain in-memory caching of objects for fast access.
 * </ul>
 *
 * @author <a href="mailto:Rafal.Krzewski@e-point.pl">Rafal Krzewski</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:marco@intermeta.de">Marco Kn&uuml;ttel</a>
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
    String userManagerClassName;
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
    /**
    	* Returns the configured UserManager.
    	*
    	* @return An UserManager object
    	*/
    public UserManager getUserManager()
    {
        return userManager;
    }
    /**
    	 * Returns the configured GroupManager.
    	 *
    	 * @return An UserManager object
    	 */
    public GroupManager getGroupManager()
    {
        return groupManager;
    }
    /**
    	 * Returns the configured RoleManager.
    	 *
    	 * @return An RoleManager object
    	 */
    public RoleManager getRoleManager()
    {
        return roleManager;
    }
    /**
    		* Returns the configured PermissionManager.
    		*
    		* @return An PermissionManager object
    		*/
    public PermissionManager getPermissionManager()
    {
        return permissionManager;
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
        /*
             userManagerClassName = conf.getAttribute(
                 SecurityService.USER_MANAGER_KEY,
                 SecurityService.USER_MANAGER_DEFAULT);
        
        	roleManagerClassName = conf.getAttribute(
        					SecurityService.ROLE_MANAGER_KEY,
        					SecurityService.ROLE_MANAGER_DEFAULT);
        					
        	groupManagerClassName = conf.getAttribute(
        					SecurityService.GROUP_MANAGER_KEY,
        					SecurityService.GROUP_MANAGER_DEFAULT);
        					
        	permissionManagerClassName = conf.getAttribute(
        					SecurityService.PERMISSION_MANAGER_KEY,
        					SecurityService.PERMISSION_MANAGER_DEFAULT);
         */
    }
    /**
     * Avalon component lifecycle method
     */
    public void compose(ComponentManager manager) throws ComponentException
    {
        this.manager = manager;
		userManager = (UserManager)manager.lookup(UserManager.ROLE);		
		roleManager = (RoleManager)manager.lookup(RoleManager.ROLE);
		groupManager = (GroupManager)manager.lookup(GroupManager.ROLE);
		permissionManager = (PermissionManager)manager.lookup(PermissionManager.ROLE);
		
    }
    /**
     * Avalon component lifecycle method
     * Initializes the SecurityService, locating the apropriate UserManager
     *
     * @throws Exception A Problem occured while initializing 
     * the User Manager.
     */
    public void initialize() throws Exception
    {
        /*
        if (getConfiguration() != null && userManagerClassName == null) 
        {
            userManagerClassName = getConfiguration().getString(
                SecurityService.USER_MANAGER_KEY,
                SecurityService.USER_MANAGER_DEFAULT);
        }
        if (getConfiguration() != null && userClassName == null) 
        {
            userClassName = getConfiguration().getString(
                SecurityService.USER_CLASS_KEY,
                SecurityService.USER_CLASS_DEFAULT);
        }
        if (getConfiguration() != null && groupClassName == null) 
        {
            groupClassName = getConfiguration().getString(
                SecurityService.GROUP_CLASS_KEY, 
                SecurityService.GROUP_CLASS_DEFAULT);
        }
        if (getConfiguration() != null && permissionClassName == null) 
        {
            permissionClassName = getConfiguration().getString(
                SecurityService.PERMISSION_CLASS_KEY, 
                SecurityService.PERMISSION_CLASS_DEFAULT);
        }
        if (getConfiguration() != null && roleClassName == null) 
        {
            roleClassName = getConfiguration().getString(
                SecurityService.ROLE_CLASS_KEY, 
                SecurityService.ROLE_CLASS_DEFAULT);
        }
        if (getConfiguration() != null && aclClassName == null) 
        {
            aclClassName = getConfiguration().getString(
                SecurityService.ACL_CLASS_KEY, 
                SecurityService.ACL_CLASS_DEFAULT);
        }
        */
		/*
        try
        {
        	
            userClass = Class.forName(userClassName);
            groupClass = Class.forName(groupClassName);
            permissionClass = Class.forName(permissionClassName);
            roleClass = Class.forName(roleClassName);
            aclClass = Class.forName(aclClassName);
            
        }
        catch (Exception e)
        {
            if (userClass == null)
            {
                throw new Exception("Failed to create a Class object for User implementation", e);
            }
            if (groupClass == null)
            {
                throw new Exception("Failed to create a Class object for Group implementation", e);
            }
            if (permissionClass == null)
            {
                throw new Exception("Failed to create a Class object for Permission implementation", e);
            }
            if (roleClass == null)
            {
                throw new Exception("Failed to create a Class object for Role implementation", e);
            }
            if (aclClass == null)
            {
                throw new Exception("Failed to create a Class object for ACL implementation", e);
            }
        }
        */
        // Let the peer know which class to create.  Why only user?
        // TurbineUserPeer.setUserClass(userClass);
      
        try
        {
            //aclFactoryService = (FactoryService) manager.lookup(FactoryService.ROLE);
        }
        catch (Exception e)
        {
            throw new Exception("BaseSecurityService.init: Failed to get the Factory Service object", e);
        }
        userManagerClassName = null;
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
    	/*
        if (aclFactoryService != null)
        {
            manager.release(aclFactoryService);
        }
        aclFactoryService = null;
        */
		manager.release(userManager);
		manager.release(roleManager);
		manager.release(groupManager);
		manager.release(permissionManager);
        manager = null;
    }
}
