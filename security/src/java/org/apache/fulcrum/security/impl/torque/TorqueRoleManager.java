package org.apache.fulcrum.security.impl.torque;
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
 * 3. The end-group documentation included with the redistribution,
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

import org.apache.fulcrum.security.util.DataBackendException;
import org.apache.fulcrum.security.util.RoleSet;
import org.apache.fulcrum.security.util.UnknownEntityException;
import org.apache.torque.util.Criteria;
import org.apache.fulcrum.security.RoleManager;
/**
 * An GroupManager performs {@link org.apache.fulcrum.security.entity.Group} objects
 * related tasks on behalf of the {@link org.apache.fulcrum.security.BaseSecurityService}.
 *
 * The responsibilities of this class include loading data of an group from the
 * storage and putting them into the {@link org.apache.fulcrum.security.entity.Group} objects,
 * saving those data to the permanent storage.
 *
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public interface TorqueRoleManager extends RoleManager
{
   
	/**
	 * Retrieve a set of Roles that meet the specified Criteria.
	 *
	 * @param criteria a Criteria of Roles selection.
	 * @return a set of Roles that meet the specified Criteria.
	 * @throws DataBackendException if there was an error accessing the data
	 *         backend.
	 */
	RoleSet getRoles(Criteria criteria)
			throws DataBackendException;
			
	/**
	   * Returns the Class object for the implementation of Role interface
	   * used by the system.
	   *
	   * @return the implementation of Role interface used by the system.
	   * @throws UnknownEntityException if the system's implementation of Role
	   *         interface could not be determined.
	   */
	  Class getRoleClass()
			  throws UnknownEntityException;
			
}
