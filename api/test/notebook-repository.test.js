'use strict';
process.env.NODE_ENV = 'test';

const chai = require('chai');
const should = chai.should();

const { clearDB } = require('./index');
const { db } = require('../app/data/database');
const Account = require('../app/account/account');
const AccountRepository = require('../app/account/account-repository');
const Notebook = require('../app/notebook/notebook');
const NotebookRepository = require('../app/notebook/notebook-repository');
const IdSpecification = require('../app/notebook/id-spec');

const accountRepo = new AccountRepository();
const notebookRepo = new NotebookRepository();

// Test data for notebook creation
const payload = Object.freeze({
    email: 'test@example.com',
    name: 'Tester',
    title: 'Test Notebook'
});

describe('NotebookRepository', () => {
    let acct;
    let notebook;
    describe('#add(notebook)', () => {
        beforeEach(async () => {
            await clearDB();
            acct = new Account(payload.email, payload.name);
            await accountRepo.add(acct);
            notebook = new Notebook(acct.id, payload.title);
        });

        it('should create the notebook if (name, author) is unique', async () => {
            let rows = await db('notebooks').select();
            rows.length.should.eql(0);

            await notebookRepo.add(notebook);

            rows = await db('notebooks').select();
            rows.length.should.eql(1);
        });

        it('should throw an exception if (name, author) is duplicate', async () => {
            await notebookRepo.add(notebook);
            
            let wasThrown = false;
            try { await notebookRepo.add(notebook); }
            catch (e) { wasThrown = true; }
            wasThrown.should.eql(true);
        });
    });

    describe('#remove(notebook)', () => {
        beforeEach(async () => {
            await clearDB();
            acct = new Account(payload.email, payload.name);
            await accountRepo.add(acct);
            notebook = new Notebook(acct.id, payload.title);
        });

        it('should remove the note if it is recognized', async () => {
            await notebookRepo.add(notebook);

            let notebooks = await acct.notebooks();
            notebooks.length.should.eql(1);

            await notebookRepo.remove(notebook);
            notebooks = await acct.notebooks();
            
            notebooks.length.should.eql(0);
        });

        it('should throw NotFoundError if unrecognized', async () => {
            let wasThrown = false;
            try { await notebookRepo.remove(notebook); }
            catch (err) { wasThrown = true; }

            wasThrown.should.eql(true);
        });
    });

    describe('#replace(notebook)', () => {
        beforeEach(async () => {
            await clearDB();
            acct = new Account(payload.email, payload.name);
            await accountRepo.add(acct);
            notebook = new Notebook(acct.id, payload.title);
        });

        it('should update the notebook if it exists', async () => {
            await notebookRepo.add(notebook);

            const newTitle = 'a' + notebook.title;
            notebook.title = newTitle;
            await notebookRepo.replace(notebook);

            const matches = await notebookRepo
                .find(new IdSpecification(notebook.id));
            matches.length.should.eql(1);
            matches[0].title.should.eql(newTitle);
        });

        it('should throw NotFoundError if missing', async () => {
            let wasThrown = false;
            try { await notebookRepo.replace(notebook) }
            catch (err) { wasThrown = true; }
            wasThrown.should.eql(true);
        });
    });
});