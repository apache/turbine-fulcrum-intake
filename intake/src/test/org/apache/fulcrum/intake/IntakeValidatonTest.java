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

import java.util.Locale;

import org.apache.fulcrum.intake.model.Field;
import org.apache.fulcrum.intake.model.Group;
import org.apache.fulcrum.intake.validator.BigDecimalValidator;
import org.apache.fulcrum.intake.validator.DateRangeValidator;
import org.apache.fulcrum.intake.validator.DateStringValidator;
import org.apache.fulcrum.intake.validator.DoubleValidator;
import org.apache.fulcrum.intake.validator.FloatValidator;
import org.apache.fulcrum.intake.validator.IntegerRangeValidator;
import org.apache.fulcrum.intake.validator.IntegerValidator;
import org.apache.fulcrum.intake.validator.LongValidator;
import org.apache.fulcrum.intake.validator.ShortValidator;
import org.apache.fulcrum.intake.validator.ValidationException;
import org.apache.fulcrum.parser.DefaultParameterParser;
import org.apache.fulcrum.parser.ParserService;
import org.apache.fulcrum.parser.ValueParser;
import org.apache.fulcrum.testcontainer.BaseUnitTest;
/**
 * Test the validators
 *
 * @author <a href="tv@apache.org">Thomas Vandahl</a>
 */
public class IntakeValidatonTest extends BaseUnitTest
{
    /**
     * Defines the testcase name for JUnit.
     *
     * @param name the testcase's name.
     */
    public IntakeValidatonTest(String name)
    {
        super(name);
    }

    public void testStringValidation() throws Exception
    {
        IntakeService is = (IntakeService) this.resolve( IntakeService.ROLE );
        Group group = is.getGroup("ValidationTest");
        assertNotNull(group);

        Field<?> stringField = group.get("StringTestField");
        try
        {
            stringField.getValidator().assertValidity((String)null);
            fail("Validator should throw ValidationException");
        }
        catch (ValidationException ve)
        {
            assertEquals("Required", ve.getMessage());
        }

        try
        {
            stringField.getValidator().assertValidity("A");
            fail("Validator should throw ValidationException");
        }
        catch (ValidationException ve)
        {
            assertEquals("Too short", ve.getMessage());
        }

        try
        {
            stringField.getValidator().assertValidity("ABCDEFGHIJK");
            fail("Validator should throw ValidationException");
        }
        catch (ValidationException ve)
        {
            assertEquals("Too long", ve.getMessage());
        }

        try
        {
            stringField.getValidator().assertValidity("AbCdEfG");
            fail("Validator should throw ValidationException");
        }
        catch (ValidationException ve)
        {
            assertEquals("Uppercase letters only", ve.getMessage());
        }

        try
        {
            stringField.getValidator().assertValidity("ABCDE");
        }
        catch (ValidationException ve)
        {
            fail("Validator should not throw ValidationException");
        }
    }

    public void testBooleanValidation() throws Exception
    {
        IntakeService is = (IntakeService) this.resolve( IntakeService.ROLE );
        Group group = is.getGroup("ValidationTest");
        assertNotNull(group);

        Field<?> booleanField = group.get("BooleanTestField");
        try
        {
            booleanField.getValidator().assertValidity((String)null);
            fail("Validator should throw ValidationException");
        }
        catch (ValidationException ve)
        {
            assertEquals("Required", ve.getMessage());
        }

        try
        {
            booleanField.getValidator().assertValidity("YEAH");
            fail("Validator should throw ValidationException");
        }
        catch (ValidationException ve)
        {
            assertEquals("YEAH could not be converted to a Boolean", ve.getMessage());
        }

        try
        {
            booleanField.getValidator().assertValidity("true");
        }
        catch (ValidationException ve)
        {
            fail("Validator should not throw ValidationException");
        }
    }

