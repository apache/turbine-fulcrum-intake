package org.apache.fulcrum.security.hibernate.dynamic.entity;
/*
 *  Copyright 2001-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
									 
import org.apache.fulcrum.security.model.dynamic.entity.DynamicUser;

/**
 * User to test subclassing an existing class and then persiting with
 * Hibernate.
 * 
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class ExtendedUser extends DynamicUser
{
    private String NTDomain;
    private String NTName;
    private String Email;
    

    /**
     * @return Returns the email.
     */
    public String getEmail()
    {
        return Email;
    }

    /**
     * @param email The email to set.
     */
    public void setEmail(String email)
    {
        Email = email;
    }

    /**
     * @return Returns the nTDomain.
     */
    public String getNTDomain()
    {
        return NTDomain;
    }

    /**
     * @param domain The nTDomain to set.
     */
    public void setNTDomain(String domain)
    {
        NTDomain = domain;
    }

    /**
     * @return Returns the nTName.
     */
    public String getNTName()
    {
        return NTName;
    }

    /**
     * @param name The nTName to set.
     */
    public void setNTName(String name)
    {
        NTName = name;
    }

}


