package org.apache.fulcrum.yaafi.framework.component;

import org.apache.fulcrum.yaafi.framework.role.RoleEntry;

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

/**
 * This interface defines a service component singleton with
 * an arbitrary lifecycle.
 * 
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a> 
 */

public interface ServiceComponent extends ServiceComponentLifecycle
{
    /**
     * Get the unique name of the service component instance.
     * @return the name of the service component
     */
    String getName();

    /**
     * Get the shorthand of the service component instance. The
     * shorthand is usually used to lookup the configuration
     * entries.
     * @return the shorthand of the service component
     */
    String getShorthand();

    /**
     * Returns the associates role entry parsed from the role configuration file.
     * @return the role entry
     */
    RoleEntry getRoleEntry();       
}