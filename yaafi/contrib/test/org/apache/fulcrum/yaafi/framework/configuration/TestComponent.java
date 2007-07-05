package org.apache.fulcrum.yaafi.framework.configuration;

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

/**
 * This is a simple component that is used to test the configuration resolver
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 */
public interface TestComponent
{
    /** the role name of the service */
    static final String ROLE = "org.apache.fulcrum.yaafi.framework.configuration.TestComponent";

    /**
     * @return Returns the foo.
     */
    String getFoo();

    /**
     * @return Returns the foo.
     */
    String getFooBar();
}
