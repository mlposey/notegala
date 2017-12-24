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
    describe('#addTag(tag)', () => {
        beforeEach(async () => await clearDB());

        it('should add link the tag and note in the database', async () => {
            await Account.construct(payload.email, payload.name);
            const note = await NoteFactory.construct(payload.email, {
                body: 'test'
            });

            await note.addTag('test');

            const rows = await db.select('id').table('note_tags');
            rows.length.should.eql(1);
        });

        it('should ignore duplicate tags', async () => {
            await Account.construct(payload.email, payload.name);
            const note =
                await NoteFactory.construct(payload.email, payload.input);
            await note.addTag(payload.input.tags[0]);

            const rows = await db.select().table('note_tags');
            rows.length.should.eql(payload.input.tags.length);
        });
    });

    describe('#replaceTags(newList)', () => {
        beforeEach(async () => await clearDB());

        it('should replace this old tag list with the new one', async () => {
            await Account.construct(payload.email, payload.name);
            const note =
                await NoteFactory.construct(payload.email, payload.input);

            const newList = ['Brand New Tag'];
            await note.replaceTags(newList);

            const tags = await note.tags();
            tags.length.should.eql(1);
            tags[0].should.eql('Brand New Tag');
        });

        it('should clear the list if given an empty array', async () => {
            await Account.construct(payload.email, payload.name);
            const note =
                await NoteFactory.construct(payload.email, payload.input);
            
            await note.replaceTags([]);
            const tags = await note.tags();
            tags.length.should.eql(0);
        });
    });

    describe('#addWatcher(email, canEdit)', () => {
        beforeEach(async () => await clearDB());

        it('should add a user to the watchers list', async () => {
            await Account.construct(payload.email, payload.name);
            const note =
                await NoteFactory.construct(payload.email, payload.input);

            const newUserEmail = 'test' + payload.email;
            const acct = await Account.construct(newUserEmail, payload.name);
            await note.addWatcher(newUserEmail, false);

            const uids = await db('note_watchers')
                .select('user_id')
                .map(row => { return row.user_id; });
            uids.should.include.members([acct.id]);
        });
    });

    describe('#tags()', () => {
        beforeEach(async () => await clearDB());

        it('should return an empty array instead of null', async () => {
            await Account.construct(payload.email, payload.name);
            const note = await NoteFactory.construct(payload.email, {
                body: 'test'
            });
            const tags = await note.tags();
            console.log(typeof tags);
            tags.should.be.a('array').that.has.length(0);
        });

        it('should return all linked tags', async () => {
            await Account.construct(payload.email, payload.name);
            const note =
                await NoteFactory.construct(payload.email, payload.input);
            const tags = await note.tags();

            tags.should.be.a('array').that.has.length(payload.input.tags.length);
        });
    });

    describe('#watchers()', () => {
        beforeEach(async () => await clearDB());

        it('should return at least one watcher', async () => {
            await Account.construct(payload.email, payload.name);
            const note =
                await NoteFactory.construct(payload.email, payload.input);
            const watchers = await note.watchers();

            watchers.length.should.eql(1);
        });
    });
});