    public void testBigDecimalValidation() throws Exception
    {
        IntakeService is = (IntakeService) this.resolve( IntakeService.ROLE );
        Group group = is.getGroup("ValidationTest");
        assertNotNull(group);

        Field<?> bigDecimalField = group.get("BigDecimalTestField");
        BigDecimalValidator v = (BigDecimalValidator)bigDecimalField.getValidator();
        try
        {
            v.assertValidity((String)null, Locale.US);
            fail("Validator should throw ValidationException");
        }
        catch (ValidationException ve)
        {
            assertEquals("Required", ve.getMessage());
        }

        try
        {
            v.assertValidity("YEAH", Locale.US);
            fail("Validator should throw ValidationException");
        }
        catch (ValidationException ve)
        {
            assertEquals("Not a number", ve.getMessage());
        }

        try
        {
            v.assertValidity("2.0", Locale.US);
            fail("Validator should throw ValidationException");
        }
        catch (ValidationException ve)
        {
            assertEquals("Too small", ve.getMessage());
        }

        try
        {
            v.assertValidity("40.3", Locale.US);
            fail("Validator should throw ValidationException");
        }
        catch (ValidationException ve)
        {
            assertEquals("Too big", ve.getMessage());
        }

        try
        {
            v.assertValidity("1.240,3", Locale.GERMANY);
            fail("Validator should throw ValidationException");
        }
        catch (ValidationException ve)
        {
            assertEquals("Too big", ve.getMessage());
        }

        try
        {
            v.assertValidity("12,5", Locale.GERMANY);
        }
        catch (ValidationException ve)
        {
            fail("Validator should not throw ValidationException");
        }
    }

    public void testIntegerValidation() throws Exception
    {
        IntakeService is = (IntakeService) this.resolve( IntakeService.ROLE );
        Group group = is.getGroup("ValidationTest");
        assertNotNull(group);

        Field<?> intField = group.get("IntegerTestField");
        IntegerValidator v = (IntegerValidator)intField.getValidator();
        try
        {
            v.assertValidity((String)null, Locale.US);
            fail("Validator should throw ValidationException");
        }
        catch (ValidationException ve)
        {
            assertEquals("Required", ve.getMessage());
        }

        try
        {
            v.assertValidity("YEAH", Locale.US);
            fail("Validator should throw ValidationException");
        }
        catch (ValidationException ve)
        {
            assertEquals("Not a number", ve.getMessage());
        }

        try
        {
            v.assertValidity("2", Locale.US);
            fail("Validator should throw ValidationException");
        }
        catch (ValidationException ve)
        {
            assertEquals("Too small", ve.getMessage());
        }

        try
        {
            v.assertValidity("40", Locale.US);
            fail("Validator should throw ValidationException");
        }
        catch (ValidationException ve)
        {
            assertEquals("Too big", ve.getMessage());
        }

        try
        {
            v.assertValidity("12", Locale.GERMANY);
        }
        catch (ValidationException ve)
        {
            fail("Validator should not throw ValidationException");
        }
    }

    public void testIntegerRangeValidation() throws Exception
    {
        IntakeService is = (IntakeService) this.resolve( IntakeService.ROLE );
        Group group = is.getGroup("ValidationTest");
        assertNotNull(group);

        ParserService ps = (ParserService) this.resolve( ParserService.ROLE );
        ValueParser pp = ps.getParser(DefaultParameterParser.class);

        pp.add("vt_0itf", "15");
        group.init(pp);

        Field<?> intField = group.get("IntegerToTestField");
        IntegerRangeValidator v = (IntegerRangeValidator)intField.getValidator();

        try
        {
            v.assertValidity((String)null);
            fail("Validator should throw ValidationException");
        }
        catch (ValidationException ve)
        {
            assertEquals("Required", ve.getMessage());
        }

        try
        {
            v.assertValidity("YEAH", group, Locale.US);
            fail("Validator should throw ValidationException");
        }
        catch (ValidationException ve)
        {
            assertEquals("Not a number", ve.getMessage());
        }

        try
        {
            v.assertValidity("14", group, Locale.US);
            fail("Validator should throw ValidationException");
        }
        catch (ValidationException ve)
        {
            assertEquals("To-int must be greater than From-int", ve.getMessage());
        }

        try
        {
            v.assertValidity("16", group, Locale.US);
        }
        catch (ValidationException ve)
        {
            fail("Validator should not throw ValidationException");
        }
    }

