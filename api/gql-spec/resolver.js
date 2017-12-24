'use strict';
const { GraphQLError } = require('graphql');
const Account = require('../model/account');
const NoteFactory = require('../model/note/note-factory');

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
        try { return await NoteFactory.construct(email, newNoteInput); }
        catch (e) { return new GraphQLError(e.message); }
    },
    myNotes: async (root, {email, first}, context) => {
        try { return await NoteFactory.getOwned(email, first); }
        catch (e) { return new GraphQLError(e.message); }
    }
};