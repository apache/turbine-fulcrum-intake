package org.apache.fulcrum.dvsl;


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

import java.io.Reader;
import java.io.Writer;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;



import org.apache.tools.dvsl.DVSL;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;

/**
 *  Implementation of the Fulcrum Dvsl Service. It transforms xml with a given
 *  dvsl file.
 *
 * @author <a href="mailto:geirm@apache.org">Geir Magnusson Jr.</a>
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @avalon.component name="dvsl" lifestyle="singleton"
 * @avalon.service type="org.apache.fulcrum.dvsl.DvslService"
 */
public class DefaultDvslService
    extends AbstractLogEnabled
    implements DvslService, Initializable
{
    protected Map servicePool = new HashMap();

    /**
     *  register a stylesheet
     */
    public void register( String styleName,  Reader stylesheet, Properties toolbox )
        throws Exception
    {
        DVSL dvsl = new DVSL();

        if ( stylesheet != null)
        {
            dvsl.setStylesheet( stylesheet );
        }
        else
        {
            throw new Exception("Null stylesheet Reader");
        }

        if( toolbox != null)
        {
            dvsl.setToolbox( toolbox );
        }

        servicePool.put( styleName, dvsl );
    }

    /**
     *  unregister a stylesheet and release resources
     */
    public void unregister( String styleName )
    {
        servicePool.remove( styleName );
    }

    /**
     * Execute an DVSLT
     */
    public void transform ( String styleName, Reader in, Writer out)
        throws Exception
    {
        DVSL dvsl = (DVSL) servicePool.get( styleName );

        dvsl.transform( in, out );
    }

    // ---------------- Avalon Lifecycle Methods ---------------------

    /**
     * Avalon component lifecycle method
     */
    public void initialize()
    {

    }

}
