-- Triggers for sequences
CREATE TRIGGER trig_create_installation_seq
AFTER INSERT ON INSTALLATION
FOR EACH ROW 
EXECUTE PROCEDURE func_create_installation_seq();

CREATE TRIGGER trig_cleanup_installation_seq
AFTER DELETE ON INSTALLATION
FOR EACH ROW 
EXECUTE PROCEDURE func_cleanup_installation_seq();

CREATE TRIGGER trig_create_classroom_seq
BEFORE INSERT ON CLASSROOM
FOR EACH ROW 
EXECUTE PROCEDURE func_create_classroom_seq();

CREATE TRIGGER trig_cleanup_classroom_seq
BEFORE DELETE ON CLASSROOM
FOR EACH ROW 
EXECUTE PROCEDURE func_cleanup_classroom_seq();

CREATE TRIGGER trig_get_assignment_number
BEFORE INSERT ON ASSIGNMENT
FOR EACH ROW 
EXECUTE PROCEDURE func_get_assignment_number();

CREATE TRIGGER trig_cleanup_assignment_seq
BEFORE DELETE ON ASSIGNMENT
FOR EACH ROW 
EXECUTE PROCEDURE func_cleanup_assignment_seq();

CREATE TRIGGER trig_get_team_number
BEFORE INSERT ON TEAM
FOR EACH ROW 
EXECUTE PROCEDURE func_get_team_number();

CREATE TRIGGER trig_get_delivery_number
BEFORE INSERT ON DELIVERY
FOR EACH ROW 
EXECUTE PROCEDURE func_get_delivery_number();


-- Remove user dependencies when leaving a classroom
CREATE TRIGGER trig_cleanup_user_dependencies
BEFORE DELETE ON USER_CLASSROOM
FOR EACH ROW
EXECUTE PROCEDURE func_cleanup_user_dependencies();