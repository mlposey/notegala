'use strict';
process.env.NODE_ENV = 'test';

const chai = require('chai');
const should = chai.should();

const { db } = require('../service/database');
const { clearDB } = require('./index');
const NoteFactory = require('../model/note/note-factory');
const Account = require('../model/account');

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

describe('NoteFactory', () => {
    describe('#construct(email, input)', () => {
        beforeEach(async () => await clearDB());

        it('should throw an exception if the email is unrecognized', async () => {
            let wasThrown = false;
            try {
                await NoteFactory.construct(payload.email, payload.input);
            } catch (err) {
                wasThrown = true;
            }
            wasThrown.should.eql(true);
        });

        it('should throw an exception if input lacks a body value', async () => {
            await Account.construct(payload.email, payload.name);

            let wasThrown = false;
            try {
                await NoteFactory.construct(payload.email, {
                    title: 'Test',
                    tags: ['test']
                });
            } catch (err) {
                wasThrown = true;
            }
            wasThrown.should.eql(true);
        });

        it('should return a valid note if no exception is thrown', async () => {
            await Account.construct(payload.email, payload.name);
            let np = await NoteFactory.construct(payload.email, payload.input);

            np.note.body.should.eql(payload.input.body);
            np.note.title.should.eql(payload.input.title);
        });

        it('should put created notes in the persistence layer', async () => {
            await Account.construct(payload.email, payload.name);
            const notepad =
                await NoteFactory.construct(payload.email, payload.input);

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

    describe('#getOwned(email)', () => {
        beforeEach(async () => await clearDB());

        it('should return an empty array instead of null', async () => {
            await Account.construct(payload.email, payload.name);
            const notes = await NoteFactory.getOwned(payload.email, null);
            notes.length.should.eql(0);
        });

        it('should return only notes that the user owns', async () => {
            await Account.construct(payload.email, payload.name);
            await NoteFactory.construct(payload.email, payload.input);

            const user = await Account.construct('test' + payload.email,
                payload.name);
            const body = user.email + user.name;
            await NoteFactory.construct(user.email, {body: body});

            const notes = await NoteFactory.getOwned(user.email, null);
            notes.length.should.eql(1);
            notes[0].body.should.eql(body);
        });

        it('should respect the specified limit', async () => {
            await Account.construct(payload.email, payload.name);
            await NoteFactory.construct(payload.email, payload.input);
            await NoteFactory.construct(payload.email, payload.input);
            
            const max = 1;
            const notes = await NoteFactory.getOwned(payload.email, max);
            
            notes.length.should.eql(max);
        });
    });

    describe('#fromId(id)', () => {
        beforeEach(async () => await clearDB());

        it('should retrieve the note if it exists', async () => {
            await Account.construct(payload.email, payload.name);
            const notepad = 
                await NoteFactory.construct(payload.email, payload.input);

            const sameOl = await NoteFactory.fromId(notepad.note.id);
            sameOl.body.should.eql(notepad.note.body);
        });

        it('should throw an exception if the note does not exist', async () => {
            await Account.construct(payload.email, payload.name);            
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