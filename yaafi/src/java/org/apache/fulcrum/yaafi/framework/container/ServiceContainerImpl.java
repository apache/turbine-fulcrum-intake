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
import java.io.InputStream;
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
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.fulcrum.yaafi.framework.util.AvalonContextHelper;
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

	/** The service configuration file to be used */
	private String componentConfigurationLocation = COMPONENT_CONFIG_VALUE;

	/** The parameters file to be used */
	private String parametersLocation = COMPONENT_PARAMETERS_VALUE;

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

	/** The default Avalon context passed to the services */
	private Context context;

	/** The default Avalon parameters */
	private Parameters parameters;
	
	/** Is this instance initialized */
	private boolean isDisposed;

    /////////////////////////////////////////////////////////////////////////
    // Service Manager Lifecycle
    /////////////////////////////////////////////////////////////////////////

    /**
     * Constructor using sensible defaults.
     */
    public ServiceContainerImpl()
    {
        super();

        this.componentRolesLocation 		= COMPONENT_ROLE_VALUE;
        this.componentConfigurationLocation = COMPONENT_CONFIG_VALUE;
        this.parametersLocation 			= COMPONENT_PARAMETERS_VALUE;
        
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
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration configuration) throws ConfigurationException
    {        
        this.setComponentRolesLocation(
            configuration.getChild(COMPONENT_ROLE_KEYS).getValue( 
                COMPONENT_ROLE_VALUE )
                );

        this.setComponentConfigurationLocation(
            configuration.getChild(COMPONENT_CONFIG_KEY).getValue(
                COMPONENT_CONFIG_VALUE )
            	);

        this.setParametersLocation(
            configuration.getChild( COMPONENT_PARAMETERS_KEY).getValue(
                COMPONENT_PARAMETERS_VALUE )
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

		this.serviceConfiguration 	= loadConfiguration(this.componentConfigurationLocation);
		this.roleConfiguration 		= loadConfiguration(this.componentRolesLocation);

		if( this.roleConfiguration == null )
		{
			String msg = "Unable to locate the role configuration : " + this.componentRolesLocation;
			this.getLogger().error( msg );
			throw new ConfigurationException( msg );
		}
		
		// create a default context

		if( this.context == null )
		{
		    this.getLogger().debug("Creating a DefaultContext");
		    this.context = this.createDefaultContext();
		}

		// create the default parameters

		if( this.parameters == null )
		{
		    this.parameters = this.loadParameters( this.parametersLocation );
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
     * 
     * @see org.apache.avalon.framework.context.Contextualizable#contextualize(org.apache.avalon.framework.context.Context)
     */
    public void contextualize(Context context) throws ContextException
    {
        Object entry = null;
        File currApplicationRootDir = null;
        File currTempRootDir = null;
        boolean contextHasHomeEntry = false;
        boolean contextHasTempEntry = false;
        Context currContext = context;
        
        // 1) set the application directory
        
        // 1.1) check for URN_AVALON_HOME
         
        if( AvalonContextHelper.isInContext( currContext, URN_AVALON_HOME ) )
        { 
            contextHasHomeEntry = true;
            entry = currContext.get( URN_AVALON_HOME );
            
            // handle Fulcrum behaviour which passes a String
            
            if( entry instanceof String )
            {
                String dirName = (String) entry;
                
                if( dirName.length() == 0 )
                {
                    currApplicationRootDir = new File( new File(dirName).getAbsolutePath() ); 
                }
                else
                {
                    currApplicationRootDir = new File( dirName );   
                }
            }
            
            // handle Merlin behaviour which passes a File
            
            if( entry instanceof File )
            {
                currApplicationRootDir = (File) currContext.get( URN_AVALON_HOME );    
            }
        }
        
        // 1.2) if we found a value for URN_AVALON_HOME overwrite our default
        
        if( currApplicationRootDir != null )
        {
            this.setApplicationRootDir( currApplicationRootDir );
        }
             
        // 2) set the temporary directory
        
        // 2.1) check for URN_AVALON_TEMP
        
        if( AvalonContextHelper.isInContext( context, URN_AVALON_TEMP ) )
        {
            contextHasTempEntry = true;
            entry = currContext.get( URN_AVALON_TEMP );
            
            if( entry instanceof String )
            {
                currTempRootDir = new File( (String) entry );   
            }
            
            if( entry instanceof File )
            {
                currTempRootDir = (File) currContext.get( URN_AVALON_TEMP );    
            }
        }        

        // 2.2) if we found a value for URN_AVALON_TEMP overwrite our default
        
        if( currTempRootDir != null )
        {
            this.setTempRootDir( currTempRootDir );
        }
                
        // 3) E.g. Phoenix does not provide a value for URN_AVALON_HOME/URN_AVALON_TEMP
        //    which breaks the Merlin services .... :-(

        // 3.1) add en entry for URN_AVALON_HOME 
        
        if( contextHasHomeEntry == false )
        {
            currContext = AvalonContextHelper.addToContext( 
                currContext,
                URN_AVALON_HOME,
                this.getApplicationRootDir()
                );                
        }
        
        // 3.2) add en entry for URN_AVALON_HOME 
        
        if( contextHasTempEntry == false )
        {
            currContext = AvalonContextHelper.addToContext( 
                currContext,
                URN_AVALON_TEMP,
                this.getTempRootDir()
                );                
        }
        
        // 4) set our context finally
        
        this.context = currContext;
    }
    
    /**
     * Disposes the service container implementation.
     * 
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     */
    public synchronized void dispose()
    {
        if( this.isDisposed ) return;
        
        this.getLogger().info("Disposing all services");
        
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
        this.getLogger().debug( "All services are disposed" );
    }

    /**
     * Reconfiguring the services. I'm not sure hopw to implement this properly since
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
	protected void setComponentConfigurationLocation(String string)
	{
		this.componentConfigurationLocation = string;
	}

	/**
	 * @param string The location of the component role file
	 */
	protected void setComponentRolesLocation(String string)
	{
		this.componentRolesLocation = string;
	}

	/**
	 * @param string The location of the parameters file
	 */
	protected void setParametersLocation(String string)
	{
		this.parametersLocation = string;
	}
	
    /**
     * @return The logger of the service containe
     */
    protected Logger getLogger()
    {
        return this.logger;
    }

    /**
     * @return Returns the serviceMap.
     */
    protected Hashtable getServiceMap()
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
    protected void incarnate( ServiceComponent serviceComponent )
    	throws ContextException, ServiceException, ConfigurationException, ParameterException, Exception
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
    
    protected void decommision( ServiceComponent serviceComponent )
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
    	throws ContextException, ServiceException, ConfigurationException, ParameterException, Exception
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
     * @param location The location as a file
     * @return The loaded configuration
     * @throws Exception Something went wrong
     */
    private Configuration loadConfiguration( String location ) throws Exception
    {
		Configuration result = null;
		InputStreamLocator locator = this.createInputStreamLocator();
    	InputStream is = locator.locate( location );
		DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();

		if( is != null )
		{
			result = builder.build( is );
		}

		return result;
    }

	/**
	 * Load the parameters
	 * @param location The location as a file
	 * @return The loaded configuration
	 * @throws Exception Something went wrong
	 */
	private Parameters loadParameters( String location ) throws Exception
	{
	    InputStreamLocator locator = this.createInputStreamLocator();
		InputStream is = locator.locate( location );
		Configuration conf = null;
		Parameters result = new Parameters();

		if( is != null )
		{
			if( location.endsWith(".xml") )
			{
				result = Parameters.fromConfiguration( conf );
			}
			else
			{
				Properties props = new Properties();
				props.load( is );
				result = Parameters.fromProperties( props );
			}
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
	 * Create a default context. The context contains the following entries
	 * <ul>
	 *   <li>urn:avalon:home</li> 
	 *   <li>urn:avalon:temp</li>
	 *   <li>componentAppRoot</li>
	 * </ul>
	 * 
	 * @return a DefaultContext
	 */
	private DefaultContext createDefaultContext()
	{
		DefaultContext result = new DefaultContext();

		result.put( ServiceConstants.URN_AVALON_HOME, this.getApplicationRootDir() );
		result.put( ServiceConstants.URN_AVALON_TEMP, this.getTempRootDir() );
		result.put( ServiceConstants.COMPONENT_APP_ROOT, this.getApplicationRootDir().getAbsolutePath() );
		
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
}
