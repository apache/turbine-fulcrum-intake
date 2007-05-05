package org.apache.fulcrum.util;

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

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
/**
 * <code>WordWrapUtils</code> is a utility class to assist with word wrapping.
 *
 * @author Henri Yandell
 * @author Stephen Colebourne
 * @version $Id$
 */
public class WordWrapUtils {

    // Wrapping
    //--------------------------------------------------------------------------

    /**
     * Wraps a block of text to a specified line length.
     * <p>
     * This method takes a block of text, which might have long lines in it
     * and wraps the long lines based on the supplied wrapColumn parameter.
     * It was initially implemented for use by VelocityEmail. If there are tabs
     * in inString, you are going to get results that are a bit strange,
     * since tabs are a single character but are displayed as 4 or 8
     * spaces. Remove the tabs.
     *
     * @param str  text which is in need of word-wrapping
     * @param newline  the characters that define a newline
     * @param wrapColumn  the column to wrap the words at
     * @return the text with all the long lines word-wrapped
     */
    public static String wrapText(String str, String newline, int wrapColumn) {
        StringTokenizer lineTokenizer = new StringTokenizer(str, newline, true);
        StringBuffer stringBuffer = new StringBuffer();

        while (lineTokenizer.hasMoreTokens()) {
            try {
                String nextLine = lineTokenizer.nextToken();

                if (nextLine.length() > wrapColumn) {
                    // This line is long enough to be wrapped.
                    nextLine = wrapLine(nextLine, newline, wrapColumn);
                }

                stringBuffer.append(nextLine);

            } catch (NoSuchElementException nsee) {
                // thrown by nextToken(), but I don't know why it would
                break;
            }
        }

        return (stringBuffer.toString());
    }

    /**
     * Wraps a single line of text.
     * Called by wrapText() to do the real work of wrapping.
     *
     * @param line  a line which is in need of word-wrapping
     * @param newline  the characters that define a newline
     * @param wrapColumn  the column to wrap the words at
     * @return a line with newlines inserted
     */
    private static String wrapLine(String line, String newline, int wrapColumn) {
        StringBuffer wrappedLine = new StringBuffer();

        while (line.length() > wrapColumn) {
            int spaceToWrapAt = line.lastIndexOf(' ', wrapColumn);

            if (spaceToWrapAt >= 0) {
                wrappedLine.append(line.substring(0, spaceToWrapAt));
                wrappedLine.append(newline);
                line = line.substring(spaceToWrapAt + 1);
            }

            // This must be a really long word or URL. Pass it
            // through unchanged even though it's longer than the
            // wrapColumn would allow. This behavior could be
            // dependent on a parameter for those situations when
            // someone wants long words broken at line length.
            else {
                spaceToWrapAt = line.indexOf(' ', wrapColumn);

                if (spaceToWrapAt >= 0) {
                    wrappedLine.append(line.substring(0, spaceToWrapAt));
                    wrappedLine.append(newline);
                    line = line.substring(spaceToWrapAt + 1);
                } else {
                    wrappedLine.append(line);
                    line = "";
                }
            }
        }

        // Whatever is left in line is short enough to just pass through
        wrappedLine.append(line);

        return (wrappedLine.toString());
    }

    // Word wrapping
    //--------------------------------------------------------------------------

    /**
     * Create a word-wrapped version of a String. Wrap at 80 characters and
     * use newlines as the delimiter. If a word is over 80 characters long
     * use a - sign to split it.
     */
    public static String wordWrap(String str) {
        return wordWrap(str, 80, "\n", "-");
    }
    /**
     * Create a word-wrapped version of a String. Wrap at a specified width and
     * use newlines as the delimiter. If a word is over the width in lenght
     * use a - sign to split it.
     */
    public static String wordWrap(String str, int width) {
        return wordWrap(str, width, "\n", "-");
    }
    /**
     * Word-wrap a string.
     *
     * @param str   String to word-wrap
     * @param width int to wrap at
     * @param delim String to use to separate lines
     * @param split String to use to split a word greater than width long
     *
     * @return String that has been word wrapped
     */
    public static String wordWrap(String str, int width, String delim, String split) {
        int sz = str.length();

        /// shift width up one. mainly as it makes the logic easier
        width++;

        // our best guess as to an initial size
        StringBuffer buffer = new StringBuffer(sz / width * delim.length() + sz);

        // every line will include a delim on the end
        width = width - delim.length();

        int idx = -1;
        String substr = null;

        // beware: i is rolled-back inside the loop
        for (int i = 0; i < sz; i += width) {

            // on the last line
            if (i > sz - width) {
                buffer.append(str.substring(i));
                break;
            }

            // the current line
            substr = str.substring(i, i + width);

            // is the delim already on the line
            idx = substr.indexOf(delim);
            if (idx != -1) {
                buffer.append(substr.substring(0, idx));
                buffer.append(delim);
                i -= width - idx - delim.length();

                // Erase a space after a delim. Is this too obscure?
                if (substr.charAt(idx + 1) != '\n') {
                    if (Character.isWhitespace(substr.charAt(idx + 1))) {
                        i++;
                    }
                }
                continue;
            }

            idx = -1;

            // figure out where the last space is
            char[] chrs = substr.toCharArray();
            for (int j = width; j > 0; j--) {
                if (Character.isWhitespace(chrs[j - 1])) {
                    idx = j;
                    break;
                }
            }

            // idx is the last whitespace on the line.
            if (idx == -1) {
                for (int j = width; j > 0; j--) {
                    if (chrs[j - 1] == '-') {
                        idx = j;
                        break;
                    }
                }
                if (idx == -1) {
                    buffer.append(substr);
                    buffer.append(delim);
                } else {
                    if (idx != width) {
                        idx++;
                    }
                    buffer.append(substr.substring(0, idx));
                    buffer.append(delim);
                    i -= width - idx;
                }
            } else {
                // insert spaces
                buffer.append(substr.substring(0, idx));
                buffer.append(StringUtils.repeat(" ", width - idx));
                buffer.append(delim);
                i -= width - idx;
            }
        }
        return buffer.toString();
    }

}
