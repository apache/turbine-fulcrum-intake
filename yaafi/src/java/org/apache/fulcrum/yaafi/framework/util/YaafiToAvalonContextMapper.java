package org.apache.fulcrum.yaafi.framework.util;

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

import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.fulcrum.yaafi.framework.container.AvalonFortressConstants;
import org.apache.fulcrum.yaafi.framework.container.AvalonMerlinConstants;
import org.apache.fulcrum.yaafi.framework.container.AvalonPhoenixConstants;

/**
 * Helper for converting a YAAFI context to a different container
 * 
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a> 
 */

public class YaafiToAvalonContextMapper
{    
    /**
     * Map a YAAFI (Merlin) context to a different incarnation
     * 
     * @param context the context to be mapped
     * @param to the target Avalon container
     * @return the mapped context
     * @throws ContextException accessing the context failed
     */
    public static Context mapTo( Context context, String to )
    	throws ContextException
    {
        Validate.notNull( context, "context" );
        Validate.notEmpty( to, "to" );
        
        if( AvalonPhoenixConstants.AVALON_CONTAINER_PHOENIX.equals(to) )
        {
            return mapToPhoenix(context);
            
        }
        else if( AvalonFortressConstants.AVALON_CONTAINER_FORTESS.equals(to) )
        {
            return mapToFortress(context);
            
        }
        else if( AvalonMerlinConstants.AVALON_CONTAINER_MERLIN.equals(to) )
        {
            return mapToMerlin(context);            
        }
        else
        {
            throw new IllegalArgumentException(to);
        }                
    }
    
    private static Context mapToPhoenix( Context context )
    	throws ContextException
    {
        return null;
    }

    private static Context mapToFortress( Context context )
		throws ContextException
	{
	    return null;
	}

    private static Context mapToMerlin( Context context )
		throws ContextException
	{
	    return null;
	}
}