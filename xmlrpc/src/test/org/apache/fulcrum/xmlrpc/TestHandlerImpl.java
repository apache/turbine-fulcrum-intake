package org.apache.fulcrum.xmlrpc;

/**
 * Description of the class.
 *
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 */
public class TestHandlerImpl
        implements TestHandler
{
    /**
     * Returns the passed parameter.
     *
     * @param message  The message to be returned
     * @return  The message passed
     */
    public String echo(String message)
    {
        return message;
    }
}
