/*
 * Created on Aug 23, 2003
 *
 */
package org.apache.fulcrum.security.adapter.turbine;

import com.mockobjects.servlet.MockHttpServletRequest;

/**
 * @author Eric Pugh
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class BetterMockHttpServletRequest extends MockHttpServletRequest
{
    /**
     * 
     */
    public BetterMockHttpServletRequest()
    {
        super();
        // TODO Auto-generated constructor stub
    }
    public int getServerPort(){
    	return 8080;	    
    }
    
    public String getCharacterEncoding(){
		return "US-ASCII";
    }
    
}
