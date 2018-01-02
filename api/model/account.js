'use strict';
const { db } = require('../service/database');
const Note = require('./note/note');
const Notebook = require('./notebook');

/**
 * Models a user account
 * 
 * This class provides a full implementation of the Account GraphQL type.
 */
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

    /**
     * Returns the notes owned by the account
     * 
     * @param {number} limit The maximum number of notes to retrieve
     *                       Set equal to null for no limit.
     * @returns {Promise.<Array.<Note>>}
     */
    async notes(limit) {
        return await db('notes')
            .select()
            .where({owner_id: this.id})
            .limit(limit ? limit : Number.MAX_SAFE_INTEGER)
            .map(row => new Note(row.id, row.owner_id, row.created_at,
                                 row.last_modified, row.is_public,
                                 row.title, row.body));
    }

    /**
     * Returns the notebooks owned by the account
     * 
     * @param {number} limit The maximum number of notebooks to retrieve
     *                       Set equal to null for no limit.
     * @returns {Promise.<Array.<Notebook>>}
     */
    async notebooks(limit) {
        return await db('notebooks')
            .select()
            .where({owner_id: this.id})
            .limit(limit ? limit : Number.MAX_SAFE_INTEGER)
            .map(row => new Notebook(row.id, row.created_at,
                                     row.owner_id, row.name));
    }
};