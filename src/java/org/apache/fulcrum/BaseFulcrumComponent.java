package org.apache.fulcrum;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2002 The Apache Software Foundation.  All rights
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

import org.apache.commons.configuration.Configuration;
// also uses org.apache.avalon.framework.configuration.Configuration

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.parameters.Parameters;


/**
 * This class is a generic implementation of <code>Service</code>.
 *
 * @author <a href="mailto:jmcnally@apache.org">John McNally</a>
 * @version $Id$
 */
public abstract class BaseFulcrumComponent 
    extends AbstractLogEnabled
    implements Contextualizable
{
    public static final String USE_PROPERTY_FILE = "use-property-file";

    /**
     * Initialization status of this class.
     */
    protected boolean isInitialized = false;

    private Configuration oldPropConf;

    public void contextualize(Context context)
        throws ContextException
    {
        if (context != null) 
        {
            try 
            {
                oldPropConf = (Configuration)context
                    .get(FulcrumContainer.PROP_CONF);
            }
            catch (ContextException e)
            {
                // ignored, oldPropConf can be null
            }
            
        }
    }

    protected boolean useOldConfiguration(Parameters parameters)
    {
        return parameters.getParameterAsBoolean(USE_PROPERTY_FILE, false);
            // && getConfiguration() != null;
    }
    
    protected boolean useOldConfiguration(
        org.apache.avalon.framework.configuration.Configuration  conf)
    {
        return conf.getAttributeAsBoolean(USE_PROPERTY_FILE, false);
    }
    
    /**
     * Returns the configuration for the specified service.
     *
     * @param name The name of the service.
     */
    protected Configuration getConfiguration()
    {
        return getConfiguration(getName());
    }

    protected abstract String getName();

    /**
     * Returns the configuration for the specified service.
     *
     * @param name The name of the service.
     */
    private Configuration getConfiguration( String name )
    {
        Configuration subset = null;
        if (oldPropConf != null) 
        {
            subset = oldPropConf.subset("services." + name);
        }
        System.out.println("Getting conf for " + name + ": " + subset);
        return subset;
    }


    /**
     * Returns initialization status.
     *
     * @return True if the service is initialized.
     * @see org.apache.fulcrum.Service#isInitialized()
     * @deprecated use isInitialized() which uses proper bean semantics.
     */
    public boolean getInit()
    {
        return isInitialized();
    }

    /**
     * Returns either <code>Initialized</code> or
     * <code>Uninitialized</code>, depending upon {@link
     * org.apache.fulcrum.Service} innitialization state.
     *
     * @see org.apache.fulcrum.Service#getStatus()
     */
    public String getStatus() throws ServiceException
    {
        return (isInitialized() ? "Initialized" : "Uninitialized");
    }

    /**
     * @see org.apache.fulcrum.Service#isInitialized()
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
    protected void setInit(boolean value)
    {
        this.isInitialized = value;
    }
}


