package org.apache.fulcrum.intake.validator;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;

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
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.fulcrum.intake.model.Field;

/**
 * Validates numbers with the following constraints in addition to those
 * listed in DefaultValidator.
 *
 * <table>
 * <caption>Validation rules</caption>
 * <tr><th>Name</th><th>Valid Values</th><th>Default Value</th></tr>
 * <tr><td>minValue</td><td>greater than BigDecimal.MIN_VALUE</td>
 * <td>&nbsp;</td></tr>
 * <tr><td>maxValue</td><td>less than BigDecimal.MAX_VALUE</td>
 * <td>&nbsp;</td></tr>
 * <tr><td>notANumberMessage</td><td>Some text</td>
 * <td>Entry was not a valid number</td></tr>
 * </table>
 *
 * @author <a href="mailto:jmcnally@collab.net">John McNally</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @author <a href="mailto:Colin.Chalmers@maxware.nl">Colin Chalmers</a>
 * @version $Id$
 */
public abstract class NumberValidator<T extends Number>
        extends DefaultValidator<T>
{
    /** The message to show if field fails min-value test */
    String minValueMessage = null;

    /** The message to show if field fails max-value test */
    String maxValueMessage = null;

    /** The message to use for invalid numbers */
    String invalidNumberMessage = null;

    private T minValue = null;
    private T maxValue = null;

    /**
     * Default Constructor
     */
    public NumberValidator()
    {
        super();
    }

    /**
     * Extract the relevant parameters from the constraints listed
     * in &lt;rule&gt; tags within the intake.xml file.
     *
     * @param paramMap a <code>Map</code> of <code>rule</code>'s
     * containing constraints on the input.
     * @throws InvalidMaskException an invalid mask was specified
     */
    @Override
	public void init(Map<String, ? extends Constraint> paramMap)
            throws InvalidMaskException
    {
        super.init(paramMap);

        Constraint constraint = paramMap.get(INVALID_NUMBER_RULE_NAME);

        if (constraint != null)
        {
            invalidNumberMessage = constraint.getMessage();
        }

        constraint = paramMap.get(MIN_VALUE_RULE_NAME);
        if (constraint != null)
        {
            String param = constraint.getValue();
            try
            {
                minValue = parseNumber(param, Locale.US);
            }
            catch (NumberFormatException e)
            {
                throw new InvalidMaskException("Could not parse minimum value " + param, e);
            }
            minValueMessage = constraint.getMessage();
        }

        constraint = paramMap.get(MAX_VALUE_RULE_NAME);
        if (constraint != null)
        {
            String param = constraint.getValue();
            try
            {
                maxValue = parseNumber(param, Locale.US);
            }
            catch (NumberFormatException e)
            {
                throw new InvalidMaskException("Could not parse minimum value " + param, e);
            }
            maxValueMessage = constraint.getMessage();
        }
    }

    /**
     * Parse the actual value out of a string
     *
     * @param stringValue the string value
     * @param locale the locale to use while parsing
     *
     * @return the value
     *
     * @throws NumberFormatException if the value could not be parsed
     */
    protected abstract T parseNumber(String stringValue, Locale locale) throws NumberFormatException;

    /**
     * Helper method to parse a number object out of a string
     *
     * @param stringValue the string value
     * @param locale the locale to use while parsing
     *
     * @return the Number
     *
     * @throws NumberFormatException if the value could not be parsed
     */
    protected Number parseIntoNumber(String stringValue, Locale locale) throws NumberFormatException
    {
        NumberFormat nf = NumberFormat.getInstance(locale);

        try
        {
            ParsePosition pos = new ParsePosition(0);
            Number number = nf.parse(stringValue, pos);

            if (pos.getIndex() != stringValue.length())
            {
                throw new ParseException("Could not parse string completely", pos.getErrorIndex());
            }

            return number;
        }
        catch (ParseException e)
        {
            throw new NumberFormatException(e.getMessage());
        }
    }

    /**
     * Determine whether a field meets the criteria specified
     * in the constraints defined for this validator
     *
     * @param field a <code>Field</code> to be tested
     * @throws ValidationException containing an error message if the
     * testValue did not pass the validation tests.
     */
    @Override
	public void assertValidity(Field<T> field) throws ValidationException
    {
        Locale locale = field.getLocale();

        if (field.isMultiValued())
        {
            String[] stringValues = (String[])field.getTestValue();

            for (int i = 0; i < stringValues.length; i++)
            {
                assertValidity(stringValues[i], locale);
            }
        }
        else
        {
            assertValidity((String)field.getTestValue(), locale);
        }
    }

    /**
     * Determine whether a testValue meets the criteria specified
     * in the constraints defined for this validator
     *
     * @param testValue a <code>String</code> to be tested
     * @param locale the Locale of the associated field
     * @throws ValidationException containing an error message if the
     * testValue did not pass the validation tests.
     */
    public void assertValidity(String testValue, Locale locale) throws ValidationException
    {
        super.assertValidity(testValue);

        if (required || StringUtils.isNotEmpty(testValue))
        {
            T number = null;
            try
            {
                number = parseNumber(testValue, locale);
            }
            catch (NumberFormatException e)
            {
                errorMessage = invalidNumberMessage;
                throw new ValidationException(invalidNumberMessage);
            }

            if (minValue != null && number.doubleValue() < minValue.doubleValue())
            {
                errorMessage = minValueMessage;
                throw new ValidationException(minValueMessage);
            }
            if (maxValue != null && number.doubleValue() > maxValue.doubleValue())
            {
                errorMessage = maxValueMessage;
                throw new ValidationException(maxValueMessage);
            }
        }
    }

    // ************************************************************
    // **                Bean accessor methods                   **
    // ************************************************************

    /**
     * Get the value of minValueMessage.
     *
     * @return value of minValueMessage.
     */
    public String getMinValueMessage()
    {
        return minValueMessage;
    }

    /**
     * Set the value of minValueMessage.
     *
     * @param minValueMessage  Value to assign to minValueMessage.
     */
    public void setMinValueMessage(String minValueMessage)
    {
        this.minValueMessage = minValueMessage;
    }

    /**
     * Get the value of maxValueMessage.
     *
     * @return value of maxValueMessage.
     */
    public String getMaxValueMessage()
    {
        return maxValueMessage;
    }

    /**
     * Set the value of maxValueMessage.
     *
     * @param maxValueMessage  Value to assign to maxValueMessage.
     */
    public void setMaxValueMessage(String maxValueMessage)
    {
        this.maxValueMessage = maxValueMessage;
    }

    /**
     * Get the value of invalidNumberMessage.
     *
     * @return value of invalidNumberMessage.
     */
    public String getInvalidNumberMessage()
    {
        return invalidNumberMessage;
    }

    /**
     *
     * Set the value of invalidNumberMessage.
     * @param invalidNumberMessage  Value to assign to invalidNumberMessage.
     */
    public void setInvalidNumberMessage(String invalidNumberMessage)
    {
        this.invalidNumberMessage = invalidNumberMessage;
    }

    /**
     * Get the value of minValue.
     *
     * @return value of minValue.
     */
    public T getMinValue()
    {
        return minValue;
    }

    /**
     * Set the value of minValue.
     *
     * @param minValue  Value to assign to minValue.
     */
    public void setMinValue(T minValue)
    {
        this.minValue = minValue;
    }

    /**
     * Get the value of maxValue.
     *
     * @return value of maxValue.
     */
    public T getMaxValue()
    {
        return maxValue;
    }

    /**
     * Set the value of maxValue.
     *
     * @param maxValue  Value to assign to maxValue.
     */
    public void setMaxValue(T maxValue)
    {
        this.maxValue = maxValue;
    }
}
