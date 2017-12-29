'use strict';
const { db } = require('../service/database');

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
};