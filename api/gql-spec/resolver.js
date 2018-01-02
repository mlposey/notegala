'use strict';
const { GraphQLError } = require('graphql');
const Account = require('../model/account');
const NoteFactory = require('../model/note/note-factory');
const { Notepad } = require('../model/note/notepad');
const Notebook = require('../model/notebook');

// Root resolver for all GraphQL queries and mutations
module.exports.root = {
    account: (root, {acct}, context) => {
        return acct;
    },
    createNote: (root, {email}, context) => {
        const newNoteInput = context.variableValues.input;
        return NoteFactory.construct(email, newNoteInput)
            .then(notepad => notepad.note)
            .catch(e => new GraphQLError(e.message));
    },
    myNotes: (root, {email, first}, context) => {
        return NoteFactory.getOwned(email, first)
            .catch(err => new GraphQLError(e.message));
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
    removeNote: (root, {email}, context) => {
        const noteId = context.variableValues.id;
        return NoteFactory.fromId(noteId)
            .then(note => note.remove(email))
            .catch(err => false);
    },
    createNotebook: (root, {acct}, context) => {
        return Notebook.build(context.variableValues.title, acct)
                .catch(err => new GraphQLError(err.message));
    },
    myNotebooks: (root, {email, first}, context) => {
        return Notebook.getOwned(email, first)
            .catch(err => new GraphQLError(err.message));
    },
    notebook: async (root, {acct}, context) => {
        const notebook = await Notebook.fromId(context.variableValues.id);
        if (notebook.owner != acct.id) throw new GraphQLError('access denied');
        return notebook;
    },
    removeNotebook: (root, {email}, context) => {
        return Notebook.fromId(context.variableValues.id)
            .then(notebook => notebook.remove(email))
            .catch(err => new GraphQLError(err.message));
    }
};