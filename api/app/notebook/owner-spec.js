'use strict';
const Specification = require('../data/specification');
const { db } = require('../data/database');

/** Specifies a Notebook by the id of its owner */
module.exports = class OwnerSpecification extends Specification {
    constructor(ownerId) {
        super();
        this.ownerId = ownerId;
    }

    /** @returns {Knex.QueryBuilder} */
    toQuery() {
        return db('notebooks')
            .select()
            .where({owner_id: this.ownerId});
    }
};