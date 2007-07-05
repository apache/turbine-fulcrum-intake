package org.apache.fulcrum.yaafi.framework.configuration;

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

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;

/**
 * Implementation of the test component.
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 */
public class TestComponentImpl
        extends AbstractLogEnabled
        implements TestComponent, Configurable
{
    public String foo;
    public String foobar;

    public void configure(Configuration configuration) throws ConfigurationException
    {
        this.foo = configuration.getChild("FOO").getValue("FOO Not Found?!");
        this.foobar = configuration.getChild("FOOBAR").getValue("FOOBAR Not Found?!");
    }

    /**
     * @return Returns the foo.
     */
    public String getFoo()
    {
        return foo;
    }

    /**
     * @return Returns the bar.
     */
    public String getFooBar()
    {
        return foobar;
    }
}
