package org.apache.fulcrum.security.password;
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

import org.apache.fulcrum.crypto.CryptoAlgorithm;
import org.apache.fulcrum.crypto.CryptoService;

/**
 * This class should provide any password functions needed.
 *
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class PasswordUtils
{
    private PasswordUtils(){

    }


	/**
	   * This method provides client-side encryption of passwords.
	   *
	   * If <code>secure.passwords</code> are enabled in TurbineResources,
	   * the password will be encrypted, if not, it will be returned unchanged.
	   * The <code>secure.passwords.algorithm</code> property can be used
	   * to chose which digest algorithm should be used for performing the
	   * encryption. <code>SHA</code> is used by default.
	   *
	   * @param password the password to process
	   * @return processed password
	   */
	  public String encryptPassword(String password) throws Exception
	  {
		  return encryptPassword(password, null);
	  }

	  /**
	   * This method provides client-side encryption of passwords.
	   *
	   * If <code>secure.passwords</code> are enabled in TurbineResources,
	   * the password will be encrypted, if not, it will be returned unchanged.
	   * The <code>secure.passwords.algorithm</code> property can be used
	   * to chose which digest algorithm should be used for performing the
	   * encryption. <code>SHA</code> is used by default.
	   *
	   * The used algorithms must be prepared to accept null as a
	   * valid parameter for salt. All algorithms in the Fulcrum Cryptoservice
	   * accept this.
	   *
	   * @param password the password to process
	   * @param salt     algorithms that needs a salt can provide one here
	   * @return processed password
	   */

	  public String encryptPassword(String password, String salt) throws Exception
	  {
		  if (password == null)
		  {
			  return null;
		  }

		  // @todo Need to pass this in..

		  String secure = null;/*getConfiguration().getString(
				  SecurityService.SECURE_PASSWORDS_KEY,
				  SecurityService.SECURE_PASSWORDS_DEFAULT).toLowerCase();
*/
//@todo also need to pass this in..
		  String algorithm = null;/*getConfiguration().getString(
				  SecurityService.SECURE_PASSWORDS_ALGORITHM_KEY,
				  SecurityService.SECURE_PASSWORDS_ALGORITHM_DEFAULT);*/

		  CryptoService cs = null;//  @todo Must fix this line.   TurbineCrypto.getService();

		  if (cs != null && (secure.equals("true") || secure.equals("yes")))
		  {

				  CryptoAlgorithm ca = cs.getCryptoAlgorithm(algorithm);

				  ca.setSeed(salt);

				  String result = ca.encrypt(password);

				  return result;

		  }
		  else
		  {
			  return password;
		  }
	  }
    /**
    	 * Checks if a supplied password matches the encrypted password
    	 *
    	 * @param checkpw      The clear text password supplied by the user
    	 * @param encpw        The current, encrypted password
    	 *
    	 * @return true if the password matches, else false
    	 *
    	 */
    public boolean checkPassword(String checkpw, String encpw) throws Exception
    {
        String result = encryptPassword(checkpw, encpw);
        return (result == null) ? false : result.equals(encpw);
    }
}
