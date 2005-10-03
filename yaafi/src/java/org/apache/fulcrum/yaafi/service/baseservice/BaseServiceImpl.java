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

import java.io.File;

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
 * serviceConfiguration artifacts. Take care that using this class
 * introduces a dependency to the YAAFI library.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public abstract class BaseServiceImpl
    extends AbstractLogEnabled
    implements BaseService
{
    /** The name of the service as defined in the role configuration file */
    private String serviceName;

    /** The context supplied by the Avalon framework */
    private Context serviceContext;

    /** The service manager supplied by the Avalon framework */
    private ServiceManager serviceManager;

    /** The configuraton supplied by the Avalon framework */
    private Configuration serviceConfiguration;

    /** The parameters supplied by the avalon framework */
    private Parameters serviceParameters;

    /** the Avalon application directory */
    private File serviceApplicationDir;

    /** the Avalon temp directory */
    private File serviceTempDir;

    /** the Avalon partition name */
    private String servicePartitionName;

    /** the class loader for this service */
    private ClassLoader serviceClassLoader;

    /////////////////////////////////////////////////////////////////////////
    // Avalon Lifecycle Implementation
    /////////////////////////////////////////////////////////////////////////

    /**
     * Constructor
     */
    public BaseServiceImpl()
    {
        // nothing to do
    }

    /**
     * @see org.apache.avalon.framework.context.Contextualizable#contextualize(org.apache.avalon.framework.context.Context)
     */
    public void contextualize(Context context) throws ContextException
    {
        this.serviceContext = context;
        this.serviceName = (String) context.get("urn:avalon:name");
        this.serviceApplicationDir = (File) context.get("urn:avalon:home");
        this.serviceTempDir = (File) context.get("urn:avalon:temp");
        this.servicePartitionName = (String) context.get("urn:avalon:partition");
        this.serviceClassLoader = (ClassLoader) context.get("urn:avalon:classloader");
    }

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager serviceManager) throws ServiceException
    {
        this.serviceManager = serviceManager;
    }

    /**
     * @see org.apache.avalon.framework.serviceConfiguration.Configurable#configure(org.apache.avalon.framework.serviceConfiguration.Configuration)
     */
    public void configure(Configuration configuration) throws ConfigurationException
    {
        this.serviceConfiguration = configuration;
    }

    /**
     * @see org.apache.avalon.framework.servieParameters.Parameterizable#parameterize(org.apache.avalon.framework.servieParameters.Parameters)
     */
    public void parameterize(Parameters parameters) throws ParameterException
    {
        this.serviceParameters = parameters;
    }

    /**
     * @see org.apache.avalon.framework.configuration.Reconfigurable#reconfigure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void reconfigure(Configuration configuration) throws ConfigurationException
    {
        this.serviceConfiguration = configuration;
    }

    /**
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     */
    public void dispose()
    {
        this.serviceApplicationDir = null;
        this.serviceClassLoader = null;
        this.serviceConfiguration = null;
        this.serviceContext = null;
        this.serviceManager = null;
        this.serviceName = null;
        this.serviceParameters = null;
        this.servicePartitionName = null;
        this.serviceTempDir = null;
    }

    /////////////////////////////////////////////////////////////////////////
    // Service Implementation
    /////////////////////////////////////////////////////////////////////////

    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        StringBuffer result = new StringBuffer();

        result.append( getClass().getName() + "@" + Integer.toHexString(hashCode()));

        result.append("{");

        result.append("serviceName: ");
        result.append(this.getServiceName());
        result.append(";");

        result.append(" servicePartitionName: ");
        result.append(this.getServicePartitionName());
        result.append(";");

        result.append(" serviceApplicatonDir: ");
        result.append(this.getServiceApplicationDir().getAbsolutePath());
        result.append(";");

        result.append(" serviceTempDir: ");
        result.append(this.getServiceTempDir().getAbsolutePath());
        result.append(";");

        result.append(" serviceContext: ");
        result.append(this.getServiceContext().toString());
        result.append(";");

        result.append(" serviceConfiguration: ");
        result.append(this.getServiceConfiguration().toString());
        result.append(";");

        result.append(" serviceParameters: ");
        result.append(Parameters.toProperties(this.getServiceParameters()));
        result.append(";");

        result.append(" serviceClassLoader: ");
        result.append(this.getServiceClassLoader());
        result.append(";");

        result.append(" serviceLogger: ");
        result.append(this.getLogger());
        result.append(";");

        result.append(" serviceManager: ");
        result.append(this.getServiceManager());

        result.append("}");

        return result.toString();
    }

    /**
     * @see org.apache.avalon.framework.service.ServiceManager#hasService(java.lang.String)
     */
    protected boolean hasService(String key)
    {
        return this.getServiceManager().hasService(key);
    }

    /**
     * @see org.apache.avalon.framework.service.ServiceManager#lookup(java.lang.String)
     */
    protected Object lookup(String key)
    {
        try
        {
            return this.getServiceManager().lookup(key);
        }
        catch (ServiceException e)
        {
            String msg = "Unable to lookup the following service : " + key;
            this.getLogger().error(msg,e);
            throw new RuntimeException(msg);
        }
    }

    /**
     * @see org.apache.avalon.framework.service.ServiceManager#release(java.lang.Object)
     */
    protected void release(Object object)
    {
        this.release(object);
    }

    /**
     * Determines the absolute file based on the application directory
     * @param fileName the filename
     * @return the absolute file
     */
    protected File createAbsoluteFile( String fileName )
    {
        File result = new File(fileName);

        if( result.isAbsolute() == false )
        {
            result = new File( this.getServiceApplicationDir(), fileName );
        }

        return result;
    }

    /**
     * Determines the absolute path based on the application directory
     * @param fileName the filename
     * @return the absolute path
     */
    protected String createAbsolutePath( String fileName )
    {
        return this.createAbsoluteFile(fileName).getAbsolutePath();
    }

    /**
     * @return Returns the serviceApplicationDir.
     */
    protected File getServiceApplicationDir()
    {
        return serviceApplicationDir;
    }

    /**
     * @return Returns the serviceClassLoader.
     */
    protected ClassLoader getServiceClassLoader()
    {
        return serviceClassLoader;
    }

    /**
     * @return Returns the serviceConfiguration.
     */
    protected Configuration getServiceConfiguration()
    {
        return serviceConfiguration;
    }

    /**
     * @return Returns the serviceContext.
     */
    protected Context getServiceContext()
    {
        return serviceContext;
    }

    /**
     * @return Returns the serviceManager.
     */
    protected ServiceManager getServiceManager()
    {
        return serviceManager;
    }

    /**
     * @return Returns the serviceName.
     */
    protected String getServiceName()
    {
        return serviceName;
    }

    /**
     * @return Returns the serviceParameters.
     */
    protected Parameters getServiceParameters()
    {
        return serviceParameters;
    }

    /**
     * @return Returns the servicePartitionName.
     */
    protected String getServicePartitionName()
    {
        return servicePartitionName;
    }

    /**
     * @return Returns the serviceTempDir.
     */
    protected File getServiceTempDir()
    {
        return serviceTempDir;
    }
}
