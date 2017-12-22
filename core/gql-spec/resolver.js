'use strict';
const { GraphQLError } = require('graphql');
const Account = require('../model/account');
const Note = require('../model/note/note');

// Root resolver for all GraphQL queries and mutations
module.exports.root = {
    account: (root, {email, name}, context) => {
        return Account.fromEmail(email)
            .then(acct => {
                return acct;
            })
            .catch(e => {
                return Account.construct(email, name);
            })
            .catch(e => {
                return new GraphQLError(e.message);
            });
    },
    createNote: async (root, {email}, context) => {
        const newNoteInput = context.variableValues.input;
        try { return await Note.construct(email, newNoteInput); }
        catch (e) { return new GraphQLError(e.message); }
    }
};