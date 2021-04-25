CREATE VIEW V_ASSIGNMENT AS
SELECT 
    aid,
    ASSIGNMENT.number AS number,
    ASSIGNMENT.name AS name,
    ASSIGNMENT.description AS description,
    type,
    repo_prefix,
    template,
    
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