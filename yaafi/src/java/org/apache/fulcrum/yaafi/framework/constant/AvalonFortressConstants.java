package org.apache.fulcrum.yaafi.framework.constant;

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
 * Commonly used constants.
 *
 *  @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public interface AvalonFortressConstants
{
    /** The Fortress Container */
    String AVALON_CONTAINER_FORTESS = "fortress";

    /////////////////////////////////////////////////////////////////////////
    // Fortress keys for Context
    /////////////////////////////////////////////////////////////////////////

    /** Fortress alias for "urn:avalon:partition" */
    String FORTRESS_COMPONENT_ID = "component.id";

    /** Fortress alias for "urn:avalon:name" */
    String FORTRESS_COMPONENT_LOGGER = "component.logger";

    /** Fortress alias for "urn:avalon:home" */
    String FORTRESS_CONTEXT_ROOT = "context-root";

    /** Fortress alias for "urn:avalon:temp" */
    String FORTRESS_IMPL_WORKDIR = "impl.workDir";
}