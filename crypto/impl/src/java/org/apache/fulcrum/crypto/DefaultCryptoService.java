package org.apache.fulcrum.crypto;
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
import java.security.NoSuchAlgorithmException;
import java.util.Hashtable;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.thread.ThreadSafe;

/**
 * An implementation of CryptoService that uses either supplied crypto
 * Algorithms (provided in the component config xml file) or tries to get them via
 * the normal java mechanisms if this fails.
 *
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 *
 * @avalon.component name="crypto" lifestyle="singleton"
 * @avalon.service type="org.apache.fulcrum.crypto.CryptoService"
 */
public class DefaultCryptoService
    extends AbstractLogEnabled
    implements CryptoService, Configurable, Initializable, ThreadSafe
{
    //
    // SJM: removed Component and Contextualizable, Startable
    //

    /** Key Prefix for our algorithms */
    private static final String ALGORITHM = "algorithm";
    /** Default Key */
    private static final String DEFAULT_KEY = "default";
    /** Default Encryption Class */
    private static final String DEFAULT_CLASS = 
      "org.apache.fulcrum.crypto.provider.JavaCrypt";
    private boolean disposed = false;
    /** Names of the registered algorithms and the wanted classes */
    private Hashtable algos = null;

    /**
     * Returns a CryptoAlgorithm Object which represents the requested
     * crypto algorithm.
     *
     * @param algo      Name of the requested algorithm
     *
     * @return An Object representing the algorithm
     *
     * @throws NoSuchAlgorithmException  Requested algorithm is not available
     *
     */
    public CryptoAlgorithm getCryptoAlgorithm( String algo ) 
      throws NoSuchAlgorithmException
    {
        String cryptoClass = (String) algos.get(algo);
        CryptoAlgorithm ca = null;
        if (cryptoClass == null)
        {
            cryptoClass = (String) algos.get(DEFAULT_KEY);
        }
        if (cryptoClass == null || cryptoClass.equalsIgnoreCase("none"))
        {
            throw new NoSuchAlgorithmException(
              "TurbineCryptoService: No Algorithm for " + algo + " found");
        }
        try
        {
            //@todo should be created via factory service.  
            //Just trying to get something to work.
            //ca = (CryptoAlgorithm) factoryService.getInstance(cryptoClass);
            ca = (CryptoAlgorithm) Class.forName(cryptoClass).newInstance();
        }
        catch (Exception e)
        {
            throw new NoSuchAlgorithmException(
              "TurbineCryptoService: Error instantiating " 
              + cryptoClass + " for " + algo);
        }
        ca.setCipher(algo);
        return ca;
    }

    // ---------------- Avalon Lifecycle Methods ---------------------

    /**
     * Avalon component lifecycle method
     */
    public void configure(Configuration conf) throws ConfigurationException
    {
        this.algos = new Hashtable();
        // Set up default (Can be overridden by default key
        // from the properties
        algos.put(DEFAULT_KEY, DEFAULT_CLASS);
        final Configuration algorithms = conf.getChild(ALGORITHM, false);
        if (algorithms != null)
        {
            Configuration[] nameVal = algorithms.getChildren();
            for (int i = 0; i < nameVal.length; i++)
            {
                String key = nameVal[i].getName();
                String val = nameVal[i].getValue();
                // getLogger.debug("Registered " + val 
                //            + " for Crypto Algorithm " + key);
                algos.put(key, val);
            }
        }
    }
    
   /**
    * @see org.apache.avalon.framework.activity.Initializable#initialize()
    */
    public void initialize()
      throws Exception
    {
        getLogger().debug("initialize()");         
    }
    
    /**
     * Avalon component lifecycle method
     */
    public void dispose()
    {
        disposed = true;
    }
 
}
