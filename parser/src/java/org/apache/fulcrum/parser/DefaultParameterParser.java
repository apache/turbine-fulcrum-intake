package org.apache.fulcrum.parser;


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


import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.io.UnsupportedEncodingException;
import javax.servlet.http.HttpServletRequest;
import org.apache.fulcrum.pool.Recyclable;
import org.apache.fulcrum.upload.UploadService;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * DefaultParameterParser is a utility object to handle parsing and
 * retrieving the data passed via the GET/POST/PATH_INFO arguments.
 *
 * <p>NOTE: The name= portion of a name=value pair may be converted
 * to lowercase or uppercase when the object is initialized and when
 * new data is added.  This behaviour is determined by the url.case.folding
 * property in TurbineResources.properties.  Adding a name/value pair may
 * overwrite existing name=value pairs if the names match:
 *
 * <pre>
 * ParameterParser pp = data.getParameters();
 * pp.add("ERROR",1);
 * pp.add("eRrOr",2);
 * int result = pp.getInt("ERROR");
 * </pre>
 *
 * In the above example, result is 2.
 *
 * @author <a href="mailto:ilkka.priha@simsoft.fi">Ilkka Priha</a>
 * @author <a href="mailto:jon@clearink.com">Jon S. Stevens</a>
 * @author <a href="mailto:sean@informage.net">Sean Legassick</a>
 * @version $Id$
 */
