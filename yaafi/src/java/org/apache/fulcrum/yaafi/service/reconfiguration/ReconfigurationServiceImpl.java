package org.apache.fulcrum.yaafi.service.reconfiguration;

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

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.activity.Suspendable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.configuration.Reconfigurable;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.fulcrum.yaafi.framework.util.InputStreamLocator;


/**
 * Monitors the componentConfiguration.xml and triggers a reconfiguration
 * if the content of the component configuration file  has changed.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public class ReconfigurationServiceImpl
    extends AbstractLogEnabled
    implements ReconfigurationService, Serviceable, Contextualizable, Configurable, Initializable, Runnable, Startable, Disposable
{    
    /** the interval between two checks in ms */
    private int interval;
    
    /** the location of the componentConfiguration file */
    private String location;
        
    /** helper for locating the component configuration */
    private InputStreamLocator locator;
    
    /** shall the worker thread terminate immediately */
    private boolean terminateNow;
    
    /** the worker thread polling the componentConfiguraton */
    private Thread workerThread;
    
    /** the ServiceManager to use */
    private ServiceManager serviceManager;
    
    /////////////////////////////////////////////////////////////////////////
    // Avalon Service Lifecycle Implementation
    /////////////////////////////////////////////////////////////////////////

    /**
     * Constructor
     */
    public ReconfigurationServiceImpl()
    {
        // nothing to do
    }

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager manager) throws ServiceException
    {
        this.serviceManager = manager;
    }
    
    /**
     * @see org.apache.avalon.framework.context.Contextualizable#contextualize(org.apache.avalon.framework.context.Context)
     */
    public void contextualize(Context context) throws ContextException
    {
        this.locator  = new InputStreamLocator( (File) context.get("urn:avalon:home") );
    }
    
    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration configuration) throws ConfigurationException
    {
        this.interval = Math.max( configuration.getAttributeAsInteger("interval",5000), 1000 );
        this.location = configuration.getChild("location").getValue();
    }
    
    /**
     * @see org.apache.avalon.framework.activity.Initializable#initialize()
     */
    public void initialize() throws Exception
    {
        // ensure that we actually find our target
        
        if( this.locate() == null )
        {
            String msg = "The component configuration was not found : " + this.location;
            this.getLogger().error(msg);
            throw new IllegalArgumentException(msg);
        }
        else
        {
            String msg = "Checking " + this.location + " every " + this.interval + " ms";
            this.getLogger().debug( msg );            
        }
        
        // request a SHA-1 to make sure that it is supported
        
        MessageDigest.getInstance( "SHA1" );
                
        // check that the ServiceManager inplements Reconfigurable
        
        if( (this.serviceManager instanceof Reconfigurable) == false )
        {
            String msg = "The ServiceManager instance does not implement Reconfigurable?!";
            throw new IllegalArgumentException( msg );
        }
        
        // create the worker thread polling the target
        
        this.workerThread = new Thread( this, "ReconfigurationService" );
    }

    /**
     * @see org.apache.avalon.framework.activity.Startable#start()
     */
    public void start() throws Exception
    {
        this.workerThread.start();
    }
    
    /**
     * @see org.apache.avalon.framework.activity.Startable#stop()
     */
    public void stop() throws Exception
    {
        this.terminateNow = true;
        this.workerThread.interrupt();
        this.workerThread.join( 10000 );
    }
    
    /**
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     */
    public void dispose()
    {
        this.locator = null;
    	this.workerThread = null;
    	this.serviceManager = null;
    }
    
    /////////////////////////////////////////////////////////////////////////
    // Service interface implementation
    /////////////////////////////////////////////////////////////////////////

    /**
     * @see java.lang.Runnable#run()
     */
    public void run()
    {
        byte[] lastDigest = null;
        byte[] currDigest = null;
        InputStream is = null;
        boolean isFirstInvocation = true;
        
        while( this.terminateNow == false )
        {
            try
            {
                // get a grip on our file 
                
                is = this.locate();

                if( is == null )
                {
                    String msg = "Unable to find the component configuration";
                    this.getLogger().warn(msg);
                    continue;
                }
                
                // calculate a SHA-1 digest
                
                currDigest = this.getDigest(is);
                is.close();
                is = null;
             
                if( isFirstInvocation == true )
                { 
                    isFirstInvocation = false;
                    this.getLogger().debug( "Storing SHA-1 digest of componentConfiguration" );
                    lastDigest = currDigest;
                }
                else
                {
                    this.getLogger().debug( "Checking the componentConfiguration to detect changes ..." );    
	                if( equals( lastDigest, currDigest ) == false )
	                {
	                    this.getLogger().debug( "The componentConfiguration has changed" );
	                    lastDigest = currDigest;
	                    this.reconfigure();
	                }
	                Thread.sleep( this.interval );
                }
            }
            catch( InterruptedException e )
            {
                continue;
            }
            catch(Exception e)
            {
                String msg = "The ReconfigurationService had a problem";
                this.getLogger().error(msg,e);
                continue;
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
        
        return;
    }    
    
    /////////////////////////////////////////////////////////////////////////
    // Service implementation
    /////////////////////////////////////////////////////////////////////////

    private void reconfigure() throws Exception
    {
        InputStream is = this.locate();
        DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
        Configuration configuration = builder.build(is);
        is.close();
        is = null;
      
        this.getLogger().warn( "Starting to reconfigure the container" );
        
        if( this.serviceManager instanceof Suspendable)
        {
            this.getLogger().info( "Calling suspend() of the container" );
            ((Suspendable) this.serviceManager).suspend();
        }

        if( this.serviceManager instanceof Reconfigurable)
        {
            this.getLogger().info( "Calling reconfigure() of the container" );
            ((Reconfigurable) this.serviceManager).reconfigure(configuration);
        }

        if( this.serviceManager instanceof Suspendable)
        {
            this.getLogger().info( "Calling resume() of the container" );
            ((Suspendable) this.serviceManager).resume();
        }
        
        this.getLogger().info( "Reconfiguring the container was successful" );
    }
    
    /**
     * Creates an InputStream  
     */
    private InputStream locate() throws IOException
    {
        return this.locator.locate(this.location);
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
        byte[] buf = new byte[1024];
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
}
