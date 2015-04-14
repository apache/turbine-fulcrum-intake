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

import java.util.Locale;

/**
 * Validates Longs with the following constraints in addition to those
 * listed in NumberValidator and DefaultValidator.
 *
 * <table>
 * <tr><th>Name</th><th>Valid Values</th><th>Default Value</th></tr>
 * <tr><td>minValue</td><td>greater than Long.MIN_VALUE</td>
 * <td>&nbsp;</td></tr>
 * <tr><td>maxValue</td><td>less than Long.MAX_VALUE</td>
 * <td>&nbsp;</td></tr>
 * <tr><td>invalidNumberMessage</td><td>Some text</td>
 * <td>Entry was not a valid number</td></tr>
 * </table>
 *
 * @author <a href="mailto:jmcnally@collab.net">John McNally</a>
 * @author <a href="mailto:Colin.Chalmers@maxware.nl">Colin Chalmers</a>
 * @author <a href="mailto:jh@byteaction.de">J&uuml;rgen Hoffmann</a>
 * @version $Id$
 */
public class LongValidator
        extends NumberValidator<Long>
{
    /**
     * Default Constructor
     */
    public LongValidator()
    {
        super();
        invalidNumberMessage = "Entry was not a valid Long";
    }

    /**
     * @see org.apache.fulcrum.intake.validator.NumberValidator#parseNumber(java.lang.String, java.util.Locale)
     */
    @Override
    protected Long parseNumber(String stringValue, Locale locale) throws NumberFormatException
    {
        return Long.valueOf(stringValue);
    }
}
