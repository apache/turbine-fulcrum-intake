package org.apache.fulcrum.yaafi.framework.role;

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

import java.util.Collection;

/**
 * @author Sigi
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface RoleEntry
{
    /**
     * @return Returns the componentType.
     */
    String getComponentType();

    /**
     * @return Returns the description.
     */
    String getDescription();

    /**
     * @return Returns the implementationClazzName.
     */
    String getImplementationClazzName();

    /**
     * @return Returns the isEarlyInit.
     */
    boolean isEarlyInit();

    /**
     * @return Returns the name.
     */
    String getName();

    /**
     * @return Returns the shorthand.
     */
    String getShorthand();

    /**
     * @return Returns the componentFlavour.
     */
    String getComponentFlavour();

    /**
     * @return Returns the hasProxy.
     */
    boolean hasDynamicProxy();

    /**
     * @param hasProxy The hasProxy to set.
     */
    public void setHasDynamicProxy(boolean hasProxy);

    /**
     * Adds all given interceptors but avoiding duplicates.
     *
     * @param collection the interceptors to be added
     */
    public void addInterceptors( Collection collection );

    /**
     * @return Returns the interceptorList.
     */
    String[] getInterceptorList();

    /**
     * @return the category for creating the logger.
     */
    String getLogCategory();
}
