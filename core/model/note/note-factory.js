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
        
        let rows = await db('users').select('id').where({email: email});
        if (rows.length != 1) throw new Error('unrecognized email');

        const uid = rows[0].id;
        rows = await db('notes')
            .insert({title: input.title, body: input.body})
            .returning(['id', 'created_at', 'last_modified', 'is_public',
                       'title', 'body']);

        let row = rows[0];
        const note = new Note(row.id, row.created_at, row.last_modified,
            row.is_public, row.title, row.body);

        if (input.tags) await this.linkTags(note.id, input.tags);
        return note;
    }

    /**
     * Links tags to a note identified by an id
     * 
     * @param {number} noteId 
     * @param {Array.<string>} tags 
     * @throws {Error} If noteId does not belong to any note
     */
    static async linkTags(noteId, tags) {
        for (let tag of tags) {
            await db.raw(`
                WITH tag AS (
                    INSERT INTO tags (label) VALUES (?)
                    ON CONFLICT(label) DO UPDATE
                    SET label=EXCLUDED.label
                    RETURNING id
                )
                INSERT INTO note_tags (note_id, tag_id)
                VALUES (?, (SELECT id FROM tag));
            `, [tag, noteId]);
        }
    }
};