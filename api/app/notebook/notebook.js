'use strict';
const { db } = require('../data/database');
const Note = require('../note/note');

/** Represents the Notebook GraphQL type */
module.exports = class Notebook {
    /**
     * Instantiates a new Notebook object
     * 
     * @param {number} owner The id of the notebook owner
     * @param {string} title The notebook name
     * @param {Object} options Optional data that may define:
     *                 id        - The unique id of the notebook
     *                 createdAt - timestamp indicating creation
     */
    constructor(owner, title, options = {}) {
        this.owner = owner;
        this.title = title;
        this.id = options.id;
        this.createdAt = options.createdAt;
    }

    /**
     * Moves a note from one notebook to another
     * 
     * @param {Note} note The note to move
     * @param {Notebook} source Optional source notebook
     * @param {Notebook} dest Destination notebook
     * @returns {Promise.<Notebook>} The new location of the note
     */
    static async moveNote(note, source, dest) {
        if (source) {
            if (source.id === dest.id) return source;
            await source.removeNote(note);
        }
        
        await db.raw(`
            INSERT INTO notebook_notes (notebook_id, note_id)
            VALUES (?, ?) ON CONFLICT DO NOTHING
        `, [dest.id, note.id]);
        return dest;
    }

    /**
     * @returns {Promise.<Array.<Note>>} All notes in the notebook
     */
    async notes() {
        return await db('notebook_notes')
            .join('notes', 'notebook_notes.note_id', 'notes.id')
            .select(['notes.id', 'notes.owner_id', 'notes.created_at',
                     'notes.last_modified', 'notes.is_public', 'notes.title',
                     'notes.body'])
            .where({notebook_id: this.id})
            .map(row => new Note(row.id, row.owner_id, row.created_at,
                                 row.last_modified, row.is_public,
                                 row.title, row.body));
    }

    /**
     * Adds the note to the notebook
     * @param {Note} note 
     */
    async addNote(note) {
        await db('notebook_notes').insert({
            notebook_id: this.id,
            note_id: note.id
        });
    }

    /**
     * Removes the note from the notebook
     * @param {Note} note 
     */
    async removeNote(note) {
        await db('notebook_notes').where({
            notebook_id: this.id,
            note_id: note.id
        }).del();
    }
};