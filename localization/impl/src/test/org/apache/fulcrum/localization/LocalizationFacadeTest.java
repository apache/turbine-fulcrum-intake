package org.apache.fulcrum.localization;
/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2001 The Apache Software Foundation.  All rights
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
 *     "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache" or
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
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

// Cactus and Junit imports
import java.util.Locale;

import junit.awtui.TestRunner;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.avalon.merlin.unit.AbstractMerlinTestCase;

/**
 * Test the facade class for LocalizationService.
 *
 * @author <a href="epugh@upstate.com">Eric Pugh</a>
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @version $Id$
 */
public class LocalizationFacadeTest extends AbstractMerlinTestCase
{
    /**
     * Defines the testcase name for JUnit.
     *
     * @param name the testcase's name.
     */
    public LocalizationFacadeTest(String name)
    {
        super( 
          MAVEN_TARGET_CLASSES_DIR, 
          MERLIN_DEFAULT_CONFIG_FILE, 
          MERLIN_INFO_OFF, 
          MERLIN_DEBUG_OFF, 
          name );
    }
    /**
     * Start the tests.
     *
     * @param args the arguments. Not used
     */
    public static void main(String args[])
    {
        TestRunner.main(new String[] { LocalizationFacadeTest.class.getName()});
    }
    /**
     * Creates the test suite.
     *
     * @return a test suite (<code>TestSuite</code>) that includes all methods
     *         starting with "test"
     */
    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(LocalizationFacadeTest.class);
    }

    public void testFacadeNotConfigured() throws Exception
    {
        getLogger().info( "testFacadeConfigured: " + Localization.isInitialized() );
	  assertFalse( Localization.isInitialized() );
        getLogger().info( "looking good" );
        try
        {
            Localization.getString("bill");
        }
        catch (RuntimeException re)
        {
            getLogger().info( "OK" );
            //good;
        }
    }

    public void testFacadeConfigured() throws Exception
    {
        getLogger().info( "testFacadeConfigured " + Localization.isInitialized() );
        // this.lookup causes the service to be configured.
        this.resolve( "localization" );
        assertTrue(Localization.isInitialized());
        String s = Localization.getString(null, new Locale("ko", "KR"), "key4");
        assertEquals(
            "Unable to retrieve localized text for locale: default",
            s,
            "value4");
        getLogger().info( "OK" );
    }

}
