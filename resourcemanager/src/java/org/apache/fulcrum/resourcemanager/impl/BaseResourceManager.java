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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.Reconfigurable;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.fulcrum.resourcemanager.ResourceManager;
import org.apache.fulcrum.pbe.PBEService;

/**
 * Base class for a service implementation capturing the Avalon
 * configuration artifats
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public abstract class BaseResourceManager
	extends AbstractLogEnabled
    implements Contextualizable, Serviceable, Configurable,  
    	Initializable, Disposable, Reconfigurable, ResourceManager
{
    /** The context supplied by the avalon framework */
    private Context context;
    
    /** The service manager supplied by the avalon framework */
    private ServiceManager serviceManager;
    
    /** The configuraton supplied by the avalon framework */
    private Configuration configuration;
    
    /** the Avalon application directory */
    private File applicationDir;

    /** the Avalon temp directory */
    private File tempDir;
    
    /** the name of the domain */
    private String domain;

    /** the seed to generate the password */
    private String seed;
    
    /** use transparent encryption/decryption */
    private String useEncryption;   
    
    /////////////////////////////////////////////////////////////////////////
    // Avalon Service Lifecycle Implementation
    /////////////////////////////////////////////////////////////////////////
    
    /**
     * Constructor
     */
    public BaseResourceManager()
    {
        // nothing to do
    }
    
    /**
     * @see org.apache.avalon.framework.context.Contextualizable#contextualize(org.apache.avalon.framework.context.Context)
     */
    public void contextualize(Context context) throws ContextException
    {
        this.context        = context;
        this.applicationDir = (File) context.get("urn:avalon:home");
        this.tempDir        = (File) context.get("urn:avalon:temp");
    }

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager serviceManager) throws ServiceException
    {
        this.serviceManager = serviceManager;
    }
    
    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration configuration) throws ConfigurationException
    {
        this.configuration = configuration;
        this.setDomain( configuration.getAttribute("name") );
        this.seed = "resourcemanager";
    }
    
    /**
     * @see org.apache.avalon.framework.activity.Initializable#initialize()
     */
    public void initialize() throws Exception
    {
        // nothing to do
    }
        
    /**
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     */
    public void dispose()
    {
        this.applicationDir = null;
        this.configuration = null;
        this.context = null;
        this.domain = null;
        this.seed = null;
        this.serviceManager = null;
        this.tempDir = null;        
    }

    /**
     * @see org.apache.avalon.framework.configuration.Reconfigurable#reconfigure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void reconfigure(Configuration configuration)
        throws ConfigurationException
    {
        this.configure(configuration);
    }
    
    /////////////////////////////////////////////////////////////////////////
    // Service Implementation
    /////////////////////////////////////////////////////////////////////////

    /**
     * @return Returns the configuration.
     */
    protected Configuration getConfiguration()
    {
        return this.configuration;
    }
    
    /**
     * @return Returns the context.
     */
    protected Context getContext()
    {
        return this.context;
    }
            
    /**
     * @return Returns the serviceManager.
     */
    protected ServiceManager getServiceManager()
    {
        return this.serviceManager;
    }
    
    /**
     * @return Returns the applicationDir.
     */
    public File getApplicationDir()
    {
        return applicationDir;
    }
    
    /**
     * @return Returns the tempDir.
     */
    public File getTempDir()
    {
        return tempDir;
    }
    
    /**
     * @return Returns the domain.
     */
    public String getDomain()
    {
        return domain;
    }
    
    /**
     * Get the content as byte[]
     */
    protected byte[] getContent( Object content )
    	throws IOException
    {
        byte[] result = null;

        if( content instanceof String )
        {
            result = ((String) content).getBytes();
        }
        else if( content instanceof byte[] )
        {
            result = (byte[]) content;
        }
        else if( content instanceof InputStream )
        {
            result = this.getBytes( (InputStream) content );
        }
        else if( content instanceof Properties )
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();            
            ((Properties) content).store( baos, "Created by fulcrum-resourcemanager-service" );
            result = baos.toByteArray();
        }        
        else
        {
            String msg = "Don't know how to read " + content.getClass().getName();
            throw new IllegalArgumentException( msg );
        }

        return result;
    }
    
    /**
     * Extract a byte[] from the input stream.
     */
    protected byte[] getBytes( InputStream is )
    	throws IOException
    {
        int ch;
        byte[] data = null;

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        BufferedInputStream isReader = new BufferedInputStream( is );
        BufferedOutputStream osWriter = new BufferedOutputStream( os );

        while ((ch = isReader.read()) != -1)
        {
            osWriter.write(ch);
        }
        
        osWriter.flush();
        data = os.toByteArray();
        osWriter.close();
        isReader.close();
        
        return data;
    }
    
    /**
     * @param domain The domain to set.
     */
    protected void setDomain(String domain)
    {
        this.domain = domain;
    }
    
    /**
     * @return Returns the useEncryption.
     */
    protected String getUseEncryption()
    {
        return useEncryption;
    }
    
    /**
     * @param useEncryption The useEncryption to set.
     */
    protected void setUseEncryption(String useEncryption)
    {
        this.useEncryption = useEncryption;
    }
    
    /** 
     * @return the instance of the PBEService
     */
    protected PBEService getPBEService()
    {
        String service = PBEService.class.getName();
        PBEService result = null;
        
        if( this.getServiceManager().hasService(service) )
        {
            try
            {
                result = (PBEService) this.getServiceManager().lookup(service);
            }
            catch (ServiceException e)
            {
                String msg = "The PBEService can't be accessed";
                this.getLogger().error( msg, e );
                throw new RuntimeException( msg );
            }
        }
        else
        {
            String msg = "The PBEService is not registered";
            throw new RuntimeException( msg );
        }
        
        return result;
    }    
    
    /** 
     * @return the password for the resource manager
     */
    private char[] getPassword() throws Exception
	{
	    return this.getPBEService().createPassword( this.seed.toCharArray() );
	}
    
    /**
     * Reads the given file and decrypts it if required
     * @param source the source file
     * @return the content of the file
     */
    protected byte[] read( InputStream is )
    	throws IOException
    {
        if( this.getUseEncryption().equalsIgnoreCase("true") )
        {
            return readEncrypted( is );
        }
        else if( this.getUseEncryption().equalsIgnoreCase("auto") )
        {
            return readSmartEncrypted( is );
        }
        else
        {
            return readPlain( is );
        }
    }

    /**
     * Reads from an unencrypted input stream
     * @param is the source input stream
     * @return the content of the input stream
     */
    private byte[] readPlain( InputStream is )
    	throws IOException
    {
        byte[] result = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        this.copy(is,baos);
        result = baos.toByteArray();        
        return result;
    }

    /**
     * Reads a potentially encrypted input stream.
     * @param is the source input stream
     * @return the content of the input stream
     */
    private byte[] readSmartEncrypted( InputStream is )
    	throws IOException
    {
        byte[] result = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try
        {
            char[] password = getPassword();
            InputStream sdis = this.getPBEService().getSmartInputStream( is, password );
            this.copy( sdis, baos );
            result = baos.toByteArray();
            return result;
        }
        catch (IOException e)
        {
            String msg = "Failed to process the input stream";
            this.getLogger().error( msg, e );
            throw e;
        }
        catch (Exception e)
        {
            String msg = "Failed to decrypt the input stream";
            this.getLogger().error( msg, e );
            throw new IOException( msg );
        }
    }

    /**
     * Reads a potentially encrypted input stream.
     * @param is the source input stream
     * @return the content of the input stream
     */
    private byte[] readEncrypted( InputStream is )
    	throws IOException
    {
        byte[] result = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try
        {
            char[] password = getPassword();
            InputStream sdis = this.getPBEService().getInputStream( is, password );
            this.copy( sdis, baos );
            result = baos.toByteArray();
            return result;
        }
        catch (IOException e)
        {
            String msg = "Failed to process the input stream";
            this.getLogger().error( msg, e );
            throw e;
        }
        catch (Exception e)
        {
            String msg = "Failed to decrypt the input stream";
            this.getLogger().error( msg, e );
            throw new IOException( msg );
        }
    }

    /**
     * Write the given file and encrypts it if required
     * @param target the target file
     * @parwm content the content to be written
     * @return
     */
    protected void write( OutputStream os, byte[] content )
    	throws IOException
    {
        if( this.getUseEncryption().equalsIgnoreCase("true") )
        {
            writeEncrypted( os, content );
        }
        else if( this.getUseEncryption().equalsIgnoreCase("auto") )
        {
            writeEncrypted( os, content );
        }
        else
        {
            writePlain( os, content );
        }
    }

    /**
     * Write the given file without encryption.
     * @param target the target file
     * @parwm content the content to be written
     * @return
     */
    private void writePlain( OutputStream os, byte[] content )
    	throws IOException
    {
        ByteArrayInputStream bais = new ByteArrayInputStream(content);
        this.copy( bais, os );        
    }
    
    /**
     * Write the given file and encrypt it.
     * @param target the target file
     * @parwm content the content to be written
     * @return
     */
    private void writeEncrypted( OutputStream os, byte[] content )
    	throws IOException
    {
        try
        {
            char[] password = this.getPassword();
            ByteArrayInputStream bais = new ByteArrayInputStream(content);
            OutputStream eos = this.getPBEService().getOutputStream( os, password );
            this.copy(bais,eos);
        }
        catch (IOException e)
        {
            String msg = "Failed to process the output stream";
            this.getLogger().error( msg, e );
            throw e;
        }
        catch (Exception e)
        {
            String msg = "Failed to encrypt the input stream";
            this.getLogger().error( msg, e );
            throw new IOException( msg );
        }
    }
    
    /**
     * Pumps the input stream to the output stream.
     *
     * @param is the source input stream
     * @param os the target output stream
     * @throws IOException the copying failed
     */
    private void copy( InputStream is, OutputStream os )
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

        os.flush();
        os.close();
    }        
}
