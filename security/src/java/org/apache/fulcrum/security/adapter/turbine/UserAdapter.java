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

import java.util.Date;
import java.util.Hashtable;

import javax.servlet.http.HttpSessionBindingEvent;

import org.apache.fulcrum.security.entity.SecurityEntity;
import org.apache.turbine.om.security.User;

/**
 * Adapter around Fulcrum User.
 *
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class UserAdapter extends BaseAdapter implements User
{

	public UserAdapter(org.apache.fulcrum.security.entity.User user)
	   {
		   super((SecurityEntity)user);
	   }
    /* Does Nothing.
     * @see org.apache.turbine.om.security.User#getAccessCounter()
     */
    public int getAccessCounter()
    {
        return 0;
    }
    /* Does Nothing.
     * @see org.apache.turbine.om.security.User#getAccessCounterForSession()
     */
    public int getAccessCounterForSession()
    {
        return 0;
    }
    /* Does Nothing.
     * @see org.apache.turbine.om.security.User#getLastAccessDate()
     */
    public Date getLastAccessDate()
    {
        return null;
    }
    /* Does Nothing.
     * @see org.apache.turbine.om.security.User#getCreateDate()
     */
    public Date getCreateDate()
    {
        return null;
    }
    /* Does Nothing.
     * @see org.apache.turbine.om.security.User#getLastLogin()
     */
    public Date getLastLogin()
    {
        return null;
    }
    /* Does Nothing.
     * @see org.apache.turbine.om.security.User#getPassword()
     */
    public String getPassword()
    {
        return ((org.apache.fulcrum.security.entity.User)entity).getPassword();
    }
    /* Does Nothing.
     * @see org.apache.turbine.om.security.User#getPerm(java.lang.String)
     */
    public Object getPerm(String arg0)
    {

        return null;
    }
    /* Does Nothing.
     * @see org.apache.turbine.om.security.User#getPerm(java.lang.String, java.lang.Object)
     */
    public Object getPerm(String arg0, Object arg1)
    {

        return null;
    }
    /* Does Nothing.
     * @see org.apache.turbine.om.security.User#getPermStorage()
     */
    public Hashtable getPermStorage()
    {

        return null;
    }
    /* Does Nothing.
     * @see org.apache.turbine.om.security.User#getTempStorage()
     */
    public Hashtable getTempStorage()
    {

        return null;
    }
    /* Does Nothing.
     * @see org.apache.turbine.om.security.User#getTemp(java.lang.String)
     */
    public Object getTemp(String arg0)
    {

        return null;
    }
    /* Does Nothing.
     * @see org.apache.turbine.om.security.User#getTemp(java.lang.String, java.lang.Object)
     */
    public Object getTemp(String arg0, Object arg1)
    {

        return null;
    }
    /* Adapter from getUserName to getName!
     * @see org.apache.turbine.om.security.User#getUserName()
     */
    public String getUserName()
    {
        return getName();
    }
    /* Does Nothing.
     * @see org.apache.turbine.om.security.User#getFirstName()
     */
    public String getFirstName()
    {

        return null;
    }
    /* Does Nothing.
     * @see org.apache.turbine.om.security.User#getLastName()
     */
    public String getLastName()
    {

        return null;
    }
    /* Does Nothing.
     * @see org.apache.turbine.om.security.User#getEmail()
     */
    public String getEmail()
    {

        return null;
    }
    /* Does Nothing.
     * @see org.apache.turbine.om.security.User#setHasLoggedIn(java.lang.Boolean)
     */
    public void setHasLoggedIn(Boolean arg0)
    {

    }
    /* Does Nothing.
     * @see org.apache.turbine.om.security.User#hasLoggedIn()
     */
    public boolean hasLoggedIn()
    {

        return false;
    }
    /* Does Nothing.
     * @see org.apache.turbine.om.security.User#incrementAccessCounter()
     */
    public void incrementAccessCounter()
    {

    }
    /* Does Nothing.
     * @see org.apache.turbine.om.security.User#incrementAccessCounterForSession()
     */
    public void incrementAccessCounterForSession()
    {

    }
    /* Does Nothing.
     * @see org.apache.turbine.om.security.User#removeTemp(java.lang.String)
     */
    public Object removeTemp(String arg0)
    {

        return null;
    }
    /* Does Nothing.
     * @see org.apache.turbine.om.security.User#setAccessCounter(int)
     */
    public void setAccessCounter(int arg0)
    {

    }
    /* Does Nothing.
     * @see org.apache.turbine.om.security.User#setAccessCounterForSession(int)
     */
    public void setAccessCounterForSession(int arg0)
    {

    }
    /* Does Nothing.
     * @see org.apache.turbine.om.security.User#setLastAccessDate()
     */
    public void setLastAccessDate()
    {

    }
    /* Does Nothing.
     * @see org.apache.turbine.om.security.User#setLastLogin(java.util.Date)
     */
    public void setLastLogin(Date arg0)
    {

    }
    /* Does Nothing.
     * @see org.apache.turbine.om.security.User#setPassword(java.lang.String)
     */
    public void setPassword(String arg0)
    {
		((org.apache.fulcrum.security.entity.User)entity).setPassword(arg0);
    }
    /* Does Nothing.
     * @see org.apache.turbine.om.security.User#setPerm(java.lang.String, java.lang.Object)
     */
    public void setPerm(String arg0, Object arg1)
    {

    }
    /* Does Nothing.
     * @see org.apache.turbine.om.security.User#setPermStorage(java.util.Hashtable)
     */
    public void setPermStorage(Hashtable arg0)
    {

    }
    /* Does Nothing.
     * @see org.apache.turbine.om.security.User#setTempStorage(java.util.Hashtable)
     */
    public void setTempStorage(Hashtable arg0)
    {

    }
    /* Does Nothing.
     * @see org.apache.turbine.om.security.User#setTemp(java.lang.String, java.lang.Object)
     */
    public void setTemp(String arg0, Object arg1)
    {

    }
    /* Adaper for user name to name.
     * @see org.apache.turbine.om.security.User#setUserName(java.lang.String)
     */
    public void setUserName(String arg0)
    {
		setName(arg0);
    }
    /* Does Nothing.
     * @see org.apache.turbine.om.security.User#setFirstName(java.lang.String)
     */
    public void setFirstName(String arg0)
    {

    }
    /* Does Nothing.
     * @see org.apache.turbine.om.security.User#setLastName(java.lang.String)
     */
    public void setLastName(String arg0)
    {

    }
    /* Does Nothing.
     * @see org.apache.turbine.om.security.User#setCreateDate(java.util.Date)
     */
    public void setCreateDate(Date arg0)
    {

    }
    /* Does Nothing.
     * @see org.apache.turbine.om.security.User#setEmail(java.lang.String)
     */
    public void setEmail(String arg0)
    {

    }
    /* Does Nothing.
     * @see org.apache.turbine.om.security.User#isConfirmed()
     */
    public boolean isConfirmed()
    {

        return false;
    }
    /* Does Nothing.
     * @see org.apache.turbine.om.security.User#setConfirmed(java.lang.String)
     */
    public void setConfirmed(String arg0)
    {

    }
    /* Does Nothing.
     * @see org.apache.turbine.om.security.User#getConfirmed()
     */
    public String getConfirmed()
    {
        return null;
    }
    /* Does Nothing.
     * @see org.apache.turbine.om.security.User#updateLastLogin()
     */
    public void updateLastLogin() throws Exception
    {
    }
    /* Does Nothing.
     * @see javax.servlet.http.HttpSessionBindingListener#valueBound(javax.servlet.http.HttpSessionBindingEvent)
     */
    public void valueBound(HttpSessionBindingEvent arg0)
    {
    }
    /* Does Nothing.
     * @see javax.servlet.http.HttpSessionBindingListener#valueUnbound(javax.servlet.http.HttpSessionBindingEvent)
     */
    public void valueUnbound(HttpSessionBindingEvent arg0)
    {
    }


}
