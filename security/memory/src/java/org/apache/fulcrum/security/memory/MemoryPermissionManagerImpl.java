package org.apache.fulcrum.security.memory;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.security.entity.Permission;
import org.apache.fulcrum.security.spi.AbstractPermissionManager;
import org.apache.fulcrum.security.util.DataBackendException;
import org.apache.fulcrum.security.util.PermissionSet;
import org.apache.fulcrum.security.util.UnknownEntityException;

/**
 * This implementation keeps all objects in memory. This is mostly meant to help with testing and
 * prototyping of ideas.
 * 
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class MemoryPermissionManagerImpl extends AbstractPermissionManager
{
    /** Logging */
    private static Log log =
        LogFactory.getLog(MemoryPermissionManagerImpl.class);
    private static List permissions = new ArrayList();
    /** Our Unique ID counter */
    private static int uniqueId = 0;

   
    /**
     * Retrieves all permissions defined in the system.
     * 
     * @return the names of all roles defined in the system.
     * @throws DataBackendException if there was an error accessing the data backend.
     */
    public PermissionSet getAllPermissions() throws DataBackendException
    {
        return new PermissionSet(permissions);
    }
    /**
     * Renames an existing Permission.
     * 
     * @param permission The object describing the permission to be renamed.
     * @param name the new name for the permission.
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws UnknownEntityException if the permission does not exist.
     */
    public synchronized void renamePermission(
        Permission permission,
        String name)
        throws DataBackendException, UnknownEntityException
    {
        boolean permissionExists = false;
        try
        {
            permissionExists = checkExists(permission);
            if (permissionExists)
            {
                permissions.remove(permission);
                permission.setName(name);
                permissions.add(permission);
                return;
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException(
                "renamePermission(Permission,name)",
                e);
        }
        finally
        {
        }
        throw new UnknownEntityException(
            "Unknown permission '" + permission + "'");
    }
    /**
     * Determines if the <code>Permission</code> exists in the security system.
     * 
     * @param permission a <code>Permission</code> value
     * @return true if the permission exists in the system, false otherwise
     * @throws DataBackendException when more than one Permission with the same name exists.
     * @throws Exception A generic exception.
     */
    public boolean checkExists(Permission permission)
        throws DataBackendException
    {
        boolean exists = false;
        for (Iterator i = permissions.iterator(); i.hasNext();)
        {
            Permission p = (Permission) i.next();
            if (p.getName().equalsIgnoreCase(permission.getName()) | p.getId()
                == permission.getId())
            {
                exists = true;
            }
        }
        return exists;
    }

    /**
     * Removes a Permission from the system.
     * 
     * @param permission The object describing the permission to be removed.
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws UnknownEntityException if the permission does not exist.
     */
    public synchronized void removePermission(Permission permission)
        throws DataBackendException, UnknownEntityException
    {
        boolean permissionExists = false;
        try
        {
            permissionExists = checkExists(permission);
            if (permissionExists)
            {
                permissions.remove(permission);
            }
            else
            {
                throw new UnknownEntityException(
                    "Unknown permission '" + permission + "'");
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException("removePermission(Permission)", e);
        }
        finally
        {
        }
    }
    /**
     * Creates a new permission with specified attributes.
     * 
     * @param permission the object describing the permission to be created.
     * @return a new Permission object that has id set up properly.
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws EntityExistsException if the permission already exists.
     */
    protected synchronized Permission persistNewPermission(Permission permission)
        throws DataBackendException
    {

        permission.setId(getUniqueId());
        permissions.add(permission);
        return permission;

    }
   

    private Integer getUniqueId()
    {
        return new Integer(++uniqueId);
    }
}
