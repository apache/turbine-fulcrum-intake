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
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.security.PermissionManager;
import org.apache.fulcrum.security.entity.Permission;
import org.apache.fulcrum.security.util.DataBackendException;
import org.apache.fulcrum.security.util.EntityExistsException;
import org.apache.fulcrum.security.util.UnknownEntityException;

/**
 * This implementation keeps all objects in memory. This is mostly meant to help with testing and
 * prototyping of ideas.
 * 
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public abstract class AbstractPermissionManager extends AbstractEntityManager implements PermissionManager
{
    /** Logging */
    private static Log log = LogFactory.getLog(AbstractPermissionManager.class);
    
	protected abstract Permission persistNewPermission(Permission permission) throws DataBackendException;

    /**
	 * Construct a blank Permission object.
	 * 
	 * This method calls getPermissionClass, and then creates a new object using the default
	 * constructor.
	 * 
	 * @return an object implementing Permission interface.
	 * @throws UnknownEntityException if the object could not be instantiated.
	 */
    public Permission getPermissionInstance() throws UnknownEntityException
    {
        Permission permission;
        try
        {
			permission = (Permission) Class.forName(getClassName()).newInstance();
        }
        catch (Exception e)
        {
            throw new UnknownEntityException(
                "Failed to instantiate a Permission implementation object",
                e);
        }
        return permission;
    }
    /**
	 * Construct a blank Permission object.
	 * 
	 * This method calls getPermissionClass, and then creates a new object using the default
	 * constructor.
	 * 
	 * @param permName The name of the permission.
	 * 
	 * @return an object implementing Permission interface.
	 * @throws UnknownEntityException if the object could not be instantiated.
	 */
    public Permission getPermissionInstance(String permName) throws UnknownEntityException
    {
        Permission perm = getPermissionInstance();
        perm.setName(permName);
        return perm;
    }
    /**
	 * Retrieve a Permission object with specified name.
	 * 
	 * @param name the name of the Permission.
	 * @return an object representing the Permission with specified name.
	 * @throws DataBackendException if there was an error accessing the data backend.
	 * @throws UnknownEntityException if the permission does not exist.
	 */
    public Permission getPermissionByName(String name)
        throws DataBackendException, UnknownEntityException
    {
        Permission permission = getAllPermissions().getPermissionByName(name);
        if (permission == null)
        {
            throw new UnknownEntityException("The specified permission does not exist");
        }
        return permission;
    }
    /**
	 * Retrieve a Permission object with specified Id.
	 * 
	 * @param name the name of the Permission.
	 * 
	 * @return an object representing the Permission with specified name.
	 * 
	 * @throws UnknownEntityException if the permission does not exist in the database.
	 * @throws DataBackendException if there is a problem accessing the storage.
	 */
    public Permission getPermissionById(Object id)
        throws DataBackendException, UnknownEntityException
    {
        Permission permission = getAllPermissions().getPermissionById(id);
        if (permission == null)
        {
            throw new UnknownEntityException("The specified permission does not exist");
        }
        return permission;
    }
        /**
	 * Creates a new permission with specified attributes.
	 * 
	 * @param permission the object describing the permission to be created.
	 * @return a new Permission object that has id set up properly.
	 * @throws DataBackendException if there was an error accessing the data backend.
	 * @throws EntityExistsException if the permission already exists.
	 */
    public synchronized Permission addPermission(Permission permission)
        throws DataBackendException, EntityExistsException
    {
        boolean permissionExists = false;
        if (StringUtils.isEmpty(permission.getName()))
        {
            throw new DataBackendException("Could not create a permission with empty name!");
        }
        if (permission.getId() != null)
        {
            throw new DataBackendException("Could not create a permission with an id!");
        }
        try
        {
            permissionExists = checkExists(permission);
            if (!permissionExists)
            {
               return persistNewPermission(permission);
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException("addPermission(Permission) failed", e);
        }
        // the only way we could get here without return/throw tirggered
        // is that the permissionExists was true.
        throw new EntityExistsException("Permission '" + permission + "' already exists");
    }
   
    /**
	* Check whether a specifieds permission exists.
	*
	* The name is used for looking up the permission
	*
	* @param role The permission to be checked.
	* @return true if the specified permission exists
	* @throws DataBackendException if there was an error accessing
	*         the data backend.
	*/
	public boolean checkExists(Permission permission) throws DataBackendException
	{
	    return checkExists(permission.getName());
	}  
   
}
