'use strict';
const { db } = require('../service/database');
const Note = require('./note/note');

/** Represents the Notebook GraphQL type */
module.exports = class Notebook {
    /**
     * Instantiates a new Notebook object
     * 
     * @param {number} id The unique id of the Notebook
     * @param {string} createdAt The timestamp with timezone when the
     *                           notebook was created
     * @param {number} owner The user id of the owner
     * @param {string} title The name of the notebook
     */
    constructor(id, createdAt, owner, title) {
        this.id = id;
        this.createdAt = createdAt;
        this.owner = owner;
        this.title = title;
    }

    /**
     * Builds a new Notebook in the persistence layer
     * 
     * @param {string} title The name of the notebook. A user cannot
     *                      define multiple notebooks with the same
     *                      name.
     * @param {Account} author The creator of the notebook
     * @throws {Error} If the notebook could not be created
     * @returns {Promise.<Notebook>}
     */
    static async build(title, author) {
        let rows = await db('notebooks')
            .insert({owner_id: author.id, name: title})
            .returning(['id', 'created_at'])
            .catch(err => { throw new Error('could not create notebook'); });

        return new Notebook(rows[0].id, rows[0].created_at, author.id, title);
    }

    /**
     * Builds a local copy of a notebook from the database
     * 
     * @param {number} id The unique id of the notebook
     * @throws {Error} If the id is unrecognized
     * @returns {Promise.<Notebook>}
     */
    static async fromId(id) {
        const rows = await db('notebooks')
            .select(['created_at', 'owner_id', 'name'])
            .where({id: id});

        if (rows.length != 1) throw new Error('unrecognized id');
        const row = rows[0];

        return new Notebook(id, row.created_at, row.owner_id, row.name);
    }

    /**
     * Deletes the database representation of the notebook
     * 
     * Any notes it contained will be detached but not deleted.
     * @returns {Promise.<boolean>} True if success; false otherwise
     */
    async destroy() {
        let rows = await db('notebooks')
            .where({id: this.id})
            .del()
            .returning('*');
        return rows.length === 1;
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
};