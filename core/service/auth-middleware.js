'use strict';
const GoogleAuth = require('google-auth-library');
const { GraphQLError, formatError } = require('graphql');

module.exports = class AuthMiddleware {
    /**
     * Handles Google OAuth2 middleware required to grant API access
     * @param {string} clientId The client ID used to verify tokens
     */
    constructor(clientId) {
        this.clientId = clientId;
        console.log('client id: ' + clientId);

        let auth = new GoogleAuth();
        this.client = new auth.OAuth2(clientId, '', '');
    }

    /**
     * Writes an error message to res
     * @param {Object} res The HTTP response
     * @param {number} code The HTTP status code
     * @param {string} msg The error description
     */
    sendError(res, code, msg) {
        return res.status(code)
            .set('Content-Type', 'application/json')
            .send(formatError(new GraphQLError(msg)));
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
            return this.sendError(res, 401, 'missing Bearer token');
        }

        this.client.verifyIdToken(
            idToken,
            this.clientId,
            (e, login) => {
                if (e) {
                    return this.sendError(res, 401, 'invalid token');
                }

                let missingClaims = this.storeClaims(req, login.getPayload());
                if (missingClaims.length != 0) {
                    return this.sendError(res, 400,
                        'missing claims: ' + missingClaims.join(','));
                }
                next();
            }
        );     
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
};