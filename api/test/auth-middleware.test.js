'use strict';
process.env.NODE_ENV = 'test';

const AuthMiddleware = require('../service/auth-middleware');
const Account = require('../account/account');
const AccountRepository = require('../account/account-repository');
const { db } = require('../service/database');
const { clearDB } = require('./index');

const chai = require('chai');
const should = chai.should();

describe('AuthMiddleware', () => {
    describe('#storeClaims()', () => {
        it('should require the email claim', () => {
            let auth = new AuthMiddleware('test');
            let missingClaims = auth.storeClaims({}, {});
            missingClaims.should.contain('email');
        });

        it('should store the email claim in req', () => {
            let auth = new AuthMiddleware('test');
            let req = {};
            const JWT = { email: 'test@example.com' };

            let missingClaims = auth.storeClaims(req, JWT);

            missingClaims.should.not.contain('email');
            req.should.have.property('email').eql(JWT.email);
        });

        it('should require the name claim', () => {
            let auth = new AuthMiddleware('test');
            let missingClaims = auth.storeClaims({}, {});
            missingClaims.should.contain('name');
        });

        it('should store the name claim in req', () => {
            let auth = new AuthMiddleware('test');
            let req = {};
            const JWT = { name: 'Jane Doe' };

            let missingClaims = auth.storeClaims(req, JWT);

            missingClaims.should.not.contain('name');
            req.should.have.property('name').eql(JWT.name);
        });
    });

    describe('#logSignIn(acct)', () => {
        beforeEach(async () => clearDB());

        it('should log the current time under the account', async () => {
            const acct = new Account('test@t.com', 'test');
            await new AccountRepository().add(acct);
            const lastSeen = acct.lastSeen;

            const auth = new AuthMiddleware('test');
            await auth.logSignIn(acct);

            const rows = await db('users')
                .select(['last_seen'])
                .where({id: acct.id});
            
            lastSeen.should.not.eql(rows[0].last_seen);
        });
    });
});