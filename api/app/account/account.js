'use strict';
const { db } = require('../data/database');
const Note = require('../note/note');
const Notebook = require('../notebook/notebook');

/**
 * Models a user account
 * 
 * This class provides a full implementation of the Account GraphQL type.
 */
module.exports = class Account {
    /**
     * @param {string} email A unique email
     * @param {string} name A display name
     * @param {Object} options Optional data that may define:
     *                 id        - number indicating unique account id
     *                 createdAt - timestamp indicating creation
     *                 lastSeen  - timestamp indicating last login
     */
    constructor(email, name, options = {}) {
        this.email = email;
        this.name = name;
        this.id = options.id;
        this.createdAt = options.createdAt;
        this.lastSeen = options.lastSeen;
    }

    /**
     * Returns the notes owned by the account
     * 
     * @param {number} limit The maximum number of notes to retrieve
     *                       Set equal to null for no limit.
     * @returns {Promise.<Array.<Note>>}
     */
    async notes(limit) {
        return await db('notes')
            .select()
            .where({owner_id: this.id})
            .limit(limit ? limit : Number.MAX_SAFE_INTEGER)
            .map(row => new Note(row.id, row.owner_id, row.created_at,
                                 row.last_modified, row.is_public,
                                 row.title, row.body));
    }

    /**
     * Returns the notebooks owned by the account
     * 
     * @param {number} limit The maximum number of notebooks to retrieve
     *                       Set equal to null for no limit.
     * @returns {Promise.<Array.<Notebook>>}
     */
    async notebooks(limit) {
        return await db('notebooks')
            .select()
            .where({owner_id: this.id})
            .limit(limit ? limit : Number.MAX_SAFE_INTEGER)
            .map(row => new Notebook(row.id, row.created_at,
                                     row.owner_id, row.name));
    }

    /**
     * Removes the account from the note's watchers list
     * 
     * @param {Note} note The note to stop watching
     */
    async stopWatching(note) {
        await db.raw(`
            DELETE FROM notebook_notes
            WHERE note_id = ?
              AND notebook_id in (SELECT id FROM notebooks WHERE owner_id = ?);
        `, [note.id, this.id]);

        await note.removeWatcher(this.id);
    }
};