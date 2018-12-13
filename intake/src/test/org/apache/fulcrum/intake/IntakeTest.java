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
import org.apache.fulcrum.testcontainer.BaseUnit5Test;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test the facade class for the service
 *
 * @author <a href="epugh@upstate.com">Eric Pugh</a>
 * @author <a href="mailto:jh@byteaction.de">J&uuml;rgen Hoffmann</a>
 * @version $Id$
 */
public class IntakeTest extends BaseUnit5Test
{
	 /**
     * Defines the testcase for JUnit5.
     *
     */
    public IntakeTest(TestInfo testInfo)
    {
    }


    /*
     * This looks strange to me. A test should not bother with explicit initialization.
     * That's the task of the container.
     */
    @Disabled
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

    @Test
    public void testFacadeConfigured() throws Exception
    {
        // this.lookup causes the workflow service to be configured.
        IntakeService is = (IntakeService) this.lookup( IntakeService.ROLE );
        Group group = is.getGroup("LoginGroup");
        assertNotNull(group);
        assertTrue(IntakeServiceFacade.isInitialized());
        group = IntakeServiceFacade.getGroup("LoginGroup");
		assertNotNull(group);
    }

    @Test
    public void testInterfaceMapTo() throws Exception
    {
        IntakeService is = (IntakeService) this.lookup( IntakeService.ROLE );
        Group group = is.getGroup("LoginIfcGroup");
        assertNotNull(group);

        Field<?> userNameField = group.get("Username");

        ParserService ps = (ParserService) this.lookup( ParserService.ROLE );
        ValueParser pp = ps.getParser(DefaultParameterParser.class);

        pp.setString(userNameField.getKey(), "Joe");
        userNameField.init(pp);
        userNameField.validate();

        LoginForm form = new LoginForm();
        group.setProperties(form);

        assertEquals("Joe", form.getUsername(), "User names should be equal");
    }

    @Test
    public void testParserInit() throws Exception
    {
        IntakeService is = (IntakeService) this.lookup( IntakeService.ROLE );
        Group group = is.getGroup("LoginGroup");
        assertNotNull(group);

        Field<?> userNameField = group.get("Username");

        ParserService ps = (ParserService) this.lookup( ParserService.ROLE );
        ValueParser pp = ps.getParser(DefaultParameterParser.class);

        pp.setString("loginGroupKey_0loginUsernameKey", "Joe");
        group.init(pp);

        assertTrue(userNameField.isSet(), "The field should be set");
        assertTrue( userNameField.isValidated(), "The field should be validated");
        assertTrue( userNameField.isValid(), "The field should be valid");
        assertEquals("Joe", userNameField.getValue(), "The field should have the value Joe");
    }

    @Test
    public void testEmptyBooleanField() throws Exception
    {
        IntakeService is = (IntakeService) this.lookup( IntakeService.ROLE );
        Group group = is.getGroup("BooleanTest");
        assertNotNull(group);
        assertTrue(IntakeServiceFacade.isInitialized());
        group = IntakeServiceFacade.getGroup("BooleanTest");
        Field<?> booleanField = group.get("EmptyBooleanTestField");
        assertTrue( (booleanField.getValidator() instanceof BooleanValidator), "The Default Validator of an intake Field type boolean should be BooleanValidator");
        assertFalse( booleanField.isRequired(), "An Empty intake Field type boolean should not be required");
    }

    @Test
    public void testBooleanField() throws Exception
    {
        IntakeService is = (IntakeService) this.lookup( IntakeService.ROLE );
        Group group = is.getGroup("BooleanTest");
        assertNotNull(group);
        assertTrue(IntakeServiceFacade.isInitialized());
        group = IntakeServiceFacade.getGroup("BooleanTest");
        Field<?> booleanField = group.get("BooleanTestField");
        assertTrue( booleanField.getValidator() instanceof BooleanValidator, "The Default Validator of an intake Field type boolean should be BooleanValidator");
        assertFalse( booleanField.isRequired(), "An intake Field type boolean, which is not required, should not be required");
    }

