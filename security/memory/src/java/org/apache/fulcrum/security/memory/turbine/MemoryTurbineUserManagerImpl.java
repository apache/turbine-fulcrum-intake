package org.apache.fulcrum.security.memory.turbine;
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
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.security.entity.User;
import org.apache.fulcrum.security.memory.MemoryUserManagerImpl;
import org.apache.fulcrum.security.model.turbine.TurbineUserManager;
import org.apache.fulcrum.security.util.DataBackendException;
import org.apache.fulcrum.security.util.UnknownEntityException;

/**
 * This implementation keeps all objects in memory.  This is mostly meant to help
 * with testing and prototyping of ideas.
 *
 * @todo Need to load up Crypto component and actually encrypt passwords!
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class MemoryTurbineUserManagerImpl
    extends MemoryUserManagerImpl
    implements TurbineUserManager
{
    /** Logging */
    private static Log log =
        LogFactory.getLog(MemoryTurbineUserManagerImpl.class);

    /**
    	   * Constructs an User object to represent an anonymous user of the
    	   * application.
    	   *
    	   * @return An anonymous Turbine User.
    	   * @throws UnknownEntityException if the implementation of User interface
    	   *         could not be determined, or does not exist.
    	   */
    public User getAnonymousUser() throws UnknownEntityException
    {
        User user;
        try
        {
            user = getUserInstance();
        }
        catch (DataBackendException dbe)
        {
            throw new UnknownEntityException(
                "Coudl not create an anonymous user.",
                dbe);
        }
        user.setName("");
        return user;
    }

    /**
     * Checks whether a passed user object matches the anonymous user pattern
     * according to the configured user manager
     *
     * @param user An user object
     *
     * @return True if this is an anonymous user
     *
     */
    public boolean isAnonymousUser(User user)
    {
        // Either just null, the name is null or the name is the empty string
        return (user == null) || StringUtils.isEmpty(user.getName());
    }

}
