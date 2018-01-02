'use strict';
process.env.NODE_ENV = 'test';

const chai = require('chai');
const should = chai.should();

const { db } = require('../service/database');
const { clearDB } = require('./index');
const NoteFactory = require('../model/note/note-factory');
const Account = require('../model/account');
const { Notepad } = require('../model/note/notepad');

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

describe('Notepad', () => {
    describe('#addTag(tag)', () => {
        beforeEach(async () => await clearDB());

        it('should add link the tag and note in the database', async () => {
            const acct = await Account.construct(payload.email, payload.name);
            const notepad = await NoteFactory.construct(acct, {
                body: 'test'
            });

            await notepad.addTag('test');

            const rows = await db.select('id').table('note_tags');
            rows.length.should.eql(1);
        });

        it('should ignore duplicate tags', async () => {
            const acct = await Account.construct(payload.email, payload.name);
            const notepad = await NoteFactory.construct(acct, payload.input);

            await notepad.addTag(payload.input.tags[0]);

            const rows = await db.select().table('note_tags');
            rows.length.should.eql(payload.input.tags.length);
        });
    });

    describe('#replaceTags(newList)', () => {
        beforeEach(async () => await clearDB());

        it('should replace this old tag list with the new one', async () => {
            const acct = await Account.construct(payload.email, payload.name);
            const notepad = await NoteFactory.construct(acct, payload.input);

            const newList = ['Brand New Tag'];
            await notepad.replaceTags(newList);

            const tags = await notepad.note.tags();
            tags.length.should.eql(1);
            tags[0].should.eql('Brand New Tag');
        });

        it('should clear the list if given an empty array', async () => {
            const acct = await Account.construct(payload.email, payload.name);
            const notepad = await NoteFactory.construct(acct, payload.input);
            
            await notepad.replaceTags([]);
            const tags = await notepad.note.tags();
            tags.length.should.eql(0);
        });
    });
});