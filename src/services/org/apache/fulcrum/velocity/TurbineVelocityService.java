package org.apache.fulcrum.velocity;

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

import java.util.Vector;
import java.util.Iterator;
import java.util.Enumeration;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.io.ByteArrayOutputStream;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.event.EventCartridge;
import org.apache.velocity.app.event.ReferenceInsertionEventHandler;
import org.apache.velocity.app.event.NullSetEventHandler;
import org.apache.velocity.app.event.MethodExceptionEventHandler;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.fulcrum.ServiceException;
import org.apache.fulcrum.InitializationException;
import org.apache.fulcrum.template.TurbineTemplate;
import org.apache.fulcrum.template.TemplateContext;
import org.apache.fulcrum.template.BaseTemplateEngineService;
import org.apache.commons.configuration.ConfigurationConverter;

/**
 * This is a Service that can process Velocity templates from within a
 * Turbine Screen.  Here's an example of how you might use it from a
 * screen:<br>
 *
 * <code><pre>
 * Context context = new VelocityContext();
 * context.put("message", "Hello from Turbine!");
 * String results = TurbineVelocity.handleRequest(context,"HelloWorld.vm");
 * </pre></code>
 *
 * Character sets map codes to glyphs, while encodings map between
 * chars/bytes and codes.
 * <i>bytes -> [encoding] -> charset -> [rendering] -> glyphs</i>
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:krzewski@e-point.pl">Rafal Krzewski</a>
 * @author <a href="mailto:jvanzyl@periapt.com">Jason van Zyl</a>
 * @author <a href="mailto:sean@informage.ent">Sean Legassick</a>
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @author <a href="mailto:mpoeschl@marmot.at">Martin Poeschl</a>
 * @version $Id$
 */
