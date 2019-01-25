package org.apache.fulcrum.crypto.provider;

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

import java.security.MessageDigest;

import org.apache.commons.codec.binary.Base64;
import org.apache.fulcrum.crypto.CryptoAlgorithm;

/**
 * This is the Message Digest Implementation of Turbine 2.1. It does not pad the
 * Base64 encryption of the Message Digests correctly but truncates after 20
 * chars. This leads to interoperability problems if you want to use e.g.
 * database columns between two languages.
 *
 * If you upgrade an application from Turbine 2.1 and have already used the
 * Security Service with encrypted passwords and no way to rebuild your
 * databases, use this provider. It is bug-compatible.
 *
 * DO NOT USE THIS PROVIDER FOR ANY NEW APPLICATION!
 *
 * Nevertheless it can be used as the default crypto algorithm .
 *
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public class OldJavaCrypt implements CryptoAlgorithm 
{
	/** The default cipher */
	public static final String DEFAULT_CIPHER = "SHA";

	/** The cipher to use for encryption */
	private String cipher = null;

	/**
	 * Constructor
	 */
	public OldJavaCrypt() 
	{
		this.cipher = DEFAULT_CIPHER;
	}

	/**
	 * Setting the actual cipher requested. If not called, then the default cipher
	 * (SHA) is used.
	 *
	 * This will never throw an error even if there is no provider for this cipher.
	 * The error will be thrown by encrypt() (Fixme?)
	 *
	 * @param cipher The cipher to use.
	 *
	 */
	public void setCipher(String cipher) 
	{
		this.cipher = cipher;
	}

	/**
	 * This class never uses a seed, so this is just a dummy.
	 *
	 * @param seed Seed (ignored)
	 *
	 */
	public void setSeed(String seed) 
	{
		/* dummy */
	}

	/**
	 * Encrypt the supplied string with the requested cipher
	 *
	 * @param value The value to be encrypted
	 * @return The encrypted value
	 * @throws Exception An Exception of the underlying implementation.
	 */
	public String encrypt(String value) throws Exception 
	{
		MessageDigest md = MessageDigest.getInstance(cipher);
		byte[] digest = md.digest(value.getBytes("UTF-8"));
		byte[] base64 = Base64.encodeBase64(digest);
		
		// from MD5 the digest has 16 bytes but for SHA1 it contains 20 bytes
		// depending on the digest length the result is truncated
		int len = (digest.length == 16 ? 20 : 24);
		byte[] result = new byte[len];
		
		System.arraycopy(base64, 0, result, 0, result.length);
		return new String(result, "UTF-8");
	}
}
