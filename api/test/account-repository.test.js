'use strict';
process.env.NODE_ENV = 'test';

const chai = require('chai');
const should = chai.should();

const { clearDB } = require('./index');
const { db } = require('../service/database');
const Account = require('../account/account');
const AccountRepository = require('../account/account-repository');
const EmailSpecification = require('../account/email-spec');

// Sample account data
const creds = Object.freeze({
    email: 'test@example.com',
    name: 'Trusty Tester'
});

describe('AccountRepository', () => {
    describe('#add(account)', () => {
        beforeEach(async () => await clearDB());

        it('should add accounts with unique emails', async () => {
            let rows = await db.select().table('users');
            rows.length.should.eql(0);

            const account = new Account(creds.email, creds.name);

            const repo = new AccountRepository();
            await repo.add(account);

            rows = await db.select().table('users');
            rows.length.should.eql(1);
        });

        it('should reject accounts with duplicate emails', async () => {
            const account = new Account(creds.email, creds.name);

            const repo = new AccountRepository();
            await repo.add(account);
            
            let wasThrown = false;
            try { await repo.add(account); }
            catch (err) { wasThrown = true; }
            wasThrown.should.eql(true);
        });
    });

    describe('#remove(account)', () => {
        beforeEach(async () => await clearDB());
        
        it('should remove account if exists', async () => {
            const account = new Account(creds.email, creds.name);
            const repo = new AccountRepository();
            await repo.add(account);
            
            let rows = await db.select().table('users');
            rows.length.should.eql(1);

            await repo.remove(account);
            rows = await db.select().table('users');
            rows.length.should.eql(0);
        });

        it('should throw NotFoundError if missing', async () => {
            const account = new Account(creds.email, creds.name);
            const repo = new AccountRepository();

            let wasThrown = false;
            try { await repo.remove(account); }
            catch (err) { wasThrown = true; }
            wasThrown.should.eql(true);
        });
    });

    describe('#replace(account)', () => {
        beforeEach(async () => await clearDB());
        
        it('should update the account if it exists', async () => {
            const account = new Account(creds.email, creds.name);
            const repo = new AccountRepository();
            await repo.add(account);

            const newName = 'a' + account.name;
            account.name = newName;
            await repo.replace(account);

            let matches = await repo.find(new EmailSpecification(account.email));
            matches.length.should.eql(1);
            matches[0].name.should.eql(newName);
        });

        it('should throw NotFoundError if missing', async () => {
            const account = new Account(creds.email, creds.name);
            const repo = new AccountRepository();

            let wasThrown = false;
            try { await repo.replace(account); }
            catch (err) { wasThrown = true; }
            wasThrown.should.eql(true);
        });
    });
});

describe('EmailSpecification', () => {
    it('should match accounts by email', async () => {
        const account = new Account(creds.email, creds.name);
        const repo = new AccountRepository();
        await repo.add(account);

        const matches = await repo.find(new EmailSpecification(creds.email));
        matches.length.should.eql(1);
        matches[0].id.should.eql(account.id);
    });
});
