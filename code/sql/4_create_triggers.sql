-- Make sure this trigger is executed with transaction level SERIALIZABLE
CREATE TRIGGER trig_create_classroom_seq
BEFORE INSERT ON CLASSROOM
FOR EACH ROW 
EXECUTE PROCEDURE func_create_classroom_seq();

-- Make sure this trigger is executed with transaction level SERIALIZABLE
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

CREATE TRIGGER trig_get_delivery_number
BEFORE INSERT ON DELIVERY
FOR EACH ROW 
EXECUTE PROCEDURE func_get_delivery_number();
