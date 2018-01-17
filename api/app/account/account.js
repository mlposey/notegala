'use strict';
const { db } = require('../data/database');
const Note = require('../note/note');
const NoteRepository = require('../note/repo/note-repository');
const NoteOwnerSpec = require('../note/repo/owner-spec');
const Notebook = require('../notebook/notebook');
const NotebookRepository = require('../notebook/notebook-repository');
const NotebookOwnerSpec = require('../notebook/owner-spec');

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
        const repo = new NoteRepository();
        return await repo
            .find(new NoteOwnerSpec(this.id), limit);
    }

    /**
     * Returns the notebooks owned by the account
     * 
     * @param {number} limit The maximum number of notebooks to retrieve
     *                       Set equal to null for no limit.
     * @returns {Promise.<Array.<Notebook>>}
     */
    async notebooks(limit) {
        const repo = new NotebookRepository();
        return await repo
            .find(new NotebookOwnerSpec(this.id), limit);
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