public class DefaultParameterParser
    extends BaseValueParser
    implements ParameterParser,
               Recyclable, Serviceable
{
    /**
     * The servlet request to parse.
     */
    private HttpServletRequest request = null;

    /**
     * The raw data of a file upload.
     */
    private byte[] uploadData = null;
    
    /**
     * The upload service component to use
     */
    private UploadService uploadService;
    /**
     * Logger to use
     */
    Log log = LogFactory.getLog(DefaultParameterParser.class);

    /**
     * Create a new empty instance of ParameterParser.  Uses the
     * default character encoding (US-ASCII).
     *
     * <p>To add name/value pairs to this set of parameters, use the
     * <code>add()</code> methods.
     *
     */
    public DefaultParameterParser()
    {
        super();
    }

    /**
     * Create a new empty instance of ParameterParser. Takes a
     * character encoding name to use when converting strings to
     * bytes.
     *
     * <p>To add name/value pairs to this set of parameters, use the
     * <code>add()</code> methods.
     *
     * @param characterEncoding The character encoding of strings.
     */
    public DefaultParameterParser(String characterEncoding)
    {
        super (characterEncoding);
    }

    /**
     * Disposes the parser.
     */
    public void dispose()
    {
        this.request = null;
        this.uploadData = null;
        super.dispose();
    }

    /**
     * Gets the parsed servlet request.
     *
     * @return the parsed servlet request or null.
     */
    public HttpServletRequest getRequest()
    {
        return this.request;
    }

    /**
     * Sets the servlet request to be parser.  This requires a
     * valid HttpServletRequest object.  It will attempt to parse out
     * the GET/POST/PATH_INFO data and store the data into a Hashtable.
     * There are convenience methods for retrieving the data as a
     * number of different datatypes.  The PATH_INFO data must be a
     * URLEncoded() string.
     *
     * <p>To add name/value pairs to this set of parameters, use the
     * <code>add()</code> methods.
     *
     * @param req An HttpServletRequest.
     */
    public void setRequest(HttpServletRequest req)
    {
        clear();

        uploadData = null;

        String enc = req.getCharacterEncoding();
        setCharacterEncoding(enc != null ? enc : "US-ASCII");

        // String object re-use at its best.
        String tmp = null;

        tmp = req.getHeader("Content-type");
        if (tmp != null && tmp.startsWith("multipart/form-data"))
        {
            try
            {
                ArrayList items = uploadService.parseRequest(req,getRepository());
                Iterator i = items.iterator();

                while (i.hasNext())
                {
                    FileItem item = (FileItem) i.next();

                    if (item.isFormField())
                    {
                        String value = null;
                        try
                        {
                            value = item.getString(getCharacterEncoding());
                        }
                        catch (UnsupportedEncodingException e)
                        {
                            log.error(getCharacterEncoding() + 
                                "encoding is not supported.  Used the default "
                                + "when reading form data.");
                            value = item.getString();
                        }
                        append(item.getFieldName(), value);
                    }
                    else
                    {
                        append(item.getFieldName(), item);
                    }
                }
            }
            catch(Exception e)
            {
                log.error(new Exception("File upload failed", e));
            }
        }

        Enumeration names = req.getParameterNames();
        if ( names != null )
        {
            while(names.hasMoreElements())
            {
                tmp = (String) names.nextElement();
                parameters.put( convert(tmp), req.getParameterValues(tmp) );
            }
        }

        // Also cache any pathinfo variables that are passed around as
        // if they are query string data.
        try
        {
            // the lines below can be substituted with the method
            // parse(req.getPathInfo(), '/', true);
            // if DefaultParameterParser extended StringParser
            StringTokenizer st = new StringTokenizer(req.getPathInfo(), "/");
            boolean isNameTok = true;
            String pathPart = null;
            while (st.hasMoreTokens())
            {
                if (isNameTok)
                {
                    tmp = URLDecoder.decode(st.nextToken());
                    isNameTok = false;
                }
                else
                {
                    pathPart = URLDecoder.decode(st.nextToken());
                    if (tmp.length() > 0)
                    {
                        add (convert(tmp), pathPart);
                    }
                    isNameTok = true;
                }
            }
        }
        catch (Exception e)
        {
            // If anything goes wrong above, don't worry about it.
            // Chances are that the path info was wrong anyways and
            // things that depend on it being right will fail later
            // and should be caught later.
        }

        this.request = req;
    }

    /**
     * Sets the uploadData byte[]
     *
     * @param uploadData A byte[] with data.
     */
    public void setUploadData ( byte[] uploadData )
    {
        this.uploadData = uploadData;
    }

    /**
     * Gets the uploadData byte[]
     *
     * @returns uploadData A byte[] with data.
     */
    public byte[] setUploadData ()
    {
        return this.uploadData;
    }


    /**
     * Add a FileItem object as a parameters.  If there are any
     * FileItems already associated with the name, append to the
     * array.  The reason for this is that RFC 1867 allows multiple
     * files to be associated with single HTML input element.
     *
     * @param name A String with the name.
     * @param value A FileItem with the value.
     */
    public void append( String name,
                        FileItem value )
    {
        FileItem[] items = this.getFileItems(name);
        if(items == null)
        {
            items = new FileItem[1];
            items[0] = value;
            parameters.put( convert(name), items );
        }
        else
        {
            FileItem[] newItems = new FileItem[items.length+1];
            System.arraycopy(items, 0, newItems, 0, items.length);
            newItems[items.length] = value;
            parameters.put( convert(name), newItems );
        }
    }


    /**
     * Return a FileItem object for the given name.  If the name does
     * not exist or the object stored is not a FileItem, return null.
     *
     * @param name A String with the name.
     * @return A FileItem.
     */
    public FileItem getFileItem(String name)
    {
        try
        {
            FileItem value = null;
            Object object = parameters.get(convert(name));
            if (object != null)
                value = ((FileItem[])object)[0];
            return value;
        }
        catch ( ClassCastException e )
        {
            return null;
        }
    }

    /**
     * Return an array of FileItem objects for the given name.  If the
     * name does not exist or the object stored is not a FileItem
     * array, return null.
     *
     * @param name A String with the name.
     * @return A FileItem[].
     */
    public FileItem[] getFileItems(String name)
    {
        try
        {
            return (FileItem[])parameters.get(convert(name));
        }
        catch ( ClassCastException e )
        {
            return null;
        }
    }
    
    /**
     * <p> Retrieves the value of the <code>repository</code> property of
     * {@link org.apache.fulcrum.upload.UploadService}.
     *
     * @return The repository.
     */
    public String getRepository()
    {
        return uploadService.getFileUpload().getRepositoryPath();
    }    
    // ---------------- Avalon Lifecycle Methods ---------------------    
    /**
     * Avalon component lifecycle method
     */
    public void service( ServiceManager manager) throws ServiceException{        
        
        uploadService = (UploadService)manager.lookup(UploadService.class.getName());
        
    }      
}
