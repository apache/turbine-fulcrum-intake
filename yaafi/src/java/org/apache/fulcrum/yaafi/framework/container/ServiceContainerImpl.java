package org.apache.fulcrum.yaafi.framework.container;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.fulcrum.yaafi.framework.component.AvalonServiceComponentImpl;
import org.apache.fulcrum.yaafi.framework.component.ServiceComponent;
import org.apache.fulcrum.yaafi.framework.configuration.ComponentConfigurationPropertiesResolver;
import org.apache.fulcrum.yaafi.framework.configuration.ComponentConfigurationPropertiesResolverImpl;
import org.apache.fulcrum.yaafi.framework.constant.AvalonYaafiConstants;
import org.apache.fulcrum.yaafi.framework.context.AvalonToYaafiContextMapper;
import org.apache.fulcrum.yaafi.framework.context.YaafiToAvalonContextMapper;
import org.apache.fulcrum.yaafi.framework.crypto.CryptoStreamFactory;
import org.apache.fulcrum.yaafi.framework.role.RoleConfigurationParser;
import org.apache.fulcrum.yaafi.framework.role.RoleConfigurationParserImpl;
import org.apache.fulcrum.yaafi.framework.role.RoleEntry;
import org.apache.fulcrum.yaafi.framework.util.ConfigurationUtil;
import org.apache.fulcrum.yaafi.framework.util.InputStreamLocator;
import org.apache.fulcrum.yaafi.framework.util.StringUtils;
import org.apache.fulcrum.yaafi.framework.util.ToStringBuilder;
import org.apache.fulcrum.yaafi.framework.util.Validate;

