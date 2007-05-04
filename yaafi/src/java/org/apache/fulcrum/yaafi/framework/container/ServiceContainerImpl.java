package org.apache.fulcrum.yaafi.framework.container;

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
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
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
import org.apache.fulcrum.jce.crypto.CryptoStreamFactoryImpl;
import org.apache.fulcrum.yaafi.framework.component.AvalonServiceComponentImpl;
import org.apache.fulcrum.yaafi.framework.component.ServiceComponent;
import org.apache.fulcrum.yaafi.framework.configuration.ComponentConfigurationPropertiesResolver;
import org.apache.fulcrum.yaafi.framework.configuration.ComponentConfigurationPropertiesResolverImpl;
import org.apache.fulcrum.yaafi.framework.constant.AvalonYaafiConstants;
import org.apache.fulcrum.yaafi.framework.context.AvalonToYaafiContextMapper;
import org.apache.fulcrum.yaafi.framework.context.YaafiToAvalonContextMapper;
import org.apache.fulcrum.yaafi.framework.role.RoleConfigurationParser;
import org.apache.fulcrum.yaafi.framework.role.RoleConfigurationParserImpl;
import org.apache.fulcrum.yaafi.framework.role.RoleEntry;
import org.apache.fulcrum.yaafi.framework.util.ConfigurationUtil;
import org.apache.fulcrum.yaafi.framework.util.InputStreamLocator;
import org.apache.fulcrum.yaafi.framework.util.ReadWriteLock;
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
    /** the timeout after getting a write lock for reconfiguration */
    private static final int RECONFIGURATION_DELAY = 0;
    
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

    /** The application directory aka the current woring directory */
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
    private boolean isDisposed;

    /** The type of container where YAAFI is embedded */
    private String containerFlavour;

    /** The ms to wait before triggering a reconfiguration */
    private int reconfigurationDelay;

    /** global flag for enabling/disabling dynamic proxies */
    private boolean hasDynamicProxies;
    
    /** The list of interceptor services applied to all services */
    private ArrayList defaultInterceptorServiceList;
    
    /** Read/Write lock to synchronize acess to services */
    private ReadWriteLock readWriteLock;
    
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

        this.reconfigurationDelay = RECONFIGURATION_DELAY;
        this.containerFlavour = COMPONENT_CONTAINERFLAVOUR_VALUE;
        this.componentRolesFlavour = COMPONENT_ROLECONFIGFLAVOUR_VALUE;

        this.componentRolesLocation = COMPONENT_ROLE_VALUE;
        this.componentConfigurationLocation = COMPONENT_CONFIG_VALUE;
        this.parametersLocation = COMPONENT_PARAMETERS_VALUE;

        this.isComponentConfigurationEncrypted = "false";
        this.isComponentRolesEncrypted = "false";
        this.isParametersEncrypted = "false";

        this.isDisposed = false;
        this.serviceList = new ArrayList();
        this.serviceMap = new HashMap();

        this.applicationRootDir = new File( new File("").getAbsolutePath() );
        this.tempRootDir = new File( System.getProperty("java.io.tmpdir",".") );

        this.defaultInterceptorServiceList = new ArrayList();
    }

    /**
     * @see org.apache.avalon.framework.logger.LogEnabled#enableLogging(org.apache.avalon.framework.logger.Logger)
     */
    public void enableLogging(Logger logger)
    {
        Validate.notNull( logger, "logger" );
        this.logger = logger;
        this.readWriteLock = new ReadWriteLock(URN_YAAFI_KERNELLOCK, logger);
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
                RECONFIGURATION_DELAY
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

        // process the caller-suppied context here

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

            // don't keep a reference of the caller-supplide context

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

        for( int j=0; j<interceptorConfigList.length; j++ )
        {
            String interceptorServiceName = interceptorConfigList[j].getValue(null);
                        
            if( !StringUtils.isEmpty(interceptorServiceName) && this.hasDynamicProxies())
            {
	            this.defaultInterceptorServiceList.add( interceptorServiceName );
	
	            this.getLogger().debug("Using the following default interceptor service : "
	                + interceptorServiceName
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

        // create the service implementaion instances

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

        // run the various lifecycle stages

        this.incarnateAll( this.getServiceList() );

        // we are up and running

        this.isDisposed = false;
        this.getLogger().debug( "YAAFI Avalon Service Container is up and running");
    }


    /**
     * Disposes the service container implementation.
     *
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     */
    public void dispose()
    {
        Object lock = null;
            
        if( this.isDisposed )
        {
            return;
        }

        try
        {
            lock = this.getWriteLock();
            
            if( this.getLogger() != null )
            {
                this.getLogger().debug("Disposing all services");
            }
            
            // decommision all servcies

            this.decommisionAll( this.getServiceList() );

            // dispose all servcies

            this.disposeAll( this.getServiceList() );

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
            this.isDisposed = true;

            if( this.getLogger() != null )
            {
                this.getLogger().debug( "All services are disposed" );
            }
        }
        finally
        {
            this.releaseLock(lock);
        }
    }

    /**
     * Reconfiguring the services. I'm not sure how to implement this properly since
     * the Avalon docs is vague on this subject. For now we suspend, reconfigure and
     * resume the services in the correct order.
     *
     * @see org.apache.avalon.framework.configuration.Reconfigurable#reconfigure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void reconfigure(Configuration configuration)
        throws ConfigurationException
    {
        Validate.notNull( configuration, "configuration" );

        Object lock = null;
        int exceptionCounter = 0;
        ServiceComponent serviceComponent = null;

        this.getLogger().warn("Reconfiguring all services ...");

        try
        {
            // 1) lock the service container
            
            lock = this.getWriteLock();
            
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
	
	            Configuration serviceComponentConfiguraton = this.getServiceConfiguration().getChild(
	                serviceComponent.getShorthand()
	                );
	
	            try
	            {
	                serviceComponent.setConfiguration(serviceComponentConfiguraton);
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
        finally
        {
            this.releaseLock(lock);
        }
    }

    /////////////////////////////////////////////////////////////////////////
    // Server Interface Implementation
    /////////////////////////////////////////////////////////////////////////

    /**
     * @see org.apache.fulcrum.yaafi.framework.container.ServiceLifecycleManager#getRoleEntry(java.lang.String)
     */    
    public RoleEntry getRoleEntry(String name)
        throws ServiceException
    {
        Object lock = null;
        
        try
        {	
            lock = this.getReadLock();
            return this.getServiceComponentEx(name).getRoleEntry();
        }
        finally
        {
            this.releaseLock(lock);
        }
    }
    
    /**
     * @see org.apache.fulcrum.yaafi.framework.container.ServiceLifecycleManager#getRoleEntries()
     */
    public RoleEntry[] getRoleEntries()
    {
        Object lock = null;
        
        try
        {
            lock = this.getReadLock();
            
	        List serviceList = this.getServiceList();
	        ServiceComponent serviceComponent = null;
	        RoleEntry[] result = new RoleEntry[serviceList.size()];
	
	        for( int i=0; i<result.length; i++ )
	        {
	            serviceComponent = (ServiceComponent) serviceList.get(i);
	            result[i] = serviceComponent.getRoleEntry();
	        }
	
	        return result;
        }
        finally
        {
            this.releaseLock(lock);
        }
    }

    /**
     * @see org.apache.fulcrum.yaafi.framework.container.ServiceLifecycleManager#reconfigure(java.lang.String[])
     */
    public void reconfigure(String[] names) throws ServiceException,
        ConfigurationException
    {
        Validate.notNull(names,"names");
        Validate.noNullElements(names,"names");

        Object lock = null;
        
        try
        {
            // get exclusive access
            
            lock = this.getWriteLock();
                       
	        for( int i=0; i<names.length; i++ )
	        {
	            // ensure that the service exists since during our reconfiguration
	            // we might use a stle recofniguration entry
	
	            if( this.getServiceMap().get(names[i]) != null )
	            {
	                this.reconfigure(names[i]);
	            }
	        }
        }
        finally
        {
            this.release(lock);
        }
    }

    /**
     * @see org.apache.avalon.framework.service.ServiceManager#hasService(java.lang.String)
     */
    public boolean hasService(String name)
    {
        Validate.notEmpty( name, "name" );

        boolean result = false;
        Object lock = null;
        ServiceComponent serviceComponent = null;
        
        // look at our available service 
        
        try
        {
            lock = this.getReadLock();            
            serviceComponent = this.getLocalServiceComponent(name);
            result = ( serviceComponent != null ? true : false );
        }
        finally
        {
            this.releaseLock(lock);
        }
        
        // if we haven't found anything ask the parent ServiceManager
        
        if( ( result == false ) && ( this.hasParentServiceManager() ) )
        {
            result = this.getParentServiceManager().hasService(name);
        }
        
        return result;            
    }

    /**
     * @see org.apache.avalon.framework.service.ServiceManager#lookup(java.lang.String)
     */
    public Object lookup(String name) throws ServiceException
    {
        Validate.notEmpty( name, "name" );

        Object lock = null;
        Object result = null;
        ServiceComponent serviceComponent = null;

        // look at our available service
        
        try
        {
            lock = this.getReadLock();
            serviceComponent = this.getLocalServiceComponent(name);
            
            if( serviceComponent != null )
            {
                result = serviceComponent.getInstance();
            }
        }
        catch( Throwable t )
        {
            String msg = "Failed to lookup a service " + name;
            this.getLogger().error( msg, t );
            throw new ServiceException( name, msg, t );   
        }
		finally
		{
		    this.releaseLock(lock);
		}

        // if we haven't found anything ask the parent ServiceManager
        
        if( result == null )
        {
            if( this.hasParentServiceManager() )
	        {
	            result = this.getParentServiceManager().lookup(name);
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
    public void decommision(String name) throws ServiceException
    {
        Object lock = null;
        
        try
        {
            lock = this.getWriteLock();
            ServiceComponent serviceComponent = this.getServiceComponentEx(name);
            this.decommision(serviceComponent);
        }
        finally
        {
            this.releaseLock(lock);
        }
    }

    /**
     * @see org.apache.fulcrum.yaafi.framework.container.ServiceContainer#getParameters()
     */
    public Parameters getParameters()
    {
        Object lock = null;
        
        try
        {
            lock = this.getReadLock();
            return this.parameters;
        }
        finally
        {
            this.releaseLock(lock);
        }
    }
    
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
    
    /////////////////////////////////////////////////////////////////////////
    // Service Implementation
    /////////////////////////////////////////////////////////////////////////

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
     * @param ServiceException the service was not found
     * @param ConfigurationException the reconfiguration failed
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
     * Try to get a local service component
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
     * @return The logger of the service containe
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
        ServiceComponent serviceComponent = null;

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
        
        // add the read/write lock to the context
        
        serviceComponentContext.put(
            URN_YAAFI_KERNELLOCK,
            this.readWriteLock
            );            

        // create the remaining Avalon artifacts for the service component

        Logger serviceComponentLogger = this.getLogger().getChildLogger(
            roleEntry.getLogCategory()
            );

        Configuration serviceComponentConfiguraton = this.getServiceConfiguration().getChild(
            roleEntry.getShorthand()
            );

        Parameters serviceComponentParameters = this.getParameters();

        // configure the service component with all the artifacts

        serviceComponent.setLogger(serviceComponentLogger);
        serviceComponent.setServiceManager(this);
        serviceComponent.setContext(serviceComponentContext);
        serviceComponent.setConfiguration(serviceComponentConfiguraton);
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
     * Decommision a ist of services
     */
    private void decommisionAll(List serviceList)
    {
        ServiceComponent serviceComponent = null;

        for( int i=serviceList.size()-1; i>=0; i-- )
        {
            serviceComponent = (ServiceComponent) serviceList.get(i);
            this.decommision( serviceComponent );
        }
    }

    /**
     * Decommision of a single service component. Decommision consists of running the
     * whole Avalon decommision lifecycle process for a service component. After
     * decommision the service is not operational any longer. During decommisioning
     * we ignore any exceptions since it is quite common that something goes wrong.
     *
     * @param serviceComponent The service component to decommision
     */
    private void decommision( ServiceComponent serviceComponent )
    {
        this.getLogger().debug( "Decommision the service " + serviceComponent.getShorthand() );

        try
        {
            serviceComponent.decommision();
        }
        catch (Throwable e)
        {
            String msg = "Decommisioning the following service failed : " + serviceComponent.getName();
            this.getLogger().error( msg, e );
        }
    }

    /**
     * Disposing a ist of services
     */
    private void disposeAll(List serviceList)
    {
        ServiceComponent serviceComponent = null;

        for( int i=serviceList.size()-1; i>=0; i-- )
        {
            serviceComponent = (ServiceComponent) serviceList.get(i);
            this.dispose( serviceComponent );
        }
    }

    /**
     * Disposing of a single service component.
     
     * @param serviceComponent The service component to decommision
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

        RoleConfigurationParser roleConfigurationParser =
            this.createRoleConfigurationParser();

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
                    logger,
                    this.readWriteLock
                    );

                result.add( serviceComponent );
            }
            catch( Throwable t )
            {
                String msg = "Failed to load the service " + serviceComponent.getName();
                this.getLogger().error( msg, t );
                throw new ConfigurationException( msg, t );
            }
        }

        return result;
    }

    /**
     * Load a configuration file either from a file or using the class loader.
     * @param location the location of the file
     * @param isEncrypted  is the configuration encryped
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
                if( isEncrypted.equalsIgnoreCase("false") == false)
                {
                    is = this.getDecryptingInputStream( is, isEncrypted );
                }

                result = builder.build( is );

                is.close();
                is = null;
            }
            catch ( Exception e )
            {
                String msg = "Unable to parse the following file : " + location;
                this.getLogger().error( msg , e );
                throw e;
            }
        }

        return result;
    }

    /**
     * Load a configuration property file either from a file or using the class loader.
     * @param location the location of the file
     * @return The loaded proeperty file
     * @throws ConfigurationException Something went wrong
     */
    private Properties loadComponentConfigurationProperties()
    	throws ConfigurationException
    {
        Properties result = new Properties();
        ComponentConfigurationPropertiesResolver resolver = null;
        
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
            if( isEncrypted.equalsIgnoreCase("false") == false )
            {
                is = this.getDecryptingInputStream( is, isEncrypted );
            }

            Properties props = new Properties();
            props.load( is );
            result = Parameters.fromProperties( props );

            is.close();
            is = null;
        }

        return result;
    }


    /**
     * Creates a locator to find a resource either in the file system or in
     * the classpath
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
    private File setApplicationRootDir(File dir)
    {
        this.getLogger().debug( "Setting applicationRootDir to " + dir.getAbsolutePath() );

        Validate.notNull(dir,"applicationRootDir is <null>");
        Validate.isTrue(dir.exists(),"applicationRootDir does not exist");

        this.applicationRootDir = dir;
        return this.applicationRootDir;
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
        return (this.getParentServiceManager() != null ? true : false );
    }
    /**
     * Set the temporary directory of the container.
     * 
     * @param dir The tempRootDir to set.
     */
    private File setTempRootDir(File dir)
    {
        this.getLogger().debug( "Setting tempRootDir to " + dir.getAbsolutePath() );

        Validate.notNull(dir,"tempRootDir is <null>");
        Validate.isTrue(dir.exists(),"tempRootDir does not exist");
        Validate.isTrue(dir.canWrite(),"tempRootDir is not writeable");

        this.tempRootDir = dir;
        return this.tempRootDir;
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
     * @return an decrypting input stream
     * @throws IOException
     * @throws GeneralSecurityException
     */
    private InputStream getDecryptingInputStream( InputStream is, String isEncrypted )
        throws IOException, GeneralSecurityException
    {
        InputStream result = null;

        if( isEncrypted.equalsIgnoreCase("true") )
        {
            result = CryptoStreamFactoryImpl.getInstance().getInputStream(is);
        }
        else if( isEncrypted.equalsIgnoreCase("auto") )
        {
            result = CryptoStreamFactoryImpl.getInstance().getSmartInputStream(is);
        }
        else
        {
            result = is;
        }
        return result;
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
    private final boolean hasDynamicProxies()
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
     * @return a read lock
     */
    private final Object getReadLock()
    {
        try
        {
            return this.readWriteLock.getReadLock(AVALON_CONTAINER_YAAFI);
        }
        catch (InterruptedException e)
        {
            String msg = "Interrupted while getting read lock";
            throw new RuntimeException(msg);
        }
    }
    
    /**
     * @return a write lock
     */
    private final Object getWriteLock()
    {
        Object result = null;
        
        try
        {
            result = this.readWriteLock.getWriteLock(AVALON_CONTAINER_YAAFI);
            
            // wait for a certain time to get non-proxied services
            // either finished or blocked
            
            try
            {
                Thread.sleep( this.reconfigurationDelay );
            }
            catch (InterruptedException e)
            {
                // nothing to do
            }

            return result;
        }
        catch (InterruptedException e)
        {
            String msg = "Interrupted while getting read lock";
            throw new RuntimeException(msg);
        }
    }
    
    /**
     * Release the read/write lock.
     */
    private final void releaseLock(Object lock)
    {
        this.readWriteLock.releaseLock(lock, AVALON_CONTAINER_YAAFI);
    }
    
    /**
     * @return the containers class loader
     */
    private ClassLoader getClassLoader()
    {
        return this.getClass().getClassLoader();
    }
}
