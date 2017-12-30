'use strict';
const { GraphQLError } = require('graphql');
const Account = require('../model/account');
const NoteFactory = require('../model/note/note-factory');
const { Notepad } = require('../model/note/notepad');
const Notebook = require('../model/notebook');

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
    createNote: (root, {email}, context) => {
        const newNoteInput = context.variableValues.input;
        return NoteFactory.construct(email, newNoteInput)
            .then(notepad => notepad.note)
            .catch(e => new GraphQLError(e.message));
    },
    myNotes: async (root, {email, first}, context) => {
        try { return await NoteFactory.getOwned(email, first); }
        catch (e) { return new GraphQLError(e.message); }
    },
    editNote: async (root, {email}, context) => {
        const input = context.variableValues.input;
        try {
            let note = await NoteFactory.fromId(input.id);
            const notepad = await Notepad.build(note, email);
            await notepad.edit(input.title, input.body, input.tags);
            return note;
        }
        catch (e) { return new GraphQLError(e.message); }
    },
    removeNote: async (root, {email}, context) => {
        const noteId = context.variableValues.id;
        try {
            const note = await NoteFactory.fromId(noteId);
            return await note.remove(email);
        } catch (err) {
            return false;
        }
    },
    createNotebook: async (root, {email}, context) => {
        const title = context.variableValues.title;
        return await Account.fromEmail(email)
            .then(acct => Notebook.build(title, acct))
            .catch(err => new GraphQLError(err.message));
    }
};