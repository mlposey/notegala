'use strict';

/** Describes a class that specifies objects of a Repository */
module.exports = class Specification {
    /** @returns {Knex.QueryBuilder} */
    toQuery() {
        throw new Error('#toQuery not implemented');
    }
};