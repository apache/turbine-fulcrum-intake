package org.apache.fulcrum.naming;


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


import javax.naming.Context;

/**
 * Implementations of the NamingService interface provide JNDI naming
 * contexts.
 *
 * @author <a href="mailto:greg@shwoop.com">Greg Ritter</a>
 * @version $Id$
 */
public interface NamingService
{
    /** Avalon role - used to id the component within the manager */
    String ROLE = NamingService.class.getName();

    /**
     * Return the Context with the specified name.
     *
     * @param name The name of the context.
     * @return The context with the specified name, or null if no context
     * exists with that name.
     */
    public Context getContext(String name);
}
