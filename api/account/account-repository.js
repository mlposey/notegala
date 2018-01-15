'use strict';
const { db } = require('../service/database');
const Account = require('./account');
const { Repository, NotFoundError } = require('../repository');

module.exports = class AccountRepository extends Repository {
    /**
     * Adds a new account to the repository
     * @param {Account} account Will be updated to match the version as it
     *                          exists in the repository
     * @throws {Error} If the account email is already in use
     */
    async add(account) {
        try {
            let rows = await db('users')
                .insert({email: account.email, name: account.name})
                .returning(['id', 'created_at', 'last_seen', 'email', 'name']);

            account.id = rows[0].id;
            account.createdAt = rows[0].created_at;
            account.lastSeen = rows[0].last_seen;
        } catch (err) {
            throw new Error('email address already in use');
        }
    }

    /**
     * Removes an account from the repository
     * @param {Account} account 
     * @throws {NotFoundError} If the account was not in the repository
     */
    async remove(account) {
        const notes = await account.notes();
        for (let note of notes) await account.stopWatching(note);

        const rows = await db('users')
            .where({id: account.id})
            .del()
            .returning('id');

        if (rows.length === 0) throw new NotFoundError();
    }

    /**
     * Replaces the existing account in the repository
     * @param {Account} account An updated account
     *                          Note: The id must remain the same.
     * @throws {NotFoundError} If the account was not in the repository
     */
    async replace(account) {
        const rows = await db('users')
            .update({
                email: account.email,
                name: account.name
            })
            .where({id: account.id})
            .returning('id');
        if (rows.length === 0) throw new NotFoundError();
    }

    /**
     * Finds all accounts in the repository that match the specification
     * @param {Specification} spec
     * @returns {Promise.<Array.<Account>>}
     */
    async find(spec) {
        let rows = await spec.toQuery();
        return rows.map(row => new Account(row.email, row.name, {
            id: row.id,
            createdAt: row.created_at,
            lastSeen: row.last_seen
        }));
    }
};