package org.apache.fulcrum.security.impl.db;


/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


/**
 * Constants for configuring the various columns and bean properties
 * for the used peer.
 *
 * <pre>
 * Default is:
 *
 * security.db.groupPeer.class = org.apache.fulcrum.security.impl.db.entity.TurbineGroupPeer
 * security.db.groupPeer.column.name       = GROUP_NAME
 * security.db.groupPeer.column.id         = GROUP_ID
 * 
 * security.db.group.class = org.apache.fulcrum.security.impl.db.entity.TurbineGroup
 * security.db.group.property.name       = Name
 * security.db.group.property.id         = GroupId
 *
 * </pre>
 * If security.db.group.class is unset, then the value of the constant CLASSNAME_DEFAULT
 * from the configured Peer is used. 
 * 
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */

public interface GroupPeerManagerConstants
{
    /** The key within the security service properties for the group class implementation */
    public static final String GROUP_CLASS_KEY = 
        "db.group.class";

    /** The key within the security service properties for the group peer class implementation */
    public static final String GROUP_PEER_CLASS_KEY = 
        "db.groupPeer.class";

    /** Group peer default class */
    public static final String GROUP_PEER_CLASS_DEFAULT =
        "org.apache.fulcrum.security.impl.db.entity.TurbineGroupPeer";

    /** The column name for the login name field. */
    public static final String GROUP_NAME_COLUMN_KEY =
        "db.groupPeer.column.name";

    /** The column name for the id field. */
    public static final String GROUP_ID_COLUMN_KEY =
        "db.groupPeer.column.id";


    /** The default value for the column name constant for the login name field. */
    public static final String GROUP_NAME_COLUMN_DEFAULT =
        "GROUP_NAME";

    /** The default value for the column name constant for the id field. */
    public static final String GROUP_ID_COLUMN_DEFAULT =
        "GROUP_ID";


    /** The property name of the bean property for the login name field. */
    public static final String GROUP_NAME_PROPERTY_KEY = 
        "db.group.property.name";
    
    /** The property name of the bean property for the id field. */
    public static final String GROUP_ID_PROPERTY_KEY =
        "db.group.property.id";
    

    /** The default value of the bean property for the login name field. */
    public static final String GROUP_NAME_PROPERTY_DEFAULT =
        "Name";

    /** The default value of the bean property for the id field. */
    public static final String GROUP_ID_PROPERTY_DEFAULT =
        "GroupId";

};

    
