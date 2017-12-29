'use strict';
const { db } = require('../../service/database');
const Note = require('./note');
const { Notepad } = require('./notepad');
const Account = require('../account');

/** Handles creation of Notes and Note accessories */
module.exports = class NoteFactory {
    /**
     * Constructs a new Note in the persistence layer
     * 
     * @param {string} email The email address of the note creator
     * @param {Object} input An object that models the NewNoteInput GraphQL type
     * @throws {Error} If the email does not belong to an account
     * @throws {Error} If the input has falsy values for body and title
     * @returns {Promise.<Notepad>}
     */
    static async construct(email, input) {
        if (!input.body && !input.title) {
            throw new Error('missing input note content');
        }

        let acct = await Account.fromEmail(email);

        const noteRows = await db('notes')
            .insert({
                owner_id: acct.id,
                title: input.title,
                body: input.body
            })
            .returning(['id', 'created_at', 'last_modified', 'is_public',
                       'title', 'body']);

        let row = noteRows[0];
        let note = new Note(row.id, acct.id, row.created_at,
            row.last_modified, row.is_public, row.title, row.body);

        let notepad = new Notepad(note, acct);

        await note.addWatcher(email, true);        
        if (input.tags) {
            for (let tag of input.tags) await notepad.addTag(tag);
        }
        return notepad;
    }

    /**
     * Gets all notes owned by the user
     * 
     * @param {string} email The email address of the user
     * @param {number} limit The maximum number of notes to retrieve
     *                       Set equal to null for no limit.
     * @returns {Promise.<Array.<Note>>}
     */
    static async getOwned(email, limit) {
        return await db('notes')
            .select(['id', 'owner_id', 'created_at', 'last_modified',
                'is_public', 'title', 'body'])
            .where({owner_id: db('users').select('id').where({email: email})})
            .limit(limit ? limit : Number.MAX_SAFE_INTEGER)
            .map(row => {
                return new Note(row.id, row.owner_id, row.created_at,
                    row.last_modified, row.is_public, row.title, row.body);
            });
    }

    /**
     * Gets a Note from the persistence layer
     * @param {number} id The table id of the note
     * @throws {Error} If the id is not recognized
     */
    static async fromId(id) {
        const rows = await db('notes')
            .select(['owner_id', 'created_at', 'last_modified', 'is_public',
                     'title', 'body'])
            .where({id: id});

        if (rows.length == 0) throw new Error('note not found');
        const row = rows[0];

        return new Note(id, row.owner_id, row.created_at, row.last_modified,
            row.is_public, row.title, row.body);
    }
};