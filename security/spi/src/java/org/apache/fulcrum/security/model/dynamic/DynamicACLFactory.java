package org.apache.fulcrum.security.model.dynamic;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.security.acl.AccessControlList;
import org.apache.fulcrum.security.entity.Group;
import org.apache.fulcrum.security.entity.User;
import org.apache.fulcrum.security.model.ACLFactory;
import org.apache.fulcrum.security.model.dynamic.entity.DynamicGroup;
import org.apache.fulcrum.security.model.dynamic.entity.DynamicRole;
import org.apache.fulcrum.security.model.dynamic.entity.DynamicUser;
import org.apache.fulcrum.security.spi.AbstractManager;
import org.apache.fulcrum.security.util.RoleSet;
import org.apache.fulcrum.security.util.UnknownEntityException;
/**
 * 
 * This factory creates instance of the DynamicAccessControlList
 * 
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class DynamicACLFactory extends AbstractManager implements ACLFactory
{

    /** Logging */
    private static Log log = LogFactory.getLog(DynamicACLFactory.class);
	/**
	   * Construct a new ACL object.
	   *
	   * This constructs a new ACL object from the configured class and
	   * initializes it with the supplied roles and permissions.
	   *
	   * @param roles The roles that this ACL should contain
	   * @param permissions The permissions for this ACL
	   *
	   * @return an object implementing ACL interface.
	   * @throws UnknownEntityException if the object could not be instantiated.
	   */
	  private AccessControlList getAclInstance(Map roles, Map permissions)
		  throws UnknownEntityException
	  {
		  Object[] objects = { roles, permissions };
		  String[] signatures = { Map.class.getName(), Map.class.getName()};
		  AccessControlList accessControlList;
		  try
		  {
			  /*
			   * 
			   @todo I think this is overkill for now..
			  accessControlList =
				  (AccessControlList) aclFactoryService.getInstance(aclClass.getName(), objects, signatures);
				  */
			  accessControlList =
				  new DynamicAccessControlListImpl(roles, permissions);
		  }
		  catch (Exception e)
		  {
			  throw new UnknownEntityException(
				  "Failed to instantiate an ACL implementation object",
				  e);
		  }
		  return accessControlList;
	  }
	  public AccessControlList getAccessControlList(User user)
	  {
		  Map roleSets = new HashMap();
		  Map permissionSets = new HashMap();
		  for (Iterator i = ((DynamicUser) user).getGroups().iterator();
			  i.hasNext();
			  )
		  {
			  Group group = (Group) i.next();
			  RoleSet roleSet = (RoleSet) ((DynamicGroup) group).getRoles();
			  roleSets.put(group, roleSet);
			  for (Iterator j = roleSet.iterator(); j.hasNext();)
			  {
				  DynamicRole role = (DynamicRole) j.next();
				  permissionSets.put(role, role.getPermissions());
			  }
		  }
		  try
		  {
			  return getAclInstance(roleSets, permissionSets);
		  }
		  catch (UnknownEntityException uue)
		  {
			  throw new RuntimeException(uue.getMessage(), uue);
		  }
	  }

}