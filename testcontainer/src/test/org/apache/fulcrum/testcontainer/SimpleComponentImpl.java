package org.apache.fulcrum.testcontainer;

/*
 * Copyright 2004 The Apache Software Foundation.
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

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;

/**
 * Interface of the component
 *
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 */
public class SimpleComponentImpl
        extends AbstractLogEnabled
        implements Initializable, Disposable, SimpleComponent,
            Contextualizable
{
    private String appRoot;
    private String appRoot2;

    public void initialize() throws Exception
    {
    }

    public void dispose()
    {
    }

    public void test()
    {
        setupLogger(this, "SimpleComponent");
        getLogger().debug("test");
        getLogger().debug("ComponentAppRoot = "+appRoot);
        getLogger().debug("ComponentAppRoot2 = "+appRoot2);
    }

    public void contextualize(Context context) throws ContextException
    {
        appRoot = (String) context.get("componentAppRoot");
        appRoot2 = (String) context.get("urn:avalon:home");
    }
    /**
     * @return Returns the appRoot.
     */
    public String getAppRoot()
    {
        return appRoot;
    }

    /**
     * @return Returns the appRoot2.
     */
    public String getAppRoot2()
    {
        return appRoot2;
    }

}