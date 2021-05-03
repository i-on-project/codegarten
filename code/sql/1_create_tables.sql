CREATE TABLE USERS
(
    uid             INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name            VARCHAR(64) NOT NULL,
    gh_id           INT NOT NULL UNIQUE,
    gh_token        VARCHAR(256) NOT NULL -- Encrypted GitHub access token
);

CREATE TABLE INSTALLATION
(
    org_id          INT PRIMARY KEY NOT NULL, -- GitHub organization id
    iid             INT UNIQUE NOT NULL,
    access_token    VARCHAR(256) NOT NULL, -- Encrypted GitHub installation access token
    expiration_date TIMESTAMP NOT NULL
);

CREATE TABLE CLASSROOM
(
    cid             INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    org_id          INT REFERENCES INSTALLATION(org_id) ON DELETE CASCADE NOT NULL,
    number          INT NOT NULL,  -- Number in relation to the organization
    name            VARCHAR(64) NOT NULL,
    description     VARCHAR(256),
    UNIQUE(org_id, number),
    UNIQUE(org_id, name)
);

CREATE TABLE ASSIGNMENT
(
    aid             INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    cid             INT REFERENCES CLASSROOM(cid) ON DELETE CASCADE NOT NULL,
    number          INT NOT NULL,  -- Number in relation to the classroom
    name            VARCHAR(64) NOT NULL,
    description     VARCHAR(256),
    type            VARCHAR(16) NOT NULL CHECK (type IN ('individual', 'group')),
    repo_prefix     VARCHAR(64) NOT NULL,
    repo_template   INT, -- Id of the template repository
    UNIQUE(cid, number),
    UNIQUE(cid, name),
    UNIQUE(cid, repo_prefix)
);

CREATE TABLE DELIVERY
(
    did             INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    aid             INT REFERENCES ASSIGNMENT(aid) ON DELETE CASCADE NOT NULL,
    number          INT NOT NULL,  -- Number in relation to the assignment
    tag             VARCHAR(64),
    due_date        TIMESTAMP,
    UNIQUE(aid, number)
);

CREATE TABLE USER_CLASSROOM
(
    type            VARCHAR(16) NOT NULL CHECK (type IN ('teacher', 'student')),
    uid             INT REFERENCES USERS(uid) ON DELETE CASCADE NOT NULL,
    cid             INT REFERENCES CLASSROOM(cid) ON DELETE CASCADE NOT NULL,
    PRIMARY KEY(uid, cid)
);

CREATE TABLE USER_ASSIGNMENT
(
    uid             INT REFERENCES USERS(uid) ON DELETE CASCADE NOT NULL,
    aid             INT REFERENCES ASSIGNMENT(aid) ON DELETE CASCADE NOT NULL,
    repo_id         INT NOT NULL,
    PRIMARY KEY(uid, aid)
);

CREATE TABLE CLIENT
(
    cid             INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name            VARCHAR(64) NOT NULL,
    secret          VARCHAR(256) NOT NULL, -- Hashed client secret
    redirect_uri    VARCHAR(256) NOT NULL
);

CREATE TABLE AUTHCODE
(
    code            VARCHAR(256) PRIMARY KEY,
    expiration_date TIMESTAMP NOT NULL,
    user_id         INT NOT NULL REFERENCES USERS(uid) ON DELETE CASCADE NOT NULL,
    client_id       INT NOT NULL REFERENCES CLIENT(cid) ON DELETE CASCADE NOT NULL
);

CREATE TABLE ACCESSTOKEN
(
    token           VARCHAR(256) PRIMARY KEY, -- Hashed CodeGarten access token
    expiration_date TIMESTAMP NOT NULL,
    user_id         INT NOT NULL REFERENCES USERS(uid) ON DELETE CASCADE NOT NULL,
    client_id       INT NOT NULL REFERENCES CLIENT(cid) ON DELETE CASCADE NOT NULL
);