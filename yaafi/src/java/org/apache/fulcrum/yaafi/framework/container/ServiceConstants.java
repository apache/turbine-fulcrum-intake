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
 * Commonly used constants.
 * 
 *  @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public interface ServiceConstants extends AvalonYaafiConstants
{
    /** The name of this component */
    String ROLE_NAME = "fulcrum-yaafi";

    /** The default implementation class for YAAFI */    
    String CLAZZ_NAME = "org.apache.fulcrum.yaafi.framework.container.ServiceContainerImpl";
    
	/////////////////////////////////////////////////////////////////////////
	// Entried for the YAAFI configuration files
	/////////////////////////////////////////////////////////////////////////

	/** property to lookup the container type */
	String CONTAINERTYPE_CONFIG_KEY = "containerType";

	/** property to lookup the implementation class of the container */
	String CONTAINERCLAZZNAME_CONFIG_KEY = "containerClazzName";

	/** property to lookup the component config file */
	String COMPONENT_CONFIG_KEY = "componentConfiguration";

	/** property to lookup the component role file */
	String COMPONENT_ROLE_KEYS = "componentRoles";

	/** property to lookup the parameters file */
	String COMPONENT_PARAMETERS_KEY = "parameters";

	/** property to lookup the the decryption handling */
	String COMPONENT_ISENCRYPTED_KEY = "isEncrypted";

	/** property to lookup the the lcoation */
	String COMPONENT_LOCATION_KEY = "location";

	/////////////////////////////////////////////////////////////////////////
	// Default values for YAAFI configuration files
	/////////////////////////////////////////////////////////////////////////
	
	/** default file name of the component config file */
	String COMPONENT_CONFIG_VALUE = "/componentConfiguration.xml";

	/** default file name of the component role file */
	String COMPONENT_ROLE_VALUE = "/componentRoles.xml";

	/** default file name of the parameters file */
	String COMPONENT_PARAMETERS_VALUE = "/parameters.properties";
	
	/** default value for container type */
	String COMPONENT_CONTAINERTYPE_VALUE = "merlin";	
	
}