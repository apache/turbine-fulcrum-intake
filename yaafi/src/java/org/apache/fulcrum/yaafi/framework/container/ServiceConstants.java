package org.apache.fulcrum.yaafi.framework.container;

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

/**
 * This service allows access to avalon components.
 */

public interface ServiceConstants
{
    /** The name of this component */
    String ROLE_NAME = "YAAFI";
    
	/** default file name of the component config file */
	String COMPONENT_CONFIG_VALUE = "/componentConfiguration.xml";

	/** default file name of the component role file */
	String COMPONENT_ROLE_VALUE = "/componentRoles.xml";

	/** default file name of the parameters file */
	String COMPONENT_PARAMETERS_VALUE = "/parameters.properties";
	
	/** Key used in the context for defining the application root */
	String COMPONENT_APP_ROOT = "componentAppRoot";

    /** Alternate Merlin Friendly Key used in the context for defining the application root */
    String URN_AVALON_HOME = "urn:avalon:home";

    /** Alternate Merlin Friendly Key used in the context for defining the temp root */
    String URN_AVALON_TEMP = "urn:avalon:temp";    	
    
	/** property to lookup the component config file */
	String COMPONENT_CONFIG_KEY = "componentConfiguration";

	/** property to lookup the component role file */
	String COMPONENT_ROLE_KEYS = "componentRoles";

	/** property to lookup the parameters file */
	String COMPONENT_PARAMETERS_KEY = "parameters";
}