package org.apache.fulcrum.resourcemanager;

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

import java.io.IOException;
import java.net.URL;

/**
 * The interface for a domain-based resource manager.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */
public interface ResourceManager
{
    /** Key of an optinal suffix */
    String CONFIG_KEY_SUFFIX = "suffix";

    /** Key to locate a resource automagically */
    String CONFIG_KEY_USELOCATOR = "useLocator";

    /** Key of the location of the resources */
    String CONFIG_KEY_LOCATION = "location";

    /////////////////////////////////////////////////////////////////////////
    // CRUD Functionality
    /////////////////////////////////////////////////////////////////////////
    
    /** 
     * @return the domain of this resource manager
     */
    String getDomain();
    
    /**
     * List all avaible resources recursively for a domain.
     * 
     * @return list of all available resources for the domain
     */
    String[] listResources();

    /**
     * Does the resource exits?.
     *
     * @param resourcePath the path of the resource
     * @return true if the resource exists
     */
    boolean exists( String resourcePath );
    
    /**
     * Saves a resource.
     *
     * @param resourcePath the path of the resource
     * @param resourceContent the content of the resource
     * @exception IOException accessing the resource failed 
     */
    void create( String resourcePath, Object resourceContent )
        throws IOException;

    /**
     * Loads a resource.
     *
     * @param resourcePath the path of the resource
     * @return the content of the resource
     * @exception IOException accessing the resource failed 
     */
    byte[] read(  String resourcePath )
        throws IOException;

    /**
     * Updates a existing resource.
     *
     * @param resourcePath the path of the resource
     * @param resourceContent the content of resource
     * @exception IOException accessing the resource failed 
     */
    void update( String resourcePath, Object resourceContent )
        throws IOException;

    /**
     * Delete the given resource.
     *
     * @param resourcePath the path of the resource
     * @return true if the resource was physically deleted
     * @exception IOException accessing the resource failed
     */
    boolean delete( String resourcePath )
        throws IOException;    

    /////////////////////////////////////////////////////////////////////////
    // Locator Functionality
    /////////////////////////////////////////////////////////////////////////

    /**
     * Does the resource exits?.
     *
     * @param context the context to locate the resource
     * @param resourceName the name of the resource
     * @return true if the resource exists
     */
    boolean exists( String[] context, String resourceName );

    /**
     * Get the path of the requested resource.
     *
     * @param context the context to locate the resource
     * @param resourceName the name of the resource
     * @return the name of the resource
     */
    String locate( String[] context, String resourceName );

    /**
     * Read the resource using the locator.
     *
     * @param context the context to locate the resource
     * @param resourceName the name of the resource
     * @return the content of the resource
     * @exception IOException accessing the resource failed 
     */
    byte[] read( String[] context, String resourceName )
    	throws IOException;

    /**
     * Get the implementation specific URL of the 
     * underlying resource. Be aware that this method
     * breaks our abstraction but is required lets
     * say for a XSL to include other stylesheets.
     * 
     * @param context the context to locate the resource
     * @param resourceName the name of the resource
     * @return the name of the resource or null
     */
    URL getResourceURL( String[] context, String resourceName );
}
