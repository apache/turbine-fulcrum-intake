package org.apache.fulcrum.dvsl;


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


import java.io.Reader;
import java.io.Writer;

import java.util.Properties;

/**
 * The Fulcrum DVSL Service is used to transform XML with a DVSL stylesheet.
 * The service makes use of the DVSL engine available from the 
 * Jakarta Velocity project.
 *
 * @author <a href="mailto:geirm@apache.org">Geir Magnusson Jr.</a>
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 */
public interface DvslService
{
    /** Avalon role - used to id the component within the manager */
    String ROLE = DvslService.class.getName();


    /**
     *  Registers a stylesheet by name
     */
    public void register( String styleName, Reader stylesheet, Properties toolbox) 
        throws Exception;

    /**
     *  Unregisters a stylesheet by name
     */
    public void unregister( String styleName ) 
        throws Exception;

    /**
     * Uses an stylesheet to transform xml input from a reader and writes the
     * output to a writer.
     *
     * @param stylename  Name of a pre-registered stylesheet
     * @param in The reader that passes the xml to be transformed
     * @param out The writer for the transformed output
     */
    public void transform ( String styleName, Reader in, Writer out) 
        throws Exception;
}
