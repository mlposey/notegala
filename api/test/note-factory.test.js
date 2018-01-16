'use strict';
process.env.NODE_ENV = 'test';

const chai = require('chai');
const should = chai.should();

const { db } = require('../app/data/database');
const { clearDB } = require('./index');
const NoteFactory = require('../app/note/note-factory');
const Account = require('../app/account/account');
const AccountRepository = require('../app/account/account-repository');

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

describe('NoteFactory', () => {
    describe('#construct(email, input)', () => {
        let acct;
        beforeEach(async () => {
            await clearDB()
            acct = new Account(payload.email, payload.name);
            await accountRepo.add(acct);
        });

        it('should throw an exception if input lacks title and body values', async () => {
            let wasThrown = false;
            try {
                await NoteFactory.construct(acct, {tags: ['test']});
            } catch (err) {
                wasThrown = true;
            }
            wasThrown.should.eql(true);
        });

        it('should return a valid note if no exception is thrown', async () => {
            let np = await NoteFactory.construct(acct, payload.input);

            np.note.body.should.eql(payload.input.body);
            np.note.title.should.eql(payload.input.title);
        });

        it('should put created notes in the persistence layer', async () => {
            const notepad =
                await NoteFactory.construct(acct, payload.input);

            let rows = await db.select().table('notes');
            rows.length.should.not.be.eql(0);

            rows = await db.select().table('note_tags');
            rows.length.should.eql(payload.input.tags.length);
            const extras = rows
                .map(row => row.note_id)
                .filter(noteId => {return noteId != notepad.note.id;});
            extras.length.should.be.eql(0);
        });
    });

    describe('#fromId(id)', () => {
        let acct;
        beforeEach(async () => {
            await clearDB()
            acct = new Account(payload.email, payload.name);
            await accountRepo.add(acct);
        });

        it('should retrieve the note if it exists', async () => {
            const notepad = 
                await NoteFactory.construct(acct, payload.input);

            const sameOl = await NoteFactory.fromId(notepad.note.id);
            sameOl.body.should.eql(notepad.note.body);
        });

        it('should throw an exception if the note does not exist', async () => {
            let wasThrown = false;
            try {
                await NoteFactory.fromId(0);
            } catch (err) {
                wasThrown = true;
            }
            wasThrown.should.eql(true);
        })
    });
});