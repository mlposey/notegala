'use strict';
process.env.NODE_ENV = 'test';

const chai = require('chai');
const should = chai.should();

const { db } = require('../service/database');
const { clearDB } = require('./index');
const Account = require('../model/account');
const NoteFactory = require('../model/note/note-factory');
const Notebook = require('../model/notebook');

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

describe('Account', () => {
    describe('#fromEmail(email)', () => {
        beforeEach(async () => await clearDB());
    
        it('should get the account if it exists', async () => {
            await db('users').insert({
                email: claims.email,
                name: claims.name
            });

            let account = await Account.fromEmail(claims.email);
            account.name.should.eql(claims.name);
        });

        it('should throw an Error if the account is missing', async () => {
            let wasThrown = false;
            try {
                await Account.fromEmail(claims.email);
            } catch (err) {
                wasThrown = true;
            }
            wasThrown.should.eql(true);
        });
    });

    describe('#construct(email, name)', () => {
        beforeEach(async () => await clearDB());

        it('should create the account if the email is unique', async () => {
            let account =
                await Account.construct(claims.email, claims.name);

            account.email.should.eql(claims.email);
            account.name.should.eql(claims.name);

            let rows = await db.select().table('users');
            rows.length.should.not.eql(0);
        });

        it('should throw an Error if the email is taken', async () => {
            await Account.construct(claims.email, claims.name);

            let wasThrown = false;
            try {
                await Account.construct(claims.email, claims.name);
            } catch (err) {
                wasThrown = true;
            }
            wasThrown.should.eql(true);
        });
    });

    describe('#notes(limit)', () => {
        let acct;
        beforeEach(async () => {
            await clearDB();
            acct = await Account.construct(claims.email, claims.name);
        });

        it('should return an empty array instead of null', async () => {
            const notes = await acct.notes(null);
            notes.length.should.eql(0);
        });

        it('should return only notes that the user owns', async () => {
            await NoteFactory.construct(acct, newNote);

            const user = await Account.construct('test' + claims.email,
                claims.name);
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
            acct = await Account.construct(claims.email, claims.name);
        });

        it('should return an empty array instead of null', async () => {
            await acct.notebooks(null)
                .then(notebooks => notebooks.length.should.eql(0));
        });

        it('should return only notebooks that the user owns', async () => {
            await Notebook.build("test", acct);

            await Account.construct('a' + claims.email, claims.name)
                .then(a2 => a2.notebooks(null))
                .then(notebooks => notebooks.length.should.eql(0));
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