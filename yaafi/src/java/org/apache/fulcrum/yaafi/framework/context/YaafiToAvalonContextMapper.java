package org.apache.fulcrum.yaafi.framework.context;

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
 * Helper for converting a YAAFI context to a different container
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public class YaafiToAvalonContextMapper
{
    /** the name of the component for which we create the context */
    private String urnAvalonName;

    /** the classloader of the component */
    private ClassLoader urnAvalonClassLoader;

    /**
     * Constructor
     *
     * @param urnAvalonName the name of the component for which we create the context
     * @param urnAvalonClassLoader the classloader of the component
     */
    public YaafiToAvalonContextMapper( String urnAvalonName, ClassLoader urnAvalonClassLoader )
    {
        Validate.notEmpty( urnAvalonName, "urnAvalonName" );
        Validate.notNull( urnAvalonClassLoader, "urnAvalonClassLoader" );

        this.urnAvalonName = urnAvalonName;
        this.urnAvalonClassLoader = urnAvalonClassLoader;
    }

    /**
     * @return Returns the urnAvalonClassLoader.
     */
    public ClassLoader getUrnAvalonClassLoader()
    {
        return urnAvalonClassLoader;
    }

    /**
     * @return Returns the urnAvalonName.
     */
    public String getUrnAvalonName()
    {
        return urnAvalonName;
    }

    /**
     * Map a YAAFI (Merlin) context to a different incarnation
     *
     * @param context the context to be mapped
     * @param to the target Avalon container
     * @return the mapped context
     * @throws ContextException accessing the context failed
     */
    public DefaultContext mapTo( Context context, String to )
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
        else if( AvalonYaafiConstants.AVALON_CONTAINER_YAAFI.equals(to) )
        {
            return mapToYaafi(context);
        }
        else
        {
            String msg = "Don't know the following container type : " + to;
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Map to a Phoenix context
     *
     * @param context the original context
     * @return the mapped context
     * @throws ContextException accessing the context failed
     */
    private DefaultContext mapToPhoenix( Context context )
        throws ContextException
    {
        DefaultContext result = createDefaultContext(context);

        String urnAvalonPartition = (String) context.get(AvalonYaafiConstants.URN_AVALON_PARTITION);
        File urnAvalonHome = (File) context.get(AvalonYaafiConstants.URN_AVALON_HOME);
        String urnAvalonName = this.getUrnAvalonName();

        result.put(AvalonPhoenixConstants.PHOENIX_APP_NAME,urnAvalonPartition);
        result.put(AvalonPhoenixConstants.PHOENIX_APP_HOME,urnAvalonHome);
        result.put(AvalonPhoenixConstants.PHOENIX_BLOCK_NAME,urnAvalonName);

        return result;
    }

    /**
     * Map to a Fortress context
     *
     * @param context the original context
     * @return the mapped context
     * @throws ContextException accessing the context failed
     */
    private DefaultContext mapToFortress( Context context )
        throws ContextException
    {
        DefaultContext result = createDefaultContext(context);

        String urnAvalonPartition = (String) context.get(AvalonYaafiConstants.URN_AVALON_PARTITION);
        File urnAvalonHome = (File) context.get(AvalonYaafiConstants.URN_AVALON_HOME);
        File urnAvalonTemp = (File) context.get(AvalonYaafiConstants.URN_AVALON_TEMP);
        String urnAvalonName = this.getUrnAvalonName();

        result.put(AvalonFortressConstants.FORTRESS_COMPONENT_ID,urnAvalonPartition);
        result.put(AvalonFortressConstants.FORTRESS_COMPONENT_LOGGER,urnAvalonName);
        result.put(AvalonFortressConstants.FORTRESS_CONTEXT_ROOT,urnAvalonHome);
        result.put(AvalonFortressConstants.FORTRESS_IMPL_WORKDIR,urnAvalonTemp);

        return result;
    }

    /**
     * Map to a Merlin context. Actually there is nothing to do but
     * we do the full monty to ensure that context mannipulation wirks.
     *
     * @param context the original context
     * @return the mapped context
     * @throws ContextException accessing the context failed
     */
    private DefaultContext mapToMerlin( Context context )
        throws ContextException
    {
        DefaultContext result = createDefaultContext(context);

        String urnAvalonPartition = (String) context.get(AvalonYaafiConstants.URN_AVALON_PARTITION);
        File urnAvalonHome = (File) context.get(AvalonYaafiConstants.URN_AVALON_HOME);
        File urnAvalonTemp = (File) context.get(AvalonYaafiConstants.URN_AVALON_TEMP);
        String urnAvalonName = this.getUrnAvalonName();
        ClassLoader urnAvalonClossLoader = this.getUrnAvalonClassLoader();

        result.put(AvalonMerlinConstants.URN_AVALON_PARTITION,urnAvalonPartition);
        result.put(AvalonMerlinConstants.URN_AVALON_NAME,urnAvalonName);
        result.put(AvalonMerlinConstants.URN_AVALON_HOME,urnAvalonHome);
        result.put(AvalonMerlinConstants.URN_AVALON_TEMP,urnAvalonTemp);
        result.put(AvalonMerlinConstants.URN_AVALON_CLASSLOADER,urnAvalonClossLoader);

        return result;
    }

    /**
     * Map to a YAAFI context.
     *
     * @param context the original context
     * @return the mapped context
     * @throws ContextException accessing the context failed
     */
    private DefaultContext mapToYaafi( Context context )
        throws ContextException
    {
        DefaultContext result = createDefaultContext(context);

        String urnAvalonPartition = (String) context.get(AvalonYaafiConstants.URN_AVALON_PARTITION);
        File urnAvalonHome = (File) context.get(AvalonYaafiConstants.URN_AVALON_HOME);
        File urnAvalonTemp = (File) context.get(AvalonYaafiConstants.URN_AVALON_TEMP);
        String urnAvalonName = this.getUrnAvalonName();
        ClassLoader urnAvalonClossLoader = this.getUrnAvalonClassLoader();

        result.put(AvalonYaafiConstants.URN_AVALON_PARTITION,urnAvalonPartition);
        result.put(AvalonYaafiConstants.URN_AVALON_NAME,urnAvalonName);
        result.put(AvalonYaafiConstants.URN_AVALON_HOME,urnAvalonHome);
        result.put(AvalonYaafiConstants.URN_AVALON_TEMP,urnAvalonTemp);
        result.put(AvalonYaafiConstants.URN_AVALON_CLASSLOADER,urnAvalonClossLoader);
        result.put(AvalonYaafiConstants.COMPONENT_APP_ROOT,urnAvalonHome.getAbsolutePath());

        return result;
    }

    /**
     * Create a context to work with
     */
    private DefaultContext createDefaultContext(Context context)
    {
        return new DefaultContext(context);
    }
}
