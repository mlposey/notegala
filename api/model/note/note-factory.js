'use strict';
const { db } = require('../../service/database');
const Note = require('./note');

/** Handles creation of Notes and Note accessories */
module.exports = class NoteFactory {
    /**
     * Constructs a new Note in the persistence layer
     * 
     * @param {string} email The email address of the note creator
     * @param {Object} input An object that models the NewNoteInput GraphQL type
     * @throws {Error} If the email does not belong to an account
     * @throws {Error} If the input file is missing a value for
     *                 the body key
     * @returns {Promise.<Note>}
     */
    static async construct(email, input) {
        if (!input.body) throw new Error('missing body field in input note');
        
        const users = await db('users').select('id').where({email: email});
        if (users.length != 1) throw new Error('unrecognized email');
        const uid = users[0].id;

        const noteRows = await db('notes')
            .insert({
                owner_id: uid,
                title: input.title,
                body: input.body
            })
            .returning(['id', 'created_at', 'last_modified', 'is_public',
                       'title', 'body']);

        let row = noteRows[0];
        const note = new Note(row.id, uid, row.created_at, row.last_modified,
            row.is_public, row.title, row.body);

        if (input.tags) {
            for (let tag of input.tags) await note.addTag(tag);
        }
        await note.addWatcher(email, true);
        return note;
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
};