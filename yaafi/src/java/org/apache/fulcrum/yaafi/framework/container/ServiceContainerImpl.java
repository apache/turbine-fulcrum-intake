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
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.fulcrum.jce.crypto.CryptoStreamFactoryImpl;
import org.apache.fulcrum.yaafi.framework.component.AvalonServiceComponentImpl;
import org.apache.fulcrum.yaafi.framework.component.ServiceComponent;
import org.apache.fulcrum.yaafi.framework.constant.AvalonYaafiConstants;
import org.apache.fulcrum.yaafi.framework.role.RoleConfigurationParser;
import org.apache.fulcrum.yaafi.framework.role.RoleConfigurationParserImpl;
import org.apache.fulcrum.yaafi.framework.role.RoleEntry;
import org.apache.fulcrum.yaafi.framework.util.AvalonToYaafiContextMapper;
import org.apache.fulcrum.yaafi.framework.util.InputStreamLocator;
import org.apache.fulcrum.yaafi.framework.util.Validate;
import org.apache.fulcrum.yaafi.framework.util.YaafiToAvalonContextMapper;

/**
 * Yet another avalon framework implementation (YAAFI).
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public class ServiceContainerImpl
    implements ServiceContainer, ServiceConstants
{
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

    /** The list of services instantiated */
    private List serviceList;

    /** The map of services used for the lookup */
    private Hashtable serviceMap;

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
    
    /////////////////////////////////////////////////////////////////////////
    // Avalon Service Lifecycle
    /////////////////////////////////////////////////////////////////////////

    /**
     * Constructor using sensible defaults.
     */
    public ServiceContainerImpl()
    {
        super();

        this.reconfigurationDelay = 2000;
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
        this.serviceMap = new Hashtable();

        this.applicationRootDir = new File( new File("").getAbsolutePath() );
        this.tempRootDir = new File( System.getProperty("java.io.tmpdir",".") );
    }

    /**
     * @see org.apache.avalon.framework.logger.LogEnabled#enableLogging(org.apache.avalon.framework.logger.Logger)
     */
    public synchronized void enableLogging(Logger logger)
    {
        Validate.notNull( logger, "logger" );
        this.logger = logger;
    }

    /**
     * @see org.apache.avalon.framework.context.Contextualizable#contextualize(org.apache.avalon.framework.context.Context)
     */
    public synchronized void contextualize(Context context) throws ContextException
    {
        Validate.notNull( context, "context" );
        // Argghhh - I need to to parse the Configuration before I can map the Context
        this.callerContext = context;
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public synchronized void configure(Configuration configuration) throws ConfigurationException
    {
        Validate.notNull( configuration, "configuration" );

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
                this.callerContext
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

        // evaluate parameters

        Configuration currParameters = configuration.getChild(COMPONENT_PARAMETERS_KEY);

        this.setParametersLocation(
            currParameters.getChild(COMPONENT_LOCATION_KEY).getValue(
                COMPONENT_CONFIG_VALUE )
                );

        this.setParametersEncrypted(
            currParameters.getChild(COMPONENT_ISENCRYPTED_KEY).getValue(
                "false" )
                );
    }

    /**
     * Initializes the service container implementation.
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */

    public synchronized void initialize() throws Exception
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

        // fill the service map

        for( int i=0; i<this.getServiceList().size(); i++ )
        {
            ServiceComponent serviceComponent = (ServiceComponent) this.getServiceList().get(i);
            this.getServiceMap().put( serviceComponent.getName(), serviceComponent );
        }

        // run the various lifecycle stages

        this.incarnate( this.getServiceList() );

        // we are up and running

        this.isDisposed = false;
        this.getLogger().debug( "Service Framework is up and running");
    }


    /**
     * Disposes the service container implementation.
     *
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     */
    public synchronized void dispose()
    {
        if( this.isDisposed )
        {
            return;
        }

        if( this.getLogger() != null )
        {
            this.getLogger().info("Disposing all services");
        }

        // decommision all servcies

        this.decommision( this.getServiceList() );

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
        ServiceComponent serviceComponent = null;

        this.getLogger().warn("Reconfiguring all services ...");
        this.waitForReconfiguration();
        
        // 1) store the new configuration

        this.serviceConfiguration = configuration;
        
        // 2) reconfigure the services
        
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
                exceptionCounter++;
            }
        }

        // 3) check the result
        
        if( exceptionCounter > 0 )
        {
            String msg = "The reconfiguration failed with " + exceptionCounter + " exception(s)";
            this.getLogger().error(msg);
            throw new ConfigurationException(msg);
        }
    }

    /**
     * @see org.apache.fulcrum.yaafi.framework.container.ServiceLifecycleManager#getServiceComponent(java.lang.String)
     */
    public synchronized RoleEntry getRoleEntry(String name)
        throws ServiceException
    {
        return this.getServiceComponentEx(name).getRoleEntry();
    }

    /**
     * @see org.apache.fulcrum.yaafi.framework.container.ServiceLifecycleManager#getServiceComponents()
     */
    public synchronized RoleEntry[] getRoleEntries()
        throws ServiceException
    {
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

    /**
     * @see org.apache.fulcrum.yaafi.framework.container.ServiceLifecycleManager#reconfigure(java.lang.String)
     */
    public synchronized void reconfigure(String name)
        throws ServiceException, ConfigurationException
    {
        this.waitForReconfiguration();
        this.reconfigureNow(name);
    }

    /**
     * @see org.apache.fulcrum.yaafi.framework.container.ServiceLifecycleManager#reconfigure(java.lang.String[])
     */
    public void reconfigure(String[] names) throws ServiceException,
        ConfigurationException
    {
        Validate.notNull(names,"names");
        Validate.noNullElements(names,"names");
        
        this.waitForReconfiguration();
        
        for( int i=0; i<names.length; i++ )
        {
            this.reconfigureNow(names[i]);
        }
    }
    
    /////////////////////////////////////////////////////////////////////////
    // Service Interface Implementation
    /////////////////////////////////////////////////////////////////////////

    /**
     * @see org.apache.avalon.framework.service.ServiceManager#hasService(java.lang.String)
     */
    public synchronized boolean hasService(String name)
    {
        Validate.notEmpty( name, "name" );

        ServiceComponent serviceComponent =
            (ServiceComponent) this. getServiceMap().get(name);

        return ( serviceComponent != null ? true : false );
    }

    /**
     * @see org.apache.avalon.framework.service.ServiceManager#lookup(java.lang.String)
     */
    public synchronized Object lookup(String name) throws ServiceException
    {
        Validate.notEmpty( name, "name" );

        Object result = null;
        ServiceComponent serviceComponent = this.getServiceComponentEx(name);

        try
        {
            result = serviceComponent.getInstance();
        }
        catch (Exception e)
        {
            String msg = "Failed to lookup the service " + serviceComponent.getShorthand();
            this.getLogger().error( msg, e );
            throw new ServiceException( serviceComponent.getShorthand(), msg, e );
        }

        return result;
    }

    /**
     * @see org.apache.avalon.framework.service.ServiceManager#release(java.lang.Object)
     */
    public synchronized void release(Object arg0)
    {
        // AFAIK this is only useful for lifecycle management regarding
        // lifestyle other than singleton.
    }

    /**
     * @see org.apache.fulcrum.yaafi.framework.container.ServiceContainer#decommision(java.lang.String)
     */
    public synchronized void decommision(String name) throws ServiceException
    {
        ServiceComponent serviceComponent = this.getServiceComponentEx(name);
        this.decommision(serviceComponent);
    }

    /////////////////////////////////////////////////////////////////////////
    // Service Implementation
    /////////////////////////////////////////////////////////////////////////

    private RoleConfigurationParser createRoleConfigurationParser()
    {
        return new RoleConfigurationParserImpl(
            this.getComponentRolesFlavour()
            );
    }
    
    /**
     * @see org.apache.fulcrum.yaafi.framework.container.ServiceLifecycleManager#reconfigure(java.lang.String)
     */
    private synchronized void reconfigureNow(String name)
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
     * Enforce that a service is known to simplify error handling
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
            throw new ServiceException( "yaafi", name );
        }
        else
        {
            return result;
        }
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
    private Hashtable getServiceMap()
    {
        return this.serviceMap;
    }

    /**
     * Incarnation of a list of services
     */
    private void incarnate(List serviceList)
        throws Exception
    {
        ServiceComponent serviceComponent = null;

        for( int i=0; i<serviceList.size(); i++ )
        {
            serviceComponent = (ServiceComponent) this.getServiceList().get(i);
            this.incarnate( serviceComponent );
        }
    }

    /**
     * Incarnation of a single service component. Incarnation consists of running the
     * whole Avalon incarnation lifecycle process for a service component. After the
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

        // map the context according to the Avalon component type

        YaafiToAvalonContextMapper mapper = new YaafiToAvalonContextMapper(
            serviceComponent.getName(),
            serviceComponent.getClass().getClassLoader()
            );

        RoleEntry roleEntry = serviceComponent.getRoleEntry();
        
        Context serviceComponentContext = mapper.mapTo(
            this.getContext(),
            roleEntry.getComponentFlavour()
            );

        // create the remaining Avalon artifcats for the service component
        
        Logger serviceComponentLogger = this.getLogger().getChildLogger( 
            roleEntry.getShorthand() 
            ); 
        
        Configuration serviceComponentConfiguraton = this.getServiceConfiguration().getChild(
            roleEntry.getShorthand() 
            );
        
        Parameters serviceComponentParameters = this.getParameters();

        // process the Avalon lifecycle definition

        serviceComponent.setLogger(serviceComponentLogger);
        serviceComponent.setServiceManager(this);
        serviceComponent.setContext(serviceComponentContext);
        serviceComponent.setConfiguration(serviceComponentConfiguraton);
        serviceComponent.setParameters(serviceComponentParameters);
        
        serviceComponent.incarnate();
    }

    /**
     * Decommision a ist of services
     */
    private void decommision(List serviceList)
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
     * Factory method for creating services
     * @param serviceList
     * @throws Exception
     */

    private List createServiceComponents(Configuration roleConfiguration, Logger logger )
        throws ConfigurationException
    {
        Validate.notNull(roleConfiguration,"roleConfiguration");
        Validate.notNull(logger,"logger");

        ArrayList result = new ArrayList();
        ServiceComponent serviceComponent = null;       
                
        // create an appropriate instance of rol configuration parser
        
        RoleConfigurationParser roleConfigurationParser = 
            this.createRoleConfigurationParser();
        
        // extract the role entries
        
        RoleEntry[] roleEntryList = roleConfigurationParser.parse(roleConfiguration);
        
        for ( int i=0; i<roleEntryList.length; i++ )
        {
            try
            {
                // create the service components
                
                serviceComponent = new AvalonServiceComponentImpl(
                    roleEntryList[i],
                    logger
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
     * @param applicationRootDir The applicationRootDir to set.
     */
    private File setApplicationRootDir(File dir)
    {
        Validate.notNull(dir,"applicationRootDir is <null>");
        Validate.isTrue(dir.exists(),"applicationRootDir does not exist");
        this.getLogger().debug( "Setting applicationRootDir to " + dir.getAbsolutePath() );
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
     * @param tempRootDir The tempRootDir to set.
     */
    private File setTempRootDir(File dir)
    {
        Validate.notNull(dir,"tempRootDir is <null>");
        Validate.isTrue(dir.exists(),"tempRootDir does not exist");
        Validate.isTrue(dir.canWrite(),"tempRootDir is not writeable");
        this.getLogger().debug( "Setting tempRootDir to " + dir.getAbsolutePath() );
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
     * @param is
     * @return
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
     * @return Returns the parameters.
     */
    private Parameters getParameters()
    {
        return parameters;
    }
    
    private void waitForReconfiguration()
    {
        try
        {
            Thread.sleep(this.reconfigurationDelay);
        }
        catch (InterruptedException e)
        {
            // ignore
        }
    }
}
