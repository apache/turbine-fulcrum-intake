package org.apache.fulcrum.security;
/*
 * ==================================================================== The Apache Software
 * License, Version 1.1
 * 
 * Copyright (c) 2001-2003 The Apache Software Foundation. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *  1. Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided
 * with the distribution.
 *  3. The end-user documentation included with the redistribution, if any, must include the
 * following acknowledgment: "This product includes software developed by the Apache Software
 * Foundation (http://www.apache.org/)." Alternately, this acknowledgment may appear in the
 * software itself, if and wherever such third-party acknowledgments normally appear.
 *  4. The names "Apache" and "Apache Software Foundation" and "Apache Turbine" must not be used to
 * endorse or promote products derived from this software without prior written permission. For
 * written permission, please contact apache@apache.org.
 *  5. Products derived from this software may not be called "Apache", "Apache Turbine", nor may
 * "Apache" appear in their name, without prior written permission of the Apache Software
 * Foundation.
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
import org.apache.avalon.framework.component.Component;
/**
 * The Security Service manages Users, Groups Roles and Permissions in the system.
 * 
 * The task performed by the security service include providing access to the various types of
 * managers.
 * 
 * <p>
 * Because of pluggable nature of the Services, it is possible to create multiple implementations
 * of SecurityService, for example employing database and directory server as the data backend.
 * <br>
 * 
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public interface SecurityService extends Component
{
    String ROLE = SecurityService.class.getName();

    /**
	 * Returns the configured UserManager.
	 * 
	 * @return An UserManager object
	 */
    UserManager getUserManager();
    /**
	 * Returns the configured GroupManager.
	 * 
	 * @return An UserManager object
	 */
    GroupManager getGroupManager();
    /**
	 * Returns the configured RoleManager.
	 * 
	 * @return An RoleManager object
	 */
    RoleManager getRoleManager();
    /**
	 * Returns the configured PermissionManager.
	 * 
	 * @return An PermissionManager object
	 */
    PermissionManager getPermissionManager();
	/**
	 * Returns the configured ModelManager object that can then
	 * be casted to the specific model.
	 * 
	 * @return An ModelManager object
	 */
	ModelManager getModelManager();    

}
