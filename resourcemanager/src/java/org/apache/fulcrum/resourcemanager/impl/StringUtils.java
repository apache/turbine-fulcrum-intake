package org.apache.fulcrum.resourcemanager.impl;

/*
 * Copyright 2004 Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.ArrayList;
import java.util.List;


/**
 * Helper for string manipulations.
 * 
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */
public class StringUtils
{
    /**
     * Taken from commons-lang-2.0 to avoid depending on it
     * for the moment.
     */
    public static String[] split(String str, String separatorChars, int max)
    {
        // Performance tuned for 2.0 (JDK1.4)
        // Direct code is quicker than StringTokenizer.
        // Also, StringTokenizer uses isSpace() not isWhitespace()

        if (str == null)
        {
            return null;
        }
        int len = str.length();
        if (len == 0)
        {
            return new String[0];
        }
        List list = new ArrayList();
        int sizePlus1 = 1;
        int i = 0, start = 0;
        boolean match = false;
        if (separatorChars == null)
        {
            // Null separator means use whitespace
            while (i < len)
            {
                if (Character.isWhitespace( str.charAt( i ) ))
                {
                    if (match)
                    {
                        if (sizePlus1++ == max)
                        {
                            i = len;
                        }
                        list.add( str.substring( start, i ) );
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                match = true;
                i++;
            }
        }
        else if (separatorChars.length() == 1)
        {
            // Optimise 1 character case
            char sep = separatorChars.charAt( 0 );
            while (i < len)
            {
                if (str.charAt( i ) == sep)
                {
                    if (match)
                    {
                        if (sizePlus1++ == max)
                        {
                            i = len;
                        }
                        list.add( str.substring( start, i ) );
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                match = true;
                i++;
            }
        }
        else
        {
            // standard case
            while (i < len)
            {
                if (separatorChars.indexOf( str.charAt( i ) ) >= 0)
                {
                    if (match)
                    {
                        if (sizePlus1++ == max)
                        {
                            i = len;
                        }
                        list.add( str.substring( start, i ) );
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                match = true;
                i++;
            }
        }
        if (match)
        {
            list.add( str.substring( start, i ) );
        }
        return (String []) list.toArray( new String[list.size()] );
    }
}