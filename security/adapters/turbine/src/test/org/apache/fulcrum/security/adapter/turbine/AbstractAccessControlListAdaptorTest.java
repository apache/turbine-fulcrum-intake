package org.apache.fulcrum.security.adapter.turbine;
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
import java.util.Vector;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.fulcrum.testcontainer.BaseUnitTest;
import org.apache.turbine.modules.actions.sessionvalidator.DefaultSessionValidator;
import org.apache.turbine.modules.actions.sessionvalidator.SessionValidator;
import org.apache.turbine.om.security.User;
import org.apache.turbine.services.ServiceManager;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.avaloncomponent.AvalonComponentService;
import org.apache.turbine.services.rundata.RunDataService;
import org.apache.turbine.services.security.SecurityService;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.TurbineConfig;

import com.mockobjects.servlet.MockHttpServletResponse;
import com.mockobjects.servlet.MockHttpSession;
import com.mockobjects.servlet.MockServletConfig;

/**
 * Test that we can load up a fulcrum ACL in Turbine, without Turbine
 * knowing that anything has changed.
 * 
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */

public abstract class AbstractAccessControlListAdaptorTest extends BaseUnitTest
{
    protected static final String PREFIX =
        "services." + SecurityService.SERVICE_NAME + '.';
	protected TurbineConfig tc;
	protected MockHttpSession session;
	protected AvalonComponentService acs;
	
	public abstract String getTRProps();
    public AbstractAccessControlListAdaptorTest(String name) throws Exception
    {
        super(name);
    }
    
	public void setUp() throws Exception
	 {
	    super.setUp();
		 tc =
			 new TurbineConfig(
				 ".",
				 "/src/test/"+getTRProps());
		 tc.initialize();
		 session = new MockHttpSession();
		 acs =
			 (AvalonComponentService) TurbineServices.getInstance().getService(
				 AvalonComponentService.SERVICE_NAME);
	 }    
   
	protected User getUserFromRunData(HttpSession session) throws Exception
    {
        RunDataService rds =
            (RunDataService) TurbineServices.getInstance().getService(
                RunDataService.SERVICE_NAME);
        BetterMockHttpServletRequest request =
            new BetterMockHttpServletRequest();
        request.setupServerName("bob");
        request.setupGetProtocol("http");
        request.setupScheme("scheme");
        request.setupPathInfo("damn");
        request.setupGetServletPath("damn2");
        request.setupGetContextPath("wow");
        request.setupGetContentType("html/text");
        request.setupAddHeader("Content-type", "html/text");
        Vector v = new Vector();
        request.setupGetParameterNames(v.elements());
        request.setSession(session);
        HttpServletResponse response = new MockHttpServletResponse();
        ServletConfig config = new MockServletConfig();
        RunData rd = rds.getRunData(request, response, config);
        SessionValidator sessionValidator = new DefaultSessionValidator();
        sessionValidator.doPerform(rd);
        User turbineUser = rd.getUser();
        assertNotNull(turbineUser);
        return turbineUser;
    }

   
    public void tearDown()
    {
        super.tearDown();
        ServiceManager serviceManager = TurbineServices.getInstance();
        serviceManager.shutdownService(SecurityService.SERVICE_NAME);
        serviceManager.shutdownServices();
    }
}
