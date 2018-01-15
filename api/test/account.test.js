'use strict';
process.env.NODE_ENV = 'test';

const chai = require('chai');
const should = chai.should();

const { db } = require('../service/database');
const { clearDB } = require('./index');
const Account = require('../account/account');
const AccountRepository = require('../account/account-repository');
const NoteFactory = require('../note/note-factory');
const Notebook = require('../note/notebook');

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
const repo = new AccountRepository();

describe('Account', () => {
    describe('#notes(limit)', () => {
        let acct;
        beforeEach(async () => {
            await clearDB();
            acct = new Account(claims.email, claims.name);
            await repo.add(acct);
        });

        it('should return an empty array instead of null', async () => {
            const notes = await acct.notes(null);
            notes.length.should.eql(0);
        });

        it('should return only notes that the user owns', async () => {
            await NoteFactory.construct(acct, newNote);

            const user = new Account('test' + claims.email, claims.name);
            await repo.add(user);
            const body = user.email + user.name;
            await NoteFactory.construct(user, {body: body});

            const notes = await user.notes(null);
            notes.length.should.eql(1);
            notes[0].body.should.eql(body);
        });

        it('should respect the specified limit', async () => {
            await NoteFactory.construct(acct, newNote);
            await NoteFactory.construct(acct, newNote);
            
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
            await repo.add(acct);
        });

        it('should return an empty array instead of null', async () => {
            await acct.notebooks()
                .then(notebooks => notebooks.length.should.eql(0));
        });

        it('should return only notebooks that the user owns', async () => {
            await Notebook.build("test", acct);

            const user = new Account('a' + claims.email, claims.name);
            await repo.add(user)
            
            const notebooks = await user.notebooks();
            notebooks.length.should.eql(0);
        });

        it('should respect the specified limit', async () => {
            await Notebook.build("test", acct);
            await Notebook.build("test2", acct);
            
            const limit = 1;
            const notebooks = await acct.notebooks(limit);
            notebooks.length.should.eql(limit);
        });
    });
});