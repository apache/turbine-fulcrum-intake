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
 * security.db.permissionPeer.class = org.apache.fulcrum.security.impl.db.entity.TurbinePermissionPeer
 * security.db.permissionPeer.column.name       = PERMISSION_NAME
 * security.db.permissionPeer.column.id         = PERMISSION_ID
 * 
 * security.db.permission.class = org.apache.fulcrum.security.impl.db.entity.TurbinePermission
 * security.db.permission.property.name       = Name
 * security.db.permission.property.id         = PermissionId
 *
 * </pre>
 * If security.db.permission.class is unset, then the value of the constant CLASSNAME_DEFAULT
 * from the configured Peer is used. 
 * 
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */

public interface PermissionPeerManagerConstants
{
    /** The key within the security service properties for the permission class implementation */
    public static final String PERMISSION_CLASS_KEY = 
        "db.permission.class";

    /** The key within the security service properties for the permission peer class implementation */
    public static final String PERMISSION_PEER_CLASS_KEY = 
        "db.permissionPeer.class";

    /** Permission peer default class */
    public static final String PERMISSION_PEER_CLASS_DEFAULT =
        "org.apache.fulcrum.security.impl.db.entity.TurbinePermissionPeer";

    /** The column name for the login name field. */
    public static final String PERMISSION_NAME_COLUMN_KEY =
        "db.permissionPeer.column.name";

    /** The column name for the id field. */
    public static final String PERMISSION_ID_COLUMN_KEY =
        "db.permissionPeer.column.id";


    /** The default value for the column name constant for the login name field. */
    public static final String PERMISSION_NAME_COLUMN_DEFAULT =
        "PERMISSION_NAME";

    /** The default value for the column name constant for the id field. */
    public static final String PERMISSION_ID_COLUMN_DEFAULT =
        "PERMISSION_ID";


    /** The property name of the bean property for the login name field. */
    public static final String PERMISSION_NAME_PROPERTY_KEY = 
        "db.permission.property.name";
    
    /** The property name of the bean property for the id field. */
    public static final String PERMISSION_ID_PROPERTY_KEY =
        "db.permission.property.id";
    

    /** The default value of the bean property for the login name field. */
    public static final String PERMISSION_NAME_PROPERTY_DEFAULT =
        "Name";

    /** The default value of the bean property for the id field. */
    public static final String PERMISSION_ID_PROPERTY_DEFAULT =
        "PermissionId";

};

    
