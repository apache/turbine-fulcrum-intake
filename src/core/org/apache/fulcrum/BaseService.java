package org.apache.fulcrum;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
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
import java.util.List;
import org.apache.log4j.Category;
//!! this must go away we want to use a configuration
// interface.
import org.apache.velocity.runtime.configuration.Configuration;

/**
 * This class is a generic implementation of <code>Service</code>.
 *
 * @author <a href="mailto:burton@apache.org">Kevin Burton</a>
 * @author <a href="mailto:krzewski@e-point.pl">Rafal Krzewski</a>
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @author <a href="mailto:leonardr@segfault.org">Leonard Richardson</a>
 * @version $Id$
 */
public class BaseService 
    implements Service
{
    /** 
     * Initialization status of this class. 
     */
    protected boolean isInitialized = false;
    
    /** 
     * A reference to the ServiceBroker that instantiated this object. 
     */
    protected ServiceBroker serviceBroker;

    /** 
     * The properties of this service. These will
     * soon go away. We will use a Configuration
     * interface.
     */
    protected Properties properties;

    /**
     * Configuration for this service.
     */
    protected Configuration configuration;
     
    /** 
     * The name of this Service. 
     */
    protected String name;

    /**
     * Performs late initializtion.  Called when the service is requested 
     * for the first time (if not already completely initialized by the 
     * early initializer).
     *
     * Late intialization of a BaseInitable is alwas successful.
     *
     * @exception InitializationException Initialization of this
     * class was not successful.
     */
    public void init() 
        throws InitializationException
    {
    }

    /**
     * Returns an Initable to uninitialized state.
     *
     * Calls setInit(flase) to mark that we are no longer in initialized
     * state.
     */
    public void shutdown()
    {
        setInit(false);
    }

    
    /**
     * Returns initialization status.
     *
     * @return True if the service is initialized.
     * @deprecated use isInitialized() which uses proper bean semantics.
     */
    public boolean getInit()
    {
        return isInitialized;
    }

    /**
     * @see org.apache.fulcrum.Service#getStatus()
     */
    public String getStatus()
        throws ServiceException
    {
        return (getInit() ? "Initialized" : "Uninitialized");
    }

    /**
     * Returns initialization status.
     *
     * @return True if the service is initialized.
     */
    public boolean isInitialized()
    {
        return isInitialized;
    }        

    /**
     * Sets initailization status.
     *
     * @param value The new initialization status.
     */
    protected void setInit( boolean value )
    {
        this.isInitialized = value;
    }

    /**
     * ServiceBroker uses this method to pass a Service its name.
     *
     * @param name The name of this Service.
     */
    public void setName( String name )
    {
        this.name = name;
    }

    /**
     * Returns the name of this service.
     *
     * @return The name of this Service.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Saves a reference to the ServiceBroker that instantiated this
     * object, so that it can ask for its properties and access other
     * Services.
     *
     * @param broker The ServiceBroker that instantiated this object.
     */
    public void setServiceBroker( ServiceBroker broker )
    {
        this.serviceBroker = broker;
    }

    /**
     * Returns a ServiceBroker reference.
     *
     * @return The ServiceBroker that instantiated this object.
     */
    public ServiceBroker getServiceBroker()
    {
        return serviceBroker;
    }

    /**
     * Returns the configuration of this Service.
     *
     * @return The Configuration of this Service.
     */
    public Configuration getConfiguration()
    {
        if (name == null)
        {
            return null;
        }
        
        if (configuration == null)
        {
            configuration = getServiceBroker().getConfiguration(name);
        }
        
        return configuration;
    }

    public Object getServiceObject(String name)
    {
        return getServiceBroker().getServiceObject(name);
    }        

    public String getRealPath(String path)
    {
        return getServiceBroker().getRealPath(path);
    }

    public Category getCategory()
    {
        return getServiceBroker().getCategory();
    }        

    public Category getCategory(String name)
    {
        return getServiceBroker().getCategory(name);
    }        
}