public class TurbineVelocityService
    extends BaseTemplateEngineService
    implements VelocityService
{
    /**
     * The generic resource loader path property in velocity.
     */
    private static final String RESOURCE_LOADER_PATH =
        ".resource.loader.path";

    /**
     * Default character set to use if not specified by caller.
     */
    private static final String DEFAULT_CHAR_SET = "ISO-8859-1";

    /**
     * The prefix used for URIs which are of type <code>jar</code>.
     */
    private static final String JAR_PREFIX = "jar:";

    /**
     * The prefix used for URIs which are of type <code>absolute</code>.
     */
    private static final String ABSOLUTE_PREFIX = "file://";

    /**
     * The EventCartridge that is used against all contexts
     */
    private EventCartridge eventCartridge;

    /**
     * Whether or not to use the eventCartridge. Defaults to true. 
     * Can be used to turn off EC processing.
     */
    private boolean eventCartridgeEnabled = true;

    /**
     * Performs early initialization of this Turbine service.
     */
    public void init()
        throws InitializationException
    {
        try
        {
            initVelocity();
            initEventCartridges();

            // Register with the template service.
            registerConfiguration("vm");
            setInit(true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new InitializationException(
                "Failed to initialize TurbineVelocityService", e);
        }
    }

    /**
     * @see org.apache.fulcrum.velocity.VelocityService
     */
    public String handleRequest(TemplateContext context, String template)
        throws ServiceException
    {
        return handleRequest(new ContextAdapter(context), template);
    }

    /**
     * @see org.apache.fulcrum.velocity.VelocityService
     */
    public String handleRequest(Context context, String filename)
        throws ServiceException
    {
        return handleRequest(context, filename, (String)null, null);
    }

    /**
     * @see org.apache.fulcrum.velocity.VelocityService
     */
    public String handleRequest(Context context, String filename,
                                String charset, String encoding)
        throws ServiceException
    {
        String results = null;
        ByteArrayOutputStream bytes = null;

        try
        {
            bytes = new ByteArrayOutputStream();
            charset = decodeRequest(context, filename, bytes, charset,
                                    encoding);
            results = bytes.toString(charset);
        }
        catch (Exception e)
        {
            renderingError(filename, e);
        }
        finally
        {
            try
            {
                if (bytes != null)
                {
                    bytes.close();
                }
            }
            catch (IOException ignored)
            {
            }
        }
        return results;
    }

    /**
     * @see org.apache.fulcrum.template.TemplateEngineService
     */
    public void handleRequest(TemplateContext context, String template,
                              OutputStream outputStream)
        throws ServiceException
    {
        handleRequest(new ContextAdapter(context), template, outputStream);
    }

    /**
     * @see org.apache.fulcrum.velocity.VelocityService
     */
    public void handleRequest(Context context, String filename,
                              OutputStream output)
        throws ServiceException
    {
        handleRequest(context, filename, output, null, null);
    }

    /**
     * @see org.apache.fulcrum.velocity.VelocityService
     */
    public void handleRequest(Context context, String filename,
                              OutputStream output, String charset,
                              String encoding)
        throws ServiceException
    {
        decodeRequest(context, filename, output, charset, encoding);
    }

    /**
     * @see org.apache.fulcrum.template.TemplateEngineService#handleRequest(
     * Context, String, Writer)
     */
    public void handleRequest(TemplateContext context,
                                       String template, Writer writer)
        throws ServiceException
    {
        handleRequest(new ContextAdapter(context), template, writer);
    }

    /**
     * @see org.apache.fulcrum.velocity.VelocityService#handleRequest(Context,
     * String, Writer)
     */
    public void handleRequest(Context context, String filename,
                              Writer writer)
        throws ServiceException
    {
        handleRequest(context, filename, writer, null);
    }

    /**
     * @see org.apache.fulcrum.velocity.VelocityService#handleRequest(Context,
     * String, Writer, String)
     */
    public void handleRequest(Context context, String filename,
                              Writer writer, String encoding)
        throws ServiceException
    {
        try
        {
            // need to convert to a VelocityContext because EventCartridges
            // need that. this sucks because it seems like unnecessary
            // object creation.
            VelocityContext velocityContext = new VelocityContext(context);

            // Attach the EC to the context
            EventCartridge ec = getEventCartridge();
            if (ec != null && eventCartridgeEnabled)
            {
                ec.attachToContext(velocityContext);
            }

            if (encoding != null)
            {
                // Request scoped encoding first supported by Velocity 1.1.
                Velocity.mergeTemplate(filename, encoding,
                                       velocityContext, writer);
            }
            else
            {
                Velocity.mergeTemplate(filename, velocityContext, writer);
            }
        }
        catch (Exception e)
        {
            renderingError(filename, e);
        }
    }

    /**
     * By default, this is true if there is configured event cartridges.
     * You can disable EC processing if you first disable it and then call
     * handleRequest.
     */
    public void setEventCartridgeEnabled(boolean value)
    {
        this.eventCartridgeEnabled = value;
    }

    /**
     * @return EventCartridge the event cartridge
     */
    public EventCartridge getEventCartridge()
    {
        return eventCartridge;
    }

    /**
     * Processes the request and fill in the template with the values
     * you set in the the supplied Context. Applies the specified
     * character and template encodings.
     *
     * @param context A context to use when evaluating the specified
     * template.
     * @param filename The file name of the template.
     * @param out The stream to which we will write the processed
     * template as a String.
     * @return The character set applied to the resulting text.
     *
     * @throws ServiceException Any exception trown while processing
     * will be wrapped into a ServiceException and rethrown.
     */
    private String decodeRequest(Context context, String filename,
                                 OutputStream output, String charset,
                                 String encoding)
        throws ServiceException
    {
        // TODO: Push this method of getting character set & encoding
        // from RunData back into Turbine.
        // charset  = ((RunData) data).getCharSet();
        // encoding = ((RunData) data).getTemplateEncoding();

        if (charset == null)
        {
            charset = DEFAULT_CHAR_SET;
        }

        OutputStreamWriter writer = null;
        try
        {
            try
            {
                writer = new OutputStreamWriter(output, charset);
            }
            catch (Exception e)
            {
                renderingError(filename, e);
            }
            handleRequest(context, filename, writer, encoding);
        }
        finally
        {
            try
            {
                if (writer != null)
                {
                    writer.flush();
                }
            }
            catch (Exception ignored)
            {
            }
        }
        return charset;
    }

    /**
     * Macro to handle rendering errors.
     *
     * @param filename The file name of the unrenderable template.
     * @param e        The error.
     *
     * @exception ServiceException Thrown every time.  Adds additional
     *                             information to <code>e</code>.
     */
    private final void renderingError(String filename, Throwable e)
        throws ServiceException
    {
        String err = "Error rendering Velocity template: " + filename;
        getCategory().error(err + ": " + e.getMessage());
        // if the Exception is a MethodInvocationException, the underlying
        // Exception is likely to be more informative, so rewrap that one.
        if (e instanceof MethodInvocationException)
        {
            e = ((MethodInvocationException)e).getWrappedThrowable();
        }

        throw new ServiceException(err, e);
    }

    /**
     * This method is responsible for initializing the various Velocity
     * EventCartridges. You just add a configuration like this:
     * <code>
     * services.VelocityService.eventCartridge.classes = org.tigris.scarab.util.ReferenceInsertionFilter
     * </code>
     * and list out (comma separated) the list of EC's that you want to
     * initialize.
     */
    private void initEventCartridges()
        throws InitializationException
    {
        eventCartridge = new EventCartridge();

        Vector ecconfig = getConfiguration()
            .getVector("eventCartridge.classes", null);
        if (ecconfig == null)
        {
            getCategory().info("No Velocity EventCartridges configured.");
            return;
        }

        Object obj = null;
        String className = null;
        for (Enumeration e = ecconfig.elements() ; e.hasMoreElements() ;)
        {
            className = (String) e.nextElement();
            try
            {
                boolean result = false;

                obj = Class.forName(className).newInstance();

                if (obj instanceof ReferenceInsertionEventHandler)
                {
                    result = getEventCartridge()
                        .addEventHandler((ReferenceInsertionEventHandler)obj);
                }
                else if (obj instanceof NullSetEventHandler)
                {
                    result = getEventCartridge()
                        .addEventHandler((NullSetEventHandler)obj);
                }
                else if (obj instanceof MethodExceptionEventHandler)
                {
                    result = getEventCartridge()
                        .addEventHandler((MethodExceptionEventHandler)obj);
                }
                getCategory().info("Added EventCartridge: " +
                    obj.getClass().getName() + " : " + result);
            }
            catch (Exception h)
            {
                throw new InitializationException(
                    "Could not initialize EventCartridge: " +
                    className, h);
            }
        }
    }

    /**
     * Setup the velocity runtime by using a subset of the
     * Turbine configuration which relates to velocity.
     *
     * @exception InitializationException For any errors during initialization.
     */
    private void initVelocity()
        throws InitializationException
    {
        // Now we have to perform a couple of path translations
        // for our log file and template paths.
        String path = getRealPath(
            getConfiguration().getString(Velocity.RUNTIME_LOG, null));

        if (path != null && path.length() > 0)
        {
            getConfiguration().setProperty(Velocity.RUNTIME_LOG, path);
        }
        else
        {
            String msg = VelocityService.SERVICE_NAME + " runtime log file " +
                "is misconfigured: '" + path + "' is not a valid log file";

            throw new Error(msg);
        }

        // Get all the template paths where the velocity
        // runtime should search for templates and
        // collect them into a separate vector
        // to avoid concurrent modification exceptions.
        String key;
        Vector keys = new Vector();
        for (Iterator i = getConfiguration().getKeys(); i.hasNext();)
        {
            key = (String) i.next();
            if (key.endsWith(RESOURCE_LOADER_PATH))
            {
                keys.add(key);
            }
        }

        // Loop through all template paths, clear the corresponding
        // velocity properties and translate them all to the webapp space.
        int ind;
        Vector paths;
        String entry;
        for (Iterator i = keys.iterator(); i.hasNext();)
        {
            key = (String) i.next();
            paths = getConfiguration().getVector(key,null);
            if (paths != null)
            {
                Velocity.clearProperty(key);
                getConfiguration().clearProperty(key);

                for (Iterator j = paths.iterator(); j.hasNext();)
                {
                    path = (String) j.next();
                    if (path.startsWith(JAR_PREFIX + "file"))
                    {
                        // A local jar resource URL path is a bit more
                        // complicated, but we can translate it as well.
                        ind = path.indexOf("!/");
                        if (ind >= 0)
                        {
                            entry = path.substring(ind);
                            path = path.substring(9,ind);
                        }
                        else
                        {
                            entry = "!/";
                            path = path.substring(9);
                        }
                        path = JAR_PREFIX + "file:" + getRealPath(path) +
                            entry;
                    }
                    else if (path.startsWith(ABSOLUTE_PREFIX))
                    {
                        path = path.substring (ABSOLUTE_PREFIX.length(),
                                               path.length());
                    }
                    else if (!path.startsWith(JAR_PREFIX))
                    {
                        // But we don't translate remote jar URLs.
                        path = getRealPath(path);
                    }
                    // Put the translated paths back to the configuration.
                    getConfiguration().addProperty(key,path);
                }
            }
        }

        try
        {
            Velocity.setExtendedProperties(ConfigurationConverter
                    .getExtendedProperties(getConfiguration()));
            Velocity.init();
        }
        catch(Exception e)
        {
            // This will be caught and rethrown by the init() method.
            // Oh well, that will protect us from RuntimeException folk showing
            // up somewhere above this try/catch
            throw new InitializationException(
                "Failed to set up TurbineVelocityService", e);
        }
    }

    /**
     * Find out if a given template exists. Velocity
     * will do its own searching to determine whether
     * a template exists or not.
     *
     * @param String template to search for
     * @return boolean
     */
    public boolean templateExists(String template)
    {
        return Velocity.templateExists(template);
    }
}
