'use strict';
process.env.NODE_ENV = 'test';

const chai = require('chai');
const should = chai.should();

const { db } = require('../app/data/database');
const { clearDB } = require('./index');
const Account = require('../app/account/account');
const AccountRepository = require('../app/account/account-repository');
const Notebook = require('../app/notebook/notebook');
const NotebookRepository = require('../app/notebook/notebook-repository');
const NoteBuilder = require('../app/note/note-builder');

// Sample data for notebook creation
const payload = Object.freeze({
    email: 'test@example.com',
    userName: 'Testy Tester',
    nbName: 'Test Notebook'
});

// The repository for all accounts
const accountRepo = new AccountRepository();
// The repository for all notebooks
const notebookRepo = new NotebookRepository();

describe('Notebook', () => {
    describe('#notes()', () => {
        beforeEach(async () => await clearDB());

        it('should return an empty array instead of null', async () => {
            const acct = new Account(payload.email, payload.userName);
            await accountRepo.add(acct);

            const notebook = new Notebook(acct.id, payload.nbName);
            await notebookRepo.add(notebook);
            const notes = await notebook.notes();

            notes.length.should.eql(0);
        });
    });

    describe('#addNote(note)', () => {
        beforeEach(async () => await clearDB());

        it('should attach the note to the notebook', async () => {
            const acct = new Account(payload.email, payload.userName);
            await accountRepo.add(acct);

            const notebook = new Notebook(acct.id, payload.nbName);
            await notebookRepo.add(notebook);
            let notes = await notebook.notes();
            notes.length.should.eql(0);

            const note = await new NoteBuilder(acct).setTitle('Test').build();
            await notebook.addNote(note);

            notes = await notebook.notes();
            notes.length.should.eql(1);
        });
    });

    describe('#removeNote(note)', () => {
        beforeEach(async () => await clearDB());

        it('should remove the note from the notebook', async () => {
            const acct = new Account(payload.email, payload.userName);
            await accountRepo.add(acct);
            const notebook = new Notebook(acct.id, payload.nbName);
            await notebookRepo.add(notebook);

            const note = await new NoteBuilder(acct).setTitle('Test').build();
            await notebook.addNote(note);

            let notes = await notebook.notes();
            notes.length.should.eql(1);

            await notebook.removeNote(note);

            notes = await notebook.notes();
            notes.length.should.eql(0);
        });
    });
});