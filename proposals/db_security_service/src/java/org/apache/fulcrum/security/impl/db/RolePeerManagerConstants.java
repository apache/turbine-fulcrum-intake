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
 * security.db.rolePeer.class = org.apache.fulcrum.security.impl.db.entity.TurbineRolePeer
 * security.db.rolePeer.column.name       = ROLE_NAME
 * security.db.rolePeer.column.id         = ROLE_ID
 * 
 * security.db.role.class = org.apache.fulcrum.security.impl.db.entity.TurbineRole
 * security.db.role.property.name       = Name
 * security.db.role.property.id         = RoleId
 *
 * </pre>
 * If security.db.role.class is unset, then the value of the constant CLASSNAME_DEFAULT
 * from the configured Peer is used. 
 * 
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */

public interface RolePeerManagerConstants
{
    /** The key within the security service properties for the role class implementation */
    public static final String ROLE_CLASS_KEY = 
        "db.role.class";

    /** The key within the security service properties for the role peer class implementation */
    public static final String ROLE_PEER_CLASS_KEY = 
        "db.rolePeer.class";

    /** Role peer default class */
    public static final String ROLE_PEER_CLASS_DEFAULT =
        "org.apache.fulcrum.security.impl.db.entity.TurbineRolePeer";

    /** The column name for the login name field. */
    public static final String ROLE_NAME_COLUMN_KEY =
        "db.rolePeer.column.name";

    /** The column name for the id field. */
    public static final String ROLE_ID_COLUMN_KEY =
        "db.rolePeer.column.id";


    /** The default value for the column name constant for the login name field. */
    public static final String ROLE_NAME_COLUMN_DEFAULT =
        "ROLE_NAME";

    /** The default value for the column name constant for the id field. */
    public static final String ROLE_ID_COLUMN_DEFAULT =
        "ROLE_ID";


    /** The property name of the bean property for the login name field. */
    public static final String ROLE_NAME_PROPERTY_KEY = 
        "db.role.property.name";
    
    /** The property name of the bean property for the id field. */
    public static final String ROLE_ID_PROPERTY_KEY =
        "db.role.property.id";
    

    /** The default value of the bean property for the login name field. */
    public static final String ROLE_NAME_PROPERTY_DEFAULT =
        "Name";

    /** The default value of the bean property for the id field. */
    public static final String ROLE_ID_PROPERTY_DEFAULT =
        "RoleId";

};

    
