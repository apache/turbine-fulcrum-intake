package org.apache.fulcrum.intake;

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

import java.util.Arrays;
import java.util.Locale;

import org.apache.fulcrum.intake.model.Field;
import org.apache.fulcrum.intake.model.Group;
import org.apache.fulcrum.intake.test.LoginForm;
import org.apache.fulcrum.intake.validator.BigDecimalValidator;
import org.apache.fulcrum.intake.validator.BooleanValidator;
import org.apache.fulcrum.intake.validator.DoubleValidator;
import org.apache.fulcrum.intake.validator.FloatValidator;
import org.apache.fulcrum.intake.validator.IntegerValidator;
import org.apache.fulcrum.intake.validator.LongValidator;
import org.apache.fulcrum.intake.validator.ShortValidator;
import org.apache.fulcrum.intake.validator.ValidationException;
import org.apache.fulcrum.parser.DefaultParameterParser;
import org.apache.fulcrum.parser.ParserService;
import org.apache.fulcrum.parser.ValueParser;
import org.apache.fulcrum.testcontainer.BaseUnitTest;
/**
 * Test the facade class for the service
 *
 * @author <a href="epugh@upstate.com">Eric Pugh</a>
 * @author <a href="mailto:jh@byteaction.de">J&uuml;rgen Hoffmann</a>
 * @version $Id$
 */
public class IntakeTest extends BaseUnitTest
{
    /**
     * Defines the testcase name for JUnit.
     *
     * @param name the testcase's name.
     */
    public IntakeTest(String name)
    {
        super(name);
    }


    /*
     * This looks strange to me. A test should not bother with explicit initialization.
     * That's the task of the container.
     */
    public void OFFtestFacadeNotConfigured() throws Exception
    {
		assertFalse(IntakeServiceFacade.isInitialized());
        try
        {
            IntakeServiceFacade.getGroup("test");
        }
        catch (RuntimeException re)
        {
            //good;
        }
    }

    public void testFacadeConfigured() throws Exception
    {
        // this.lookup causes the workflow service to be configured.
        IntakeService is = (IntakeService) this.resolve( IntakeService.ROLE );
        Group group = is.getGroup("LoginGroup");
        assertNotNull(group);
        assertTrue(IntakeServiceFacade.isInitialized());
        group = IntakeServiceFacade.getGroup("LoginGroup");
		assertNotNull(group);
    }

    public void testInterfaceMapTo() throws Exception
    {
        IntakeService is = (IntakeService) this.resolve( IntakeService.ROLE );
        Group group = is.getGroup("LoginIfcGroup");
        assertNotNull(group);

        Field<?> userNameField = group.get("Username");

        ParserService ps = (ParserService) this.resolve( ParserService.ROLE );
        ValueParser pp = ps.getParser(DefaultParameterParser.class);

        pp.setString(userNameField.getKey(), "Joe");
        userNameField.init(pp);
        userNameField.validate();

        LoginForm form = new LoginForm();
        group.setProperties(form);

        assertEquals("User names should be equal", "Joe", form.getUsername());
    }

    public void testParserInit() throws Exception
    {
        IntakeService is = (IntakeService) this.resolve( IntakeService.ROLE );
        Group group = is.getGroup("LoginGroup");
        assertNotNull(group);

        Field<?> userNameField = group.get("Username");

        ParserService ps = (ParserService) this.resolve( ParserService.ROLE );
        ValueParser pp = ps.getParser(DefaultParameterParser.class);

        pp.setString("loginGroupKey_0loginUsernameKey", "Joe");
        group.init(pp);

        assertTrue("The field should be set", userNameField.isSet());
        assertTrue("The field should be validated", userNameField.isValidated());
        assertTrue("The field should be valid", userNameField.isValid());
        assertEquals("The field should have the value Joe", "Joe", userNameField.getValue());
    }

    public void testEmptyBooleanField() throws Exception
    {
        IntakeService is = (IntakeService) this.resolve( IntakeService.ROLE );
        Group group = is.getGroup("BooleanTest");
        assertNotNull(group);
        assertTrue(IntakeServiceFacade.isInitialized());
        group = IntakeServiceFacade.getGroup("BooleanTest");
        Field<?> booleanField = group.get("EmptyBooleanTestField");
        assertTrue("The Default Validator of an intake Field type boolean should be BooleanValidator", (booleanField.getValidator() instanceof BooleanValidator));
        assertFalse("An Empty intake Field type boolean should not be required", booleanField.isRequired());
    }

    public void testBooleanField() throws Exception
    {
        IntakeService is = (IntakeService) this.resolve( IntakeService.ROLE );
        Group group = is.getGroup("BooleanTest");
        assertNotNull(group);
        assertTrue(IntakeServiceFacade.isInitialized());
        group = IntakeServiceFacade.getGroup("BooleanTest");
        Field<?> booleanField = group.get("BooleanTestField");
        assertTrue("The Default Validator of an intake Field type boolean should be BooleanValidator", (booleanField.getValidator() instanceof BooleanValidator));
        assertFalse("An intake Field type boolean, which is not required, should not be required", booleanField.isRequired());
    }

