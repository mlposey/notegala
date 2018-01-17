'use strict';
const Specification = require('../data/specification');
const { db } = require('../data/database');

/** Specifies a Note by its unique id */
module.exports = class IdSpecification extends Specification {
    constructor(id) {
        super();
        this.id = id;
    }

    /** @returns {Knex.QueryBuilder} */
    toQuery() {
        return db('notes')
            .select()
            .where({id: this.id});
    }
};