    public void testFloatValidation() throws Exception
    {
        IntakeService is = (IntakeService) this.resolve( IntakeService.ROLE );
        Group group = is.getGroup("ValidationTest");
        assertNotNull(group);

        Field<?> floatField = group.get("FloatTestField");
        FloatValidator v = (FloatValidator)floatField.getValidator();
        try
        {
            v.assertValidity((String)null, Locale.US);
            fail("Validator should throw ValidationException");
        }
        catch (ValidationException ve)
        {
            assertEquals("Required", ve.getMessage());
        }

        try
        {
            v.assertValidity("YEAH", Locale.US);
            fail("Validator should throw ValidationException");
        }
        catch (ValidationException ve)
        {
            assertEquals("Not a number", ve.getMessage());
        }

        try
        {
            v.assertValidity("2.0", Locale.US);
            fail("Validator should throw ValidationException");
        }
        catch (ValidationException ve)
        {
            assertEquals("Too small", ve.getMessage());
        }

        try
        {
            v.assertValidity("40.3", Locale.US);
            fail("Validator should throw ValidationException");
        }
        catch (ValidationException ve)
        {
            assertEquals("Too big", ve.getMessage());
        }

        try
        {
            v.assertValidity("1.240,3", Locale.GERMANY);
            fail("Validator should throw ValidationException");
        }
        catch (ValidationException ve)
        {
            assertEquals("Too big", ve.getMessage());
        }

        try
        {
            v.assertValidity("12,5", Locale.GERMANY);
        }
        catch (ValidationException ve)
        {
            fail("Validator should not throw ValidationException");
        }
    }

    public void testDateStringValidation() throws Exception
    {
        IntakeService is = (IntakeService) this.resolve( IntakeService.ROLE );
        Group group = is.getGroup("ValidationTest");
        assertNotNull(group);

        Field<?> dateField = group.get("DateStringTestField");
        DateStringValidator v = (DateStringValidator)dateField.getValidator();

        try
        {
            v.assertValidity((String)null);
            fail("Validator should throw ValidationException");
        }
        catch (ValidationException ve)
        {
            assertEquals("Required", ve.getMessage());
        }

        try
        {
            v.assertValidity("YEAH");
            fail("Validator should throw ValidationException");
        }
        catch (ValidationException ve)
        {
            assertEquals("Bad format", ve.getMessage());
        }

        try
        {
            v.assertValidity("12/23.20");
            fail("Validator should throw ValidationException");
        }
        catch (ValidationException ve)
        {
            assertEquals("Bad format", ve.getMessage());
        }

        try
        {
            v.assertValidity("12/23/2001");
        }
        catch (ValidationException ve)
        {
            fail("Validator should not throw ValidationException");
        }

        try
        {
            v.assertValidity("23.12.2001");
        }
        catch (ValidationException ve)
        {
            fail("Validator should not throw ValidationException");
        }

        try
        {
            v.assertValidity("12/35/2001"); // should work due to flexible=true
        }
        catch (ValidationException ve)
        {
            fail("Validator should not throw ValidationException");
        }
    }

    public void testDateRangeValidation() throws Exception
    {
        IntakeService is = (IntakeService) this.resolve( IntakeService.ROLE );
        Group group = is.getGroup("ValidationTest");
        assertNotNull(group);

        ParserService ps = (ParserService) this.resolve( ParserService.ROLE );
        ValueParser pp = ps.getParser(DefaultParameterParser.class);

        pp.add("vt_0dstf", "12/23/2001");
        group.init(pp);

        Field<?> dateField = group.get("DateToTestField");
        DateRangeValidator v = (DateRangeValidator)dateField.getValidator();

        try
        {
            v.assertValidity((String)null);
            fail("Validator should throw ValidationException");
        }
        catch (ValidationException ve)
        {
            assertEquals("Required", ve.getMessage());
        }

        try
        {
            v.assertValidity("YEAH", group);
            fail("Validator should throw ValidationException");
        }
        catch (ValidationException ve)
        {
            assertEquals("Bad format", ve.getMessage());
        }

        try
        {
            v.assertValidity("12/22/2001", group);
            fail("Validator should throw ValidationException");
        }
        catch (ValidationException ve)
        {
            assertEquals("To-Date must be greater than From-Date", ve.getMessage());
        }

        try
        {
            v.assertValidity("12/24/2001", group);
        }
        catch (ValidationException ve)
        {
            fail("Validator should not throw ValidationException");
        }
    }

