package org.apache.fulcrum.yaafi.framework.util;

import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.DefaultContext;

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

/**
 * Helper for manipulating the Avalon context. The default implementation
 * throws a ContextException if you try to access a non-existing entry.
 * 
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a> 
 */

public class AvalonContextHelper
{
	/** 
	 * Determines if an entry exists within the given context. 
	 * 
	 * @param context the contect to look at
	 * @param name the name of the parameter within the context  
	 */
	
	public static boolean isInContext( Context context, String name )
	{
	    if( context == null )
	    {
	        return false;
	    }
	    
	    try
        {
            if( context.get(name) != null )
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        catch (ContextException e)
        {
            return false;
        }
	}
	
	/**
	 * Add a new entry to the context by creating a new one.
	 * @param context our context
	 * @param name the name of the new entry
	 * @param value the value of the new entry
	 * @return the updated context
	 */
	public static Context addToContext( Context context, String name, Object value )
	{
	    DefaultContext result = new DefaultContext( context );
	    result.put( name, value );
	    return result;
	}
}