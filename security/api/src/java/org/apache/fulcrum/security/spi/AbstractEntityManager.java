package org.apache.fulcrum.security.spi;
/*
 *  Copyright 2001-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * This abstract implementation provides most of the functionality that 
 * a manager will need.
 * 
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public abstract class AbstractEntityManager
    extends AbstractManager
    implements Configurable
{
    private String className;
    private static final String CLASS_NAME_KEY = "className";
    
    /** Logging */
    private static Log log = LogFactory.getLog(AbstractEntityManager.class);

        /**
     * Avalon Service lifecycle method
     */
    public void configure(Configuration conf) throws ConfigurationException
    {
        className = conf.getChild(CLASS_NAME_KEY).getValue();
    }
    /**
     * @return Returns the className.
     */
    public String getClassName()
    {
        return className;
    }

    /**
     * @param className The className to set.
     */
    public void setClassName(String className)
    {
        this.className = className;
    }

    
}
