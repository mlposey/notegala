'use strict';

/**
 * Represents the NoteWatcher GraphQL type
 * 
 * This class models the user who watches the note moreso
 * than the database concept of a note watcher. E.g., the
 * id belongs to the user -- not the note_watcher row.
 */
module.exports = class NoteWatcher {
    constructor(uid, noteId, displayName, since, canEdit) {
        this.id = uid;
        // Not displayed in the NoteWatcher type
        this.noteId = noteId;
        this.name = displayName;
        // Not displayed in the NoteWatcher type
        this.since = since;
        this.canEdit = canEdit;
    }
};