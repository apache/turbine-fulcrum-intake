package org.apache.fulcrum.security.model.turbine;
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

import org.apache.fulcrum.security.entity.Group;
import org.apache.fulcrum.security.entity.Role;
import org.apache.fulcrum.security.entity.User;
import org.apache.fulcrum.security.model.dynamic.DynamicModelManager;
import org.apache.fulcrum.security.util.DataBackendException;
import org.apache.fulcrum.security.util.UnknownEntityException;

/**
 * Describes all the relationships between entities in the "Turbine" model.
 * 
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public interface TurbineModelManager extends DynamicModelManager
{

    /**
    	* The name of the <a href="#global">global group</a>
    	*/
    String GLOBAL_GROUP_NAME = "global";

    /**
    	* Provides a reference to the Group object that represents the
    	* <a href="#global">global group</a>.
    	*
    	* @return A Group object that represents the global group.
    	*/
    Group getGlobalGroup() throws DataBackendException;

    /**
     * Grant an User a Role in a Group.
     * 
     * @param user the user.
     * @param group the group.
     * @param role the role.
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws UnknownEntityException if user account, group or role is not present.
     */
    void grant(User user, Group group, Role role)
        throws DataBackendException, UnknownEntityException;
    /**
     * Revoke a Role in a Group from an User.
     * 
     * @param user the user.
     * @param group the group.
     * @param role the role.
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws UnknownEntityException if user account, group or role is not present.
     */
    void revoke(User user, Group group, Role role)
        throws DataBackendException, UnknownEntityException;

}
