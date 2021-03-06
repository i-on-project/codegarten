-- Triggers for sequences
CREATE FUNCTION func_create_installation_seq()
RETURNS TRIGGER AS
$$
    BEGIN
        EXECUTE format('CREATE sequence classroom_number_seq_%s', NEW.org_id);
        RETURN NEW;
    END
$$
LANGUAGE 'plpgsql';

CREATE FUNCTION func_cleanup_installation_seq()
RETURNS TRIGGER AS
$$
    BEGIN
        EXECUTE format('DROP sequence classroom_number_seq_%s', OLD.org_id);
        RETURN OLD;
    END
$$
LANGUAGE 'plpgsql';

CREATE FUNCTION func_create_classroom_seq()
RETURNS TRIGGER AS
$$
    BEGIN  
        NEW.number = nextval('classroom_number_seq_' || NEW.org_id);
        EXECUTE format('CREATE sequence assignment_number_seq_%s', NEW.cid);
        EXECUTE format('CREATE sequence team_number_seq_%s', NEW.cid);
        RETURN NEW;
    END
$$
LANGUAGE 'plpgsql';

CREATE FUNCTION func_cleanup_classroom_seq()
RETURNS TRIGGER AS
$$
    BEGIN
        EXECUTE format('DROP sequence assignment_number_seq_%s', OLD.cid);
        EXECUTE format('DROP sequence team_number_seq_%s', OLD.cid);
        RETURN OLD;
    END
$$
LANGUAGE 'plpgsql';

CREATE FUNCTION func_get_assignment_number()
RETURNS TRIGGER AS
$$
    BEGIN
        NEW.number = nextval('assignment_number_seq_' || NEW.cid);
        EXECUTE format('CREATE sequence delivery_number_seq_%s', NEW.aid);
        RETURN NEW;
    END
$$
LANGUAGE 'plpgsql';

CREATE FUNCTION func_cleanup_assignment_seq()
RETURNS TRIGGER AS
$$
    BEGIN
        EXECUTE format('DROP sequence delivery_number_seq_%s', OLD.aid);
        RETURN OLD;
    END
$$
LANGUAGE 'plpgsql';

CREATE FUNCTION func_get_team_number()
RETURNS TRIGGER AS
$$
    BEGIN
        NEW.number = nextval('team_number_seq_' || NEW.cid);
        RETURN NEW;
    END
$$
LANGUAGE 'plpgsql';

CREATE FUNCTION func_get_delivery_number()
RETURNS TRIGGER AS
$$
    BEGIN
        NEW.number = nextval('delivery_number_seq_' || NEW.aid);
        RETURN NEW;
    END
$$
LANGUAGE 'plpgsql';


-- Remove user dependencies when leaving a classroom
CREATE FUNCTION func_cleanup_user_dependencies()
RETURNS TRIGGER AS
$$
    BEGIN
        DELETE FROM USER_ASSIGNMENT WHERE uid = OLD.uid;
        DELETE FROM USER_TEAM WHERE uid = OLD.uid;
        RETURN OLD;
    END
$$
LANGUAGE 'plpgsql';
