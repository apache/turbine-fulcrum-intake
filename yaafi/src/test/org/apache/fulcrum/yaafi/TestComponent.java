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

import org.apache.avalon.framework.component.Component;

/**
 * This is a simple component that is only used to test the avalon component
 * service.
 *
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 */
public interface TestComponent
        extends Component
{
    /** the role name of the service */
    static final String ROLE = "org.apache.fulcrum.yaafi.TestComponent";

    /** the service method to be called during testing */
    void test();

    /**
     * @return Returns the bar.
     */
    String getBar();

    /**
     * @return Returns the componentName.
     */
    String getComponentName();

    /**
     * @return Returns the decomissioned.
     */
    boolean isDecomissioned();

    /**
     * @return Returns the foo.
     */
    String getFoo();

    /**
     * @return Returns the urnAvalonClassLoader.
     */
    ClassLoader getUrnAvalonClassLoader();

    /**
     * @return Returns the urnAvaloneHome.
     */
    File getUrnAvaloneHome();

    /**
     * @return Returns the urnAvaloneTemp.
     */
    File getUrnAvaloneTemp();

    /**
     * @return Returns the urnAvalonName.
     */
    String getUrnAvalonName();

    /**
     * @return Returns the urnAvalonPartition.
     */
    String getUrnAvalonPartition();

    /**
     * Alwayas throws an exception
     */
    void createException(String reason, Object caller);
    
    /**
     * Do something for the given time
     * @param millis
     */
    public void doSomething(long millis, Object arg);
}
