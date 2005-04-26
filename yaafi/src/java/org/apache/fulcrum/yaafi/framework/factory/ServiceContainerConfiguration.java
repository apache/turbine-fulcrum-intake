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
import org.apache.fulcrum.yaafi.framework.constant.AvalonMerlinConstants;
import org.apache.fulcrum.yaafi.framework.container.ServiceConstants;
import org.apache.fulcrum.yaafi.framework.util.InputStreamLocator;
import org.apache.fulcrum.yaafi.framework.util.Validate;

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
    private String isComponentRolesEncrypted;
    
    /** the location of the component configuration file */
    private String componentConfigurationLocation;

    /** is the component configuration file encrypted? */
    private String isComponentConfigurationEncrypted;

    /** the location of the paramaters file */
    private String parametersLocation;

    /** is the parameters file encrypted? */
    private String isParametersEncrypted;
    
    /** the user-supplied Avalon context */
    private DefaultContext context;
    
    /** the Avalon */
    private Logger logger;
    
    /** the application directory */
    private String applicationRootDir;
    
    /** the temporary directory */
    private String tempRootDir;
    
    /** the class loader passed in the Avalon Context */
    private ClassLoader componentClassLoader;
    
    /** the type of container where YAAFI is embedded */
    private String containerFlavour;
    
    /** the caller-supplied container configuration */
    private Configuration containerConfiguration;    
    
    /** Constructor */    
    public ServiceContainerConfiguration()
    {
        this.logger = new ConsoleLogger();
        this.containerFlavour = ServiceConstants.AVALON_CONTAINER_YAAFI;
        this.serviceContainerClazzName = ServiceConstants.CLAZZ_NAME;
        this.componentRolesLocation = ServiceConstants.COMPONENT_ROLE_VALUE;
        this.isComponentRolesEncrypted = "false";
        this.componentConfigurationLocation = ServiceConstants.COMPONENT_CONFIG_VALUE;
        this.isComponentConfigurationEncrypted = "false";
        this.parametersLocation = ServiceConstants.COMPONENT_PARAMETERS_VALUE;
        this.isParametersEncrypted = "false";        
        this.context = new DefaultContext();
        this.applicationRootDir = new File("").getAbsolutePath();
        this.tempRootDir = System.getProperty("java.io.tmpdir",".");      
        this.componentClassLoader = this.getClass().getClassLoader();        
    }
    
	/**
	 * Add a new entry to the context by creating a new one.
	 * @param name the name of the new entry
	 * @param value the value of the new entry
	 */
	public void addToContext( String name, Object value )
	{
        Validate.notEmpty(name,"name");
        Validate.notNull(value,"value");
	    this.getContext().put( name, value );
	}    

	/**
	 * Add a hashtable to the context 
	 * @param hashtable the Hashtable to be added
	 */
	public void addToContext( Hashtable hashtable )
	{
	    Validate.notNull(hashtable,"hashtable");
	    
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
	        // <containerFlavour>merlin</containerFlavour>
	        
	        DefaultConfiguration containerFlavourConfig = new DefaultConfiguration(
	            ServiceConstants.CONTAINERFLAVOUR_CONFIG_KEY
	            );
	        
	        containerFlavourConfig.setValue( this.getContainerFlavour() );
	        
	        result.addChild( containerFlavourConfig );
	
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
        Validate.notNull(componentConfigurationLocation,"componentConfigurationLocation");
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
    private String isComponentConfigurationEncrypted()
    {
        return isComponentConfigurationEncrypted;
    }
    
    /**
     * @param isComponentConfigurationEncrypted The isComponentConfigurationEncrypted to set.
     */
    public void setComponentConfigurationEncrypted(
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
    public void setComponentRolesEncrypted(String isComponentRolesEncrypted)
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
    public void setParametersEncrypted(String isParametersEncrypted)
    {
        Validate.notEmpty(isParametersEncrypted,"isParametersEncrypted");
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
        return new File(applicationRootDir);
    }
    
    /**
     * @param applicationRootDir The applicationRootDir to set.
     */
    public void setApplicationRootDir(String applicationRootDir)
    {
        Validate.notNull(applicationRootDir, "applicationRootDir");
        
        if( applicationRootDir.equals(".") )
        {
            this.applicationRootDir = new File("").getAbsolutePath();
        }
        else
        {
            this.applicationRootDir = new File( applicationRootDir ).getAbsolutePath();
        }
    }
    
    /**
     * @return Returns the tempRootDir.
     */
    private File getTempRootDir()
    {
        return makeAbsoluteFile(this.getApplicationRootDir(),this.tempRootDir);
    }
    
    /**
     * @param tempRootDir The tempRootDir to set.
     */
    public void setTempRootDir(String tempRootDir)
    {
        Validate.notNull(tempRootDir, "tempRootDir");
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
        Validate.notNull(componentClassLoader, "componentClassLoader");
        this.componentClassLoader = componentClassLoader;
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
    public void setContainerFlavour(String containerFlavour)
    {
        this.containerFlavour = containerFlavour;
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
     * Loads a containerConfiguration file and set is as the Avalon
     * configuration to be used for Configurable.configure(). Take
     * care that the implementation uses an InputStreamLocator to
     * find the containerConfiguration which uses the previously
     * set application root directory.
     * 
     * @param location the location of the containerConfiguration
     * @throws IOException loading the configuration failed
     */
    public void loadContainerConfiguration( String location )
    	throws IOException
    {
        this.loadContainerConfiguration( location, "false" );
    }

    /**
     * Loads a containerConfiguration file and set is as the Avalon
     * configuration to be used for Configurable.configure(). Take
     * care that the implementation uses an InputStreamLocator to
     * find the containerConfiguration which uses the previously
     * set application root directory.
     * 
     * @param location the location of the containerConfiguration
     * @param isEncrypted is the file encrypted
     * @throws IOException loading the configuration failed
     */
    public void loadContainerConfiguration( String location, String isEncrypted ) 
		throws IOException
    {
		Configuration result = null;
		
		InputStreamLocator locator = new InputStreamLocator( 
		    this.getApplicationRootDir(), 
		    this.getLogger() 
		    );
    	
		DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
		InputStream is = locator.locate( location );
		InputStream cis = is;
		
		if( is != null )
		{
		    try
		    {		        
		        if( isEncrypted.equalsIgnoreCase("true") )
		        {
		            cis = CryptoStreamFactoryImpl.getInstance().getInputStream(is); 
		            result = builder.build( cis );
		            cis.close();
		        }
		        if( isEncrypted.equalsIgnoreCase("auto") )
		        {
		            cis = CryptoStreamFactoryImpl.getInstance().getSmartInputStream(is); 
		            result = builder.build( cis );
		            cis.close();
		        }
		        else
		        {
		            result = builder.build( is );
		            is.close();
		        }		        
				        
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
    
    /**
     * Determines the absolute file.
     * @param baseDir the base directory 
     * @param fileName the filename 
     * @return the absolute path
     */
    private static File makeAbsoluteFile( File baseDir, String fileName )
    {
        File result = new File(fileName);       
        
        if( result.isAbsolute() == false )
        {
            result = new File( baseDir, fileName ); 
        }
        
        return result;
    }
}
