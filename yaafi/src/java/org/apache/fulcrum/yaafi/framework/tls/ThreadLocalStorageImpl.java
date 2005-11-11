package org.apache.fulcrum.yaafi.framework.tls;

/*
 * Copyright 2004 Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of {@link org.apache.fulcrum.yaafi.framework.tls.ThreadLocalStorage}.
 *
 * The code was pasted from the Hivemnind container written by
 * Howard Lewis Ship and Harish Krishnaswamy
 *  
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public class ThreadLocalStorageImpl implements ThreadLocalStorage
{
    private static final String INITIALIZED_KEY =
        "$org.apache.fulcrum.yaafi.framework.tls.ThreadLocalStorageImpl#initialized$";

    private CleanableThreadLocal local = new CleanableThreadLocal();

    private static class CleanableThreadLocal extends ThreadLocal
    {
        /**
         * <p>
         * Intializes the variable with a HashMap containing a single Boolean flag to denote the
         * initialization of the variable. 
         */
        protected Object initialValue()
        {
            // NOTE: This is a workaround to circumvent the ThreadLocal behavior.
            // It would be easier if the implementation of ThreadLocal.get() checked for
            // the existence of the thread local map, after initialValue() is evaluated,
            // and used it instead of creating a new map always after initialization (possibly
            // overwriting any variables created from within ThreadLocal.initialValue()).

            Map map = new HashMap();
            map.put(INITIALIZED_KEY, Boolean.TRUE);

            return map;
        }
    }

    /**
     * Gets the thread local variable and registers the listener with ThreadEventNotifier
     * if the thread local variable has been initialized. The registration cannot be done from
     * within {@link CleanableThreadLocal#initialValue()} because the notifier's thread local
     * variable will be overwritten and the listeners for the thread will be lost.
     */
    private Map getThreadLocalVariable()
    {
        Map map = (Map) local.get();
        return map;
    }

    public Object get(String key)
    {
        Map map = getThreadLocalVariable();

        return map.get(key);
    }

    public void put(String key, Object value)
    {
        Map map = getThreadLocalVariable();

        map.put(key, value);
    }

    public boolean containsKey(String key)
    {
        Map map = getThreadLocalVariable();
        
        return map.containsKey(key);
    }
    
    public void clear()
    {
        Map map = (Map) local.get();

        if (map != null)
        {
            map.clear();
        }
    }
}
