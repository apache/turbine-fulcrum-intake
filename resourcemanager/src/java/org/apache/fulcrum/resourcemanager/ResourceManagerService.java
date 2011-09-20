package org.apache.fulcrum.resourcemanager;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.IOException;
import java.net.URL;

/**
 * An Avalon service to manage resources based on a domain. The service
 * supports multiple domains and uses a context to locate a resource.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */
public interface ResourceManagerService
{
    /////////////////////////////////////////////////////////////////////////
    // CRUD Functionality
    /////////////////////////////////////////////////////////////////////////

    /**
     * List all available domains.
     *
     * @return list of all available domains
     */
    String[] listDomains();

    /**
     * List all available resources recursively for a given domain.
     *
     * @param domain the domain you are interested in
     * @return list of all available resources for the domain
     */
    String[] listResources( String domain );

    /**
     * Does the domain exists?
     *
     * @param domain the domain you are interested in
     * @return true if the domain exists
     */
    boolean exists( String domain );

    /**
     * Does the resource exits for the given domain?
     *
     * @param domain the domain you are interested in
     * @param resourcePath the path of the resource
     * @return true if the resource exists
     */
    boolean exists( String domain, String resourcePath );

    /**
     * Saves a resource.
     *
     * @param domain the domain you are interested in
     * @param resourcePath the path of the resource
     * @param resourceContent the content of the resource
     * @throws IOException unable to create the resource
     */
    void create( String domain, String resourcePath, Object resourceContent )
        throws IOException;

    /**
     * Loads a resource.
     *
     * @param domain the domain you are interested in
     * @param resourcePath the path of the resource
     * @return the loaded resource
     * @throws IOException unable to save the resource
     */
    byte[] read( String domain, String resourcePath )
        throws IOException;

    /**
     * Updates a given resource
     *
     * @param domain the domain you are interested in
     * @param resourcePath the path of the resource
     * @param resourceContent the content of the resource
     * @throws IOException unable to update the resource
     */
    void update( String domain, String resourcePath, Object resourceContent )
        throws IOException;

    /**
     * Delete the given resource
     *
     * @param domain the domain you are interested in
     * @param resourcePath the path of the resource
     * @return true if the resource was deleted
     * @throws IOException unable to delete the resource
     */
    boolean delete( String domain, String resourcePath )
        throws IOException;

    /////////////////////////////////////////////////////////////////////////
    // Locator Functionality
    /////////////////////////////////////////////////////////////////////////

    /**
     * Does the resource exits for the given domain?
     *
     * @param domain the domain you are interested in
     * @param context the context to locate the resource
     * @param resourceName the name of the resource
     * @return true if the resource exists
     */
    boolean exists( String domain, String[] context, String resourceName );

    /**
     * Get the resource name.
     *
     * @param domain the domain you are interested in
     * @param context the context to locate the resource
     * @param resourceName the name of the resource
     * @return the name of the resource
     */
    String locate( String domain, String[] context, String resourceName );

    /**
     * Loads a resource.
     *
     * @param domain the domain you are interested in
     * @param context the context to locate the resource
     * @param resourceName the name of the resource
     * @return the loaded resource
     * @throws IOException unable to load the resource
     */
    byte[] read( String domain, String[] context, String resourceName )
        throws IOException;

    /**
     * Get the implementation specific URL of the underlying resource.
     *
     * @param domain the domain you are interested in
     * @param context the context to locate the resource
     * @param resourceName the name of the resource
     * @return the resource URL or null
     */
    URL getResourceURL( String domain, String[] context, String resourceName );
}
