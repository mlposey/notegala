'use strict';
const { GraphQLError } = require('graphql');
const Account = require('../model/account');
const NoteFactory = require('../model/note/note-factory');
const { Notepad } = require('../model/note/notepad');
const Notebook = require('../model/notebook');

// Returned by a resolver if a request is made but it
// lacks required fine-grained permissions
const accessError = new GraphQLError('access denied');

// Root resolver for all GraphQL queries and mutations
module.exports.root = {
    account: (root, {acct}, context) => {
        return acct;
    },
    createNote: (root, {acct}, context) => {
        const newNoteInput = context.variableValues.input;
        return NoteFactory.construct(acct, newNoteInput)
            .then(notepad => notepad.note)
            .catch(e => new GraphQLError(e.message));
    },
    myNotes: (root, {acct, first}, context) => {
        return acct.notes(first);
    },
    editNote: async (root, {acct}, context) => {
        const input = context.variableValues.input;
        try {
            let note = await NoteFactory.fromId(input.id);
            const notepad = new Notepad(note, acct);
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
    moveNote: async (root, {acct}, context) => {
        const input = context.variableValues.input;

        const note = await NoteFactory.fromId(input.id);
        if (!note.isPublic && note.ownerId !== acct.id) throw accessError;

        const dest = await Notebook.fromId(input.dest);
        if (dest.owner !== acct.id) throw accessError;
        
        let source;
        if (input.source) {
            source = await Notebook.fromId(input.source);
            if (source.owner !== acct.id) throw accessError;
        }

        return await Notebook.moveNote(note, source, dest);
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
        if (notebook.owner !== acct.id) throw accessError;
        return notebook;
    },
    removeNotebook: async (root, {acct}, context) => {
        const notebook = await Notebook.fromId(context.variableValues.id);
        if (acct.id !== notebook.owner) return accessError;
        return await notebook.destroy();
    },
    editNotebook: async (root, {acct}, context) => {
        const input = context.variableValues.input;
        const notebook = await Notebook.fromId(input.id)
        if (acct.id !== notebook.owner) return accessError;
        
        if (input.title) await notebook.setTitle(input.title);
        return notebook;
    },
    search: async (root, {acct}, context) => {
        // TODO: Implement search query.
        // This is just a placeholder until the
        // data model is completed.
        if (!root.notebook) {
            const notes = await acct.notes(root.first)
            return notes.map(note => { return {score: 0.0, note: note}; });
        }

        const notes = await Notebook.fromId(root.notebook)
            .then(notebook => notebook.notes())
            .then(notes => notes.slice(0, root.first));
        return notes.map(note => { return {score: 0.0, note: note}; });
    }
};