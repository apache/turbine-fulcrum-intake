import java.util.ListResourceBundle;

/**
 * A dummied up Korean resource bundle for use in testing.
 */
public class BarBundle_kr_KO extends ListResourceBundle
{
    private static final Object[][] CONTENTS =
    {
        { "key1", "[kr] value1" },
        { "key2", "[kr] value2" },
        { "key3", "[kr] value3" },
        { "key4", "[kr] value4" }
    };
    
    protected Object[][] getContents()
    {
        return CONTENTS;
    }
}
