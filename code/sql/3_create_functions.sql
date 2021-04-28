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
        RETURN NEW;
    END
$$
LANGUAGE 'plpgsql';

CREATE FUNCTION func_cleanup_classroom_seq()
RETURNS TRIGGER AS
$$
    BEGIN
        EXECUTE format('DROP sequence assignment_number_seq_%s', OLD.cid);
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

CREATE FUNCTION func_get_delivery_number()
RETURNS TRIGGER AS
$$
    BEGIN
        NEW.number = nextval('delivery_number_seq_' || NEW.aid);
        RETURN NEW;
    END
$$
LANGUAGE 'plpgsql';