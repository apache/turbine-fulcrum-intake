package org.apache.fulcrum.xmlrpc;

/* ----------------------------------------------------------------------------
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
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
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Plexus", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
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
 * ----------------------------------------------------------------------------
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 * ----------------------------------------------------------------------------
 */

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;

import org.apache.xmlrpc.XmlRpc;

/**
 * Common code for the XML RPC components. This processes the system-properties and parser entries
 * from the config.
 *
 * @todo Handle XmlRpc.setDebug(boolean)
 *
 * @author <a href="mailto:jason@zenplex.com">Jason van Zyl</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 */
public abstract class AbstractXmlRpcComponent
    extends AbstractLogEnabled
    implements Configurable, Initializable
{
    /** SAX Parser class. */
    protected String saxParserClass;

    protected AbstractXmlRpcComponent()
    {
    }

    // ----------------------------------------------------------------------
    // Lifecycle Management
    // ----------------------------------------------------------------------

    public void configure(Configuration configuration)
        throws ConfigurationException
    {
        // Set system properties. These are required if you're
        // using SSL.
        setSystemPropertiesFromConfiguration(configuration);

        // Set the XML driver to the correct SAX parser class
        saxParserClass = configuration.getChild("parser").getValue("org.apache.xerces.parsers.SAXParser");
    }

    /**
     * Create System properties using the key-value pairs in a given
     * Configuration.  This is used to set system properties and the
     * URL https connection handler needed by JSSE to enable SSL
     * between XMLRPC client and server.
     *
     * @param configuration the Configuration defining the System
     * properties to be set
     */
    protected void setSystemPropertiesFromConfiguration(Configuration configuration)
        throws ConfigurationException
    {
        Configuration[] systemProperties =
                configuration.getChildren("systemProperty");

        getLogger().debug("system properties: " + systemProperties.length);

        for( int i = 0; i < systemProperties.length; i++ )
        {
            Configuration systemProperty = systemProperties[i];

            String key = systemProperty.getAttribute("name");
            String value = systemProperty.getAttribute("value");

            getLogger().debug("System property: " + key + " => " + value);

            System.setProperty(key, value);
        }
    }

    /**
     * This function initializes the XmlRpcService.
     */
    public void initialize()
        throws Exception
    {
        XmlRpc.setDriver ( saxParserClass );
    }
}
