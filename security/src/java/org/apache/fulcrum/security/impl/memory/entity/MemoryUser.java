/*
 * Created on Aug 21, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.apache.fulcrum.security.impl.memory.entity;
import org.apache.fulcrum.security.entity.Group;
import org.apache.fulcrum.security.entity.impl.UserImpl;
import org.apache.fulcrum.security.util.GroupSet;
/**
 * @author Eric Pugh
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class MemoryUser extends UserImpl
{
   
    private GroupSet groups = new GroupSet();
        /**
     * @return
     */
    public GroupSet getGroups()
    {
        return groups;
    }

    /**
     * @param groups
     */
    public void setGroups(GroupSet groups)
    {
        this.groups = groups;
    }



    
	public void removeGroup(Group group){
		getGroups().remove(group);
	}    
	public void addGroup(Group group)
	 {
		 getGroups().add(group);
	 }

}
