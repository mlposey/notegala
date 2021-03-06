'use strict';
const GoogleAuth = require('google-auth-library');
const { GraphQLError, formatError } = require('graphql');
const Account = require('./account/account');
const AccountRepository = require('./account/account-repository');
const EmailSpecification = require('./account/email-spec');
const { db } = require('./data/database');
const logger = require('./logging/logger');

module.exports = class AuthMiddleware {
    /**
     * Handles Google OAuth2 middleware required to grant API access
     * @param {string} clientId The client ID used to verify tokens
     */
    constructor(clientId) {
        this.clientId = clientId;
        logger.info('client id: ' + clientId);

        let auth = new GoogleAuth();
        this.client = new auth.OAuth2(clientId, '', '');
        this.accountRepo = new AccountRepository();
    }

    /**
     * Writes an error message to res
     * @param {Object} res The HTTP response
     * @param {Object} req The HTTP request that triggered he error
     * @param {number} code The HTTP status code to respond with
     * @param {string} msg The error description
     */
    sendError(res, req, code, msg) {
        logger.warn('failed authentication attempt', {
            cause: msg,
            request: JSON.stringify({
                ip: req.headers['x-forwarded-for'] || req.connection.remoteAddress,
                path: req.path,
                method: req.method,               
                headers: req.headers
            })
        });

        return res.status(code)
            .set('Content-Type', 'application/json')
            .send(formatError(new GraphQLError(msg)));
    }

    /**
     * Verifies a Google ID token, calling next if successful
     * @param {Object} req The HTTP request
     * @param {Object} res The HTTP response. This is sent with an appropriate
     *                     error if verification fails.
     * @param {Function} next A function that calls the next middleware handler
     *                        or the GraphQL resolver.
     */
    verifyGId(req, res, next) {
        let idToken;
        try {
            idToken = this.extractToken(req);
        } catch (err) {
            return this.sendError(res, req, 401, 'missing Bearer token');
        }

        this.client.verifyIdToken(
            idToken,
            this.clientId,
            async (e, login) => {
                if (e) {
                    return this.sendError(res, req, 401, 'invalid token');
                }

                let missingClaims = this.storeClaims(req, login.getPayload());
                if (missingClaims.length != 0) {
                    return this.sendError(res, req, 400,
                        'missing claims: ' + missingClaims.join(','));
                }

                try { await this.storeAccount(req, req.email, req.name); }
                catch (err) { return this.sendError(res, req, 400, err.message); }
                
                next();
            }
        );     
    }

    /**
     * Extracts a bearer token from the Authorization header of req
     * @param {Object} req The HTTP request
     * @throws {Error} If the token is missing or not of Bearer type
     * @return {string} The token
     */
    extractToken(req) {
        let auth = req.get('Authorization');
        if (auth === undefined) {
            throw new Error("missing Authorization header");
        }

        let parts = auth.split(' ');
        if (parts.length != 2 || parts[0] != 'Bearer') {
            throw new Error("wrong Authorization value type");
        }

        return parts[1];
    }

    /**
     * Stores claims from a JWT into req
     * @param {Object} req 
     * @param {Object} payload The JWT
     * @return {Array.<string>} Any claims that were expected but missing
     */
    storeClaims(req, payload) {
        return ['email', 'name'] // Store other claims here.
            .filter(claim => {
                if (!payload[claim]) return true;
                req[claim] = payload[claim];
            })
    }

    /**
     * Stores the account associated with the email into the req object
     * 
     * If the account does not exist, it is created.
     * @param {Object} req
     * @param {string} email The email of the account
     * @param {string} name The display name of the account
     * @throws {Error} If a new account cannot be constructed
     */
    async storeAccount(req, email, name) {
        try {
            const matches = await this.accountRepo
                .find(new EmailSpecification(email));
            
            let acct = matches.length === 0 ? null : matches[0];
            if (!acct) {
                acct = new Account(email, name);
                this.accountRepo.add(acct);
            } else {
                this.logSignIn(acct);
            }
            req.acct = acct;
        } catch (err) {
            throw new Error('could not create account');
        }
    }

    /**
     * Logs the time when the account performed the API request
     * @param {Account} acct 
     */
    async logSignIn(acct) {
        // TODO: Use AccountRepository#replace(account)
        await db('users')
            .update({last_seen: db.raw('NOW()')})
            .where({id: acct.id});
    }
};