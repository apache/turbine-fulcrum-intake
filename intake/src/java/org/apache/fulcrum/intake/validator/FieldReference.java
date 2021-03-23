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

import java.util.List;

import org.apache.fulcrum.intake.IntakeException;
import org.apache.fulcrum.intake.model.Field;
import org.apache.fulcrum.intake.model.Group;

/**
 * Helper Class to manage relations between fields. The following
 * comparisons are supported:
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
public class FieldReference
{
	public static enum Comparison
	{
	    /** Rule for "&lt;" comparison */
		LT ("less-than"),

		/** Rule for "&gt;" comparison */
		GT ("greater-than"),

	    /** Rule for "&lt;=" comparison */
		LTE ("less-than-or-equal"),

	    /** Rule for "&gt;=" comparison */
		GTE ("greater-than-or-equal");

		private final String text;

		private Comparison(String text)
		{
			this.text = text;
		}

		@Override
		public String toString()
		{
			return text;
		}

		public static Comparison fromString(String string)
		{
		    if (string != null)
		    {
		    	for (Comparison c : Comparison.values())
		    	{
		    		if (string.equals(c.text))
		    		{
		    			return c;
		    		}
		    	}
		    }

		    return null;
		}
	}

    /** Numeric comparison */
    private Comparison compare = null;

    /** Name of referenced field */
    private String fieldName = null;

    /** Error message */
    private String message = null;

    /**
     *  Constructor
     */
    public FieldReference()
    {
        // do nothing
    }

    /**
     * @return the comparison type
     */
    public Comparison getComparison()
    {
        return compare;
    }

    /**
     * @param compare the comparison type to set
     */
    public void setComparison(Comparison compare)
    {
        this.compare = compare;
    }

    /**
     * @return the field name
     */
    public String getFieldName()
    {
        return fieldName;
    }

    /**
     * @param fieldName the field name to set
     */
    public void setFieldName(String fieldName)
    {
        this.fieldName = fieldName;
    }

    /**
     * @return the message
     */
    public String getMessage()
    {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message)
    {
        this.message = message;
    }

    /**
     * Map the comparison strings to their numeric counterparts
     *
     * @param key the string representation of a comparison operator
     * @return the numeric representation of the given comparison operator
     */
    public static Comparison getComparisonType(String key)
    {
    	return Comparison.fromString(key);
    }

    /**
     * Check the parsed value against the referenced fields
     *
     * @param fieldReferences List of field references to check
     * @param compareCallback Callback to the actual compare operation
     * @param value the parsed value of the related field
     * @param group the group the related field belongs to
     *
     * @param <T> the field type
     *
     * @throws ValidationException if the validation against at least one related field fails
     */
    public static <T> void checkReferences(List<FieldReference> fieldReferences, CompareCallback<T> compareCallback,
            T value, Group group)
        throws ValidationException
    {
        for (FieldReference ref : fieldReferences)
        {
            boolean comp_true = true;

            try
            {
                @SuppressWarnings("unchecked")
                Field<T> refField = (Field<T>) group.get(ref.getFieldName());

                if (refField.isSet())
                {
                    /*
                     * Fields are processed in sequence so that our
                     * reference field might have been set but not
                     * yet validated. We check this here.
                     */
                    if (!refField.isValidated())
                    {
                        refField.validate();
                    }

                    if (refField.isValid())
                    {
                        comp_true = compareCallback.compareValues(ref.getComparison(),
                                value,
                                refField.getValue());
                    }
                }
            }
            catch (IntakeException e)
            {
                throw new ValidationException(ref.getMessage());
            }

            if (comp_true == false)
            {
                throw new ValidationException(ref.getMessage());
            }
        }
    }
}
