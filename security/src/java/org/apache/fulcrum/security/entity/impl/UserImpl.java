/*
 * Created on Aug 21, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.apache.fulcrum.security.entity.impl;

import org.apache.fulcrum.security.entity.User;

/**
 * @author Eric Pugh
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class UserImpl extends SecurityEntityImpl implements User 
{
	private String password;
	
    /**
     * @return
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * @param password
     */
    public void setPassword(String password)
    {
        this.password = password;
    }

    /**
     * this is a wrapper around name.
     * @return
     */
    public String getUserName()
    {
        return getName();
    }

    /**
     * this is a wrapper around the inherited Name.
     * @param userName
     */
    public void setUserName(String userName)
    {
        setName(userName);
    }

    /**
     * 
     */
    public UserImpl()
    {
        super();
        // TODO Auto-generated constructor stub
    }
}