    public void testRequiredBooleanField() throws Exception
    {
        IntakeService is = (IntakeService) this.resolve( IntakeService.ROLE );
        Group group = is.getGroup("BooleanTest");
        assertNotNull(group);
        assertTrue(IntakeServiceFacade.isInitialized());
        group = IntakeServiceFacade.getGroup("BooleanTest");
        Field<?> booleanField = group.get("RequiredBooleanTestField");
        assertTrue("The Default Validator of an intake Field type boolean should be BooleanValidator", (booleanField.getValidator() instanceof BooleanValidator));
        assertTrue("An intake Field type boolean, which is required, should be required", booleanField.isRequired());
    }

    public void testMultiValueField() throws Exception
    {
        IntakeService is = (IntakeService) this.resolve( IntakeService.ROLE );
        Group group = is.getGroup("NumberTest");
        assertNotNull(group);
        Field<?> multiValueField = group.get("MultiIntegerTestField");
        assertTrue("The Default Validator of an intake Field type int should be IntegerValidator", (multiValueField.getValidator() instanceof IntegerValidator));
        assertTrue("An intake Field type int, which is multiValued, should be multiValued", multiValueField.isMultiValued());

        ParserService ps = (ParserService) this.resolve( ParserService.ROLE );
        ValueParser pp = ps.getParser(DefaultParameterParser.class);

        int[] values = new int[] { 1, 2 };
        pp.add("nt_0mitf", values[0]);
        pp.add("nt_0mitf", values[1]);
        group.init(pp);

        assertTrue("The field should be set", multiValueField.isSet());
        assertTrue("The field should be validated", multiValueField.isValidated());
        assertTrue("The field should be valid", multiValueField.isValid());
        assertTrue("The field should have the value [1, 2]", Arrays.equals(values, (int[])multiValueField.getValue()));
    }

    public void testInvalidNumberMessage() throws Exception // TRB-74
    {
        IntakeService is = (IntakeService) this.resolve( IntakeService.ROLE );
        Group group = is.getGroup("NumberTest");
        assertNotNull(group);

        Field<?> intField = group.get("EmptyIntegerTestField");
        try
        {
            ((IntegerValidator)intField.getValidator()).assertValidity("aa", Locale.US);
            fail("Validator should throw ValidationException");
        }
        catch (ValidationException ve)
        {
            assertEquals("Invalid number message is wrong.", "Entry was not a valid Integer", ve.getMessage());
        }

        Field<?> longField = group.get("EmptyLongTestField");
        try
        {
        	((LongValidator)longField.getValidator()).assertValidity("aa", Locale.US);
            fail("Validator should throw ValidationException");
        }
        catch (ValidationException ve)
        {
            assertEquals("Invalid number message is wrong.", "Entry was not a valid Long", ve.getMessage());
        }

        Field<?> shortField = group.get("EmptyShortTestField");
        try
        {
        	((ShortValidator)shortField.getValidator()).assertValidity("aa", Locale.US);
            fail("Validator should throw ValidationException");
        }
        catch (ValidationException ve)
        {
            assertEquals("Invalid number message is wrong.", "Entry was not a valid Short", ve.getMessage());
        }

        Field<?> floatField = group.get("EmptyFloatTestField");
        try
        {
        	((FloatValidator)floatField.getValidator()).assertValidity("aa", Locale.US);
            fail("Validator should throw ValidationException");
        }
        catch (ValidationException ve)
        {
            assertEquals("Invalid number message is wrong.", "Entry was not a valid Float", ve.getMessage());
        }

        Field<?> doubleField = group.get("EmptyDoubleTestField");
        try
        {
        	((DoubleValidator)doubleField.getValidator()).assertValidity("aa", Locale.US);
            fail("Validator should throw ValidationException");
        }
        catch (ValidationException ve)
        {
            assertEquals("Invalid number message is wrong.", "Entry was not a valid Double", ve.getMessage());
        }

        Field<?> bigDecimalField = group.get("EmptyBigDecimalTestField");
        try
        {
        	((BigDecimalValidator)bigDecimalField.getValidator()).assertValidity("aa", Locale.US);
            fail("Validator should throw ValidationException");
        }
        catch (ValidationException ve)
        {
            assertEquals("Invalid number message is wrong.", "Entry was not a valid BigDecimal", ve.getMessage());
        }

        Field<?> numberField = group.get("NumberTestField");
        try
        {
        	((IntegerValidator)numberField.getValidator()).assertValidity("aa", Locale.US);
            fail("Validator should throw ValidationException");
        }
        catch (ValidationException ve)
        {
            assertEquals("Invalid number message is wrong.", "Not a number", ve.getMessage());
        }
    }
}
