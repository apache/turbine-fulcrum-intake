package org.apache.fulcrum.security.spi.torque.turbine.impl;
/*
 * ==================================================================== The Apache Software
 * License, Version 1.1
 * 
 * Copyright (c) 2001-2003 The Apache Software Foundation. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met: 1. Redistributions of source code must retain
 * the above copyright notice, this list of conditions and the following disclaimer. 2.
 * Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided
 * with the distribution. 3. The end-user documentation included with the redistribution, if any,
 * must include the following acknowledgment: "This product includes software developed by the
 * Apache Software Foundation (http://www.apache.org/)." Alternately, this acknowledgment may
 * appear in the software itself, if and wherever such third-party acknowledgments normally appear. 4.
 * The names "Apache" and "Apache Software Foundation" and "Apache Turbine" must not be used to
 * endorse or promote products derived from this software without prior written permission. For
 * written permission, please contact apache@apache.org. 5. Products derived from this software may
 * not be called "Apache", "Apache Turbine", nor may "Apache" appear in their name, without prior
 * written permission of the Apache Software Foundation.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR ITS CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 * 
 * This software consists of voluntary contributions made by many individuals on behalf of the
 * Apache Software Foundation. For more information on the Apache Software Foundation, please see
 * <http://www.apache.org/> .
 */

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.fulcrum.security.UserManager;
import org.apache.fulcrum.security.util.DataBackendException;
/**
 * Factor out the ThreadLockingStuff...
 * 
 * @author <a href="mailto:jon@collab.net">Jon S. Stevens</a>
 * @author <a href="mailto:jmcnally@collab.net">John D. McNally</a>
 * @author <a href="mailto:frank.kim@clearink.com">Frank Y. Kim</a>
 * @author <a href="mailto:cberry@gluecode.com">Craig D. Berry</a>
 * @author <a href="mailto:Rafal.Krzewski@e-point.pl">Rafal Krzewski</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public abstract class TorqueManagerComponent
    extends AbstractLogEnabled
    implements Initializable, ThreadSafe, Composable
{
    private UserManager userManager;
    /** The number of threads concurrently reading security information */
    private int readerCount = 0;

    protected ComponentManager manager = null;
    /**
	 * @return
	 */
    UserManager getUserManager() throws DataBackendException
    {
        if (userManager == null)
        {
            try
            {
                userManager = (UserManager) manager.lookup(UserManager.ROLE);

            }
            catch (ComponentException ce)
            {
                throw new DataBackendException(ce.getMessage(), ce);
            }
        }
        return userManager;
    }
    /**
	 * Acquire a shared lock on the security information repository.
	 * 
	 * Methods that read security information need to invoke this method at the beginning of their
	 * body.
	 */
    protected synchronized void lockShared()
    {
        readerCount++;
    }
    /**
	 * Release a shared lock on the security information repository.
	 * 
	 * Methods that read security information need to invoke this method at the end of their body.
	 */
    protected synchronized void unlockShared()
    {
        readerCount--;
        this.notify();
    }
    /**
	 * Acquire an exclusive lock on the security information repository.
	 * 
	 * Methods that modify security information need to invoke this method at the beginning of
	 * their body. Note! Those methods must be <code>synchronized</code> themselves!
	 */
    protected void lockExclusive()
    {
        while (readerCount > 0)
        {
            try
            {
                this.wait();
            }
            catch (InterruptedException e)
            {
            }
        }
    }
    /**
	 * Release an exclusive lock on the security information repository.
	 * 
	 * This method is provided only for completeness. It does not really do anything. Note! Methods
	 * that modify security information must be <code>synchronized</code>!
	 */
    protected void unlockExclusive()
    {
        // do nothing
    }
    /** * AVALON LIFECYCLE METHOS * */
    /**
	 * Avalon component lifecycle method
	 */
    public void initialize()
    {
    }
    /**
	 * Avalon component lifecycle method
	 */
    public void compose(ComponentManager manager) throws ComponentException
    {
        this.manager = manager;

    }
    public void dispose()
    {

        manager = null;

    }
}
