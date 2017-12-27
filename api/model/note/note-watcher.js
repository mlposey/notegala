'use strict';
const { db } = require('../../service/database');

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

    /** Changes the watcher's edit permissions */
    async changeEditPerm(canEdit) {
        await db('note_watchers')
            .where({
                note_id: this.noteId,
                user_id: this.id
            })
            .update({can_edit: canEdit});
        this.canEdit = true;
    }

    /**
     * Returns the watcher that has the earliest since timestamp
     * @param {Array.<NoteWatcher>} watchers 
     * @return The earliest NoteWatcher or null if watchers is empty or falsy
     */
    static earliest(watchers) {
        if (!watchers || watchers.length == 0) return null;

        let earliest = 0;
        let earliestTz = Date.parse(watchers[0].since);

        for (let i = 1; i < watchers.length; i++) {
            let tz = Date.parse(watchers[i].since);
            if (tz < earliestTz) {
                earliest = i;
                earliestTz = tz;
            }
        }
        return watchers[earliest];
    }
};