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
                let acct = await Account.fromEmail(userToken.email);
                console.log(acct);
            } catch (err) {
                wasThrown = true;
            }
            wasThrown.should.eql(true);
        });
    });
});