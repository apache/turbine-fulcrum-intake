package org.apache.fulcrum.yaafi.service.baseservice;

/*
 * Copyright 2004 Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;

/**
 * Base class for a service implementation capturing the Avalon
 * configuration artifats
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public abstract class BaseServiceImpl
	extends AbstractLogEnabled
    implements BaseService
{
    /** The name of the service - we don't know it yet */
    // private String name;
    
    /** The context supplied by the avalon framework */
    private Context context;
    
    /** The service manager supplied by the avalon framework */
    private ServiceManager serviceManager;
    
    /** The configuraton supplied by the avalon framework */
    private Configuration configuration;
    
    /** The parameters supplied by the avalon framework */
    private Parameters parameters;
    
    /**
     * @see org.apache.avalon.framework.context.Contextualizable#contextualize(org.apache.avalon.framework.context.Context)
     */
    public void contextualize(Context context) throws ContextException
    {
        this.context = context;
    }

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceContainer)
     */
    public void service(ServiceManager serviceManager) throws ServiceException
    {
        this.serviceManager = serviceManager;
    }
    
    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration configuration) throws ConfigurationException
    {
        this.configuration = configuration;
    }

    /**
     * @see org.apache.avalon.framework.parameters.Parameterizable#parameterize(org.apache.avalon.framework.parameters.Parameters)
     */
    public void parameterize(Parameters parameters) throws ParameterException
    {
        this.parameters = parameters;
    }
    
    /**
     * @return Returns the configuration.
     */
    protected Configuration getConfiguration()
    {
        return this.configuration;
    }
    
    /**
     * @return Returns the context.
     */
    protected Context getContext()
    {
        return this.context;
    }
        
    /**
     * @return Returns the parameters.
     */
    protected Parameters getParameters()
    {
        return this.parameters;
    }
    
    /**
     * @return Returns the serviceManager.
     */
    protected ServiceManager getServiceManager()
    {
        return this.serviceManager;
    }
}
