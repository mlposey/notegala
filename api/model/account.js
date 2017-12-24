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
     * has a matching email address
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

    /**
     * Constructs a new account in the persistence layer
     * 
     * @param {string} email The email address of the account
     * @param {string} name The display name of the account
     * @throws {Error} An exception is thrown if the email is already in use
     * @returns {Promise.<Account>}
     */
    static async construct(email, name) {
        try {
            let rows = await db('users').insert({
                email: email,
                name: name
            }).returning(['id', 'created_at', 'last_seen', 'email', 'name']);
            let row = rows[0];

            return new Account(row.id, row.created_at, row.last_seen,
                row.email, row.name);
        } catch (err) {
            throw new Error('email address already in use');
        }
    }
};