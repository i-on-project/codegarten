-- Delete table entries
DELETE FROM DELIVERY;
DELETE FROM USER_ASSIGNMENT;
DELETE FROM ASSIGNMENT;
DELETE FROM USER_CLASSROOM;
DELETE FROM CLASSROOM;
DELETE FROM USERS;

-- Restart identity columns
ALTER SEQUENCE USERS_uid_seq restart;
ALTER SEQUENCE CLASSROOM_cid_seq restart;
ALTER SEQUENCE ASSIGNMENT_aid_seq restart;