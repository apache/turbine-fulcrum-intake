package org.apache.fulcrum.security;
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
import org.apache.avalon.framework.component.Component;
/**
 * The Security Service manages Users, Groups Roles and Permissions in the
 * system.
 *
 * The task performed by the security service include creation and removal of
 * accounts, groups, roles, and permissions; assigning users roles in groups;
 * assigning roles specific permissions and construction of objects
 * representing these logical entities.
 *
 * <p> Because of pluggable nature of the Services, it is possible to create
 * multiple implementations of SecurityService, for example employing database
 * and directory server as the data backend.<br>
 *
 * @author <a href="mailto:Rafal.Krzewski@e-point.pl">Rafal Krzewski</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:marco@intermeta.de">Marco Kn&uuml;ttel</a>
 * @version $Id$
 */
public interface SecurityService extends Component
{
    String ROLE = SecurityService.class.getName();
    /** the key within services's properties for secure passwords flag (secure.passwords) */
    public static final String SECURE_PASSWORDS_KEY = "secure.passwords";
    /** the value of secure passwords flag (false) */
    public static final String SECURE_PASSWORDS_DEFAULT = "false";
    /** the key within services's properties for secure passwords algorithm (secure.passwords.algorithm) */
    public static final String SECURE_PASSWORDS_ALGORITHM_KEY = "secure.passwords.algorithm";
    /** the default algorithm for password encryption (SHA) */
    public static final String SECURE_PASSWORDS_ALGORITHM_DEFAULT = "SHA";
   
    /**
     * Returns the configured UserManager.
     *
     * @return An UserManager object
     */
    UserManager getUserManager();
    /**
     * Configure a new user Manager.
     *
     * @param userManager An UserManager object
     */
   // void setUserManager(UserManager userManager);
    /**
      * Returns the configured GroupManager.
      *
      * @return An UserManager object
      */
    GroupManager getGroupManager();
    /**
     * Configure a new group Manager.
     *
     * @param userManager An GroupManager object
     */
    //void setGroupManager(GroupManager userManager);
    /**
      * Returns the configured RoleManager.
      *
      * @return An RoleManager object
      */
    RoleManager getRoleManager();
    /**
     * Configure a new role Manager.
     *
     * @param roleManager An RoleManager object
     */
   // void setRoleManager(RoleManager roleManager);
    /**
    	 * Returns the configured PermissionManager.
    	 *
    	 * @return An PermissionManager object
    	 */
    PermissionManager getPermissionManager();
    /**
    	* Configure a new role Manager.
    	*
    	* @param -ermissionManager An PermissionManager object
    	*/
   // void setPermissionManager(PermissionManager permissionManager);
}