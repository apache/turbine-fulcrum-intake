package org.apache.fulcrum.crypto;

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

import java.security.NoSuchAlgorithmException;
import java.util.Hashtable;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.component.Component;
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
