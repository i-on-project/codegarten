CREATE VIEW V_ASSIGNMENT AS
SELECT 
    aid,
    ASSIGNMENT.name AS name,
    ASSIGNMENT.description AS description,
    TYPE,
    repo_prefix,
    template,
    
    CLASSROOM.cid AS classroom_id,
    CLASSROOM.name AS classroom_name
FROM ASSIGNMENT JOIN CLASSROOM ON (ASSIGNMENT.cid = CLASSROOM.cid);

CREATE VIEW V_DELIVERY AS
SELECT
    tag,
    due_date,
    
    ASSIGNMENT.aid AS assignment_id,
    ASSIGNMENT.name AS assignment_name
FROM DELIVERY JOIN ASSIGNMENT ON (DELIVERY.aid = ASSIGNMENT.aid);