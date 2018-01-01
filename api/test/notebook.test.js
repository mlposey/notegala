'use strict';
process.env.NODE_ENV = 'test';

const chai = require('chai');
const should = chai.should();

const { db } = require('../service/database');
const { clearDB } = require('./index');
const Account = require('../model/account');
const Notebook = require('../model/notebook');
const NoteFactory = require('../model/note/note-factory');

// Sample data for notebook creation
const payload = Object.freeze({
    email: 'test@example.com',
    userName: 'Testy Tester',
    nbName: 'Test Notebook'
});

describe('Notebook', () => {
    describe('#build(title, author)', () => {
        beforeEach(async () => await clearDB());

        it('should throw an exception if the author is unrecognized', async () => {
            let fakeAcct = new Account(2, '', '', 'blah@blah.com', 'blah');

            let wasThrown = false;            
            try { await Notebook.build(payload.nbName, fakeAcct); }
            catch (e) { wasThrown = true; }
            wasThrown.should.eql(true);
        });

        it('should create the notebook if (name, author) is unique', async () => {
            let acct = await Account.construct(payload.email, payload.userName);
            await Notebook.build(payload.nbName, acct);

            let rows = await db('notebooks').select();
            rows.length.should.eql(1);
        });

        it('should throw an exception if (name, author) is duplicate', async () => {
            let acct = await Account.construct(payload.email, payload.userName);
            await Notebook.build(payload.nbName, acct);
            
            let wasThrown = false;
            try { await Notebook.build(payload.nbName, acct); }
            catch (e) { wasThrown = true; }
            wasThrown.should.eql(true);
        });
    });

    describe('#fromId(id)', () => {
        beforeEach(async () => await clearDB());

        it('should get the notebook if it exists', async () => {
            let acct = await Account.construct(payload.email, payload.userName);
            let expected = await Notebook.build(payload.nbName, acct);

            let actual = await Notebook.fromId(expected.id);
            actual.id.should.eql(expected.id);
        });

        it('should throw an exception if id is unrecognized', async () => {
            let wasThrown = false;
            try { await Notebook.fromId(3); }
            catch (e) { wasThrown = true; }
            wasThrown.should.eql(true);
        });
    });

    describe('#getOwned(email, limit)', () => {
        beforeEach(async () => await clearDB());

        it('should return an empty array instead of null', async () => {
            await Account.construct(payload.email, payload.userName)
                .then(acct => Notebook.getOwned(payload.email, null))
                .then(notebooks => notebooks.length.should.eql(0));
        });

        it('should return only notebooks that the user owns', async () => {
            const a1 = await Account.construct(payload.email, payload.userName)
            await Notebook.build("test", a1);

            await Account.construct('a' + payload.email, payload.userName)
                .then(acct => Notebook.getOwned('a' + payload.email, null))
                .then(notebooks => notebooks.length.should.eql(0));
        });

        it('should respect the specified limit', async () => {
            const act = await Account.construct(payload.email, payload.userName)
            await Notebook.build("test", act);
            await Notebook.build("test2", act);
            
            const limit = 1;
            const notebooks = await Notebook.getOwned(payload.email, limit);
            notebooks.length.should.eql(limit);
        });
    });

    describe('#remove(email)', () => {
        beforeEach(async () => await clearDB());

        it('should remove the note if it is owned by the user', async () => {
            const act = await Account.construct(payload.email, payload.userName)
            await Notebook.build(payload.nbName, act);

            let notebooks = await Notebook.getOwned(payload.email);
            notebooks.length.should.eql(1);

            const res = await notebooks[0].remove(payload.email);
            res.should.eql(true);
            notebooks = await Notebook.getOwned(payload.email);
            
            notebooks.length.should.eql(0);
        });

        it('should return false if unrecognized or not owned', async () => {
            const act = await Account.construct(payload.email, payload.userName)
            await Notebook.build(payload.nbName, act);

            let notebooks = await Notebook.getOwned(payload.email);
            notebooks.length.should.eql(1);

            const acct =
                await Account.construct('a' + payload.email, payload.userName);         
            const res = await notebooks[0].remove('a' + payload.email);
            res.should.eql(false);
        });
    });

    describe('#notes()', () => {
        beforeEach(async () => await clearDB());

        it('should return an empty array instead of null', async () => {
            await Account.construct(payload.email, payload.userName)
                .then(acct => Notebook.build(payload.nbName, acct))
                .then(notebook => notebook.notes())
                .then(notes => notes.length.should.eql(0));
        });
    });

    describe('#addNote(note)', () => {
        beforeEach(async () => await clearDB());

        it('should attach the note to the notebook', async () => {
            const acct =
                await Account.construct(payload.email, payload.userName)

            const notebook = await Notebook.build(payload.nbName, acct);
            let notes = await notebook.notes();
            notes.length.should.eql(0);

            const notepad = await NoteFactory.construct(payload.email, {
                title: 'Test'
            });
            await notebook.addNote(notepad.note);

            notes = await notebook.notes();
            notes.length.should.eql(1);
        });
    });
});