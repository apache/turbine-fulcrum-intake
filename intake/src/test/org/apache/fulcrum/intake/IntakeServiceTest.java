/*
 * Created on Aug 20, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.apache.fulcrum.intake;
import java.io.File;

import org.apache.avalon.framework.component.ComponentException;
import org.apache.fulcrum.intake.model.Group;
import org.apache.fulcrum.testcontainer.BaseUnitTest;
/**
 * @author Eric Pugh
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class IntakeServiceTest extends BaseUnitTest {
	private IntakeService intakeService = null;
	/**
	  * Defines the testcase name for JUnit.
	  *
	  * @param name the testcase's name.
	  */
	public IntakeServiceTest(String name) {
		super(name);
	}
	public static void main(String[] args) {
		junit.textui.TestRunner.run(IntakeServiceTest.class);
	}
	protected void setUp() {
		super.setUp();
		File appData = new File("target/appData.ser");
		if(appData.exists()){
			appData.delete();
		}
		try {
			intakeService = (IntakeService) this.lookup(IntakeService.ROLE);
		} catch (ComponentException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	public void testBasicConfigLoads() throws Exception {
		assertNotNull(intakeService);
		File file = new File("target/appData.ser");
		assertTrue(
			"Make sure serialized data file exists:" + file,
			file.exists());
		Group group = intakeService.getGroup("LoginGroup");
		assertNotNull(group);
		assertEquals("loginGroupKey", group.getGID());
		assertEquals("LoginGroup", group.getIntakeGroupName());

		Group group2 = intakeService.getGroup("AnotherGroup");
		assertNotNull(group2);
		assertEquals("anotherGroupKey", group2.getGID());
		assertEquals("AnotherGroup", group2.getIntakeGroupName());

	}

}
