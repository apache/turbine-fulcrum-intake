package org.apache.fulcrum.xmlrpc;

import org.apache.avalon.framework.component.Component;

/**
 * Description of the class.
 *
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 */
public interface TestHandler
        extends Component
{
    public static String ROLE = TestHandler.class.getName();

    /**
     * Returns the passed parameter.
     *
     * @param message  The message to be returned
     * @return  The message passed
     */
    public String echo(String message);
}
