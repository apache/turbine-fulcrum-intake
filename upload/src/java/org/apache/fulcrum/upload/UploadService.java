package org.apache.fulcrum.upload;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Turbine" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Turbine", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.DiskFileUpload;

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
            throws Exception;

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