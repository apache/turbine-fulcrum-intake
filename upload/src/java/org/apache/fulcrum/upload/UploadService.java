package org.apache.fulcrum.upload;


/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileUploadException;

/**
 * <p> This service handles parsing <code>multipart/form-data</code>
 * POST requests and turing them into form fields and uploaded files.
 * This can be either performed automatically by the {@link
 * org.apache.fulcrum.util.parser.ParameterParser} or manually by an user
 * definded {@link org.apache.turbine.modules.Action}.
 *
 * @author <a href="mailto:Rafal.Krzewski@e-point.pl">Rafal Krzewski</a>
 * @author <a href="mailto:dlr@collab.net">Daniel Rall</a>
 * @version $Id$
 */
public interface UploadService
    
{
    /** Avalon Identifier **/
    public String ROLE = UploadService.class.getName();
    
    /**
     * HTTP header.
     */
    public static final String CONTENT_TYPE = "Content-type";

    /**
     * HTTP header.
     */
    public static final String CONTENT_DISPOSITION = "Content-disposition";

    /**
     * HTTP header base type.
     */
    public static final String MULTIPART = "multipart";

    /**
     * HTTP header base type modifier.
     */
    public static final String FORM_DATA = "form-data";

    /**
     * HTTP header base type modifier.
     */
    public static final String MIXED = "mixed";

    /**
     * HTTP header.
     */
    public static final String MULTIPART_FORM_DATA =
        MULTIPART + '/' + FORM_DATA;

    /**
     * HTTP header.
     */
    public static final String MULTIPART_MIXED = MULTIPART + '/' + MIXED;

    /**
     * The key in the TurbineResources.properties that references this
     * service.
     */
    public static final String SERVICE_NAME = "UploadService";

    /**
     * The key in UploadService properties in
     * TurbineResources.properties 'automatic' property.
     */
    public static final String AUTOMATIC_KEY = "automatic";

    /**
     * <p> The default value of 'automatic' property
     * (<code>false</code>).  If set to <code>true</code>, parsing the
     * multipart request will be performed automaticaly by {@link
     * org.apache.fulcrum.util.parser.ParameterParser}.  Otherwise, an {@link
     * org.apache.turbine.modules.Action} may decide to to parse the
     * request by calling {@link #parseRequest(HttpServletRequest, String)
     * parseRequest} manually.
     */
    public static final boolean AUTOMATIC_DEFAULT = false;

    /**
     * The request parameter name for overriding 'repository' property
     * (path).
     */
    public static final String REPOSITORY_PARAMETER = "path";

    /**
     * The key in UploadService properties in
     * TurbineResources.properties 'repository' property.
     */
    public static final String REPOSITORY_KEY = "repository";

    /**
     * <p> The default value of 'repository' property (.).  This is
     * the directory where uploaded fiels will get stored temporarily.
     * Note that "."  is whatever the servlet container chooses to be
     * it's 'current directory'.
     */
    public static final String REPOSITORY_DEFAULT = ".";

    /**
     * w The key in UploadService properties in
     * service configuration 'sizeMax' property.
     */
    public static final String SIZE_MAX_KEY = "sizeMax";

    /**
     * <p> The default value of 'sizMax' property (1 megabyte =
     * 1048576 bytes).  This is the maximum size of POST request that
     * will be parsed by the uploader.  If you need to set specific
     * limits for your users, set this property to the largest limit
     * value, and use an action + no auto upload to enforce limits.
     *
     */
    public static final int SIZE_MAX_DEFAULT = 1048576;

    /**
     * The key in UploadService properties in
     * TurbineResources.properties 'sizeThreshold' property.
     */
    public static final String SIZE_THRESHOLD_KEY = "sizeThreshold";

    /**
     * <p> The default value of 'sizeThreshold' property (10
     * kilobytes = 10240 bytes).  This is the maximum size of a POST
     * request that will have it's components stored temporarily in
     * memory, instead of disk.
     */
    public static final int SIZE_THRESHOLD_DEFAULT = 10240;

    /**
     * <p>Parses a <a href="http://rf.cx/rfc1867.html">RFC 1867</a>
     * compliant <code>multipart/form-data</code> stream.</p>
     *
     * @param req The servlet request to be parsed.
     * @param path The location where the files should be stored.
     * @exception ServiceException Problems reading/parsing the
     * request or storing the uploaded file(s).
     */
    public ArrayList parseRequest(HttpServletRequest req, String path)
            throws FileUploadException;

    /**
     * <p> Retrieves the value of <code>size.max</code> property of the
     * {@link org.apache.fulcrum.upload.UploadService}.
     *
     * @return The maximum upload size.
     */
    public long getSizeMax();

    /**
     * <p> Retrieves the value of <code>size.threshold</code> property of
     * {@link org.apache.fulcrum.upload.UploadService}.
     *
     * @return The threshold beyond which files are written directly to disk.
     */
    public long getSizeThreshold();

    /**
     * <p> Retrieves the value of the <code>repository</code> property of
     * {@link org.apache.fulcrum.upload.UploadService}.
     *
     * @return The repository.
     */
    public String getRepository();

    public DiskFileUpload getFileUpload();
}
