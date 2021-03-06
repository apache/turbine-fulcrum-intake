package org.apache.fulcrum.intake.validator;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.fulcrum.intake.model.Field;
import org.apache.fulcrum.intake.model.Group;
import org.apache.fulcrum.intake.validator.FieldReference.Comparison;

/**
 * Validates an int field in dependency on another int field.
 *
 * <table>
 * <caption>Validation rules</caption>
 * <tr>
 *   <th>Name</th><th>Valid Values</th><th>Default Value</th>
 * </tr>
 * <tr>
 *   <td>less-than</td>
 *   <td>&lt;name of other field&gt;</td>
 *   <td>&nbsp;</td>
 * </tr>
 * <tr>
 *   <td>greater-than</td>
 *   <td>&lt;name of other field&gt;</td>
 *   <td>&nbsp;</td>
 * </tr>
 * <tr>
 *   <td>less-than-or-equal</td>
 *   <td>&lt;name of other field&gt;</td>
 *   <td>&nbsp;</td>
 * </tr>
 * <tr>
 *   <td>greater-than-or-equal</td>
 *   <td>&lt;name of other field&gt;</td>
 *   <td>&nbsp;</td>
 * </tr>
 * </table>
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 * @version $Id$
 */
public class IntegerRangeValidator
        extends IntegerValidator
{
    /** List of FieldReferences for multiple comparisons */
    List<FieldReference> fieldReferences;

    /** Callback for the actual compare operation */
    CompareCallback<Integer> compareCallback;

    /**
     *  Default constructor
     */
    public IntegerRangeValidator()
    {
        super();
    }

    /**
     * Constructor to use when initializing Object
     *
     * @param paramMap a map of parameters
     * @throws InvalidMaskException one of the mask rules is invalid
     */
    @Override
	public void init(Map<String, ? extends Constraint> paramMap)
            throws InvalidMaskException
    {
        super.init(paramMap);

        compareCallback = new CompareCallback<Integer>()
            {
                /**
                 * Compare the given values using the compare operation provided
                 *
                 * @param compare type of compare operation
                 * @param thisValue value of this field
                 * @param refValue value of the reference field
                 *
                 * @return the result of the comparison
                 */
                @Override
				public boolean compareValues(Comparison compare, Integer thisValue, Integer refValue)
                {
                    boolean result = true;

                    switch (compare)
                    {
                        case LT:
                            result = thisValue.compareTo(refValue) < 0;
                            break;

                        case LTE:
                            result = thisValue.compareTo(refValue) <= 0;
                            break;

                        case GT:
                            result = thisValue.compareTo(refValue) > 0;
                            break;

                        case GTE:
                            result = thisValue.compareTo(refValue) >= 0;
                            break;
                    }

                    return result;
                }
            };

        fieldReferences = new ArrayList<FieldReference>(10);

        for (Map.Entry<String, ? extends Constraint> entry : paramMap.entrySet())
        {
            String key = entry.getKey();
            Constraint constraint = entry.getValue();

            Comparison compare = FieldReference.getComparisonType(key);

            if (compare != null)
            {
                // found matching constraint
                FieldReference fieldref = new FieldReference();
                fieldref.setComparison(compare);
                fieldref.setFieldName(constraint.getValue());
                fieldref.setMessage(constraint.getMessage());

                fieldReferences.add(fieldref);
            }
        }

        if (fieldReferences.isEmpty())
        {
            log.warn("No reference field rules have been found.");
        }
    }

    /**
     * Determine whether a testValue meets the criteria specified
     * in the constraints defined for this validator
     *
     * @param testField a <code>Field</code> to be tested
     * @throws ValidationException containing an error message if the
     * testValue did not pass the validation tests.
     */
    @Override
	public void assertValidity(final Field<Integer> testField)
        throws ValidationException
    {
        super.assertValidity(testField);

        Group thisGroup = testField.getGroup();
        Locale locale = testField.getLocale();

        if (testField.isMultiValued())
        {
            String[] stringValues = (String[])testField.getTestValue();

            for (int i = 0; i < stringValues.length; i++)
            {
                assertValidity(stringValues[i], thisGroup, locale);
            }
        }
        else
        {
            String testValue = (String)testField.getTestValue();

            assertValidity(testValue, thisGroup, locale);
        }
    }

    /**
     * Determine whether a testValue meets the criteria specified
     * in the constraints defined for this validator
     *
     * @param testValue a <code>String</code> to be tested
     * @param group the group this field belongs to
     * @param locale the locale for this field
     *
     * @throws ValidationException containing an error message if the
     * testValue did not pass the validation tests.
     */
    public void assertValidity(final String testValue, final Group group, final Locale locale)
        throws ValidationException
    {
        if (required || StringUtils.isNotEmpty(testValue))
        {
            Integer testInt;

			try
			{
				testInt = parseNumber(testValue, locale);
			}
			catch (NumberFormatException e)
			{
				errorMessage = invalidNumberMessage;
				throw new ValidationException(invalidNumberMessage);
			}

            try
            {
                FieldReference.checkReferences(fieldReferences, compareCallback,
                        testInt, group);
            }
            catch (ValidationException e)
            {
                errorMessage = e.getMessage();
                throw e;
            }
        }
    }
}
