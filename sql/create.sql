CREATE TABLE USERS
(
    uid         INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name        VARCHAR(64) NOT NULL,
    gh_id       VARCHAR(16) NOT NULL,
    gh_token    VARCHAR(256) NOT NULL, -- Encrypted GitHub access token
    cg_token    VARCHAR(256) -- Encrypted? CodeGarten access token
);

CREATE TABLE CLASSROOM
(
    cid         INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    org_id      VARCHAR(16) NOT NULL,  -- GitHub organization id
    name        VARCHAR(64) NOT NULL,
    description VARCHAR(256)
);

CREATE TABLE ASSIGNMENT
(
    aid         INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name        VARCHAR(64) NOT NULL,
    description VARCHAR(256),
    type        VARCHAR(16) NOT NULL CHECK (type IN ('individual', 'group')),
    repo_prefix VARCHAR(64) NOT NULL,
    template    VARCHAR(256) -- Link to GitHub template repository
);

CREATE TABLE DELIVERY
(
    tag         VARCHAR(64),
    due_date    TIMESTAMP,
    aid         INT REFERENCES ASSIGNMENT(aid),
    PRIMARY KEY(tag, aid)
);

CREATE TABLE USER_CLASSROOM
(
    type        VARCHAR(16) NOT NULL CHECK (type IN ('teacher', 'student')),
    uid         INT REFERENCES USERS(uid),
    cid         INT REFERENCES CLASSROOM(cid),
    PRIMARY KEY(uid, cid)
);

CREATE TABLE USER_ASSIGNMENT
(
    uid         INT REFERENCES USERS(uid),
    aid         INT REFERENCES ASSIGNMENT(aid),
    PRIMARY KEY(uid, aid)
);