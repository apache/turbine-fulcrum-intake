package org.apache.fulcrum.pool;
/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.fulcrum.factory.FactoryException;
import org.apache.fulcrum.factory.FactoryService;

/**
 * The Pool Service extends the Factory Service by adding support
 * for pooling instantiated objects. When a new instance is
 * requested, the service first checks its pool if one is available.
 * If the the pool is empty, a new instance will be requested
 * from the FactoryService.
 *
 * For objects implementing the Recyclable interface, a recycle
 * method will be called, when they taken from the pool, and
 * a dispose method, when they are returned to the pool.
 *
 * @author <a href="mailto:ilkka.priha@simsoft.fi">Ilkka Priha</a>
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @version $Id$
 *
 * @avalon.component name="pool" lifestyle="transient"
 * @avalon.service type="org.apache.fulcrum.pool.PoolService"
 */
public class DefaultPoolService extends AbstractLogEnabled implements PoolService, Serviceable, Disposable, Initializable, Configurable
{
    /**
     * The property specifying the pool capacity.
     */
    public static final String POOL_CAPACITY = "capacity";
    /**
     * An inner class for class specific pools.
     */
    private class PoolBuffer
    {
        /**
         * An inner class for cached recycle methods.
         */
        private class Recycler
        {
            /**
             * The method.
             */
            private final Method recycle;
            /**
             * The signature.
             */
            private final String[] signature;
            /**
             * Constructs a new recycler.
             *
             * @param rec the recycle method.
             * @param sign the signature.
             */
            public Recycler(Method rec, String[] sign)
            {
                recycle = rec;
                signature = (sign != null) && (sign.length > 0) ? sign : null;
            }
            /**
             * Matches the given signature against
             * that of the recycle method of this recycler.
             *
             * @param sign the signature.
             * @return the matching recycle method or null.
             */
            public Method match(String[] sign)
            {
                if ((sign != null) && (sign.length > 0))
                {
                    if ((signature != null) && (sign.length == signature.length))
                    {
                        for (int i = 0; i < signature.length; i++)
                        {
                            if (!signature[i].equals(sign[i]))
                            {
                                return null;
                            }
                        }
                        return recycle;
                    }
                    else
                    {
                        return null;
                    }
                }
                else if (signature == null)
                {
                    return recycle;
                }
                else
                {
                    return null;
                }
            }
        }
        /**
         * A buffer for class instances.
         */
        private BoundedBuffer pool;
        /**
         * A flag to determine if a more efficient recycler is implemented.
         */
        private boolean arrayCtorRecyclable;
        /**
         * A cache for recycling methods.
         */
        private ArrayList recyclers;
        /**
         * Contructs a new pool buffer with a specific capacity.
         *
         * @param capacity a capacity.
         */
        public PoolBuffer(int capacity)
        {
            pool = new BoundedBuffer(capacity);
        }
        /**
         * Tells pool that it contains objects which can be
         * initialized using an Object array.
         *
         * @param isArrayCtor a <code>boolean</code> value
         */
        public void setArrayCtorRecyclable(boolean isArrayCtor)
        {
            arrayCtorRecyclable = isArrayCtor;
        }
        /**
         * Polls for an instance from the pool.
         *
         * @return an instance or null.
         */
        public Object poll(Object[] params, String[] signature) throws PoolException
        {
            Object instance = pool.poll();
            if (instance != null)
            {
                if (arrayCtorRecyclable)
                {
                    ((ArrayCtorRecyclable) instance).recycle(params);
                }
                else if (instance instanceof Recyclable)
                {
                    try
                    {
                        if ((signature != null) && (signature.length > 0))
                        {
                            /* Get the recycle method from the cache. */
                            Method recycle = getRecycle(signature);
                            if (recycle == null)
                            {
                                synchronized (this)
                                {
                                    /* Make a synchronized recheck. */
                                    recycle = getRecycle(signature);
                                    if (recycle == null)
                                    {
                                        Class clazz = instance.getClass();
                                        recycle =
                                            clazz.getMethod(
                                                "recycle",
                                                getFactory().getSignature(clazz, params, signature));
                                        ArrayList cache =
                                            recyclers != null ? (ArrayList) recyclers.clone() : new ArrayList();
                                        cache.add(new Recycler(recycle, signature));
                                        recyclers = cache;
                                    }
                                }
                            }
                            recycle.invoke(instance, params);
                        }
                        else
                        {
                            ((Recyclable) instance).recycle();
                        }
                    }
                    catch (Exception x)
                    {
                        throw new PoolException("Recycling failed for " + instance.getClass().getName(), x);
                    }
                }
            }
            return instance;
        }
        /**
         * Offers an instance to the pool.
         *
         * @param instance an instance.
         */
        public boolean offer(Object instance)
        {
            if (instance instanceof Recyclable)
            {
                try
                {
                    ((Recyclable) instance).dispose();
                }
                catch (Exception x)
                {
                    return false;
                }
            }
            return pool.offer(instance);
        }
        /**
         * Returns the capacity of the pool.
         *
         * @return the capacity.
         */
        public int capacity()
        {
            return pool.capacity();
        }
        /**
         * Returns the size of the pool.
         *
         * @return the size.
         */
        public int size()
        {
            return pool.size();
        }
        /**
         * Returns a cached recycle method
         * corresponding to the given signature.
         *
         * @param signature the signature.
         * @return the recycle method or null.
         */
        private Method getRecycle(String[] signature)
        {
            ArrayList cache = recyclers;
            if (cache != null)
            {
                Method recycle;
                for (Iterator i = cache.iterator(); i.hasNext();)
                {
                    recycle = ((Recycler) i.next()).match(signature);
                    if (recycle != null)
                    {
                        return recycle;
                    }
                }
            }
            return null;
        }
    }
    /**
     * The default capacity of pools.
     */
    private int poolCapacity = DEFAULT_POOL_CAPACITY;
    /**
     * The pool repository, one pool for each class.
     */
    private HashMap poolRepository = new HashMap();
    private Map capacityMap;
    private FactoryService factoryService;
    private ServiceManager manager;
    /**
     * Constructs a Pool Service.
     */
    public DefaultPoolService()
    {
    }
    /**
     * Gets an instance of a named class either from the pool
     * or by calling the Factory Service if the pool is empty.
     *
     * @param className the name of the class.
     * @return the instance.
     * @throws PoolException if recycling fails.
     */
    public Object getInstance(String className) throws PoolException
    {
        try
        {
            Object instance = pollInstance(className, null, null);
            return instance == null ? getFactory().getInstance(className) : instance;
        }
        catch (FactoryException fe)
        {
            throw new PoolException(fe);
        }
    }
    /**
     * Gets an instance of a named class either from the pool
     * or by calling the Factory Service if the pool is empty.
     * The specified class loader will be passed to the Factory Service.
     *
     * @param className the name of the class.
     * @param loader the class loader.
     * @return the instance.
     * @throws PoolException if recycling fails.
     */
    public Object getInstance(String className, ClassLoader loader) throws PoolException
    {
        try
        {
            Object instance = pollInstance(className, null, null);
            return instance == null ? getFactory().getInstance(className, loader) : instance;
        }
        catch (FactoryException fe)
        {
            throw new PoolException(fe);
        }
    }
    /**
     * Gets an instance of a named class either from the pool
     * or by calling the Factory Service if the pool is empty.
     * Parameters for its constructor are given as an array of objects,
     * primitive types must be wrapped with a corresponding class.
     *
     * @param className the name of the class.
     * @param loader the class loader.
     * @param params an array containing the parameters of the constructor.
     * @param signature an array containing the signature of the constructor.
     * @return the instance.
     * @throws PoolException if recycling fails.
     */
    public Object getInstance(String className, Object[] params, String[] signature) throws PoolException
    {
        try
        {
            Object instance = pollInstance(className, params, signature);
            return instance == null ? getFactory().getInstance(className, params, signature) : instance;
        }
        catch (FactoryException fe)
        {
            throw new PoolException(fe);
        }
    }
    /**
     * Gets an instance of a named class either from the pool
     * or by calling the Factory Service if the pool is empty.
     * Parameters for its constructor are given as an array of objects,
     * primitive types must be wrapped with a corresponding class.
     * The specified class loader will be passed to the Factory Service.
     *
     * @param className the name of the class.
     * @param loader the class loader.
     * @param params an array containing the parameters of the constructor.
     * @param signature an array containing the signature of the constructor.
     * @return the instance.
     * @throws PoolException if recycling fails.
     */
    public Object getInstance(String className, ClassLoader loader, Object[] params, String[] signature)
        throws PoolException
    {
        try
        {
            Object instance = pollInstance(className, params, signature);
            return instance == null ? getFactory().getInstance(className, loader, params, signature) : instance;
        }
        catch (FactoryException fe)
        {
            throw new PoolException(fe);
        }
    }
    /**
     * Tests if specified class loaders are supported for a named class.
     *
     * @param className the name of the class.
     * @return true if class loaders are supported, false otherwise.
     * @throws PoolException if test fails.
     */
    public boolean isLoaderSupported(String className) throws FactoryException
    {
        return getFactory().isLoaderSupported(className);
    }
    /**
     * Gets an instance of a specified class either from the pool
     * or by instatiating from the class if the pool is empty.
     *
     * @param clazz the class.
     * @return the instance.
     * @throws PoolException if recycling fails.
     */
    public Object getInstance(Class clazz) throws PoolException
    {
        try
        {
            Object instance = pollInstance(clazz.getName(), null, null);
            return instance == null ? factoryService.getInstance(clazz) : instance;
        }
        catch (FactoryException fe)
        {
            throw new PoolException(fe);
        }
    }
    /**
     * Gets an instance of a specified class either from the pool
     * or by instatiating from the class if the pool is empty.
     *
     * @todo There is a whacky .toString() on the clazzz, but otherwise it
     * won't compile..
     * @param clazz the class.
     * @param params an array containing the parameters of the constructor.
     * @param signature an array containing the signature of the constructor.
     * @return the instance.
     * @throws PoolException if recycling fails.
     */
    public Object getInstance(Class clazz, Object params[], String signature[]) throws PoolException
    {
        try
        {
            Object instance = pollInstance(clazz.getName(), params, signature);
            //FactoryService fs = getFactory();            
            return instance == null ? getFactory().getInstance(clazz.toString(), params, signature) : instance;
        }
        catch (FactoryException fe)
        {
            throw new PoolException(fe);
        }
    }
    /**
     * Puts a used object back to the pool. Objects implementing
     * the Recyclable interface can provide a recycle method to
     * be called when they are reused and a dispose method to be
     * called when they are returned to the pool.
     *
     * @param instance the object instance to recycle.
     * @return true if the instance was accepted.
     */
    public boolean putInstance(Object instance)
    {
        if (instance != null)
        {
            HashMap repository = poolRepository;
            String className = instance.getClass().getName();
            PoolBuffer pool = (PoolBuffer) repository.get(className);
            if (pool == null)
            {
                pool = new PoolBuffer(getCapacity(className));
                repository = (HashMap) repository.clone();
                repository.put(className, pool);
                poolRepository = repository;
                if (instance instanceof ArrayCtorRecyclable)
                {
                    pool.setArrayCtorRecyclable(true);
                }
            }
            return pool.offer(instance);
        }
        else
        {
            return false;
        }
    }
    /**
     * Gets the capacity of the pool for a named class.
     *
     * @param className the name of the class.
     */
    public int getCapacity(String className)
    {
        PoolBuffer pool = (PoolBuffer) poolRepository.get(className);
        if (pool == null)
        {
            /* Check class specific capacity. */
            int capacity = poolCapacity;
            if (capacityMap != null)
            {
                Integer cap = (Integer) capacityMap.get(className);
                if (cap != null)
                {
                    capacity = cap.intValue();
                }
            }
            return capacity;
        }
        else
        {
            return pool.capacity();
        }
    }
    /**
     * Sets the capacity of the pool for a named class.
     * Note that the pool will be cleared after the change.
     *
     * @param className the name of the class.
     * @param capacity the new capacity.
     */
    public void setCapacity(String className, int capacity)
    {
        HashMap repository = poolRepository;
        repository = repository != null ? (HashMap) repository.clone() : new HashMap();
        repository.put(className, new PoolBuffer(capacity));
        poolRepository = repository;
    }
    /**
     * Gets the current size of the pool for a named class.
     *
     * @param className the name of the class.
     */
    public int getSize(String className)
    {
        PoolBuffer pool = (PoolBuffer) poolRepository.get(className);
        return pool != null ? pool.size() : 0;
    }
    /**
     * Clears instances of a named class from the pool.
     *
     * @param className the name of the class.
     */
    public void clearPool(String className)
    {
        HashMap repository = poolRepository;
        if (repository.get(className) != null)
        {
            repository = (HashMap) repository.clone();
            repository.remove(className);
            poolRepository = repository;
        }
    }
    /**
     * Clears all instances from the pool.
     */
    public void clearPool()
    {
        poolRepository = new HashMap();
    }
    /**
     * Polls and recycles an object of the named class from the pool.
     *
     * @param className the name of the class.
     * @param params an array containing the parameters of the constructor.
     * @param signature an array containing the signature of the constructor.
     * @return the object or null.
     * @throws PoolException if recycling fails.
     */
    private Object pollInstance(String className, Object[] params, String[] signature) throws PoolException
    {
        PoolBuffer pool = (PoolBuffer) poolRepository.get(className);
        return pool != null ? pool.poll(params, signature) : null;
    }
    /**
     * Gets the factory service.
     *
     * @return the factory service.
     */
    private FactoryService getFactory()
    {
        return factoryService;
    }
    // ---------------- Avalon Lifecycle Methods ---------------------
    /**
     * Avalon component lifecycle method
     */
    public void configure(Configuration conf)
    {
        final Configuration capacities = conf.getChild(POOL_CAPACITY, false);
        if (capacities != null)
        {
            Configuration defaultConf = capacities.getChild("default");
            int capacity = defaultConf.getValueAsInteger(DEFAULT_POOL_CAPACITY);
            if (capacity <= 0)
            {
                throw new IllegalArgumentException("Capacity must be >0");
            }
            poolCapacity = capacity;
            Configuration[] nameVal = capacities.getChildren();
            for (int i = 0; i < nameVal.length; i++)
            {
                String key = nameVal[i].getName();
                if (!"default".equals(key))
                {
                    capacity = nameVal[i].getValueAsInteger(poolCapacity);
                    if (capacity < 0)
                    {
                        capacity = poolCapacity;
                    }
                    if (capacityMap == null)
                    {
                        capacityMap = new HashMap();
                    }
                    capacityMap.put(key, new Integer(capacity));
                }
            }
        }
    }

    /**
     * Avalon component lifecycle method
     * @avalon.dependency type="org.apache.fulcrum.factory.FactoryService"
     */
    public void service(ServiceManager manager)
    {
        this.manager = manager;
    }

    /**
     * Avalon component lifecycle method
     * Initializes the service by loading default class loaders
     * and customized object factories.
     *
     * @throws InitializationException if initialization fails.
     */
    public void initialize() throws Exception
    {
        try
        {
            factoryService = (FactoryService) manager.lookup(FactoryService.ROLE);
        }
        catch (Exception e)
        {
            throw new Exception(
               "TurbineCryptoService.init: Failed to get a Factory object", e);
        }
    }

    /**
     * Avalon component lifecycle method
     */
    public void dispose()
    {
        if (factoryService != null)
        {
            manager.release(factoryService);
        }
        factoryService = null;
        manager = null;
    }
}
