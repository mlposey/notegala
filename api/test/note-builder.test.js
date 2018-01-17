'use strict';
process.env.NODE_ENV = 'test';

const chai = require('chai');
const should = chai.should();

const { db } = require('../app/data/database');
const { clearDB } = require('./index');
const Account = require('../app/account/account');
const AccountRepository = require('../app/account/account-repository');
const Note = require('../app/note/note');
const NoteBuilder = require('../app/note/note-builder');
const Notebook = require('../app/notebook/notebook');
const NotebookRepository = require('../app/notebook/notebook-repository');

const payload = Object.freeze({
    title: 'Test Title',
    body: 'This is a test.',
    tags: ['test', 'example', 'mock']
});

describe('NoteBuilder', () => {
    describe('#build()', () => {
        let acct;
        let builder;
        beforeEach(async () => {
            await clearDB();
            acct = new Account('test@example.com', 'Tester');
            const accountRepo = new AccountRepository();            
            await accountRepo.add(acct);
            builder = new NoteBuilder(acct);
        });

        it('should throw exception if missing title and body', async () => {
            let wasThrown = false;
            try { await builder.build(); }
            catch (err) { wasThrown = true; }
            wasThrown.should.eql(true);
        });

        it('should build with only title', async () => {
            const note = await builder.setTitle(payload.title).build();
            note.title.should.eql(payload.title);
        });

        it('should throw exception if invalid account', async () => {
            builder = new NoteBuilder(++acct.id);
            
            let wasThrown = false;
            try { await builder.setTitle(payload.title).build(); }
            catch (err) { wasThrown = true; }
            wasThrown.should.eql(true);
        });

        it('should build with only body', async () => {
            const note = await builder.setBody(payload.body).build();
            note.body.should.eql(payload.body);
        });

        it('should build with title and body', async () => {
            const note = await builder
                .setTitle(payload.title)
                .setBody(payload.body)
                .build();
            note.title.should.eql(payload.title);
            note.body.should.eql(payload.body);            
        });

        it('should build with tags', async () => {
            const note = await builder
                .setBody(payload.body)
                .addTags(payload.tags)
                .build();

            const tags = await note.tags();
            tags.should.have.members(payload.tags);
        });

        it('should build with notebook', async () => {
            const notebook = new Notebook(acct.id, 'test');
            const notebookRepo = new NotebookRepository();
            await notebookRepo.add(notebook);

            const note = await builder
                .setBody(payload.body)
                .setNotebook(notebook.id)
                .build();

            const notebooks = await acct.notebooks();
            const notes = await notebooks[0].notes();
            notes.length.should.eql(1);
            notes[0].id.should.eql(note.id);
        });
    });
});