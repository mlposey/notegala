'use strict';
const { db } = require('../service/database');
const Account = require('./account');
const { Repository, NotFoundError } = require('../repository');

module.exports = class AccountRepository extends Repository {
    /**
     * Adds a new account to the repository
     * @param {Account} account 
     * @throws {Error} If the account email is already in use
     */
    async add(account) {
        try {
            let rows = await db('users')
                .insert({email: account.email, name: account.name})
                .returning(['id', 'created_at', 'last_seen', 'email', 'name']);
            let row = rows[0];

            return new Account(row.id, row.created_at, row.last_seen,
                row.email, row.name);
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
        return rows.map(row => new Account(row.id, row.created_at,
            row.last_seen, row.email, row.name))
    }
};