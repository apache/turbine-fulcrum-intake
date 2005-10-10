package org.apache.fulcrum.yaafi.service.shutdown;

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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;

import org.apache.avalon.framework.logger.Logger;
import org.apache.fulcrum.yaafi.framework.util.InputStreamLocator;

/**
 * Monitors a resource and checks if it has changed
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public class ShutdownEntry
{
    /** buffer size for copy() */
    private static final int BUF_SIZE = 1024;

    /** the location to monitor for changes */
    private String location;

    /** the last message digest of the location */
    private byte[] digest;

    /** the locator to load the monitored resource */
    private InputStreamLocator locator;

    /** keep a notice for the very first invocation */
    private boolean isFirstInvocation;

    /** the logger to be used */
    private Logger logger;

    /** use System.exit() to shutdown the JVM */
    private boolean useSystemExit;

    /**
     * Constructor
     *
     * @param logger the logger to use
     * @param applicationDir the home directory of the application
     * @param location the location to monitor for changes
     * @param useSystemExit use System.exit() on shutdown
     */
    public ShutdownEntry( Logger logger, File applicationDir, String location, boolean useSystemExit )
    {
        this.isFirstInvocation = true;
        this.useSystemExit = useSystemExit;
        this.location = location;
        this.locator  = new InputStreamLocator( applicationDir );
        this.logger = logger;
    }

    /**
     * Has the monitored location changed?
     */
    public boolean hasChanged()
    {
        boolean result = false;
        InputStream is = null;
        byte[] currDigest = null;

        try
        {
            // get a grip on our resource

            is = this.locate();

            if( is == null )
            {
                String msg = "Unable to find the following resource : " + this.getLocation();
                this.getLogger().warn(msg);
            }
            else
            {
                // calculate a SHA-1 digest

                currDigest = this.getDigest(is);
                is.close();
                is = null;

                if( this.isFirstInvocation() == true )
                {
                    isFirstInvocation = false;
                    this.getLogger().debug( "Storing SHA-1 digest of " + this.getLocation() );
                    this.setDigest( currDigest );
                }
                else
                {
                    if( equals( this.digest, currDigest ) == false )
                    {
                        this.getLogger().debug( "The following resource has changed : " + this.getLocation() );
                        this.setDigest( currDigest );
                        result = true;
                    }
                }
            }

            return result;
        }
        catch(Exception e)
        {
            String msg = "The ShutdownService encountered an internal error";
            this.getLogger().error(msg,e);
            return false;
        }
        finally
        {
            if( is != null )
            {
                try
                {
                    is.close();
                }
                catch (Exception e)
                {
                    String msg = "Can't close the InputStream during error recovery";
                    this.getLogger().error(msg,e);
                }
            }
        }

    }

    /**
     * @return Returns the useSystemExit.
     */
    public boolean isUseSystemExit()
    {
        return useSystemExit;
    }

    /**
     * @return Returns the isFirstInvocation.
     */
    private boolean isFirstInvocation()
    {
        return isFirstInvocation;
    }

    /**
     * @return Returns the location.
     */
    private String getLocation()
    {
        return location;
    }

    /**
     * @return Returns the locator.
     */
    private InputStreamLocator getLocator()
    {
        return locator;
    }

    /**
     * Creates an InputStream
     */
    public InputStream locate() throws IOException
    {
        return this.getLocator().locate(this.getLocation());
    }

    /**
     * Creates a message digest
     */
    private byte[] getDigest( InputStream is )
        throws Exception
    {
        byte[] result = null;
        byte[] content = null;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        copy( is, baos );
        content = baos.toByteArray();
        baos.close();

        MessageDigest sha1 = MessageDigest.getInstance( "SHA1" );
        sha1.update( content );
        result = sha1.digest();

        return result;
    }

    /**
     * @param digest The digest to set.
     */
    private void setDigest(byte [] digest)
    {
        this.digest = digest;
    }

    /**
     * Compares two byte[] for equality
     */
    private static boolean equals(byte[] lhs, byte[] rhs)
    {
        if( lhs == rhs )
        {
            return true;
        }
        else if( lhs.length != rhs.length )
        {
            return false;
        }
        else
        {
            for( int i=0; i<lhs.length; i++ )
            {
                if( lhs[i] != rhs[i] )
                {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Pumps the input stream to the output stream.
     *
     * @param is the source input stream
     * @param os the target output stream
     * @throws IOException the copying failed
     */
    private static void copy( InputStream is, OutputStream os )
        throws IOException
    {
        byte[] buf = new byte[BUF_SIZE];
        int n = 0;
        int total = 0;

        while ((n = is.read(buf)) > 0)
        {
            os.write(buf, 0, n);
            total += n;
        }

        is.close();

        os.flush();
        os.close();
    }

    /**
     * @return Returns the logger.
     */
    private Logger getLogger()
    {
        return logger;
    }
}