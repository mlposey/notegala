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

describe('Note', () => {
    describe('#addWatcher(userId, canEdit)', () => {
        beforeEach(async () => await clearDB());

        it('should add a user to the watchers list', async () => {
            const a1 = await Account.construct(payload.email, payload.name);
            const notepad = await NoteFactory.construct(a1, payload.input);
            notepad.note.isPublic = true;

            const newUserEmail = 'test' + payload.email;
            const acct = await Account.construct(newUserEmail, payload.name);
            await notepad.note.addWatcher(acct.id, false);

            const uids = await db('note_watchers')
                .select('user_id')
                .map(row => { return row.user_id; });
            uids.should.include.members([acct.id]);
        });
    });

    describe('#removeWatcher(userId)', () => {
        beforeEach(async () => await clearDB());

        it('should delete the note if their was only one watcher', async () => {
            const acct = await Account.construct(payload.email, payload.name);
            const notepad = await NoteFactory.construct(acct, payload.input);
            
            await notepad.note.removeWatcher(acct.id);

            const rows = await db('notes').select();
            rows.length.should.eql(0);
        });

        it('should give ownership to earliest watcher', async () => {
            const acct = await Account.construct(payload.email, payload.name);
            const notepad = await NoteFactory.construct(acct, payload.input);
            const note = notepad.note;
            note.isPublic = true;

            const b = await Account.construct('a' + payload.email, 'b' + payload.name);
            await note.addWatcher(b.id, false);

            await note.removeWatcher(acct.id);

            let rows = await db('notes').select();
            rows.length.should.eql(1);
            rows = await db('note_watchers').select();
            rows.length.should.eql(1);
        });
    });

    describe('#tags()', () => {
        beforeEach(async () => await clearDB());

        it('should return an empty array instead of null', async () => {
            const acct = await Account.construct(payload.email, payload.name);
            const notepad = await NoteFactory.construct(acct, {
                body: 'test'
            });
            const tags = await notepad.note.tags();
            console.log(typeof tags);
            tags.should.be.a('array').that.has.length(0);
        });

        it('should return all linked tags', async () => {
            const acct = await Account.construct(payload.email, payload.name);
            const notepad = await NoteFactory.construct(acct, payload.input);
            const tags = await notepad.note.tags();

            tags.should.be.a('array').that.has.length(payload.input.tags.length);
        });
    });

    describe('#watchers()', () => {
        beforeEach(async () => await clearDB());

        it('should return at least one watcher', async () => {
            const acct = await Account.construct(payload.email, payload.name);
            const notepad = await NoteFactory.construct(acct, payload.input);
            const watchers = await notepad.note.watchers();

            watchers.length.should.eql(1);
        });
    });
});