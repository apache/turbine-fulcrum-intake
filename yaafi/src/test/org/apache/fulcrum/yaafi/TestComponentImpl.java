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
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.Reconfigurable;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;

/**
 * Implementation of the test component.
 *
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 */
public class TestComponentImpl
        extends AbstractLogEnabled
        implements Initializable, Reconfigurable, Parameterizable, Disposable, TestComponent, Contextualizable
{
    public File urnAvaloneHome;
    public File urnAvaloneTemp;
    public String urnAvalonPartition;
    public String urnAvalonName;
    public ClassLoader urnAvalonClassLoader;

    public String foo;
    public String bar;
    public static boolean decomissioned;
    public String componentName;

    public void initialize() throws Exception
    {
        getLogger().debug("initialize() was called");
        decomissioned = false;
    }

    public void contextualize(Context context) throws ContextException
    {
        this.urnAvaloneHome = (File) context.get( "urn:avalon:home" );
        this.urnAvaloneTemp = (File) context.get( "urn:avalon:temp" );
        this.urnAvalonName = (String) context.get( "urn:avalon:name" );
        this.urnAvalonPartition = (String) context.get( "urn:avalon:partition" );
        this.urnAvalonClassLoader = (ClassLoader) context.get( "urn:avalon:classloader" );
    }

    public void configure(Configuration configuration) throws ConfigurationException
    {
        this.foo = configuration.getChild("FOO").getValue("FOO Not Found?!");
    }

    public void reconfigure(Configuration configuration)
        throws ConfigurationException
    {
        this.configure(configuration);
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
        getLogger().debug("urnAvaloneHome = " + this.urnAvaloneHome.toString());
        getLogger().debug("urnAvaloneTemp = " + this.urnAvaloneTemp.toString());
        getLogger().debug("urnAvalonPartition = " + this.urnAvalonPartition);
        getLogger().debug("urnAvalonName = " + this.urnAvalonName);
        getLogger().debug("foo = " + this.foo );
        getLogger().debug("bar = " + this.bar );
    }
}
