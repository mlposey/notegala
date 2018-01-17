'use strict';
process.env.NODE_ENV = 'test';

const chai = require('chai');
const should = chai.should();

const { clearDB } = require('./index');
const { db } = require('../app/data/database');
const Account = require('../app/account/account');
const AccountRepository = require('../app/account/account-repository');
const Note = require('../app/note/note');
const NoteRepository = require('../app/note/repo/note-repository');
const NoteIdSpec = require('../app/note/repo/id-spec');

// Models potential data supplied by a createNote request
const payload = Object.freeze({
    email: 'test@example.com',
    name: 'Trusty Tester',
    input: {
        title: 'Test Title',
        body: 'This is a test.',
        tags: ['test', 'example', 'mock']
    }
});

const accountRepo = new AccountRepository();
const noteRepo = new NoteRepository();

describe('NoteRepository', () => {
    let acct;
    let note;
    describe('#add(note)', () => {
        beforeEach(async () => {
            await clearDB();
            acct = new Account(payload.email, payload.name);
            await accountRepo.add(acct);
            note = new Note(acct.id, payload.input.title, payload.input.body);
        });

        it('should add the note if the note.owner is valid', async () => {
            await noteRepo.add(note);
            const matches = await noteRepo.find(new NoteIdSpec(note.id));
            matches.length.should.eql(1);
        });

        it('should throw exception if note.owner is invalid', async () => {
            note.owner = -1;

            let wasThrown = false;
            try { await noteRepo.add(note); }
            catch (err) { wasThrown = true; }
            
            wasThrown.should.eql(true);
        });
    });

    describe('#remove(note)', () => {
        beforeEach(async () => {
            await clearDB();
            acct = new Account(payload.email, payload.name);
            await accountRepo.add(acct);
            note = new Note(acct.id, payload.input.title, payload.input.body);
            await noteRepo.add(note);            
        });

        it('should remove the note if it is recognized', async () => {
            await noteRepo.remove(note);
            const matches = await noteRepo.find(new NoteIdSpec(note.id));
            matches.length.should.eql(0);
        });

        it('should throw NotFoundError if unrecognized', async () => {
            note.id++;

            let wasThrown = false;
            try { await noteRepo.remove(note); }
            catch (err) { wasThrown = true; }

            wasThrown.should.eql(true);
        });
    });

    describe('#replace(note)', () => {
        beforeEach(async () => {
            await clearDB();
            acct = new Account(payload.email, payload.name);
            await accountRepo.add(acct);
            note = new Note(acct.id, payload.input.title, payload.input.body);
            await noteRepo.add(note);            
        });

        it('should update the note if it exists', async () => {
            const newTitle  = 'a' + note.title;
            note.title = newTitle;
            await noteRepo.replace(note);

            const matches = await noteRepo.find(new NoteIdSpec(note.id));
            matches.length.should.eql(1);
            matches[0].title.should.eql(newTitle);
        });

        it('should throw NotFoundError if missing', async () => {
            note.id++;

            let wasThrown = false;
            try { await noteRepo.replace(note) }
            catch (err) { wasThrown = true; }
            wasThrown.should.eql(true);
        });
    });
});
