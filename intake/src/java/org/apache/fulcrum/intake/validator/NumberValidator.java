package org.apache.fulcrum.intake.validator;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Turbine" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Turbine", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

import java.util.Map;

/**
 * Validates numbers with the following constraints in addition to those
 * listed in DefaultValidator.
 *
 * <table>
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
abstract class NumberValidator
        extends DefaultValidator
{
    /** The message to show if field fails min-value test */
    String minValueMessage = null;

    /** The message to show if field fails max-value test */
    String maxValueMessage = null;

    /** The message to use for invalid numbers */
    String invalidNumberMessage = null;

    /**
     * Extract the relevant parameters from the constraints listed
     * in <rule> tags within the intake.xml file.
     *
     * @param paramMap a <code>Map</code> of <code>rule</code>'s
     * containing constraints on the input.
     * @exception InvalidMaskException an invalid mask was specified
     */
    public void init(Map paramMap)
            throws InvalidMaskException
    {
        super.init(paramMap);

        Constraint constraint =
                (Constraint) paramMap.get(INVALID_NUMBER_RULE_NAME);

        if (constraint != null)
        {
            invalidNumberMessage = constraint.getMessage();
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

}