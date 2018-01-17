'use strict';
process.env.NODE_ENV = 'test';

const chai = require('chai');
const should = chai.should();

const { db } = require('../app/data/database');
const { clearDB } = require('./index');
const NoteBuilder = require('../app/note/note-builder');
const Account = require('../app/account/account');
const AccountRepository = require('../app/account/account-repository');
const { Notepad } = require('../app/note/notepad');

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

describe('Notepad', () => {
    let acct;
    let notepad;            
    describe('#addTag(tag)', () => {
        beforeEach(async () => {
            await clearDB();
            acct = new Account(payload.email, payload.name);
            await accountRepo.add(acct);

            let note = await new NoteBuilder(acct)
                .setTitle(payload.input.title)
                .setBody(payload.input.body)
                .addTags(payload.input.tags)
                .build();
            notepad = new Notepad(note, acct);
        });

        it('should ignore duplicate tags', async () => {
            await notepad.addTag(payload.input.tags[0]);
            const rows = await db.select().table('note_tags');
            rows.length.should.eql(payload.input.tags.length);
        });
    });

    describe('#replaceTags(newList)', () => {
        beforeEach(async () => {
            await clearDB();
            acct = new Account(payload.email, payload.name);
            await accountRepo.add(acct);

            let note = await new NoteBuilder(acct)
                .setTitle(payload.input.title)
                .build();
            notepad = new Notepad(note, acct);
        });

        it('should replace this old tag list with the new one', async () => {
            const newList = ['Brand New Tag'];
            await notepad.replaceTags(newList);

            const tags = await notepad.note.tags();
            tags.length.should.eql(1);
            tags[0].should.eql('Brand New Tag');
        });

        it('should clear the list if given an empty array', async () => {
            await notepad.replaceTags([]);
            const tags = await notepad.note.tags();
            tags.length.should.eql(0);
        });
    });
});