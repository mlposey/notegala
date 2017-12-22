'use strict';

/**
 * Represents the NoteWatcher GraphQL type
 * 
 * This class models the user who watches the note moreso
 * than the database concept of a note watcher. E.g., the
 * id belongs to the user -- not the note_watcher row.
 */
module.exports = class NoteWatcher {
    constructor(uid, displayName, canEdit) {
        this.id = uid;
        this.name = displayName;
        this.canEdit = canEdit;
    }
};