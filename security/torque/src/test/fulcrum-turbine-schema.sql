
-----------------------------------------------------------------------------
-- FULCRUM_TURBINE_PERMISSION
-----------------------------------------------------------------------------
drop table FULCRUM_TURBINE_PERMISSION if exists CASCADE;

CREATE TABLE FULCRUM_TURBINE_PERMISSION
(
    PERMISSION_ID INTEGER NOT NULL,
    PERMISSION_NAME VARCHAR(64) NOT NULL,
    PRIMARY KEY(PERMISSION_ID),
    UNIQUE (PERMISSION_NAME)
);


-----------------------------------------------------------------------------
-- FULCRUM_TURBINE_ROLE
-----------------------------------------------------------------------------
drop table FULCRUM_TURBINE_ROLE if exists CASCADE;

CREATE TABLE FULCRUM_TURBINE_ROLE
(
    ROLE_ID INTEGER NOT NULL,
    ROLE_NAME VARCHAR(64) NOT NULL,
    PRIMARY KEY(ROLE_ID),
    UNIQUE (ROLE_NAME)
);


-----------------------------------------------------------------------------
-- FULCRUM_TURBINE_GROUP
-----------------------------------------------------------------------------
drop table FULCRUM_TURBINE_GROUP if exists CASCADE;

CREATE TABLE FULCRUM_TURBINE_GROUP
(
    GROUP_ID INTEGER NOT NULL,
    GROUP_NAME VARCHAR(64) NOT NULL,
    PRIMARY KEY(GROUP_ID),
    UNIQUE (GROUP_NAME)
);


-----------------------------------------------------------------------------
-- FULCRUM_TURBINE_USER
-----------------------------------------------------------------------------
drop table FULCRUM_TURBINE_USER if exists CASCADE;

CREATE TABLE FULCRUM_TURBINE_USER
(
    USER_ID INTEGER NOT NULL,
    LOGIN_NAME VARCHAR(64) NOT NULL,
    PASSWORD_VALUE VARCHAR(16) NOT NULL,
    PRIMARY KEY(USER_ID),
    UNIQUE (LOGIN_NAME)
);


-----------------------------------------------------------------------------
-- TURBINE_ROLE_PERMISSION
-----------------------------------------------------------------------------
drop table TURBINE_ROLE_PERMISSION if exists CASCADE;

CREATE TABLE TURBINE_ROLE_PERMISSION
(
    ROLE_ID INTEGER NOT NULL,
    PERMISSION_ID INTEGER NOT NULL,
    PRIMARY KEY(ROLE_ID,PERMISSION_ID)
);


-----------------------------------------------------------------------------
-- TURBINE_USER_GROUP_ROLE
-----------------------------------------------------------------------------
drop table TURBINE_USER_GROUP_ROLE if exists CASCADE;

CREATE TABLE TURBINE_USER_GROUP_ROLE
(
    USER_ID INTEGER NOT NULL,
    GROUP_ID INTEGER NOT NULL,
    ROLE_ID INTEGER NOT NULL,
    PRIMARY KEY(USER_ID,GROUP_ID,ROLE_ID)
);









    ALTER TABLE TURBINE_ROLE_PERMISSION
        ADD CONSTRAINT TURBINE_ROLE_PERMISSION_FK_1 FOREIGN KEY (ROLE_ID)
            REFERENCES FULCRUM_TURBINE_ROLE (ROLE_ID)
;
    ALTER TABLE TURBINE_ROLE_PERMISSION
        ADD CONSTRAINT TURBINE_ROLE_PERMISSION_FK_2 FOREIGN KEY (PERMISSION_ID)
            REFERENCES FULCRUM_TURBINE_PERMISSION (PERMISSION_ID)
;


    ALTER TABLE TURBINE_USER_GROUP_ROLE
        ADD CONSTRAINT TURBINE_USER_GROUP_ROLE_FK_1 FOREIGN KEY (USER_ID)
            REFERENCES FULCRUM_TURBINE_USER (USER_ID)
;
    ALTER TABLE TURBINE_USER_GROUP_ROLE
        ADD CONSTRAINT TURBINE_USER_GROUP_ROLE_FK_2 FOREIGN KEY (GROUP_ID)
            REFERENCES FULCRUM_TURBINE_GROUP (GROUP_ID)
;
    ALTER TABLE TURBINE_USER_GROUP_ROLE
        ADD CONSTRAINT TURBINE_USER_GROUP_ROLE_FK_3 FOREIGN KEY (ROLE_ID)
            REFERENCES FULCRUM_TURBINE_ROLE (ROLE_ID)
;


