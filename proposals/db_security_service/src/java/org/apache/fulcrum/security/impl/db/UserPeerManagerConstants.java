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


import java.io.Serializable;

/**
 * Constants for configuring the various columns and bean properties
 * for the used peer.
 *
 * <pre>
 * Default is:
 *
 * security.db.userPeer.class = org.apache.fulcrum.security.impl.db.entity.TurbineUserPeer
 * security.db.userPeer.column.name       = LOGIN_NAME
 * security.db.userPeer.column.id         = USER_ID
 * security.db.userPeer.column.password   = PASSWORD_VALUE
 * security.db.userPeer.column.firstname  = FIRST_NAME
 * security.db.userPeer.column.lastname   = LAST_NAME
 * security.db.userPeer.column.email      = EMAIL
 * security.db.userPeer.column.confirm    = CONFIRM_VALUE
 * security.db.userPeer.column.createdate = CREATED
 * security.db.userPeer.column.lastlogin  = LAST_LOGIN
 * security.db.userPeer.column.objectdata = OBJECTDATA
 * 
 * security.db.user.class = org.apache.fulcrum.security.impl.db.entity.TurbineUser
 * security.db.user.property.name       = UserName
 * security.db.user.property.id         = UserId
 * security.db.user.property.password   = Password
 * security.db.user.property.firstname  = FirstName
 * security.db.user.property.lastname   = LastName
 * security.db.user.property.email      = Email
 * security.db.user.property.confirm    = Confirmed
 * security.db.user.property.createdate = CreateDate
 * security.db.user.property.lastlogin  = LastLogin
 * security.db.user.property.objectdata = Objectdata
 *
 * </pre>
 * If security.db.user.class is unset, then the value of the constant CLASSNAME_DEFAULT
 * from the configured Peer is used. 
 * 
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */

