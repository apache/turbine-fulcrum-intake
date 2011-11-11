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

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.Map;


/**
 * Validates BigDecimals with the following constraints in addition to those
 * listed in NumberValidator and DefaultValidator.
 *
 * <table>
 * <tr><th>Name</th><th>Valid Values</th><th>Default Value</th></tr>
 * <tr><td>minValue</td><td>greater than BigDecimal minValue</td>
 * <td>&nbsp;</td></tr>
 * <tr><td>maxValue</td><td>less than BigDecimal maxValue</td>
 * <td>&nbsp;</td></tr>
 * <tr><td>invalidNumberMessage</td><td>Some text</td>
 * <td>Entry was not a valid number</td></tr>
 * </table>
 *
 * @author <a href="mailto:jmcnally@collab.net">John McNally</a>
 * @author <a href="mailto:Colin.Chalmers@maxware.nl">Colin Chalmers</a>
 * @author <a href="mailto:jh@byteaction.de">J&uuml;rgen Hoffmann</a>
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 * @version $Id$
 */
public class BigDecimalValidator
        extends NumberValidator<BigDecimal>
{
    /**
     * Constructor to use when initializing Object
     *
     * @param paramMap
     * @throws InvalidMaskException
     */
    public BigDecimalValidator(Map<String, Constraint> paramMap)
            throws InvalidMaskException
    {
        super(paramMap);
    }

    /**
     * Default Constructor
     */
    public BigDecimalValidator()
    {
        super();
        invalidNumberMessage = "Entry was not a valid BigDecimal";
    }

    /**
     * @see org.apache.fulcrum.intake.validator.NumberValidator#parseNumber(java.lang.String, java.util.Locale)
     */
    @Override
    protected BigDecimal parseNumber(String stringValue, Locale locale) throws ParseException
    {
        NumberFormat nf = NumberFormat.getInstance(locale);

        Number number = nf.parse(stringValue);
        return new BigDecimal(number.doubleValue());
    }
}
