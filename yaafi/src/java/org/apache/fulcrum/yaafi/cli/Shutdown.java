package org.apache.fulcrum.yaafi.cli;

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

import org.apache.avalon.framework.activity.Disposable;

/**
 * This class process the shutdown notification from the JVM.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public class Shutdown implements Runnable
{
    /** The service manager tobe disposed */
    private Disposable disposable;

    /**
     * Constructor
     * @param disposable The service manager to be disposed
     */
    public Shutdown( Disposable disposable )
    {
        this.disposable = disposable;
    }

    /**
     * @see java.lang.Runnable#run()
     */
    public void run()
    {
        this.disposable.dispose();
        this.disposable = null;
    }
}