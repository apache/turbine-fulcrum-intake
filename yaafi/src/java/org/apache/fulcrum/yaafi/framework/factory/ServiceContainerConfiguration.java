package org.apache.fulcrum.yaafi.framework.factory;

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
import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.logger.Logger;
import org.apache.fulcrum.jce.crypto.CryptoStreamFactoryImpl;
import org.apache.fulcrum.yaafi.framework.container.AvalonMerlinConstants;
import org.apache.fulcrum.yaafi.framework.container.ServiceConstants;
import org.apache.fulcrum.yaafi.framework.util.InputStreamLocator;

/**
 * Helper class to capture configuration related stuff. The are two ways
 * for setting up the configuration:
 * <ul>
 * 	<li>set all parameters manually</li>
 *  <li>use a containerConfiguration file and provide the remaining settings</li>
 * </ul> 
 * 
 * The Avalon context and configuration are created by  
 * <ul>
 * 	<li>createFinalContext()</li>
 *  <li>createFinalConfiguration()</li>
 * </ul>  
 * 
 *  @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public class ServiceContainerConfiguration
{
    /** our default implementation class of the service container */
    private String serviceContainerClazzName;

    /** the location of the component role file */
    private String componentRolesLocation;
    
    /** is the component role file encrypted? */
    private boolean isComponentRolesEncrypted;
    
    /** the location of the component configuration file */
    private String componentConfigurationLocation;

    /** is the component configuration file encrypted? */
    private boolean isComponentConfigurationEncrypted;

    /** the location of the paramaters file */
    private String parametersLocation;

    /** is the parameters file encrypted? */
    private boolean isParametersEncrypted;
    
    /** the user-supplied Avalon context */
    private DefaultContext context;
    
    /** the Avalon */
    private Logger logger;
    
    /** the application directory */
    private File applicationRootDir;
    
    /** the temporary directory */
    private File tempRootDir;
    
    /** the class loader passed in the Avalon Context */
    private ClassLoader componentClassLoader;
    
    /** the type of container where YAAFI is embedded */
    private String containerType;
    
    /** the caller-supplied container configuration */
    private Configuration containerConfiguration;    
    
    /** Constructor */    
    public ServiceContainerConfiguration()
    {
        this.logger = new ConsoleLogger();
        this.containerType = ServiceConstants.AVALON_CONTAINER_YAAFI;
        this.serviceContainerClazzName = ServiceConstants.CLAZZ_NAME;
        this.componentRolesLocation = ServiceConstants.COMPONENT_ROLE_VALUE;
        this.isComponentRolesEncrypted = false;
        this.componentConfigurationLocation = ServiceConstants.COMPONENT_CONFIG_VALUE;
        this.isComponentConfigurationEncrypted = false;
        this.parametersLocation = ServiceConstants.COMPONENT_PARAMETERS_VALUE;
        this.isParametersEncrypted = false;        
        this.context = new DefaultContext();
        this.applicationRootDir = new File( new File("").getAbsolutePath() );
        this.tempRootDir = new File( System.getProperty("java.io.tmpdir","."));      
        this.componentClassLoader = this.getClass().getClassLoader();        
    }
    
	/**
	 * Add a new entry to the context by creating a new one.
	 * @param name the name of the new entry
	 * @param value the value of the new entry
	 */
	public void addToContext( String name, Object value )
	{
	    this.getContext().put( name, value );
	}    

	/**
	 * Add a hashtable to the context 
	 * @param hashtable the Hashtable to be added
	 */
	public void addToContext( Hashtable hashtable )
	{
	    String name = null;
	    Object value = null;
	    Enumeration keys = hashtable.keys();
	    
	    while( keys.hasMoreElements() )
	    {
	        name = (String) keys.nextElement();
	        value = hashtable.get( name );
	        this.addToContext( name, value );
	    }
	}    

	/**
	 * Create the final Avalon context passed to YAAFI containing
	 * <ul>
	 *   <li>user-supplied context</li>
	 *   <li>urn:avalon:home</li>
	 *   <li>urn:avalon:temp</li>
	 *   <li>urn:avalon:name</li>
	 *   <li>urn:avalon:partition</li>
	 *   <li>urn:avalon:classloader</li>
	 * </ul>
	 * 
	 * @return the final Context
	 */
	
	public Context createFinalContext()
	{	    
	    // 1) add the application root dir
	    
	    this.addToContext( 
	        AvalonMerlinConstants.URN_AVALON_HOME,
	        this.getApplicationRootDir()
	        );

	    // 2) add the temp root dir
	    
	    this.addToContext( 
	        AvalonMerlinConstants.URN_AVALON_TEMP,
	        this.getTempRootDir()
	        );
	    
	    // 3) add the Avalon name
	    
	    this.addToContext(
	        AvalonMerlinConstants.URN_AVALON_NAME,
	        ServiceConstants.ROLE_NAME
	        );

	    // 4) add the Avalon partition name
	    
	    this.addToContext(
	        AvalonMerlinConstants.URN_AVALON_PARTITION,
	        "root"
	        );
	    
	    // 5) add the class loader
	    
	    this.addToContext(
	        AvalonMerlinConstants.URN_AVALON_CLASSLOADER,
	        this.getComponentClassLoader()
	        );	   
	    
	    return this.getContext();
	}
	
	/**
	 * Create a final configuratin 
	 * @return the configuration
	 */
	public Configuration createFinalConfiguration()
	{	    
	    DefaultConfiguration result = null;

	    if( this.getContainerConfiguration() != null )
	    {
	        return this.getContainerConfiguration();
	    }
	    else
	    {
		    // the root element is "fulcrum-yaafi"
		    
	        result = new DefaultConfiguration(
	            ServiceConstants.ROLE_NAME
	            );
	
	        // add the following fragement
	        //
	        // <containerType>merlin</containerType>
	        
	        DefaultConfiguration containerTypeConfig = new DefaultConfiguration(
	            ServiceConstants.CONTAINERTYPE_CONFIG_KEY
	            );
	        
	        containerTypeConfig.setValue( this.getContainerType() );
	        
	        result.addChild( containerTypeConfig );
	
	        // add the following fragement
	        //
	        // <containerClazzName>...</containerClazzName>
	        
	        DefaultConfiguration containerClazzNameConfig = new DefaultConfiguration(
	            ServiceConstants.CONTAINERCLAZZNAME_CONFIG_KEY
	            );
	        
	        containerClazzNameConfig.setValue( this.getServiceContainerClazzName() );
	        
	        result.addChild( containerClazzNameConfig );
	
	        
	        // add the following fragement
	        //
	        // <componentRoles>
	        //  <location>../conf/componentRoles.xml</location>
	        //  <isEncrypted>true</isEncrypted>
	        // </componentRoles>
	
	        DefaultConfiguration componentRolesConfig = new DefaultConfiguration(
	            ServiceConstants.COMPONENT_ROLE_KEYS
	            );
	        
	        DefaultConfiguration componentRolesLocation = new DefaultConfiguration(
	            ServiceConstants.COMPONENT_LOCATION_KEY
	            );
	
	        componentRolesLocation.setValue( 
	            this.getComponentRolesLocation()
	            );
	
	        DefaultConfiguration componentRolesIsEncrypted = new DefaultConfiguration(
	            ServiceConstants.COMPONENT_ISENCRYPTED_KEY
	            );
	
	        componentRolesIsEncrypted.setValue( 
	            this.isComponentRolesEncrypted()
	            );
	        
	        componentRolesConfig.addChild( componentRolesLocation );
	        componentRolesConfig.addChild( componentRolesIsEncrypted );
	        
	        result.addChild( componentRolesConfig );
	            
	        // add the following fragement
	        //
	        // <componentConfiguration>
	        //  <location>../conf/componentRoles.xml</location>
	        //  <isEncrypted>true</isEncrypted>
	        // </componentConfiguration>
	        
	        DefaultConfiguration componentConfigurationConfig = new DefaultConfiguration(
	            ServiceConstants.COMPONENT_CONFIG_KEY
	            );
	           
	        DefaultConfiguration componentConfigurationLocation = new DefaultConfiguration(
	            ServiceConstants.COMPONENT_LOCATION_KEY
	            );
	
	        componentConfigurationLocation.setValue( 
	            this.getComponentConfigurationLocation()
	            );
	
	        DefaultConfiguration componentConfigurationIsEncrypted = new DefaultConfiguration(
	            ServiceConstants.COMPONENT_ISENCRYPTED_KEY
	            );
	
	        componentConfigurationIsEncrypted.setValue( 
	            this.isComponentConfigurationEncrypted()
	            );
	        
	        componentConfigurationConfig.addChild( componentConfigurationLocation );
	        componentConfigurationConfig.addChild( componentConfigurationIsEncrypted );
	        
	        result.addChild( componentConfigurationConfig );
	
	        // Add the following fragement
	        //
	        // <parameters>
	        //   <location>../conf/parameters.properties</location>    
	        //   <isEncrypted>true</isEncrypted>
	        // </parameters>
	        
	        DefaultConfiguration parameterConfigurationConfig = new DefaultConfiguration(
	            ServiceConstants.COMPONENT_PARAMETERS_KEY
	            );
	           
	        DefaultConfiguration parameterConfigurationLocation = new DefaultConfiguration(
	            ServiceConstants.COMPONENT_LOCATION_KEY
	            );
	
	        parameterConfigurationLocation.setValue( 
	            this.getParametersLocation()
	            );
	
	        DefaultConfiguration parameterConfigurationIsEncrypted = new DefaultConfiguration(
	            ServiceConstants.COMPONENT_ISENCRYPTED_KEY
	            );
	
	        parameterConfigurationIsEncrypted.setValue( 
	            this.isParametersEncrypted()
	            );
	        
	        parameterConfigurationConfig.addChild( parameterConfigurationLocation );
	        parameterConfigurationConfig.addChild( parameterConfigurationIsEncrypted );
	        
	        result.addChild( parameterConfigurationConfig );
	        
	        return result;
	    }	    
	}
	
	/////////////////////////////////////////////////////////////////////////
	// Generated Getters/Setters
	/////////////////////////////////////////////////////////////////////////

	/**
     * @return Returns the serviceContainerClazzName.
     */
	private String getServiceContainerClazzName()
    {
        return this.serviceContainerClazzName;
    }
    
    /**
     * @param serviceContainerClazzName The serviceContainerClazzName to set.
     */
    public void setServiceContainerClazzName(
        String serviceContainerClazzName)
    {
        this.serviceContainerClazzName = serviceContainerClazzName;
    }
    
    /**
     * @return Returns the componentConfigurationLocation.
     */
    private String getComponentConfigurationLocation()
    {
        return componentConfigurationLocation;
    }
    
    /**
     * @param componentConfigurationLocation The componentConfigurationLocation to set.
     */
    public void setComponentConfigurationLocation(
        String componentConfigurationLocation)
    {
        this.componentConfigurationLocation = componentConfigurationLocation;
    }
    
    /**
     * @return Returns the componentRolesLocation.
     */
    private String getComponentRolesLocation()
    {
        return componentRolesLocation;
    }
    
    /**
     * @param componentRolesLocation The componentRolesLocation to set.
     */
    public void setComponentRolesLocation(String componentRolesLocation)
    {
        this.componentRolesLocation = componentRolesLocation;
    }
    
    /**
     * @return Returns the context.
     */
    private DefaultContext getContext()
    {
        return context;
    }
    
    /**
     * @param context The context to set.
     */
    public void setContext(Context context)
    {
        if( context instanceof DefaultContext )
        {
            this.context = (DefaultContext) context;
        }
        else
        {
            this.context = new DefaultContext( context );
        }
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
    public void setComponentConfigurationEncrypted(
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
    public void setComponentRolesEncrypted(boolean isComponentRolesEncrypted)
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
    public void setParametersEncrypted(boolean isParametersEncrypted)
    {
        this.isParametersEncrypted = isParametersEncrypted;
    }
    
    /**
     * @return Returns the logger.
     */
    public Logger getLogger()
    {
        return logger;
    }
    
    /**
     * @param logger The logger to set.
     */
    public void setLogger(Logger logger)
    {
        this.logger = logger;
    }
    
    /**
     * @return Returns the parametersLocation.
     */
    private String getParametersLocation()
    {
        return parametersLocation;
    }
    
    /**
     * @param parametersLocation The parametersLocation to set.
     */
    public void setParametersLocation(String parametersLocation)
    {
        this.parametersLocation = parametersLocation;
    }
        
    /**
     * @return Returns the applicationRootDir.
     */
    private File getApplicationRootDir()
    {
        return applicationRootDir;
    }
    
    /**
     * @param applicationRootDir The applicationRootDir to set.
     */
    public void setApplicationRootDir(File applicationRootDir)
    {
        this.applicationRootDir = applicationRootDir;
    }
    
    /**
     * @return Returns the tempRootDir.
     */
    private File getTempRootDir()
    {
        return tempRootDir;
    }
    
    /**
     * @param tempRootDir The tempRootDir to set.
     */
    public void setTempRootDir(File tempRootDir)
    {
        this.tempRootDir = tempRootDir;
    }
        
    /**
     * @return Returns the classLoader.
     */
    private ClassLoader getComponentClassLoader()
    {
        return componentClassLoader;
    }
    
    /**
     * @param componentClassLoader The classLoader to set.
     */
    public void setComponentClassLoader(ClassLoader componentClassLoader)
    {
        this.componentClassLoader = componentClassLoader;
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
    public void setContainerType(String containerType)
    {
        this.containerType = containerType;
    }
        
    /**
     * @return Returns the containerConfiguration.
     */
    private Configuration getContainerConfiguration()
    {
        return containerConfiguration;
    }
    
    /**
     * @param containerConfiguration The containerConfiguration to set.
     */
    public void setContainerConfiguration(Configuration containerConfiguration)
    {
        this.containerConfiguration = containerConfiguration;
    }
    
    /**
     * Loads a containerConfiguration.
     * 
     * @param location the location of the containerConfiguration
     * @param isEncrypted is the file encrypted
     * @throws IOException loading the configuration failed
     */
    public void setContainerConfiguration( String location, boolean isEncrypted ) 
		throws IOException
    {
		Configuration result = null;
		
		InputStreamLocator locator = new InputStreamLocator( 
		    this.getApplicationRootDir(), 
		    this.getLogger() 
		    );
    	
		InputStream is = locator.locate( location );
		
		if( is != null )
		{
		    try
		    {		        
		        if( isEncrypted )
		        {
		            is = CryptoStreamFactoryImpl.getInstance().getInputStream(is); 
		        }
		
		        DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
		        result = builder.build( is );
		        this.setContainerConfiguration( result );
		    }
		    catch ( Exception e )
		    {
		        String msg = "Unable to parse the following file : " + location;
		        this.getLogger().error( msg , e );
		        throw new IOException(msg);
		    }
		}
		else
		{
		    String msg = "Unable to locate the containerConfiguration file : " + location;
		    this.getLogger().error(msg);
		    throw new IOException(msg);
		}
    }
}
