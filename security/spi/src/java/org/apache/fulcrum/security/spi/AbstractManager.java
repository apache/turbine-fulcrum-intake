package org.apache.fulcrum.security.spi;
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
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.security.GroupManager;
import org.apache.fulcrum.security.PermissionManager;
import org.apache.fulcrum.security.RoleManager;
import org.apache.fulcrum.security.UserManager;
import org.apache.fulcrum.security.util.DataBackendException;

/**
 * 
 * This abstract implementation provides most of the functionality that 
 * a manager will need.
 * 
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public abstract class AbstractManager
    extends AbstractLogEnabled
    implements Serviceable, Disposable, ThreadSafe
{

    boolean composed = false;
    /** Logging */
    private static Log log = LogFactory.getLog(AbstractManager.class);

    private ServiceManager manager = null;
    protected PermissionManager permissionManager;
    protected RoleManager roleManager;
    protected GroupManager groupManager;
    protected UserManager userManager;

    /**
     * @return
     */
    protected ServiceManager getServiceManager()
    {
        return manager;
    }

    /**
     * @return
     */
    protected UserManager getUserManager() throws DataBackendException
    {
        if (userManager == null)
        {
            try
            {
                userManager = (UserManager) manager.lookup(UserManager.ROLE);

            }
            catch (ServiceException ce)
            {
                throw new DataBackendException(ce.getMessage(), ce);
            }
        }
        return userManager;
    }
    /**
     * @return
     */
    protected PermissionManager getPermissionManager()
        throws DataBackendException
    {
        if (permissionManager == null)
        {
            try
            {
                permissionManager =
                    (PermissionManager) manager.lookup(PermissionManager.ROLE);

            }
            catch (ServiceException ce)
            {
                throw new DataBackendException(ce.getMessage(), ce);
            }
        }
        return permissionManager;
    }
    /**
     * @return
     */
    protected RoleManager getRoleManager() throws DataBackendException
    {
        if (roleManager == null)
        {
            try
            {
                roleManager = (RoleManager) manager.lookup(RoleManager.ROLE);

            }
            catch (ServiceException ce)
            {
                throw new DataBackendException(ce.getMessage(), ce);
            }
        }
        return roleManager;
    }
    /**
     * @return
     */
    protected GroupManager getGroupManager() throws DataBackendException
    {
        if (groupManager == null)
        {
            try
            {
                groupManager = (GroupManager) manager.lookup(GroupManager.ROLE);

            }
            catch (ServiceException ce)
            {
                throw new DataBackendException(ce.getMessage(), ce);
            }
        }
        return groupManager;
    }
    /**
    * Avalon Service lifecycle method
    */
    public void service(ServiceManager manager) throws ServiceException
    {
        this.manager = manager;

    }
    public void dispose()
    {
		release(roleManager);
		release(permissionManager);
		release(groupManager);
		release(userManager);		
        manager = null;       
    }
    
    protected void release(Object obj){
        if(obj!=null){
            manager.release(obj);
            obj = null;
        }
        
    }


    /**
     * @return A resolved object
     * @throws DataBackendException if the backend failed for some reason.
     */
    protected Object resolve(String lookup) 
    {
        Object component = null;
        {
            try
            {
                component = manager.lookup(lookup);

            }
            catch (ServiceException ce)
            {
                throw new RuntimeException(ce.getMessage(), ce);
            }
        }
        return component;
    }

}
