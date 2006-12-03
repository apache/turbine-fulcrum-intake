SET IGNORECASE TRUE

-----------------------------------------------------------------------------
-- FULCRUM_PERMISSION
-----------------------------------------------------------------------------
drop table FULCRUM_PERMISSION if exists;

CREATE TABLE FULCRUM_PERMISSION
(
    PERMISSION_ID INTEGER NOT NULL,
    PERMISSION_NAME VARCHAR(64) NOT NULL,
    PRIMARY KEY(PERMISSION_ID),
    UNIQUE (PERMISSION_NAME)
);


-----------------------------------------------------------------------------
-- FULCRUM_ROLE
-----------------------------------------------------------------------------
drop table FULCRUM_ROLE if exists;

CREATE TABLE FULCRUM_ROLE
(
    ROLE_ID INTEGER NOT NULL,
    ROLE_NAME VARCHAR(64) NOT NULL,
    PRIMARY KEY(ROLE_ID),
    UNIQUE (ROLE_NAME)
);


-----------------------------------------------------------------------------
-- FULCRUM_GROUP
-----------------------------------------------------------------------------
drop table FULCRUM_GROUP if exists;

CREATE TABLE FULCRUM_GROUP
(
    GROUP_ID INTEGER NOT NULL,
    GROUP_NAME VARCHAR(64) NOT NULL,
    PRIMARY KEY(GROUP_ID),
    UNIQUE (GROUP_NAME)
);


-----------------------------------------------------------------------------
-- FULCRUM_USER
-----------------------------------------------------------------------------
drop table FULCRUM_USER if exists;

CREATE TABLE FULCRUM_USER
(
    USER_ID INTEGER NOT NULL,
    LOGIN_NAME VARCHAR(64) NOT NULL,
    PASSWORD_VALUE VARCHAR(16) NOT NULL,
    PRIMARY KEY(USER_ID),
    UNIQUE (LOGIN_NAME)
);


-----------------------------------------------------------------------------
-- BASIC_USER_GROUP
-----------------------------------------------------------------------------
drop table BASIC_USER_GROUP if exists;

CREATE TABLE BASIC_USER_GROUP
(
    USER_ID INTEGER NOT NULL,
    GROUP_ID INTEGER NOT NULL,
    PRIMARY KEY(USER_ID,GROUP_ID)
);


-----------------------------------------------------------------------------
-- DYNAMIC_ROLE_PERMISSION
-----------------------------------------------------------------------------
drop table DYNAMIC_ROLE_PERMISSION if exists;

CREATE TABLE DYNAMIC_ROLE_PERMISSION
(
    ROLE_ID INTEGER NOT NULL,
    PERMISSION_ID INTEGER NOT NULL,
    PRIMARY KEY(ROLE_ID,PERMISSION_ID)
);


-----------------------------------------------------------------------------
-- DYNAMIC_USER_GROUP
-----------------------------------------------------------------------------
drop table DYNAMIC_USER_GROUP if exists;

CREATE TABLE DYNAMIC_USER_GROUP
(
    USER_ID INTEGER NOT NULL,
    GROUP_ID INTEGER NOT NULL,
    PRIMARY KEY(USER_ID,GROUP_ID)
);


-----------------------------------------------------------------------------
-- DYNAMIC_GROUP_ROLE
-----------------------------------------------------------------------------
drop table DYNAMIC_GROUP_ROLE if exists;

CREATE TABLE DYNAMIC_GROUP_ROLE
(
    GROUP_ID INTEGER NOT NULL,
    ROLE_ID INTEGER NOT NULL,
    PRIMARY KEY(GROUP_ID,ROLE_ID)
);


-----------------------------------------------------------------------------
-- DYNAMIC_USER_DELEGATES
-----------------------------------------------------------------------------
drop table DYNAMIC_USER_DELEGATES if exists;

CREATE TABLE DYNAMIC_USER_DELEGATES
(
    DELEGATOR_USER_ID INTEGER NOT NULL,
    DELEGATEE_USER_ID INTEGER NOT NULL,
    PRIMARY KEY(DELEGATOR_USER_ID,DELEGATEE_USER_ID)
);


-----------------------------------------------------------------------------
-- TURBINE_ROLE_PERMISSION
-----------------------------------------------------------------------------
drop table TURBINE_ROLE_PERMISSION if exists;

CREATE TABLE TURBINE_ROLE_PERMISSION
(
    ROLE_ID INTEGER NOT NULL,
    PERMISSION_ID INTEGER NOT NULL,
    PRIMARY KEY(ROLE_ID,PERMISSION_ID)
);


-----------------------------------------------------------------------------
-- TURBINE_USER_GROUP_ROLE
-----------------------------------------------------------------------------
drop table TURBINE_USER_GROUP_ROLE if exists;

CREATE TABLE TURBINE_USER_GROUP_ROLE
(
    USER_ID INTEGER NOT NULL,
    GROUP_ID INTEGER NOT NULL,
    ROLE_ID INTEGER NOT NULL,
    PRIMARY KEY(USER_ID,GROUP_ID,ROLE_ID)
);

