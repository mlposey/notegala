'use strict';
const Specification = require('../../data/specification');
const { db } = require('../../data/database');

/** Specifies Notes by the Notebook they are in */
module.exports = class NotebookSpecification extends Specification {
    constructor(notebookId) {
        super();
        this.notebookId = notebookId;
    }

    /** @returns {Knex.QueryBuilder} */
    toQuery() {
        return db('notebook_notes')
            .join('notes', 'notebook_notes.note_id', 'notes.id')
            .select(['notes.id', 'notes.owner_id', 'notes.created_at',
                     'notes.last_modified', 'notes.is_public', 'notes.title',
                     'notes.body'])
            .where({notebook_id: this.notebookId});
    }
};