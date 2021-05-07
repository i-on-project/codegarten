CREATE VIEW V_ASSIGNMENT AS
SELECT 
    aid,
    ASSIGNMENT.inv_code AS inv_code,
    ASSIGNMENT.number AS number,
    ASSIGNMENT.name AS name,
    ASSIGNMENT.description AS description,
    type,
    repo_prefix,
    repo_template,
    
    CLASSROOM.org_id AS org_id,
    CLASSROOM.cid AS classroom_id,
    CLASSROOM.number AS classroom_number,
    CLASSROOM.name AS classroom_name
FROM ASSIGNMENT JOIN CLASSROOM ON (ASSIGNMENT.cid = CLASSROOM.cid);

CREATE VIEW V_TEAM AS
SELECT
    tid,
    TEAM.number,            
    TEAM.name,             
    gh_id,
    
    CLASSROOM.org_id AS org_id,
    CLASSROOM.cid AS classroom_id,
    CLASSROOM.number AS classroom_number,
    CLASSROOM.name AS classroom_name
FROM TEAM JOIN CLASSROOM ON (TEAM.cid = CLASSROOM.cid);

CREATE VIEW V_DELIVERY AS
SELECT
    did,
    DELIVERY.number AS number,
    tag,
    due_date,
    
    V_ASSIGNMENT.aid AS assignment_id,
    V_ASSIGNMENT.number AS assignment_number,
    V_ASSIGNMENT.name AS assignment_name,
    
    org_id,
    classroom_id,
    classroom_number,
    classroom_name
FROM DELIVERY JOIN V_ASSIGNMENT ON (DELIVERY.aid = V_ASSIGNMENT.aid);

CREATE VIEW V_USER_CLASSROOM AS
SELECT
    USERS.uid,
    USERS.name AS name,
    gh_id,
    gh_token,
    
    USER_CLASSROOM.type AS classroom_role,
    USER_CLASSROOM.cid AS classroom_id
FROM USERS JOIN USER_CLASSROOM ON (USERS.uid = USER_CLASSROOM.uid);

CREATE VIEW V_USER_ASSIGNMENT AS
SELECT
    USERS.uid,
    USERS.name AS name,
    gh_id,
    gh_token,
    
    USER_ASSIGNMENT.repo_id AS repo_id,
    USER_ASSIGNMENT.aid AS assignment_id
FROM USERS JOIN USER_ASSIGNMENT ON (USERS.uid = USER_ASSIGNMENT.uid);

CREATE VIEW V_TEAM_USER_ASSIGNMENT AS
SELECT
    USER_TEAM.uid,
    USER_TEAM.tid,
    TEAM_ASSIGNMENT.aid
FROM USER_TEAM JOIN TEAM_ASSIGNMENT ON (USER_TEAM.tid = TEAM_ASSIGNMENT.tid);

CREATE VIEW V_TEAM_ASSIGNMENT AS
SELECT
    TEAM.tid,
    TEAM.number,
    TEAM.name AS name,
    gh_id,
    
    TEAM_ASSIGNMENT.repo_id AS repo_id,
    TEAM_ASSIGNMENT.aid AS assignment_id
FROM TEAM JOIN TEAM_ASSIGNMENT ON (TEAM.tid = TEAM_ASSIGNMENT.tid);