/**
 * Yet another avalon framework implementation (YAAFI).
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public class ServiceContainerImpl
    implements ServiceContainer, ServiceConstants
{
    /** Default timeout before disposing the container */
    private static final int DISPOSAL_DELAY_DEFAULT = 0;

    /** Default timeout before reconfiguring the container or services */
    private static final int RECONFIGURATION_DELAY_DEFAULT = 2000;

    /** The role configuration file to be used */
    private String componentRolesLocation;

    /** is the component role file encrypted? */
    private String isComponentRolesEncrypted;

    /** which flavour of component role file we have to parse? */
    private String componentRolesFlavour;

    /** The service configuration file to be used */
    private String componentConfigurationLocation;

    /** is the component configuration file encrypted? */
    private String isComponentConfigurationEncrypted;

    /** The parameters file to be used */
    private String parametersLocation;

    /** is the parameters file encrypted? */
    private String isParametersEncrypted;

    /** The application directory aka the current working directory */
    private File applicationRootDir;

    /** The directory for storing temporary files */
    private File tempRootDir;

    /** The logger to be used passed by the caller */
    private Logger logger;

    /** The service manager passed to the container */
    private ServiceManager parentServiceManager;

    /** The list of services instantiated */
    private List serviceList;

    /** The map of services used for the lookup */
    private HashMap serviceMap;

    /** The Avalon role configuration loaded by this class */
    private Configuration roleConfiguration;

    /** The Avalon service configuration loaded by this class */
    private Configuration serviceConfiguration;

    /** The temporary Avalon context passed to the implementation */
    private Context callerContext;

    /** The default Avalon context passed to the services */
    private Context context;

    /** The default Avalon parameters */
    private Parameters parameters;

    /** Is this instance already disposed? */
    private volatile boolean isAlreadyDisposed;

    /** Is this instance currently disposed? */
    private volatile boolean isCurrentlyDisposing;

    /** The type of container where YAAFI is embedded */
    private String containerFlavour;

    /** The ms to wait before triggering a reconfiguration of the container or service */
    private int reconfigurationDelay;

    /** The ms to wait before triggering a disposal of the container */
    private int disposalDelay;

    /** global flag for enabling/disabling dynamic proxies */
    private boolean hasDynamicProxies;

    /** The list of interceptor services applied to all services */
    private ArrayList defaultInterceptorServiceList;

    /** The list of ServiceManagers as fallback service lookup */
    private ArrayList fallbackServiceManagerList;

    /** the configuration for running the ComponentConfigurationPropertiesResolver */
    private Configuration componentConfigurationPropertiesResolverConfig;

    /////////////////////////////////////////////////////////////////////////
    // Avalon Service Lifecycle
    /////////////////////////////////////////////////////////////////////////

    /**
     * Constructor using sensible defaults.
     */
    public ServiceContainerImpl()
    {
        super();

        this.containerFlavour = COMPONENT_CONTAINERFLAVOUR_VALUE;
        this.componentRolesFlavour = COMPONENT_ROLECONFIGFLAVOUR_VALUE;

        this.componentRolesLocation = COMPONENT_ROLE_VALUE;
        this.componentConfigurationLocation = COMPONENT_CONFIG_VALUE;
        this.parametersLocation = COMPONENT_PARAMETERS_VALUE;

        this.isComponentConfigurationEncrypted = "false";
        this.isComponentRolesEncrypted = "false";
        this.isParametersEncrypted = "false";

        this.isAlreadyDisposed = false;
        this.isCurrentlyDisposing = false;

        this.serviceList = new ArrayList();
        this.serviceMap = new HashMap();

        this.applicationRootDir = new File( new File("").getAbsolutePath() );
        this.tempRootDir = new File( System.getProperty("java.io.tmpdir",".") );

        this.fallbackServiceManagerList = new ArrayList();
        this.defaultInterceptorServiceList = new ArrayList();

        this.disposalDelay = DISPOSAL_DELAY_DEFAULT;
        this.reconfigurationDelay = RECONFIGURATION_DELAY_DEFAULT;
    }

    /**
     * @see org.apache.avalon.framework.logger.LogEnabled#enableLogging(org.apache.avalon.framework.logger.Logger)
     */
    public void enableLogging(Logger logger)
    {
        Validate.notNull( logger, "logger" );
        this.logger = logger;
    }

    /**
     * @see org.apache.avalon.framework.context.Contextualizable#contextualize(org.apache.avalon.framework.context.Context)
     */
    public void contextualize(Context context) throws ContextException
    {
        Validate.notNull( context, "context" );
        // Argghhh - I need to to parse the Configuration before I can map the Context
        this.callerContext = context;
    }


    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager serviceManager) throws ServiceException
    {
        this.parentServiceManager = serviceManager;
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration configuration) throws ConfigurationException
    {
        Validate.notNull( configuration, "configuration" );

        // retrieve the reconfigurationDelay

        this.reconfigurationDelay =
            configuration.getChild(RECONFIGURATION_DELAY_KEY).getValueAsInteger(
                    RECONFIGURATION_DELAY_DEFAULT
                );

        // retrieve the disposal delay

        this.disposalDelay =
                configuration.getChild(DISPOSAL_DELAY_KEY).getValueAsInteger(
                        DISPOSAL_DELAY_DEFAULT
                );

        // evaluate if we are using dynamic proxies

        this.hasDynamicProxies =
            configuration.getChild(DYNAMICPROXY_ENABLED_KEY).getValueAsBoolean(false);

        // retrieve the container flavour

        this.setContainerFlavour(
            configuration.getChild(CONTAINERFLAVOUR_CONFIG_KEY).getValue(
                COMPONENT_CONTAINERFLAVOUR_VALUE )
            );

        this.getLogger().debug( "Using the following container type : " + this.getContainerFlavour() );

        // process the caller-supplied context here

        try
        {
            // instantiate a mapper using the existing context - it might
            // contain application specific entries we are not aware of

            AvalonToYaafiContextMapper mapper = new AvalonToYaafiContextMapper(
                this.getTempRootDir(),
                this.callerContext,
                this.getClassLoader()
                );

            // do the magic mapping

            this.context = mapper.mapFrom(
                this.callerContext,
                this.getContainerFlavour()
                );

            // don't keep a reference of the caller-supplied context

            this.callerContext = null;
        }
        catch( ContextException e )
        {
            String msg = "Failed to parse the caller-supplied context";
            this.getLogger().error( msg, e );
            throw new ConfigurationException( msg );
        }

        // evaluate componentRoles

        Configuration currComponentRoles = configuration.getChild(COMPONENT_ROLE_KEYS);

        this.setComponentRolesLocation(
            currComponentRoles.getChild(COMPONENT_LOCATION_KEY).getValue(
                COMPONENT_ROLE_VALUE )
                );

        this.setComponentRolesFlavour(
            currComponentRoles.getChild(CONTAINERFLAVOUR_CONFIG_KEY).getValue(
                COMPONENT_ROLECONFIGFLAVOUR_VALUE )
                );

        this.setComponentRolesEncrypted(
            currComponentRoles.getChild(COMPONENT_ISENCRYPTED_KEY).getValue(
                "false" )
                );

        // evaluate componentConfiguraion

        Configuration currComponentConfiguration = configuration.getChild(COMPONENT_CONFIG_KEY);

        this.setComponentConfigurationLocation(
            currComponentConfiguration.getChild(COMPONENT_LOCATION_KEY).getValue(
                COMPONENT_CONFIG_VALUE )
                );

        this.setComponentConfigurationEncrypted(
            currComponentConfiguration.getChild(COMPONENT_ISENCRYPTED_KEY).getValue(
                "false" )
                );

        // get the configuration for componentConfigurationPropertiesResolver

        this.componentConfigurationPropertiesResolverConfig = configuration.getChild(
            COMPONENT_CONFIG_PROPERTIES_KEY
            );

        // evaluate parameters

        Configuration currParameters = configuration.getChild(COMPONENT_PARAMETERS_KEY);

        this.setParametersLocation(
            currParameters.getChild(COMPONENT_LOCATION_KEY).getValue(
                COMPONENT_PARAMETERS_VALUE )
                );

        this.setParametersEncrypted(
            currParameters.getChild(COMPONENT_ISENCRYPTED_KEY).getValue(
                "false" )
                );

        // evaluate the default interceptors

        Configuration currInterceptorList = configuration.getChild(
            INTERCEPTOR_LIST_KEY
            );

        Configuration[] interceptorConfigList = currInterceptorList.getChildren(
            INTERCEPTOR_KEY
            );

        for( int i=0; i<interceptorConfigList.length; i++ )
        {
            String interceptorServiceName = interceptorConfigList[i].getValue(null);

            if( !StringUtils.isEmpty(interceptorServiceName) && this.hasDynamicProxies())
            {
	            this.defaultInterceptorServiceList.add( interceptorServiceName );

	            this.getLogger().debug("Using the following default interceptor service : "
	                + interceptorServiceName
	                );
            }
        }

        // evaluate a list of service managers managing their own set of services
        // independent from the Avalon container. This service managers are used
        // to find services implemented as Spring bean or remote web services.

        Configuration currServiceManagerList = configuration.getChild(
            SERVICEMANAGER_LIST_KEY
            );

        Configuration[] serviceManagerConfigList = currServiceManagerList.getChildren(
            SERVICEMANAGER_KEY
            );

        for( int i=0; i<serviceManagerConfigList.length; i++ )
        {
            String serviceManagerName = serviceManagerConfigList[i].getValue(null);

            if( !StringUtils.isEmpty(serviceManagerName) )
            {
	            this.fallbackServiceManagerList.add( serviceManagerName );

	            this.getLogger().debug("Using the following fallback service manager : "
	                + serviceManagerName
	                );
            }
        }
    }

    /**
     * @see org.apache.avalon.framework.parameters.Parameterizable#parameterize(org.apache.avalon.framework.parameters.Parameters)
     */
    public void parameterize(Parameters parameters) throws ParameterException
    {
        this.parameters = parameters;
    }

    /**
     * @see org.apache.avalon.framework.activity.Initializable#initialize()
     */
    public void initialize() throws Exception
    {
        this.getLogger().debug( "YAAFI Service Framework is starting up");

        // set the directories being used

        this.setApplicationRootDir(
            (File) this.getContext().get(AvalonYaafiConstants.URN_AVALON_HOME )
            );

        this.setTempRootDir(
            (File) this.getContext().get(AvalonYaafiConstants.URN_AVALON_TEMP )
            );

        // get the configuration files

        this.roleConfiguration = loadConfiguration(
            this.componentRolesLocation,
            this.isComponentRolesEncrypted()
            );

        if( this.roleConfiguration == null )
        {
            String msg = "Unable to locate the role configuration : " + this.componentRolesLocation;
            this.getLogger().error( msg );
            throw new ConfigurationException( msg );
        }

        this.serviceConfiguration = loadConfiguration(
            this.componentConfigurationLocation,
            this.isComponentConfigurationEncrypted()
            );

        // create the configuration properties

        Properties componentConfigurationProperties = this.loadComponentConfigurationProperties();

        // expand the componentConfiguration using the componentConfigurationProperties

        ConfigurationUtil.expand(
            this.getLogger(),
            (DefaultConfiguration) this.serviceConfiguration,
            componentConfigurationProperties
            );

        // create the default parameters

        if( this.getParameters() == null )
        {
            this.parameters= this.loadParameters(
                this.parametersLocation,
                this.isParametersEncrypted()
                );
        }

        // create the service implementation instances

        List currServiceList = this.createServiceComponents(
            this.roleConfiguration,
            this.getLogger()
            );

        this.setServiceList( currServiceList );

        // fill the service map mapping from a service name to an instance

        for( int i=0; i<this.getServiceList().size(); i++ )
        {
            ServiceComponent serviceComponent = (ServiceComponent) this.getServiceList().get(i);
            this.getServiceMap().put( serviceComponent.getName(), serviceComponent );
        }

        // ensure that fallback service managers are available

        for(int i=0; i<this.fallbackServiceManagerList.size(); i++)
        {
            String currServiceManagerName = (String) this.fallbackServiceManagerList.get(i);
            if(this.getServiceMap().get(currServiceManagerName) == null)
            {
                String msg = "The following fallback service manager was not found : " + currServiceManagerName;
                throw new IllegalArgumentException(msg);
            }
        }

        // run the various lifecycle stages

        this.incarnateAll( this.getServiceList() );

        // we are up and running

        this.isCurrentlyDisposing = false;
        this.isAlreadyDisposed = false;
        this.getLogger().debug( "YAAFI Avalon Service Container is up and running");
    }


    /**
     * Disposes the service container implementation.
     *
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     */
    public void dispose()
    {
        if( this.isCurrentlyDisposing() || this.isAlreadyDisposed() )
        {
            return;
        }

        this.isCurrentlyDisposing = true;

        if( this.getLogger() != null )
        {
            this.getLogger().debug("Disposing all services");
        }

        // wait some time before disposing all services

        waitForDisposal();

        synchronized (this)
        {
            // de-commission all services

            this.decommissionAll(this.getServiceList());

            // dispose all services

            this.disposeAll(this.getServiceList());

            // clean up

            this.getServiceList().clear();
            this.getServiceMap().clear();

            this.componentRolesLocation = null;
            this.componentConfigurationLocation = null;
            this.context = null;
            this.parametersLocation = null;
            this.roleConfiguration = null;
            this.serviceConfiguration = null;
            this.parameters = null;
            this.fallbackServiceManagerList = null;
            this.defaultInterceptorServiceList = null;
            this.isCurrentlyDisposing = false;
            this.isAlreadyDisposed = true;

            if( this.getLogger() != null )
            {
                this.getLogger().debug( "All services are disposed" );
            }
        }
    }

    /**
     * Reconfiguring the services. I'm not sure how to implement this properly since
     * the Avalon docs is vague on this subject. For now we suspend, reconfigure and
     * resume the services in the correct order.
     *
     * @see org.apache.avalon.framework.configuration.Reconfigurable#reconfigure(org.apache.avalon.framework.configuration.Configuration)
     */
    public synchronized void reconfigure(Configuration configuration)
        throws ConfigurationException
    {
        Validate.notNull( configuration, "configuration" );

        int exceptionCounter = 0;
        ServiceComponent serviceComponent;

        this.getLogger().warn("Reconfiguring all services ...");

        // 1) wait for some time

        this.waitForReconfiguration();

        // 2) store the new configuration

        this.serviceConfiguration = configuration;

        Properties componentConfigurationProperties = this.loadComponentConfigurationProperties();

        ConfigurationUtil.expand(
            this.getLogger(),
            (DefaultConfiguration) this.serviceConfiguration,
            componentConfigurationProperties
            );

        // 3) reconfigure the services

        for( int i=0; i<this.getServiceList().size(); i++ )
        {
            serviceComponent = (ServiceComponent) this.getServiceList().get(i);

            Configuration serviceComponentConfiguration = this.getServiceConfiguration().getChild(
                serviceComponent.getShorthand()
                );

            try
            {
                serviceComponent.setConfiguration(serviceComponentConfiguration);
                serviceComponent.reconfigure();
            }
            catch(Throwable t)
            {
                String msg = "Reconfiguring of " + serviceComponent.getShorthand() + " failed";
                this.getLogger().error(msg);
                exceptionCounter++;
            }
        }

        // 4) check the result

        if( exceptionCounter > 0 )
        {
            String msg = "The reconfiguration failed with " + exceptionCounter + " exception(s)";
            this.getLogger().error(msg);
            throw new ConfigurationException(msg);
        }
    }

    /////////////////////////////////////////////////////////////////////////
    // Server Interface Implementation
    /////////////////////////////////////////////////////////////////////////

    /**
     * @see org.apache.fulcrum.yaafi.framework.container.ServiceLifecycleManager#getRoleEntry(java.lang.String)
     */
    public synchronized RoleEntry getRoleEntry(String name)
        throws ServiceException
    {
        return this.getServiceComponentEx(name).getRoleEntry();
    }

    /**
     * @see org.apache.fulcrum.yaafi.framework.container.ServiceLifecycleManager#getRoleEntries()
     */
    public synchronized RoleEntry[] getRoleEntries()
    {
        ServiceComponent serviceComponent;
        List serviceList = this.getServiceList();
        RoleEntry[] result = new RoleEntry[serviceList.size()];

        for( int i=0; i<result.length; i++ )
        {
            serviceComponent = (ServiceComponent) serviceList.get(i);
            result[i] = serviceComponent.getRoleEntry();
        }

        return result;
    }

    /**
     * @see org.apache.fulcrum.yaafi.framework.container.ServiceLifecycleManager#reconfigure(java.lang.String[])
     */
    public synchronized void reconfigure(String[] names) throws ServiceException,
        ConfigurationException
    {
        Validate.notNull(names,"names");
        Validate.noNullElements(names,"names");
        
        this.waitForReconfiguration();

        for( int i=0; i<names.length; i++ )
        {
            // ensure that the service exists since during our reconfiguration
            // we might use a stale reconfiguration entry

            if( this.getServiceMap().get(names[i]) != null )
            {
                this.reconfigure(names[i]);
            }
        }
    }

    /**
     * @see org.apache.avalon.framework.service.ServiceManager#hasService(java.lang.String)
     */
    public boolean hasService(String name)
    {
        Validate.notEmpty( name, "name" );

        boolean result;

        if(this.isCurrentlyDisposing() || this.isAlreadyDisposed())
        {
            return false;
        }

        synchronized(this)
        {
            // look at our available service

            result = (this.getLocalServiceComponent(name) != null);

            // look at fallback service managers

            if(!result)
            {
                result = this.hasFallbackService(name);
            }
        }

        // if we haven't found anything ask the parent ServiceManager

        if( (!result) && ( this.hasParentServiceManager() ) )
        {
            result = this.getParentServiceManager().hasService(name);
        }

        return result;
    }

    /**
     * Lookup a service instance. The implementation uses the following
     * mechanism
     * <ul>
     *  <li>look for a matching local service
     *  <li>use the fallback service manager as they might know the service
     *  <li>ask the parent service manager
     * </ul>
     *
     * @see org.apache.avalon.framework.service.ServiceManager#lookup(java.lang.String)
     */
    public Object lookup(String name) throws ServiceException
    {
        Validate.notEmpty( name, "name" );

        Object result = null;
        ServiceComponent serviceManagerComponent;

        if(this.isAlreadyDisposed())
        {
            String msg = "The container is disposed an no services are available";
            this.getLogger().error( msg );
            throw new ServiceException( name, msg );
        }

        try
        {
            synchronized (this)
            {
                // 1) check our local services

                serviceManagerComponent = this.getLocalServiceComponent(name);

                if( serviceManagerComponent != null )
                {
                    result = serviceManagerComponent.getInstance();

                    if((result != null) && this.getLogger().isDebugEnabled())
                    {
                        String msg = "Located the service '" + name + "' in the local container";
                        this.getLogger().debug(msg);
                    }
                }

                // 2) look at fallback service managers

                if(result == null)
                {
                    result = this.getFallbackService(name);
                }
            }
        }
        catch(ServiceException e)
        {
            String msg = "Failed to lookup a service " + name;
            this.getLogger().error( msg, e );            
            throw e;
        }
        catch( Throwable t )
        {
            String msg = "Failed to lookup a service " + name;
            this.getLogger().error( msg, t );
            throw new ServiceException( name, msg, t );
        }

        // 3) if we haven't found anything ask the parent ServiceManager

        if( (result == null) && this.hasParentServiceManager() )
        {
            result = this.getParentServiceManager().lookup(name);

            if((result != null) && this.getLogger().isDebugEnabled())
            {
                String msg = "Located the service '" + name + "' using the parent service manager";
                this.getLogger().debug(msg);
            }
        }

        // if we still haven't found anything then complain

        if( result == null )
        {
            String msg = "The following component does not exist : " + name;
            this.getLogger().error(msg);
            throw new ServiceException( AvalonYaafiConstants.AVALON_CONTAINER_YAAFI, name );
        }

        return result;
    }

    /**
     * @see org.apache.avalon.framework.service.ServiceManager#release(java.lang.Object)
     */
    public void release(Object object)
    {
        // AFAIK this is only useful for lifecycle management regarding
        // lifestyle other than singleton.
    }

    /**
     * @see org.apache.fulcrum.yaafi.framework.container.ServiceContainer#decommision(java.lang.String)
     */
    public synchronized void decommision(String name) throws ServiceException
    {
        this.waitForReconfiguration();       
        ServiceComponent serviceComponent = this.getServiceComponentEx(name);
        this.decommission(serviceComponent);
    }

    /**
     * @see org.apache.fulcrum.yaafi.framework.container.ServiceContainer#getParameters()
     */
    public Parameters getParameters()
    {
        return this.parameters;
    }

    /////////////////////////////////////////////////////////////////////////
    // Service Implementation
    /////////////////////////////////////////////////////////////////////////

    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        ToStringBuilder toStringBuilder = new ToStringBuilder(this);

        toStringBuilder.append("applicationRootDir", this.getApplicationRootDir());
        toStringBuilder.append("tempRootDir", this.getTempRootDir());
        toStringBuilder.append("componentRolesLocation", this.componentRolesLocation);
        toStringBuilder.append("componentConfigurationLocation", this.componentConfigurationLocation);
        toStringBuilder.append("parametersLocation", parametersLocation);
        toStringBuilder.append("logger", this.getLogger().getClass().getName());
        toStringBuilder.append("hasDynamicProxies", this.hasDynamicProxies);
        toStringBuilder.append("containerFlavour", this.containerFlavour);
        toStringBuilder.append("componentRolesFlavour", this.componentRolesFlavour);
        toStringBuilder.append("isComponentRolesEncrypted", this.isComponentRolesEncrypted);
        toStringBuilder.append("isComponentConfigurationEncrypted", this.isComponentConfigurationEncrypted);
        toStringBuilder.append("isParametersEncrypted", this.isParametersEncrypted);

        return toStringBuilder.toString();
    }

    /**
     * Create a role configuration parser based on the container flavour.
     * @return the role configuration parser
     */
    private RoleConfigurationParser createRoleConfigurationParser()
    {
        return new RoleConfigurationParserImpl(
            this.getComponentRolesFlavour()
            );
    }

    /**
     * Reconfigure a single service
     *
     * @param name the name of the service to be reconfigured
     * @throws ServiceException the service was not found
     * @throws ConfigurationException the reconfiguration failed
     */
    private void reconfigure(String name)
        throws ServiceException, ConfigurationException
    {
        Validate.notEmpty( name, "name" );
        ServiceComponent serviceComponent = this.getServiceComponentEx(name);

        // reconfigure the component

        try
        {
            serviceComponent.reconfigure();
        }
        catch(ConfigurationException e)
        {
            String msg = "Reconfiguring failed : " + serviceComponent.getShorthand();
            this.getLogger().error(msg,e);
            throw new ConfigurationException(msg,e);
        }
        catch(Throwable t)
        {
            String msg = "Reconfiguring failed : " + serviceComponent.getShorthand();
            this.getLogger().error(msg,t);
            throw new ConfigurationException(msg,t);
        }
    }

    /**
     * Enforce that a service is known to simplify error handling.
     *
     * @param name the name of the service component
     * @return the service component
     * @throws ServiceException the service was not found
     */
    private ServiceComponent getServiceComponentEx(String name)
        throws ServiceException
    {
        Validate.notEmpty( name, "name" );
        ServiceComponent result = (ServiceComponent) this. getServiceMap().get(name);

        if( result == null )
        {
            String msg = "The following component does not exist : " + name;
            this.getLogger().error(msg);
            throw new ServiceException( AvalonYaafiConstants.AVALON_CONTAINER_YAAFI, name );
        }

        return result;
    }

    /**
     * Try to get a local service component.
     *
     * @param name the name of the service component
     * @return the service component if any
     */
    private ServiceComponent getLocalServiceComponent(String name)
    {
        Validate.notEmpty( name, "name" );
        ServiceComponent result = (ServiceComponent) this. getServiceMap().get(name);
        return result;
    }

    /**
     * Try to get a service component provided by a fallback service
     * manager.
     *
     * @param name the name of the service component
     * @return the service component if any
     * @throws Exception getting the service failed
     */
    private Object getFallbackService(String name) throws Exception
    {

        Validate.notEmpty( name, "name" );

        Object result = null;
        ServiceComponent serviceManagerComponent;

        for(int i=0; i<this.fallbackServiceManagerList.size(); i++)
        {
            String serviceManagerComponentName = (String) fallbackServiceManagerList.get(i);
            serviceManagerComponent = this.getLocalServiceComponent(serviceManagerComponentName);

            if(serviceManagerComponent != null)
            {
                ServiceManager currServiceManager = (ServiceManager) serviceManagerComponent.getInstance();

                if (currServiceManager.hasService(name))
                {
                    result = currServiceManager.lookup(name);

                    if((result != null) && this.getLogger().isDebugEnabled())
                    {
                        String msg = "Located the service '" + name + "' using the fallback service manager '" + serviceManagerComponentName + "'";
                        this.getLogger().debug(msg);
                    }
                }
            }
        }

        return result;
    }

    /**
     * Try to get a service provided by a fallback service manager.
     *
     * @param name the name of the service component
     * @return the service component if any
     */
    private boolean hasFallbackService(String name)
    {
        Validate.notEmpty( name, "name" );

        ServiceComponent serviceManagerComponent;

        for(int i=0; i<this.fallbackServiceManagerList.size(); i++)
        {
            String serviceManagerComponentName = (String) fallbackServiceManagerList.get(i);
            serviceManagerComponent = this.getLocalServiceComponent(serviceManagerComponentName);

            if(serviceManagerComponent != null)
            {
                ServiceManager currServiceManager;

                try
                {
                    currServiceManager = (ServiceManager) serviceManagerComponent.getInstance();
                    if (currServiceManager.hasService(name))
                    {
                        return true;
                    }
                }
                catch (Exception e)
                {
                    String msg = "Unable to invoke fallback service manager '" + serviceManagerComponentName + "'";
                    this.getLogger().error(msg, e);
                    throw new RuntimeException(msg);
                }
            }
        }

        return false;
    }

    /**
     * @param string The location of the component configuration file
     */
    private void setComponentConfigurationLocation(String string)
    {
        this.componentConfigurationLocation = string;
    }

    /**
     * @param string The location of the component role file
     */
    private void setComponentRolesLocation(String string)
    {
        this.componentRolesLocation = string;
    }

    /**
     * @param string The location of the parameters file
     */
    private void setParametersLocation(String string)
    {
        this.parametersLocation = string;
    }

    /**
     * @return The logger of the service container
     */
    private Logger getLogger()
    {
        return this.logger;
    }

    /**
     * @return Returns the serviceMap.
     */
    private HashMap getServiceMap()
    {
        return this.serviceMap;
    }

    /**
     * Incarnation of a list of services.
     *
     * @param serviceList the list of available services
     * @throws Exception the incarnation of a service failed
     */
    private void incarnateAll(List serviceList)
        throws Exception
    {
        ServiceComponent serviceComponent;

        // configure all services

        for( int i=0; i<serviceList.size(); i++ )
        {
            serviceComponent = (ServiceComponent) this.getServiceList().get(i);
            this.configure( serviceComponent );
        }

        // incarnate all services

        for( int i=0; i<serviceList.size(); i++ )
        {
            serviceComponent = (ServiceComponent) this.getServiceList().get(i);
            this.incarnate( serviceComponent );
        }

    }

    /**
     * Configure a single service component. After the invocation
     * the service component is ready to be incarnated.
     *
     * @param serviceComponent The service component to be configured
     * @throws Exception the configuration failed
     */
    private void configure( ServiceComponent serviceComponent )
        throws Exception
    {
        this.getLogger().debug( "Configuring the service component "
            + serviceComponent.getShorthand()
            );

        // map the context according to the Avalon component type

        YaafiToAvalonContextMapper mapper = new YaafiToAvalonContextMapper(
            serviceComponent.getName(),
            this.getClassLoader()
            );

        RoleEntry roleEntry = serviceComponent.getRoleEntry();
        String componentFlavour = roleEntry.getComponentFlavour();

        DefaultContext serviceComponentContext = mapper.mapTo(
            this.getContext(),
            componentFlavour
            );

        // create the remaining Avalon artifacts for the service component

        Logger serviceComponentLogger = this.getLogger().getChildLogger(
            roleEntry.getLogCategory()
            );

        Configuration serviceComponentConfiguration = this.getServiceConfiguration().getChild(
            roleEntry.getShorthand()
            );

        Parameters serviceComponentParameters = this.getParameters();

        // configure the service component with all the artifacts

        serviceComponent.setLogger(serviceComponentLogger);
        serviceComponent.setServiceManager(this);
        serviceComponent.setContext(serviceComponentContext);
        serviceComponent.setConfiguration(serviceComponentConfiguration);
        serviceComponent.setParameters(serviceComponentParameters);

        // load the implementation class of the service

        serviceComponent.loadImplemtationClass(
           this.getClassLoader()
            );
    }

    /**
     * Incarnation of a configured service component. After the
     * incarnation the service component is operational.
     *
     * @param serviceComponent The service component to incarnate
     * @exception Exception incarnating the service component failed
     */
    private void incarnate( ServiceComponent serviceComponent )
        throws Exception
    {
        this.getLogger().debug( "Incarnating the service "
            + serviceComponent.getShorthand()
            );

        serviceComponent.incarnate();
    }

    /**
     * De-commission a ist of services.
     *
     * @param serviceList the list of services to decommission
     */
    private void decommissionAll(List serviceList)
    {
        for( int i=serviceList.size()-1; i>=0; i-- )
        {
            ServiceComponent serviceComponent = (ServiceComponent) serviceList.get(i);
            this.decommission(serviceComponent);
        }
    }

    /**
     * Decommission of a single service component. Decommission consists of running the
     * whole Avalon decommission lifecycle process for a service component. After
     * decommission the service is not operational any longer. During decommissioning
     * we ignore any exceptions since it is quite common that something goes wrong.
     *
     * @param serviceComponent The service component to decommission
     */
    private void decommission(ServiceComponent serviceComponent)
    {
        this.getLogger().debug( "Decommission the service " + serviceComponent.getShorthand() );

        try
        {
            serviceComponent.decommision();
        }
        catch (Throwable e)
        {
            String msg = "Decommissioning the following service failed : " + serviceComponent.getName();
            this.getLogger().error( msg, e );
        }
    }

    /**
     * Disposing a ist of services
     *
     * @param serviceList the list of services to dispose
     */
    private void disposeAll(List serviceList)
    {
        for( int i=serviceList.size()-1; i>=0; i-- )
        {
            ServiceComponent serviceComponent = (ServiceComponent) serviceList.get(i);
            this.dispose( serviceComponent );
        }
    }

    /**
     * Disposing of a single service component.

     * @param serviceComponent The service component to decommission
     */
    private void dispose( ServiceComponent serviceComponent )
    {
        this.getLogger().debug( "Disposing the service " + serviceComponent.getShorthand() );

        try
        {
            serviceComponent.dispose();
        }
        catch (Throwable e)
        {
            String msg = "Disposing the following service failed : " + serviceComponent.getName();
            this.getLogger().error( msg, e );
        }
    }

    private boolean isCurrentlyDisposing() {
        return isCurrentlyDisposing;
    }

    private boolean isAlreadyDisposed() {
        return isAlreadyDisposed;
    }

    /**
     * @return The list of currently know services
     */
    private List getServiceList()
    {
        return this.serviceList;
    }

    /**
     * @param list The list of known services
     */
    private void setServiceList(List list)
    {
        this.serviceList = list;
    }

    /**
     * @return The service configuration
     */
    private Configuration getServiceConfiguration()
    {
        return this.serviceConfiguration;
    }

    /**
     * Factory method for creating services. The service
     * instances are not initialized at this point.
     *
     * @param roleConfiguration the role configuration file
     * @param logger the logger
     * @return the list of service components
     * @throws ConfigurationException creating the service instance failed
     */
    private List createServiceComponents(Configuration roleConfiguration, Logger logger )
        throws ConfigurationException
    {
        Validate.notNull(roleConfiguration,"roleConfiguration");
        Validate.notNull(logger,"logger");

        ArrayList result = new ArrayList();
        ServiceComponent serviceComponent = null;

        // create an appropriate instance of role configuration parser

        RoleConfigurationParser roleConfigurationParser = this.createRoleConfigurationParser();

        // extract the role entries

        RoleEntry[] roleEntryList = roleConfigurationParser.parse(roleConfiguration);

        // get the default interceptors defined for the container

        ArrayList defaultInterceptorList = this.getDefaultInterceptorServiceList();

        // create the service components based on the role entries

        for ( int i=0; i<roleEntryList.length; i++ )
        {
            try
            {
                // add the default interceptors to all role entries

                RoleEntry roleEntry = roleEntryList[i];

                if( this.hasDynamicProxies() )
                {
                    roleEntry.addInterceptors(defaultInterceptorList);
                }
                else
                {
                    roleEntry.setHasDynamicProxy(false);
                }

                serviceComponent = new AvalonServiceComponentImpl(
                    roleEntry,
                    this.getLogger(),
                    logger
                    );

                result.add( serviceComponent );
            }
            catch( Throwable t )
            {
                String serviceComponentName = ( serviceComponent != null ? serviceComponent.getName() : "unknown" ); 
                String msg = "Failed to load the service " + serviceComponentName;
                this.getLogger().error( msg, t );
                throw new ConfigurationException( msg, t );
            }
        }

        return result;
    }

    /**
     * Load a configuration file either from a file or using the class loader.
     * @param location the location of the file
     * @param isEncrypted  is the configuration encrypted
     * @return The loaded configuration
     * @throws Exception Something went wrong
     */
    private Configuration loadConfiguration( String location, String isEncrypted )
        throws Exception
    {
        Configuration result = null;
        InputStreamLocator locator = this.createInputStreamLocator();
        InputStream is = locator.locate( location );
        DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();

        if( is != null )
        {
            try
            {
                is = this.getDecryptingInputStream( is, isEncrypted );
                result = builder.build( is );
            }
            catch ( Exception e )
            {
                String msg = "Unable to parse the following file : " + location;
                this.getLogger().error( msg , e );
                throw e;
            }
            finally
            {
                safeClose(is);
            }
        }

        return result;
    }

    /**
     * Load a configuration property file either from a file or using the class loader.
     * 
     * @return The loaded property file
     * @throws ConfigurationException Something went wrong
     */
    private Properties loadComponentConfigurationProperties()
    	throws ConfigurationException
    {
        Properties result;
        ComponentConfigurationPropertiesResolver resolver;

        String className = this.componentConfigurationPropertiesResolverConfig.getChild("resolver").getValue(
            ComponentConfigurationPropertiesResolverImpl.class.getName()
            );

        try
        {
            Class resolverClass = this.getClassLoader().loadClass( className );
            resolver = (ComponentConfigurationPropertiesResolver) resolverClass.newInstance();
            ContainerUtil.enableLogging(resolver, this.getLogger());
            ContainerUtil.contextualize(resolver, this.getContext());
            ContainerUtil.configure(resolver, this.componentConfigurationPropertiesResolverConfig);

            result = resolver.resolve(null);

            this.getLogger().debug("Using the following componentConfigurationProperties: " + result);
        }
        catch (Exception e)
        {
            String msg = "Resolving componentConfigurationProperties failed using the following class : " + className;
            this.getLogger().error(msg, e);
            throw new ConfigurationException(msg, e);
        }

        return result;
    }

    /**
     * Load the parameters
     * @param location The location as a file
     * @param isEncrypted is the file encrypted
     * @return The loaded configuration
     * @throws Exception Something went wrong
     */
    private Parameters loadParameters( String location, String isEncrypted )
        throws Exception
    {
        InputStreamLocator locator = this.createInputStreamLocator();
        InputStream is = locator.locate( location );
        Parameters result = new Parameters();

        if( is != null )
        {
            try
            {
                is = this.getDecryptingInputStream( is, isEncrypted );
                Properties props = new Properties();
                props.load( is );
                result = Parameters.fromProperties( props );
            }
            finally
            {
                safeClose(is);
            }
        }

        return result;
    }


    /**
     * Creates a locator to find a resource either in the file system or in
     * the classpath.
     *
     * @return the locator
     */
    private InputStreamLocator createInputStreamLocator()
    {
        return new InputStreamLocator( this.getApplicationRootDir(), this.getLogger() );
    }

    /**
     * Set the application directory of the container.
     *
     * @param dir The applicationRootDir to set.
     */
    private void setApplicationRootDir(File dir)
    {
        this.getLogger().debug( "Setting applicationRootDir to " + dir.getAbsolutePath() );

        Validate.notNull(dir,"applicationRootDir is <null>");
        Validate.isTrue(dir.exists(),"applicationRootDir does not exist");

        this.applicationRootDir = dir;
    }

    /**
     * @return Returns the applicationRootDir.
     */
    private File getApplicationRootDir()
    {
        return this.applicationRootDir;
    }

    /**
     * @return Returns the serviceManager of the parent container
     */
    private ServiceManager getParentServiceManager()
    {
        return this.parentServiceManager;
    }

    /**
     * @return is a parent ServiceManager available
     */
    private boolean hasParentServiceManager()
    {
        return (this.getParentServiceManager() != null);
    }
    /**
     * Set the temporary directory of the container.
     *
     * @param dir The tempRootDir to set.
     */
    private void setTempRootDir(File dir)
    {
        this.getLogger().debug( "Setting tempRootDir to " + dir.getAbsolutePath() );

        Validate.notNull(dir,"tempRootDir is <null>");
        Validate.isTrue(dir.exists(),"tempRootDir does not exist");
        Validate.isTrue(dir.canWrite(),"tempRootDir is not writeable");

        this.tempRootDir = dir;
    }

    /**
     * @return Returns the tempRootDir.
     */
    private File getTempRootDir()
    {
        return tempRootDir;
    }

    /**
     * @return Returns the isComponentConfigurationEncrypted.
     */
    private String isComponentConfigurationEncrypted()
    {
        return isComponentConfigurationEncrypted;
    }

    /**
     * @param isComponentConfigurationEncrypted The isComponentConfigurationEncrypted to set.
     */
    private void setComponentConfigurationEncrypted(
        String isComponentConfigurationEncrypted)
    {
        this.isComponentConfigurationEncrypted = isComponentConfigurationEncrypted;
    }

    /**
     * @return Returns the isComponentRolesEncrypted.
     */
    private String isComponentRolesEncrypted()
    {
        return isComponentRolesEncrypted;
    }

    /**
     * @param isComponentRolesEncrypted The isComponentRolesEncrypted to set.
     */
    private void setComponentRolesEncrypted(String isComponentRolesEncrypted)
    {
        this.isComponentRolesEncrypted = isComponentRolesEncrypted;
    }

    /**
     * @return Returns the isParametersEncrypted.
     */
    private String isParametersEncrypted()
    {
        return isParametersEncrypted;
    }

    /**
     * @param isParametersEncrypted The isParametersEncrypted to set.
     */
    private void setParametersEncrypted(String isParametersEncrypted)
    {
        this.isParametersEncrypted = isParametersEncrypted;
    }

    /**
     * Create a decrypting input stream using the default password.
     *
     * @param is the input stream to be decrypted
     * @param isEncrypted the encryption mode (true|false|auto)
     * @return an decrypting input stream
     * @throws Exception reading the input stream failed
     */
    private InputStream getDecryptingInputStream( InputStream is, String isEncrypted )
        throws Exception
    {
        return CryptoStreamFactory.getDecryptingInputStream(is, isEncrypted);
    }

    /**
     * @return Returns the containerFlavour.
     */
    private String getContainerFlavour()
    {
        return containerFlavour;
    }

    /**
     * @param containerFlavour The containerFlavour to set.
     */
    private void setContainerFlavour(String containerFlavour)
    {
        this.containerFlavour = containerFlavour;
    }

    /**
     * @return Returns the componentRolesFlavour.
     */
    private String getComponentRolesFlavour()
    {
        return componentRolesFlavour;
    }

    /**
     * @param componentRolesFlavour The componentRolesFlavour to set.
     */
    private void setComponentRolesFlavour(String componentRolesFlavour)
    {
        this.componentRolesFlavour = componentRolesFlavour;
    }

    /**
     * @return Returns the context.
     */
    private Context getContext()
    {
        return context;
    }

    /**
     * @return Returns the hasDynamicProxies.
     */
    private boolean hasDynamicProxies()
    {
        return this.hasDynamicProxies;
    }

    /**
     * @return Returns the defaultInterceptorServiceList.
     */
    private ArrayList getDefaultInterceptorServiceList()
    {
        return defaultInterceptorServiceList;
    }

    /**
     * @return the containers class loader
     */
    private ClassLoader getClassLoader()
    {
        return this.getClass().getClassLoader();
    }

    /**
     * Wait for the time configured as 'reconfigurationDelay' before
     * reconfiguring the container or services.
     */
    private void waitForReconfiguration()
    {
        try
        {
            Thread.sleep(this.reconfigurationDelay);
        }
        catch(InterruptedException e)
        {
            // nothing to do
        }
    }

    /**
     * Wait for the time configured as 'disposalDelay' before
     * disposing the container.
     */
    private void waitForDisposal()
    {
        try
        {
            Thread.sleep(this.disposalDelay);
        }
        catch(InterruptedException e)
        {
            // nothing to do
        }
    }

    private void safeClose(InputStream is)
    {
        if(is != null)
        {
            try
            {
                is.close();
            }
            catch(Exception e)
            {
                getLogger().error("Failed to close an input stream", e);
            }
        }
    }
}
