'use strict';
const { GraphQLError } = require('graphql');
const Account = require('../account/account');
const NoteBuilder = require('../note/note-builder');
const NoteRepository = require('../note/repo/note-repository');
const NoteIdSpec = require('../note/repo/id-spec');
const { Notepad } = require('../note/notepad');
const Notebook = require('../notebook/notebook');
const NotebookRepository = require('../notebook/notebook-repository');
const NotebookIdSpec = require('../notebook/id-spec');
const { Query } = require('../query');

// Returned by a resolver if a request is made but it
// lacks required fine-grained permissions
const accessError = new GraphQLError('access denied');

// The repository of all notebooks
const notebookRepo = new NotebookRepository();
// The repository of all notes
const noteRepo = new NoteRepository();

// Root resolver for all GraphQL queries and mutations
module.exports.root = {
    account: (root, {acct}, context) => {
        return acct;
    },
    createNote: (root, {acct}, context) => {
        const builder = new NoteBuilder(acct);
        if (root.input.title) builder.setTitle(root.input.title);
        if (root.input.body)  builder.setBody(root.input.body);
        if (root.input.tags)  builder.addTags(root.input.tags);
        if (root.input.notebook) builder.setNotebook(root.input.notebook);        

        return builder.build().catch(e => new GraphQLError(e.message));
    },
    notes: (root, {acct, first}, context) => {
        return acct.notes(first);
    },
    editNote: async (root, {acct}, context) => {
        const input = root.input;
        const matches = await noteRepo.find(new NoteIdSpec(input.id));
        if (matches.length === 0) return accessError;

        const notepad = new Notepad(matches[0], acct);
        try {
            await notepad.edit(input.title, input.body, input.tags);
            return notepad.note;
        } catch (e) { return new GraphQLError(e.message); }
    },
    removeNote: async (root, {acct}, context) => {
        const matches = await noteRepo.find(new NoteIdSpec(root.id));
        if (matches.length === 0) return accessError;

        await acct.stopWatching(matches[0]);
        return true;
    },
    createNotebook: async (root, {acct}, context) => {
        const notebook = new Notebook(acct.id, root.title);
        await notebookRepo.add(notebook);
        return notebook;
    },
    notebooks: (root, {acct}, context) => {
        return acct.notebooks(root.first);
    },
    notebook: async (root, {acct}, context) => {
        let matches = await notebookRepo.find(new NotebookIdSpec(root.id));
        if (matches.length === 0 || acct.id !== matches[0].owner) {
            return accessError;
        }
        return matches[0];
    },
    removeNotebook: async (root, {acct}, context) => {
        let matches = await notebookRepo.find(new NotebookIdSpec(root.id));
        if (matches.length === 0 || acct.id !== matches[0].owner) {
            return accessError;
        }
        await notebookRepo.remove(matches[0]);
        return true;
    },
    editNotebook: async (root, {acct}, context) => {
        const input = root.input;
        const matches = await notebookRepo.find(new NotebookIdSpec(input.id));
        if (matches.length === 0 || acct.id !== matches[0].owner) {
            return accessError;
        }

        if (input.title) {
            matches[0].title = input.title;
            await notebookRepo.replace(matches[0]);
        }
        return matches[0];
    },
    search: (root, {acct}, context) => {
        return new Query(acct, root.query, root.notebook)
            .submit(root.first);
    }
};