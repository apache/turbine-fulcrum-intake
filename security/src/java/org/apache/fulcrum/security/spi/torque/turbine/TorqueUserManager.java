package org.apache.fulcrum.security.spi.torque.turbine;
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
import java.util.List;
import java.util.Map;

import org.apache.fulcrum.security.acl.AccessControlList;
import org.apache.fulcrum.security.model.turbine.manager.TurbineUserManager;
import org.apache.fulcrum.security.util.DataBackendException;
import org.apache.fulcrum.security.util.UnknownEntityException;
import org.apache.torque.util.Criteria;
/**
 * This User managers adds the Torque Criteria class and listUsers.
 *
 * @author <a href="mailto:Rafal.Krzewski@e-point.pl">Rafal Krzewski</a>
 * @version $Id$
 */
public interface TorqueUserManager extends SessionAwareUserManager, TurbineUserManager
{
   
    /**
     * Retrieve a list of users that meet the specified criteria.
     *
     * As the keys for the criteria, you should use the constants that
     * are defined in {@link User} interface, plus the names
     * of the custom attributes you added to your user representation
     * in the data storage. Use verbatim names of the attributes -
     * without table name prefix in case of DB implementation.
     *
     * @param criteria The criteria of selection.
     * @return a List of users meeting the criteria.
     * @throws DataBackendException if there is a problem accessing the
     *         storage.
     */
    List retrieveList(Criteria criteria) throws DataBackendException;
    
    
	/**
		 * Returns the Class object for the implementation of User interface
		 * used by the system.
		 *
		 * @return the implementation of User interface used by the system.
		 * @throws UnknownEntityException if the system's implementation of User
		 *         interface could not be determined.
		 */
		Class getUserClass()
				throws UnknownEntityException;

		
		
	   

	/**
	 * Retrieve a set of users that meet the specified criteria.
	 *
	 * As the keys for the criteria, you should use the constants that
	 * are defined in {@link User} interface, plus the names
	 * of the custom attributes you added to your user representation
	 * in the data storage. Use verbatim names of the attributes -
	 * without table name prefix in case of Torque implementation.
	 *
	 * @param criteria The criteria of selection.
	 * @return a List of users meeting the criteria.
	 * @throws DataBackendException if there is a problem accessing the
	 *         storage.
	 */
	List getUserList(Criteria criteria)
			throws DataBackendException;	  
			

	
	
	/**
		* Returns the Class object for the implementation of AccessControlList interface
		* used by the system.
		*
		* @return the implementation of AccessControlList interface used by the system.
		* @throws UnknownEntityException if the system's implementation of AccessControlList
		*         interface could not be determined.
		*/
	   Class getAclClass()
			   throws UnknownEntityException;

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
	   AccessControlList getAclInstance(Map roles, Map permissions)
			   throws UnknownEntityException;		

	/**
			 * This method provides client-side encryption mechanism for passwords.
			 *
			 * This is an utility method that is used by other classes to maintain
			 * a consistent approach to encrypting password. The behavior of the
			 * method can be configured in service's properties.
			 *
			 * @param password the password to process
			 * @return processed password
			 */
			String encryptPassword(String password);

			/**
			 * This method provides client-side encryption mechanism for passwords.
			 *
			 * This is an utility method that is used by other classes to maintain
			 * a consistent approach to encrypting password. The behavior of the
			 * method can be configured in service's properties.
			 *
			 * Algorithms that must supply a salt for encryption
			 * can use this method to provide it.
			 *
			 * @param password the password to process
			 * @param salt Salt parameter for some crypto algorithms
			 *
			 * @return processed password
			 */
			String encryptPassword(String password, String salt);

			/**
			 * Checks if a supplied password matches the encrypted password
			 * when using the current encryption algorithm
			 *
			 * @param checkpw      The clear text password supplied by the user
			 * @param encpw        The current, encrypted password
			 *
			 * @return true if the password matches, else false
			 *
			 */
			boolean checkPassword(String checkpw, String encpw);			   	   			
}
