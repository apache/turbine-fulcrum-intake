/*
 * Copyright 2002-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.fulcrum.yaafi.framework.util;

import java.util.Map;


/**
 * A subset of the utilities available in commons-lang-2.1 StringUtils.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */
public class StringUtils
{
    // Replacing
    //-----------------------------------------------------------------------
    /**
     * <p>Replaces a String with another String inside a larger String, once.</p>
     * 
     * <p>A <code>null</code> reference passed to this method is a no-op.</p>
     * 
     * <pre>
     * StringUtils.replaceOnce(null, *, *)        = null
     * StringUtils.replaceOnce("", *, *)          = ""
     * StringUtils.replaceOnce("aba", null, null) = "aba"
     * StringUtils.replaceOnce("aba", null, null) = "aba"
     * StringUtils.replaceOnce("aba", "a", null)  = "aba"
     * StringUtils.replaceOnce("aba", "a", "")    = "aba"
     * StringUtils.replaceOnce("aba", "a", "z")   = "zba"
     * </pre>
     * 
     * @see #replace(String text, String repl, String with, int max)
     * @param text  text to search and replace in, may be null
     * @param repl  the String to search for, may be null
     * @param with  the String to replace with, may be null
     * @return the text with any replacements processed,
     *  <code>null</code> if null String input
     */
    public static String replaceOnce(String text, String repl, String with) {
        return replace(text, repl, with, 1);
    }

    /**
     * <p>Replaces all occurrences of a String within another String.</p>
     *
     * <p>A <code>null</code> reference passed to this method is a no-op.</p>
     * 
     * <pre>
     * StringUtils.replace(null, *, *)        = null
     * StringUtils.replace("", *, *)          = ""
     * StringUtils.replace("aba", null, null) = "aba"
     * StringUtils.replace("aba", null, null) = "aba"
     * StringUtils.replace("aba", "a", null)  = "aba"
     * StringUtils.replace("aba", "a", "")    = "aba"
     * StringUtils.replace("aba", "a", "z")   = "zbz"
     * </pre>
     * 
     * @see #replace(String text, String repl, String with, int max)
     * @param text  text to search and replace in, may be null
     * @param repl  the String to search for, may be null
     * @param with  the String to replace with, may be null
     * @return the text with any replacements processed,
     *  <code>null</code> if null String input
     */
    public static String replace(String text, String repl, String with) {
        return replace(text, repl, with, -1);
    }

    /**
     * <p>Replaces a String with another String inside a larger String,
     * for the first <code>max</code> values of the search String.</p>
     *
     * <p>A <code>null</code> reference passed to this method is a no-op.</p>
     *
     * <pre>
     * StringUtils.replace(null, *, *, *)         = null
     * StringUtils.replace("", *, *, *)           = ""
     * StringUtils.replace("abaa", null, null, 1) = "abaa"
     * StringUtils.replace("abaa", null, null, 1) = "abaa"
     * StringUtils.replace("abaa", "a", null, 1)  = "abaa"
     * StringUtils.replace("abaa", "a", "", 1)    = "abaa"
     * StringUtils.replace("abaa", "a", "z", 0)   = "abaa"
     * StringUtils.replace("abaa", "a", "z", 1)   = "zbaa"
     * StringUtils.replace("abaa", "a", "z", 2)   = "zbza"
     * StringUtils.replace("abaa", "a", "z", -1)  = "zbzz"
     * </pre>
     * 
     * @param text  text to search and replace in, may be null
     * @param repl  the String to search for, may be null
     * @param with  the String to replace with, may be null
     * @param max  maximum number of values to replace, or <code>-1</code> if no maximum
     * @return the text with any replacements processed,
     *  <code>null</code> if null String input
     */
    public static String replace(String text, String repl, String with, int max) {
        if (text == null || repl == null || with == null || repl.length() == 0 || max == 0) {
            return text;
        }

        StringBuffer buf = new StringBuffer(text.length());
        int start = 0, end = 0;
        while ((end = text.indexOf(repl, start)) != -1) {
            buf.append(text.substring(start, end)).append(with);
            start = end + repl.length();

            if (--max == 0) {
                break;
            }
        }
        buf.append(text.substring(start));
        return buf.toString();
    }

    // Replace, character based
    //-----------------------------------------------------------------------
    /**
     * <p>Replaces all occurrences of a character in a String with another.
     * This is a null-safe version of {@link String#replace(char, char)}.</p>
     *
     * <p>A <code>null</code> string input returns <code>null</code>.
     * An empty ("") string input returns an empty string.</p>
     *
     * <pre>
     * StringUtils.replaceChars(null, *, *)        = null
     * StringUtils.replaceChars("", *, *)          = ""
     * StringUtils.replaceChars("abcba", 'b', 'y') = "aycya"
     * StringUtils.replaceChars("abcba", 'z', 'y') = "abcba"
     * </pre>
     *
     * @param str  String to replace characters in, may be null
     * @param searchChar  the character to search for, may be null
     * @param replaceChar  the character to replace, may be null
     * @return modified String, <code>null</code> if null string input
     * @since 2.0
     */
    public static String replaceChars(String str, char searchChar,
        char replaceChar)
    {
        if (str == null)
        {
            return null;
        }
        return str.replace( searchChar, replaceChar );
    }