public interface UserPeerManagerConstants
    extends Serializable
{
    /** The key within the security service properties for the user class implementation */
    public static final String USER_CLASS_KEY = 
        "db.user.class";

    /** The key within the security service properties for the user peer class implementation */
    public static final String USER_PEER_CLASS_KEY = 
        "db.userPeer.class";

    /** User peer default class */
    public static final String USER_PEER_CLASS_DEFAULT =
        "org.apache.fulcrum.security.impl.db.entity.TurbineUserPeer";

    /** The column name for the login name field. */
    public static final String USER_NAME_COLUMN_KEY =
        "db.userPeer.column.name";

    /** The column name for the id field. */
    public static final String USER_ID_COLUMN_KEY =
        "db.userPeer.column.id";

    /** The column name for the password field. */
    public static final String USER_PASSWORD_COLUMN_KEY =
        "db.userPeer.column.password";

    /** The column name for the first name field. */
    public static final String USER_FIRST_NAME_COLUMN_KEY =
        "db.userPeer.column.firstname";

    /** The column name for the last name field. */
    public static final String USER_LAST_NAME_COLUMN_KEY =
        "db.userPeer.column.lastname";

    /** The column name for the email field. */
    public static final String USER_EMAIL_COLUMN_KEY =
        "db.userPeer.column.email";

    /** The column name for the confirm field. */
    public static final String USER_CONFIRM_COLUMN_KEY =
        "db.userPeer.column.confirm";

    /** The column name for the create date field. */
    public static final String USER_CREATE_COLUMN_KEY =
        "db.userPeer.column.createdate";

    /** The column name for the last login field. */
    public static final String USER_LAST_LOGIN_COLUMN_KEY =
        "db.userPeer.column.lastlogin";

    /** The column name for the objectdata field. */
    public static final String USER_OBJECTDATA_COLUMN_KEY =
        "db.userPeer.column.objectdata";


    /** The default value for the column name constant for the login name field. */
    public static final String USER_NAME_COLUMN_DEFAULT =
        "LOGIN_NAME";

    /** The default value for the column name constant for the id field. */
    public static final String USER_ID_COLUMN_DEFAULT =
        "USER_ID";

    /** The default value for the column name constant for the password field. */
    public static final String USER_PASSWORD_COLUMN_DEFAULT =
        "PASSWORD_VALUE";

    /** The default value for the column name constant for the first name field. */
    public static final String USER_FIRST_NAME_COLUMN_DEFAULT =
        "FIRST_NAME";

    /** The default value for the column name constant for the last name field. */
    public static final String USER_LAST_NAME_COLUMN_DEFAULT =
        "LAST_NAME";

    /** The default value for the column name constant for the email field. */
    public static final String USER_EMAIL_COLUMN_DEFAULT =
        "EMAIL";

    /** The default value for the column name constant for the confirm field. */
    public static final String USER_CONFIRM_COLUMN_DEFAULT =
        "CONFIRM_VALUE";

    /** The default value for the column name constant for the create date field. */
    public static final String USER_CREATE_COLUMN_DEFAULT =
        "CREATED";

    /** The default value for the column name constant for the last login field. */
    public static final String USER_LAST_LOGIN_COLUMN_DEFAULT =
        "LAST_LOGIN";

    /** The default value for the column name constant for the objectdata field. */
    public static final String USER_OBJECTDATA_COLUMN_DEFAULT =
        "OBJECTDATA";

    /** The property name of the bean property for the login name field. */
    public static final String USER_NAME_PROPERTY_KEY = 
        "db.user.property.name";
    
    /** The property name of the bean property for the id field. */
    public static final String USER_ID_PROPERTY_KEY =
        "db.user.property.id";
    
    /** The property name of the bean property for the password field. */
    public static final String USER_PASSWORD_PROPERTY_KEY =
        "db.user.property.password";
    
    /** The property name of the bean property for the first name field. */
    public static final String USER_FIRST_NAME_PROPERTY_KEY =
        "db.user.property.firstname";
    
    /** The property name of the bean property for the last name field. */
    public static final String USER_LAST_NAME_PROPERTY_KEY =
        "db.user.property.lastname";
    
    /** The property name of the bean property for the email field. */
    public static final String USER_EMAIL_PROPERTY_KEY =
        "db.user.property.email";
    
    /** The property name of the bean property for the confirm field. */
    public static final String USER_CONFIRM_PROPERTY_KEY =
        "db.user.property.confirm";
    
    /** The property name of the bean property for the create date field. */
    public static final String USER_CREATE_PROPERTY_KEY =
        "db.user.property.createdate";
    
    /** The property name of the bean property for the last login field. */
    public static final String USER_LAST_LOGIN_PROPERTY_KEY =
        "db.user.property.lastlogin";

    /** The property name of the bean property for the last login field. */
    public static final String USER_OBJECTDATA_PROPERTY_KEY =
        "db.user.property.objectdata";

    /** The default value of the bean property for the login name field. */
    public static final String USER_NAME_PROPERTY_DEFAULT =
        "UserName";

    /** The default value of the bean property for the id field. */
    public static final String USER_ID_PROPERTY_DEFAULT =
        "UserId";

    /** The default value of the bean property for the password field. */
    public static final String USER_PASSWORD_PROPERTY_DEFAULT =
        "Password";

    /** The default value of the bean property for the first name field. */
    public static final String USER_FIRST_NAME_PROPERTY_DEFAULT =
        "FirstName";

    /** The default value of the bean property for the last name field. */
    public static final String USER_LAST_NAME_PROPERTY_DEFAULT =
        "LastName";

    /** The default value of the bean property for the email field. */
    public static final String USER_EMAIL_PROPERTY_DEFAULT =
        "Email";

    /** The default value of the bean property for the confirm field. */
    public static final String USER_CONFIRM_PROPERTY_DEFAULT =
        "Confirmed";

    /** The default value of the bean property for the create date field. */
    public static final String USER_CREATE_PROPERTY_DEFAULT =
        "CreateDate";

    /** The default value of the bean property for the last login field. */
    public static final String USER_LAST_LOGIN_PROPERTY_DEFAULT =
        "LastLogin";

    /** The default value of the bean property for the objectdata field. */
    public static final String USER_OBJECTDATA_PROPERTY_DEFAULT =
        "Objectdata";
};

    
