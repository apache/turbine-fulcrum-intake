package org.apache.fulcrum.resourcemanager.impl;

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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;


/**
 * Concrete implementation of a file-based resource service. The current 
 * implementation caches the location of the available resources. The context
 * and resourceName is used to build a relative filename.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */
public class FileResourceManager
	extends BaseResourceManager
{
    /** an optinal suffix to filter resources */
    private String suffix;
    
    /** try to locate a resource automagically? */
    private boolean useLocator = true;

    /** the location where all resources are located */
    private File resourceDir;

    /** the cached list of all available resources */
    private String[] resourceFileNameList;

    /////////////////////////////////////////////////////////////////////////
    // Avalon Service Lifecycle Implementation
    /////////////////////////////////////////////////////////////////////////

    /**
     * Constructor 
     */
    public FileResourceManager()
    { 
        super();
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration cfg) throws ConfigurationException
    {
        super.configure( cfg );
        
        // the optional suffix - "*" is the usual wildcard suffix
        
        this.suffix = cfg.getChild(CONFIG_KEY_SUFFIX).getValue("*");
        
        // try to locate a resources automagically  ?

        this.useLocator = cfg.getChild(CONFIG_KEY_USELOCATOR).getValueAsBoolean(false);

        // locate the directory where we the resources are located

        String currLocationName = cfg.getChild(CONFIG_KEY_LOCATION).getValue();
        File currLocation = new File( currLocationName );

        if( currLocation.isAbsolute() )
        {
            this.resourceDir = currLocation;
        }
        else
        {
            this.resourceDir = new File( this.getApplicationDir(), currLocationName );
        }

        if( this.resourceDir.exists() )
        {
            this.getLogger().debug( 
                "Using the resource directory : " + this.resourceDir.getAbsolutePath() 
                );            
        }
        else
        {
            String msg = "The following resource directory is not found : " 
                + this.resourceDir.getAbsolutePath();
            
            throw new ConfigurationException( msg );
        }
        

        // load the file names of all resources and sort it

        this.createResourceFileNameList();
    }

    /**
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     */
    public void dispose()
    {
        super.dispose();
        
        this.suffix = null;
        this.useLocator = false;
        this.resourceDir = null;
        this.resourceFileNameList = null;
    }

    /////////////////////////////////////////////////////////////////////////
    // Service interface implementation
    /////////////////////////////////////////////////////////////////////////
    
    /**
     * @see org.apache.fulcrum.resourcemanager.ResourceManager#exists(java.lang.String)
     */
    public boolean exists(String resourceName)
    {
        File resourceFile = this.findResourceFile( resourceName, this.resourceFileNameList );
        
    	if( resourceFile == null )
    	{
    	    return false;
    	}
    	else
    	{
    	    return true;
    	}
	}

    /**
     * @see org.apache.fulcrum.resourcemanager.ResourceManager#listResources()
     */
    public synchronized String[] listResources()
    {
        String resourceDirName = this.getResourceDir().getAbsolutePath();
        String[] fileList = this.resourceFileNameList;
        String[] result = new String[fileList.length];

        // remove the resource directory name 

        for( int i=0; i< result.length; i++ )
        {
            String relativeName = fileList[i].substring(
                resourceDirName.length()+1,
                fileList[i].length()
                );

            result[i] = relativeName;
        }

        return result;
    }

    /**
     * @see org.apache.fulcrum.resourcemanager.ResourceManager#create(java.lang.String, java.lang.Object)
     */
    public synchronized void create(String resourcePath, Object resourceContent)
        throws IOException
    {
        File resourceFile = new File( this.getResourceDir(), resourcePath );

        this.getLogger().debug( "Creating resource : " + resourceFile.getAbsolutePath() );

        byte[] byteContent = this.getContent( resourceContent);
        FileOutputStream fos = new FileOutputStream( resourceFile );        
        fos.write(byteContent);
        fos.flush();
        fos.close();

        this.createResourceFileNameList();
    }
            
    /**
     * @see org.apache.fulcrum.resourcemanager.ResourceManager#read(java.lang.String)
     */    
    public synchronized byte[] read( String resourcePath )
        throws IOException
    {
        byte[] result = null;
        
        File resourceFile = this.findResourceFile( resourcePath, this.resourceFileNameList );

        if( resourceFile != null )
        {
            this.getLogger().debug( "Loading the resource : " + resourceFile.getAbsolutePath() );
	        FileInputStream fis = new FileInputStream(resourceFile);
	        result = new byte[fis.available()];
	        fis.read(result);
	        fis.close();
	        return result;
        }
        else
        {
            String msg = "Unable to find the resource : " + resourcePath;
            this.getLogger().error( msg );
            throw new IOException( msg );
        }
    }

    /**
     * @see org.apache.fulcrum.resourcemanager.ResourceManager#update(java.lang.String, java.lang.Object)
     */
    public synchronized void update( String resourcePath, Object resourceContent)
    	throws IOException
    {
        this.create( resourcePath, resourceContent );
    }
    
    /**
     * @see org.apache.fulcrum.resourcemanager.ResourceManager#delete(java.lang.String)
     */    
    public synchronized boolean delete( String resourcePath )
    	throws IOException
    {
        boolean result = false;
        File file = new File( this.getResourceDir(), resourcePath );

        // if a resource was deleted we have to update our resource list
        // to avoid a stale entry

        if( file.delete() == true )
        {
            this.createResourceFileNameList();
            result = true;
        }

        return result;
    }
    
    /**
     * @see org.apache.fulcrum.resourcemanager.ResourceManager#exists(, java.lang.String)
     */    
    public synchronized boolean exists( String[] context, String resourceName )
    {
        return( this.locate(context,resourceName) != null ? true : false );
    }
    
    /**
     * @see org.apache.fulcrum.resourcemanager.ResourceManager#read(java.lang.String[], java.lang.String)
     */
    public byte [] read( String[] context, String resourceName )
        throws IOException
    {
        String resourceFileName = this.createResourceFileName( context, resourceName );
        return this.read( resourceFileName );
    }
    
    /**
     * @see org.apache.fulcrum.resourcemanager.ResourceManager#locate(java.lang.String[], java.lang.String)
     */
    public String locate( String[] context, String resourceName )
    {
        String result = null;
        String resourceDirName = this.getResourceDir().getAbsolutePath();
        String resourceFileName = this.createResourceFileName( context, resourceName );
        File resourceFile = this.findResourceFile( resourceFileName, this.resourceFileNameList ); 
        
        if( resourceFile != null )
        {
            String temp = resourceFile.getAbsolutePath();
       
	        result = temp.substring(
	            resourceDirName.length()+1,
	            temp.length()
	            );

	        result = result.replace( '\\', '/' );
        }
        
        return result;        
    }
    
    /**
     * @see org.apache.fulcrum.resourcemanager.ResourceManager#getResourceURL(java.lang.String[], java.lang.String)
     */
    public URL getResourceURL(String [] context, String resourceName)
    {
        String resourceFileName = this.createResourceFileName( context, resourceName );
        File resourceFile = this.findResourceFile( resourceFileName, this.resourceFileNameList );

        if( resourceFile != null )
        {
            try
            {
                return resourceFile.toURL();
            }
            catch( MalformedURLException e )
            {
                throw new RuntimeException( e.getMessage() );
            }
        }
        else
        {
            return null;
        }
    }
    
    /////////////////////////////////////////////////////////////////////////
    // Service Implementation
    /////////////////////////////////////////////////////////////////////////

    /**
     * Creates a sorted list of resource file names using the user-supplied 
     * suffix.
     */
    private void createResourceFileNameList()
    {
        ArrayList resourceList = new ArrayList();
        this.findAllResources( this.getResourceDir(), this.suffix, resourceList );
        this.resourceFileNameList = (String[]) resourceList.toArray( new String[resourceList.size()] );
        Arrays.sort( this.resourceFileNameList );
    }

    /**
     * @return Returns the useLocator.
     */
    private boolean isUseLocator()
    {
        return useLocator;
    }

    /**
     * @return Returns the resource directory.
     */
    private File getResourceDir()
    {
        return this.resourceDir;
    }

    /**
     * Finds the resource file for the given name.
     * @param resourceName the script name
     * @param resourceList the list of available resources
     * @return the resource file or <b>null</b> if it wasn't found
     */
    private File findResourceFile( String resourceName, String[] resourceList )
    {
        File result = null;
        String tempFileName = null;
        String resourceFileName = new File( this.getResourceDir(), resourceName).getAbsolutePath();

        boolean wasFound = ( Arrays.binarySearch( resourceList, resourceFileName ) >= 0 ? true : false );

        if( wasFound == true  )
        {
            result = new File( resourceFileName );
        }
        else if( this.isUseLocator() )
        {
            // create a String[] with the directories contained in the resourceName
            // e.g. [0]=foor [1]=bar resourceName=empty.groovy

            String[] parts = StringUtils.split( resourceName, "/\\", -1 );
            String[] context = new String[parts.length-1];
            String scriptBaseName = parts[parts.length-1];

            for( int i=0; i<context.length; i++ )
            {
                context[i] = parts[i];
            }

            // create a list of files stepping up the directories
            // [0]=./foo/bar/empty.groovy
            // [1]=./foo/empty.groovy
            // [2] ./empty.groovy

            String baseFileName = null;
            File[] fileList = new File[context.length+1];
            fileList[0] = new File( this.getResourceDir(), scriptBaseName );

            for(int i=1; i<context.length+1; i++ )
            {
                if( i == 1 )
                {
                    baseFileName = context[i-1];
                }
                else
                {
                    baseFileName = baseFileName + File.separator + context[i-1];
                }

                tempFileName = baseFileName + File.separator + scriptBaseName;

                fileList[i] = new File( this.getResourceDir(), tempFileName );
            }

            // search for the resource using the generated file list

            for( int i=fileList.length; i>0; i-- )
            {
                if( this.getLogger().isDebugEnabled() )
                {
	                this.getLogger().debug(
	                    "Searching for the following file : "
	                    + fileList[i-1].getAbsolutePath()
	                    );
                }
                
                if( Arrays.binarySearch( resourceList, fileList[i-1].getAbsolutePath() ) >= 0 )
                {
                    result = fileList[i-1];
                    break;
                }
            }
        }
        else
        {
            result = null;
        }

        return result;
    }

    /**
     * Find all resources recursively.
     * @param startDir the start directory of the search
     * @param suffix an optional suffix to filter the result
     * @param result list of all Grovvy scripts
     */
    private void findAllResources( File startDir, String suffix, ArrayList result )
    {
        if( startDir.isDirectory() && startDir.canRead() )
        {
            File[] list = startDir.listFiles();

            for( int i=0; i<list.length; i++ )
            {
                if( list[i].isDirectory() )
                {
                    this.findAllResources( list[i], suffix, result );
                }
                else
                {
                    if( suffix.equals("*") == false )
                    {
	                    if( list[i].getName().endsWith(suffix) )
	                    {
	                        result.add( list[i].getAbsolutePath() );
	                    }
                    }
                    else
                    {
                        result.add( list[i].getAbsolutePath() );
                    }
                }
            }
        }
    }

    /**
     * Build a file name using the context and resoure name.
     * @param context the context to locate the resource
     * @param resourceName the name of the resource
     * @return a file name
     */
    private String createResourceFileName( String[] context, String resourceName )
    {
        StringBuffer result = new StringBuffer();
                
        if( ( context != null ) && ( context.length > 0 ) )
        {
            for( int i=0; i<context.length; i++ )
            {
                if( context[i] != null )
                {
	                result.append( context[i] );
	                result.append( File.separator  );
                }
                else
                {
                    String msg = "Don't know how to handle <null> in the context";
                    throw new IllegalArgumentException( msg );
                }
            }
        }
        
        result.append( resourceName );
        
        return result.toString();
    }
}