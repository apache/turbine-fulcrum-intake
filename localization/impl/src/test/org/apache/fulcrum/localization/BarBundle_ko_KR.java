package org.apache.fulcrum.localization;

import java.util.ListResourceBundle;

/**
 * A dummied up Korean resource bundle for use in testing.
 */
public class BarBundle_ko_KR extends ListResourceBundle
{
    private static final Object[][] CONTENTS =
    {
        { "key1", "[ko] value1" },
        { "key2", "[ko] value2" },
        { "key3", "[ko] value3" },
    };
    
    protected Object[][] getContents()
    {
        return CONTENTS;
    }
}
