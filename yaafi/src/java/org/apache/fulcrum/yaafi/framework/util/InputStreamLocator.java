package org.apache.fulcrum.yaafi.framework.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.avalon.framework.logger.Logger;

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

/**
 * Helper for locating a file name and returning an input stream.
 * 
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a> 
 */

public class InputStreamLocator
{
    /** the root directory of our search */
    private File rootDir;
    
    /** the logger to be used */
    private Logger logger;
    
    /**
     * Constructor
     * 
     * @param rootDir the root directory to start the search
     * @param logger the logger to be used
     */
    public InputStreamLocator( File rootDir, Logger logger )
    {
        this.rootDir	= rootDir;
        this.logger 	= logger;
    }    
        
	/**
	 * Locate the file with the given position
	 */
	public InputStream locate( String location ) throws Exception
	{
        if( ( location == null ) || ( location.length() == 0 ) )
        {
            return null;
        }

		File file = null;
		InputStream is = null;

		// try to load a relative location with the given root dir
		// e.g. "componentRoles.xml" located in the current working directory

		if( is == null ) 
		{
			file = new File( this.rootDir, location );

			this.getLogger().debug("Looking for " + location + " in the root directory");
			
			if( file.exists() )
			{
				is = new FileInputStream( file );
			}
		}

		// try to load an absolute location as file
		// e.g. "/foo/componentRoles.xml" from the root of the file system

		if( is == null ) 
		{
			file = new File( location );

			this.getLogger().debug("Looking for " + location + " as absolute file location");
			
			if( file.isAbsolute() && file.exists() )
			{
				is = new FileInputStream( file );
			}
		}

		// try to load an absolute location through the classpath
		// e.g. "/componentRoles.xml" located in the classpath

		if( ( is == null ) && ( location.startsWith( "/" ) == true ) )
		{
		    this.getLogger().debug("Looking for " + location + " using the class loader");
			is =  getClass().getResourceAsStream( location );
		}

		if( is == null )
		{
		    this.getLogger().warn("Unable to locate " + location);
		}
		else
		{
		    this.getLogger().debug("Successfully located " + location);
		}
		
		return is;
	}
	
    /**
     * @return Returns the logger.
     */
    protected Logger getLogger()
    {
        return logger;
    }
    
    /**
     * @return Returns the rootDir.
     */
    protected File getRootDir()
    {
        return rootDir;
    }
	

}