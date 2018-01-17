'use strict';
const Specification = require('../../data/specification');
const { db } = require('../../data/database');

/** Specifies Notes by their owner */
module.exports = class OwnerSpecification extends Specification {
    constructor(ownerId) {
        super();
        this.ownerId = ownerId;
    }

    /** @returns {Knex.QueryBuilder} */
    toQuery() {
        return db('notes')
            .select()
            .where({owner_id: this.ownerId});
    }
};