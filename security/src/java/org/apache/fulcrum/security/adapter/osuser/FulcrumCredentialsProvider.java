package org.apache.fulcrum.security.adapter.osuser;
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

import org.apache.fulcrum.security.entity.User;
import org.apache.fulcrum.security.util.DataBackendException;
import org.apache.fulcrum.security.util.PasswordMismatchException;
import org.apache.fulcrum.security.util.UnknownEntityException;

import com.opensymphony.user.Entity.Accessor;
import com.opensymphony.user.provider.CredentialsProvider;

/**
 * Fulcrum provider for OSUser.  Primarily provides support for authenticating
 * a user.  This delegates to whatever authenticator is configured in the
 * SecurityService.
 * 
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class FulcrumCredentialsProvider
	extends BaseFulcrumProvider
	implements CredentialsProvider
{

	/*
	 * Authenticate a user with their password.
	 * 
	 * @see com.opensymphony.user.provider.CredentialsProvider#authenticate(java.lang.String,
	 *      java.lang.String)
	 */
	public boolean authenticate(String name, String password)
	{
		try
		{
			User user = securityService.getUserManager().getUser(name);
			securityService.getUserManager().authenticate(user, password);
			return true;
		}
		catch (PasswordMismatchException pme)
		{
			return false;
		}
		catch (UnknownEntityException uee)
		{
			return false;
		}
		catch (DataBackendException dbe)
		{
			throw new RuntimeException(dbe);
		}

	}

	/*
	 * Not implemented.
	 * 
	 * @see com.opensymphony.user.provider.CredentialsProvider#changePassword(java.lang.String,
	 *      java.lang.String)
	 */
	public boolean changePassword(String arg0, String arg1)
	{
		return false;
	}

	/*
	 * Not implemented.
	 * 
	 * @see com.opensymphony.user.provider.UserProvider#create(java.lang.String)
	 */
	public boolean create(String arg0)
	{
		return false;
	}

	/*
	 * Does nothing.
	 * 
	 * @see com.opensymphony.user.provider.UserProvider#flushCaches()
	 */
	public void flushCaches()
	{

	}

	/*
	 * Returns whether a user exists or not.
	 * 
	 * @see com.opensymphony.user.provider.UserProvider#handles(java.lang.String)
	 */
	public boolean handles(String name)
	{
		try
		{
			User user = securityService.getUserManager().getUser(name);
			return true;
		}
		catch (UnknownEntityException uee)
		{
			return false;
		}
		catch (DataBackendException dbe)
		{
			throw new RuntimeException(dbe);
		}
	}

	/*
	 * Not implemented.
	 * 
	 * @see com.opensymphony.user.provider.UserProvider#list()
	 */
	public List list()
	{
		return null;
	}

	/*
	 * Not implemented.
	 * 
	 * @see com.opensymphony.user.provider.UserProvider#remove(java.lang.String)
	 */
	public boolean remove(String arg0)
	{
		return false;
	}

	/*
	 * Not implemented.
	 * 
	 * @see com.opensymphony.user.provider.UserProvider#store(java.lang.String,
	 *      com.opensymphony.user.Entity.Accessor)
	 */
	public boolean store(String arg0, Accessor arg1)
	{
		return false;
	}

}
