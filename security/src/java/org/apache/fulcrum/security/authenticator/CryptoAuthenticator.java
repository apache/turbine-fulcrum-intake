package org.apache.fulcrum.security.authenticator;
/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.crypto.CryptoAlgorithm;
import org.apache.fulcrum.crypto.CryptoService;
import org.apache.fulcrum.security.entity.User;
import org.apache.fulcrum.security.util.DataBackendException;
/**
 * This class decorates any calls to authenticate a  user, and also
 * authenticates against NT.  
 *
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class CryptoAuthenticator extends AbstractLogEnabled implements Authenticator, Composable, Disposable,Configurable
{
    /** Logging */
    private static Log log = LogFactory.getLog(CryptoAuthenticator.class);
    boolean composed = false;
    protected CryptoService cryptoService = null;
    private String algorithm;
    private String cipher;
    /**
     * Authenticate an username with the specified password. If authentication
     * is successful the method returns true. If it fails, it returns false
     * If there are any problems, an exception is thrown.
     * 
     *
     * @param usernameAndDomain an string in the format [domain]/[username].
     * @param password the user supplied password.
     * @exception UnknownEntityException if the user's account does not
     *            exist in the database.
     * @exception DataBackendException if there is a problem accessing the
     *            storage.
     */
    public boolean authenticate(User user, String password) throws  DataBackendException
    {
 
        try
        {
            CryptoAlgorithm ca = cryptoService.getCryptoAlgorithm(algorithm);
            ca.setCipher(cipher);
            String output = ca.encrypt(password);
            return output.equals(user.getPassword());
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new DataBackendException(e.getMessage(), e);
        }
        catch (Exception ex)
        {
            throw new DataBackendException(ex.getMessage(), ex);
        }
    }
    
	// ---------------- Avalon Lifecycle Methods ---------------------
	   /**
		* Avalon component lifecycle method
		*/
	   public void configure(Configuration conf) throws ConfigurationException
	   {
		   
		algorithm = conf.getChild("algorithm").getValue();
		cipher = conf.getChild("cipher").getValue();
	   }    
    /**
      * Avalon component lifecycle method
      */
    public void compose(ComponentManager manager) throws ComponentException
    {
        this.cryptoService = (CryptoService)manager.lookup(CryptoService.ROLE);
    }
    public void dispose()
    {
        cryptoService = null;
    }
}
