package org.apache.fulcrum.template.velocity;


/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import org.apache.fulcrum.template.TemplateContext;

import org.apache.velocity.context.AbstractContext;

/**
 * An adapter for Fulcrum's {@link TemplateContext}. Allows for easy processing
 * of TemplateContext objects by Velocity. This class extends
 * {@link AbstractContext}, so it supports event cartridge handling.
 *
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @author <a href="mailto:james@jamestaylor.org">James Taylor</a>
 * @see org.apache.fulcrum.template.TemplateContext
 * @see org.apache.velocity.context.Context
 */
public class ContextAdapter extends AbstractContext
{
    private TemplateContext context;

    public ContextAdapter(TemplateContext context)
    {
        this.context = context;
    }

    /**
     * @see AbstractContext#internalGet
     */
    public Object internalGet( String key )
    {
        return context.get( key );
    }

    /**
     * @see AbstractContext#internalPut
     */
    public Object internalPut( String key, Object value )
    {
        context.put( key, value );

        return null;
    }

    /**
     * @see AbstractContext#internalContainsKey
     */
    public  boolean internalContainsKey(Object key)
    {
        return context.containsKey( key );
    }

    /**
     * @see AbstractContext#internalGetKeys
     */
    public  Object[] internalGetKeys()
    {
        return context.getKeys();
    }

    /**
     * @see AbstractContext#internalRemove
     */
    public  Object internalRemove(Object key)
    {
        return context.remove( key );
    }
}
