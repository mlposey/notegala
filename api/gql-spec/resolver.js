'use strict';
const { GraphQLError } = require('graphql');
const Account = require('../account/account');
const NoteFactory = require('../note/note-factory');
const { Notepad } = require('../note/notepad');
const Notebook = require('../note/notebook');
const { Query } = require('../query');

// Returned by a resolver if a request is made but it
// lacks required fine-grained permissions
const accessError = new GraphQLError('access denied');

// Root resolver for all GraphQL queries and mutations
module.exports.root = {
    account: (root, {acct}, context) => {
        return acct;
    },
    createNote: (root, {acct}, context) => {
        return NoteFactory.construct(acct, root.input)
            .then(notepad => notepad.note)
            .catch(e => new GraphQLError(e.message));
    },
    notes: (root, {acct, first}, context) => {
        return acct.notes(first);
    },
    editNote: async (root, {acct}, context) => {
        const input = root.input;
        try {
            let note = await NoteFactory.fromId(input.id);
            const notepad = new Notepad(note, acct);
            await notepad.edit(input.title, input.body, input.tags);
            return note;
        }
        catch (e) { return new GraphQLError(e.message); }
    },
    removeNote: async (root, {acct}, context) => {
        const note = await NoteFactory.fromId(root.id);
        await acct.stopWatching(note);
        return true;
    },
    moveNote: async (root, {acct}, context) => {
        const input = root.input;

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
        return Notebook.build(root.title, acct)
                .catch(err => new GraphQLError(err.message));
    },
    notebooks: (root, {acct}, context) => {
        return acct.notebooks(root.first);
    },
    notebook: async (root, {acct}, context) => {
        const notebook = await Notebook.fromId(root.id);
        if (notebook.owner !== acct.id) throw accessError;
        return notebook;
    },
    removeNotebook: async (root, {acct}, context) => {
        const notebook = await Notebook.fromId(root.id);
        if (acct.id !== notebook.owner) return accessError;
        return await notebook.destroy();
    },
    editNotebook: async (root, {acct}, context) => {
        const input = root.input;
        const notebook = await Notebook.fromId(input.id)
        if (acct.id !== notebook.owner) return accessError;
        
        if (input.title) await notebook.setTitle(input.title);
        return notebook;
    },
    search: (root, {acct}, context) => {
        return new Query(acct, root.query, root.notebook)
            .submit(root.first);
    }
};