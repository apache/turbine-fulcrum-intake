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
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.fulcrum.jce.crypto.CryptoStreamFactoryImpl;
import org.apache.fulcrum.yaafi.framework.util.AvalonToYaafiContextMapper;
import org.apache.fulcrum.yaafi.framework.util.InputStreamLocator;

/**
 * Yet another avalon framework implementation (yaafi).
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public class ServiceContainerImpl
    implements ServiceContainer, ServiceConstants
{
    /** The role configuration file to be used */
    private String componentRolesLocation = COMPONENT_ROLE_VALUE;

    /** is the component role file encrypted? */
    private boolean isComponentRolesEncrypted;

	/** The service configuration file to be used */
	private String componentConfigurationLocation = COMPONENT_CONFIG_VALUE;

    /** is the component configuration file encrypted? */
    private boolean isComponentConfigurationEncrypted;
	
	/** The parameters file to be used */
	private String parametersLocation = COMPONENT_PARAMETERS_VALUE;

    /** is the parameters file encrypted? */
    private boolean isParametersEncrypted;

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

	/** The Avalon context passed to the implementation */
	private Context callerContext;

	/** The default Avalon context passed to the services */
	private Context context;

	/** The default Avalon parameters */
	private Parameters parameters;
	
	/** Is this instance already disposed? */
	private boolean isDisposed;
	
	/** The type of container where YAAFI is embedded */
	private String containerType;

    /////////////////////////////////////////////////////////////////////////
    // Service Manager Lifecycle
    /////////////////////////////////////////////////////////////////////////

    /**
     * Constructor using sensible defaults.
     */
    public ServiceContainerImpl()
    {
        super();

        this.containerType = COMPONENT_CONTAINERTYPE_VALUE;
        this.componentRolesLocation = COMPONENT_ROLE_VALUE;
        this.componentConfigurationLocation = COMPONENT_CONFIG_VALUE;
        this.parametersLocation = COMPONENT_PARAMETERS_VALUE;
        
        this.isComponentConfigurationEncrypted = false;
        this.isComponentRolesEncrypted = false;
        this.isParametersEncrypted = false;
        
        this.isDisposed			= false;
        this.applicationRootDir = new File( new File("").getAbsolutePath() );
        this.tempRootDir 		= new File( System.getProperty("java.io.tmpdir","."));
        this.serviceList		= new ArrayList();
        this.serviceMap			= new Hashtable();
    }
    
    /**
     * @see org.apache.avalon.framework.logger.LogEnabled#enableLogging(org.apache.avalon.framework.logger.Logger)
     */
    public void enableLogging(Logger logger)
    {
        this.logger = logger;
    }

    /** 
     * @see org.apache.avalon.framework.context.Contextualizable#contextualize(org.apache.avalon.framework.context.Context)
     */
    public void contextualize(Context context) throws ContextException
    {
        // Argghhh - I need to to parse the Configuration before I can map the Context
        this.callerContext = context;
    }    
        
    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration configuration) throws ConfigurationException
    {
        // retrieve the container type
        
        Configuration containerTypeConfiguration = configuration.getChild(CONTAINERTYPE_CONFIG_KEY);

        if( containerTypeConfiguration != null )
        {
            this.setContainerType(
                containerTypeConfiguration.getValue( COMPONENT_CONTAINERTYPE_VALUE )
                );
        }
        
        this.getLogger().debug( "Using the following container type : " + this.getContainerType() );
        
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
                this.getContainerType()
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

        this.setComponentRolesEncrypted(
            currComponentRoles.getChild(COMPONENT_ISENCRYPTED_KEY).getValueAsBoolean( 
                false )               
                );
        
        // evaluate componentConfiguraion
        
        Configuration currComponentConfiguration = configuration.getChild(COMPONENT_CONFIG_KEY);
            
        this.setComponentConfigurationLocation(
            currComponentConfiguration.getChild(COMPONENT_LOCATION_KEY).getValue( 
                COMPONENT_CONFIG_VALUE )
                );

        this.setComponentConfigurationEncrypted(
            currComponentConfiguration.getChild(COMPONENT_ISENCRYPTED_KEY).getValueAsBoolean( 
                false )               
                );
        
        // evaluate parameters        

        Configuration currParameters = configuration.getChild(COMPONENT_PARAMETERS_KEY);

        this.setParametersLocation(
            currParameters.getChild(COMPONENT_LOCATION_KEY).getValue( 
                COMPONENT_CONFIG_VALUE )
                );

        this.setParametersEncrypted(
            currParameters.getChild(COMPONENT_ISENCRYPTED_KEY).getValueAsBoolean( 
                false )               
                );        
    }

    /**
     * Initializes the service container implementation.
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */

    public void initialize() throws Exception
    {
        this.getLogger().debug( "Service Framework is starting up");
        
        // print some diagnostics
        
        this.getLogger().debug( 
            "Using the following applicationRootDir : " + this.getApplicationRootDir().getAbsolutePath() 
            );

        this.getLogger().debug( 
            "Using the following tempRootDir : " + this.getTempRootDir().getAbsolutePath() 
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

		if( this.parameters == null )
		{
		    this.parameters = this.loadParameters( 
		        this.parametersLocation,
		        this.isParametersEncrypted()
		        );
		}

		// create the service implementaion instances

		List currServiceList = this.createServiceComponents(this.roleConfiguration,this.logger);
		this.setServiceList( currServiceList );

		// fill the service map
		
		for( int i=0; i<this.serviceList.size(); i++ )
		{
		    ServiceComponent serviceComponent = (ServiceComponent) this.serviceList.get(i);
		    this.serviceMap.put( serviceComponent.getName(), serviceComponent );
		}
		
		// run the various lifecycle stages

		this.incarnate( this.serviceList );
		
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
        
        this.serviceList.clear();
        this.serviceMap.clear();
        
        this.componentRolesLocation			= null;
        this.componentConfigurationLocation	= null;
        this.context 						= null;
        this.parametersLocation				= null;
        this.roleConfiguration				= null;
        this.serviceConfiguration			= null;
        this.parameters						= null;
        
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
    public synchronized void reconfigure(Configuration configuration) throws ConfigurationException
    {
        ServiceComponent serviceComponent = null;

        this.getLogger().info("Reconfiguring all services");

        // 1) suspend the services

        for( int i=this.getServiceList().size()-1; i>=0; i-- )
        {
            serviceComponent = (ServiceComponent) this.getServiceList().get(i);
            serviceComponent.suspend();
        }

        // 2) reconfigure the services

        for( int i=0; i<this.getServiceList().size(); i++ )
        {
            serviceComponent = (ServiceComponent) this.getServiceList().get(i);
            serviceComponent.reconfigure(this.getServiceConfiguration());
        }

        // 3) resume the services

        for( int i=0; i<this.getServiceList().size(); i++ )
        {
            serviceComponent = (ServiceComponent) this.getServiceList().get(i);
            serviceComponent.resume();
        }
    }

	/////////////////////////////////////////////////////////////////////////
	// Service Manager Implementation
	/////////////////////////////////////////////////////////////////////////

    /**
     * @see org.apache.avalon.framework.service.ServiceContainer#hasService(java.lang.String)
     */
    public synchronized boolean hasService(String name)
    {
        ServiceComponent serviceComponent = 
            (ServiceComponent) this. getServiceMap().get(name);
        
        return ( serviceComponent != null ? true : false );
    }

    /**
     * @see org.apache.avalon.framework.service.ServiceContainer#lookup(java.lang.String)
     */
    public synchronized Object lookup(String name) throws ServiceException
    {
        Object result = null;
        ServiceComponent serviceComponent = null;

        serviceComponent = (ServiceComponent) this. getServiceMap().get(name);
        
        if( serviceComponent != null )
        {    
            try
            {
                if( serviceComponent.isInstantiated() )
                {
                    // This component is initialized early
                    result = serviceComponent.getInstance();
                }
                else
                {
                    // This component is initialized lazily or was disposed before
                    result = serviceComponent.create();
                    this.incarnate( serviceComponent );
                }
            }
            catch (Exception e)
            {
                String msg = "Failed to lookup the service " + serviceComponent.getShorthand();
                this.getLogger().error( msg, e );
                throw new ServiceException( serviceComponent.getShorthand(), msg, e );
            }                    	                
        }
        
        if( result == null )
        {	
            String msg = "Service not found : " + name;
            this.getLogger().error(msg);
            throw new ServiceException(name, msg );
        }
        else
        {
            return result;
        }
    }

    /**
     * @see org.apache.avalon.framework.service.ServiceContainer#release(java.lang.Object)
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
        ServiceComponent serviceComponent = null;

        serviceComponent = (ServiceComponent) this. getServiceMap().get(name);
       
        // lookup the service component
        
        if( serviceComponent == null )
        {
            String msg = "Service not found : " + name;
            this.getLogger().error(msg);
            throw new ServiceException(name, msg );
        }
        
        // stop the service component
        
        try
        {
            serviceComponent.stop();
        }
        catch (Exception e)
        {
            String msg = "Unable to stop the service : " + name ;
            throw new ServiceException( name, msg, e );
        }
        
        // dispose the service component
        
        try
        {
            serviceComponent.dispose();
        }
        catch (Exception e)
        {
            String msg = "Unable to diospose the service : " + name ;
            throw new ServiceException( name, msg, e );
        }        
       
    }
    
    /////////////////////////////////////////////////////////////////////////
    // Service YaffiContainer Implementation
    /////////////////////////////////////////////////////////////////////////
        
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
     * Incarnation of a single service component. Incarnation consists of running the
     * whole Avalon incarnation lifecycle process for a service component. After the
     * incarnation the service if operational.
     * 
     * @param serviceComponent The service component to incarnate 
     */
	private void incarnate( ServiceComponent serviceComponent )
    	throws Exception
    {        
        this.getLogger().debug( "Incarnating the service " + serviceComponent.getShorthand() );
     
        // setup a context for the individual component containing
        // "urn:avalon:name" and "urn:avalon:classLoader"
        
        DefaultContext serviceComponentContext = new DefaultContext( this.context );        
        serviceComponentContext.put( URN_AVALON_NAME, serviceComponent.getName() );
        serviceComponentContext.put( URN_AVALON_CLASSLOADER, serviceComponent.getClass().getClassLoader() );
        
        // process the lifecycle definition
        
        serviceComponent.enableLogging( this.getLogger() );
        serviceComponent.contextualize( serviceComponentContext );
        serviceComponent.service( this );
        serviceComponent.configure( this.getServiceConfiguration() );
	    serviceComponent.parameterize( this.parameters );
	    serviceComponent.initialize();
        serviceComponent.execute();
        serviceComponent.start();
    }

    /**
     * Decommision of a single service component. Decommision consists of running the
     * whole Avalon decommision lifecycle process for a service component. After the
     * Decommision the service is not operational any longer. During decommisioning 
     * we ignore any sxceptions since it is quite common that something goes wrong.
     * 
     * @param serviceComponent The service component to decommision 
     */
	private void decommision( ServiceComponent serviceComponent )
    {
        this.getLogger().debug( "Decommisioning the service " + serviceComponent.getShorthand() );
        
        try
        {
            serviceComponent.stop();
        }
        catch (Throwable e)
        {
            String msg = "Stopping the following service failed : " + serviceComponent.getName();
            this.getLogger().error( msg, e );
        }

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
     * Incarnation of a list of services
     */
    private void incarnate(List serviceList)
    	throws Exception
    {
        ServiceComponent serviceComponent = null;

		for( int i=0; i<serviceList.size(); i++ )
		{
		    serviceComponent = (ServiceComponent) this.serviceList.get(i);
		    this.incarnate( serviceComponent );
		}
    }

    /**
     * Decommision a ist of services
     */
    private void decommision(List serviceList)
    {
        ServiceComponent serviceComponent = null;

        for( int i=serviceList.size()-1; i>=0; i-- )
        {
            serviceComponent = (ServiceComponent) this.getServiceList().get(i);
            this.decommision( serviceComponent );
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
        if( roleConfiguration == null )
        {
            String msg = "The roleConfiguration is <null>";
            throw new ConfigurationException(msg);
        }

		ArrayList result = new ArrayList();
		Configuration[] roleListEntries = getRoleConfigurationList(roleConfiguration);

        ServiceComponent serviceComponent = null;

        for ( int i=0; i<roleListEntries.length; i++ )
        {
            try
            {
                serviceComponent = new ServiceComponentImpl(roleListEntries[i],logger);
                
                // If late initialization is used we still load the class to
                // ensure the integrity of the configuration
                
                if( serviceComponent.isEarlyInit() )
                {
                    serviceComponent.loadClass();
                    serviceComponent.create();
                }
                else
                {
                    serviceComponent.loadClass();
                }
                
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
    private Configuration loadConfiguration( String location, boolean isEncrypted ) 
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
		        if( isEncrypted )
		        {
		            is = this.getDecryptingInputStream( is );
		        }
		        
		        result = builder.build( is );
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
	private Parameters loadParameters( String location, boolean isEncrypted ) throws Exception
	{
	    InputStreamLocator locator = this.createInputStreamLocator();
		InputStream is = locator.locate( location );
		Parameters result = new Parameters();

		if( is != null )
		{
	        if( isEncrypted )
	        {	            
	            is = this.getDecryptingInputStream( is );
	        }

			Properties props = new Properties();
			props.load( is );
			result = Parameters.fromProperties( props );
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
	 * Extract the metadate for creating the services
	 * @param roleConfiguration
	 * @return
	 */
	private Configuration[] getRoleConfigurationList( Configuration roleConfiguration )
	{
	    Configuration[] result = null;
	    ArrayList roleListEntries = new ArrayList();
	    String rootElementName = roleConfiguration.getName();
		
        if( rootElementName.equals("role-list") )
        {
            // Excalibur style definition using /role-list/role
            
            roleListEntries.addAll( Arrays.asList( roleConfiguration.getChildren() ) );
        }
        else if( rootElementName.equals("container") )
        {
            // Merlin style definition using /container/component
            
            Configuration[] temp = roleConfiguration.getChildren();
            for( int i=0; i<temp.length; i++ )
            {
                if( temp[i].getName().equals("component") )
                {
                    roleListEntries.add(temp[i]);   
                }
            }
        }	 
        else
        {
            throw new RuntimeException(
                "Don't know how to parse the roleConfiguration"
                );
        }
        
        result = (Configuration[]) roleListEntries.toArray( 
            new Configuration[roleListEntries.size()] 
            );
        
        return result;
	}
			
    /**
     * @param applicationRootDir The applicationRootDir to set.
     */
    private void setApplicationRootDir(File dir)
    {
        if( dir == null )
        {
            String msg = "The applicationRootDir is <null>";
            throw new IllegalArgumentException ( msg );
        }
        
        if( dir.exists() == false )
        {
            String msg = "The applicatonRootDir " + dir.getAbsolutePath() + " does not exist";
            throw new IllegalArgumentException ( msg );            
        }
        
        this.getLogger().debug( "Setting applicationRootDir to " + dir.getAbsolutePath() );
        
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
     * @param tempRootDir The tempRootDir to set.
     */
    private void setTempRootDir(File dir)
    {
        if( dir == null )
        {
            String msg = "The tempRootDir is <null>";
            throw new IllegalArgumentException ( msg );
        }
        
        if( dir.exists() == false )
        {
            String msg = "The tempRootDir " + dir.getAbsolutePath() + " does not exist";
            throw new IllegalArgumentException ( msg );            
        }
     
        this.getLogger().debug( "Setting tempRootDir to " + dir.getAbsolutePath() );
        
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
    private boolean isComponentConfigurationEncrypted()
    {
        return isComponentConfigurationEncrypted;
    }
    
    /**
     * @param isComponentConfigurationEncrypted The isComponentConfigurationEncrypted to set.
     */
    private void setComponentConfigurationEncrypted(
        boolean isComponentConfigurationEncrypted)
    {
        this.isComponentConfigurationEncrypted = isComponentConfigurationEncrypted;
    }
    
    /**
     * @return Returns the isComponentRolesEncrypted.
     */
    private boolean isComponentRolesEncrypted()
    {
        return isComponentRolesEncrypted;
    }
    
    /**
     * @param isComponentRolesEncrypted The isComponentRolesEncrypted to set.
     */
    private void setComponentRolesEncrypted(boolean isComponentRolesEncrypted)
    {
        this.isComponentRolesEncrypted = isComponentRolesEncrypted;
    }
    
    /**
     * @return Returns the isParametersEncrypted.
     */
    private boolean isParametersEncrypted()
    {
        return isParametersEncrypted;
    }
    
    /**
     * @param isParametersEncrypted The isParametersEncrypted to set.
     */
    private void setParametersEncrypted(boolean isParametersEncrypted)
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
    private InputStream getDecryptingInputStream( InputStream is )
    	throws IOException, GeneralSecurityException
    {
        InputStream result = is;        
        result = CryptoStreamFactoryImpl.getInstance().getInputStream(is);    
        return result;
    }
    
    /**
     * @return Returns the containerType.
     */
    private String getContainerType()
    {
        return containerType;
    }
    /**
     * @param containerType The containerType to set.
     */
    private void setContainerType(String containerType)
    {
        this.containerType = containerType;
    }
}
