package org.apache.fulcrum.yaafi.framework.context;

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

import java.io.File;

import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.fulcrum.yaafi.framework.constant.AvalonFortressConstants;
import org.apache.fulcrum.yaafi.framework.constant.AvalonMerlinConstants;
import org.apache.fulcrum.yaafi.framework.constant.AvalonPhoenixConstants;
import org.apache.fulcrum.yaafi.framework.constant.AvalonYaafiConstants;
import org.apache.fulcrum.yaafi.framework.util.Validate;

/**
 * Helper for converting Avalon Context of Fortress and Phoenix
 * container to a standard Merlin context.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public class AvalonToYaafiContextMapper
{
    /** The directory for storing temporary files */
    private File tempRootDir;

    /** Our default context */
    private DefaultContext defaultContext;

    /**
     * Constructor
     *
     * @param tempRootDir current temp directory
     * @param context the existing context
     */
    public AvalonToYaafiContextMapper(
        File tempRootDir,
        Context context )
    {
        Validate.notNull( tempRootDir, "tempRootDir" );

        this.tempRootDir = tempRootDir;
        this.defaultContext = new DefaultContext( context );
    }

    /**
     * Map a Avalon context to the YAAFI (Merlin) incarnation whereas
     * the following containers are supported
     * <ul>
     *   <li>merlin</li>
     *   <li>fortress</li>
     *   <li>phoenix</li>
     * </ul>
     *
     * @param context the Avalon context to map
     * @param from Avalon container identifier
     * @return the mapped Avalon context
     * @throws ContextException Accessing the context failed
     */
    public Context mapFrom( Context context, String from )
        throws ContextException
    {
        Validate.notNull( context, "context" );
        Validate.notEmpty( from, "from" );

        if( AvalonPhoenixConstants.AVALON_CONTAINER_PHOENIX.equals(from) )
        {
            return mapFromPhoenix(context);

        }
        else if( AvalonFortressConstants.AVALON_CONTAINER_FORTESS.equals(from) )
        {
            return mapFromFortress(context);

        }
        else if( AvalonMerlinConstants.AVALON_CONTAINER_MERLIN.equals(from) )
        {
            return mapFromMerlin(context);
        }
        else if( AvalonYaafiConstants.AVALON_CONTAINER_YAAFI.equals(from) )
        {
            return mapFromMerlin(context);
        }
        else
        {
            String msg = "Don't know the following container type : " + from;
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Map a Avalon Phoenix context to the YAAFI (Merlin) incarnation
     *
     * @param context the Avalon context to map
     * @return the mapped Avalon context
     * @throws ContextException Accessing the context failed
     */
    private Context mapFromPhoenix(Context context)
        throws ContextException
    {
        DefaultContext result = this.getDefaultContext();

        String urnAvalonPartition = (String) context.get( AvalonPhoenixConstants.PHOENIX_APP_NAME );
        File urnAvalonHome = (File) context.get( AvalonPhoenixConstants.PHOENIX_APP_HOME );
        File urnAvalonTemp = this.tempRootDir;

        result.put( AvalonYaafiConstants.URN_AVALON_PARTITION, urnAvalonPartition );
        result.put( AvalonYaafiConstants.URN_AVALON_HOME, urnAvalonHome );
        result.put( AvalonYaafiConstants.URN_AVALON_TEMP, urnAvalonTemp );
        result.put( AvalonYaafiConstants.COMPONENT_APP_ROOT, urnAvalonHome.getAbsolutePath() );

        return result;
    }

    /**
     * Map a Avalon Fortress context to the YAAFI (Merlin) incarnation
     *
     * @param context the Avalon context to map
     * @return the mapped Avalon context
     * @throws ContextException Accessing the context failed
     */
    private Context mapFromFortress(Context context)
        throws ContextException
    {
        DefaultContext result = this.getDefaultContext();

        String urnAvalonPartition = (String) context.get( AvalonFortressConstants.FORTRESS_COMPONENT_ID );
        File urnAvalonHome = (File) context.get( AvalonFortressConstants.FORTRESS_CONTEXT_ROOT );
        File urnAvalonTemp = (File) context.get( AvalonFortressConstants.FORTRESS_IMPL_WORKDIR );

        result.put( AvalonYaafiConstants.URN_AVALON_PARTITION, urnAvalonPartition );
        result.put( AvalonYaafiConstants.URN_AVALON_HOME, urnAvalonHome );
        result.put( AvalonYaafiConstants.URN_AVALON_TEMP, urnAvalonTemp );
        result.put( AvalonYaafiConstants.COMPONENT_APP_ROOT, urnAvalonHome.getAbsolutePath() );

        return result;
    }

    /**
     * Map a Avalon Merlin context to the YAAFI (Merlin) incarnation
     *
     * @param context the Avalon context to map
     * @return the mapped Avalon context
     * @throws ContextException Accessing the context failed
     */
    private Context mapFromMerlin(Context context)
        throws ContextException
    {
        DefaultContext result = this.getDefaultContext();

        String urnAvalonPartition = (String) context.get(AvalonYaafiConstants.URN_AVALON_PARTITION);
        File urnAvalonHome = (File) context.get(AvalonYaafiConstants.URN_AVALON_HOME);
        File urnAvalonTemp = (File) context.get(AvalonYaafiConstants.URN_AVALON_TEMP);
        String urnAvalonName = (String) (String) context.get(AvalonYaafiConstants.URN_AVALON_NAME);
        ClassLoader urnAvalonClossLoader = (ClassLoader) context.get(AvalonYaafiConstants.URN_AVALON_CLASSLOADER);

        result.put(AvalonMerlinConstants.URN_AVALON_PARTITION,urnAvalonPartition);
        result.put(AvalonMerlinConstants.URN_AVALON_NAME,urnAvalonName);
        result.put(AvalonMerlinConstants.URN_AVALON_HOME,urnAvalonHome);
        result.put(AvalonMerlinConstants.URN_AVALON_TEMP,urnAvalonTemp);
        result.put(AvalonMerlinConstants.URN_AVALON_CLASSLOADER,urnAvalonClossLoader);
        result.put(AvalonYaafiConstants.COMPONENT_APP_ROOT, urnAvalonHome.getAbsolutePath());

        return result;

    }

    private DefaultContext getDefaultContext()
    {
        return this.defaultContext;
    }
}