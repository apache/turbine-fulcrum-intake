package org.apache.fulcrum.osworkflow.example.modules.actions;

import org.apache.turbine.modules.actions.VelocityAction;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;



/**
 * @author     <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @created    October 11, 2002
 */
public class Index extends VelocityAction {

	/**
	 *  Default action is to load up the default template and list out the locations to go to:
	 <ol><li>Inventory Add Request<li>Inventory Delete Request<li>General Admin of Request</ol>
	 *
	 * @param  data           Current RunData information
	 * @param  context        Context to populate
	 * @exception  Exception  Thrown on error
	 */
	public void doPerform(RunData data, Context context) throws Exception {

		System.out.println("Doperform called");
		data.setScreenTemplate("Index.vm");

	}

	
	

}
