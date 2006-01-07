package org.apache.fulcrum.script;

/*
 * Copyright 2005 Apache Software Foundation
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

import java.io.File;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceManager;

/**
 * Holder class for Avalon related artifacts to be passed to a script.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */
public interface ScriptAvalonContext
{
    /**
     * @return Returns the configuration
     */
    Configuration getConfiguration();

    /**
     * @return Returns the context.
     */
    Context getContext();

    /**
     * @return Returns the logger.
     */
    Logger getLogger();

    /**
     * @return Returns the serviceManager.
     */
    ServiceManager getServiceManager();

    /**
     * @return Returns the parameters.
     */
    Parameters getParameters();

    /**
     * @return Returns the Avalon application directory
     */
    File getApplicationDir() throws ContextException;

    /**
     * @return Returns the Avalon application directory
     */
    File getTempDir() throws ContextException;
}
