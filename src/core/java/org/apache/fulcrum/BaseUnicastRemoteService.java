package org.apache.fulcrum;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import org.apache.log4j.Category;
import org.apache.velocity.runtime.configuration.Configuration;

/**
 * A base implementation of an {@link java.rmi.server.UnicastRemoteObject}
 * as a Turbine {@link org.apache.fulcrum.Service}.
 *
 * @author <a href="mailto:dlr@collab.net">Daniel Rall</a>
 */
public class BaseUnicastRemoteService 
    extends UnicastRemoteObject
    implements Service
{
    protected Configuration configuration;
    private boolean isInitialized;
    private String name;
    private ServiceBroker serviceBroker;

    public BaseUnicastRemoteService()
        throws RemoteException
    {
        isInitialized = false;
        name = null;
        serviceBroker = null;
    }

    /**
     * Returns the configuration of this service.
     *
     * @return The configuration of this service.
     */
    public Configuration getConfiguration()
    {
        if (name == null)
        {
            return null;
        }
        else
        {
            if (configuration == null)
            {
                configuration = getServiceBroker().getConfiguration(name);
            }
            return configuration;
        }
    }

    public void init() 
        throws InitializationException
    {
        setInit(true);
    }

    protected void setInit(boolean value)
    {
        isInitialized = value;
    }
    
    public boolean getInit()
    {
        return isInitialized;
    }

    /**
     * Shuts down this service.
     */
    public void shutdown()
    {
        setInit(false);
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String setName()
    {
        return name;
    }

    public ServiceBroker getServiceBroker()
    {
        return serviceBroker;
    }

    public void setServiceBroker(ServiceBroker broker)
    {
        this.serviceBroker = broker;
    }

    public String getRealPath(String path)
    {
        return null;
    }        

    public Category getCategory()
    {
        return null;
    }        

    public Category getCategory(String name)
    {
        return null;
    }
}
