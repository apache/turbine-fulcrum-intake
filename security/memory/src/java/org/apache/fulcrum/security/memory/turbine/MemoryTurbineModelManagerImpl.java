package org.apache.fulcrum.security.memory.turbine;
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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.security.entity.Group;
import org.apache.fulcrum.security.entity.Role;
import org.apache.fulcrum.security.entity.User;
import org.apache.fulcrum.security.memory.dynamic.MemoryModelManagerImpl;
import org.apache.fulcrum.security.model.turbine.TurbineModelManager;
import org.apache.fulcrum.security.util.DataBackendException;
import org.apache.fulcrum.security.util.EntityExistsException;
import org.apache.fulcrum.security.util.UnknownEntityException;


/**
 * This implementation keeps all objects in memory.  This is mostly meant to help
 * with testing and prototyping of ideas.
 *
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class MemoryTurbineModelManagerImpl
    extends MemoryModelManagerImpl
    implements TurbineModelManager
{
    /** Logging */
    private static Log log = LogFactory.getLog(MemoryModelManagerImpl.class);

    /**
      * Provides a reference to the Group object that represents the
      * <a href="#global">global group</a>.
      *
      * @return A Group object that represents the global group.
      */
    public Group getGlobalGroup() throws DataBackendException
    {
        Group g = null;
        try
        {
            g = getGroupManager().getGroupByName(GLOBAL_GROUP_NAME);
        }
        catch (UnknownEntityException uee)
        {
            g = getGroupManager().getGroupInstance(GLOBAL_GROUP_NAME);
            try
            {
				getGroupManager().addGroup(g);
            }
            catch (EntityExistsException eee)
            {
                throw new DataBackendException(eee.getMessage(), eee);
            }

        }
        return g;
    }

    public void grant(User user, Group group, Role role){
        
    }
	public void revoke(User user, Group group, Role role){
    
	}
   

}
