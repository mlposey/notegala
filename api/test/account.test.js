'use strict';
process.env.NODE_ENV = 'test';

const chai = require('chai');
const should = chai.should();

const { db } = require('../service/database');
const { clearDB } = require('./index');
const Account = require('../model/account');

// Models a bearer token that would be supplied to the auth layer
const userToken = Object.freeze({
    email: 'test@example.com',
    name: 'Trusty Tester'
});

describe('Account', () => {
    describe('#fromEmail(email)', () => {
        beforeEach(async () => await clearDB());
    
        it('should get the account if it exists', async () => {
            await db('users').insert({
                email: userToken.email,
                name: userToken.name
            });

            let account = await Account.fromEmail(userToken.email);
            account.name.should.eql(userToken.name);
        });

        it('should throw an Error if the account is missing', async () => {
            let wasThrown = false;
            try {
                await Account.fromEmail(userToken.email);
            } catch (err) {
                wasThrown = true;
            }
            wasThrown.should.eql(true);
        });
    });

    describe('#construct(email, name)', () => {
        beforeEach(async () => await clearDB());

        it('should create the account if the email is unique', async () => {
            let account =
                await Account.construct(userToken.email, userToken.name);

            account.email.should.eql(userToken.email);
            account.name.should.eql(userToken.name);

            let rows = await db.select().table('users');
            rows.length.should.not.eql(0);
        });

        it('should throw an Error if the email is taken', async () => {
            await Account.construct(userToken.email, userToken.name);

            let wasThrown = false;
            try {
                await Account.construct(userToken.email, userToken.name);
            } catch (err) {
                wasThrown = true;
            }
            wasThrown.should.eql(true);
        });
        
    });
});