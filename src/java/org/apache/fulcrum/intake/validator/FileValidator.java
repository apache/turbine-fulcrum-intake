package org.apache.fulcrum.intake.validator;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.Part;

/**
 * A validator that will compare a Part testValue against the following
 * constraints in addition to those listed in DefaultValidator.
 *
 * This validator can serve as the base class for more specific validators
 *
 * @author <a href="mailto:jmcnally@collab.net">John McNally</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @author <a href="mailto:Colin.Chalmers@maxware.nl">Colin Chalmers</a>
 * @version $Id$
 */
public class FileValidator
        extends DefaultValidator<Part>
{
    private final static Pattern charsetPattern = Pattern.compile(".+charset\\s*=\\s*(.+)");

    /**
     * Default constructor
     */
    public FileValidator()
    {
        super();
    }

    /**
     * Determine whether a testValue meets the criteria specified
     * in the constraints defined for this validator
     *
     * @param testValue a <code>Part</code> to be tested
     * @throws ValidationException containing an error message if the
     * testValue did not pass the validation tests.
     */
    public void assertValidity(Part testValue)
            throws ValidationException
    {
        byte[] fileData = new byte[(int) testValue.getSize()];
        String contentType = testValue.getContentType();
        String charset = Charset.defaultCharset().name();

        if (contentType.contains("charset"))
        {
            Matcher matcher = charsetPattern.matcher(contentType);
            if (matcher.matches())
            {
                charset = matcher.group(1);
            }
        }

        try (InputStream fis = testValue.getInputStream())
        {
            fis.read(fileData);
        }
        catch (IOException e)
        {
            fileData = null;
        }

        String content;
        try
        {
            content = new String(fileData, charset);
        }
        catch (UnsupportedEncodingException e)
        {
            throw new ValidationException("Invalid charset " + charset);
        }

        super.assertValidity(content);
    }
}
