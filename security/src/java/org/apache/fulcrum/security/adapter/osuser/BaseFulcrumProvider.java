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
import java.util.Properties;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.security.SecurityService;
import org.apache.fulcrum.security.util.DataBackendException;

import com.opensymphony.user.Entity.Accessor;
import com.opensymphony.user.provider.UserProvider;

/**
 * Base implementation of the Fulcrum provider for OSUser. This is meant to
 * provide access from OSUser to the Fulcrum Security implementation.
 * Currently, to change things you should use the Fulcrum Security system
 * directly, this is a very mimimal implementation.
 * 
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public abstract class BaseFulcrumProvider extends AbstractLogEnabled implements UserProvider,Composable, Disposable,Component
{
	/** Component Manager to query for the SecurityService through */
	protected ComponentManager manager = null;
	/** Logging */
	private static Log log = LogFactory.getLog(BaseFulcrumProvider.class);
	/** Our Fulcrum Security Service to use */
	private SecurityService securityService;

	/*
	 * Does nothing for now.
	 * 
	 * @see com.opensymphony.user.provider.UserProvider#create(java.lang.String)
	 */
	public boolean create(String arg0)
	{
		return true;
	}

	/*
	 * Does nothign for now.
	 * 
	 * @see com.opensymphony.user.provider.UserProvider#flushCaches()
	 */
	public void flushCaches()
	{

	}

	/*
	 * Doesn't do anything. Init isn't required as the Fulcrum Security is
	 * assumed to be running in an Avalon container.
	 * 
	 * @see com.opensymphony.user.provider.UserProvider#init(java.util.Properties)
	 */
	public boolean init(Properties arg0)
	{
		return true;
	}

	/*
	 * Sets the accessor to be mutable, and returns true.
	 * 
	 * @see com.opensymphony.user.provider.UserProvider#load(java.lang.String,
	 *      com.opensymphony.user.Entity.Accessor)
	 */
	public boolean load(String name, Accessor accessor)
	{
		accessor.setMutable(true);

		return true;
	}

	/*
	 * Returns false, this doesn't do anything.
	 * 
	 * @see com.opensymphony.user.provider.UserProvider#remove(java.lang.String)
	 */
	public boolean remove(String arg0)
	{
		return false;
	}

	/*
	 * Returns false, this doesn't do anything right now.
	 * 
	 * @see com.opensymphony.user.provider.UserProvider#store(java.lang.String,
	 *      com.opensymphony.user.Entity.Accessor)
	 */
	public boolean store(String arg0, Accessor arg1)
	{
		return false;
	}

	/**
	  * Lazy loads the SecurityService.
	  * 
	  * @return
	  */
	 SecurityService getSecurityService() throws DataBackendException
	 {
		 if (securityService == null)
		 {
			 try
			 {
				securityService = (SecurityService) manager.lookup(SecurityService.ROLE);
			 }
			 catch (ComponentException ce)
			 {
				 throw new DataBackendException(ce.getMessage(), ce);
			 }
		 }
		 return securityService;
	 }
	
	/**
	 * The Fulcrum Security Service that will back the Fulcrum
	 * providers.
	 * 
	 * @param securityService
	 *            The securityService to set.
	 */
	public void setSecurityService(SecurityService securityService)
	{
		this.securityService = securityService;
	}

	/**
	  * Avalon component lifecycle method
	  */
	public void compose(ComponentManager manager) throws ComponentException
	{
		this.manager = manager;
	}
	public void dispose()
	{
		manager = null;
		securityService = null;
		
	}
}
