package org.apache.fulcrum.dvsl;

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
import java.io.Reader;
import java.io.Writer;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;



import org.apache.tools.dvsl.DVSL;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.thread.ThreadSafe;

/**
 *  Implementation of the Fulcrum Dvsl Service. It transforms xml with a given
 *  dvsl file.
 *
 * @author <a href="mailto:geirm@apache.org">Geir Magnusson Jr.</a>
 */
public class DefaultDvslService
    extends AbstractLogEnabled
    implements DvslService, Initializable, ThreadSafe
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