    @Test
    public void testRequiredBooleanField() throws Exception
    {
        IntakeService is = (IntakeService) this.lookup( IntakeService.ROLE );
        Group group = is.getGroup("BooleanTest");
        assertNotNull(group);
        assertTrue(IntakeServiceFacade.isInitialized());
        group = IntakeServiceFacade.getGroup("BooleanTest");
        Field<?> booleanField = group.get("RequiredBooleanTestField");
        assertTrue( booleanField.getValidator() instanceof BooleanValidator, "The Default Validator of an intake Field type boolean should be BooleanValidator");
        assertTrue( booleanField.isRequired(), "An intake Field type boolean, which is required, should be required");
    }

    @Test
    public void testMultiValueField() throws Exception
    {
        IntakeService is = (IntakeService) this.lookup( IntakeService.ROLE );
        Group group = is.getGroup("NumberTest");
        assertNotNull(group);
        Field<?> multiValueField = group.get("MultiIntegerTestField");
        assertTrue( (multiValueField.getValidator() instanceof IntegerValidator), "The Default Validator of an intake Field type int should be IntegerValidator");
        assertTrue( multiValueField.isMultiValued(), "An intake Field type int, which is multiValued, should be multiValued");

        ParserService ps = (ParserService) this.lookup( ParserService.ROLE );
        ValueParser pp = ps.getParser(DefaultParameterParser.class);

        int[] values = new int[] { 1, 2 };
        pp.add("nt_0mitf", values[0]);
        pp.add("nt_0mitf", values[1]);
        group.init(pp);

        assertTrue( multiValueField.isSet(), "The field should be set");
        assertTrue( multiValueField.isValidated(), "The field should be validated");
        assertTrue( multiValueField.isValid(), "The field should be valid");
        assertTrue( Arrays.equals(values, (int[])multiValueField.getValue()), "The field should have the value [1, 2]");
    }

    @Test
    public void testInvalidNumberMessage() throws Exception // TRB-74
    {
        IntakeService is = (IntakeService) this.lookup( IntakeService.ROLE );
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
            assertEquals("Entry was not a valid Integer", ve.getMessage(), "Invalid number message is wrong.");
        }

        Field<?> longField = group.get("EmptyLongTestField");
        try
        {
        	((LongValidator)longField.getValidator()).assertValidity("aa", Locale.US);
            fail("Validator should throw ValidationException");
        }
        catch (ValidationException ve)
        {
            assertEquals("Entry was not a valid Long", ve.getMessage(), "Invalid number message is wrong.");
        }

        Field<?> shortField = group.get("EmptyShortTestField");
        try
        {
        	((ShortValidator)shortField.getValidator()).assertValidity("aa", Locale.US);
            fail("Validator should throw ValidationException");
        }
        catch (ValidationException ve)
        {
            assertEquals("Entry was not a valid Short", ve.getMessage(), "Invalid number message is wrong.");
        }

        Field<?> floatField = group.get("EmptyFloatTestField");
        try
        {
        	((FloatValidator)floatField.getValidator()).assertValidity("aa", Locale.US);
            fail("Validator should throw ValidationException");
        }
        catch (ValidationException ve)
        {
            assertEquals("Entry was not a valid Float", ve.getMessage(), "Invalid number message is wrong.");
        }

        Field<?> doubleField = group.get("EmptyDoubleTestField");
        try
        {
        	((DoubleValidator)doubleField.getValidator()).assertValidity("aa", Locale.US);
            fail("Validator should throw ValidationException");
        }
        catch (ValidationException ve)
        {
            assertEquals("Entry was not a valid Double", ve.getMessage(), "Invalid number message is wrong.");
        }

        Field<?> bigDecimalField = group.get("EmptyBigDecimalTestField");
        try
        {
        	((BigDecimalValidator)bigDecimalField.getValidator()).assertValidity("aa", Locale.US);
            fail("Validator should throw ValidationException");
        }
        catch (ValidationException ve)
        {
            assertEquals( "Entry was not a valid BigDecimal", ve.getMessage(), "Invalid number message is wrong.");
        }

        Field<?> numberField = group.get("NumberTestField");
        try
        {
        	((IntegerValidator)numberField.getValidator()).assertValidity("aa", Locale.US);
            fail("Validator should throw ValidationException");
        }
        catch (ValidationException ve)
        {
            assertEquals("Not a number", ve.getMessage(), "Entry was not a valid BigDecimal");
        }
    }
}
