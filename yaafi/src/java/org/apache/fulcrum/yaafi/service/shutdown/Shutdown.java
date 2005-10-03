package org.apache.fulcrum.yaafi.service.shutdown;

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

    /** use System.exit() to shutdown the JVM */
    private boolean useSystemExit;

    /**
     * Constructor.
     * @param disposable the instance to be disposed
     * @param useSystemExit call System.exit()?
     *
     */
    public Shutdown( Disposable disposable, boolean useSystemExit )
    {
        this.disposable = disposable;
        this.useSystemExit = useSystemExit;
    }

    /**
     * @see java.lang.Runnable#run()
     */
    public void run()
    {
        try
        {
            this.disposable.dispose();
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }

        this.disposable = null;

        if( this.useSystemExit )
        {
            System.exit(0);
        }
    }
}