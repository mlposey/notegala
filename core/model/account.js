'use strict';
const { db } = require('../service/database');

/** Represents the Account GraphQL type */
module.exports = class Account {
    constructor(id, createdAt, lastSeen, email, name) {
        this.id = id;
        this.createdAt = createdAt;
        this.lastSeen = lastSeen;
        this.email = email;
        this.name = name;
    }

    /**
     * Retrieves an account from the persistence layer that
     * has a matching email address.
     * 
     * @param {string} email The email address of the account
     * @throws {Error} An exception is thrown if no such account exists
     * @returns {Promise.<Account>}
     */
    static async fromEmail(email) {
        let rows = await db.select().table('users').where({email: email});

        if (rows.length === 0) throw new Error('unrecognized email');
        let row = rows[0];
        return new Account(row.id, row.created_at, row.last_seen, row.email,
            row.name);
    }
};