    public void testDoubleValidation() throws Exception
    {
        IntakeService is = (IntakeService) this.resolve( IntakeService.ROLE );
        Group group = is.getGroup("ValidationTest");
        assertNotNull(group);

        Field<?> doubleField = group.get("DoubleTestField");
        DoubleValidator v = (DoubleValidator)doubleField.getValidator();
        try
        {
            v.assertValidity((String)null, Locale.US);
            fail("Validator should throw ValidationException");
        }
        catch (ValidationException ve)
        {
            assertEquals("Required", ve.getMessage());
        }

        try
        {
            v.assertValidity("YEAH", Locale.US);
            fail("Validator should throw ValidationException");
        }
        catch (ValidationException ve)
        {
            assertEquals("Not a number", ve.getMessage());
        }

        try
        {
            v.assertValidity("2.0", Locale.US);
            fail("Validator should throw ValidationException");
        }
        catch (ValidationException ve)
        {
            assertEquals("Too small", ve.getMessage());
        }

        try
        {
            v.assertValidity("40.3", Locale.US);
            fail("Validator should throw ValidationException");
        }
        catch (ValidationException ve)
        {
            assertEquals("Too big", ve.getMessage());
        }

        try
        {
            v.assertValidity("1.240,3", Locale.GERMANY);
            fail("Validator should throw ValidationException");
        }
        catch (ValidationException ve)
        {
            assertEquals("Too big", ve.getMessage());
        }

        try
        {
            v.assertValidity("12,5", Locale.GERMANY);
        }
        catch (ValidationException ve)
        {
            fail("Validator should not throw ValidationException");
        }
    }

    public void testShortValidation() throws Exception
    {
        IntakeService is = (IntakeService) this.resolve( IntakeService.ROLE );
        Group group = is.getGroup("ValidationTest");
        assertNotNull(group);

        Field<?> shortField = group.get("ShortTestField");
        ShortValidator v = (ShortValidator)shortField.getValidator();
        try
        {
            v.assertValidity((String)null, Locale.US);
            fail("Validator should throw ValidationException");
        }
        catch (ValidationException ve)
        {
            assertEquals("Required", ve.getMessage());
        }

        try
        {
            v.assertValidity("YEAH", Locale.US);
            fail("Validator should throw ValidationException");
        }
        catch (ValidationException ve)
        {
            assertEquals("Not a number", ve.getMessage());
        }

        try
        {
            v.assertValidity("2", Locale.US);
            fail("Validator should throw ValidationException");
        }
        catch (ValidationException ve)
        {
            assertEquals("Too small", ve.getMessage());
        }

        try
        {
            v.assertValidity("40", Locale.US);
            fail("Validator should throw ValidationException");
        }
        catch (ValidationException ve)
        {
            assertEquals("Too big", ve.getMessage());
        }

        try
        {
            v.assertValidity("12", Locale.GERMANY);
        }
        catch (ValidationException ve)
        {
            fail("Validator should not throw ValidationException");
        }
    }

    public void testLongValidation() throws Exception
    {
        IntakeService is = (IntakeService) this.resolve( IntakeService.ROLE );
        Group group = is.getGroup("ValidationTest");
        assertNotNull(group);

        Field<?> longField = group.get("LongTestField");
        LongValidator v = (LongValidator)longField.getValidator();
        try
        {
            v.assertValidity((String)null, Locale.US);
            fail("Validator should throw ValidationException");
        }
        catch (ValidationException ve)
        {
            assertEquals("Required", ve.getMessage());
        }

        try
        {
            v.assertValidity("YEAH", Locale.US);
            fail("Validator should throw ValidationException");
        }
        catch (ValidationException ve)
        {
            assertEquals("Not a number", ve.getMessage());
        }

        try
        {
            v.assertValidity("2", Locale.US);
            fail("Validator should throw ValidationException");
        }
        catch (ValidationException ve)
        {
            assertEquals("Too small", ve.getMessage());
        }

        try
        {
            v.assertValidity("40", Locale.US);
            fail("Validator should throw ValidationException");
        }
        catch (ValidationException ve)
        {
            assertEquals("Too big", ve.getMessage());
        }

        try
        {
            v.assertValidity("12", Locale.GERMANY);
        }
        catch (ValidationException ve)
        {
            fail("Validator should not throw ValidationException");
        }
    }

}

