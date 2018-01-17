'use strict';
process.env.NODE_ENV = 'test';

const chai = require('chai');
const should = chai.should();

const { db } = require('../app/data/database');
const { clearDB } = require('./index');
const Account = require('../app/account/account');
const AccountRepository = require('../app/account/account-repository');
const NoteBuilder = require('../app/note/note-builder');
const Notebook = require('../app/notebook/notebook');
const NotebookRepository = require('../app/notebook/notebook-repository');

// Models a bearer token that would be supplied to the auth layer
const claims = Object.freeze({
    email: 'test@example.com',
    name: 'Trusty Tester'
});

// Models the data for a NewNoteInput type
const newNote = Object.freeze({
    title: 'Test Title',
    body: 'This is a test.',
    tags: ['test', 'example', 'mock']
});

// The repository for all accounts
const accountRepo = new AccountRepository();
// The repository for all notebooks
const notebookRepo = new NotebookRepository();

describe('Account', () => {
    describe('#notes(limit)', () => {
        let acct;
        let noteBuilder;
        beforeEach(async () => {
            await clearDB();
            acct = new Account(claims.email, claims.name);
            await accountRepo.add(acct);
            noteBuilder = new NoteBuilder(acct)
                .setTitle(newNote.title)
                .setBody(newNote.body)
                .addTags(newNote.tags);
        });

        it('should return an empty array instead of null', async () => {
            const notes = await acct.notes(null);
            notes.length.should.eql(0);
        });

        it('should return only notes that the user owns', async () => {
            await noteBuilder.build();

            const user = new Account('test' + claims.email, claims.name);
            await accountRepo.add(user);
            const body = user.email + user.name;
            await new NoteBuilder(user).setBody(body).build();

            const notes = await user.notes(null);
            notes.length.should.eql(1);
            notes[0].body.should.eql(body);
        });

        it('should respect the specified limit', async () => {
            await noteBuilder.build();
            await noteBuilder.build();
            
            const max = 1;
            const notes = await acct.notes(max);
            
            notes.length.should.eql(max);
        });
    });

    describe('#notebooks(limit)', () => {
        let acct;
        beforeEach(async () => {
            await clearDB();
            acct = new Account(claims.email, claims.name);
            await accountRepo.add(acct);
        });

        it('should return an empty array instead of null', async () => {
            await acct.notebooks()
                .then(notebooks => notebooks.length.should.eql(0));
        });

        it('should return only notebooks that the user owns', async () => {
            await notebookRepo.add(new Notebook(acct.id, 'test'));

            const user = new Account('a' + claims.email, claims.name);
            await accountRepo.add(user)
            
            const notebooks = await user.notebooks();
            notebooks.length.should.eql(0);
        });

        it('should respect the specified limit', async () => {
            await notebookRepo.add(new Notebook(acct.id, 'test1'));
            await notebookRepo.add(new Notebook(acct.id, 'test2'));
            
            const limit = 1;
            const notebooks = await acct.notebooks(limit);
            notebooks.length.should.eql(limit);
        });
    });
});