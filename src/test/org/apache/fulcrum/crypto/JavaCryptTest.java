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

import junit.framework.TestCase;

import org.apache.fulcrum.ServiceManager;
import org.apache.fulcrum.TurbineServices;

import org.apache.fulcrum.factory.FactoryService; 
import org.apache.fulcrum.factory.TurbineFactoryService; 

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;

/**
 * Testcase for the CryptoService.
 *
 * Objective: Checks, whether the supplied "java" crypto
 * provider can be selected and offers MD5 and SHA1
 * algorithms.
 *
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 *
 */

public class JavaCryptTest
    extends TestCase
{
    private static final String PREFIX = "services." +
        CryptoService.SERVICE_NAME + '.';

    private String input   = "Oeltanks";
    private String md5result   = "XSop0mncK19Ii2r2CUe29w==";
    private String sha1result  = "uVDiJHaavRYX8oWt5ctkaa7j1cw=";

    public JavaCryptTest( String name )
    {
        super(name);
    }

    public void testSelection()
    {
        try
        {
            doit();
        }
        catch( Exception e )
        {
            fail( e.getMessage() );
        }
    }

    public void doit()
        throws Exception
    {
        ServiceManager serviceManager = TurbineServices.getInstance();
        serviceManager.setApplicationRoot(".");
        
        Configuration cfg = new BaseConfiguration();
        cfg.setProperty(PREFIX + "classname",
                        TurbineCryptoService.class.getName());
        cfg.setProperty(PREFIX + "algorithm.unix",
                        "org.apache.fulcrum.crypto.provider.UnixCrypt");
        cfg.setProperty(PREFIX + "algorithm.clear",
                        "org.apache.fulcrum.crypto.provider.ClearCrypt");
        cfg.setProperty(PREFIX + "algorithm.java",
                        "org.apache.fulcrum.crypto.provider.JavaCrypt");

        /* Ugh */

        cfg.setProperty("services." + FactoryService.SERVICE_NAME + ".classname",
                        TurbineFactoryService.class.getName());

        serviceManager.setConfiguration(cfg);

        serviceManager.init();

        CryptoAlgorithm ca = TurbineCrypto.getService().getCryptoAlgorithm("java");

        ca.setCipher("MD5");

        String output = ca.encrypt(input);

        if(!output.equals(md5result))
        {
            fail("MD5 Encryption failed, expected "+md5result+", got "+output);
        }

        ca.setCipher("SHA1");

        output = ca.encrypt(input);

        if(!output.equals(sha1result))
        {
            fail("SHA1 Encryption failed, expected "+sha1result+", got "+output);
        }
    }
}
