CREATE VIEW V_ASSIGNMENT AS
SELECT 
    aid,
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
    
    USER_CLASSROOM.type AS classroom_role
FROM USERS JOIN USER_CLASSROOM ON (USERS.uid = USER_CLASSROOM.uid);

CREATE VIEW V_USER AS
SELECT
    USERS.uid,
    USERS.name AS name,
    gh_id,
    gh_token,
    
    CLASSROOM.cid AS cid,
    CLASSROOM.org_id AS org_id,
    CLASSROOM.number AS classroom_number,
    CLASSROOM.name AS classroom_name,
    CLASSROOM.description AS classroom_description,
    USER_CLASSROOM.type AS classroom_role,
    
    ASSIGNMENT.aid AS aid,
    ASSIGNMENT.number AS assignment_number,
    ASSIGNMENT.name AS assignment_name,
    ASSIGNMENT.description AS assignment_description,
    
    ASSIGNMENT.type AS assignment_type,
    repo_prefix,
    repo_template
FROM USERS LEFT JOIN USER_CLASSROOM ON (USERS.uid = USER_CLASSROOM.uid)
           LEFT JOIN USER_ASSIGNMENT ON (USERS.uid = USER_ASSIGNMENT.uid)          
           LEFT JOIN CLASSROOM ON (USER_CLASSROOM.cid = CLASSROOM.cid)
           LEFT JOIN ASSIGNMENT ON (USER_ASSIGNMENT.aid = ASSIGNMENT.aid);
          