package org.apache.fulcrum.template;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


import java.util.HashMap;

/**
 *  General purpose implemention of the application Context
 *  interface for general application use.  This class should
 *  be used in place of the original Context class.
 *
 *  This implementation uses a HashMap  (@see java.util.HashMap )
 *  for data storage.
 *
 *  This context implementation cannot be shared between threads
 *  without those threads synchronizing access between them, as
 *  the HashMap is not synchronized, nor are some of the fundamentals
 *  of AbstractContext.  If you need to share a Context between
 *  threads with simultaneous access for some reason, please create
 *  your own and extend the interface Context
 *
 *  @see org.apache.velocity.context.Context
 *
 *  @author <a href="mailto:geirm@optonline.net">Geir Magnusson Jr.</a>
 *  @author <a href="mailto:jvanzyl@periapt.com">Jason van Zyl</a>
 *  @author <a href="mailto:fedor.karpelevitch@home.com">Fedor Karpelevitch</a>
 *  @version $Id$
 */
public class DefaultTemplateContext
    implements TemplateContext
{
    /**
     *  storage for key/value pairs
     */
    private HashMap context = new HashMap();

    /**
     * default contructor, does nothing
     * interesting
     */
    public DefaultTemplateContext()
    {
    }

    /**
     * Allow chained contexts.
     */
    public DefaultTemplateContext(TemplateContext context)
    {
        super();

        //!! I realize this is not the most efficient
        // way to do this, but I'm not sure if chained
        // contexts can work with templating solutions
        // other than velocity. I don't see why not right
        // of the bat, but this will work for now.

        Object[] keys = context.getKeys();

        for (int i = 0; i < keys.length; i++)
        {
            put((String) keys[i], context.get((String)keys[i]));
        }
    }

    public void put(String key, Object value)
    {
        context.put(key, value);
    }

    public Object get(String key)
    {
        return context.get(key);
    }

    public Object remove(Object key)
    {
        return context.remove(key);
    }

    public boolean containsKey(Object key)
    {
        return context.containsKey(key);
    }

    public Object[] getKeys()
    {
        return context.keySet().toArray();
    }
}

