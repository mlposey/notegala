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
     * @param {string} name The name of the notebook
     */
    constructor(id, createdAt, owner, name) {
        this.id = id;
        this.createdAt = createdAt;
        this.owner = owner;
        this.name = name;
    }

    /**
     * Builds a new Notebook in the persistence layer
     * 
     * @param {string} name The name of the notebook. A user cannot
     *                      define multiple notebooks with the same
     *                      name.
     * @param {Account} author The creator of the notebook
     * @throws {Error} If the notebook could not be created
     * @returns {Promise.<Notebook>}
     */
    static async build(name, author) {
        const rows = await db('notebooks')
            .insert({owner_id: author.id, name: name})
            .returning(['id', 'created_at']);
        
        if (rows.length != 1) throw new Error('could not create notebook');
        return new Notebook(rows[0].id, rows[0].created_at, author.id, name);
    }
};