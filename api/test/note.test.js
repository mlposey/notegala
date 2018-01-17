'use strict';
process.env.NODE_ENV = 'test';

const chai = require('chai');
const should = chai.should();

const { db } = require('../app/data/database');
const { clearDB } = require('./index');
const NoteBuilder = require('../app/note/note-builder');
const Account = require('../app/account/account');
const AccountRepository = require('../app/account/account-repository');

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

const accountRepo = new AccountRepository();

describe('Note', () => {
    describe('#addWatcher(userId, canEdit)', () => {
        beforeEach(async () => await clearDB());

        it('should add a user to the watchers list', async () => {
            const a1 = new Account(payload.email, payload.name);
            await accountRepo.add(a1);

            const note = await new NoteBuilder(a1)
                .setTitle(payload.input.title)
                .setBody(payload.input.body)
                .addTags(payload.input.tags)
                .build();
            note.isPublic = true;

            const newUserEmail = 'test' + payload.email;
            const acct = new Account(newUserEmail, payload.name);
            await accountRepo.add(acct);
            await note.addWatcher(acct.id, false);

            const uids = await db('note_watchers')
                .select('user_id')
                .map(row => { return row.user_id; });
            uids.should.include.members([acct.id]);
        });
    });

    describe('#removeWatcher(userId)', () => {
        let acct;
        let note;
        beforeEach(async () => {
            await clearDB();
            acct = new Account(payload.email, payload.name);
            await accountRepo.add(acct);
            note = await new NoteBuilder(acct)
                .setTitle(payload.input.title)
                .setBody(payload.input.body)
                .addTags(payload.input.tags)
                .build();
        });

        it('should delete the note if their was only one watcher', async () => {
            await note.removeWatcher(acct.id);
            const rows = await db('notes').select();
            rows.length.should.eql(0);
        });

        it('should give ownership to earliest watcher', async () => {
            note.isPublic = true;

            const b = new Account('a' + payload.email, 'b' + payload.name);
            await accountRepo.add(b);
            await note.addWatcher(b.id, false);

            await note.removeWatcher(acct.id);

            let rows = await db('notes').select();
            rows.length.should.eql(1);
            rows = await db('note_watchers').select();
            rows.length.should.eql(1);
        });
    });

    describe('#tags()', () => {
        let acct;
        beforeEach(async () => {
            await clearDB();
            acct = new Account(payload.email, payload.name);
            await accountRepo.add(acct);
        });

        it('should return an empty array instead of null', async () => {
            const note = await new NoteBuilder(acct).setBody('test').build();
            const tags = await note.tags();
            tags.should.be.a('array').that.has.length(0);
        });

        it('should return all linked tags', async () => {
            const note = await new NoteBuilder(acct)
                .setTitle(payload.input.title)
                .setBody(payload.input.body)
                .addTags(payload.input.tags)
                .build();
            const tags = await note.tags();

            tags.should.be.a('array').that.has.length(payload.input.tags.length);
        });
    });

    describe('#watchers()', () => {
        beforeEach(async () => await clearDB());

        it('should return at least one watcher', async () => {
            const acct = new Account(payload.email, payload.name);
            await accountRepo.add(acct);
            const note = await new NoteBuilder(acct)
                .setTitle(payload.input.title)
                .setBody(payload.input.body)
                .addTags(payload.input.tags)
                .build();

            const watchers = await note.watchers();
            watchers.length.should.eql(1);
        });
    });
});