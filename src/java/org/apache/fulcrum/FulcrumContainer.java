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

package org.apache.fulcrum;

import java.io.StringBufferInputStream;
import java.io.InputStream;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.excalibur.component.ExcaliburComponentManager;
import org.apache.avalon.excalibur.component.DefaultRoleManager;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.context.Contextualizable;
//import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.log.Hierarchy;

public class FulcrumContainer
    extends AbstractLogEnabled
    implements Component, Contextualizable, Parameterizable,
               Initializable, Disposable 
{
    public static final String CONF_PATH = "fulcrum-configuration-path";
    public static final String CONF_XML = "fulcrum-configuration-xml";
    public static final String APP_ROOT = "application-root";
    public static final String PROP_CONF = "old.style.property.configuration";

    ExcaliburComponentManager manager = new ExcaliburComponentManager();
    private String confPath;
    private InputStream confStream;
    private String appRoot;
    private Context context;
    
    /**
     * Any values set through the Context 
     *
     * @param parameters a <code>Parameters</code> value
     */
    public void parameterize(Parameters parameters)
        throws ParameterException
    {
        if (confPath == null) 
        {
            confPath = parameters.getParameter(CONF_PATH);            
        }
        if (appRoot == null) 
        {
            appRoot = parameters.getParameter(APP_ROOT);
        }
    }

    public void contextualize(Context context)
        throws ContextException
    {
        if (context == null) 
        {
            this.context = new DefaultContext();
        }
        else 
        {
            this.context = context;
            // allow context info to override parameters
            try 
            {
                String contextConfPath = (String) context.get(CONF_PATH);
                if ( contextConfPath != null ) 
                {
                    confPath = contextConfPath;
                }
            }
            catch (ContextException e)
            {
                String contextConf = (String) context.get(CONF_XML);
                if ( contextConf != null ) 
                {
                    confStream = new StringBufferInputStream(contextConf);
                }                
            }
            
            String contextAppRoot = (String) context.get(APP_ROOT);
            if ( contextAppRoot != null ) 
            {
                appRoot = contextAppRoot;
            }
        }
    }

    public void initialize()
        throws Exception
    {
        DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
        Configuration sysConfig = null;
        if (confStream == null && confPath != null) 
        {
            sysConfig = builder.buildFromFile(confPath);            
        }
        else if (confStream != null) 
        {
            sysConfig = builder.build(confStream);            
        }
        else 
        {
            throw new IllegalStateException("component was not configured.");
        }
        

        Configuration roleConfig = builder.build(
            this.getClass().getResourceAsStream("avalon-roles.xml"));

        DefaultRoleManager roles = new DefaultRoleManager();
        roles.setLogger( Hierarchy.getDefaultHierarchy()
                         .getLoggerFor("fulcrum.roles") );
        roles.configure(roleConfig);

        this.manager.setLogger( Hierarchy.getDefaultHierarchy()
                                .getLoggerFor("fulcrum") );        
        this.manager.contextualize( context );
        this.manager.setRoleManager( roles );
        this.manager.configure( sysConfig );
        this.manager.initialize();

        ((BaseServiceBroker)TurbineServices.getInstance())
            .setAvalonManager(manager);
    }

    public void dispose()
    {
        this.manager.dispose();
    }
}
          
