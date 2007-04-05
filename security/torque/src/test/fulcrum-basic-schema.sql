
-----------------------------------------------------------------------------
-- FULCRUM_BASIC_GROUP
-----------------------------------------------------------------------------
drop table FULCRUM_BASIC_GROUP if exists CASCADE;

CREATE TABLE FULCRUM_BASIC_GROUP
(
    GROUP_ID INTEGER NOT NULL,
    GROUP_NAME VARCHAR(64) NOT NULL,
    PRIMARY KEY(GROUP_ID),
    UNIQUE (GROUP_NAME)
);


-----------------------------------------------------------------------------
-- FULCRUM_BASIC_USER
-----------------------------------------------------------------------------
drop table FULCRUM_BASIC_USER if exists CASCADE;

CREATE TABLE FULCRUM_BASIC_USER
(
    USER_ID INTEGER NOT NULL,
    LOGIN_NAME VARCHAR(64) NOT NULL,
    PASSWORD_VALUE VARCHAR(16) NOT NULL,
    PRIMARY KEY(USER_ID),
    UNIQUE (LOGIN_NAME)
);


-----------------------------------------------------------------------------
-- FULCRUM_BASIC_USER_GROUP
-----------------------------------------------------------------------------
drop table FULCRUM_BASIC_USER_GROUP if exists CASCADE;

CREATE TABLE FULCRUM_BASIC_USER_GROUP
(
    USER_ID INTEGER NOT NULL,
    GROUP_ID INTEGER NOT NULL,
    PRIMARY KEY(USER_ID,GROUP_ID)
);





    ALTER TABLE FULCRUM_BASIC_USER_GROUP
        ADD CONSTRAINT FULCRUM_BASIC_USER_GROUP_FK_1 FOREIGN KEY (USER_ID)
            REFERENCES FULCRUM_BASIC_USER (USER_ID)
;
    ALTER TABLE FULCRUM_BASIC_USER_GROUP
        ADD CONSTRAINT FULCRUM_BASIC_USER_GROUP_FK_2 FOREIGN KEY (GROUP_ID)
            REFERENCES FULCRUM_BASIC_GROUP (GROUP_ID)
;


