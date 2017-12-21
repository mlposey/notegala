'use strict';
process.env.NODE_ENV = 'test';

const AuthMiddleware = require('../service/auth-middleware');
const chai = require('chai');
const should = chai.should();

describe('Auth', () => {
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
});