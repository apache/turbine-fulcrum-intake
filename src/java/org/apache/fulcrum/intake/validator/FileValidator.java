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
            int byteSize = fis.read(fileData);
            if ( fileData.length != byteSize )
            {
            	throw new ValidationException("Byte length mismatch found");
            }
            
        }
        catch (IOException e)
        {
            fileData = null;
        }

        String content = null;
        try
        {
        	if ( fileData != null )
        	{
        		content = new String(fileData, charset);
        	}
        }
        catch (UnsupportedEncodingException e)
        {
            throw new ValidationException("Invalid charset " + charset);
        }

        super.assertValidity(content);
    }
}
