'use strict';
const { db } = require('../data/database');
const { Repository, NotFoundError } = require('../data/repository');
const Notebook = require('./notebook');

/** Manages the global collection of Notebooks */
module.exports = class NotebookRepository extends Repository {
    /**
     * Adds a new notebook to the repository
     * @param {Notebook} notebook Will be updated to match the version as it
     *                            exists in the repository
     * @throws {Error} If there is an existing notebook with the same
     *                 owner and name
     */
    async add(notebook) {
        let rows = await db('notebooks')
            .insert({owner_id: notebook.owner, name: notebook.title})
            .returning(['id', 'created_at'])
            .catch(err => { throw new Error('could not create notebook'); });

        notebook.id = rows[0].id;
        notebook.createdAt = rows[0].created_at;
    }

    /**
     * Removes a notebook from the repository
     * @param {Notebook} notebook 
     * @throws {NoteFoundError} If the notebook is not in the repository
     */
    async remove(notebook) {
        let rows = await db('notebooks')
            .where({id: notebook.id})
            .del()
            .returning('*');
        if (rows.length === 0) throw new NotFoundError();
    }

    /**
     * Replaces the existing notebook in the repository
     * @param {Notebook} notebook An updated notebook
     *                            Note: The id must remain the same
     * @throws {NoteFoundError} If the account is not in the repository
     */
    async replace(notebook) {
        let rows = await db('notebooks')
            .update({
                name: notebook.title,
                owner_id: notebook.owner
            })
            .where({id: notebook.id})
            .returning('id');
        if (rows.length === 0) throw new NotFoundError();
    }

    /**
     * Finds all notebooks in the repository that match the specification
     * @param {Specification} spec
     * @param {number} limit The maximum number of notebooks to retrieve
     * @return {Promise.<Array.<Notebook>>}
     */
    async find(spec, limit = this.DEFAULT_LIMIT) {
        let rows = await spec.toQuery().limit(limit);
        return rows.map(row => new Notebook(row.owner_id, row.name, {
            id: row.id,
            createdAt: row.created_at
        }));
    }
};