package org.apache.fulcrum.yaafi.baseservice;


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

import org.apache.fulcrum.yaafi.service.baseservice.BaseServiceImpl;

/**
 * Implementation of the test component.
 *
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 */
public class DerivedServiceImpl
	extends BaseServiceImpl
	implements DerivedService
{
    /**
     * @see org.apache.fulcrum.yaafi.baseservice.DerivedService#test()
     */
    public void test() throws Exception
    {
        this.getLogger().debug(this.toString());
    }
    
    /**
     * @see org.apache.fulcrum.yaafi.service.baseservice.BaseServiceImpl#createAbsoluteFile(java.lang.String)
     */
    public File createAbsoluteFile(String fileName)
    {
        return super.createAbsoluteFile( fileName );
    }
    
    /**
     * @see org.apache.fulcrum.yaafi.service.baseservice.BaseServiceImpl#createAbsolutePath(java.lang.String)
     */
    public String createAbsolutePath(String fileName)
    {
        return super.createAbsolutePath( fileName );
    }
}
