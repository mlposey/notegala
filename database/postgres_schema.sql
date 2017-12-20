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
    title text NOT NULL DEFAULT '',
    title_tsv tsvector,
    body text NOT NULL,
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

CREATE TABLE note_watchers (
    id serial PRIMARY KEY,
    note_id integer NOT NULL REFERENCES notes(id),
    user_id integer NOT NULL REFERENCES users(id),
    can_edit boolean NOT NULL DEFAULT FALSE,
    last_edit timestamptz
);

CREATE TABLE note_tags (
    id serial PRIMARY KEY,
    note_id integer NOT NULL REFERENCES notes(id),
    tag_id integer NOT NULL REFERENCES tags(id)
);