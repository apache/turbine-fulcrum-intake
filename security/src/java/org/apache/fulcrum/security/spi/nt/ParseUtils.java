/*
 * Created on Aug 24, 2003
 *
 */
package org.apache.fulcrum.security.spi.nt;

import javax.security.auth.login.LoginException;

/**
 * @author Eric Pugh
 *
 * Utility class for parsing out data.
 */
public class ParseUtils
{
    public static String parseForUsername(String usernameAndDomain) throws LoginException
    {
        // fix up angles in case user puts in wrong one!
        usernameAndDomain = usernameAndDomain.replace('/', '\\');
        // parse the domain and username values out of the username
        int separator = usernameAndDomain.indexOf("\\");
        if (separator == -1)
        {
            throw new LoginException("Error: no separator (\\) found in the username pased in to distingush between domain and username");
        }
        return usernameAndDomain.substring(separator + 1);
    }
    public static String parseForDomain(String usernameAndDomain) throws LoginException
    {
        // fix up angles in case user puts in wrong one!
        usernameAndDomain = usernameAndDomain.replace('/', '\\');
        // parse the domain and username values out of the username
        int separator = usernameAndDomain.indexOf("\\");
        if (separator == -1)
        {
            throw new LoginException("Error: no separator (\\) found in the username pased in to distingush between domain and username");
        }
        return usernameAndDomain.substring(0, separator);
    }
}
