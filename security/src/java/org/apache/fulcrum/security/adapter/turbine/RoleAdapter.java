package org.apache.fulcrum.security.adapter.turbine;
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

import org.apache.fulcrum.security.entity.SecurityEntity;
import org.apache.turbine.om.security.Permission;
import org.apache.turbine.om.security.Role;
import org.apache.turbine.util.security.PermissionSet;
import org.apache.turbine.util.security.TurbineSecurityException;
/**
 * Adapter around Fulcrum Role.
 *
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class RoleAdapter extends BaseAdapter implements Role
{

   public RoleAdapter(org.apache.fulcrum.security.entity.Role role){
   	super((SecurityEntity)role);
   }

    public void setPermissions(PermissionSet arg0)
    {
		throw new RuntimeException("Unsupported operation");
    }
    public PermissionSet getPermissions()
    {
		throw new RuntimeException("Unsupported operation");
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.Role#create(java.lang.String)
     */
    public Role create(String arg0) throws TurbineSecurityException
    {
		throw new RuntimeException("Unsupported operation");
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.Role#grant(org.apache.turbine.om.security.Permission)
     */
    public void grant(Permission arg0) throws TurbineSecurityException
    {
		throw new RuntimeException("Unsupported operation");
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.Role#grant(org.apache.turbine.util.security.PermissionSet)
     */
    public void grant(PermissionSet arg0) throws TurbineSecurityException
    {
		throw new RuntimeException("Unsupported operation");
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.Role#revoke(org.apache.turbine.om.security.Permission)
     */
    public void revoke(Permission arg0) throws TurbineSecurityException
    {
		throw new RuntimeException("Unsupported operation");
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.Role#revoke(org.apache.turbine.util.security.PermissionSet)
     */
    public void revoke(PermissionSet arg0) throws TurbineSecurityException
    {
		throw new RuntimeException("Unsupported operation");
    }
}
