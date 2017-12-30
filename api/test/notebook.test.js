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