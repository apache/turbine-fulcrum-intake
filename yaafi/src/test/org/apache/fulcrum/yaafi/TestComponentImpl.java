package org.apache.fulcrum.yaafi;


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


import java.io.File;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;

/**
 * Implementation of the test component.
 *
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 */
public class TestComponentImpl
        extends AbstractLogEnabled
        implements Initializable, Configurable, Parameterizable, Disposable, TestComponent, Contextualizable
{
    public File appRoot;
    public String foo;
    public String bar;
    public static boolean decomissioned;

    public void initialize() throws Exception
    {
        getLogger().debug("initialize() was called");
        decomissioned = false;
    }

    public void contextualize(Context context) throws ContextException
    {
        appRoot 	= (File) context.get( "urn:avalon:home" );
    }

    public void configure(Configuration configuration) throws ConfigurationException
    {
        this.foo = configuration.getChild("FOO").getValue("FOO Not Found?!");
    }

    public void parameterize(Parameters parameters) throws ParameterException
    {
        this.bar = parameters.getParameter("BAR", "BAR Not Found?!");
    }

    public void dispose()
    {
        getLogger().debug("dispose() was called");
        TestComponentImpl.decomissioned=true;
    }

    public void test()
    {
        setupLogger(this, "TestComponent");
        getLogger().debug("TestComponent.test() was called");
        getLogger().debug("componentAppRoot = " + appRoot.getAbsolutePath());
        getLogger().debug("foo = " + this.foo );
        getLogger().debug("bar = " + this.bar );
    }
}
