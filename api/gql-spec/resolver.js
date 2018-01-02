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
    myNotes: (root, {acct, first}, context) => {
        return acct.notes(first);
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
    removeNote: async (root, {acct}, context) => {
        const note = await NoteFactory.fromId(context.variableValues.id);
        await acct.stopWatching(note);
        return true;
    },
    createNotebook: (root, {acct}, context) => {
        return Notebook.build(context.variableValues.title, acct)
                .catch(err => new GraphQLError(err.message));
    },
    myNotebooks: (root, {acct, first}, context) => {
        return acct.notebooks(first);
    },
    notebook: async (root, {acct}, context) => {
        const notebook = await Notebook.fromId(context.variableValues.id);
        if (notebook.owner != acct.id) throw new GraphQLError('access denied');
        return notebook;
    },
    removeNotebook: async (root, {acct}, context) => {
        const notebook = await Notebook.fromId(context.variableValues.id)
        if (acct.id === notebook.owner) return await notebook.destroy();
        else return new GraphQLError('permission error');
    }
};