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


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileUploadException;

/**
 * <p> This class is an implementation of {@link UploadService}.
 *
 * <p> Files will be stored in temporary disk storage on in memory,
 * depending on request size, and will be available from the {@link
 * org.apache.fulcrum.util.parser.ParameterParser} as {@link
 * org.apache.fulcrum.upload.FileItem}s.
 *
 * <p>This implementation of {@link UploadService} handles multiple
 * files per single html widget, sent using multipar/mixed encoding
 * type, as specified by RFC 1867.  Use {@link
 * org.apache.fulcrum.util.parser.ParameterParser#getFileItems(String)} to
 * acquire an array of {@link
 * org.apache.fulcrum.upload.FileItem}s associated with given
 * html widget.
 *
 * @author <a href="mailto:Rafal.Krzewski@e-point.pl">Rafal Krzewski</a>
 * @author <a href="mailto:dlr@collab.net">Daniel Rall</a>
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @version $Id$
 */
public class DefaultUploadService
    extends AbstractLogEnabled
    implements UploadService, Initializable,Configurable, Contextualizable
{
    protected DiskFileUpload fileUpload;

    private int sizeThreshold;
    private int sizeMax;

    private String repositoryPath;
    /**
     * The application root
     */
    private String applicationRoot;


    public DiskFileUpload getFileUpload()
    {
        return fileUpload;
    }


    /**
     * The maximum allowed upload size
     */
    public long getSizeMax()
    {
        return getFileUpload().getSizeMax();
    }


    /**
     * The threshold beyond which files are written directly to disk.
     */
    public long getSizeThreshold()
    {
        return fileUpload.getSizeThreshold();
    }

    /**
     * The location used to temporarily store files that are larger
     * than the size threshold.
     */
    public String getRepository()
    {
        return fileUpload.getRepositoryPath();
    }

    /**
     * <p>Parses a <a href="http://rf.cx/rfc1867.html">RFC 1867</a>
     * compliant <code>multipart/form-data</code> stream.</p>
     *
     * @param req The servlet request to be parsed.
     * @param path The location where the files should be stored.
     * @exception FileUploadException Problems reading/parsing the
     * request or storing the uploaded file(s).
     */
    public ArrayList parseRequest(HttpServletRequest req, String path)
            throws FileUploadException
    {
       
        return (ArrayList)
            (getFileUpload())
            .parseRequest(req, sizeThreshold, sizeMax, path);
       
    }


    /**
     * <p>Parses a <a href="http://rf.cx/rfc1867.html">RFC 1867</a>
     * compliant <code>multipart/form-data</code> stream.</p>
     *
     * @param req The servlet request to be parsed.
     * @param sizeThreshold the max size in bytes to be stored in memory
     * @param sizeMax the maximum allowed upload size in bytes
     * @param path The location where the files should be stored.
     * @exception FileUploadException Problems reading/parsing the
     * request or storing the uploaded file(s).
     */
    public List parseRequest(HttpServletRequest req, int sizeThreshold,
                                  int sizeMax, String path)
            throws FileUploadException
    {
       
        return getFileUpload()
            .parseRequest(req, sizeThreshold, sizeMax, path);
   
    }

    /**
     * @see org.apache.fulcrum.ServiceBroker#getRealPath(String)
     */
    public String getRealPath(String path)
    {
        String absolutePath = null;
        if (applicationRoot == null)
        {
            absolutePath = new File(path).getAbsolutePath();
        }
        else
        {
            absolutePath = new File(applicationRoot, path).getAbsolutePath();
        }

        return absolutePath;
    }

    /**
     * @return Returns the repositoryPath.
     */
    public String getRepositoryPath()
    {
        return repositoryPath;
    }

    // ---------------- Avalon Lifecycle Methods ---------------------
    /**
     * Avalon component lifecycle method
     */
    public void configure(Configuration conf)
    {
        repositoryPath = conf.getAttribute(
                UploadService.REPOSITORY_KEY,
                UploadService.REPOSITORY_DEFAULT);


        sizeMax = conf.getAttributeAsInteger(
                UploadService.SIZE_MAX_KEY,
                UploadService.SIZE_MAX_DEFAULT);

        sizeThreshold = conf.getAttributeAsInteger(
                UploadService.SIZE_THRESHOLD_KEY,
                UploadService.SIZE_THRESHOLD_DEFAULT);

    }

    /**
     * Initializes the service.
     *
     * This method processes the repository path, to make it relative to the
     * web application root, if neccessary
     */
    public void initialize() throws Exception
    {


        // test for the existence of the path within the webapp directory.
        // if it does not exist, assume the path was to be used as is.
        String testPath = getRealPath(repositoryPath);
        File testDir = new File(testPath);
        if ( testDir.exists() )
        {
            repositoryPath = testPath;
        }


        getLogger().debug(
                "Upload Service: REPOSITORY_KEY => " + repositoryPath);

        DiskFileUpload diskFileUpload = new DiskFileUpload();


        diskFileUpload.setSizeMax(sizeMax);
        diskFileUpload.setSizeThreshold(sizeThreshold);

        diskFileUpload.setRepositoryPath( repositoryPath);

        fileUpload = diskFileUpload;
    }

    public void contextualize(Context context) throws ContextException {
        this.applicationRoot = context.get( "urn:avalon:home" ).toString();
    }


}
