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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.fulcrum.resourcemanager.ResourceManager;

/**
 * Base class for a service implementation capturing the Avalon
 * configuration artifats
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public abstract class BaseResourceManager
	extends AbstractLogEnabled
    implements Contextualizable, Serviceable, Configurable,  Initializable, Disposable, ResourceManager
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
        
        // extract the domain name
        
        this.setDomain( configuration.getAttribute("name") );
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
        this.tempDir = null;
        this.context = null;
        this.serviceManager = null;
        this.configuration = null;
    }
    
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
}
