/*
 * Created on Aug 22, 2003
 *
 */
package org.apache.fulcrum.security.adapter.turbine;

import java.util.Date;
import java.util.Hashtable;

import javax.servlet.http.HttpSessionBindingEvent;

import org.apache.fulcrum.security.entity.SecurityEntity;
import org.apache.turbine.om.security.User;

/**
 * @author Eric Pugh
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class UserAdapter extends BaseAdapter implements User
{

	public UserAdapter(org.apache.fulcrum.security.entity.User user)
	   {
		   super((SecurityEntity)user);
	   }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.User#getAccessCounter()
     */
    public int getAccessCounter()
    {
        // TODO Auto-generated method stub
        return 0;
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.User#getAccessCounterForSession()
     */
    public int getAccessCounterForSession()
    {
        // TODO Auto-generated method stub
        return 0;
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.User#getLastAccessDate()
     */
    public Date getLastAccessDate()
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.User#getCreateDate()
     */
    public Date getCreateDate()
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.User#getLastLogin()
     */
    public Date getLastLogin()
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.User#getPassword()
     */
    public String getPassword()
    {
        return ((User)entity).getPassword();
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.User#getPerm(java.lang.String)
     */
    public Object getPerm(String arg0)
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.User#getPerm(java.lang.String, java.lang.Object)
     */
    public Object getPerm(String arg0, Object arg1)
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.User#getPermStorage()
     */
    public Hashtable getPermStorage()
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.User#getTempStorage()
     */
    public Hashtable getTempStorage()
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.User#getTemp(java.lang.String)
     */
    public Object getTemp(String arg0)
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.User#getTemp(java.lang.String, java.lang.Object)
     */
    public Object getTemp(String arg0, Object arg1)
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* Adapter from getUserName to getName!
     * @see org.apache.turbine.om.security.User#getUserName()
     */
    public String getUserName()
    {
        return getName();
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.User#getFirstName()
     */
    public String getFirstName()
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.User#getLastName()
     */
    public String getLastName()
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.User#getEmail()
     */
    public String getEmail()
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.User#setHasLoggedIn(java.lang.Boolean)
     */
    public void setHasLoggedIn(Boolean arg0)
    {
        // TODO Auto-generated method stub
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.User#hasLoggedIn()
     */
    public boolean hasLoggedIn()
    {
        // TODO Auto-generated method stub
        return false;
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.User#incrementAccessCounter()
     */
    public void incrementAccessCounter()
    {
        // TODO Auto-generated method stub
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.User#incrementAccessCounterForSession()
     */
    public void incrementAccessCounterForSession()
    {
        // TODO Auto-generated method stub
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.User#removeTemp(java.lang.String)
     */
    public Object removeTemp(String arg0)
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.User#setAccessCounter(int)
     */
    public void setAccessCounter(int arg0)
    {
        // TODO Auto-generated method stub
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.User#setAccessCounterForSession(int)
     */
    public void setAccessCounterForSession(int arg0)
    {
        // TODO Auto-generated method stub
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.User#setLastAccessDate()
     */
    public void setLastAccessDate()
    {
        // TODO Auto-generated method stub
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.User#setLastLogin(java.util.Date)
     */
    public void setLastLogin(Date arg0)
    {
        // TODO Auto-generated method stub
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.User#setPassword(java.lang.String)
     */
    public void setPassword(String arg0)
    {
		((User)entity).setPassword(arg0);
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.User#setPerm(java.lang.String, java.lang.Object)
     */
    public void setPerm(String arg0, Object arg1)
    {
        // TODO Auto-generated method stub
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.User#setPermStorage(java.util.Hashtable)
     */
    public void setPermStorage(Hashtable arg0)
    {
        // TODO Auto-generated method stub
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.User#setTempStorage(java.util.Hashtable)
     */
    public void setTempStorage(Hashtable arg0)
    {
        // TODO Auto-generated method stub
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.User#setTemp(java.lang.String, java.lang.Object)
     */
    public void setTemp(String arg0, Object arg1)
    {
        // TODO Auto-generated method stub
    }
    /* Adaper for user name to name.
     * @see org.apache.turbine.om.security.User#setUserName(java.lang.String)
     */
    public void setUserName(String arg0)
    {
		setName(arg0);
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.User#setFirstName(java.lang.String)
     */
    public void setFirstName(String arg0)
    {
        // TODO Auto-generated method stub
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.User#setLastName(java.lang.String)
     */
    public void setLastName(String arg0)
    {
        // TODO Auto-generated method stub
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.User#setCreateDate(java.util.Date)
     */
    public void setCreateDate(Date arg0)
    {
        // TODO Auto-generated method stub
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.User#setEmail(java.lang.String)
     */
    public void setEmail(String arg0)
    {
        // TODO Auto-generated method stub
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.User#isConfirmed()
     */
    public boolean isConfirmed()
    {
        // TODO Auto-generated method stub
        return false;
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.User#setConfirmed(java.lang.String)
     */
    public void setConfirmed(String arg0)
    {
        // TODO Auto-generated method stub
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.User#getConfirmed()
     */
    public String getConfirmed()
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.User#updateLastLogin()
     */
    public void updateLastLogin() throws Exception
    {
        // TODO Auto-generated method stub
    }
    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSessionBindingListener#valueBound(javax.servlet.http.HttpSessionBindingEvent)
     */
    public void valueBound(HttpSessionBindingEvent arg0)
    {
        // TODO Auto-generated method stub
    }
    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSessionBindingListener#valueUnbound(javax.servlet.http.HttpSessionBindingEvent)
     */
    public void valueUnbound(HttpSessionBindingEvent arg0)
    {
        // TODO Auto-generated method stub
    }

   
}
