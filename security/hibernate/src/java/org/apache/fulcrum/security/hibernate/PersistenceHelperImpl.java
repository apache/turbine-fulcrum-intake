package org.apache.fulcrum.security.hibernate;
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
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.Transaction;
import net.sf.hibernate.avalon.HibernateService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.security.entity.SecurityEntity;
import org.apache.fulcrum.security.spi.AbstractManager;
import org.apache.fulcrum.security.util.DataBackendException;
/**
 * 
 * This base implementation persists to a database via Hibernate. it provides methods shared by all
 * Hibernate SPI managers.
 * 
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class PersistenceHelperImpl
    extends AbstractManager
    implements PersistenceHelper
{

    /** Logging */
    private static Log log = LogFactory.getLog(PersistenceHelperImpl.class);
    protected HibernateService hibernateService;
    private Session session;
    protected Transaction transaction;

    /**
     * Deletes an entity object
     * 
     * @param role The object to be removed
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws UnknownEntityException if the object does not exist.
     */
    public void removeEntity(SecurityEntity entity) throws DataBackendException
    {
        try
        {
            session = retrieveSession();
            transaction = session.beginTransaction();
            session.delete(entity);
            transaction.commit();
        }
        catch (HibernateException he)
        {
            try
            {
                transaction.rollback();
            }
            catch (HibernateException hex)
            {
            }
            throw new DataBackendException(
                "Problem removing entity:" + he.getMessage(),
                he);
        }
    }
    /**
     * Stores changes made to an object
     * 
     * @param role The object to be saved
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws UnknownEntityException if the role does not exist.
     */
    public void updateEntity(SecurityEntity entity) throws DataBackendException
    {
        try
        {

            session = retrieveSession();

            transaction = session.beginTransaction();
            session.update(entity);
            transaction.commit();

        }
        catch (HibernateException he)
        {
            try
            {
                if (transaction != null)
                {
                    transaction.rollback();
                }
                if (he
                    .getMessage()
                    .indexOf("Another object was associated with this id")
                    > -1)
                {
                    session.close();
                    updateEntity(entity);
                }
                else
                {
                    throw new DataBackendException(
                        "updateEntity(" + entity + ")",
                        he);
                }
            }
            catch (HibernateException hex)
            {
            }

        }
        return;
    }
    /**
     * adds an entity
     * 
     * @param role The object to be saved
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws UnknownEntityException if the role does not exist.
     */
    public void addEntity(SecurityEntity entity) throws DataBackendException
    {
        try
        {
            session = retrieveSession();
            transaction = session.beginTransaction();
            session.save(entity);
            transaction.commit();
        }
        catch (HibernateException he)
        {
            try
            {
                transaction.rollback();
            }
            catch (HibernateException hex)
            {
            }
            throw new DataBackendException("addEntity(s,name)", he);
        }
        return;
    }

    /**
     * Returns a hibernate session that has been opened if it was null or not
     * connected or not open.
     * @return An Open hibernate session.
     * @throws HibernateException
     */
    public Session retrieveSession() throws HibernateException
    {
        if (session == null || (!session.isConnected() && !session.isOpen()))
        {
            session = getHibernateService().openSession();
        }
        return session;
    }

    /**
     * In some environments (like ECM) the service ends up getting it's own
     * copy of the HibernateService.  In those environments, we might want to
     * pass in a different HibernateService instead.
     * 
     * @param hibernateService The hibernateService to set.
     */
    public void setHibernateService(HibernateService hibernateService)
    {
        this.hibernateService = hibernateService;
    }

    /**
     * Lazy loads the hibernateservice if it hasn't been requested yet.
     * @return the hibernate service
     */
    public HibernateService getHibernateService() throws HibernateException
    {
        if (hibernateService == null)
        {
            hibernateService =
                (HibernateService) resolve(HibernateService.ROLE);

        }
        return hibernateService;
    }

}