    /**
     * <p>Replaces multiple characters in a String in one go.
     * This method can also be used to delete characters.</p>
     *
     * <p>For example:<br />
     * <code>replaceChars(&quot;hello&quot;, &quot;ho&quot;, &quot;jy&quot;) = jelly</code>.</p>
     *
     * <p>A <code>null</code> string input returns <code>null</code>.
     * An empty ("") string input returns an empty string.
     * A null or empty set of search characters returns the input string.</p>
     *
     * <p>The length of the search characters should normally equal the length
     * of the replace characters.
     * If the search characters is longer, then the extra search characters
     * are deleted.
     * If the search characters is shorter, then the extra replace characters
     * are ignored.</p>
     *
     * <pre>
     * StringUtils.replaceChars(null, *, *)           = null
     * StringUtils.replaceChars("", *, *)             = ""
     * StringUtils.replaceChars("abc", null, *)       = "abc"
     * StringUtils.replaceChars("abc", "", *)         = "abc"
     * StringUtils.replaceChars("abc", "b", null)     = "ac"
     * StringUtils.replaceChars("abc", "b", "")       = "ac"
     * StringUtils.replaceChars("abcba", "bc", "yz")  = "ayzya"
     * StringUtils.replaceChars("abcba", "bc", "y")   = "ayya"
     * StringUtils.replaceChars("abcba", "bc", "yzx") = "ayzya"
     * </pre>
     *
     * @param str  String to replace characters in, may be null
     * @param searchChars  a set of characters to search for, may be null
     * @param replaceChars  a set of characters to replace, may be null
     * @return modified String, <code>null</code> if null string input
     * @since 2.0
     */
    public static String replaceChars(String str, String searchChars,
        String replaceChars)
    {
        if (isEmpty( str ) || isEmpty( searchChars ))
        {
            return str;
        }
        if (replaceChars == null)
        {
            replaceChars = "";
        }
        boolean modified = false;
        StringBuffer buf = new StringBuffer( str.length() );
        for (int i = 0; i < str.length(); i++)
        {
            char ch = str.charAt( i );
            int index = searchChars.indexOf( ch );
            if (index >= 0)
            {
                modified = true;
                if (index < replaceChars.length())
                {
                    buf.append( replaceChars.charAt( index ) );
                }
            }
            else
            {
                buf.append( ch );
            }
        }
        if (modified)
        {
            return buf.toString();
        }
        else
        {
            return str;
        }
    }

    /**
     * <p>Checks if a String is empty ("") or null.</p>
     *
     * <pre>
     * StringUtils.isEmpty(null)      = true
     * StringUtils.isEmpty("")        = true
     * StringUtils.isEmpty(" ")       = false
     * StringUtils.isEmpty("bob")     = false
     * StringUtils.isEmpty("  bob  ") = false
     * </pre>
     *
     * <p>NOTE: This method changed in Lang version 2.0.
     * It no longer trims the String.
     * That functionality is available in isBlank().</p>
     *
     * @param str  the String to check, may be null
     * @return <code>true</code> if the String is empty or null
     */
    public static boolean isEmpty(String str)
    {
        return str == null || str.length() == 0;
    }
    
    /**
     * Perform a series of substitutions. The substitions
     * are performed by replacing ${variable} in the target
     * string with the value of provided by the key "variable"
     * in the provided hashtable.
     *
     * @param argStr target string
     * @param vars name/value pairs used for substitution
     * @param isLenient ignore failures
     * @return String target string with replacements.
     */
    public static StringBuffer stringSubstitution(String argStr, Map vars, boolean isLenient)
    {
        StringBuffer argBuf = new StringBuffer();
        int argStrLength = argStr.length();

        for (int cIdx = 0 ; cIdx < argStrLength;)
        {
            char ch = argStr.charAt(cIdx);
            char del = ' ';

            switch (ch)
            {
                case '$':
                    StringBuffer nameBuf = new StringBuffer();
                    del = argStr.charAt(cIdx+1);
                    if( del == '{')
                    {
                        cIdx++;

                        for (++cIdx ; cIdx < argStr.length(); ++cIdx)
                        {
                            ch = argStr.charAt(cIdx);
                            if (ch != '}')
                                nameBuf.append(ch);
                            else
                                break;
                        }

                        if (nameBuf.length() > 0)
                        {
                            Object value = vars.get(nameBuf.toString());

                            if (value != null)
                            {
                                argBuf.append(value.toString());
                            }
                            else
                            {
                                if (!isLenient)
                                {
                                    throw new RuntimeException("No value found for : " + nameBuf );
                                }
                            }

                            del = argStr.charAt(cIdx);

                            if( del != '}')
                            {
                                throw new RuntimeException("Delimineter not found for : " + nameBuf );
                            }
                        }

                        cIdx++;
                    }
                    else
                    {
                        argBuf.append(ch);
                        ++cIdx;
                    }

                    break;

                default:
                    argBuf.append(ch);
                    ++cIdx;
                    break;
            }
        }

        return argBuf;
    }
}
