package org.apache.fulcrum.yaafi.framework.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.logger.NullLogger;

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
     */
    public InputStreamLocator()
    {
        this.rootDir = new File( new File("").getAbsolutePath() );
        this.logger = new NullLogger();
    }    

    /**
     * Constructor
     * 
     * @param rootDir the root directory to start the search     */
    public InputStreamLocator( File rootDir )
    {
        this( rootDir, new NullLogger() );
    }    

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
	 * Locate the file with the given position.
	 * 
	 * @param location the location of the source to be loaded
	 */
	public InputStream locate( String location ) throws IOException
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