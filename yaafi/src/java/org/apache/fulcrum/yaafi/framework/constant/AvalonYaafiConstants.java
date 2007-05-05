package org.apache.fulcrum.yaafi.framework.constant;

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
 * Commonly used constants for YAAFI. Basically we are mimicking a
 * Merlin container fow whatever it is worth.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public interface AvalonYaafiConstants
{
    /** The YAAFI Container */
    String AVALON_CONTAINER_YAAFI = "yaafi";

    /////////////////////////////////////////////////////////////////////////
    // Merlin keys for Context
    /////////////////////////////////////////////////////////////////////////

    /** define the Merlin application root (File) */
    String URN_AVALON_HOME = "urn:avalon:home";

    /** define the Merlin temporary directory (File) */
    String URN_AVALON_TEMP = "urn:avalon:temp";

    /** define the Merlin component name (String) */
    String URN_AVALON_NAME = "urn:avalon:name";

    /** define the Merlin partition name (String) */
    String URN_AVALON_PARTITION = "urn:avalon:partition";

    /** define the Merlin component classloader (ClassLoader) */
    String URN_AVALON_CLASSLOADER = "urn:avalon:classloader";

    /////////////////////////////////////////////////////////////////////////
    // YAAFI keys for Context
    /////////////////////////////////////////////////////////////////////////

    /** define the Merlin component classloader (ClassLoader) */
    String URN_YAAFI_KERNELLOCK = "urn:yaafi:kernellock";

    /////////////////////////////////////////////////////////////////////////
    // ECM keys for Context
    /////////////////////////////////////////////////////////////////////////

    /** this is only supplied for backward compatibilty with ECM */
    String COMPONENT_APP_ROOT  = "componentAppRoot";

}
