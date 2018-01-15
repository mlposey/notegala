-- Tested with PostgreSQL 9.6

CREATE TABLE users (
    id serial PRIMARY KEY,
    created_at timestamptz NOT NULL DEFAULT NOW(),
    last_seen timestamptz NOT NULL DEFAULT NOW(),
    email text UNIQUE NOT NULL,
    name text NOT NULL
);

CREATE TABLE tags (
    id serial PRIMARY KEY,
    label text UNIQUE NOT NULL,
    label_tsv tsvector
);

CREATE TRIGGER label_tsvupdate BEFORE INSERT OR UPDATE
ON tags FOR EACH ROW EXECUTE PROCEDURE
tsvector_update_trigger(label_tsv, 'pg_catalog.english', label);

CREATE TABLE notes (
    id serial PRIMARY KEY,
    created_at timestamptz NOT NULL DEFAULT NOW(),
    last_modified timestamptz NOT NULL DEFAULT NOW(),
    is_public boolean NOT NULL DEFAULT FALSE,
    owner_id integer NOT NULL REFERENCES users(id),
    title text NOT NULL DEFAULT '',
    title_tsv tsvector,
    body text NOT NULL DEFAULT '',
    body_tsv tsvector,
    title_body_tsv tsvector
);

CREATE TRIGGER title_tsvupdate BEFORE INSERT OR UPDATE
ON notes FOR EACH ROW EXECUTE PROCEDURE
tsvector_update_trigger(title_tsv, 'pg_catalog.english', title);

CREATE TRIGGER body_tsvupdate BEFORE INSERT OR UPDATE
ON notes FOR EACH ROW EXECUTE PROCEDURE
tsvector_update_trigger(body_tsv, 'pg_catalog.english', body);

CREATE TRIGGER title_body_tsvupdate BEFORE INSERT OR UPDATE
ON notes FOR EACH ROW EXECUTE PROCEDURE
tsvector_update_trigger(title_body_tsv, 'pg_catalog.english', title, body);

-- Updates the system state when a note is modified in the notes table
CREATE FUNCTION on_note_modified() RETURNS trigger AS $$
BEGIN
    UPDATE notes
    SET last_modified = NOW()
    WHERE id = NEW.id;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER note_modified AFTER UPDATE
ON notes FOR EACH ROW WHEN (OLD.is_public IS DISTINCT FROM NEW.is_public OR
                            OLD.owner_id  IS DISTINCT FROM NEW.owner_id  OR
                            OLD.title     IS DISTINCT FROM NEW.title     OR
                            OLD.body      IS DISTINCT FROM NEW.BODY)
EXECUTE PROCEDURE on_note_modified();

CREATE TABLE note_watchers (
    id serial PRIMARY KEY,
    note_id integer NOT NULL REFERENCES notes(id),
    user_id integer NOT NULL REFERENCES users(id),
    can_edit boolean NOT NULL DEFAULT FALSE,
    since timestamptz NOT NULL DEFAULT NOW(),
    last_edit timestamptz,
    UNIQUE (note_id, user_id)
);

CREATE TABLE note_tags (
    id serial PRIMARY KEY,
    note_id integer NOT NULL REFERENCES notes(id) ON DELETE CASCADE,
    tag_id integer NOT NULL REFERENCES tags(id),
    UNIQUE (note_id, tag_id)
);

-- TODO: There has to be a way to handle insertion/deletion
-- of note_tags with one function.

-- Updates the system state when a note gets new tags in
-- the note_tags table
CREATE FUNCTION on_note_tag_inserted() RETURNS trigger AS $$
BEGIN
    UPDATE notes SET last_modified = NOW() WHERE id = NEW.note_id;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Updates the system state when a note loses tags in
-- the note_tags table
CREATE FUNCTION on_note_tag_deleted() RETURNS trigger AS $$
BEGIN
    UPDATE notes SET last_modified = NOW() WHERE id = OLD.note_id;
    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER note_tag_inserted AFTER INSERT
ON note_tags FOR EACH ROW EXECUTE PROCEDURE on_note_tag_inserted();

CREATE TRIGGER note_tag_deleted AFTER DELETE
ON note_tags FOR EACH ROW EXECUTE PROCEDURE on_note_tag_deleted();

CREATE TABLE notebooks (
    id serial PRIMARY KEY,
    created_at timestamptz NOT NULL DEFAULT NOW(),
    owner_id integer NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name text NOT NULL,
    UNIQUE (owner_id, name)
);

CREATE TABLE notebook_notes (
    id serial PRIMARY KEY,
    notebook_id integer NOT NULL REFERENCES notebooks(id) ON DELETE CASCADE,
    note_id integer NOT NULL REFERENCES notes(id),
    UNIQUE (notebook_id, note_id)
);