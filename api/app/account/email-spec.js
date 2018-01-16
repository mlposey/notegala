'use strict';
const Specification = require('../data/specification');
const { db } = require('../data/database');

/** Specifies an Account by its email address */
module.exports = class EmailSpecification extends Specification {
    constructor(email) {
        super();
        this.email = email;
    }

    /** @returns {Knex.QueryBuilder} */
    toQuery() {
        return db('users').select().where({email: this.email});
    }
}