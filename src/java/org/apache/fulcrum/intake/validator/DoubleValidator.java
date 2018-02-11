package org.apache.fulcrum.intake.validator;

import java.util.Locale;

/**
 * Validates Doubles with the following constraints in addition to those
 * listed in NumberValidator and DefaultValidator.
 *
 * <table>
 * <caption>Validation rules</caption>
 * <tr><th>Name</th><th>Valid Values</th><th>Default Value</th></tr>
 * <tr><td>minValue</td><td>greater than Double.MIN_VALUE</td>
 * <td>&nbsp;</td></tr>
 * <tr><td>maxValue</td><td>less than Double.MAX_VALUE</td>
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
public class DoubleValidator
        extends NumberValidator<Double>
{
    /**
     * Default Constructor
     */
    public DoubleValidator()
    {
        super();
        invalidNumberMessage = "Entry was not a valid Double";
    }

    /**
     * @see org.apache.fulcrum.intake.validator.NumberValidator#parseNumber(java.lang.String, java.util.Locale)
     */
    @Override
    protected Double parseNumber(String stringValue, Locale locale) throws NumberFormatException
    {
        Number number = parseIntoNumber(stringValue, locale);
		return Double.valueOf(number.doubleValue());
    }
}
