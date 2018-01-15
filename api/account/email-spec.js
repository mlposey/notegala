'use strict';
const Specification = require('../specification');
const { db } = require('../service/database');

/** Specifies an Account by its email address */
module.exports = class EmailSpecification extends Specification {
    constructor(email) {
        super();
        this.email = email;
    }

    toQuery() {
        return db('users').select().where({email: this.email});
    }
}