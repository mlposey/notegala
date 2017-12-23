'use strict';
process.env.NODE_ENV = 'test';

const chai = require('chai');
const should = chai.should();

const { db } = require('../service/database');
const { clearDB } = require('./index');
const NoteFactory = require('../model/note/note-factory');
const Account = require('../model/account');

// Models potential data supplied by the request
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
            let note = await NoteFactory.construct(payload.email, payload.input);

            note.body.should.eql(payload.input.body);
            note.title.should.eql(payload.input.title);
        });

        it('should put created notes in the persistence layer', async () => {
            await Account.construct(payload.email, payload.name);
            const note = await NoteFactory.construct(payload.email, payload.input);

            let rows = await db.select().table('notes');
            rows.length.should.not.be.eql(0);

            rows = await db.select().table('note_tags');
            rows.length.should.eql(payload.input.tags.length);
            const extras = rows
                .map(row => row.note_id)
                .filter(noteId => {return noteId != note.id;});
            extras.length.should.be.eql(0);
        });
    });
});