package org.apache.fulcrum.yaafi.testcontainer;

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

import junit.framework.TestCase;

import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.service.ServiceException;

/**
 * Base class for unit tests for components. This version doesn't load the container until the
 * first request for a component. This allows the tester to populate the configurationFileName and
 * roleFileName, possible one per test.
 * 
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 */
public abstract class BaseUnitTest extends TestCase
{
    /** YaffiContainer for the components */
    private Container container;
    /** Setup our default configurationFileName */
    private String configurationFileName = "src/test/TestComponentConfig.xml";
    /** Setup our default roleFileName */
    private String roleFileName = "src/test/TestRoleConfig.xml";
    /** Setup our paramterFileName */
    private String parameterFileName = "src/test/TestParameters.properties";

    /**
	 * Gets the configuration file name for the container should use for this test. By default it
	 * is src/test/TestComponentConfig.
	 * 
	 * @param configurationFileName
	 */
    protected void setConfigurationFileName(String configurationFileName)
    {
        this.configurationFileName = configurationFileName;
    }

    /**
	 * Override the role file name for the container should use for this test. By default it is
	 * src/test/TestRoleConfig.
	 * 
	 * @param roleFileName
	 */
    protected void setRoleFileName(String roleFileName)
    {
        this.roleFileName = roleFileName;
    }

    /**
	 * Override the parameter file name for the container should use for this test. By default it is
	 * src/test/TestRoleConfig.
	 * 
	 * @param roleFileName
	 */
    protected void setParameterFileName(String parameterFileName)
    {
        this.parameterFileName = parameterFileName;
    }

    /**
	 * Constructor for test.
	 * 
	 * @param testName name of the test being executed
	 */
    public BaseUnitTest(String testName)
    {
        super(testName);
    }
    
    /**
	 * Clean up after each test is run.
	 */
    protected void tearDown() throws Exception
    {
        if (this.container != null)
        {
            this.container.dispose();
        }
        this.container = null;
    }
    /**
	 * Gets the configuration file name for the container should use for this test.
	 * 
	 * @return The filename of the configuration file
	 */
    protected String getConfigurationFileName()
    {
        return this.configurationFileName;
    }
    /**
	 * Gets the role file name for the container should use for this test.
	 * 
	 * @return The filename of the role configuration file
	 */
    protected String getRoleFileName()
    {
        return this.roleFileName;
    }
    /**
	 * Gets the parameter file name for the container should use for this test.
	 * 
	 * @return The filename of the parameter file
	 */
    protected String getParameterFileName()
    {
        return this.parameterFileName;
    }    
    /**
	 * Returns an instance of the named component. Starts the container if it hasn't been started.
	 * 
	 * @param roleName Name of the role the component fills.
	 * @throws ComponentException generic exception
	 */
    protected Object lookup(String roleName) throws ComponentException
    {
        if (this.container == null)
        {
            this.container = new Container();
            this.container.startup(getConfigurationFileName(), getRoleFileName(), getParameterFileName());
        }
        return this.container.lookup(roleName);
    }
    /**
	 * Releases the component
	 * 
	 * @param component
	 */
    protected void release(Component component)
    {
        if (this.container != null)
        {
            this.container.release(component);
        }
    }
    /**
	 * Releases the component
	 * 
	 * @param component
	 */
    protected void release(Object component)
    {
        if (this.container != null)
        {
            this.container.release(component);
        }
    }
    
    /**
     * Decommision the service
     * @param name the name of the service
     */
    protected void decommision( String name )
    	throws ServiceException, Exception
    {
        if (this.container != null)
        {
            this.container.decommision( name );
        }
    }
}
